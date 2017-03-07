package havabol.storage;

import java.util.*;

import havabol.lexer.Token;

public class SymbolTable {
	public HashMap<String, STEntry> ST = new HashMap<>();
	int VAR_ARGS;

	/**
	 * main function for Symbol Table class which calls initGlobal
	 */

	public SymbolTable(){
		initGlobal();  
	}

	/**
	 * initGlobal function for initializing the Global Symbol Table with put
	 * functions.
	 */

	private void initGlobal(){
		ST.put("def", new STControl("def", Token.CONTROL, Token.FLOW));
		ST.put("enddef", new STControl("def", Token.CONTROL, Token.END));
		ST.put("if", new STControl("if", Token.CONTROL, Token.FLOW));
		ST.put("endif", new STControl("endif", Token.CONTROL, Token.END));
		ST.put("else", new STControl("else", Token.CONTROL, Token.FLOW));
		ST.put("for", new STControl("for", Token.CONTROL, Token.FLOW));
		ST.put("endfor", new STControl("endfor", Token.CONTROL, Token.END));
		ST.put("while", new STControl("while", Token.CONTROL, Token.FLOW));
		ST.put("endwhile", new STControl("endwhile", Token.CONTROL, Token.END));
		ST.put("print", new STFunction("print", Token.FUNCTION, DataType.VOID, Token.BUILTIN, VAR_ARGS));
		ST.put("Int", new STControl("Int", Token.CONTROL, Token.DECLARE));
		ST.put("Float", new STControl("Float", Token.CONTROL, Token.DECLARE));
		ST.put("String", new STControl("String", Token.CONTROL, Token.DECLARE));
		ST.put("Bool", new STControl("Bool", Token.CONTROL, Token.DECLARE));
		ST.put("Date", new STControl("Date", Token.CONTROL, Token.DECLARE));
		ST.put("LENGTH", new STFunction("LENGTH", Token.FUNCTION, DataType.INTEGER, Token.BUILTIN, VAR_ARGS));
		ST.put("MAXLENGTH", new STFunction("MAXLENGTH", Token.FUNCTION, DataType.INTEGER, Token.BUILTIN, VAR_ARGS));
		ST.put("SPACES", new STFunction("SPACES", Token.FUNCTION, DataType.BOOLEAN, Token.BUILTIN, VAR_ARGS));
		ST.put("ELEM", new STFunction("ELEM", Token.FUNCTION, DataType.INTEGER, Token.BUILTIN, VAR_ARGS));
		ST.put("MAXELEM", new STFunction("MAXELEM", Token.FUNCTION, DataType.INTEGER, Token.BUILTIN, VAR_ARGS));
		ST.put("and", new STEntry("and", Token.OPERATOR));
		ST.put("or", new STEntry("or", Token.OPERATOR));
		ST.put("not", new STEntry("not", Token.OPERATOR));
		ST.put("in", new STEntry("in", Token.OPERATOR));
		ST.put("notin", new STEntry("notin", Token.OPERATOR));

	}

	/**
	 * returns the symbol and its corresponding entry in the symbol table.
	 * 
	 * @param symbol
	 *            the symbol to get to the Symbol Table
	 */

	public STEntry getSymbol(String symbol){
		if(ST.containsKey(symbol)){
			return (STEntry) ST.get(symbol);
		}else{
			// TODO: error: symbol not found 
			return null;
		}

	}
	
	public boolean containsSymbol(String symbol) {
		return ST.containsKey(symbol);
	}

	/**
	 * creates and/or stores the symbol and its corresponding entry in the symbol table
	 * @param symbol the symbol to add to the Symbol Table
	 * @param entry the entry in the symbol table that corresponds to the symbol name
	 */
	public void createSymbol(String symbol, STEntry entry){
		ST.put(symbol, entry);
	}
	
	/**
	 * removes the symbol and its corresponding entry in the symbol table
	 * @param symbol the symbol to delete from the Symbol Table
	 */
	public void deleteSymbol(String symbol){
		if(ST.containsKey(symbol)){
			ST.remove(symbol);
		}else{
			// TODO: error: symbol not in table
		}
	}
	
	public void printSymbolTable() {
		System.out.println("******************** SymbolTable ********************");
		for (STEntry e : ST.values()) {
			if (e instanceof STIdentifier) {
				System.out.println(((STIdentifier) e).toString());
			} else if (e instanceof STFunction) {
				System.out.println(((STFunction) e).toString());
			} else if (e instanceof STControl) {
				System.out.println(((STControl) e).toString());
			} else {
				System.out.println(e);
			}
		}

		System.out.println("*****************************************************");
	}
}