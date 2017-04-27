package havabol.lexer;

import java.util.*;

import havabol.parser.Structure;
import havabol.parser.Value;
import havabol.storage.DataType;
import havabol.storage.StorageStructure;

public class Token
{
	public String tokenStr = "";
	public int primClassif = 0;
	public int subClassif = 0;
	public int iSourceLineNr = 0;
	public int iColPos = 0;
	public boolean nonPrintable = false;
	public boolean whileStmt = false;
	public boolean forStmt = false;

	// Constants for primClassif
	public static final int OPERAND = 1; // constants, identifier
	public static final int OPERATOR = 2; // + - * / < > = !
	public static final int SEPARATOR = 3; // ( ) , : ; [ ]
	public static final int FUNCTION = 4; // TBD
	public static final int CONTROL = 5; // TBD
	public static final int EOF = 6; // EOF encountered
	public static final int RT_PAREN = 7; // TBD
	// Constants for OPERAND's subClassif
	public static final int IDENTIFIER = 1;
	public static final int INTEGER = 2; // integer constant
	public static final int FLOAT = 3; // float constant
	public static final int BOOLEAN = 4; // boolean constant
	public static final int STRING = 5; // string constant
	public static final int DATE = 6; // date constant
	public static final int VOID = 7; // void
	// Constants for CONTROL's subClassif (after Pgm 1)
	public static final int FLOW = 10;// flow statement (e.g., if)
	public static final int END = 11;// end statement (e.g., endif)
	public static final int DECLARE = 12;// declare statement (e.g., Int)
	// Constants for FUNCTION's subClassif (definedby)
	public static final int BUILTIN = 13;// builtin function (e.g., print)
	public static final int USER = 14;// user defined

	// array of primClassif string values for the constants
	public static final String[] strPrimClassifM =
	{ "Undefined", "OPERAND" // 1
			, "OPERATOR" // 2
			, "SEPARATOR" // 3
			, "FUNCTION" // 4
			, "CONTROL" // 5
			, "EOF" // 6
	};
	public static final int PRIM_CLASS_MAX = 6;
	// array of subClassif string values for the constants
	public static final String[] strSubClassifM =
	{ "Undefined", "IDENTFIER" // 1
			, "INTEGER" // 2
			, "FLOAT" // 3
			, "BOOLEAN" // 4
			, "STRING" // 5
			, "DATE" // 6
			, "Void" // 7
			, "**not used**"// 8
			, "**not used**"// 9
			, "FLOW" // 10
			, "END" // 11
			, "DECLARE" // 12
	};
	public static final int OPERAND_SUB_CLASS_MIN = 1;
	public static final int OPERAND_SUB_CLASS_MAX = 7;
	public static final int CONTROL_SUB_CLASS_MIN = 10;
	public static final int CONTROL_SUB_CLASS_MAX = 12;
	
	public Value tempValue;
	public boolean isValueContainer = false;

	public Token(String value)
	{
		this.tokenStr = value;
	}

	public Token()
	{
		this(""); // invoke the other constructor
	}

	/**
	 * Function: printToken
	 * Preconditions: 
	 * 		- tokens have been correctly classified by the Scanner
	 * Purpose: 
	 * 		- prints a tokenStr based on whether or not it 
	 * 		  contains hexChars
	 */
	public void printToken()
	{
		String primClassifStr;
		String subClassifStr;
		// convert the primClassif to a string
		if (primClassif >= 0 && primClassif <= PRIM_CLASS_MAX)
			primClassifStr = strPrimClassifM[primClassif];
		else
			primClassifStr = "**garbage**";

		// convert the subClassif to a string
		switch (primClassif)
		{
		case Token.OPERAND:
			if (subClassif >= OPERAND_SUB_CLASS_MIN && subClassif <= OPERAND_SUB_CLASS_MAX)
				subClassifStr = strSubClassifM[subClassif];
			else
				subClassifStr = "**garbage**";
			break;
		case Token.CONTROL:
			if (subClassif >= CONTROL_SUB_CLASS_MIN && subClassif <= CONTROL_SUB_CLASS_MAX)
				subClassifStr = strSubClassifM[subClassif];
			else
				subClassifStr = "**garbage**";
			break;
		case Token.FUNCTION:
			if (subClassif == BUILTIN)
				subClassifStr = "BUILTIN";
			else if (subClassif == USER)
				subClassifStr = "USER";
			else
				subClassifStr = "**garbage**";
			break;
		default:
			subClassifStr = "-";
		}

		System.out.printf("%-11s %-12s", primClassifStr, subClassifStr);

		if (nonPrintable)
		{
			hexPrint(24, tokenStr);
		}

		else
			System.out.printf("%s\n", tokenStr);

		// Debug
		// System.out.println("INFO: [str: '" + tokenStr + "'] [nonPrintable: "
		// + nonPrintable + "] [column: " + iColPos + "] [lineNumber: " +
		// iSourceLineNr + "]");

	}

