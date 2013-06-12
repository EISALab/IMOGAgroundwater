package ncsa.d2k.modules.core.datatype.table.basic;

import ncsa.d2k.modules.core.datatype.table.*;

/**
	Useful methods that are used on Columns.
*/
final public class ColumnUtilities {

	/**
		Create a new DoubleColumn with a copy of the data from
		sc.
		@param sc the original column
		@return a DoubleColumn initialized with the data from sc
	*/
	public static DoubleColumn toDoubleColumn(Column sc) {
		DoubleColumn dc = new DoubleColumn(sc.getNumRows());
        try {
		for(int i = 0; i < sc.getNumRows(); i++)
			dc.setDouble(sc.getDouble(i), i);
        } catch (NumberFormatException nfe) {
            
            // This column is not numberic.
            for(int i = 0; i < sc.getNumRows(); i++)
                dc.setDouble(0.0, i);
        }
		dc.setLabel(sc.getLabel());
		dc.setComment(sc.getComment());
		ColumnUtilities.copyMissingValues(sc, dc);
		return dc;
	}

	/**
		Create a new IntColumn with a copy of the data from
		sc.
		@param sc the original column
		@return an IntColumn initialized with the data from sc
	*/
	public static IntColumn toIntColumn(Column sc) {
		IntColumn dc = new IntColumn(sc.getNumRows());
		for(int i = 0; i < sc.getNumRows(); i++)
            try {
			   dc.setInt(sc.getInt(i), i);
            } catch (NumberFormatException nfe) {
               dc.setInt(0, i);
            }
		dc.setLabel(sc.getLabel());
		dc.setComment(sc.getComment());
		ColumnUtilities.copyMissingValues(sc, dc);
		return dc;
	}

	/**
		Create a new LongColumn with a copy of the data from
		sc.
		@param sc the original column
		@return a LongColumn initialized with the data from sc
	*/
	public static LongColumn toLongColumn(Column sc) {
		LongColumn dc = new LongColumn(sc.getNumRows());
        try {
    		for(int i = 0; i < sc.getNumRows(); i++)
    			dc.setLong(sc.getLong(i), i);
        } catch (NumberFormatException nfe) {
                
            // This column is not numberic.
            for(int i = 0; i < sc.getNumRows(); i++)
                dc.setLong(0, i);
        }
		dc.setLabel(sc.getLabel());
		dc.setComment(sc.getComment());
		ColumnUtilities.copyMissingValues(sc, dc);
		return dc;
	}

	/**
		Create a new ShortColumn with a copy of the data from
		sc.
		@param sc the original column
		@return a ShortColumn initialized with the data from sc
	*/
	public static ShortColumn toShortColumn(Column sc) {
		ShortColumn dc = new ShortColumn(sc.getNumRows());
		try {
            for(int i = 0; i < sc.getNumRows(); i++)
			    dc.setShort(sc.getShort(i), i);
        } catch (NumberFormatException nfe) {
            
            // This column is not numberic.
            for(int i = 0; i < sc.getNumRows(); i++)
                dc.setShort((short) 0, i);
        }

		dc.setLabel(sc.getLabel());
		dc.setComment(sc.getComment());
		ColumnUtilities.copyMissingValues(sc, dc);
		return dc;
	}

	/**
		Create a new FloatColumn with a copy of the data from
		sc.
		@param sc the original column
		@return a FloatColumn initialized with the data from sc
	*/
	public static FloatColumn toFloatColumn(Column sc) {
		FloatColumn dc = new FloatColumn(sc.getNumRows());
		try {
            for(int i = 0; i < sc.getNumRows(); i++)
			    dc.setFloat(sc.getFloat(i), i);
        } catch (NumberFormatException nfe) {
            
            // This column is not numberic.
            for(int i = 0; i < sc.getNumRows(); i++)
                dc.setFloat((float) 0.0, i);
        }
		dc.setLabel(sc.getLabel());
		dc.setComment(sc.getComment());
		ColumnUtilities.copyMissingValues(sc, dc);
		return dc;
	}

	/**
		Create a new BooleanColumn with a copy of the data from
		sc.
		@param sc the original column
		@return a BooleanColumn initialized with the data from sc
	*/
	public static BooleanColumn toBooleanColumn(Column sc) {
		BooleanColumn dc = new BooleanColumn(sc.getNumRows());
		for(int i = 0; i < sc.getNumRows(); i++)
			dc.setBoolean(sc.getBoolean(i), i);
		dc.setLabel(sc.getLabel());
		dc.setComment(sc.getComment());
		ColumnUtilities.copyMissingValues(sc, dc);
		return dc;
	}

