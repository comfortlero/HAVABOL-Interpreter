package havabol.lexer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import havabol.error.SyntaxError;
import havabol.parser.Parser;
import havabol.storage.SymbolTable;

public class Scanner {
	
	// Source file information
	public String sourceFileNm;
	public ArrayList<String> sourceLineM = new ArrayList<>();
	public SymbolTable symbolTable;
	
	// Holds source lines buffered for printing
	public ArrayList<String> lineBuffer = new ArrayList<>();
	
	// Scanner current line
	public char[] textCharM;
	// Source line number of our current cursor position, zero-indexed
	public int iSourceLineNr = 0;
	// Source character index of the current scanner line, zero-indexed
	public int iColPos = 0;
	public int debugColPos = 0;
	// Current and lookahead tokens
	public Token currentToken;
	public Token nextToken;
	public Token previous;
	// Comment found
	public boolean commentFound = false;
	public int commentFoundOn = 0;
	// Done scanning this file
	public boolean done = false;
	private boolean stop = false;
	public boolean unary = false;
	public boolean printBuffer = false;
	
	public boolean debugToken = false;
	
	
	private final static String DELIMITERS = " {}\t;:()\'\"~=!<>+-*/[]#^,\n"; // terminate a token
	private final static String WHITESPACE = " \t\n";
	private final static String QUOTES = "\"'";
	private final static String OPERATORS = "!=<>+-*/#^\'IN\'\'NOTIN\'";
	private final static String SEPARATORS = ",;:[](){}";
	private final static String ESCAPEPRINT ="\\'\"";
	
	private final static HashMap<Character, Character> escapeMap = new HashMap<Character, Character>(){
		private static final long serialVersionUID = 1L;

	{
		put('t', '\t');
		put('n', '\n');
		put('a', (char) 0x07);    
		}};
		
	private final static String[] WORD_OPERATORS = {"and", "or", "not", "in", "notin"};
	private final static String[] FLOW_OPERATORS = {"if", "endif", "else", "while", "endwhile", "for", "endfor"};
	private final static String[] DATA_TYPES = {"Int", "Float", "String", "Bool"};
	private final static List<String> UNARY = Arrays.asList("=", "+=", "-=", "+", "-", "*", "/", "^", "<", ">",
															"<=", ">=", "!=", "#", "and", "or", "u-", "(", ",",
															"!", "if", "select", "while", "when");
	/**
	 * Reads a Havabol source file and initializes environment for scanning.
	 * @param sourceFileNm Havabol source file path
	 * @param symbolTable TODO: not yet implemented
	 * @throws IOException Exception encountered while reading file
	 * @throws FileNotFoundException Source file not found or inaccessible
	 */
	public Scanner(String sourceFileNm, SymbolTable symbolTable) throws IOException, FileNotFoundException {
		
		// Read all source lines into an ArrayList
		try (BufferedReader sourceIn = new BufferedReader(new FileReader(sourceFileNm))) {
			String line;
			while ((line = sourceIn.readLine()) != null) {
				sourceLineM.add(line);
			}
		}
		
		// Initialize scanning environment
		currentToken = new Token();
		nextToken = new Token();

		if (sourceLineM.size() > 0) {
			textCharM = sourceLineM.get(0).toCharArray();
			bufferLine(0);
		} else {
			done = true;
		}

	}
	
	/**
	 * Reads and classifies the next token in the source file.
	 * Returns the next token as a string, functionally.
	 * @return String representation of the next token
	 */
	public String getNext() throws SyntaxError {
		previous = currentToken;	
		
		currentToken = getNextToken(false);
		//debugger for token
		if (debugToken) {
			System.out.println("\t\t... Current Token = " + currentToken.tokenStr);
		}
		
		if (stop) {
			currentToken.primClassif = Token.EOF;
			return "";
		}
		
		if (currentToken.primClassif == Token.EOF) {
			if (!nextToken.tokenStr.equals("")) {
				currentToken = nextToken;
				stop = true;
				return currentToken.tokenStr;
			}
			return "";
		}
		
		nextToken = getNextToken(true);
		//Take care of unary minus
		if(currentToken.subClassif != Token.STRING && nextToken.subClassif != Token.STRING)
			if(UNARY.contains(previous.tokenStr) && currentToken.tokenStr.equals("-")){
				currentToken.tokenStr = "u-";
			}else
				unary = false;
		//System.out.println("----> cur token " + currentToken.tokenStr);
		return currentToken.tokenStr;
	}
	
