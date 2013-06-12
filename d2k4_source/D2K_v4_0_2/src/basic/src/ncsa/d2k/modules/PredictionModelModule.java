package ncsa.d2k.modules;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import java.util.*;


/**
 * TO DO add useNameChecking
 */
abstract public class PredictionModelModule extends /*Prediction*/ModelModule implements java.io.Serializable {

    /** the size of the training set for this model */
    private int trainingSetSize;
    /** the labels for the input columns */
    private String[] inputColumnLabels;
    /** the labels for the outputs columns */
    private String[] outputColumnLabels;
    /** the datatypes for the input features */
    private int[] inputFeatureTypes;
    /** the datatypes for the output features */
    private int[] outputFeatureTypes;

    /** the scalar inputs are marked true */
    private boolean[] scalarInputs;
    /** the scalar outputs are marked true */
    private boolean[] scalarOutputs;

    private List transformations;

    /** A flag used to determine whether transformations should be
     * applied in the predict method or not
     */
    protected boolean applyTransformationsInPredict = false;
    protected boolean applyReverseTransformationsAfterPredict = false;

    protected PredictionModelModule() {
    }

    protected PredictionModelModule(ExampleTable train) {
        setTrainingTable(train);
    }

    protected PredictionModelModule(
        int trainingSetSize,
        String[] inputColumnLabels,
        String[] outputColumnLabels,
        int[] inputFeatureTypes,
        int[] outputFeatureTypes/*,
        boolean[] scalarInputs,
        boolean[] scalarOutputs*/) {
          this.trainingSetSize    = trainingSetSize;
          this.inputColumnLabels  = inputColumnLabels;
          this.outputColumnLabels = outputColumnLabels;
          this.inputFeatureTypes  = inputFeatureTypes;
          this.outputFeatureTypes = outputFeatureTypes;
          //this.scalarInputs = scalarInputs;
          //this.scalarOutputs = scalarOutputs;
    }
/*    private void writeObject(java.io.ObjectOutputStream out)
         throws Exception {



        out.defaultWriteObject();

     }
     private void readObject(java.io.ObjectInputStream in)
         throws Exception {

      System.out.println("1IC: "+inputColumnLabels);
      in.defaultReadObject();
      System.out.println("2IC: "+inputColumnLabels);
     }*/

    /**
     *	Describe the function of this module.
     *	@return the description of this module.
     */
    public String getModuleInfo() {
        return "Makes predictions on a set of examples.";
    }

    /**
     *	The input is a Table.
     *	@return the input types
     */
    public String[] getInputTypes() {
        String[] in = {"ncsa.d2k.modules.core.datatype.table.Table"};
        return in;
    }

    /**
     *	The input is an ExampleTable.
     *	@param i the index of the input
     *	@return the description of the input
     */
    public String getInputInfo(int i) {
        return "A set of examples to make predicitons on.";
    }

    /**
     *	Get the name of the input.
     *	@param i the index of the input
     *	@return the name of the input
     */
    public String getInputName(int i) {
        return "Examples";
    }

    /**
     *	The output is a PredictionTable.
     *	@return the output types
     */
    public String[] getOutputTypes() {
        String[] out = {"ncsa.d2k.modules.core.datatype.table.PredictionTable"};
        return out;
    }

    /**
     *	Describe the output.
     *	@param i the index of the output
     *	@return the description of the output
     */
    public String getOutputInfo(int i) {
        String out = "The input set of examples, with extra columns of "
                   +"predicitions added to the end.";
        return out;
    }

    /**
     *	Get the name of the output.
     *	@param i the index of the output
     *	@return the name of the output
     */
    public String getOutputName(int i) {
        return "Predictions";
    }

    /**
     *	Pull in the ExampleTable, pass it to the predict() method,
     *	and push out the PredictionTable returned by predict();
     */
    public void doit() throws Exception {
        Table et = (Table)pullInput(0);
        pushOutput(predict(et), 0);
    }