	/**
		Create a new ObjectColumn with the data from sc.  The
		Objects in the new column are references to the Objects in
		the original column.
		@param sc the original column
		@return an ObjectColumn initialized with the data from sc
	*/
	public static ObjectColumn toObjectColumn(Column sc) {
		ObjectColumn dc = new ObjectColumn(sc.getNumRows());
		for(int i = 0; i < sc.getNumRows(); i++)
			dc.setObject(sc.getObject(i), i);
		dc.setLabel(sc.getLabel());
		dc.setComment(sc.getComment());
		ColumnUtilities.copyMissingValues(sc, dc);
		return dc;
	}

	/**
		Create a new StringColumn with the data from sc.  The
		Objects in the new column are references to the Objects in
		the original column.
		@param sc the original column
		@return a StringColumn initialized with the data from sc
	*/
	public static StringColumn toStringColumn(Column sc) {
		StringColumn dc = new StringColumn(sc.getNumRows());
		for(int i = 0; i < sc.getNumRows(); i++)
			dc.setString(sc.getString(i), i);
		dc.setLabel(sc.getLabel());
		dc.setComment(sc.getComment());
		ColumnUtilities.copyMissingValues(sc, dc);
		return dc;
	}

	/**
		Create a new StringColumn with the data from sc.  The
		Objects in the new column are references to the Objects in
		the original column.
		@param sc the original column
		@return a StringColumn initialized with the data from sc
	*/
	public static ByteArrayColumn toByteArrayColumn(Column sc) {
		ByteArrayColumn dc = new ByteArrayColumn(sc.getNumRows());
		for(int i = 0; i < sc.getNumRows(); i++)
			dc.setBytes(sc.getBytes(i), i);
		dc.setLabel(sc.getLabel());
		dc.setComment(sc.getComment());
		ColumnUtilities.copyMissingValues(sc, dc);
		return dc;
	}

	/**
		Create a new StringColumn with the data from sc.  The
		Objects in the new column are references to the Objects in
		the original column.
		@param sc the original column
		@return a StringColumn initialized with the data from sc
	*/
	public static CharArrayColumn toCharArrayColumn(Column sc) {
		CharArrayColumn dc = new CharArrayColumn(sc.getNumRows());
		for(int i = 0; i < sc.getNumRows(); i++)
			dc.setChars(sc.getChars(i), i);
		dc.setLabel(sc.getLabel());
		dc.setComment(sc.getComment());
		ColumnUtilities.copyMissingValues(sc, dc);
		return dc;
	}


	/**
		Create a new StringColumn with the data from sc.  The
		Objects in the new column are references to the Objects in
		the original column.
		@param sc the original column
		@return a StringColumn initialized with the data from sc
	*/
	public static ByteColumn toByteColumn(Column sc) {
		ByteColumn dc = new ByteColumn(sc.getNumRows());
		for(int i = 0; i < sc.getNumRows(); i++)
			dc.setByte(sc.getByte(i), i);
		dc.setLabel(sc.getLabel());
		dc.setComment(sc.getComment());
		ColumnUtilities.copyMissingValues(sc, dc);
		return dc;
	}

	/**
		Create a new StringColumn with the data from sc.  The
		Objects in the new column are references to the Objects in
		the original column.
		@param sc the original column
		@return a StringColumn initialized with the data from sc
	*/
	public static CharColumn toCharColumn(Column sc) {
		CharColumn dc = new CharColumn(sc.getNumRows());
		for(int i = 0; i < sc.getNumRows(); i++)
			dc.setChar(sc.getChar(i), i);
		dc.setLabel(sc.getLabel());
		dc.setComment(sc.getComment());
		ColumnUtilities.copyMissingValues(sc, dc);
		return dc;
	}

	/**
	 * Copy the missing values from one column to another.
	 * @param from column to copy missing values from.
	 * @param to column to copy the missing values to.
	 */
	private static void copyMissingValues(Column from, Column to) {
		boolean[] orig = ((MissingValuesColumn)from).getMissingValues();
		boolean[] copy = new boolean[orig.length];
		System.arraycopy(orig, 0, copy, 0, copy.length);
		((MissingValuesColumn)to).setMissingValues(copy);
	}

    /**
       Loop through the items in column, if they can all be represented
       numerically, return true.  Otherwise return false.
       @param column the column to test
       @return true if column contains only numeric data, false otherwise
    */
    public static boolean isNumericColumn(Column column) {
        int numRows = column.getNumRows();
        for(int row = 0; row < numRows; row++) {
            try {
                Double d = Double.valueOf(column.getString(row));
            }
            catch(Exception e) {
                return false;
            }
        }
        return true;

    }


