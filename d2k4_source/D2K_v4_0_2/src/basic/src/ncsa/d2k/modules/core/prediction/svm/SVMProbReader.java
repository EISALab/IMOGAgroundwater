package ncsa.d2k.modules.core.prediction.svm;

import java.util.Vector;
import java.io.*;
import java.util.StringTokenizer;
import ncsa.d2k.core.modules.*;
import libsvm.svm_problem;
import libsvm.svm_node;

/**
  This module will read a text file that is already in the standard
  libsvm format and generate the corresponding svm_problem class to be
  used by the libsvm library.<p>

  The standard libsvm format is:<br>
  &nbsp;&nbsp;&nbsp;label index:value index:value ... index:value<p>

  The label is to be an integer and the values are to be reals.  The
  index should start at 1.  Indices that do not appear in the file are
  assumed to carry value 0.

  @author Xiaolei Li
  */
public class SVMProbReader extends InputModule
{
	/* empty constructor */
	public SVMProbReader()
	{
	}
	public String getModuleInfo()
	{
		return "This module will read a text file that is already in " +
			"the standard libsvm format and generate the corresponding "
			+ "svm_problem class to be used by the libsvm library.<p> "
			+ "The standard libsvm format is:<br>" +
			"&nbsp;&nbsp;&nbsp;label index:value index:value ... " +
			"index:value<p> The label is to be an integer and the " +
			"values are to be reals.  The index should start at 1. " +
			"Indices that do not appear in the file are assumed to " +
			"carry value 0.";
	}

	public String getInputInfo(int parm1)
	{
		switch (parm1) {
			case 0:
				return "Input data file name.";
			default:
				return "";
		}
	}

	public String getInputName(int parm1)
	{
		switch (parm1) {
			case 0:
				return "File Name";
			default:
				return "";
		}
	}

	public String[] getInputTypes()
	{
		String[] in = {"java.lang.String"};
		return in ;
	}


	public String getOutputInfo(int parm1)
	{
		switch (parm1) {
			case 0:
				return "Native SVM Problem class.";
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
			String input_file_name = (String) this.pullInput(0);

			/* open the text file */
			BufferedReader fp = new BufferedReader(new FileReader(input_file_name));
			Vector vy = new Vector();
			Vector vx = new Vector();
			int max_index = 0;

			/* read all lines */
			while(true)
			{
				String line = fp.readLine();
				if(line == null) break;

				StringTokenizer st = new StringTokenizer(line," \t\n\r\f:");

				/* the y vector is the class */
				vy.addElement(st.nextToken());

				/* the x vector are all the features */
				int m = st.countTokens()/2;
				svm_node[] x = new svm_node[m];
				for(int j=0;j<m;j++)
				{
					x[j] = new svm_node();
					x[j].index = atoi(st.nextToken());
					x[j].value = atof(st.nextToken());
				}
				if(m>0) max_index = Math.max(max_index, x[m-1].index);
				vx.addElement(x);
			}

			/* now create the problem class from the vectors */
			libsvm.svm_problem prob = new svm_problem();
			prob.l = vy.size();
			prob.x = new svm_node[prob.l][];
			for(int i=0;i<prob.l;i++)
				prob.x[i] = (svm_node[])vx.elementAt(i);
			prob.y = new double[prob.l];
			for(int i=0;i<prob.l;i++)
				prob.y[i] = atof((String)vy.elementAt(i));

			fp.close();

			this.pushOutput(prob, 0);
			this.pushOutput(new Integer(max_index), 1);

		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println(ex.getMessage());
			System.out.println("ERROR: SVMProbReader.doit()");
			throw ex;
		}
	}


        private static double atof(String s)
        {
                if ((s == null) || (s.length() == 0))
                        return 0.0;
                else
                        return Double.valueOf(s).doubleValue();
        }

        private static int atoi(String s)
        {
                if ((s == null) || (s.length() == 0))
                        return 0;
                else
                        return Integer.parseInt(s);
        }
}
