package ncsa.d2k.modules.core.optimize.util.gui;

import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.parameter.impl.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.prediction.*;
import ncsa.d2k.core.modules.PropertyDescription;


/**
 * This module demonstrates just how little is required to develop a space generator
 * for a learnning algorithm.
 * @author Thomas L. Redman
 * @version 1.0
 */
public class ParameterSpaceGenerator extends AbstractParamSpaceGenerator {

	static final String names[] = {"Strong bias", "Weak bias", "Random Gen"};
	static final double min[] = {-1.0D, 0.0D, 100.0D};
	static final double max[] = {1.0D, 5.0D, 1000.0D};
	static final double def[] = {0.0D, 1.0D, 500.0D};
	static final int res[] = {10, 6, 100};
	static final int types[] = {ColumnTypes.FLOAT, ColumnTypes.FLOAT, ColumnTypes.INTEGER};
	/**
	 * Returns a reference to the developer supplied defaults. These are
	 * like factory settings, absolute ranges and definitions that are not
	 * mutable.
	 * @return the factory settings space.
	 */
	protected ParameterSpace getDefaultSpace() {
		ParameterSpaceImpl psi =  new ParameterSpaceImpl();
		psi.createFromData(names, min, max, def, res, types);
		return psi;
	}

	/**
	 * REturn a name more appriate to the module.
	 * @return a name
	 */
	public String getModuleName() {
		return "Demo Param Space Generator";
	}

	/**
	 * Return a list of the property descriptions.
	 * @return a list of the property descriptions.
	 */
	public PropertyDescription [] getPropertiesDescriptions () {
		PropertyDescription [] pds = new PropertyDescription [3];
		pds[0] = new PropertyDescription ("strongBias", "Strong Bias", "This is dummy bias number one.");
		pds[1] = new PropertyDescription ("weakBias", "Weak Bias", "Dummy bias number two.");
		pds[2] = new PropertyDescription ("randomGen", "Random Gen", "This is the last bias.");
		return pds;
	}

}