    /**
     * Predict the outcomes given a set of examples.  The return value
     * is a PredictionTable, which is the same as the input set with
     * extra column(s) of predictions added to the end.
     * @param value a table with a set of examples to predict on
     * @return the input table, with extra columns for predictions
     */
    public PredictionTable predict (Table table) throws Exception {
        PredictionTable pt;
        if(table instanceof PredictionTable) {
            // ensure that the inputFeatures and predictionSet are correct..
            pt = (PredictionTable)table;
            if(transformations != null && getApplyTransformationsInPredict()) {
              applyTransformations(pt);
            }

            Map columnToIndexMap = new HashMap(pt.getNumColumns());
            for(int i = 0; i < pt.getNumColumns(); i++)
                columnToIndexMap.put(pt.getColumnLabel(i), new Integer(i));

            // ensure that the input features of pt are set correctly.
            int[] curInputFeat = pt.getInputFeatures();
            boolean inok = true;
            if(curInputFeat != null) {
                if(curInputFeat.length != inputColumnLabels.length)
                    inok = false;
                else {
                    // for each input feature
                    for(int i = 0; i < curInputFeat.length; i++) {
                        String lbl = pt.getColumnLabel(curInputFeat[i]);
                        if(!lbl.equals(inputColumnLabels[i]))
                            inok = false;
                        if(!inok)
                            break;
                    }
                }
            }
            else
                inok = false;

            if(!inok) {
                // if the inputs are not ok, redo them
                int[] newInputFeat = new int[inputColumnLabels.length];
                for(int i = 0; i < inputColumnLabels.length; i++) {
                    Integer idx = (Integer)columnToIndexMap.get(inputColumnLabels[i]);
                    if(idx == null)
                        // the input column did not exist!!
                        //throw new Exception();
                        throw new Exception("input column missing:index="+i+":label="+inputColumnLabels[i]);
                    else
                        newInputFeat[i] = idx.intValue();
                }
                pt.setInputFeatures(newInputFeat);
            }

            // ensure that the prediction columns are set correctly.
            int[] curPredFeat = pt.getPredictionSet();
            boolean predok = true;
            if(curPredFeat != null) {
                if(curPredFeat.length != outputColumnLabels.length)
                    predok = false;
                else {
                    // for each input feature
                    for(int i = 0; i < curPredFeat.length; i++) {
                        String lbl = pt.getColumnLabel(curPredFeat[i]);
                        if(!lbl.equals(outputColumnLabels[i]+PredictionTable.PREDICTION_COLUMN_APPEND_TEXT))
                            predok = false;
                        if(!predok)
                            break;
                    }
                }
            }
            else
                predok = false;

        }

        // it was not a prediction table.  make it one and set the input features
        // and prediction set accordingly
        else {
            ExampleTable et = table.toExampleTable();
            if(transformations != null && getApplyTransformationsInPredict()) {
              applyTransformations(et);
            }
            // turn it into a prediction table
            pt = et.toPredictionTable();
            Map columnToIndexMap = new HashMap(pt.getNumColumns());
            for(int i = 0; i < pt.getNumColumns(); i++)
                columnToIndexMap.put(pt.getColumnLabel(i), new Integer(i));

            int[] inputFeat = new int[inputColumnLabels.length];
            for(int i = 0; i < inputColumnLabels.length; i++) {
                Integer idx = (Integer)columnToIndexMap.get(inputColumnLabels[i]);
                if(idx == null)
                    // the input column was missing, throw exception
                    throw new Exception("input column missing:index="+i+":label="+inputColumnLabels[i]);
                else
                    inputFeat[i] = idx.intValue();
            }
            pt.setInputFeatures(inputFeat);

            boolean outOk = true;
            int[] outputFeat = new int[outputColumnLabels.length];
            for(int i = 0; i < outputColumnLabels.length; i++) {
                Integer idx = (Integer)columnToIndexMap.get(outputColumnLabels[i]);
                if(idx == null)
                    // the input column was missing, throw exception
                    outOk = false;
                //    throw new Exception();
                else
                    outputFeat[i] = idx.intValue();
            }
            if(outOk) {
              pt.setOutputFeatures(outputFeat);
            }

            // ensure that the prediction columns are set correctly.
            int[] curPredFeat = pt.getPredictionSet();
            boolean predok = true;
            if(curPredFeat != null) {
                if(curPredFeat.length != outputColumnLabels.length)
                    predok = false;
                else {
                    // for each input feature
                    for(int i = 0; i < curPredFeat.length; i++) {
                        String lbl = pt.getColumnLabel(curPredFeat[i]);
                        if(!lbl.equals(outputColumnLabels[i]+PredictionTable.PREDICTION_COLUMN_APPEND_TEXT))
                            predok = false;
                        if(!predok)
                            break;
                    }
                }
            }
            else
                predok = false;
            if(!predok) {
              int[] predSet = new int[outputFeatureTypes.length];
              // add as many prediction columns as there are outputs
              for(int i = 0; i < outputFeatureTypes.length; i++) {
                // add the prediction columns
                int type = outputFeatureTypes[i];
                switch(type) {
                  case ColumnTypes.DOUBLE:
                    pt.addColumn(new DoubleColumn(pt.getNumRows()));
                    predSet[i] = pt.getNumColumns()-1;
                    break;
                  case ColumnTypes.STRING:
                    pt.addColumn(new StringColumn(pt.getNumRows()));
                    predSet[i] = pt.getNumColumns()-1;
                    break;

                    // !!!!!!!!!!!!!!!!!!!!!!!!!!!
                    // fill in for all column types

                    // is it ok to call pt.addColumn() ?  Will this work with
                    // different implementations of Table?
                }
              }
              pt.setPredictionSet(predSet);
            }
        }
        makePredictions(pt);
        if(transformations != null && this.getApplyReverseTransformationsAfterPredict())
          applyReverseTransformations(pt);
        return pt;
    }

