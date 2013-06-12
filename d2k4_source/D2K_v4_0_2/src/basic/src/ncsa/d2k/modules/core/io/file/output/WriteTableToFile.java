package ncsa.d2k.modules.core.io.file.output;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import java.io.*;
import java.beans.PropertyVetoException;

/**
 * This module writes the contents of a <code>Table</code> to a flat file.
 *
 * @author David Clutter
 */
public class WriteTableToFile extends OutputModule {

   transient String delimiter;

   String  delimChar = "C";
   boolean useDataTypes = true;
   boolean useColumnLabels = true;

   public void setDelimChar(String c) throws PropertyVetoException {
      // here we check for valid entries and save as upper case
      if (c.equalsIgnoreCase("C")) {
         delimChar = "C";
      } else if (c.equalsIgnoreCase("S")) {
         delimChar = "S";
      } else if (c.equalsIgnoreCase("T")) {
         delimChar = "T";
      } else {
         throw new PropertyVetoException(
              "An invalid Delimiter Character was entered. "+
              "Enter C for comma, S for space, or T for tab.",
              null);
      }
   }

   public String getDelimChar() {
      return delimChar;
   }

   public void setUseDataTypes(boolean b) {
      useDataTypes = b;
   }

   public boolean getUseDataTypes() {
      return useDataTypes;
   }

   public void setUseColumnLabels(boolean b) {
      useColumnLabels = b;
   }

   public boolean getUseColumnLabels() {
      return useColumnLabels;
   }

    /**
       Return a description of the function of this module.
       @return A description of this module.
    */
    public String getModuleInfo() {
       StringBuffer sb = new StringBuffer("<p>Overview: ");
       sb.append("<p>This module writes the contents of a Table to a file. ");
       sb.append("</p><p>Detailed Description: ");
       sb.append("This module writes the contents of the input ");
       sb.append("<i>Table</i> to the file specified by the input <i>File Name</i> if that input is connected, otherwise it will write the table to a file whose name is constructed from the table label.");
       sb.append("The user can select a space, a common, or a tab as the ");
       sb.append("column delimiter using the properties editor. ");
       sb.append("If the <i>useColumnLabels</i> property is set, ");
       sb.append("the first row of the file will be the column labels. ");
       sb.append("If the <i>useDataTypes</i> property is set, the data type of ");
       sb.append("each column will be written to the file.");
       sb.append("</p><p>Data Handling: ");
       sb.append("This module does not destroy or modify its input data.");
       sb.append("</p>");
       return sb.toString();
   }

   public PropertyDescription[] getPropertiesDescriptions() {

      PropertyDescription[] descriptions = new PropertyDescription [3];

      descriptions[0] = new PropertyDescription("delimChar",
         "Delimiter Character (C=comma, S=space, T=tab)",
         "Selects the delimiter character used to separate columns in the file.  "+
         "Enter C for comma, S for space, or T for tab.");

      descriptions[1] = new PropertyDescription("useColumnLabels",
         "Write Column Labels",
         "Controls whether the table's column labels are written to the file.");

      descriptions[2] = new PropertyDescription("useDataTypes",
         "Write Data Types",
         "Controls whether the table's column data types are written to the file.");

      return descriptions;

   }

    /**
       Return the name of this module.
       @return The name of this module.
    */
    public String getModuleName() {
      return "Write Table to File";
   }

    /**
       Return a String array containing the datatypes the inputs to this
       module.
       @return The datatypes of the inputs.
    */
    public String[] getInputTypes() {
      String[] types = {"java.lang.String","ncsa.d2k.modules.core.datatype.table.Table"};
      return types;
   }

    /**
       Return a String array containing the datatypes of the outputs of this
       module.
       @return The datatypes of the outputs.
    */
    public String[] getOutputTypes() {
      String[] types = {		};
      return types;
   }

    /**
       Return a description of a specific input.
       @param i The index of the input
       @return The description of the input
    */
    public String getInputInfo(int i) {
      switch (i) {
         case 0: return "The name of the file to be written.";
         case 1: return "The Table to write.";
         default: return "No such input";
      }
   }

    /**
       Return the name of a specific input.
       @param i The index of the input.
       @return The name of the input
    */
    public String getInputName(int i) {
      switch(i) {
         case 0:
            return "File Name";
         case 1:
            return "Table";
         default: return "No such input";
      }
   }

    /**
       Return the description of a specific output.
       @param i The index of the output.
       @return The description of the output.
    */
    public String getOutputInfo(int i) {
      switch (i) {
         default: return "No such output";
      }
   }

