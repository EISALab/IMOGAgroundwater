package ncsa.d2k.modules.core.transform.table;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import java.util.*;
public class AppendTables extends ncsa.d2k.core.modules.DataPrepModule {
    //begin setting Properties

    /**
     * returns information about the input at the given index.
     * @return information about the input at the given index.
     */
    public String getInputInfo(int index) {
        switch (index) {
            case 0 :
                return "<p>First of two tables that will be combined.</p>";
            case 1 :
                return "<p>Second of two tables that will be combined.</p>";
            default :
                return "No such input";
        }
    }

    /**
     * returns information about the output at the given index.
     * @return information about the output at the given index.
     */
    public String getInputName(int index) {
        switch (index) {
            case 0 :
                return "First Table";
            case 1 :
                return "Second Table";
            default :
                return "No such input";
        }
    }

    /**
     * returns string array containing the datatypes of the inputs.
     * @return string array containing the datatypes of the inputs.
     */
    public String[] getInputTypes() {
        String[] types = { "ncsa.d2k.modules.core.datatype.table.Table", "ncsa.d2k.modules.core.datatype.table.Table" };
        return types;
    }

    /**
     * returns information about the output at the given index.
     * @return information about the output at the given index.
     */
    public String getOutputInfo(int index) {
        switch (index) {
            case 0 :
                return "<p>Newly created table containing contents of both original tables.</p>";
            default :
                return "No such output";
        }
    }

    /**
     * returns information about the output at the given index.
     * @return information about the output at the given index.
     */
    public String getOutputName(int index) {
        switch (index) {
            case 0 :
                return "Result Table";
            default :
                return "No such output";
        }
    }

    /**
     * returns string array containing the datatypes of the outputs.
     * @return string array containing the datatypes of the outputs.
     */
    public String[] getOutputTypes() {
        String[] types = { "ncsa.d2k.modules.core.datatype.table.Table" };
        return types;
    }

    /**
     * returns the information about the module.
     * @return the information about the module.
     */
    public String getModuleInfo() {
        String s = "<p>Overview: " + "This module takes two tables and creates a new table containing the contents of both. ";

        s += "</p><p>Detailed Description: "
            + "This module combines two tables to create a single table that contains all of the data in the "
            + "original tables.  The data from the <i>First Table</i> appears first in the new table, "
            + "with the data from the <i>Second Table</i> appended to it.  The number of rows in the "
            + "<i>Result</i> table equals the number of rows in the <i>First Table</i> plus the number of "
            + "rows in the <i>Second Table</i>. "
            + "</p><p> "
            + "When an attribute name (column label) is common across the two input tables, a single "
            + "column with that name is created in the result table. "
            + "Columns are also created in the result table for attributes that appear in the "
            + "<i>First Table</i> or in the <i>Second Table</i> but not in both. "
            + "When the result table is populated, rows for table one are inserted first.  For these rows, "
            + "data values in columns that appear in the result table but not in the first table are set "
            + "to the <i>String</i>, <i>Boolean</i>, or <i>Numeric Filler</i> values specified "
            + "in the module properties.  Rows from table two follow, and again filler values are used "
            + "in result table columns that do not appear in the original table. ";

        s += "</p><p>Data Type Restrictions: "
            + "If input table columns have the same name but different data types, the data type "
            + "from the <i>First Table</i> is used in the result table, and an attempt is made to "
            + "convert the data values from the <i>Second Table</i> to that type. "
            + "This conversion may result in unexpected values in the output table. In some cases, "
            + "such as when a string cannot be converted to a numeric, an exception will be raised. "
            + "The user is discouraged from appending tables containing attributes with the same name "
            + "whose types differ.  For some conversions, for example when an integer is converted "
            + "to a double, there may be no loss of data, but the user should verify the result table "
            + "has the expected values. ";

        s += "</p><p>Data Handling: " + "This module does not modify either of the original tables. ";

        s += "</p><p>Scalability: This module performs its operations on a table that is of "
            + "the same type as the <i>First Table</i>.  The scalability of the module "
            + "therefore depends on the scalability properties of that Table implementation. "
            + "The module requires that the data for a complete column must be able to fit "
            + "into memory, regardless of the Table implementation being used. The module is "
            + "optimized for Tables whose underlying implementation is column-based. </p>";

        return s;
    }

    /**
     * Return the human readable name of the module.
     * @return the human readable name of the module.
     */
    public String getModuleName() {
        return "Append Tables";
    }

