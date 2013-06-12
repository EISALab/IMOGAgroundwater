package ncsa.d2k.modules.core.prediction.svm;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import libsvm.*;

/**
  Builds a Support Vector Machine (SVM).  This is a wrapper for the
  popular libsvm library (the Java version).  The desired SVM could
  be of several types and kernel types.<p>

  The original libsvm can be found at http://www.csie.ntu.edu.tw/~cjlin/libsvm/.
  It has a modified BSD licence that is compatible with GPL.

  @author Xiaolei Li
  */
public class SVMBuilder extends SVMBuilderOPT
{
	/**
	  Type of SVM.  Has 5 possible choices: C-SVC, nu-SVC, one-class
	  SVM, epsilon-SVR, and nu-SVR.  Default is C-SVC.
	  */
	private int SvmType = 0;

	/**
	  Type of kernel.  Has 4 possible choices: linear, polynomial,
	  radial basis, and sigmoid.  Default is radial basis.
	  */
	private int KernelType = 2;

	/**
	  Degree of the kernel function (if a polynomial kernel is chosen).
	  Default is 3.
	  */
	private double Degree = 3.0;

	/**
	  Gamma of the kernel function.  Applicable for the polynomial,
	  radial, and sigmoid kernels only.  Default is 1 / (number of
	  attributes in the input data).
	  */
	private double Gamma;

	/**
	  Coefficent 0 of the kernel.  Applicable for the polynomial and
	  sigmoid kernels.  Default is 0.
	  */
	private double Coef0 = 0.0;

	/**
	  Cache memory size in MB.  Default is 40MB.
	  */
	private double CacheSize = 40.0;

	/**
	  Tolerance of termination (stopping criterion).  Default is 0.001.
	 */
	private double Eps = 0.001;

	/**
	  Parameter C of C-SVC, Epsilon-SVR, and nu-SVR.  Default is 1.0.
	  */
	private double C = 1.0;

	/* the following 3 parameters are for C-SVC only and haven't been
	 * coded in yet.  It will require a dynamic properties module
	 * because the size of the weight array depends on user input */
	//int nrWeight = 0;
	//int[] weight_label;
	//double[] weight;

	/**
	  Parameter nu of nu-SVC, One-class SVM, and nu-SVR.  Default value
	  is 1.
	  */
	private double Nu = 0.5;

	/**
	  Epsilon in the loss function of epsilon-SVR.  Default is 0.1.
	  */
	private double P = 0.1;

	/**
	  Binary value (0 or 1) to disable or enable the shrinking
	  heuristics.  Default is on or 1.
	  */
	private int Shrinking = 1;





	/* empty constructor */
	public SVMBuilder()
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

			"<b>Properties</b>:" +
			"<ul>" +
				"<li>SVM Type: " +
				"Type of the SVM.  0 is C-SVC, 1 is nu-SVC, 2 is " +
				"one-class SVM, 3 is epsilon-SVR, 4 is nu-SVR.</li>" +

				"<li>Kernel Type: " +
				"Type of the kernel.  0 is linear, 1 is polynomial, 2 is " +
				"radial, and 3 is sigmoid.</li>" +

				"<li>Degree: " +
				"Degree in polynomial kernel function.</li>" +

				"<li>Gamma: " +
				"Gamma in polynomail, radial, or sigmoid kernel function.</li>" +

				"<li>Coef0: " +
				"Coefficient 0 in polynomial or sigmoid kernel function.</li>" +

				"<li>Cache Size: " +
				"Cache memory size in MB.</li>" +

				"<li>Epsilon: " +
				"Tolerance of termination (stopping criterion).</li>" +

				"<li>C: " +
				"Parameter C of C-SVC, Epsilon-SVR, and nu-SVR.</li>" +

				"<li>Nu: " +
				"Parameter nu of nu-SVC, One-class SVM, and nu-SVR.</li>" +

				"<li>P: " +
	  			"Epsilon in the loss function of epsilon-SVR.</li>" +

