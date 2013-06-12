package ncsa.d2k.modules.core.datatype.table;

import ncsa.d2k.modules.core.io.file.input.FlatFileParser;

/**
 * Defines the different types of columns that can make up a table.
 */
public final class ColumnTypes {
	/** A column of integer values */
	public static final int INTEGER = 0;
	/** A column of float values */
	public static final int FLOAT = 1;
	/** A column of double values */
	public static final int DOUBLE = 2;
	/** A column of short values */
	public static final int SHORT = 3;
	/** A column of long values */
	public static final int LONG = 4;
	/** A column of String values */
	public static final int STRING = 5;
	/** A column of char[] values */
	public static final int CHAR_ARRAY = 6;
	/** A column of byte[] values */
	public static final int BYTE_ARRAY = 7;
	/** A column of boolean values */
	public static final int BOOLEAN = 8;
	/** A column of Object values */
	public static final int OBJECT = 9;
	/** A column of byte values */
	public static final int BYTE = 10;
	/** A column of char values */
	public static final int CHAR = 11;
	/** A column of char values */
	public static final int NOMINAL = 12;
        /** A column of unspecified values -- used for sparse tables*/
        public static final int UNSPECIFIED = 13;

  private static final String[] _names = {"INTEGER",
    "FLOAT",
    "DOUBLE",
    "SHORT",
    "LONG",
    "STRING",
    "CHARACTER ARRAY",
    "BYTE ARRAY",
    "BOOLEAN",
    "OBJECT",
    "BYTE",
    "CHAR",
    "NOMINAL",
    "UNSPECIFIED"};

  public static String getTypeName(int i){
    return _names[i];
  }

  public static boolean isEqualNumeric(String inString)
  {
    if(inString.toLowerCase().equals("number") ||
       inString.toLowerCase().equals("numeric") ||
       inString.toLowerCase().equals("decimal") ||
       inString.toLowerCase().equals("bigint") ||
       inString.toLowerCase().equals("smallint") ||
       inString.toLowerCase().equals("integer") ||
       inString.toLowerCase().equals("real") ||
       inString.toLowerCase().equals("double"))

      return true;

    // for file parser numeric data type
    else if(inString.toLowerCase().equals(FlatFileParser.INT_TYPE.toLowerCase()) ||
            inString.toLowerCase().equals(FlatFileParser.FLOAT_TYPE.toLowerCase()) ||
            inString.toLowerCase().equals(FlatFileParser.DOUBLE_TYPE.toLowerCase()) ||
            inString.toLowerCase().equals(FlatFileParser.LONG_TYPE.toLowerCase()) ||
            inString.toLowerCase().equals(FlatFileParser.SHORT_TYPE.toLowerCase()))
      return true;

    else
      return false;

  }

  public static boolean isContainNumeric(String inString)
  {
    if(inString.toLowerCase().indexOf("number")>=0 ||
       inString.toLowerCase().indexOf("numeric")>=0 ||
       inString.toLowerCase().indexOf("decimal")>=0 ||
       inString.toLowerCase().indexOf("bigint")>=0 ||
       inString.toLowerCase().indexOf("smallint")>=0 ||
       inString.toLowerCase().indexOf("integer")>=0 ||
       inString.toLowerCase().indexOf("real")>=0 ||
       inString.toLowerCase().indexOf("double")>=0)

      return true;

    else
      return false;

  }
}
