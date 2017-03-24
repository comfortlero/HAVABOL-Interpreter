package havabol.runtime;

import java.util.List;
import havabol.error.*;
import havabol.lexer.Token;
import havabol.parser.*;
import havabol.storage.*;

public class Operators {
	
	/***
	 * Adds to evaluate operands
	 * @param parser - information about  values being parsed
	 * @param op1 	 - first operand to be evaluated with op2
	 * @param op2	 - second operand to be evaluated with op1
	 * @return
	 */
	public static ResultValue add(Parser parser, ResultValue op1, ResultValue op2) {
		
		//System.out.print("add: " + op1 + " " + op2);
		
		ResultValue res = new ResultValue();
		
		switch (op1.dataType) {
		case INTEGER:
			
			// Coerce second operand into an integer
			res.dataType = DataType.INTEGER;
			res.structure = Structure.PRIMITIVE;
			res.intValue = op1.intValue + op2.asInteger(parser).intValue;
			
			break;
		case FLOAT:
			
			// Coerce second operand into an float
			res.dataType = DataType.FLOAT;
			res.structure = Structure.PRIMITIVE;
			res.floatValue = op1.floatValue + op2.asFloat(parser).floatValue;
			
			break;
		case STRING:
			
			// Coerce both operands into floats
			res.dataType = DataType.FLOAT;
			res.structure = Structure.PRIMITIVE;
			res.floatValue = op1.asFloat(parser).floatValue + op2.asFloat(parser).floatValue;
			
			break;
		default:
			throw new TypeError("Left hand operand is not castable to numeric type for addition");
		}
		
		//System.out.println(" = " + res);
		
		return res;
	}
	
	/***
	 * Subtracts to evaluate operands
	 * @param parser - information about  values being parsed
	 * @param op1 	 - first operand to be evaluated with op2
	 * @param op2	 - second operand to be evaluated with op1
	 * @return
	 */
	public static ResultValue subtract(Parser parser, ResultValue op1, ResultValue op2) {
		
		//System.out.print("subtract: " + op1 + " " + op2);
		
		ResultValue res = new ResultValue();
		
		DataType resultType = op1.dataType;
		
		if (resultType == DataType.INTEGER) {
			
			if (op2.dataType != DataType.INTEGER) {
				op2 = op2.asInteger(parser);
			}
			
			res.dataType = DataType.INTEGER;
			res.structure = Structure.PRIMITIVE;
			res.intValue = op1.intValue - op2.intValue;
			
		} else if (resultType == DataType.FLOAT) {
			
			if (op2.dataType != DataType.FLOAT) {
				op2 = op2.asFloat(parser);
			}
			
			res.dataType = DataType.FLOAT;
			res.structure = Structure.PRIMITIVE;
			res.floatValue = op1.floatValue - op2.floatValue;
			
		} else {
			// TODO: handle str as first operand, etc
			throw new UnsupportedOperationError("First operand type cannot be used in addition.");
		}
		
		//System.out.println(" = " + res);
		
		return res;
	}
	
	/***
	 * Multiplies to evaluate operands
	 @param parser - information about  values being parsed
	 * @param op1 	 - first operand to be evaluated with op2
	 * @param op2	 - second operand to be evaluated with op1
	 * @return
	 */
	public static ResultValue multiply(Parser parser, ResultValue op1, ResultValue op2) {
		
		//System.out.print("multiply: " + op1 + " " + op2);
		
		ResultValue res = new ResultValue();
		
		DataType resultType = op1.dataType;
		
		if (resultType == DataType.INTEGER) {
			
			if (op2.dataType != DataType.INTEGER) {
				op2 = op2.asInteger(parser);
			}
			
			res.dataType = DataType.INTEGER;
			res.structure = Structure.PRIMITIVE;
			res.intValue = op1.intValue * op2.intValue;
			
		} else if (resultType == DataType.FLOAT) {
			
			if (op2.dataType != DataType.FLOAT) {
				op2 = op2.asFloat(parser);
			}
			
			res.dataType = DataType.FLOAT;
			res.structure = Structure.PRIMITIVE;
			res.floatValue = op1.floatValue * op2.floatValue;
			
		} else {
			// TODO: handle str as first operand, etc
			throw new UnsupportedOperationError("First operand type cannot be used in addition.");
		}
		
		//System.out.println(" = " + res);
		
		return res;
	}
	
