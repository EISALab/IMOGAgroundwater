package ncsa.d2k.modules.core.io.file.input;

import java.io.*;
import java.util.*;

import ncsa.d2k.modules.core.datatype.table.*;

/**
 * Reads data from a delimited file.  The delimiter is found automatically, or
 * can be set.
 */
public class DelimitedFileParser implements FlatFileParser {

    /** Possible delimiters */
    private static final char TAB = '\t';
    private static final char SPACE = ' ';
    protected static final char COMMA = ',';
    private static final char PIPE = '|';
    private static final char EQUALS = '=';

    /** the file to read from */
    protected File file;
    /** the index, from 0, of the types row */
    private int typesRow;
    /** the index, from 0, of the labels row */
    private int labelsRow;
    /** the index, from 0, of the in out row */
    private int inOutRow;
    /** the index, from 0, of the nominal/scalar row */
    private int nomScalarRow;

    /** the number of data rows in the file (does not include meta rows) */
    protected int numRows;
    /** the number of columns in the file */
    protected int numColumns;

    /** the types of the columns */
    protected int[] columnTypes;
    /** the labels of the columns */
    protected String[] columnLabels;
    /** the data (in/out) types of the columns */
    private int[] dataTypes;
    /** the feature (nom/scalar) types of the columns */
    private int[] featureTypes;

    private int blankRows = 0;

    /** the file reader */
    protected LineNumberReader lineReader;

    /** the delimter for this file */
    protected char delimiter;

    protected DelimitedFileParser() {}

    /**
     * Create a new DelimitedFileReader with no types row, no labels row,
     * no in out row, no nominal scalar row.
     * @param f the file to read
     */
    public DelimitedFileParser(File f) throws Exception {
        this(f, -1, -1, -1, -1);
    }

    /**
     * Create a new DelimitedFileReader with the specified labels row.
     * @param f the file to read
     * @param _labelsRow the index of the labels row
     */
    public DelimitedFileParser(File f, int _labelsRow) throws Exception {
        this(f, _labelsRow, -1, -1, -1);
    }

    /**
     * Create a new DelimitedFileReader with the specified labels and types rows.
     * @param f the file to read
     * @param _labelsRow the index of the labels row
     * @param _typesRow the index of the types row
     */
    public DelimitedFileParser(File f, int _labelsRow, int _typesRow) throws Exception {
        this(f, _labelsRow, _typesRow, -1, -1);
    }

    public DelimitedFileParser(File f, int _labelsRow, int _typesRow, char delim) throws Exception {
        this(f, _labelsRow, _typesRow, -1, -1, delim);
    }


    /**
     * Create a new DelimitedFileReader with the specified labels, types, inout,
     * and nominal/scalar rows.
     * @param f the file to read
     * @param _labelsRow the index of the labels row
     * @param _typesRow the index of the types row
     * @param _inOutRow the index of the in-out row
     * @param _nomScalarRow the index of the nominal-scalar row
     */
    public DelimitedFileParser(File f, int _labelsRow, int _typesRow, int _inOutRow,
                               int _nomScalarRow) throws Exception {
        file = f;
        typesRow = _typesRow;
        labelsRow = _labelsRow;
        inOutRow = _inOutRow;
        nomScalarRow = _nomScalarRow;

        // read through the file to count the number of rows, columns, and find
        // the delimiter
        scanFile();

        lineReader = new LineNumberReader(new FileReader(file));


        // now read in the types, scalar, in out rows, labels
        if(typesRow > -1) {
            numRows--;

            // now parse the line and get the types

            ArrayList row = getLineElements(typesRow);
            if (row != null ) createColumnTypes(row);
            else throw new Exception("Delimited File Parser: types' row number " +
                     typesRow + " does not exist in the file");

        }
        else
            columnTypes = null;
        if(labelsRow > -1) {
            numRows--;

            // now parse the line and the the labels
            ArrayList row = getLineElements(labelsRow);
           if (row != null) createColumnLabels(row);
             else throw new Exception("Delimited File Parser: labels' row number " +
                  labelsRow + " does not exist in the file");

            }
        else
            columnLabels = null;

        lineReader.setLineNumber(0);
    }