   /**
     * Copy the missing value flags from the source column to the destination.
     * @param tbl the table containing the column to copy missing values from.
     * @param sourceIndex the index of the column to copy missing values from.
     * @param dest the column to copy missing values to.
     * @param start the starting location for the copy in the destination column
     * @param type the datatype of the resulting column.
     * @param count the number of missings to copy.
     */
    private void copyMissingValues(Table tbl, int sourceIndex, Column dest, int start, int type, int count) {
        if (sourceIndex == -1) {

            // if there is no source column, verything is missing.
            int total = count+start;
            for (int i = start; i < total; i++) {
                switch (type) {
                    case ColumnTypes.LONG :
                    case ColumnTypes.INTEGER :
                    case ColumnTypes.SHORT :
                        dest.setInt(tbl.getMissingInt(), i);
                        break;
                    case ColumnTypes.DOUBLE :
                    case ColumnTypes.FLOAT :
                        dest.setDouble(tbl.getMissingDouble(), i);
                        break;
                    case ColumnTypes.BOOLEAN :
                        dest.setBoolean(tbl.getMissingBoolean(), i);
                        break;
                    case ColumnTypes.STRING :
                        dest.setString(tbl.getMissingString(), i);
                        break;
                    case ColumnTypes.CHAR_ARRAY :
                        dest.setChars(tbl.getMissingChars(), i);
                        break;
                    case ColumnTypes.BYTE_ARRAY :
                        dest.setBytes(tbl.getMissingBytes(), i);
                        break;
                    case ColumnTypes.CHAR :
                        dest.setChar(tbl.getMissingChar(), i);
                        break;
                    case ColumnTypes.BYTE :
                        dest.setByte(tbl.getMissingByte(), i);
                        break;
                }
                dest.setValueToMissing(true, i);
            }
        } else
            for (int i = 0; i < count; i++)
                dest.setValueToMissing(tbl.isValueMissing(i, sourceIndex), i + start);
    }

