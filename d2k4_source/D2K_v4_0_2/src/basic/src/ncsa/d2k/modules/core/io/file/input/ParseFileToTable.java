//package ncsa.d2k.modules.projects.clutter.rdr;
package ncsa.d2k.modules.core.io.file.input;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

/**
 * Given a FlatFileReader, create a TableImpl initialized with its contents.
 */
public class ParseFileToTable extends InputModule {

    protected static final char QUESTION = '?';
    protected static final char SPACE = ' ';

    private boolean useBlanks = true;
    public void setUseBlanks(boolean b) {
        useBlanks = b;
    }
    public boolean getUseBlanks() {
        return useBlanks;
    }

    public PropertyDescription [] getPropertiesDescriptions () {
        PropertyDescription[] retVal = new PropertyDescription[1];
        retVal[0] = new PropertyDescription("useBlanks", "Set Blanks to be Missing Values",
            "When true, any blank entries in the file will be set as missing values in the table.");
        return retVal;
    }

    public String[] getInputTypes() {
        String[] in = {"ncsa.d2k.modules.core.io.file.input.FlatFileParser"};
        return in;
    }

    public String[] getOutputTypes() {
        String[] out = {"ncsa.d2k.modules.core.datatype.table.MutableTable"};
        return out;
    }

    public String getInputInfo(int i) {
        return "A FlatFileParser to read data from.";
    }

    public String getOutputInfo(int i) {
        switch(i) {
            case 0:
                return "A Table with the data from the file reader.";
            default:
                return "";
        }
    }

    public String getInputName(int i) {
        return "File Parser";
    }

    public String getOutputName(int i) {
        switch(i) {
            case 0:
                return "Table";
            default:
                return "";
        }
    }

    public String getModuleInfo() {
        // return "Given a FlatFileReader, create a Table initialized with its contents.";
        StringBuffer sb = new StringBuffer("<p>Overview: ");
        sb.append("Given a FlatFileParser, this module creates a Table ");
        sb.append("initialized with the contents of a flat file from disk.");
        sb.append("</p>");
        return sb.toString();
    }

    public String getModuleName() {
        return "Parse File To Table";
    }

    public void doit() throws Exception {
        FlatFileParser fle = (FlatFileParser)pullInput(0);
        Table t = createTable(fle);
        pushOutput(t, 0);
    }

    protected Table createTable(FlatFileParser df) {
        int numRows = df.getNumRows();
        int numColumns = df.getNumColumns();

        boolean hasTypes = false;

        Column[] columns = new Column[numColumns];
        for(int i = 0; i < columns.length; i++) {
            int type = df.getColumnType(i);
            columns[i] = ColumnUtilities.createColumn(type, numRows);
            if(type != -1)
                hasTypes = true;

            // set the label
            String label = df.getColumnLabel(i);
            if(label != null)
                columns[i].setLabel(label);
        }

        MutableTableImpl ti = new MutableTableImpl(columns);
        
         for(int i = 0; i < numRows; i++) {
            ParsedLine pl = df.getRowElements(i);
            char[][] row = pl.elements;
            boolean[] blanks = pl.blanks;
            if(row != null) {
                for(int j = 0; j < columns.length; j++) {
                    boolean isMissing = true;
                    char[] elem = row[j];//(char[])row.get(j);
 					// test to see if this is '?'
					// if it is, this value is missing.
                    for(int k = 0; k < elem.length; k++) {
                        if(elem[k] != QUESTION && elem[k] != SPACE) {
                            isMissing = false;
                            break;
                        }
                    }

                    // if the value was not missing, just put it in the table
                    if(!isMissing && !blanks[j]) {
                        try {
                            ti.setChars(elem, i, j);
                        }
                        // if there was a number format exception, set the value
                        // to 0 and mark it as missing
                        catch(NumberFormatException e) {
                            ti.setChars(Integer.toString(0).toCharArray(), i, j);
                            ti.setValueToMissing(true, i, j);
                        }
                    }
                    // if the value was missing..
                    else {
                        // put 0 in a numeric column and set the value to missing
						ti.setValueToMissing(true, i, j);
                        switch (df.getColumnType(j)) {
                        	case ColumnTypes.INTEGER:
                        	case ColumnTypes.SHORT:
                        	case ColumnTypes.LONG:
                        		ti.setInt(ti.getMissingInt(), i, j);
                        		break;
							case ColumnTypes.DOUBLE:
							case ColumnTypes.FLOAT:
								ti.setDouble(ti.getMissingDouble(), i, j);
								break;
							case ColumnTypes.CHAR_ARRAY:
								ti.setChars(ti.getMissingChars(), i, j);
								break;
							case ColumnTypes.BYTE_ARRAY:
								ti.setBytes(ti.getMissingBytes(), i, j);
								break;
							case ColumnTypes.BYTE:
								ti.setByte(ti.getMissingByte(), i, j);
								break;
							case ColumnTypes.CHAR:
								ti.setChar(ti.getMissingChar(), i, j);
								break;
							case ColumnTypes.STRING:
								ti.setString(ti.getMissingString(), i, j);
								break;
							case ColumnTypes.BOOLEAN:
								ti.setBoolean(ti.getMissingBoolean(), i, j);
								break;
	                     }
                    }
               }
            }
        }

        // if types were not specified, we should try to convert to double columns
        // where appropriate
        if(!hasTypes) {

           // System.out.println("no types");

            // for each column
            for(int i = 0; i < numColumns; i++) {
                boolean isNumeric = true;
                double[] newCol = new double[numRows];

                // for each row
                for(int j = 0; j < numRows; j++) {
                    String s = ti.getString(j, i);

                    if (ti.isValueMissing(j, i) || ti.isValueEmpty(j, i))
                       continue;

                    try {
                        double d = Double.parseDouble(s);
                        newCol[j] = d;
                    }
                    catch(NumberFormatException e) {
                        isNumeric = false;
                    }
                    if(!isNumeric)
                        break;
                }

                if(isNumeric) {
                    DoubleColumn dc = new DoubleColumn(newCol);
                    dc.setLabel(ti.getColumnLabel(i));

                    for (int k = 0; k < ti.getNumRows(); k++) {
                       if (ti.isValueMissing(k, i))
                          dc.setValueToMissing(true, k);
                       if (ti.isValueEmpty(k, i))
                          dc.setValueToEmpty(true, k);
                    }

                    ti.setColumn(dc, i);
                }
            }
        }
        return ti;
    }
}