    /**
     * Create a new DelimitedFileReader with the specified labels, types, inout,
     * and nominal/scalar rows.
     * @param f the file to read
     * @param _labelsRow the index of the labels row
     * @param _typesRow the index of the types row
     * @param _inOutRow the index of the in-out row
     * @param _nomScalarRow the index of the nominal-scalar row
     */
    public DelimitedFileParser(File f, int _labelsRow, int _typesRow, int _inOutRow,
                               int _nomScalarRow, char delim) throws Exception {
        file = f;
        typesRow = _typesRow;
        labelsRow = _labelsRow;
        inOutRow = _inOutRow;
        nomScalarRow = _nomScalarRow;

        setDelimiter(delim);
        this.scanRowsCols();

        char[] ar = {delim};

        lineReader = new LineNumberReader(new FileReader(file));

        // now read in the types, scalar, in out rows, labels
        if(typesRow > -1) {
            numRows--;

            // now parse the line and get the types
            ArrayList row = getLineElements(typesRow);
            createColumnTypes(row);
        }
        else
            columnTypes = null;
        if(labelsRow > -1) {
            numRows--;

            // now parse the line and the the labels
            ArrayList row = getLineElements(labelsRow);
            createColumnLabels(row);
        }
        else
            columnLabels = null;

        lineReader.setLineNumber(0);
    }


    public void setDelimiter(char d) {
        delimiter = d;
    }

    public char getDelimiter() {
        return delimiter;
    }

    /**
     * Create the columns types.
     */
    private void createColumnTypes(ArrayList row) {
        columnTypes = new int[row.size()];

        for(int i = 0; i < row.size(); i++) {
            char[] ty = (char[])row.get(i);
            String type = new String(ty).trim();
            if(type.equalsIgnoreCase(STRING_TYPE))
                columnTypes[i] = ColumnTypes.STRING;
            else if(type.equalsIgnoreCase(DOUBLE_TYPE))
                columnTypes[i] = ColumnTypes.DOUBLE;
            else if(type.equalsIgnoreCase(INT_TYPE))
                columnTypes[i] = ColumnTypes.INTEGER;
            else if(type.equalsIgnoreCase(FLOAT_TYPE))
                columnTypes[i] = ColumnTypes.FLOAT;
            else if(type.equalsIgnoreCase(SHORT_TYPE))
                columnTypes[i] = ColumnTypes.SHORT;
            else if(type.equalsIgnoreCase(LONG_TYPE))
                columnTypes[i] = ColumnTypes.LONG;
            else if(type.equalsIgnoreCase(BYTE_TYPE))
                columnTypes[i] = ColumnTypes.BYTE;
            else if(type.equalsIgnoreCase(CHAR_TYPE))
                columnTypes[i] = ColumnTypes.CHAR;
            else if(type.equalsIgnoreCase(BYTE_ARRAY_TYPE))
                columnTypes[i] = ColumnTypes.BYTE_ARRAY;
            else if(type.equalsIgnoreCase(CHAR_ARRAY_TYPE))
                columnTypes[i] = ColumnTypes.CHAR_ARRAY;
            else if(type.equalsIgnoreCase(BOOLEAN_TYPE))
                columnTypes[i] = ColumnTypes.BOOLEAN;
			else if(type.equalsIgnoreCase(NOMINAL_TYPE))
				columnTypes[i] = ColumnTypes.NOMINAL;
            else
                columnTypes[i] = ColumnTypes.STRING;
        }
    }

    private void createColumnLabels(ArrayList row) {
        columnLabels = new String[row.size()];
        for(int i = 0; i < row.size(); i++)
            columnLabels[i] = new String((char[])row.get(i));
    }


    /**
     * Get the number of columns.
     * @return the number of columns
     */
    public int getNumColumns() {
        return numColumns;
    }

    /**
     * Get the number of rows
     * @return the number of rows
     */
    public int getNumRows() {
        return numRows;
    }

    /**
     * Get the column type at column i.
     * @return the column type at column i, or -1 if no column types were
     * specified
     */
    public int getColumnType(int i) {
        if(columnTypes == null)
            return -1;
        try {
          return columnTypes[i];
        }
        catch(ArrayIndexOutOfBoundsException e) {
          throw new ArrayIndexOutOfBoundsException("DelimitedFileParser: Types row = "+typesRow+" labels row = "+labelsRow+" - The number of column "+
                                     "types does not match the number of column labels.");
        }
    }

