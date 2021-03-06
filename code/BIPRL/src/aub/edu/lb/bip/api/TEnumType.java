package aub.edu.lb.bip.api;


public enum TEnumType {
	INT(0, "int"),
	CONST_INT(1, "const int"),
	WIRE_INT(2, "wire int"),
	BOOLEAN(3, "boolean"),
	WIRE_BOOLEAN(4, "wire boolean"),
	ARRAY_WIRE_BOOLEAN(5, "wire boolean"),
	TwoD_ARRAY_WIRE_BOOLEAN(6, "wire boolean"),
	TwoD_ARRAY_BOOLEAN(7, "boolean"),
	UNORDERED_MAP_STRING_DOUBLE(8,"std::unordered_map<std::string, double>"),
	STRING(9, "string");

	private int value; 
	private String type; 
	
	public static final int INT_VALUE = 0;
	public static final int CONS_INT_VALUE = 1;
	public static final int CONS_WIRE_INT_VALUE = 2;
	public static final int BOOLEAN_VALUE = 3;
	public static final int WIRE_BOOLEAN_VALUE = 4;
	public static final int ARRAY_WIRE_BOOLEAN_VALUE = 5;
	public static final int TwoD_ARRAY_WIRE_BOOLEAN_VALUE = 6;
	public static final int TwoD_ARRAY_BOOLEAN_VALUE = 7;
	public static final int UNORDERED_MAP_STRING_DOUBLE_VALUE = 8;
	public static final int STRING_VALUE = 9;


	
	TEnumType(int v, String t) {
		value = v;
		type = t; 
	}

	public String getName() {
		return type; 
	}

	public int getValue() {
		return value; 
	}
}