				"<li>Shrinking: " +
				"Binary value to disable or enable the shrinking heuristics.</li>" +
			"</ul>" +

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
			default:
				return "";
		}
	}

	public String[] getInputTypes()
	{
		String[] in = {"libsvm.svm_problem", "java.lang.Integer"};
		return in;
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
	  Given the properties set by the user, create the native
	  svm_parameter class that is used by svm_train().

	  @param num_attributes The number of attributes in the input data
	  (i.e., size of input vector).

	  @return The parameters chosen for training the SVM encapsulated in
	  the native svm_parameter class.
	  */
	private svm_parameter createParameters(Integer num_attributes)
	{
		svm_parameter param = new svm_parameter();

		param.svm_type = this.SvmType;
		param.kernel_type = this.KernelType;
		param.degree = this.Degree;

		/* if the user entered 0.0, default to 1/k where k is the
		 * number of attributes in the input data. */
		param.gamma = this.Gamma;
		if (this.Gamma == 0.0)
			param.gamma = 1.0 / num_attributes.doubleValue();

		param.coef0 = this.Coef0;
		param.nu = this.Nu;
		param.cache_size = this.CacheSize;
		param.C = this.C;
		param.eps = this.Eps;
		param.p = this.P;
		param.shrinking = this.Shrinking;

		/* these are hard-coded.  should be user inputs. */
		param.nr_weight = 0;
		param.weight_label = new int[0];
		param.weight = new double[0];

		return param;
	}

	public int getSvmType() { return SvmType; }
	public void setSvmType(int val) { SvmType = val; }

	public int getKernelType() { return KernelType; }
	public void setKernelType(int val) { KernelType = val; }

	public double getDegree() { return Degree; }
	public void setDegree(double val) { Degree = val; }

	public double getGamma() { return Gamma; }
	public void setGamma(double val) { Gamma = val; }

	public double getCoef0() { return Coef0; }
	public void setCoef0(double val) { Coef0 = val; }

	public double getCacheSize() { return CacheSize; }
	public void setCacheSize(double val) { CacheSize = val; }

	public double getEps() { return Eps; }
	public void setEps(double val) { Eps = val; }

	public double getC() { return C; }
	public void setC(double val) { C = val; }

	public double getNu() { return Nu; }
	public void setNu(double val) { Nu = val; }

	public double getP() { return P; }
	public void setP(double val) { P = val; }

	public int getShrinking() { return Shrinking; }
	public void setShrinking(int val) { Shrinking = val; }

	public PropertyDescription[] getPropertiesDescriptions()
	{
		PropertyDescription[] pds = new PropertyDescription[11];

		pds[0] = new PropertyDescription("svmType", "SVM Type", "Type of the SVM.");
		pds[1] = new PropertyDescription("kernelType", "Kernel Type", "Type of the kernel.");
		pds[2] = new PropertyDescription("degree", "Degree", "Degree of kernel function, applicable to polynomial kernels only.");
		pds[3] = new PropertyDescription("gamma", "Gamma", "Gamma of kernel function.");
		pds[4] = new PropertyDescription("coef0", "Coef0", "Coefficent 0 of kernel function.");
		pds[5] = new PropertyDescription("cacheSize", "Cache Size", "Cache memory size in MB.");
		pds[6] = new PropertyDescription("eps", "Epsilon", "Stopping criterion.");
		pds[7] = new PropertyDescription("c", "C", "Parameter C of C-SVC, Epsilon-SVR, and nu-SVR.");
		pds[8] = new PropertyDescription("nu", "nu", "Parameter nu of nu-SVC, One-class SVM, and nu-SVR.");
		pds[9] = new PropertyDescription("p", "p", "Epsilon of loss function in epsilon-SVR.");
		pds[10] = new PropertyDescription("shrinking", "Shrinking", "Binary value to turn on/off shrinking heuristics.");

		return pds;
	}
}