    /**
       Create a column given the type and size.
       @param type the type of column to create
       @param size the initial size of the column
       @return a new, empty column
    */
    public static Column createColumn(String type, int size) {
        if(type.compareToIgnoreCase("String") == 0)
            return new StringColumn(size);
        else if(type.compareToIgnoreCase("float") == 0)
            return new FloatColumn(size);
        else if(type.compareToIgnoreCase("double") == 0)
            return new DoubleColumn(size);
        else if(type.compareToIgnoreCase("int") == 0)
            return new IntColumn(size);
        else if(type.compareToIgnoreCase("boolean") == 0)
            return new BooleanColumn(size);
        else if(type.compareToIgnoreCase("char[]") == 0)
            return new ContinuousCharArrayColumn(size);
        else if(type.compareToIgnoreCase("byte[]") == 0)
            return new ContinuousByteArrayColumn(size);
        else if(type.compareToIgnoreCase("long") == 0)
            return new LongColumn(size);
        else if(type.compareToIgnoreCase("short") == 0)
            return new ShortColumn(size);
	else
	    return new StringColumn(size);
    }

	/**
		CopyColumn

		make a copy of the column data in a Table (the interface kind of Table) and return
		it as a Column (The package 'basic' kind of Column)

		@param sourceTable the table to copy the column out of
		@param colIndex which column in the table to copy
		@author pgroves
	*/
	public static Column copyColumn(Table sourceTable, int colIndex){
		int type=sourceTable.getColumnType(colIndex);
		int numRows=sourceTable.getNumRows();
		Column c;
		switch(type){
			case (ColumnTypes.INTEGER) : {
				c = sourceTable.getColumn(colIndex);
				c= new IntColumn((int[])c.getInternal());
				break;
			}
			case (ColumnTypes.FLOAT) : {
				c = sourceTable.getColumn(colIndex);
				c= new FloatColumn((float[])c.getInternal());
				break;
			}
			case (ColumnTypes.SHORT) : {
				c = sourceTable.getColumn(colIndex);
				c= new ShortColumn((short[])c.getInternal());
				break;
			}
			case (ColumnTypes.LONG) : {
				c = sourceTable.getColumn(colIndex);
				c= new LongColumn((long[])c.getInternal());
				break;
			}
			case (ColumnTypes.STRING) : {
				c = sourceTable.getColumn(colIndex);
				c= new StringColumn((String[])c.getInternal());
				break;
			}
			case (ColumnTypes.CHAR_ARRAY) : {
				c = sourceTable.getColumn(colIndex);
				c= new CharArrayColumn((char [][])c.getInternal());
				break;
			}
			case (ColumnTypes.BYTE_ARRAY) : {
				c = sourceTable.getColumn(colIndex);
				c= new ByteArrayColumn((byte[][])c.getInternal());
				break;
			}
			case (ColumnTypes.BOOLEAN) : {
				c = sourceTable.getColumn(colIndex);
				c= new BooleanColumn((boolean[])c.getInternal());
				break;
			}
			case (ColumnTypes.OBJECT) : {
				c = sourceTable.getColumn(colIndex);
				c= new ObjectColumn((Object [])c.getInternal());
				break;
			}
			case (ColumnTypes.BYTE) : {
				c = sourceTable.getColumn(colIndex);
				c= new ByteColumn((byte[])c.getInternal());
				break;
			}
			case (ColumnTypes.CHAR) : {
				c = sourceTable.getColumn(colIndex);
				c= new CharColumn((char[])c.getInternal());
				break;
			}
			case (ColumnTypes.DOUBLE) : {
				c = sourceTable.getColumn(colIndex);
				c= new DoubleColumn((double[])c.getInternal());
				break;

			}
			default : {
				System.err.println("ColumnUtilities:CopyColumn: Invalid Column Type");
				c= new ObjectColumn(numRows);
			}
		}
		c.setLabel(sourceTable.getColumnLabel(colIndex));
		c.setComment(sourceTable.getColumnComment(colIndex));
		return c;
	}
	/**
       Create a column given the type and size.
       @param type the type of column to create
       @param size the initial size of the column
       @return a new, empty column
    */
    public static Column createColumn(int type, int size) {
		Column c;
		switch(type){
			case (ColumnTypes.INTEGER) : {
				c= new IntColumn(size);
				break;
			}
			case (ColumnTypes.FLOAT) : {
				c= new FloatColumn(size);
				break;
			}
			case (ColumnTypes.SHORT) : {
				c= new ShortColumn(size);
				break;
			}
			case (ColumnTypes.LONG) : {
				c= new LongColumn(size);
				break;
			}
			case (ColumnTypes.STRING) : {
				c= new StringObjectColumn(size);
				break;
			}
			case (ColumnTypes.CHAR_ARRAY) : {
				c= new CharArrayColumn(size);
				break;
			}
			case (ColumnTypes.BYTE_ARRAY) : {
				c= new ByteArrayColumn(size);
				break;
			}
			case (ColumnTypes.BOOLEAN) : {
				c= new BooleanColumn(size);
				break;
			}
			case (ColumnTypes.OBJECT) : {
				c= new ObjectColumn(size);
				break;
			}
			case (ColumnTypes.BYTE) : {
				c= new ByteColumn(size);
				break;
			}
			case (ColumnTypes.CHAR) : {
				c= new CharColumn(size);
				break;
			}
			case (ColumnTypes.DOUBLE) : {
				c= new DoubleColumn(size);
				break;

			}
			case (ColumnTypes.NOMINAL) : {
				c= new StringColumn(size);
				break;

			}
			default : {
				//System.err.println(	"ColumnUtilities:CopyColumn"+
				//					": Invalid Column Type");
				//c= new ObjectColumn();
                c = new StringObjectColumn(size);
			}
		}
		c.setLabel("");
		return c;
	}