    /**
     * Get the column label at column i.
     * @return the column label at column i, or null if no column labels were
     * specified
     */
    public String getColumnLabel(int i) {
        if(columnLabels == null)
            // change:
            // return null;
            return "column_" + i;
        try {
           // change:
           // return columnLabels[i];
           if (columnLabels[i] != null && columnLabels[i].length() != 0)
              return columnLabels[i];
           else
              return "column_" + i;
        }
        catch(ArrayIndexOutOfBoundsException e) {
          throw new ArrayIndexOutOfBoundsException("DelimitedFileParser: The number of column "+
                                     "labels does not match the number of columns.");
        }
    }

    /**
     * Get the data type (in-out) at column i.
     * @return the data type (in-out) at column i, or -1 if no in-out types were
     * specified
     */
    public int getDataType(int i) {
        if(dataTypes == null)
            return -1;
        return dataTypes[i];
    }

    /**
     * Get the feature type (nominal-scalar) at column i.
     * @return the feature type at column i, or -1 if no nominal-scalar types
     * were specified.
     */
    public int getFeatureType(int i) {
        if(featureTypes == null)
            return -1;
        return featureTypes[i];
    }

    /**
     * This method will search the document, counting the number of each
     * possible delimiter per line to identify the delimiter to use. If in
     * the first pass we can not find a single delimiter that that can be found
     * the same number of times in each line, we will strip all the whitespace
     * off the start and end of the lines, and try again. If then we still can
     * not find the delimiter, we will fail.
     *
     * This method also counts the number of rows and columns in the file.
     */
    private void scanFile() throws Exception {
        int counters [] = new int [4];
        final int tabIndex = 0, spaceIndex = 1, commaIndex = 2, pipeIndex = 3;

        // Now just count them.
        int commaCount = -1, spaceCount = -1, tabCount = -1, pipeCount = -1;
        boolean isComma = true, isSpace = true, isTab = true, isPipe = true;

        String line;
        final int NUM_ROWS_TO_COUNT = 10;
        ArrayList lines = new ArrayList();

        BufferedReader reader = new BufferedReader (new FileReader (file));

        // read the file in one row at a time
        int currentRow = 0;
        while ( ((line = reader.readLine ()) != null) && (currentRow < NUM_ROWS_TO_COUNT)) {
            lines.add(line);
            char[] bytes = line.toCharArray ();

            // In this row, count instances of each delimiter
            for (int i = 0 ; i < bytes.length ; i++) {
                switch (bytes [i]) {
                case TAB:
                    counters [tabIndex] ++;
                    break;
                case SPACE:
                    counters [spaceIndex] ++;
                    break;
                case COMMA:
                    counters [commaIndex] ++;
                    break;
                case PIPE:
                    counters[pipeIndex]++;
                    break;
                }
            }

            // If first row, just init the counts...
            if (currentRow == 0) {
                commaCount = counters [commaIndex] == 0 ? -1 : counters[commaIndex];
                spaceCount = counters [spaceIndex] == 0 ? -1 : counters[spaceIndex];
                tabCount = counters [tabIndex] == 0 ? -1 : counters[tabIndex];
                pipeCount = counters [pipeIndex] == 0 ? -1 : counters[pipeIndex];
            } else {
                // Check that the counts remain the same.
                if (counters [commaIndex] != commaCount)
                    isComma = false;
                if (counters [spaceIndex] != spaceCount)
                    isSpace = false;
                if (counters [tabIndex] != tabCount)
                    isTab = false;
                if (counters [pipeIndex] != pipeCount)
                    isPipe = false;
            }
            counters [tabIndex] = counters [spaceIndex] = counters [commaIndex] = counters[pipeIndex] = 0;
            currentRow++;
        }

        if ( lines.size() < 2 ) {
	    throw new Exception(
		"The input file must have at least 2 rows for"
		+ " the delimiter to be identified automatically. \n"
                + file.getName()
                + " has only "
                + lines.size()
                + " row(s). ");
        }

        char delim = '0';
        boolean delimiterFound = false;

        if( (commaCount <= 0) && (spaceCount <= 0) && (tabCount >= 0) && (pipeCount <= 0) )
            isTab = true;
        else if( (commaCount <= 0) && (spaceCount >= 0) && (tabCount <= 0) && (pipeCount <= 0) )
            isSpace = true;
        else if( (commaCount >= 0) && (spaceCount <= 0) && (tabCount <= 0) && (pipeCount <= 0) )
            isComma = true;
        else if( (commaCount <= 0) && (spaceCount <= 0) && (tabCount <= 0) && (pipeCount >= 0) )
            isPipe = true;

        // Did one of the possible delimiters come up a winner?
        if (isComma && !isSpace && !isTab && !isPipe) {
            setDelimiter(COMMA);
            delimiterFound = true;
        }
        else if (!isComma && isSpace && !isTab && !isPipe) {
            setDelimiter(SPACE);
            delimiterFound = true;
        }
        else if (!isComma && !isSpace && isTab && !isPipe) {
            setDelimiter(TAB);
            delimiterFound = true;
        }
        else if(!isComma && !isSpace && !isTab && isPipe) {
            setDelimiter(PIPE);
            delimiterFound = true;
        }

        if(!delimiterFound) {
            // OK, that didn't work. Lets trim the strings and see if it will work the.
            // read the file in one row at a time
            isComma = true;
            isSpace = true;
            isTab = true;
            isPipe = false;

            for (currentRow = 0; currentRow < lines.size(); currentRow++) {
                String tmp = ((String)lines.get(currentRow)).trim();
                char [] bytes = tmp.toCharArray ();
                counters [tabIndex] = counters [spaceIndex] = counters [commaIndex] = 0;
                // In this row, count instances of each delimiter
                for (int i = 0 ; i < bytes.length ; i++) {
                    switch (bytes [i]) {
                    case TAB:
                        counters [tabIndex] ++;
                        break;
                    case SPACE:
                        counters [spaceIndex] ++;
                        break;
                    case COMMA:
                        counters [commaIndex] ++;
                        break;
                    case PIPE:
                        counters [pipeIndex] ++;
                        break;
                    }
                }

                // If first row, just init the counts...
                if (currentRow == 0) {
                    commaCount = counters [commaIndex] == 0 ? -1 : counters[commaIndex];
                    spaceCount = counters [spaceIndex] == 0 ? -1 : counters[spaceIndex];
                    tabCount = counters [tabIndex] == 0 ? -1 : counters[tabIndex];
                    pipeCount = counters [pipeIndex] == 0 ? -1 : counters[pipeIndex];
                } else {

                    // Check that the counts remain the same.
                    if (counters [commaIndex] != commaCount)
                        isComma = false;
                    if (counters [spaceIndex] != spaceCount)
                        isSpace = false;
                    if (counters [tabIndex] != tabCount)
                        isTab = false;
                    if (counters [pipeIndex] != pipeCount)
                        isPipe = false;
                }
            }

            if( (commaCount <= 0) && (spaceCount <= 0) && (tabCount > 0) && (pipeCount <= 0))
                isTab = true;
            else if( (commaCount <= 0) && (spaceCount >= 0) && (tabCount <= 0) && (pipeCount <= 0))
                isSpace = true;
            else if( (commaCount >= 0) && (spaceCount <= 0) && (tabCount <= 0) && (pipeCount <= 0))
                isComma = true;
            else if( (commaCount <= 0) && (spaceCount <= 0) && (tabCount <= 0) && (pipeCount >= 0))
                isPipe = true;

            // Did one of the possible delimiters come up a winner?
            if (isComma && !isSpace && !isTab && !isPipe) {
                setDelimiter(COMMA);
                delimiterFound = true;
            }
            else if (!isComma && isSpace && !isTab && !isPipe) {
                setDelimiter(SPACE);
                delimiterFound = true;
            }
            else if (!isComma && !isSpace && isTab && !isPipe) {
                setDelimiter(TAB);
                delimiterFound = true;
            }
            else if (!isComma && !isSpace && !isTab && isPipe) {
                setDelimiter(PIPE);
                delimiterFound = true;
            }

            if(!delimiterFound)
                throw new IOException("No delimiter could be found.");
        }

        scanRowsCols();
    }

