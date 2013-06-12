package ncsa.d2k.modules.core.datatype.table.basic.test;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class TestMissingValues extends ncsa.d2k.core.modules.ComputeModule {

private int counter = 1;

 protected void doit()throws Exception{
   Table t = (Table) pullInput(0);
   System.out.println("\n\nTestMissingValues take " + counter);

   testMissing(t);



  eraseMissing(t);
  System.out.println("now the table is clean");
  testMissing(t);
  counter++;

 }//doit


private void eraseMissing(Table t){
   for (int i=0; i<t.getNumRows(); i++)
     for (int j=0; j<t.getNumColumns(); j++)
       t.getColumn(j).setValueToMissing(false, i);

 }

private void testMissing(Table t)throws Exception{
   //indices will hold indices into columns of t
  int[] indices = new int[t.getNumColumns()];


  for (int i=0; i<indices.length; i++) indices[i] = i;



  boolean clean = testColumns(t,  indices);

  System.out.println("\nAccording to tester the table has " + (clean ? "no " : "") +
                     "missing values.");
  System.out.println("\n\nAccording to table the table has " + (t.hasMissingValues() ? "" : "no ") +
                     "missing values.");

  if(t.hasMissingValues() == clean)
      throw new Exception("the input table has " +
                        (clean ? "no " : "") + "missing values but the table itself" +
                        "says that it has " + (t.hasMissingValues() ? "some." : "none.") );


     if(t instanceof ExampleTable) testExampleTable ((ExampleTable)t);

 }

private boolean  testColumns(Table t, int[] indices)throws Exception{
   boolean clean = true;

  for(int j=0; j<indices.length; j++){
    boolean cleanCol = true;
    for (int i=0; i<t.getNumRows(); i++){
      cleanCol = (cleanCol && (!t.isValueMissing(i,indices[j])));
    }


      System.out.println("\nAccording to tester column " + indices[j] + " has " + (cleanCol ? "no " : "") +
                         "missing values.");
      System.out.println("\n\nAccording to table column " + indices[j] + " has " + (t.hasMissingValues(indices[j]) ? "" : "no ") +
                         "missing values.");
      System.out.println("\n\nAccording to column " + indices[j] + ", it has " + (t.getColumn(indices[j]).hasMissingValues() ? "" : "no ") +
                         "missing values.");


    if(t.hasMissingValues(indices[j]) == cleanCol)
      throw new Exception("column index " + indices[j] + " has " +
                         (cleanCol ? "no " : "") + "missing values but the table " +
                         "says that it has " + (t.hasMissingValues(indices[j]) ? "some." : "none.") );

    if(t.getColumn(indices[j]).hasMissingValues() == cleanCol)
        throw new Exception("column index " + indices[j] + " has " +
                          (cleanCol ? "no " : "") + "missing values but the column " +
                          "says that it has " + (t.getColumn(indices[j]).hasMissingValues() ? "some." : "none.") );
    clean = clean && cleanCol;

  }//for j


return clean;

 }//testColumns

 private void testExampleTable(ExampleTable t)throws Exception{
    System.out.println("testing input columns");
   boolean inputClean = testColumns(t, t.getInputFeatures());

   System.out.println("\naccording to tester, input columns have " + (inputClean? "no " :"") +
                      "missing values.");

   System.out.println("testing output columns");
   boolean outputClean = testColumns(t, t.getOutputFeatures());
   System.out.println("\naccording to tester, output columns have " + (outputClean? "no " :"") +
                      "missing values.");

   System.out.println("\naccording to table, output & input columns have " + (t.hasMissingInputsOutputs() ? "" : "no ") +
                      "missing values.");

   if((inputClean && outputClean) == t.hasMissingInputsOutputs()){
     throw new Exception("the input table has " +
                         ((inputClean && outputClean) ? "no " : "") + "missing values  in the input or the output columns " +
                         "but the table itself" +
                         "says that it has " + (t.hasMissingInputsOutputs() ? "some." : "none.") );


   }//if

 }//testExampleTable


       /**
        * returns information about the input at the given index.
        * @return information about the input at the given index.
        */
       public String getInputInfo(int index) {
               switch (index) {
                       case 0: return "<p>      table to be tested    </p>";
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
               String[] types = {"ncsa.d2k.modules.core.datatype.table.Table"};
               return types;
       }

       /**
        * returns information about the output at the given index.
        * @return information about the output at the given index.
        */
       public String getOutputInfo(int index) {
               switch (index) {
                       default: return "No such output";
               }
       }

       /**
        * returns information about the output at the given index.
        * @return information about the output at the given index.
        */
       public String getOutputName(int index) {
               switch(index) {
                       default: return "NO SUCH OUTPUT!";
               }
       }

       /**
        * returns string array containing the datatypes of the outputs.
        * @return string array containing the datatypes of the outputs.
        */
       public String[] getOutputTypes() {
               String[] types = {		};
               return types;
       }

       /**
        * returns the information about the module.
        * @return the information about the module.
        */
       public String getModuleInfo() {
               return "This module tests the integrity of Table datatpyes and Column datatypes     regrading presence"+
                       " of missing values.";
       }

       /**
        * Return the human readable name of the module.
        * @return the human readable name of the module.
        */
       public String getModuleName() {
               return "Test Missing Values";
       }
}


