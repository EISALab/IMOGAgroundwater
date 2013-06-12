package ncsa.d2k.modules.core.datatype.table.basic;

import ncsa.d2k.modules.core.datatype.table.*;
import java.io.*;



/**
 * TableImpl is an implementation of Table where each Column is represented by a
 * subclass of the Column class.  The Table is represented internally as
 * vertical arrays of data (often primitives).
 * <br>
 * This is the first, most obvious, and probably most commonly used implementation
 * of Table.
 * <br>
 */
abstract public class TableImpl extends DefaultMissingValuesTable /*implements Table*/ {

	/** the columns of data. */
    protected Column[] columns = null;

	/** this is the label for the table.*/
	protected String label;

	/** comment for the table. */
	protected String comment;

   /**
    * Create a new Table with zero columns.
    */
    public TableImpl () {
        columns = new Column[0];
    }

    /**
     * Create a new Table with the specified number of columns.  Space for the
     * columns is created, but the columns themselves will be null.
     * @param numColumns the initial number of columns
     */
    public TableImpl (int numColumns) {
		columns = new Column[numColumns];
    }

    /**
     * Create a new Table with the specified columns.
     * @param c the initial columns
     */
    public TableImpl (Column[] c) {
        columns = c;
    }

	/**
	 * Read the serialized object from the data stream.
	 * @param in the data input stream
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
    private void readObject(ObjectInputStream in)
       throws IOException, ClassNotFoundException {
        in.defaultReadObject();
    }

	////////////////////////////
	// access metadata for the table.
	//
	/**
	 * Get the label associated with this Table.
	 * @return the label which describes this Table
	 */
	public String getLabel( ) {
		return label;
	}

	/**
		Set the label associated with this Table.
		@param labl the label which describes this Table
	*/
	public void setLabel( String labl ) {
		label = labl;
	}

	/**
		Get the comment associated with this Table.
		@return the comment which describes this Table
	*/
	public String getComment( ) {
		return comment;
	}

	/**
		Set the comment associated with this Table.
		@param cmt the comment which describes this Table
	*/
	public void setComment( String cmt ) {
		comment = cmt;
	}

    //////////////////////////////////////
    //// ACCESSING Table Fields

   /**
    * Return the number of Columns this table holds.
    * @return the capacity of the number of Columns in table
   */
    public int getNumColumns () {
        return  columns.length;
    }

	/**
	 * Get a Column from the table.
	 * @param pos the position of the Column to get from table
	 * @return the Column at in the table at pos
	 */
	final Column [] getColumns () {
		return columns;
	}
	/**
	 * Get a Column from the table.
	 * @param pos the position of the Column to get from table
	 * @return the Column at in the table at pos
	 */
	public Column getColumn (int pos) {
		return columns[pos];
	}

    /**
     * Set a specified Column in the table.  If a Column exists at this
     * position already, it will be replaced.  If position is beyond
     * the capacity of this table then an ArrayIndexOutOfBoundsException
     * will be thrown.
     * @param newColumn the Column to be set in the table
     * @param pos the postion of the Column to be set in the table
     */
    void setColumn (Column newColumn, int pos) {
        columns[pos] = newColumn;
    }

    /**
     * Returns the label associated with the Column at the indicated position.
     * @param pos the index of the Column name to get.
     * @returns the name associated with the Column.
     */
    public String getColumnLabel (int pos) {
        String colLabel = columns[pos].getLabel();
        if (colLabel == null)
            return  "column_" + pos;
        else
            return  colLabel;
    }

    /**
     * Set the name associated with a Column.
     * @param label the new column label
     * @param pos the index of the Column to set
     */
    public void setColumnLabel (String label, int pos) {
        columns[pos].setLabel(label);
    }

    /**
     * Returns the comment associated with the Column at the indicated position.
     * @param pos the index of the Column name to get.
     * @returns the comment associated with the Column.
     */
    public String getColumnComment (int pos) {
      return columns[pos].getComment();
    }

    /**
     * Set the comment associated with a column.
     * @param label the new column comment
     * @param pos the index of the column to set
     */
    public void setColumnComment (String label, int pos) {
        columns[pos].setComment(label);
    }