    abstract protected void makePredictions(PredictionTable pt) throws Exception;

    /**
     * Set up all the meta-data related to the training set for this model.
     * @param et
     */
    protected void setTrainingTable(ExampleTable et) {
        trainingSetSize = et.getNumRows();
        int[] inputs = et.getInputFeatures();
        inputColumnLabels = new String[inputs.length];
        for(int i = 0; i < inputColumnLabels.length; i++)
            inputColumnLabels[i] = et.getColumnLabel(inputs[i]);
        int[] outputs = et.getOutputFeatures();
        outputColumnLabels = new String[outputs.length];
        for(int i = 0; i < outputColumnLabels.length; i++)
            outputColumnLabels[i] = et.getColumnLabel(outputs[i]);
        inputFeatureTypes = new int[inputs.length];
        for(int i = 0; i < inputs.length; i++)
            inputFeatureTypes[i] = et.getColumnType(inputs[i]);
        outputFeatureTypes = new int[outputs.length];
        for(int i = 0; i < outputs.length; i++)
            outputFeatureTypes[i] = et.getColumnType(outputs[i]);

        scalarInputs = new boolean[inputs.length];
        for(int i = 0; i < inputs.length; i++)
          scalarInputs[i] = et.isInputScalar(i);
        scalarOutputs = new boolean[outputs.length];
        for(int i = 0; i < outputs.length; i++)
          scalarOutputs[i] = et.isOutputScalar(i);

        //copy the transformations
        try {
          List trans = et.getTransformations();
          if(trans instanceof ArrayList) {
            transformations = (ArrayList) ( (ArrayList) (et).getTransformations()).clone();
          }
          else if(trans instanceof LinkedList) {
            transformations = (LinkedList) ( (LinkedList) (et).getTransformations()).clone();
          }
          else if(trans instanceof Vector) {
            transformations = (Vector) ( (Vector) (et).getTransformations()).clone();
          }
          else
            transformations = null;
        }
        catch (Exception e) {
          e.printStackTrace();
          transformations = null;
        }
    }

    /**
     * Get the size of the training set that built this model.
     * @return the size of the training set used to build this model
     */
    public int getTrainingSetSize() {
        return trainingSetSize;
    }

    /**
     * Get the labels of the input columns.
     * @return the labels of the input columns
     */
    public String[] getInputColumnLabels() {
        return inputColumnLabels;
    }

    /**
     * Get the labels of the output columns.
     * @return the labels of the output columns
     */
    public String[] getOutputColumnLabels() {
        return outputColumnLabels;
    }

    /**
     * Get the data types of the input columns in the training table.
     * @return the datatypes of the input columns in the training table
     * @see ncsa.d2k.modules.core.datatype.table.ColumnTypes
     */
    public int[] getInputFeatureTypes() {
        return inputFeatureTypes;
    }

