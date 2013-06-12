package ncsa.d2k.modules.core.datatype.table.basic.test;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import java.util.*;
import ncsa.d2k.modules.core.transform.StaticMethods;


public class AddAllTypesColumns extends ncsa.d2k.core.modules.DataPrepModule {

  protected void doit()throws Exception{
    MutableTable t = (MutableTable) pullInput(0);


    HashSet set = new HashSet();  //set of types of columns in t
    for(int i=0; i<t.getNumColumns(); i++){
      Integer type = new Integer(t.getColumnType(i));
      if (!set.contains(type))
        set.add(type);
    }//for i

//indices of numeric columns
    int cleanNumericCol = -1; //without missing values
    int nonCleanNumericCol = -1; //with missing value

//indices of nominal columns
    int cleanNominalCol = -1; //without missing values
    int nonCleanNominalCol = -1; //with missing value


//for each column - looking for the indices.
    for(int i=0; i<t.getNumColumns() &&(nonCleanNumericCol == -1 || cleanNumericCol == -1 ||
        cleanNominalCol == -1 || nonCleanNominalCol == -1); i++){

      //if the column is numeric
     if(t.isColumnNumeric(i)){
       if (cleanNumericCol == -1 && !t.hasMissingValues(i))
         cleanNumericCol = i;

       if (nonCleanNumericCol == -1 && t.hasMissingValues(i))
         nonCleanNumericCol = i;
     }//is numeric

     //the column is nominal
     else{

       if (cleanNominalCol == -1 && !t.hasMissingValues(i))
         cleanNominalCol = i;

       if (nonCleanNominalCol == -1 && t.hasMissingValues(i))
         nonCleanNominalCol = i;
     }//else
    }//for i


//adding the numeric and nominal columns.
    addNumericColumns(t, set, cleanNumericCol, nonCleanNumericCol);
    addNominalColumns(t, set, cleanNominalCol, nonCleanNominalCol);

pushOutput(t, 0);





  }//doit

  public static final int NUM_TYPES = 12;
  public static final int NUMERIC_MIN = 0;
  public static final int NUMERIC_MAX = 4;
  public static final int NOMINAL_MIN = 5;
  public static final int NOMINAL_MAX = 11;


  /**
    * Adds nominal columns of all types to <code>t</code>, with data taken from columns
    * indexed <code>cleanCol</code> and <code>nonCleanCol</code>. the columns that are added
    * are not of types included in <code>set</code>.
    * @param t - table to have columns added to it.
    * @param set - set of column types included in original t.
    * @param cleanCol - index of nominal column that has no missing values in it.
    * @param nonCleanCol - index of nominal column that has missing values in it
    */
   private void  addNominalColumns(MutableTable t, HashSet set, int cleanCol,
                                   int nonCleanCol) throws Exception{
//for each nominal type that is not yet included in the table t
     for (int i=NOMINAL_MIN; i<=NOMINAL_MAX; i++){
       if(!set.contains(new Integer(i)) && (i!=ColumnTypes.BYTE)){
         //adding 2 columns - with and without missing values.
         addNominalColumn(t, i, cleanCol);
         addNominalColumn(t, i, nonCleanCol);
       }

     }

   }



  /**
   * Adds numeric columns of all types to <code>t</code>, with data taken from columns
   * indexed <code>cleanCol</code> and <code>nonCleanCol</code>. the columns that are added
   * are not of types included in <code>set</code>.
   * @param t - table to have columns added to it.
   * @param set - set of column types included in original t.
   * @param cleanCol - index of numeric column that has no missing values in it.
   * @param nonCleanCol - index of numeric column that has missing values in it
   */
  private void  addNumericColumns(MutableTable t, HashSet set, int cleanCol,
                                  int nonCleanCol) throws Exception{


    for (int i=NUMERIC_MIN; i<=NUMERIC_MAX; i++){
      if(!set.contains(new Integer(i))){
        addNumericColumn(t, i, cleanCol);
        addNumericColumn(t, i, nonCleanCol);
      }//if

    }//for
    //special case
    addNumericColumn(t, ColumnTypes.BYTE, cleanCol);
    addNumericColumn(t, ColumnTypes.BYTE, nonCleanCol);

  }//addNumericColumns


