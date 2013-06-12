package ncsa.d2k.modules.core.prediction.svm;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.parameter.*;
import libsvm.*;

/**
  Builds a Support Vector Machine (SVM).  This is a wrapper for the
  popular libsvm library (the Java version).  The desired SVM could
  be of several types and kernel types.<p>

  The original libsvm can be found at http://www.csie.ntu.edu.tw/~cjlin/libsvm/.  
  It has a modified BSD licence that is compatible with GPL.

  @author Xiaolei Li
  */
public class SVMBuilderOPT extends ComputeModule
{
	/* empty constructor */
	public SVMBuilderOPT()
	{
	}

	public String getModuleInfo()
	{
		return "<b>Overview</b>: Builds a Support Vector Machine (SVM).<p> " +
			"<b>Detailed description</b>: This is a " +
			"wrapper for the popular libsvm library (the Java " +
			"version).  It builds a Support Vector Machine (SVM) for " +
			"the given sample data.  SVMs are popular in classification"
			+ " due to its marginal maximization property which finds "
			+ "a decision hyperplane that maximizes the distance to"
			+ " the separate classes.  This makes for better " +
			"generalization.<p>" + 
			
			"<b>Properties</b>: None.  All to be passed in via a " +
			"parameter point.<p>" +

			"<b>Restrictions</b>: The SVM can deal with binary or " +
			"multi-class classification.  The classes need to be " +
			"integers and the attribute values need to be numerical.<p>"
			+
			
			"<b>Reference</b>: Chih-Chung Chang and Chih-Jen Lin, LIBSVM : a "
			+ "library for support vector machines, 2001. Software " +
			"available at http://www.csie.ntu.edu.tw/~cjlin/libsvm/." +

			"<p>Note that libsvm has a modified BSD licence that is " +
			"compatible with GPL.";
	}

	/* requires two inputs */
	public String getInputInfo(int index)
	{
		switch (index) {
			case 0:
				return "Training data to be used for building the SVM.";
			case 1:
				return "Number of attributes in a single input.  The " +
					"size of the input vector.";
			case 2:
				return "Control point in the parameter space.";
			default:
				return "";
		}
	}

	/* requires two inputs */
	public String getInputName(int index)
	{
		switch (index) {
			case 0:
				return "Training Data";
			case 1:
				return "Number of Attributes";
			case 2:
				return "Parameter Point";
			default:
				return "";
		}
	}

	public String[] getInputTypes()
	{
		String[] in = {"libsvm.svm_problem", "java.lang.Integer",
			"ncsa.d2k.modules.core.datatype.parameter.ParameterPoint"};
		return in;
	}

	public String getOutputInfo(int index)
	{
		switch (index) {
			case 0:
				return "The built SVM in its native class.";
			default:
				return "";
		}
	}

	public String getOutputName(int index)
	{
		switch (index) {
			case 0:
				return "SVM";
			default:
				return "";
		}
	}

	public String[] getOutputTypes()
	{
		String[] out = {"libsvm.svm_model"};
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
			/* get the training data */
			svm_problem prob = (libsvm.svm_problem) this.pullInput(0);

			/* create the parameters */
			svm_parameter param = createParameters((Integer) this.pullInput(1));

			/* check for errors */
			String error_msg = svm.svm_check_parameter(prob,param);

			if(error_msg != null) {
				System.out.println("ERROR: SVMBuilder.doit()");
				System.err.print(error_msg + "\n");
				System.exit(1);
			}

			/* build the actual SVM */
			svm_model model = svm.svm_train(prob, param);

			this.pushOutput(model, 0);

		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println(ex.getMessage());
			System.out.println("ERROR: SVMBuilder.doit()");
			throw ex;
		}
	}

	/**
	  Given a parameter point, create the native svm_parameter class
	  that is used by svm_train().

	  @param num_attributes The number of attributes in the input data
	  (i.e., size of input vector). 

	  @return The parameters chosen for training the SVM encapsulated in
	  the native svm_parameter class.
	  */
	private svm_parameter createParameters(Integer num_attributes)
		throws Exception
	{
		ParameterPoint pp = (ParameterPoint) this.pullInput(2);

		if (pp == null)
			throw new Exception("SVMBuilderOPT: Invalid parameter point.");

		svm_parameter param = new svm_parameter();

		param.svm_type = (int) pp.getValue(SVMParamSpaceGenerator.SVM_TYPE);
		param.kernel_type = (int) pp.getValue(SVMParamSpaceGenerator.KERNEL_TYPE);
		param.degree = (double) pp.getValue(SVMParamSpaceGenerator.DEGREE);

		/* if the user entered 0.0, default to 1/k where k is the
		 * number of attributes in the input data. */
		param.gamma = (double) pp.getValue(SVMParamSpaceGenerator.GAMMA);
		if (param.gamma == 0.0) 
			param.gamma = 1.0 / num_attributes.doubleValue();

		param.coef0 = (double) pp.getValue(SVMParamSpaceGenerator.COEF0);
		param.nu = (double) pp.getValue(SVMParamSpaceGenerator.NU);
		param.cache_size = (double) pp.getValue(SVMParamSpaceGenerator.CACHE_SIZE);
		param.C = (double) pp.getValue(SVMParamSpaceGenerator.C);
		param.eps = (double) pp.getValue(SVMParamSpaceGenerator.EPS);
		param.p = (double) pp.getValue(SVMParamSpaceGenerator.P);
		param.shrinking = (int) pp.getValue(SVMParamSpaceGenerator.SHRINKING);

		/* these are hard-coded.  should be user inputs. */
		param.nr_weight = 0;
		param.weight_label = new int[0];
		param.weight = new double[0];

		return param;
	} 
}