    /**
     * Get the data types of the output columns in the training table.
     * @return the data types of the output columns in the training table
     * @see ncsa.d2k.modules.core.datatype.table.ColumnTypes
     */
    public int[] getOutputFeatureTypes() {
        return outputFeatureTypes;
    }

    /**
     * Determine which inputs were scalar.
     * @return a boolean map with inputs that are scalar marked 'true'
     */
    public boolean[] getScalarInputs() {
      return scalarInputs;
    }

    /**
     * Determine which outputs were scalar.
     * @return a boolean map with outputs that are scalar marked 'true'
     */
    public boolean[] getScalarOutputs() {
      return scalarOutputs;
    }

    /**
     * Apply all the transformations in the Transformations list to
     * the given ExampleTable
     * @param et the ExampleTable to transform
     */
    protected void applyTransformations(ExampleTable et) {
      if(transformations != null) {
        for(int i = 0; i < transformations.size(); i++) {
          Transformation trans = (Transformation)transformations.get(i);
          trans.transform(et);
        }
      }
    }

    protected void applyReverseTransformations(ExampleTable et) {
      if(transformations != null) {
        for(int i = 0; i < transformations.size(); i++) {
          Transformation trans = (Transformation)transformations.get(i);
          if(trans instanceof ReversibleTransformation)
            //trans.transform(et);
            ((ReversibleTransformation)trans).untransform(et);
        }
      }
    }

    public void setTransformations(List trans) {
      transformations = trans;
    }

    public List getTransformations() {
      return transformations;
    }

    public void setApplyTransformationsInPredict(boolean b) {
      applyTransformationsInPredict = b;
    }

    public boolean getApplyTransformationsInPredict() {
      return applyTransformationsInPredict;
    }

    public void setApplyReverseTransformationsAfterPredict(boolean b) {
      applyReverseTransformationsAfterPredict = b;
    }

    public boolean getApplyReverseTransformationsAfterPredict() {
      return applyReverseTransformationsAfterPredict;
    }

    /**
     * Return a list of the property descriptions.
     * @return a list of the property descriptions.
     */
    public PropertyDescription[] getPropertiesDescriptions() {
      PropertyDescription[] pds = new PropertyDescription[2];
      pds[0] = new PropertyDescription("applyTransformationsInPredict", "Apply all transformations before predicting",
        "Set this value to true if the data should be transformed before attemping "+
        "to make predictions.");
      pds[1] = new PropertyDescription("applyReverseTransformationsAfterPredict",
        "Apply all reverse transformations after predicting",
        "Set this value to true to reverse all transformations after making "+
        "predictions.");
      return pds;
    }


	protected String getTrainingInfoHtml(){
		StringBuffer sb=new StringBuffer();
		sb.append("<b>Number Training Examples</b>:"+ trainingSetSize+"<br><br>");
		sb.append("<b>Input Features:</b>: <br>");
		sb.append("<table><tr><td><u>Name</u><td><u>Type</u>");
		sb.append("<td><u>S/N</u></tr>");
		for(int i=0;i<inputColumnLabels.length;i++){
			sb.append("<tr><td>"+inputColumnLabels[i]);
			sb.append("<td>"+ColumnTypes.getTypeName(inputFeatureTypes[i]));
			if(scalarInputs[i]){
				sb.append("<td>sclr");
			}else{
				sb.append("<td>nom");
			}
			sb.append("</tr>");
		}
		sb.append("</table><br>");

		sb.append("<b>Output (Predicted) Features</b>: <br><br>");
		sb.append("<table><tr><td><u>Name</u><td><u>Type</u>");
		sb.append("<td><u>S/N</u></tr>");

		for(int i=0;i<outputColumnLabels.length;i++){
			sb.append("<tr><td>"+outputColumnLabels[i]);
			sb.append("<td>"+ColumnTypes.getTypeName(outputFeatureTypes[i]));
			if(scalarOutputs[i]){
				sb.append("<td>sclr");
			}else{
				sb.append("<td>nom");
			}
			sb.append("</tr>");
		}
		sb.append("</table>");
		return sb.toString();
	}
}