	/**
	 * Returns the next available token from the scanner's current internal
	 * cursor position.
	 * Pseudo:
	 	 * 	
		 * token refers to the next available token we will read
		 * delimiter refers to any character in above DELIMITERS list.
		 * 
		 * Preconditions:
		 * 		- Internal cursor is ON or BEFORE the beginning of our token.
		 * 
		 * Algorithm:
		 * 		If backtrack is true:
		 * 			Save internal cursor state
		 * 		Skip any and all whitespace and comments
		 * 		If our cursor is ON a delimiter:
		 * 			If delimiter is a quote:
		 * 				Read a string literal as our token
		 * 			Otherwise:
		 * 				Read one character into tokenStr
		 * 		Otherwise:
		 * 			Read characters into tokenStr until cursor is ON a delimiter.
		 * 		If backtrack is true:
		 * 			Restore internal cursor state
		 * 
		 * Postconditions:
		 * 		- If backtrack is false: internal cursor is STRICTLY AFTER the end of our token.
		 * 		- If backtrack is true: internal cursor is unchanged from its state before the call to this function.
		 *
	 * @param lookahead If true, save and restore scanner's internal cursor position
	 * @return The next available token
	 */
	private Token getNextToken(boolean lookahead) {
		
		int beforeSourceLineNr = -1;
		int beforeColPos = -1;
		
		if (lookahead) {
			beforeSourceLineNr = iSourceLineNr;
			beforeColPos = iColPos;
		}

		Token token = new Token();
		StringBuilder tokenStr = new StringBuilder();
		boolean isStringLiteral = false;
		commentFound = false;
		
		// Skip until we find something other than whitespace, comments, or we finish
		while ((iColPos >= textCharM.length || textCharM[iColPos] == '/'
				|| WHITESPACE.contains(Character.toString(textCharM[iColPos]))) && !done)
		{
			if ((iColPos >= textCharM.length || WHITESPACE.contains(Character.toString(textCharM[iColPos]))) && !done)
				advanceCursor(!lookahead);
			else if (textCharM[iColPos] == '/')
			{
				if(iColPos < (textCharM.length - 1) && textCharM[iColPos + 1] == '/'){
					commentFound = true; 
					commentFoundOn = iSourceLineNr;
					while (iSourceLineNr == commentFoundOn)
						advanceCursor(!lookahead);
				} else {
					break;
				}
			}
		}
		
		// If the done flag was set, there are no more tokens
		if (done) {
			token.primClassif = Token.EOF;
			return token;
		}
		
		// Save the start position of this token in case of error
		token.iColPos = iColPos;
		token.iSourceLineNr = iSourceLineNr;
		debugColPos = iColPos;
		char currentChar = textCharM[iColPos];
		char [] retCharM = new char[textCharM.length];
		int iRet = 0;
		
	
		if (DELIMITERS.contains(Character.toString(currentChar))) {
			if (QUOTES.contains(Character.toString(currentChar))) {
				char openStringChar = currentChar;
				boolean escapeNext = false;
				int openQuoteLineNr = iSourceLineNr;
				for (;;) {
					
					advanceCursor(!lookahead);	
					if (iSourceLineNr != openQuoteLineNr) {
						// Quote literal must end on opening line
						throw new SyntaxError("String literal must begin and end on same line", openQuoteLineNr + 1);
					}
					
					currentChar = textCharM[iColPos];
					if (done || (currentChar == openStringChar && !escapeNext)) {
						isStringLiteral = true;
						break;
					}

					if (currentChar == '\\' && !escapeNext) {
						escapeNext = true;
						if(ESCAPEPRINT.contains(Character.toString(textCharM[iColPos + 1])))
							continue;
						else{
							token.nonPrintable = true;
							if (escapeMap.containsKey(textCharM[iColPos + 1])){
								retCharM[iRet++] = escapeMap.get(textCharM[iColPos + 1]);
								continue;
							}
						}
					} else {
						escapeNext = false;
					}
					if(textCharM[iColPos - 1] != '\\') {
						retCharM[iRet++] = textCharM[iColPos];
					}
				}
			
				tokenStr =  tokenStr.insert(0, retCharM, 0, iRet);
				tokenStr.delete(iRet, tokenStr.length() + 1);
				advanceCursor(!lookahead);
			} else {
				if(OPERATORS.contains(Character.toString(textCharM[iColPos])) && 
						textCharM[iColPos + 1] == '=')
				{
					tokenStr.append(textCharM[iColPos]);
					tokenStr.append(textCharM[iColPos + 1]);
					advanceCursor(!lookahead);
				}else
					tokenStr.append(currentChar);
				advanceCursor(!lookahead);
			}
		} else {
			while (!DELIMITERS.contains(Character.toString(currentChar))) {
				tokenStr.append(currentChar);
				advanceCursor(!lookahead);
				currentChar = textCharM[iColPos];
			}
		}

		token.tokenStr = tokenStr.toString();
		classifyToken(token, isStringLiteral);
		//System.out.println("token is = " + token.tokenStr);
		//System.out.println("token subclass is = " + token.subClassif);
		
		if (lookahead) {
			iSourceLineNr = beforeSourceLineNr;
			iColPos = beforeColPos;
			textCharM = sourceLineM.get(beforeSourceLineNr).toCharArray();
		}
		
		return token;
	}

