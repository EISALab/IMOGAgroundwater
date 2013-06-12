// working version

package ncsa.d2k.modules.core.prediction.decisiontree.rainforest;



import ncsa.d2k.modules.*;

import ncsa.d2k.modules.core.datatype.table.*;

import ncsa.d2k.modules.core.prediction.decisiontree.*;

import java.io.Serializable;

import java.util.*;



/**

        Encapsulates a decision tree built from a database cube.  Takes an ExampleTable as input

        and uses the decision tree to predict the outcome for each row

        in the data set.



        This module is created based on DecisionTreeModel written by David Clutter



        @author Dora Cai



*/

public class DecisionForestModel extends PredictionModelModule

        implements Serializable, NominalViewableDTModel {



        static final long serialVersionUID = -473168938003511128L;



        // The root of the decision tree

        private DecisionForestNode root;



        // The table that is passed to the tree to perform predictions

        private transient ExampleTable table;



        // The unique values in the output column of the table

        //private String[] uniqueOutputs;



        //private String[][] uniqueInputs;



        // The number of examples in the data set

        private int numExamples;



        private int[] inputFeatures;

        private int[] outputFeatures;



//	private int trainingSetSize;



	//private String[] inputColumnNames;

	//private String[] outputColumnNames;



        private String[] classNames;



        private boolean[] inputIsScalar;

        private boolean[] outputIsScalar;



        /** the labels for the input columns */

        //private String[] inputColumnLabels;

        /** the labels for the outputs columns */

        //private String[] outputColumnLabels;

        /** the datatypes for the input features */

        //private int[] inputFeatureTypes;

        /** the datatypes for the output features */

        //private int[] outputFeatureTypes;



        /**

                Constructor

                @param rt the root of the decision tree

        */

        public DecisionForestModel(DecisionForestNode rt, ExampleTable table, int totalRow, String[] classValues) {

                super(table);

                setName("DecisionForestModel");

                root = rt;

                inputFeatures = table.getInputFeatures();

                outputFeatures = table.getOutputFeatures();

                numExamples = totalRow;

                classNames = classValues;

        }



        public String getModuleInfo() {

                String s = "Encapsulates a decision tree.  Takes an ";

                s += "ExampleTable as input and uses the decision tree to ";

                s += "predict the outcome for each row in the data set.";

                return s;

        }



    public String getModuleName() {

        return "Rain Forest Decision Tree Model";

    }



        public String[] getInputTypes() {

                String[] in = {"ncsa.d2k.modules.core.prediction.decisiontree.rainforest.DecisionForestNode","ncsa.d2k.modules.core.datatype.table.ExampleTable","java.lang.int"};

                return in;

        }



        // change to prediction table

        public String[] getOutputTypes() {

                String[] out = {"ncsa.d2k.modules.core.datatype.table.PredictionTable",

                               "ncsa.d2k.modules.core.prediction.decisiontree.rainforest.DecisionForestModel"};

                return out;

        }



        public String getInputInfo(int i) {

          switch (i) {

            case 0: return "DecisionForestNode.";

            case 1: return "ExampleTable.";

            case 2: return "Total number of rows.";

            default: return "No such input.";

          }

        }



    public String getInputName(int i) {

          switch (i) {

            case 0: return "DecisionForestNode.";

            case 1: return "ExampleTable.";

            case 2: return "Total number of rows.";

            default: return "No such input.";

          }

    }



        public String getOutputInfo(int i) {

          switch (i) {

            case 0: return "The data set with an extra column of predictions.";

            case 1: return "Decision tree model.";

            default: return "No such output.";

          }

        }



    public String getOutputName(int i) {

          switch (i) {

            case 0: return "Predictions";

            case 1: return "DTModel";

            default: return "No such output.";

          }

    }


        /**

                Get the class names.

                @return the class names

        */

          public String[] getClassNames() {

          //public final String[] getClassNames() {

                return classNames;

        }



        /**

                Pull in the table and pass it to predict.

        */

        public void doit() throws Exception {

                ExampleTable et = (ExampleTable)pullInput(0);

                PredictionTable retVal = predict(et);

                pushOutput(retVal, 0);

                pushOutput(this, 1);

        }



        /**

                Predict an outcome for each row of the table using the

                decision tree.

                @param val the table

                @return the table with an extra column of predictions at the end

        */

/*	public PredictionTable predict(ExampleTable val) {

                table = (ExampleTable)val;



                // When building model by RainForest algorithm, prediction table

                // does not exist, the date type of the prediction column is "object".

                // When using model to predict testing examples, the prediction

                // table is created, and the data type of the prediction column is "String".

                if (table.getColumnType(table.getNumColumns()-1)==9) {

                  return null;

                }

                numExamples = table.getNumRows();



                PredictionTable pt = null;

                if (table instanceof PredictionTable)

                        pt = (PredictionTable) table;

                else

                        pt = table.toPredictionTable();



                int[] outputs = pt.getOutputFeatures();

                int[] preds = pt.getPredictionSet();



                if(preds.length == 0) {

                        String [] predic = new String[pt.getNumRows()];

                        pt.addPredictionColumn(predic, "Predictions");

                        preds = pt.getPredictionSet();

                }



                for(int i = 0; i < pt.getNumRows(); i++) {

                        String pred = (String)root.evaluate(root, pt, i);

                        pt.setStringPrediction(pred, i, 0);

                }

                return pt;

        }*/





        protected void makePredictions(PredictionTable pt) {

          // When building model by RainForest algorithm, prediction table

          // does not exist, the date type of the prediction column is "object".

          // When using model to predict testing examples, the prediction

          // table is created, and the data type of the prediction column is "String".

          if (pt.getColumnType(pt.getNumColumns()-1) != 9) {

            for(int i = 0; i < pt.getNumRows(); i++) {

                String pred = (String)root.evaluate(root, pt, i);

                pt.setStringPrediction(pred, i, 0);

            }

          }

        }





        /**

         * Get the number of examples from the data set passed to

         * the predict method.

         * @return the number of examples.

         */

        public int getNumExamples() {

                return numExamples;

        }



        /**

                Get the unique values of the output column.

                @param the unique items of the output column

        */



        public String[] getUniqueOutputValues() {

          return classNames;

                //return uniqueOutputs;

        }



        /**

                Get the unique values in a column of a Table

                @param vt the Table

                @param col the column we are interested in

                @return a String array containing the unique values of the column

        */

        public static String[] uniqueValues(Table vt, int col) {

                int numRows = vt.getNumRows();



                // count the number of unique items in this column

                HashSet set = new HashSet();

                for(int i = 0; i < numRows; i++) {

                        String s = vt.getString(i, col);

                        if(!set.contains(s))

                                set.add(s);

                }



                String[] retVal = new String[set.size()];

                int idx = 0;

                Iterator it = set.iterator();

                while(it.hasNext()) {

                        retVal[idx] = (String)it.next();

                        idx++;

                }



                return retVal;

        }



        /**

                Get the root of this decision tree.

                @return the root of the tree

        */

        public DecisionForestNode getRoot() {

                return root;

        }



        public void setRoot(DecisionForestNode newRoot) {

                root = newRoot;

        }



        /**

                Get the Viewable root of this decision tree.

                @return the root of the tree

        */

        public ViewableDTNode getViewableRoot() {

                return root;

        }



        public String[] getInputs() {

                return getInputColumnLabels();

        }



        public String[] getUniqueInputValues(int index) {

          // this method has only been used to get the unique values in the class column.

          return classNames;

        }



        public boolean scalarInput(int index) {

            //return inputIsScalar[index];
            return super.getScalarInputs()[index];

        }



}