    private void scanRowsCols() throws FileNotFoundException, IOException {
        int nr = 0;
        int nc = 0;

        BufferedReader reader = new BufferedReader (new FileReader (file));
        String line;

        // read the file in one row at a time
        while ( (line = reader.readLine ()) != null) {
            if(line.trim().length() > 0) {
              nr++;
              int ct = countRowElements(line);
              if (ct > nc)
                nc = ct;
            }
        }
        numRows = nr;
        numColumns = nc;

    }


    /**
     * Skip to a specific line in the file.
     * @param lineNum the line number to skip to
     */
    private void skipToLine(int lineNum) {
        try {
            if(lineNum < lineReader.getLineNumber())
                lineReader = new LineNumberReader(new FileReader(file));
            int ctr = 0;
            while(ctr < lineNum) {
                lineReader.readLine();
                ctr++;
            }
        }
        catch(Exception e) {
        }
    }

    /**
     * Skip to a specific row in the file.  Rows are lines of data in the file,
     * not including the optional meta data rows.
     * @param rowNum the row number to skip to
     */
    protected String skipToRow(int rowNum) {
        if(labelsRow > -1)
            rowNum++;
        if(typesRow > -1)
            rowNum++;
        if(inOutRow > -1)
            rowNum++;
        if(nomScalarRow > -1)
            rowNum++;
        rowNum += blankRows;
        try {
            if(rowNum < lineReader.getLineNumber())
                lineReader = new LineNumberReader(new FileReader(file));
            int current = lineReader.getLineNumber();
            while(current < rowNum-1) {
                lineReader.readLine();
                current++;
            }

            String line;
            while( ( (line = lineReader.readLine()) != null) && (line.trim().length() == 0))
              blankRows++;
            return line;
        }
        catch(Exception e) {
            return null;
        }
    }