	/**
	 * Sets the cursor position of this scanner
	 * @param iSourceLineNr Zero-based line number to set cursor at
	 * @param iColPos Zero-based column to set cursor at
	 */
	public void setPosition(int iSourceLineNr, int iColPos) 
	{
		this.textCharM = sourceLineM.get(iSourceLineNr).toCharArray();
		this.iSourceLineNr = iSourceLineNr;
		this.iColPos = iColPos;
		this.done = false;
		
		//System.out.println("Set position to: " + (iSourceLineNr + 1) + " " + (iColPos + 1));
		//System.out.println("On line: " + String.valueOf(textCharM));
		this.getNext();
	}
	
	/**
	 * Classifies a token and sets necessary token fields.
	 * @param token Token to populate
	 * @param isStringLiteral Identifies this token as a string literal
	 */
	public void classifyToken(Token token, boolean isStringLiteral) throws SyntaxError {
		
		if (isStringLiteral) {
			token.primClassif = Token.OPERAND;
			token.subClassif = Token.STRING;
			return;
		}
		
		// Check if tokenStr is a data type
		switch (token.tokenStr) {
			case "Int":
			case "Float":
			case "String":
			case "Bool":
			case "Date":
			//case "Void":
				token.primClassif = Token.CONTROL;
				token.subClassif = Token.DECLARE;
				return;
			case "if":
			case "while":
			case "for":
			case "select":
			case "when":
			case "by":
			case "to":
			//case "def":
				token.primClassif = Token.CONTROL;
				token.subClassif = Token.FLOW;
				return;
			case "else":
			case "endif":
			case "endwhile":
			case "endfor":
			case "enddef":
				token.primClassif = Token.CONTROL;
				token.subClassif = Token.END;
				return;
			case "and":
			case "or":
			case "not":
				token.primClassif = Token.OPERATOR;
				return;
			case "IN":
			case "NOTIN":
				token.primClassif = Token.OPERATOR;
				return;
			case "T":
			case "F":
				token.primClassif = Token.OPERAND;
				token.subClassif = Token.BOOLEAN;
				return;
			// Built-in functions
			case "print":
			case "LENGTH":
			case "SPACES":
			case "MAXLENGTH":
			case "ELEM":
			case "MAXELEM":
			case "dateDiff":
			case "dateAdj":
			case "dateAge":
				token.primClassif = Token.FUNCTION;
				token.subClassif = Token.BUILTIN;
				return;
			case ">=":
			case "<=":
			case "==":
			case "!=":
			case "u-":
				token.primClassif = Token.OPERATOR;
				return;
			case "~":
				token.primClassif = Token.SEPARATOR;
				token.subClassif = Token.SEPARATOR;
				return;
		}
		
		if (OPERATORS.contains(token.tokenStr)) {
			token.primClassif = Token.OPERATOR;
		} else if (SEPARATORS.contains(token.tokenStr)) {
			token.primClassif = Token.SEPARATOR;
		} else {
			token.primClassif = Token.OPERAND;
			if (Character.isDigit(token.tokenStr.charAt(0))) {
				// Numeric literal
				if (token.tokenStr.contains(".")) {
					if (!token.tokenStr.matches("(\\d+\\.\\d*|\\d*\\.\\d+)")) {
						throw new SyntaxError("Invalid floating point literal", iSourceLineNr + 1, debugColPos + 1);
					}
					token.subClassif = Token.FLOAT;
				} else {
					if (!token.tokenStr.matches("\\d+")) {
						throw new SyntaxError("Invalid integer literal", iSourceLineNr + 1, debugColPos + 1);
					}
					token.subClassif = Token.INTEGER;
				}
			} else {
				if (isStringLiteral) {
					token.subClassif = Token.STRING;
				} else {
					token.subClassif = Token.IDENTIFIER;
				}
			}
		}
		
	}
	
	/**
	 * Adds the given source code line to the line print buffer
	 * @param lineNumber The 0-based line number of the line to add to the buffer
	 */
	public void bufferLine(int lineNumber) {
		lineBuffer.add("  " + (lineNumber + 1) + " " + sourceLineM.get(lineNumber));
	}
	
	/**
	 * Advances the scanner's cursor location by one, reading in new lines
	 * as necessary.
	 */
	public void advanceCursor(boolean print) {
		iColPos += 1;
		//if(iColPos < textCharM.length)
		//System.out.println("Line: " + (iSourceLineNr) + " Col: " + (iColPos - 1) + " Char: " + textCharM[iColPos - 1]);
		if (iColPos >= textCharM.length) {
			iColPos = 0;
			iSourceLineNr++;
			if (iSourceLineNr < sourceLineM.size()) {
				textCharM = sourceLineM.get(iSourceLineNr).toCharArray();
			} else {
				done = true;
			}
		}
	}

}

