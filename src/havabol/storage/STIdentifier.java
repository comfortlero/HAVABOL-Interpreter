package havabol.storage;

import havabol.error.IndexError;
import havabol.parser.*;
import havabol.storage.*;

/*
 * STIdentifier class for the Identifier symbol table entries.
 */
public class STIdentifier extends STEntry
{
	/*
	 * Constructor for STIdentifier subclass
	 */
	public DataType declaredType;
	public Structure structure;
	public int declaredSize = 1;
	public String parm;
	public int nonLocal;
	
	public Value[] arrayValue;
	public Value primitiveValue;
	public STIdentifier(String tokenStr, DataType declaredType, Structure structure, String parm, int nonLocal) {
		super(tokenStr, 0);
		this.declaredType = declaredType;
		this.structure = structure;
		this.parm = parm;
		this.nonLocal = nonLocal;
	}
	
	/**
	 * Fetches the ResultValue at the given array index if this ResultValue
	 * is an array value.
	 * @param parser Havabol parser that called this method
	 * @param index Index to access in array value
	 * @return a primitive ResultValue corresponding to the value at the given index
	 */
	public Value fetch(Parser parser, int index) {
		
		if (this.structure == Structure.PRIMITIVE) {
			throw new IndexError("Cannot refer to an index of a primitive value");
		}
		
		if (index >= this.declaredSize) {
			throw new IndexError("Array index is out of bounds");
		}
		
		if (this.arrayValue[index] == null) {
			throw new IndexError("Reference to undefined array index");
		}
		
		return this.arrayValue[index];
		
	}
	
	/**
	 * Stores the given ResultValue into the index of this array ResultValue
	 * @param parser Calling parser object
	 * @param index index to set in this array
	 * @param value value to set at the given index
	 */
	public void set(Parser parser, int index, Value value) {
		
		if (this.structure == Structure.PRIMITIVE) {
			throw new IndexError("Cannot refer to an index of a primitive value");
		}
		
		if (index >= this.declaredSize) {
			throw new IndexError("Array index is out of bounds");
		}
		
		this.arrayValue[index] = value;
		
	}
	
	/**
	 * Function:	toString
	 * Purpose:		returns string representation of identifier
	 */
	public String toString() {
		return String.format("IDENTIFIER: %s: %s %s %s %d", symbol, declaredType, structure, parm, nonLocal);
	}
	
}