    //////////////////////////////////////
	// getters.
	//
    /**
     * Get an Object from the Table.
     * @param row the position of the row to find the element
     * @param column the column of row to be returned
     * @return the Object in the Table at (row, column)
     */
    public Object getObject (int row, int column) {
        return  columns[column].getRow(row);
    }

    /**
     * Get an int from the Table.
     * @param row the position of the row to find the element
     * @param column the column of row to be returned
     * @return the int in the Table at (row, column)
     */
    public int getInt (int row, int column) {
        return  columns[column].getInt(row);
    }

    /**
     * Get a short from the Table.
     * @param row the position of the row to find the element
     * @param column the column of row to be returned
     * @return the short in the Table at (row, column)
     */
    public short getShort (int row, int column) {
        return  columns[column].getShort(row);
    }

    /**
     * Get a long from the Table.
     * @param row the position of the row to find the element
     * @param column the column of row to be returned
     * @return the long in the Table at (row, column)
     */
    public long getLong (int row, int column) {
        return  columns[column].getLong(row);
    }

    /**
     * Get a float from the Table.
     * @param row the position of the row to find the element
     * @param column the column of row to be returned
     * @return the float in the Table at (row, column)
     */
    public float getFloat (int row, int column) {
        return  columns[column].getFloat(row);
    }

    /**
     * Get a double from the Table.
     * @param row the position of the row to find the element
     * @param column the column of row to be returned
     * @return the float in the Table at (row, column)
     */
    public double getDouble (int row, int column) {
        return  columns[column].getDouble(row);
    }

    /**
     * Get a String from the Table.
     * @param row the position of the row to find the element
     * @param column the column of row to be returned
     * @return the String in the Table at (row, column)
     */
    public String getString (int row, int column) {
        return  columns[column].getString(row);
    }

    /**
     * Get the bytes from the Table.
     * @param row the position of the row to find the element
     * @param column the column of row to be returned
     * @return the bytes in the Table at (row, column)
     */
    public byte[] getBytes (int row, int column) {
        return  columns[column].getBytes(row);
    }

    /**
     * Get a byte from the Table.
     * @param row the position of the row to find the element
     * @param column the column of row to be returned
     * @return the byte in the Table at (row, column)
     */
    public byte getByte (int row, int column) {
        return  columns[column].getByte(row);
    }

    /**
     * Get a char[] from the Table.
     * @param row the position of the row to find the element
     * @param column the column of row to be returned
     * @return the chars in the Table at (row, column)
     */
    public char[] getChars (int row, int column) {
        return  columns[column].getChars(row);
    }

    /**
     * Get a char from the Table.
     * @param row the position of the row to find the element
     * @param column the column of row to be returned
     * @return the chars in the Table at (row, column)
     */
    public char getChar (int row, int column) {
        return  columns[column].getChar(row);
    }

    /**
     * Get a boolean from the Table.
     * @param row the position of the row to find the element
     * @param column the column of row to be returned
     * @return the boolean in the Table at (row, column)
     */
    public boolean getBoolean (int row, int column) {
        return  columns[column].getBoolean(row);
    }

    //////////////////////////////////////
    // Accessing Table Metadata
    //

    /**
     * Get the number of rows this Table holds.
     * @return this Table's number of rows
     */
    public int getNumRows () {
        if (columns.length < 1)
            return  0;
        return  columns[0].getNumRows();
    }

    /**
     * Sets the reference to the internal representation of this Table.
     * @param newColumns a new internal representation for this Table
     */
	public void setColumns (Column[] newColumns) {
       columns = newColumns;
    }

   /**
    * Swap the positions of two rows.
    * @param pos1 the first row to swap
    * @param pos2 the second row to swap
    */
    public void swapRows (int pos1, int pos2) {
        for (int i = 0; i < columns.length; i++) {
            Object Obj1 = columns[i].getRow(pos1);
            columns[i].setRow(columns[i].getRow(pos2), pos1);
            columns[i].setRow(Obj1, pos2);

            // swap missing values.
            boolean missing1 = columns[i].isValueMissing(pos1);
            boolean missing2 = columns[i].isValueMissing(pos2);
            columns[i].setValueToMissing(missing2, pos1);
            columns[i].setValueToMissing(missing1, pos2);

        }
    }

