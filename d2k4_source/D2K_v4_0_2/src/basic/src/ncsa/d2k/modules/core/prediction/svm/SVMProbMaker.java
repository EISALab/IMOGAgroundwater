package ncsa.d2k.modules.core.prediction.svm;

import java.util.Vector;
import java.io.*;
import java.util.StringTokenizer;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import libsvm.svm_problem;
import libsvm.svm_node;

/**
  Creates a svm_problem class to be used by a SVM.  The libsvm library
  reads input data in the format of a svm_problem class.  This module
  will convert an ExampleTable into such a class.  It will also output
  the number of attributes in the input data.

  @author Xiaolei Li
  */

public class SVMProbMaker extends DataPrepModule
{
	/* empty constructor */
	public SVMProbMaker()
	{
	}

	public String getModuleInfo()
	{
		return "Given an ExampleTable, this module will produce the " +
			"corresponding svm_problem class that can be used by the " +
			"SVM library (libsvm).";
	}

	public String getInputInfo(int index)
	{
		switch (index) {
			case 0:
				return "Example table with input and output features assigned.";
			default:
				return "";
		}
	}

	public String getInputName(int index)
	{
		switch (index) {
			case 0:
				return "Example Table";
			default:
				return "";
		}
	}

	public String[] getInputTypes()
	{
		String[] in = {"ncsa.d2k.modules.core.datatype.table.ExampleTable"};
		return in ;
	}


	public String getOutputInfo(int parm1)
	{
		switch (parm1) {
			case 0:
				return "SVM Problem class.";
			case 1:
				return "Number of attributes in problem.";
			default:
				return "";
		}
	}

	public String getOutputName(int parm1)
	{
		switch (parm1) {
			case 0:
				return "SVM Problem";
			case 1:
				return "Number of Attributes";
			default:
				return "";
		}
	}

	public String[] getOutputTypes()
	{
		String[] out = {"libsvm.svm_problem", "java.lang.Integer"};
		return out;
	}

	public void beginExecution()
	{
	}

	public void endExecution()
	{
		super.endExecution();
	}

	protected void doit() throws Exception
	{
		try {
			ExampleTable table  = (ExampleTable) this.pullInput(0);

			/* get the input features */
			int[] inputs = table.getInputFeatures();

			/* get the output feature */
			int[] outputs = table.getOutputFeatures();

			/* there should only be 1 output feature */
			if(outputs.length > 1) {
				System.out.println("Only one output feature is allowed.");
				System.out.println("Building SVM for only the first output variable.");
			}
			else if (outputs.length == 0 || inputs.length == 0) {
				System.out.println("Invalid features.");
			}

			Vector vy = new Vector();
			Vector vx = new Vector();
			int max_index = 0;
			int num_valid_columns = 0;
			int k;

			/* for all given examples */
			for (int i = 0; i < table.getNumRows(); i++) {

				/* the first element should be the classification */
				vy.addElement(table.getString(i, outputs[0]));

				//System.out.print(table.getString(i, outputs[0]) + " ");

				/* create nodes for all input features */
				svm_node[] x = new svm_node[inputs.length];

				num_valid_columns = 0;

				/* for all input attributes in the example */
				for (int j = 0; j < inputs.length; j++) {
					x[j] = new svm_node();

					/* add 1 because SVM starts index at 1 */
					x[j].index = inputs[j] + 1;
					x[j].value = atof(table.getString(i, inputs[j]));

					/* count the number of columns with value > 0 */
					if (x[j].value > 0) num_valid_columns++;

				}

				svm_node[] x_pruned = new svm_node[num_valid_columns];
				k = 0;

				/* now make a sparse version of the input example */
				for (int j = 0; j < num_valid_columns; j++) {
					x_pruned[j] = new svm_node();

					/* find the next non-zero value */
					while (x[k].value == 0) k++;

					x_pruned[j].index = x[k].index;
					x_pruned[j].value = x[k].value;
					k++;

					//System.out.print(x_pruned[j].index + ":" + x_pruned[j].value + " ");
				}

				/* add this particular example to the training set */
				vx.addElement(x_pruned);

				//System.out.println();
			}
			//System.out.println("--------------------------------------------");

			libsvm.svm_problem prob = new svm_problem();
			prob.l = vy.size();

			prob.x = new svm_node[prob.l][];
			for(int i=0;i<prob.l;i++)
				prob.x[i] = (svm_node[])vx.elementAt(i);

			prob.y = new double[prob.l];
			for(int i=0;i<prob.l;i++)
				prob.y[i] = atof((String)vy.elementAt(i));

			this.pushOutput(prob, 0);
			this.pushOutput(new Integer(inputs.length), 1);

		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println(ex.getMessage());
			System.out.println("ERROR: SVMProbMaker.doit()");
			throw ex;
		}
	}

	private static double atof(String s)
	{
		if ((s == null) || (s.length() == 0))
			return 0.0;
		else
			return Double.parseDouble(s);
	}

	private static int atoi(String s)
	{
		if ((s == null) || (s.length() == 0))
			return 0;
		else
			return Integer.parseInt(s);
	}

}
