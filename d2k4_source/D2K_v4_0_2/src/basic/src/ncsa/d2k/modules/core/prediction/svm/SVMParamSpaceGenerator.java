package ncsa.d2k.modules.core.prediction.svm;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.prediction.AbstractParamSpaceGenerator;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.parameter.impl.*;

public class SVMParamSpaceGenerator extends AbstractParamSpaceGenerator 
{
	public static final String SVM_TYPE = "SVM Type";
	public static final String KERNEL_TYPE = "Kernel Type";
	public static final String DEGREE = "Degree of Kernel";
	public static final String GAMMA = "Gamma of Kernel";
	public static final String COEF0 = "Coefficent 0 of Kernel";
	public static final String NU = "Nu of Kernel";
	public static final String CACHE_SIZE = "Cache Size";
	public static final String C = "C of Kernel";
	public static final String EPS = "Stopping Criterion";
	public static final String P = "P of Kernel";
	public static final String SHRINKING = "Shrinking Heuristics";

	/**
	* Returns a reference to the developer supplied defaults. These are
	* like factory settings, absolute ranges and definitions that are not
	* mutable.
	* @return the default settings space.
	*/
	protected ParameterSpace getDefaultSpace() {
		ParameterSpace psi = new ParameterSpaceImpl();
		String[] names = {SVM_TYPE, KERNEL_TYPE, DEGREE, GAMMA, COEF0,
			NU, CACHE_SIZE, C, EPS, P, SHRINKING};
		double[] min = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		double[] max = {4, 3, 9, 5, 0, 0, 0, 0, 0, 0, 0};
		double[] def = {0, 2, 3, 0, 0, 0.5, 40, 1, 0.001, 0.1, 1};
		int[] res = {5, 4, 10, 6, 1, 1, 1, 1, 1, 1, 1};
		int[] types = {ColumnTypes.INTEGER, ColumnTypes.INTEGER,
			ColumnTypes.DOUBLE, ColumnTypes.DOUBLE, ColumnTypes.DOUBLE,
		ColumnTypes.DOUBLE, ColumnTypes.DOUBLE, ColumnTypes.DOUBLE,
		ColumnTypes.DOUBLE, ColumnTypes.DOUBLE, ColumnTypes.INTEGER};
		psi.createFromData(names, min, max, def, res, types);
		return psi;
	}

	public String getModuleName() {
		return "Support Vector Machine Parameter Space Generator";
	}

	/**
	  Returns a list of the property descriptions.
	  @return a list of the property descriptions.
	  */
	public PropertyDescription[] getPropertiesDescriptions() 
	{
		PropertyDescription[] pds = new PropertyDescription[11];

		pds[0] = new PropertyDescription(SVM_TYPE, SVM_TYPE, "Type of the SVM.");
		pds[1] = new PropertyDescription(KERNEL_TYPE, KERNEL_TYPE, "Type of the kernel.");
		pds[2] = new PropertyDescription(DEGREE, DEGREE, "Degree of kernel function, applicable to polynomial kernels only.");
		pds[3] = new PropertyDescription(GAMMA, GAMMA, "Gamma of kernel function.");
		pds[4] = new PropertyDescription(COEF0, COEF0, "Coefficent 0 of kernel function.");
		pds[5] = new PropertyDescription(CACHE_SIZE, CACHE_SIZE, "Cache memory size in MB.");
		pds[6] = new PropertyDescription(EPS, EPS, "Stopping criterion.");
		pds[7] = new PropertyDescription(C, C, "Parameter C of C-SVC, Epsilon-SVR, and nu-SVR.");
		pds[8] = new PropertyDescription(NU, NU, "Parameter nu of nu-SVC, One-class SVM, and nu-SVR.");
		pds[9] = new PropertyDescription(P, P, "Epsilon of loss function in epsilon-SVR.");
		pds[10] = new PropertyDescription(SHRINKING, SHRINKING, "Binary value to turn on/off shrinking heuristics.");

		return pds;
	}

}