    /**
     * append table one to table two.
     * @throws Exception
     */
    public void doit() throws Exception {
        Table t1 = (Table) this.pullInput(0);
        Table t2 = (Table) this.pullInput(1);

        // Hash the column names of the second table.
        HashMap colMap2 = new HashMap();
        for (int i = 0; i < t2.getNumColumns(); i++) {
            colMap2.put(t2.getColumnLabel(i), new Integer(i));
        }

        // Now, create the new table of same type as table 1 and popluate it
        // column by column, starting with the columns in table 1.  After all the
        // columns labelled in table 1 are in the result table, work on any remaining
        // columns from table 2.
        // Note:  This implementation is designed to speed up operations for tables whose
        // underlying implemenation is column based, but may be poor choice for those
        // that have other implementations (row-based for example).   Since most
        // D2K are column-based, good choice for now.

        MutableTable result = (MutableTable) t1.createTable();

        int t1Size = t1.getNumRows();
        int t2Size = t2.getNumRows();
        int combinedSize = t1Size + t2Size;

        int resultCol = 0;
        int t1numColumns = t1.getNumColumns();
        int t2numColumns = t2.getNumColumns();
        String label = null;

        try {

            //for each column in first table
            for (int i = 0; i < t1numColumns; i++) {
                int t2Col = -1;
                //removing common labels fro mthe labels map of second table.
                label = t1.getColumnLabel(i);
                Integer tmp = (Integer) colMap2.get(label);
                if (tmp != null) { // col in both
                    t2Col = tmp.intValue();
                    colMap2.remove(label);
                }

                // set the column values from table one.
                int row = 0;
                switch (t1.getColumnType(i)) {
                    case ColumnTypes.INTEGER :
                        {

                            int[] vals = new int[combinedSize];

                            // get the data from table 1, put it first.
                            for (int x = 0 ; x < t1Size ; x++) vals[x] = t1.getInt(x,i);

                            // get the data from table 2, append it
                            if (t2Col != -1) {
                                for (int x = 0 ; x < t2Size ; x++) vals[x+t1Size] = t2.getInt(x,t2Col);
                                // or, append filler values for table 2 entries
                            } else {
                                int[] s2 = new int[t2Size];
                                for (int j = 0; j < t2Size; j++) {
                                    s2[j] = t1.getMissingInt(); //this.getFillerNumeric();
                                }
                                System.arraycopy(s2, 0, vals, t1Size, t2Size);
                            }

                            // add the column
                            Column col = new IntColumn(combinedSize);
                            this.copyMissingValues(t1, i, col, 0,ColumnTypes.INTEGER, t1.getNumRows());
                            if (t2Col != -1)
                                this.copyMissingValues(t2, t2Col, col, t1Size, ColumnTypes.INTEGER, t2.getNumRows());
                            else
                                this.copyMissingValues(t1, -1, col, t1Size, ColumnTypes.INTEGER, t2.getNumRows());
                            result.addColumn(col);
                            int addedIndex = result.getNumColumns() - 1;
                            for (int index = 0 ; index < vals.length ; index++)
                                result.setInt(vals[index], index, addedIndex);
                            break;
                        }
                    case ColumnTypes.FLOAT :
                        {

                            float[] vals = new float[combinedSize];

                            // get the data from table 1, put it first.
                            for (int x = 0 ; x < t1Size ; x++) vals[x] = t2.getFloat(x,t2Col);

                            // get the data from table 2, append it
                            if (t2Col != -1) {
                                for (int x = 0 ; x < t2Size ; x++) vals[x+t1Size] = t2.getFloat(x,t2Col);
                                // or, append filler values for table 2 entries
                            } else {
                                float[] s2 = new float[t2Size];
                                for (int j = 0; j < t2Size; j++) {
                                    s2[j] = (float) t1.getMissingDouble(); //(float) this.getFillerNumeric();
                                }
                                System.arraycopy(s2, 0, vals, t1Size, t2Size);
                            }

                            // add the column
                            Column col = new FloatColumn(combinedSize);
                            this.copyMissingValues(t1, i, col, 0, ColumnTypes.FLOAT, t1.getNumRows());
                            if (t2Col != -1)
                                this.copyMissingValues(t2, t2Col, col, t1Size, ColumnTypes.FLOAT, t2.getNumRows());
                            else
                                this.copyMissingValues(t1, -1, col, t1Size, ColumnTypes.FLOAT, t2.getNumRows());
                            result.addColumn(col);
                            int addedIndex = result.getNumColumns() - 1;
                            for (int index = 0 ; index < vals.length ; index++)
                                result.setFloat(vals[index], index, addedIndex);
                            break;
                        }
                    case ColumnTypes.DOUBLE :
                        {

                            double[] vals = new double[combinedSize];

                            // get the data from table 1, put it first.
                            for (int x = 0 ; x < t1Size ; x++)
                                vals[x] = t1.getDouble(x,i);

                            // get the data from table 2, append it
                            if (t2Col != -1) {
                                for (int x = 0 ; x < t2Size ; x++) {
                                    double psl = t2.getDouble(x, t2Col);
                                    vals[x+t1Size] = psl;
                                }
                                // or, append filler values for table 2 entries
                            } else {
                                double[] s2 = new double[t2Size];
                                for (int j = 0; j < t2Size; j++) {
                                    s2[j] = t1.getMissingDouble(); //(double) this.getFillerNumeric();
                                }
                                System.arraycopy(s2, 0, vals, t1Size, t2Size);
                            }

                            // add the column
                            Column col = new DoubleColumn(combinedSize);
                            this.copyMissingValues(t1, i, col, 0, ColumnTypes.DOUBLE, t1.getNumRows());
                            if (t2Col != -1)
                                this.copyMissingValues(t2, t2Col, col, t1Size, ColumnTypes.DOUBLE, t2.getNumRows());
                            else
                                this.copyMissingValues(t1, -1, col, t1Size, ColumnTypes.DOUBLE, t2.getNumRows());
                            result.addColumn(col);
                            int addedIndex = result.getNumColumns() - 1;
                            for (int index = 0 ; index < vals.length ; index++)
                                result.setDouble(vals[index], index, addedIndex);
                            break;
                        }
                    case ColumnTypes.SHORT :
                        {

                            short[] vals = new short[combinedSize];

                            // get the data from table 1, put it first.
                            for (int x = 0 ; x < t1Size ; x++) vals[x] = t1.getShort(x,i);

                            // get the data from table 2, append it
                            if (t2Col != -1) {
                                for (int x = 0 ; x < t2Size ; x++) vals[x+t1Size] = t2.getShort(x,t2Col);
                                // or, append filler values for table 2 entries
                            } else {
                                short[] s2 = new short[t2Size];
                                for (int j = 0; j < t2Size; j++) {
                                    s2[j] = (short) t1.getMissingInt(); // this.getFillerNumeric();
                                }
                                System.arraycopy(s2, 0, vals, t1Size, t2Size);
                            }

                            // add the column
                            Column col = new ShortColumn(combinedSize);
                            this.copyMissingValues(t1, i, col, 0, ColumnTypes.SHORT, t1.getNumRows());
                            if (t2Col != -1)
                                this.copyMissingValues(t2, t2Col, col, t1Size, ColumnTypes.SHORT, t2.getNumRows());
                            else
                                this.copyMissingValues(t1, -1, col, t1Size, ColumnTypes.SHORT, t2.getNumRows());
                            result.addColumn(col);
                            int addedIndex = result.getNumColumns() - 1;
                            for (int index = 0 ; index < vals.length ; index++)
                                result.setShort(vals[index], index, addedIndex);
                            break;
                        }
                    case ColumnTypes.LONG :
                        {

                            long[] vals = new long[combinedSize];

                            // get the data from table 1, put it first.
                            for (int x = 0 ; x < t1Size ; x++) vals[x] = t1.getLong(x,i);

                            // get the data from table 2, append it
                            if (t2Col != -1) {
                                for (int x = 0 ; x < t2Size ; x++) vals[x+t1Size] = t2.getLong(x,t2Col);
                                // or, append filler values for table 2 entries
                            } else {
                                long[] s2 = new long[t2Size];
                                for (int j = 0; j < t2Size; j++) {
                                    s2[j] = (long) t1.getMissingInt(); //this.getFillerNumeric();
                                }
                                System.arraycopy(s2, 0, vals, t1Size, t2Size);
                            }

                            // add the column
                            Column col = new LongColumn(combinedSize);
                            this.copyMissingValues(t1, i, col, 0, ColumnTypes.LONG, t1.getNumRows());
                            if (t2Col != -1)
                                this.copyMissingValues(t2, t2Col, col, t1Size, ColumnTypes.LONG, t2.getNumRows());
                            else
                                this.copyMissingValues(t1, -1, col, t1Size, ColumnTypes.LONG, t2.getNumRows());
                            result.addColumn(col);
                            int addedIndex = result.getNumColumns() - 1;
                            for (int index = 0 ; index < vals.length ; index++)
                                result.setLong(vals[index], index, addedIndex);
                            break;
                        }

                    case ColumnTypes.STRING :
                        {

                            String[] vals = new String[combinedSize];

                            // get the data from table 1, put it first.
                            for (int x = 0 ; x < t1Size ; x++) vals[x] = t1.getString(x,i);

                            // get the data from table 2, append it
                            if (t2Col != -1) {
                                for (int x = 0 ; x < t2Size ; x++) vals[x+t1Size] = t2.getString(x,t2Col);
                                // or, append filler values for table 2 entries
                            } else {
                                String[] s2 = new String[t2Size];
                                for (int j = 0; j < t2Size; j++) {
                                    s2[j] = t1.getMissingString(); //this.getFillerString();
                                }
                                System.arraycopy(s2, 0, vals, t1Size, t2Size);
                            }

                            // add the column
                            Column col = new StringColumn(combinedSize);
                            this.copyMissingValues(t1, i, col, 0, ColumnTypes.STRING, t1.getNumRows());
                            if (t2Col != -1)
                                this.copyMissingValues(t2, t2Col, col, t1Size, ColumnTypes.STRING, t2.getNumRows());
                            else
                                this.copyMissingValues(t1, -1, col, t1Size, ColumnTypes.STRING, t2.getNumRows());
                            result.addColumn(col);
                            int addedIndex = result.getNumColumns() - 1;
                            for (int index = 0 ; index < vals.length ; index++)
                                result.setString(vals[index], index, addedIndex);
                            break;
                        }
                    case ColumnTypes.CHAR_ARRAY :
                        {

                            char[][] vals = new char[combinedSize][];

                            // get the data from table 1, put it first.
                            for (int x = 0 ; x < t1Size ; x++) vals[x] = t1.getChars(x,i);

                            // get the data from table 2, append it
                            if (t2Col != -1) {
                                for (int x = 0 ; x < t2Size ; x++) vals[x+t1Size] = t2.getChars(x,t2Col);
                                // or, append filler values for table 2 entries
                            } else {
                                char[][] s2 = new char[t2Size][];
                                for (int j = 0; j < t2Size; j++) {
                                    s2[j] = t1.getMissingChars(); //this.getFillerChars();
                                }
                                System.arraycopy(s2, 0, vals, t1Size, t2Size);
                            }

                            // add the column
                           Column col = new CharArrayColumn(combinedSize);
                            this.copyMissingValues(t1, i, col, 0, ColumnTypes.CHAR_ARRAY, t1.getNumRows());
                            if (t2Col != -1)
                                this.copyMissingValues(t2, t2Col, col, t1Size, ColumnTypes.CHAR_ARRAY, t2.getNumRows());
                            else
                                this.copyMissingValues(t1, -1, col, t1Size, ColumnTypes.CHAR_ARRAY, t2.getNumRows());
                            result.addColumn(col);
                            int addedIndex = result.getNumColumns() - 1;
                            for (int index = 0 ; index < vals.length ; index++)
                                result.setChars(vals[index], index, addedIndex);
                            break;
                        }
                    case ColumnTypes.BYTE_ARRAY :
                        {

                            byte[][] vals = new byte[combinedSize][];

                            // get the data from table 1, put it first.
                            for (int x = 0 ; x < t1Size ; x++) vals[x] = t1.getBytes(x,i);

                            // get the data from table 2, append it
                            if (t2Col != -1) {
                                for (int x = 0 ; x < t2Size ; x++) vals[x+t1Size] = t2.getBytes(x,t2Col);
                                // or, append filler values for table 2 entries
                            } else {
                                byte[][] s2 = new byte[t2Size][];
                                for (int j = 0; j < t2Size; j++) {
                                    s2[j] = t1.getMissingBytes(); //this.getFillerBytes();
                                }
                                System.arraycopy(s2, 0, vals, t1Size, t2Size);
                            }

                            // add the column
                            Column col = new ByteArrayColumn(combinedSize);
                            this.copyMissingValues(t1, i, col, 0, ColumnTypes.BYTE_ARRAY, t1.getNumRows());
                            if (t2Col != -1)
                                this.copyMissingValues(t2, t2Col, col, t1Size, ColumnTypes.BYTE_ARRAY, t2.getNumRows());
                            else
                                this.copyMissingValues(t1, -1, col, t1Size, ColumnTypes.BYTE_ARRAY, t2.getNumRows());
                            result.addColumn(col);
                            int addedIndex = result.getNumColumns() - 1;
                            for (int index = 0 ; index < vals.length ; index++)
                                result.setBytes(vals[index], index, addedIndex);
                            break;
                        }
                    case ColumnTypes.BOOLEAN :
                        {

                            boolean[] vals = new boolean[combinedSize];

                            // get the data from table 1, put it first.
                            for (int x = 0 ; x < t1Size ; x++) vals[x] = t1.getBoolean(x,i);

                            // get the data from table 2, append it
                            if (t2Col != -1) {
                                for (int x = 0 ; x < t2Size ; x++) vals[x+t1Size] = t2.getBoolean(x,t2Col);
                                // or, append filler values for table 2 entries
                            } else {
                                boolean[] s2 = new boolean[t2Size];
                                for (int j = 0; j < t2Size; j++) {
                                    s2[j] = t1.getMissingBoolean(); //this.getFillerBool();
                                }
                                System.arraycopy(s2, 0, vals, t1Size, t2Size);
                            }

                            // add the column
                            Column col = new BooleanColumn(combinedSize);
                            this.copyMissingValues(t1, i, col, 0, ColumnTypes.BOOLEAN, t1.getNumRows());
                            if (t2Col != -1)
                                this.copyMissingValues(t2, t2Col, col, t1Size, ColumnTypes.BOOLEAN, t2.getNumRows());
                            else
                                this.copyMissingValues(t1, -1, col, t1Size, ColumnTypes.BOOLEAN, t2.getNumRows());
                            result.addColumn(col);
                            int addedIndex = result.getNumColumns() - 1;
                            for (int index = 0 ; index < vals.length ; index++)
                                result.setBoolean(vals[index], index, addedIndex);
                            break;
                        }
                    case ColumnTypes.OBJECT :
                        {

                            Object[] vals = new Object[combinedSize];

                            // get the data from table 1, put it first.
                            for (int x = 0 ; x < t1Size ; x++) vals[x] = t1.getObject(x,i);

                            // get the data from table 2, append it
                            if (t2Col != -1) {
                                for (int x = 0 ; x < t2Size ; x++) vals[x+t1Size] = t2.getObject(x,t2Col);
                                // or, append filler values for table 2 entries
                            } else {
                                Object[] s2 = new Object[t2Size];
                                for (int j = 0; j < t2Size; j++) {
                                    s2[j] = null;
                                }
                                System.arraycopy(s2, 0, vals, t1Size, t2Size);
                            }

                            // add the column
                            Column col = new ObjectColumn(combinedSize);
                            this.copyMissingValues(t1, i, col, 0, ColumnTypes.OBJECT, t1.getNumRows());
                            if (t2Col != -1)
                                this.copyMissingValues(t2, t2Col, col, t1Size, ColumnTypes.OBJECT, t2.getNumRows());
                            else
                                this.copyMissingValues(t1, -1, col, t1Size, ColumnTypes.OBJECT, t2.getNumRows());
                            result.addColumn(col);
                            int addedIndex = result.getNumColumns() - 1;
                            for (int index = 0 ; index < vals.length ; index++)
                                result.setObject(vals[index], index, addedIndex);
                            break;
                        }
                    case ColumnTypes.BYTE :
                        {

                            byte[] vals = new byte[combinedSize];

                            // get the data from table 1, put it first.
                            for (int x = 0 ; x < t1Size ; x++) vals[x] = t1.getByte(x,i);

                            // get the data from table 2, append it
                            if (t2Col != -1) {
                                for (int x = 0 ; x < t2Size ; x++) vals[x+t1Size] = t2.getByte(x,t2Col);
                                // or, append filler values for table 2 entries
                            } else {
                                byte[] s2 = new byte[t2Size];
                                for (int j = 0; j < t2Size; j++) {
                                    s2[j] = t1.getMissingByte(); //this.getFillerByte();
                                }
                                System.arraycopy(s2, 0, vals, t1Size, t2Size);
                            }

                            // add the column
                            Column col = new ByteColumn(combinedSize);
                            this.copyMissingValues(t1, i, col, 0, ColumnTypes.BYTE, t1.getNumRows());
                            if (t2Col != -1)
                                this.copyMissingValues(t2, t2Col, col, t1Size, ColumnTypes.BYTE, t2.getNumRows());
                            else
                                this.copyMissingValues(t1, -1, col, t1Size, ColumnTypes.BYTE, t2.getNumRows());
                            result.addColumn(col);
                            int addedIndex = result.getNumColumns() - 1;
                            for (int index = 0 ; index < vals.length ; index++)
                                result.setByte(vals[index], index, addedIndex);
                            break;
                        }

                    case ColumnTypes.CHAR :
                        {

                            char[] vals = new char[combinedSize];

                            // get the data from table 1, put it first.
                            for (int x = 0 ; x < t1Size ; x++) vals[x] = t1.getChar(x,i);

                            // get the data from table 2, append it
                            if (t2Col != -1) {
                                // get the data from table 1, put it first.
                                for (int x = 0 ; x < t2Size ; x++) vals[x+t1Size] = t2.getChar(x,t2Col);
                                // or, append filler values for table 2 entries
                            } else {
                                char[] s2 = new char[t2Size];
                                for (int j = 0; j < t2Size; j++) {
                                    s2[j] = t1.getMissingChar(); //this.getFillerChar();
                                }
                                System.arraycopy(s2, 0, vals, t1Size, t2Size);
                            }

                            // add the column
                            Column col = new CharColumn(combinedSize);
                            this.copyMissingValues(t1, i, col, 0, ColumnTypes.CHAR, t1.getNumRows());
                            if (t2Col != -1)
                                this.copyMissingValues(t2, t2Col, col, t1Size, ColumnTypes.CHAR, t2.getNumRows());
                            else
                                this.copyMissingValues(t1, -1, col, t1Size, ColumnTypes.CHAR, t2.getNumRows());
                            result.addColumn(col);
                            int addedIndex = result.getNumColumns() - 1;
                            for (int index = 0 ; index < vals.length ; index++)
                                result.setChar(vals[index], index, addedIndex);
                            break;
                        }
                    default :
                        {
                            throw new Exception(
                                "Datatype for Table 1 column named '" + label + "' was not recognized when appending tables.");
                        }
                }
                result.setColumnLabel(label, resultCol++);
            }
        } catch (NumberFormatException e) {
            throw new NumberFormatException(
                getAlias() + ": Unable to convert data for Second Table column \"" + label + "\" to numeric type" + "\n" + e);
        }

        // Now for any columns in table 2 that are not yet accounted for,
        // fill them in for table 1 rows and add the data from table 2 at the end.
        // Retain their original order in the result table.

        tbl2cols : for (int i = 0; i < t2numColumns; i++) {

            label = t2.getColumnLabel(i);
            if (colMap2.get(label) == null) {
                // col already processed so go to next one
                continue tbl2cols;
            }
            Column col;
            switch (t2.getColumnType(i)) {

                case ColumnTypes.INTEGER :
                    {
                        int[] vals = new int[combinedSize];

                        // use filler values for table 1 entries
                        for (int j = 0; j < t1Size; j++) {
                            vals[j] = t1.getMissingInt();
                        }

                        // Read the data from the second table.
                        for (int j = 0, newRow = t1Size ; j < t2Size; j++, newRow++)
                            vals[newRow] = t2.getInt(j, i);

                        // add the column
                        col = new IntColumn(vals);
                        break;
                    }

                case ColumnTypes.FLOAT :
                    {
                        float[] vals = new float[combinedSize];

                        // use filler values for table 1 entries
                        for (int j = 0; j < t1Size; j++) {
                            vals[j] = (float) t1.getMissingDouble();
                        }

                        // Read the data from the second table.
                        for (int j = 0, newRow = t1Size ; j < t2Size; j++, newRow++)
                            vals[newRow] = t2.getFloat(j, i);

                        // add the column
                        col = new FloatColumn(vals);
                        break;
                    }
                case ColumnTypes.DOUBLE :
                    {

                        double[] vals = new double[combinedSize];

                        // use filler values for table 1 entries
                        for (int j = 0; j < t1Size; j++) {
                            vals[j] = t1.getMissingDouble();
                        }

                        // Read the data from the second table.
                        for (int j = 0, newRow = t1Size ; j < t2Size; j++, newRow++)
                            vals[newRow] = t2.getDouble(j, i);

                        // add the column
                        col = new DoubleColumn(vals);
                        break;
                    }
                case ColumnTypes.SHORT :
                    {

                        short[] vals = new short[combinedSize];

                        // use filler values for table 1 entries
                        for (int j = 0; j < t1Size; j++) {
                            vals[j] = (short) t1.getMissingInt();
                        }

                        // Read the data from the second table.
                        for (int j = 0, newRow = t1Size ; j < t2Size; j++, newRow++)
                            vals[newRow] = t2.getShort(j, i);

                        // add the column
                       col = new ShortColumn(vals);
                        break;
                    }
                case ColumnTypes.LONG :
                    {

                        long[] vals = new long[combinedSize];

                        // use filler values for table 1 entries
                        for (int j = 0; j < t1Size; j++) {
                            vals[j] = t1.getMissingInt();
                        }

                        // Read the data from the second table.
                        for (int j = 0, newRow = t1Size ; j < t2Size; j++, newRow++)
                            vals[newRow] = t2.getLong(j, i);

                        // add the column
                        col = new LongColumn(vals);
                        break;
                    }
                case ColumnTypes.STRING :
                    {

                        String[] vals = new String[combinedSize];

                        // use filler values for table 1 entries
                        for (int j = 0; j < t1Size; j++) {
                            vals[j] = t1.getMissingString();
                        }

                        // Read the data from the second table.
                        for (int j = 0, newRow = t1Size ; j < t2Size; j++, newRow++)
                            vals[newRow] = t2.getString(j, i);

                        // add the column
                        col = new StringColumn(vals);
                        break;
                    }
                case ColumnTypes.CHAR_ARRAY :
                    {

                        char[][] vals = new char[combinedSize][];

                        // use filler values for table 1 entries
                        for (int j = 0; j < t1Size; j++) {
                            vals[j] = t1.getMissingString().toCharArray();
                        }

                        // Read the data from the second table.
                        for (int j = 0, newRow = t1Size ; j < t2Size; j++, newRow++)
                            vals[newRow] = t2.getChars(j, i);

                        // add the column
                        col = new CharArrayColumn(vals);
                        break;
                    }

                case ColumnTypes.BYTE_ARRAY :
                    {

                        byte[][] vals = new byte[combinedSize][];

                        // use filler values for table 1 entries
                        for (int j = 0; j < t1Size; j++) {
                           vals[j] = t1.getMissingString().getBytes();
                        }

                        // Read the data from the second table.
                        for (int j = 0, newRow = t1Size ; j < t2Size; j++, newRow++)
                            vals[newRow] = t2.getBytes(j, i);

                        // add the column
                        col = new ByteArrayColumn(vals);
                        break;
                    }
                case ColumnTypes.BOOLEAN :
                    {

                        boolean[] vals = new boolean[combinedSize];

                        // use filler values for table 1 entries
                        for (int j = 0; j < t1Size; j++) {
                            vals[j] = t1.getMissingBoolean();
                        }

                        // Read the data from the second table.
                        for (int j = 0, newRow = t1Size ; j < t2Size; j++, newRow++)
                            vals[newRow] = t2.getBoolean(j, i);

                        // add the column
                        col = new BooleanColumn(vals);
                        break;
                    }

                case ColumnTypes.OBJECT :
                    {

                        Object[] vals = new Object[combinedSize];

                        // use filler values for table 1 entries
                        for (int j = 0; j < t1Size; j++) {
                            vals[j] = null;
                        }

                        // Read the data from the second table.
                        for (int j = 0, newRow = t1Size ; j < t2Size; j++, newRow++)
                            vals[newRow] = t2.getObject(j, i);

                        // add the column
                        col = new ObjectColumn(vals);
                        break;
                    }
                case ColumnTypes.BYTE :
                    {

                        byte[] vals = new byte[combinedSize];

                        // use filler values for table 1 entries
                        for (int j = 0; j < t1Size; j++) {
                            vals[j] = t1.getMissingByte();
                        }

                        // Read the data from the second table.
                        for (int j = 0, newRow = t1Size ; j < t2Size; j++, newRow++)
                            vals[newRow] = t2.getByte(j, i);

                        // add the column
                        col = new ByteColumn(vals);
                        break;
                    }
                case ColumnTypes.CHAR :
                    {

                        char[] vals = new char[combinedSize];

                        // use filler values for table 1 entries
                       for (int j = 0; j < t1Size; j++) {
                           vals[j] = t1.getMissingChar();
                       }

                       // Read the data from the second table.
                       for (int j = 0, newRow = t1Size ; j < t2Size; j++, newRow++)
                           vals[newRow] = t2.getChar(j, i);

                        // add the column
                        col = new CharColumn(vals);
                        break;
                    }
                default :
                    {
                        throw new Exception(
                            "Datatype for Table 2 column named '" + label + "' was not recognized when appending tables.");
                    }
            } //switch
            this.copyMissingValues(t1, -1, col, 0, t2.getColumnType(i), t1.getNumRows());
            this.copyMissingValues(t2, i, col, t1.getNumRows(), t2.getColumnType(i), t2.getNumRows());
            result.addColumn(col);
            result.setColumnLabel(label, resultCol++);
        }

        // Done!
        this.pushOutput(result, 0);
    }
}