	/**
	 * creates a column of the same type and with the same 
	 * label as the one passed in, but with the specified
	 * number of rows and not filled in at all.
	 *
	 * @param tbl a source table that contains the column to
	 * 	do the 'meta copy' of
	 * @param colIndex which column in table to use as a source
	 * @param numRows the number of rows that the new column
	 * 	will contain
	 * 
	 * @return A new column, with numRows number of rows, and the
	 * same type and label as the source column
	 *
	 * @author pgroves 03/29/04
	 */
	public static Column metaColumnCopy(Table tbl, int colIndex, int numRows){
		String label = tbl.getColumnLabel(colIndex);
		int type = tbl.getColumnType(colIndex);
		Column col = createColumn(type, numRows);
		col.setLabel(label);
		return col;
	}
		
	
	/**
		Creates a copy of a column, with all of the data copied by
		value.
	
		@param tbl the table to copy the column out of
		@param colIndex which column in the table to copy
		@author pgroves

	*/
	
	public static Column deepCopyColumn(Table tbl, int colIndex){
		
		int type = tbl.getColumnType(colIndex);
		int size = tbl.getNumRows();

		Column col=ColumnUtilities.createColumn(type,size);
		col.setLabel(tbl.getColumnLabel(colIndex));
		col.setComment(tbl.getColumnLabel(colIndex));
		
		switch(type){
			case (ColumnTypes.DOUBLE) : {
				for(int i=0; i<size;i++){
					col.setDouble(tbl.getDouble(i,colIndex),i);
				}
				break;
			}
			case (ColumnTypes.INTEGER) : {
				for(int i=0; i<size;i++){
					col.setInt(tbl.getInt(i,colIndex),i);
				}
				break;
			}
			case (ColumnTypes.FLOAT) : {
				for(int i=0; i<size;i++){
					col.setFloat(tbl.getFloat(i,colIndex), i);
				}
				break;
			}
			case (ColumnTypes.SHORT) : {
				for(int i=0; i<size;i++){
					col.setShort(tbl.getShort(i,colIndex),i);
				}
				break;
			}
			case (ColumnTypes.LONG) : {
				for(int i=0; i<size;i++){
					col.setLong(tbl.getLong(i,colIndex),i);
				}
				break;
			}
			case (ColumnTypes.STRING) : {
				for(int i=0; i<size;i++){
					col.setString(tbl.getString(i,colIndex),i);
				}
				break;
			}
			case (ColumnTypes.CHAR_ARRAY) : {
				for(int i=0; i<size;i++){
					col.setChars(tbl.getChars(i,colIndex),i);
				}
				break;
			}
			case (ColumnTypes.BYTE_ARRAY) : {
				for(int i=0; i<size;i++){
					col.setBytes(tbl.getBytes(i,colIndex),i);
				}
				break;
			}
			case (ColumnTypes.BOOLEAN) : {
				for(int i=0; i<size;i++){
					col.setBoolean(tbl.getBoolean(i,colIndex),i);
				}
				break;
			}
			case (ColumnTypes.OBJECT) : {
				for(int i=0; i<size;i++){
					col.setObject(tbl.getObject(i,colIndex),i);
				}
				break;
			}
			case (ColumnTypes.BYTE) : {
				for(int i=0; i<size;i++){
					col.setByte(tbl.getByte(i,colIndex),i);
				}
				break;
			}
			case (ColumnTypes.CHAR) : {
				for(int i=0; i<size;i++){
					col.setChar(tbl.getChar(i,colIndex),i);
				}
				break;
			}
			default : {
			}
		}
		return col;


	}
	
		

		
	/**
		This is for creating a subset from a Table interface object
		and putting it into a TableImpl object

		@param tbl the original table
		@param colIndex which column to make a subset of
		@param subset the indices of the rows from the original
						column to put in the new column
		@return a new Column object of the same datatype as the
				original column of tbl w/ the entries being the
				subset values

		@author pgroves 5/30/02
	*/
	public static Column createColumnSubset(Table tbl, int colIndex,
											int[] subset){
		int type=tbl.getColumnType(colIndex);
		int size=subset.length;

		Column col=ColumnUtilities.createColumn(type,size);
		col.setLabel(tbl.getColumnLabel(colIndex));
		col.setComment(tbl.getColumnLabel(colIndex));
		switch(type){
			case (ColumnTypes.DOUBLE) : {
				for(int i=0; i<size;i++){
					col.setDouble(tbl.getDouble(subset[i],colIndex),i);
				}
				break;
			}
			case (ColumnTypes.INTEGER) : {
				for(int i=0; i<size;i++){
					col.setInt(tbl.getInt(subset[i],colIndex),i);
				}
				break;
			}
			case (ColumnTypes.FLOAT) : {
				for(int i=0; i<size;i++){
					col.setFloat(tbl.getFloat(subset[i],colIndex), i);
				}
				break;
			}
			case (ColumnTypes.SHORT) : {
				for(int i=0; i<size;i++){
					col.setShort(tbl.getShort(subset[i],colIndex),i);
				}
				break;
			}
			case (ColumnTypes.LONG) : {
				for(int i=0; i<size;i++){
					col.setLong(tbl.getLong(subset[i],colIndex),i);
				}
				break;
			}
			case (ColumnTypes.STRING) : {
				for(int i=0; i<size;i++){
					col.setString(tbl.getString(subset[i],colIndex),i);
				}
				break;
			}
			case (ColumnTypes.CHAR_ARRAY) : {
				for(int i=0; i<size;i++){
					col.setChars(tbl.getChars(subset[i],colIndex),i);
				}
				break;
			}
			case (ColumnTypes.BYTE_ARRAY) : {
				for(int i=0; i<size;i++){
					col.setBytes(tbl.getBytes(subset[i],colIndex),i);
				}
				break;
			}
			case (ColumnTypes.BOOLEAN) : {
				for(int i=0; i<size;i++){
					col.setBoolean(tbl.getBoolean(subset[i],colIndex),i);
				}
				break;
			}
			case (ColumnTypes.OBJECT) : {
				for(int i=0; i<size;i++){
					col.setObject(tbl.getObject(subset[i],colIndex),i);
				}
				break;
			}
			case (ColumnTypes.BYTE) : {
				for(int i=0; i<size;i++){
					col.setByte(tbl.getByte(subset[i],colIndex),i);
				}
				break;
			}
			case (ColumnTypes.CHAR) : {
				for(int i=0; i<size;i++){
					col.setChar(tbl.getChar(subset[i],colIndex),i);
				}
				break;
			}
			default : {
			}
		}
		return col;


	}
	/* DONT DELETE THIS! every function needs to cut and
		paste this switch

		switch(type){
			case (ColumnTypes.DOUBLE) : {
				break;
			}
			case (ColumnTypes.INTEGER) : {
				break;
			}
			case (ColumnTypes.FLOAT) : {
				break;
			}
			case (ColumnTypes.SHORT) : {
				break;
			}
			case (ColumnTypes.LONG) : {
				break;
			}
			case (ColumnTypes.STRING) : {
				break;
			}
			case (ColumnTypes.CHAR_ARRAY) : {
				break;
			}
			case (ColumnTypes.BYTE_ARRAY) : {
				break;
			}
			case (ColumnTypes.BOOLEAN) : {
				break;
			}
			case (ColumnTypes.OBJECT) : {
				break;
			}
			case (ColumnTypes.BYTE) : {
				break;
			}
			case (ColumnTypes.CHAR) : {
				break;
			}
			default : {
			}
		}
	*/
}