	//divide function
	public static ResultValue divide(Parser parser, ResultValue op1, ResultValue op2) {
		
		//System.out.print("divide: " + op1 + " " + op2);
		
		ResultValue res = new ResultValue();
		
		DataType resultType = op1.dataType;
		
		if (resultType == DataType.INTEGER) {
			
			if (op2.dataType != DataType.INTEGER) {
				op2 = op2.asInteger(parser);
			}
			
			res.dataType = DataType.INTEGER;
			res.structure = Structure.PRIMITIVE;
			res.intValue = op1.intValue / op2.intValue;
			
		} else if (resultType == DataType.FLOAT) {
			
			if (op2.dataType != DataType.FLOAT) {
				op2 = op2.asFloat(parser);
			}
			
			res.dataType = DataType.FLOAT;
			res.structure = Structure.PRIMITIVE;
			res.floatValue = op1.floatValue / op2.floatValue;
			
		} else {
			// TODO: handle str as first operand, etc
			throw new UnsupportedOperationError("First operand type cannot be used in addition.");
		}
		
		//System.out.println(" = " + res);
		
		return res;
	}
	
	public static ResultValue exponentiate(Parser parser, ResultValue op1, ResultValue op2) {
		
		//System.out.print("exponentiate: " + op1 + " " + op2);
		
		ResultValue res = new ResultValue();
		
		// op1 dataType determines result of 
		if (op1.dataType == DataType.INTEGER) {
			op2 = op2.asInteger(parser);
			
			res.intValue = (int) Math.pow(op1.intValue, op2.intValue);
			res.dataType = DataType.INTEGER;
			res.structure = Structure.PRIMITIVE;
			
		} else {
			op1 = op1.asFloat(parser);
			op2 = op2.asFloat(parser);
			
			res.floatValue = Math.pow(op1.floatValue, op2.floatValue);
			res.dataType = DataType.FLOAT;
			res.structure = Structure.PRIMITIVE;
		}
		
		//System.out.println(" = " + res);
		
		return res;
	}

	public static ResultValue unaryMinus(Parser parser, ResultValue op1)
	{
		
		ResultValue res = new ResultValue();
		res.dataType = op1.dataType;
		res.structure = Structure.PRIMITIVE;
		
		if (op1.dataType == DataType.INTEGER) {
			res.intValue = op1.intValue * -1;
		} else if (op1.dataType == DataType.FLOAT) {
			res.floatValue = op1.floatValue * -1;
		} else {
			throw new UnsupportedOperationError("Unary minus may not be used on non-numeric types.");
		}
		
		return res;
	}

	public static ResultValue unaryNot(Parser parser, ResultValue op1)
	{
		
		ResultValue res = op1.asBoolean(parser);
		res.booleanValue = !res.booleanValue;
		
		return res;
	}

	public static ResultValue less(Parser parser, ResultValue op1, ResultValue op2)
	{
		
		ResultValue res = new ResultValue();
		res.structure = Structure.PRIMITIVE;
		res.dataType = DataType.BOOLEAN;
		
		if (op1.dataType == DataType.INTEGER) {
			
			op2 = op2.asInteger(parser);
			res.booleanValue = op1.intValue < op2.intValue;
			
		} else if (op1.dataType == DataType.FLOAT) {
			
			op2 = op2.asFloat(parser);
			res.booleanValue = op1.floatValue < op2.floatValue;
			
		} else if (op1.dataType == DataType.STRING) {
			
			op2 = op2.asString(parser);
			res.booleanValue = op1.strValue.compareTo(op2.strValue) < 0;
			
		} else {
			throw new UnsupportedOperationError("");
		}
		
		return res;
	}

	public static ResultValue greater(Parser parser, ResultValue op1, ResultValue op2)
	{
		ResultValue res = new ResultValue();
		res.structure = Structure.PRIMITIVE;
		res.dataType = DataType.BOOLEAN;
		
		if (op1.dataType == DataType.INTEGER) {
			
			op2 = op2.asInteger(parser);
			res.booleanValue = op1.intValue > op2.intValue;
			
		} else if (op1.dataType == DataType.FLOAT) {
			
			op2 = op2.asFloat(parser);
			res.booleanValue = op1.floatValue > op2.floatValue;
			
		} else if (op1.dataType == DataType.STRING) {
			
			op2 = op2.asString(parser);
			res.booleanValue = op1.strValue.compareTo(op2.strValue) > 0;
			
		} else {
			throw new UnsupportedOperationError("");
		}
		
		return res;
	}