// QA comments
// 3/3/03 - Handed to QA by Tom;  Ruth starts QA
//        - Fixed some typos in documentation
// 3/4/03 - Emailed Tom;  Expect Table1 then Table2 in result - both in rows and columns. And,
//          don't expect order of columns to be shuffled.  Waiting for feedback.
//        - Heard back and okay to change.  Updated description and reordered.
// 3/5/03 - Added exception handling to catch string->numeric conversions that fail.
//        - committed to Basic.
//	  - WISH:  At some point may want implementation that's efficient for Tables that aren't
//          column-oriented.

/**
  QA comments

  10/23/03 - vered started qa process.
     the module throws an exception  when trying to load an itinerary
     with it. - fixed [11-03-03]

 10/24/03 - line 283 throws array index out of bound exception
        because the internal of StringColumn is memory saving
        and alocates a String for each unique value in the column, only.
        need to send a different parameter to arraycopy or replace it by a special
        copying method. fixed.

  10/30/03 - does not handle missing values well. the output table does not
*               "know" which of the values were missing in the original table.
*               this is not being updated using setValueToMissing or any other
*               method. [bug 103] fixed - partially.
* 11/03/03 - preservation of missing values is not done for second table in
*             columns that are unique to this table. [bug 108] fixed.
*
  11/21/03 - missing values are not handled correctly. Any value that can not be gotten
            from one of the two table is considered missing, it is marked as such and the
            default value is gotten from the first input table. The properties specifying
            the values to assign missing values of various types is gone since this
            is now carried with the table.
 12-05-03 - modules is ready for basic 4

 01-11-04 - module is pulled back into qa process
 bug 217 - array index out of bounds exception with subset table that includes
 only part of the original set of data. (fixed)

 01-13-04:
 bug 226: with subset tables - when the second table to be appended is a subset
 table and it has unique columns, the items that are being inserted into the new
 appended table are not coresponding with the subset the second table is made of.
 the items are being copied from the original whole set of the table, until
 reached the number of rows in the subset table. (fixed)

 01-21-04:
 ready for basic 4.

  */
