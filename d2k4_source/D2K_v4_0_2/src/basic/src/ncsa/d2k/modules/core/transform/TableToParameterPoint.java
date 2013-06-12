package ncsa.d2k.modules.core.transform;

import ncsa.d2k.core.modules.DataPrepModule;
import ncsa.d2k.modules.core.datatype.table.ExampleTable;
import ncsa.d2k.modules.core.datatype.parameter.impl.ParameterPointImpl;
import ncsa.d2k.modules.core.datatype.parameter.ParameterPoint;

/**
 * Created by IntelliJ IDEA.
 * User: redman
 * Date: Aug 26, 2003
 * Time: 9:44:01 AM
 * To change this template use Options | File Templates.
 */
public class TableToParameterPoint extends DataPrepModule{
	public String getModuleInfo () {
		return "Convert a table to a parameter point. The inputs are assumed to be the coordinates in parameter space.";
	}

	static final String [] ins = {"ncsa.d2k.modules.core.datatype.table.ExampleTable"};
	public String [] getInputTypes() {
		return ins;
	}

	static final String [] outs = {"ncsa.d2k.modules.core.datatype.parameter.ParameterPoint"};
	public String [] getOutputTypes() {
		return outs;
	}
	public String getInputInfo(int i) {
		switch (i) {
			case 0: return "Input table containing the paramter point.";
			default: return "No such input";
		}
	}

	public String getInputName(int i) {
		switch(i) {
			case 0: return "Parameter Point Table";
			default: return "No such input";
		}
	}

	public String getOutputInfo(int i) {
		switch (i) {
			case 0: return "The parameter point found in the table.";
			default: return "No such output";
		}
	}

	public String getOutputName(int i) {
		switch(i) {
			case 0: return "Paramter Point";
			default: return "No such output";
		}
	}
	public void doit () throws Exception {
		ExampleTable et = (ExampleTable) this.pullInput(0);
		int numInputs = et.getNumInputs(0);
		double [] parameters = new double [numInputs];
		String [] labels = new String [numInputs];
		for (int i = 0 ; i < numInputs ; i++) {
			parameters [i] = et.getInputDouble(0, i);
			labels [i] = et.getInputName(i);
		}
		ParameterPoint ppi = ParameterPointImpl.getParameterPoint (labels, parameters);
		this.pushOutput (ppi, 0);
	}
}