	public static ResultValue lessEqual(Parser parser, ResultValue op1, ResultValue op2)
	{
		ResultValue res = new ResultValue();
		res.structure = Structure.PRIMITIVE;
		res.dataType = DataType.BOOLEAN;
		
		if (op1.dataType == DataType.INTEGER) {
			
			op2 = op2.asInteger(parser);
			res.booleanValue = op1.intValue <= op2.intValue;
			
		} else if (op1.dataType == DataType.FLOAT) {
			
			op2 = op2.asFloat(parser);
			res.booleanValue = op1.floatValue <= op2.floatValue;
			
		} else if (op1.dataType == DataType.STRING) {
			
			op2 = op2.asString(parser);
			res.booleanValue = op1.strValue.compareTo(op2.strValue) <= 0;
			
		} else {
			throw new UnsupportedOperationError("");
		}
		
		return res;
	}

	public static ResultValue greaterEqual(Parser parser, ResultValue op1, ResultValue op2)
	{
		ResultValue res = new ResultValue();
		res.structure = Structure.PRIMITIVE;
		res.dataType = DataType.BOOLEAN;
		
		if (op1.dataType == DataType.INTEGER) {
			
			op2 = op2.asInteger(parser);
			res.booleanValue = op1.intValue >= op2.intValue;
			
		} else if (op1.dataType == DataType.FLOAT) {
			
			op2 = op2.asFloat(parser);
			res.booleanValue = op1.floatValue >= op2.floatValue;
			
		} else if (op1.dataType == DataType.STRING) {
			
			op2 = op2.asString(parser);
			res.booleanValue = op1.strValue.compareTo(op2.strValue) >= 0;
			
		} else {
			throw new UnsupportedOperationError("");
		}
		
		return res;
	}

	public static ResultValue doubleEqual(Parser parser, ResultValue op1, ResultValue op2)
	{
		ResultValue res = new ResultValue();
		res.structure = Structure.PRIMITIVE;
		res.dataType = DataType.BOOLEAN;
		
		if (op1.dataType == DataType.INTEGER) {
			
			op2 = op2.asInteger(parser);
			res.booleanValue = op1.intValue == op2.intValue;
			
		} else if (op1.dataType == DataType.FLOAT) {
			
			op2 = op2.asFloat(parser);
			res.booleanValue = op1.floatValue == op2.floatValue;
			
		} else if (op1.dataType == DataType.STRING) {
			
			op2 = op2.asString(parser);
			res.booleanValue = op1.strValue.equals(op2.strValue);
			
		} else if (op1.dataType == DataType.BOOLEAN) {
			
			op2 = op2.asBoolean(parser);
			res.booleanValue = op1.booleanValue == op2.booleanValue;
			
		} else {
			throw new UnsupportedOperationError("Unsupported type");
		}
		
		return res;
	}

	public static ResultValue notEqual(Parser parser, ResultValue op1, ResultValue op2)
	{
		ResultValue res = new ResultValue();
		res.structure = Structure.PRIMITIVE;
		res.dataType = DataType.BOOLEAN;
		
		if (op1.dataType == DataType.INTEGER) {
			
			op2 = op2.asInteger(parser);
			res.booleanValue = op1.intValue != op2.intValue;
			
		} else if (op1.dataType == DataType.FLOAT) {
			
			op2 = op2.asFloat(parser);
			res.booleanValue = op1.floatValue != op2.floatValue;
			
		} else if (op1.dataType == DataType.STRING) {
			
			op2 = op2.asString(parser);
			res.booleanValue = !op1.strValue.equals(op2.strValue);
			
		} else {
			throw new UnsupportedOperationError("");
		}
		
		return res;
	}

	public static ResultValue logicalAnd(Parser parser, ResultValue op1, ResultValue op2)
	{
		ResultValue res = new ResultValue();
		res.structure = Structure.PRIMITIVE;
		res.dataType = DataType.BOOLEAN;
		
		op1 = op1.asBoolean(parser);
		op2 = op2.asBoolean(parser);
		
		res.booleanValue = op1.booleanValue && op2.booleanValue;
		
		return res;
	}

	public static ResultValue logicalOr(Parser parser, ResultValue op1, ResultValue op2)
	{
		ResultValue res = new ResultValue();
		res.structure = Structure.PRIMITIVE;
		res.dataType = DataType.BOOLEAN;
		
		op1 = op1.asBoolean(parser);
		op2 = op2.asBoolean(parser);
		
		res.booleanValue = op1.booleanValue || op2.booleanValue;
		
		return res;
	}

	public static ResultValue concatenate(Parser parser, ResultValue op1, ResultValue op2)
	{
		ResultValue res = new ResultValue();
		res.structure = Structure.PRIMITIVE;
		res.dataType = DataType.STRING;
		
		op1 = op1.asString(parser);
		op2 = op2.asString(parser);
		
		res.strValue = op1.strValue.concat(op2.strValue);
		
		return res;
	}

}