    /**
     * Read in a row and put its elements into an ArrayList.
     * @param row the row to tokenize
     * @return an ArrayList containing each of the elements in the row
     */
    public ParsedLine getRowElements(int rowNum) {
        try {
          ParsedLine pl = new ParsedLine();
            String row = skipToRow(rowNum);
            //String row = lineReader.readLine();
            if(row == null)
                return null;
            int current = 0;
            char[][] thisRow = new char[numColumns][];
            boolean[] bl = new boolean[numColumns];
            int counter = 0;
            char [] bytes = row.toCharArray();
            char del = getDelimiter();
            int len = bytes.length;

            for (int i = 0 ; i < len ; i++) {
                if (bytes[i] == del) {
                    if ((i-current) > 0) {
                        char [] newBytes = new char [i-current];
                        System.arraycopy (bytes, current, newBytes, 0, i-current);
                        thisRow[counter] = newBytes;
                        counter++;
                    } else {
                        bl[counter] = true;
                        thisRow[counter] = new char[0];
                        counter++;
                    }
                    current = i+1;
                }
            }

            if ((len-current) > 0) {
                char [] newBytes = new char [len-current];
                System.arraycopy (bytes, current, newBytes, 0, len-current);
                thisRow[counter] = newBytes;
                counter++;
            }

            for(int i = counter; i < thisRow.length; i++) {
                thisRow[i] = new char[0];
                bl[i] = true;
            }

            pl.elements = thisRow;
            pl.blanks = bl;

            return pl;
        }
        catch(Exception e) {
            return null;
        }
    }
    /**
     * Read in a row and put its elements into an ArrayList.
     * @param row the row to tokenize
     * @return an ArrayList containing each of the elements in the row
     */
    private ArrayList getLineElements(int rowNum) {
        try {
            skipToLine(rowNum);
            String row = lineReader.readLine();
            int current = 0;
            ArrayList thisRow = new ArrayList();
            char [] bytes = row.toCharArray();
            char del = getDelimiter();
            int len = bytes.length;

            for (int i = 0 ; i < len ; i++) {
                if (bytes[i] == del) {
                    if ((i-current) > 0) {
                        char [] newBytes = new char [i-current];
                        System.arraycopy (bytes, current, newBytes, 0, i-current);
                        thisRow.add(newBytes);
                    } else {
                        thisRow.add (new char [0]);
                    }
                    current = i+1;
                }
            }

            if ((len-current) > 0) {
                char [] newBytes = new char [len-current];
                System.arraycopy (bytes, current, newBytes, 0, len-current);
                thisRow.add(newBytes);
            }
            return thisRow;
        }
        catch(Exception e) {
            return null;
        }
    }

    /**
        Count the number of tokens in a row.
        @param row the row to count
        @return the number of tokens in the row
    */
    private int countRowElements (String row) {
        int current = 0;

        char [] bytes = row.toCharArray ();
        int len = bytes.length;

        int numToks = 0;

        for (int i = 0 ; i < len ; i++) {
            if (bytes[i] == getDelimiter()) {
                current = i+1;
                numToks++;
            }
        }

        if ((len-current) > 0) {
            numToks++;
        }

        return numToks;
    }
}

// QA Comments
// 2/14/03 - Handed off to QA by David Clutter
// 2/16/03 - Anca started QA process. Tested negative, zero, wrong label
//           type row numbers. Added error handling for types and labels
//           row numbers out of bound.
// 2/18/03 - checked into basic.
// 3/21/03 - added except if too few rows to find delim;  deleted code
//           that was commented out so clearer what's actually being used.
// END QA Comments