	/**
	 * Function: clone
	 * TODO:
	 * Preconditions:
	 * 		-
	 * Purpose:
	 * 
	 * @return a newToken with all the same credentials of currentToken
	*/
	public Token clone()
	{
		Token newToken = new Token();
		newToken.tokenStr = this.tokenStr;
		newToken.primClassif = this.primClassif;
		newToken.subClassif = this.subClassif;
		newToken.iColPos = this.iColPos;
		newToken.iSourceLineNr = this.iSourceLineNr;
		newToken.nonPrintable = this.nonPrintable;
		return newToken;
	}

	/**
	 * Prints a string that may contain non-printable characters as two lines.
	 * <p>
	 * On the first line, it prints printable characters by simply printing the
	 * character. For non-printable characters in the string, it prints ". ".
	 * <p>
	 * The second line prints a two character hex value for the non printable
	 * characters in the string line. For the printable characters, it prints a
	 * space.
	 * <p>
	 * It is sometimes necessary to print the first line on the end of an
	 * existing line of output. This would make it difficult to properly align
	 * the second line of output. The indent parameter is for indenting the
	 * second line.
	 * <p>
	 * <blockquote>
	 * 
	 * <pre>
	 * Example for the string "\tTX\tTexas\n"
	 *      . TX. Texas.
	 *      09  09     0A
	 * </pre>
	 * 
	 * </blockquote>
	 * <p>
	 * 
	 * @param indent
	 *            the number of spaces to indent the second printed line
	 * @param str
	 *            the string to print which may contain non-printable characters
	 * 
	 */
	public void hexPrint(int indent, String str)
	{
		int len = str.length();
		char[] charray = str.toCharArray();
		char ch;
		// print each character in the string
		for (int i = 0; i < len; i++)
		{
			ch = charray[i];
			if (ch > 31 && ch < 127) // ASCII printable characters
				System.out.printf("%c", ch);
			else
				System.out.printf(". ");
		}
		System.out.printf("\n");
		// indent the second line to the number of specified spaces
		for (int i = 0; i < indent; i++)
		{
			System.out.printf(" ");
		}
		// print the second line. Non-printable characters will be shown
		// as their hex value. Printable will simply be a space
		for (int i = 0; i < len; i++)
		{
			ch = charray[i];
			// only deal with the printable characters
			if (ch > 31 && ch < 127) // ASCII printable characters
				System.out.printf(" ", ch);
			else
				System.out.printf("%02X", (int) ch);
		}
		System.out.printf("\n");
	}

	enum PrimClassif
	{
		OPERATOR, OPERAND, SEPARATOR, CONTROL, FUNCTION, EOF
	}

	enum SubClassif
	{
		FLOW, END, IDENTIFIER, INTEGER, FLOAT, STRING, BOOLEAN, DATE, VOID, BUILTIN, DECLARE, USER
	}

	/**
	 * Function: ResultValue
	 * Preconditions: 
	 * 		- currentToken is the token to be changed
	 * 		to a ResultValue
	 * Purpose:
	 * 		- sets the ResultValue of the token based on DataType,
	 * 		and structure.
	 * @return the populated result classifications of a single token 
	 */
	public Value toResult()
	{
		
		if (this.tempValue != null) {
			Value val = tempValue;
			tempValue = null;
			return val;
		}
		
		Value res = new Value();
		
		switch (this.subClassif) {
		case INTEGER:
			res.dataType = DataType.INTEGER;
			res.structure = Structure.PRIMITIVE;
			res.intValue = Integer.parseInt(tokenStr);
			break;
		case FLOAT:
			res.dataType = DataType.FLOAT;
			res.structure = Structure.PRIMITIVE;
			res.floatValue = Double.parseDouble(tokenStr);
			break;
		case BOOLEAN:
			res.dataType = DataType.BOOLEAN;
			res.structure = Structure.PRIMITIVE;
			res.booleanValue = tokenStr.equals("T");
			break;
		case STRING:
			res.dataType = DataType.STRING;
			res.structure = Structure.PRIMITIVE;
			res.strValue = tokenStr;
			break;
		}
		
		return res;

	}
	
	public void storeValue(Value value) {
		this.tempValue = value;
	}

}