    /**
       Return the name of a specific output.
       @param i The index of the output.
       @return The name of the output
    */
    public String getOutputName(int i) {
      switch(i) {
         default: return "No such output";
      }
   }

	/**
	 * if the file name input is not connected, ignore it.
	 * @return true if we are ready to fire.
	 */
	public boolean isReady() {
		if (this.isInputPipeConnected(0))
			return super.isReady();
		else
			return this.getFlags()[1] > 0;
	}

    /**
      Write the table to the file.
   */
    public void doit() throws Exception {
      Table vt = (Table)pullInput(1);
	  String filename;
	  if (this.isInputPipeConnected(0))
		  filename = (String)pullInput(0);
	  else {
	   	  filename = vt.getLabel()+".out.csv";
		  System.out.println("Writing file to "+new File(filename).getPath());
	  }


      FileWriter fw;
      String newLine = "\n";
      delimiter = ",";      // default to comma
      if (delimChar.equals("S")) {
         delimiter = " ";
      } else if (delimChar.equals("T")) {
         delimiter = "\t";
      }

      try {
          // write the actual data
         writeTable(vt, delimiter, filename, useColumnLabels, useDataTypes);
      }
      catch (IOException e) {
         throw new IOException( getAlias() +
              ": Could not open file: " + filename +
              "\n" + e );
      }

   }


   /**
      Get the datatype of a column.
   */
   public static final String getDataType(int i) {
      switch(i) {
         case 0:
            return "int";
         case 1:
            return "float";
         case 2:
            return "double";
         case 3:
            return "short";
         case 4:
            return "long";
         case 5:
            return "String";
         case 6:
            return "char[]";
         case 7:
            return "byte[]";
         case 8:
            return "boolean";
         case 9:
            return "Object";
         case 10:
            return "byte";
         case 11:
            return "char";
         default:
            return "String";
      }
   }

   public static void writeTable(Table vt, String delimiter, String fileName,
      boolean writeColumnLabels, boolean writeColumnTypes) throws IOException {

      FileWriter fw = new FileWriter(fileName);
      String newLine = "\n";

      // write the column labels
      if(writeColumnLabels) {
         for(int i = 0; i < vt.getNumColumns(); i++) {
            String s = vt.getColumnLabel(i);
            if (s == null || s.length() == 0)
               s = "column_" + i;
            fw.write(s, 0, s.length());
            if(i != (vt.getNumColumns() - 1))
               fw.write(delimiter.toCharArray(), 0, delimiter.length());
         }
         fw.write(newLine.toCharArray(), 0, newLine.length());
      }

      // write the datatypes.
      if(writeColumnTypes) {
         for(int i = 0; i < vt.getNumColumns(); i++) {
            String s = getDataType(vt.getColumnType(i));
            fw.write(s, 0, s.length());
            if(i != (vt.getNumColumns() - 1))
               fw.write(delimiter.toCharArray(), 0, delimiter.length());
         }
         fw.write(newLine.toCharArray(), 0, newLine.length());
      }

      // write the actual data
	  boolean checkMissing = true;
      for(int i = 0; i < vt.getNumRows(); i++) {
      //	System.out.println("numofRows" + vt.getNumRows());
         for(int j = 0; j < vt.getNumColumns(); j++) {
            String s;

			try {
				if (checkMissing && (vt.isValueMissing(i, j) || vt.isValueEmpty(i, j)))
					s = "";
				else
					s=vt.getString(i, j);
				if(s == null)
					s = "";
			} catch (NullPointerException npe) {
                 //ANCA intoduced line below for when vt.getString returns nullPointerException
                  s ="";
				// This table has not missing or empty values, don't check. ???
				//checkMissing = false;
				//s = vt.getString(i, j);
			}
            //System.out.println("s: "+s);
            fw.write(s, 0, s.length());
            if(j != (vt.getNumColumns() - 1) )
               fw.write(delimiter.toCharArray(), 0, delimiter.length());
         }
         fw.write(newLine.toCharArray(), 0, newLine.length());
      }
      fw.flush();
      fw.close();

   }

}
// Start QA Comments
// 3/6/03 - Received from David C and Greg & QA started by Ruth
//        - Added code to allow for selection of delimiter character and
//          updated description accordingly.   Removed lots of commented-out
//          code.  Added exception handler for IO exceptions instead of just
//          message to stderr.   Committing to Basic.
// End QA Comments
