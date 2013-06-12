package ncsa.d2k.modules.core.transform.table;

import ncsa.d2k.modules.core.datatype.parameter.ParameterPoint;
import ncsa.d2k.modules.core.datatype.table.ExampleTable;
import ncsa.d2k.modules.core.datatype.table.basic.Column;
import ncsa.d2k.modules.core.datatype.table.basic.DoubleColumn;
import ncsa.d2k.modules.core.datatype.table.basic.ExampleTableImpl;
import ncsa.d2k.modules.core.datatype.table.basic.MutableTableImpl;

import java.util.ArrayList;

public class AverageModelScores extends ncsa.d2k.core.modules.DataPrepModule {

   /** number of folds to average scores for. */
   private int numberFolds =-1;
   
   /** number of scores currently obtained. */
   int counter = 0;
   
   /** the accumulated points in objective space. */
   ArrayList objectivePoints = new ArrayList();
   
   /** used to store the average score for each of the objective features. */
   double [] avgs = null;
   
   /** number of points in solution space. */
   int solutionSpaceDimensions = 0;
   
   /**
    * returns information about the input at the given index.
    * @return information about the input at the given index.
    */
   public String getInputInfo(int index) {
      switch (index) {
         case 0 :
            return "<p>"
               + "      This is the number of scores input to average."
               + "    </p>";
         case 1 :
            return "<p>"
               + "      This paramter point contains the objective score."
               + "    </p>";
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
            return "Input Count";
         case 1 :
            return "Score";
         default :
            return "NO SUCH INPUT!";
      }
   }

   /**
    * returns string array containing the datatypes of the inputs.
    * @return string array containing the datatypes of the inputs.
    */
   public String[] getInputTypes() {
      String[] types =
         {
            "java.lang.Integer",
            "ncsa.d2k.modules.core.datatype.parameter.ParameterPoint" };
      return types;
   }

   /**
    * returns information about the output at the given index.
    * @return information about the output at the given index.
    */
   public String getOutputInfo(int index) {
      switch (index) {
         case 0 :
            return "<p>"
               + "      This is the point in paramter space which contains the averaged "
               + "      objective score."
               + "    </p>";
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
            return "Averaged Objective";
         default :
            return "NO SUCH OUTPUT!";
      }
   }

   /**
    * returns string array containing the datatypes of the outputs.
    * @return string array containing the datatypes of the outputs.
    */
   public String[] getOutputTypes() {
      String[] types =
         { "ncsa.d2k.modules.core.datatype.parameter.ParameterPoint" };
      return types;
   }

   /**
    * returns the information about the module.
    * @return the information about the module.
    */
   public String getModuleInfo() {
      return "<p>"
         + "      Overview: This module takes an integer which indicates how many scores "
         + "      it should accumulate, and on the other input, the scores which will be "
         + "      averaged."
         + "    </p>"
         + "    <p>"
         + "      Detailed Description: This module will take N scores and average them, "
         + "      produced the averaged score in the form of a ParameterPoint. The number "
         + "      of scores to average is ascertained from the Integer passed in on the "
         + "      first port. When all N scores have been received, the average is "
         + "      calcuated and the result passed along."
         + "    </p>"
         + "    <p>"
         + "      Data Type Restrictions: None. This module will average any number of "
         + "      objective scores, producing a Parameter Point with the same number of "
         + "      objectives. "
         + "    </p>"
         + "    <p>"
         + "      Data Handling: The original ParameterPoiints are discarded and a new one "
         + "      is produced."
         + "    </p>"
         + "    <p>"
         + "      Scalability: There must be sufficient memory to store all the "
         + "      ParameterPoints to be averaged."
         + "    </p>";
   }

   /**
    * Return the human readable name of the module.
    * @return the human readable name of the module.
    */
   public String getModuleName() {
      return "Average Scores";
   }

   /**
    * This module will enable if we have not already gotten the fold count and
    * we have inputs on pipe 0 and 1, or if we have gotten the fold count, if we
    * have an input on pipe 1.
    */
   public boolean isReady () {
      if (numberFolds == -1 && this.getInputPipeSize(0) > 0 && 
         this.getInputPipeSize(1) > 0)
         return true;
      else if (this.getInputPipeSize(1) > 0)
         return true;
      else
         return false;
   }
   
   /**
    * Take each paramter point, accumulate the sum of the various solutions
    * in the solution space for each fold, and when we have the objective
    * values for each fold, compute the means, put that in new paramter point
    * and pass it on.
    */
   public void doit () {
      
      // Get the parameter point.
      ParameterPoint pp = (ParameterPoint) this.pullInput(1);
      if (numberFolds == -1) {
         
         // First time through, we need to get num folds, number of
         // attributes in solution space, allocate array for the
         // sums of all the solutions. 
         this.numberFolds = ((Integer) this.pullInput(0)).intValue();
         solutionSpaceDimensions = pp.getNumParameters();
         this.avgs = new double [solutionSpaceDimensions];
         for (int i = 0 ; i < solutionSpaceDimensions ; i++) 
            this.avgs[i] = 0.0;
         System.out.println("AverageModelScores: First run, folds = "
            +numberFolds+" solution space dims = "+solutionSpaceDimensions);
      }
      
      // add each objective value to the sum of the objective value.
      for (int i = 0 ; i < this.solutionSpaceDimensions; i++) {
         this.avgs[i] += pp.getValue(i);
      }
      
      System.out.print ("AverageModelScores: Values to average -");
      for (int i = 0 ; i < this.solutionSpaceDimensions; i++)
         System.out.print (pp.getValue(i)+",");
      System.out.println("");
      
      // Average the objective scores.
      this.objectivePoints.add(pp);
      this.counter++;
      if (this.counter == this.numberFolds) {
         
         // Done.
         this.numberFolds = -1;
         
         // From the sum, compute the mean         
         for (int i = 0 ; i < this.solutionSpaceDimensions; i++)
            this.avgs[i] /= this.numberFolds;
            
         System.out.print ("AverageModelScores: Produce results -");
         for (int i = 0 ; i < this.solutionSpaceDimensions; i++)
            System.out.print (avgs[i]+",");
         System.out.println("");
         
         // Reset the counter and the object points array.
         this.counter = 0;
         this.objectivePoints.clear();
         
         // create a new table from which we construct a new paramter point
         String [] names = new String [this.solutionSpaceDimensions];
         for (int i = 0; i < this.solutionSpaceDimensions; i++)
            names[i] = pp.getColumnLabel(i);
         int numColumns = names.length;
         Column [] cols = new Column[avgs.length];
         int [] outs = new int [cols.length];
         
         // compute the averages
         for (int i = 0 ; i < avgs.length; i++) {
             double [] vals = new double [1];
             vals [0] = avgs[i];
             DoubleColumn dc = new DoubleColumn(vals);
             dc.setLabel(names[i]);
             cols[i] = dc;
             outs[i] = i;
         }
         ExampleTable eti = new MutableTableImpl(cols).toExampleTable();
         eti.setOutputFeatures(outs);
         ParameterPoint objectivepp = pp.createFromTable(eti); //names, avgs);
         this.pushOutput(objectivepp, 0);
      }
   }
}
