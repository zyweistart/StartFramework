package start.application.core.constant;

/**
 * 数据类型验证
 */
public interface DataTypeValidation {

	final String isBoolean = "boolean,java.lang.Boolean";
	
	final String isShort = "short,java.lang.Short";
	
	final String isInteger = "int,java.lang.Integer";
	
	final String isFloat = "float,java.lang.Float";
	
	final String isLong = "long,java.lang.Long";
	
	final String isDouble = "double,java.lang.Double";

	final String isString = "java.lang.String";
	
	final String isDate = "java.util.Date";

	//有默认值的数据类型
	final String isDefaultValueDataType="boolean,short,int,float,long,double";
	//Entity类数组所支持的类型
	final String isSupportDataTypeArray="[Ljava.lang.String;";
	//Entity类字段所支持的基本类型
	final String isSupportDataType= isBoolean+","+isShort + "," + 
			isInteger + "," + isLong + "," + isFloat + "," + isDouble+","+isString+","+isDate;

}