   /**
    * Swap the positions of two Columns.
    * @param pos1 the first column to swap
    * @param pos2 the second column to swap
    */
    public void swapColumns (int pos1, int pos2) {
        Column temp = getColumn(pos1);
        setColumn(getColumn(pos2), pos1);
        setColumn(temp, pos2);
    }

	/**
	 * Returns the contents of this table in an instance of an example table.
	 * @return an example table for the data in this table.
	 */
	public ExampleTable toExampleTable() {
		return new ExampleTableImpl(this);
	}

    //////////////////////////////////////
    /**
     * Will print the contents of this Table to standard out.
     * Each row of the Table will be printed to a separate line
     * of standard out.
     * <br>
     * This method assumes there is a proper implementation of
     * getString for every underlying column.  (note: this should never
     * be a problem, as any implmentation of Column should be able to
     * support a String rep)
     */
    public void print () {
        int rows = this.getNumRows();
        int cols = this.getNumColumns();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++)
                System.out.print(this.getString(r, c) + ", ");
            System.out.println(" ");
        }
    }

	public boolean isColumnNominal(int index) {
	  return getColumn(index).getIsNominal();
	}

	public boolean isColumnScalar(int index) {
	  return getColumn(index).getIsScalar();
	}

	public void setColumnIsNominal(boolean value, int index) {
	  getColumn(index).setIsNominal(value);
	}

	public void setColumnIsScalar(boolean value, int index) {
	  getColumn(index).setIsScalar(value);
	}

	public boolean isColumnNumeric(int position) {
	  if(getColumn(position) instanceof NumericColumn)
		 return true;

	  Column col = getColumn(position);
	  int numRows = col.getNumRows();
		for(int row = 0; row < numRows; row++) {
			try {
				Double d = Double.valueOf(col.getString(row));
			}
			catch(Exception e) {
				return false;
			}
		}
		return true;
	}

	public int getColumnType(int position) {
		return getColumn(position).getType();
	}

	public boolean isValueMissing(int row, int col) {
		return getColumn(col).isValueMissing(row);
	}
	public boolean isValueEmpty(int row, int col) {
		return getColumn(col).isValueEmpty(row);
	}

	public void setValueToMissing(boolean b, int row, int col) {
		getColumn(col).setValueToMissing(b, row);
	}

	public void setValueToEmpty(boolean b, int row, int col) {
		getColumn(col).setValueToEmpty(b, row);
	}

//		ANCA: method for comparing two Table objects.
		  // Could be more efficient but as is used only in Junit tests,
		  // less code is more important than speed of execution.
		  // should also compare missing and empty arrays for columns or use column.equals
		public boolean equals(Object tbl) {
						Table table;
						try {
								table = (Table) tbl;
						} catch (Exception e) {
								return false;
						}

				if(getNumRows() != table.getNumRows()) return false;
				if(getNumColumns() != table.getNumColumns()) return false;
								for (int i =0; i < getNumRows(); i ++) {
												for (int j =0; j < getNumColumns(); j ++)
														if(!getObject(i,j).equals(table.getObject(i,j))) return false;
														}
								return true;

						}



	/**
	 * Return true if any value in this Table is missing.
	 * @return true if there are any missing values, false if there are no missing values
	 */
	public boolean hasMissingValues() {
		for(int i = 0; i < getNumColumns(); i++)
			for(int j = 0; j < getNumRows(); j++)
				if(isValueMissing(j, i))
					return true;
		return false;
	}

	/**
	 * Return true if any value in this Table is missing.
	 * @param columnIndex, the index of the column to search for missing values.
	 * @return true if there are any missing values, false if there are no missing values
	 */
	public boolean hasMissingValues(int columnIndex) {
		for(int j = 0; j < getNumRows(); j++)
			if(isValueMissing(j, columnIndex))
				return true;
		return false;
	}
}