  private Column getColumn(Table t, int type)throws Exception{
    Column newCol; //the new columns to be added.
    int rows = t.getNumRows();
    String label;


    switch (type) {
      //allocating the columns.
      case ColumnTypes.DOUBLE:
        newCol = new DoubleColumn(rows);
        label = "_double";
        break;
      case ColumnTypes.FLOAT:
        newCol = new FloatColumn(rows);
        label = "_float";
        break;
      case ColumnTypes.LONG:
        newCol = new LongColumn(rows);
        label = "_long";
        break;
      case ColumnTypes.INTEGER:
        newCol = new IntColumn(rows);
        label = "_int";
        break;
      case ColumnTypes.SHORT:
        newCol = new ShortColumn(rows);
        label = "_short";
        break;
      case ColumnTypes.STRING:
     newCol = new StringColumn(rows);
     label = "_string";
     break;
   case ColumnTypes.OBJECT:
     newCol = new ObjectColumn(rows);
     label = "_object";
      break;
   case ColumnTypes.BOOLEAN:
     newCol = new BooleanColumn(rows);
     label = "_bool";
     break;
   case ColumnTypes.BYTE:
     newCol = new ByteColumn(rows);
     label = "_byte";
     break;
   case ColumnTypes.CHAR:
     newCol = new CharColumn(rows);
     label = "_char";
     break;
   case ColumnTypes.BYTE_ARRAY:
     newCol = new ByteArrayColumn(rows);
     label = "_byte_a";
      break;
   case ColumnTypes.CHAR_ARRAY:
     newCol = new CharArrayColumn(rows);
     label = "_char_a";
      break;


 default: throw new Exception ("type of column is not known");


    }//switch
    newCol.setLabel(label);
    return newCol;

  }

  /**
   *
   * @param t
   * @param type - type of column to add
   * @param index - index of original numeric column
   * @throws Exception
   */
  private void addNumericColumn(MutableTable t, int type, int index) throws Exception{
    Column newCol = getColumn(t, type);
    String originalName = t.getColumnLabel(index);
    newCol.setLabel(originalName + newCol.getLabel());


    for (int i = 0; i < t.getNumRows(); i++) {
      newCol.setDouble(t.getDouble(i, index), i);

      //copying the is missing varrays. only for miss Col.
      newCol.setValueToMissing(t.isValueMissing(i, index), i);
    } //for i
t.addColumn(newCol);
  }//addNumericColumn



  /**
    *
    * @param t
    * @param type - type of column to add
    * @param index - index of original numeric column
    * @throws Exception
    */
   private void addNominalColumn(MutableTable t, int type, int index) throws Exception{
     Column newCol = getColumn(t, type);
     String originalName = t.getColumnLabel(index);
    newCol.setLabel(originalName + newCol.getLabel());

     for (int i = 0; i < t.getNumRows(); i++) {
       newCol.setString(t.getString(i, index), i);

       //copying the is missing varrays. only for miss Col.
       newCol.setValueToMissing(t.isValueMissing(i, index), i);
     } //for i

     t.addColumn(newCol);

   }//addNominalColumn



  /**
   * adds 2 columns of type <code>type </code> to <codE>t</code>, one with data
   * from column index <codE>cleanCol</code> and one with data from column index
   * <codE>nonCleanCol</codE>.
   * @param t - Table to have 2 columns added to
   * @param type - the type of the added columns
   * @param cleanCol - index of column that has no missing values.
   * @param nonCleanCol - index of column that has missing values.
   */




	/**
	 * returns information about the input at the given index.
	 * @return information about the input at the given index.
	 */
	public String getInputInfo(int index) {
		switch (index) {
			case 0: return "<p>      input table    </p>";
			default: return "No such input";
		}
	}

	/**
	 * returns information about the output at the given index.
	 * @return information about the output at the given index.
	 */
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Input Table";
			default: return "NO SUCH INPUT!";
		}
	}

	/**
	 * returns string array containing the datatypes of the inputs.
	 * @return string array containing the datatypes of the inputs.
	 */
	public String[] getInputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.MutableTable"};
		return types;
	}

	/**
	 * returns information about the output at the given index.
	 * @return information about the output at the given index.
	 */
	public String getOutputInfo(int index) {
		switch (index) {
			case 0: return "<p>      same as input table, only added columns of all types (Short, Long etc.)       with same data    </p>";
			default: return "No such output";
		}
	}

	/**
	 * returns information about the output at the given index.
	 * @return information about the output at the given index.
	 */
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "modified table";
			default: return "NO SUCH OUTPUT!";
		}
	}

	/**
	 * returns string array containing the datatypes of the outputs.
	 * @return string array containing the datatypes of the outputs.
	 */
	public String[] getOutputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.Table"};
		return types;
	}

	/**
	 * returns the information about the module.
	 * @return the information about the module.
	 */
	public String getModuleInfo() {
		return "this module adds to the input table columns of all types that are not yet     included in the"+
			" table. this is done so that all columns could be tested by other modules.";
	}

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "AddAllTypesColumns";
	}
}
