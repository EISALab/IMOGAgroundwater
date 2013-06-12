package ncsa.d2k.modules.core.datatype.parameter.impl;

import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.parameter.*;

/**
 * this is the implementation based on the real table.
 * @author Thomas L. Redman
 * @version 1.0
 */
public class ParameterSpaceImpl extends ExampleTableImpl implements ParameterSpace {

	/** the resolution of each property. */
	int [] res = new int[0];

	/** the number of spaces. */
	int numSpaces = 0;

	/** counts of the number of parameters in each subspace. */
	int [] parameterCount = new int[0];

	public ParameterSpaceImpl () {
		this(0);
	}
	public ParameterSpaceImpl (int numColumns) {
		super(numColumns);
	}
	public ParameterSpace createFromTable(MutableTable table) {
		return null;
	}

	/**
	 * Instantiate a ParameterSpace from primative data types.
	 * @param names the names of the parameters.
	 * @param minValues the minimum parameter values.
	 * @param maxValues the maximum parameter values.
	 * @param defaultValues the default parameter settings.
	 * @param resolutions the parameter resolutions in terms of number of intervals.
	 * @param types the type as an integer as defined in ColumnTypes.
	 * @return a ParameterSpace.
	 */
	public void createFromData(String [] names,
										 double [] minValues,
										 double [] maxValues,
										 double [] defaultValues,
										 int    [] resolutions,
										 int    [] types){

		// Given the data create a parameter space object and return it.
		int numColumns = names.length;
		ParameterSpaceImpl spi = this;
		Column [] columns = new Column[numColumns];
		Column addMe = null;

		// Init all the columns of the parameter space.
		for (int i = 0; i < numColumns; i++) {
			switch (types[i]) {
				case ColumnTypes.DOUBLE:
				case ColumnTypes.FLOAT:
					double [] dvals = new double[3];
					dvals[0] = minValues[i];
					dvals[1] = maxValues[i];
					dvals[2] = defaultValues[i];
					addMe = new DoubleColumn(dvals);
					break;
				case ColumnTypes.INTEGER:
				case ColumnTypes.LONG:
					int [] ivals = new int[3];
					ivals[0] = (int)minValues[i];
					ivals[1] = (int)maxValues[i];
					ivals[2] = (int)defaultValues[i];
					addMe = new IntColumn(ivals);
					break;
				case ColumnTypes.BOOLEAN:
					boolean [] bvals = new boolean[3];
					bvals[0] = minValues[i] == 0 ? false : true;
					bvals[1] = maxValues[i] == 0 ? false : true;
					bvals[2] = defaultValues[i] == 0 ? false : true;
					addMe = new BooleanColumn(bvals);
					break;
			}
			addMe.setLabel(names[i]);
			columns[i] = addMe;
		}
		spi.addColumns(columns);
		this.res = resolutions;
		spi.addSubspace(numColumns);
	}

	/**
	 * Add a new subspace to the parameter space.
	 * @param newcols the number of parameters in the new space.
	 */
	private void addSubspace(int newcols) {
		this.numSpaces++;
		int [] newcounts = new int[this.numSpaces];
		System.arraycopy(this.parameterCount, 0, newcounts, 0, this.numSpaces-1);
		this.parameterCount = newcounts;
		this.parameterCount [this.numSpaces-1] = newcols;
	}

	/**
	 * Get the number of parameters that define the space.
	 * @return an int value representing the minimum possible value of the parameter.
	 */
	public int getNumParameters() {
		return this.getNumColumns();
	}

	/**
	 * Get the name of a parameter.
	 * @param parameterIndex the index of the parameter of interest.
	 * @return a string value representing the name of the parameter.
	 */
	public String getName(int parameterIndex) {
		return columns[parameterIndex].getLabel();
	}

	/**
	 * Get the parameter index of that corresponds to the given name.
	 * @return an integer representing the index of the parameters.
	 */
	public int getParameterIndex(String name) {
		for (int i = 0 ; i < this.getNumColumns(); i++) {
			if (this.getColumnLabel(i).equals(name)) return i;
		}
		return -1;
	}

	/**
	 * Get the minimum value of a parameter.
	 * @param parameterIndex the index of the parameter of interest.
	 * @return a double value representing the minimum possible value of the parameter.
	 */
	public double getMinValue(int parameterIndex) {
		return this.getDouble(0, parameterIndex);
	}

	/**
	 * Get the maximum value of a parameter.
	 * @param parameterIndex the index of the parameter of interest.
	 * @return a double value representing the minimum possible value of the parameter.
	 */
	public double getMaxValue(int parameterIndex) {
		return this.getDouble(1, parameterIndex);
	}

	/**
	 * Get the default value of a parameter.
	 * @param parameterIndex the index of the parameter of interest.
	 * @return a double value representing the minimum possible value of the parameter.
	 */
	public double getDefaultValue(int parameterIndex) {
		return this.getDouble(2, parameterIndex);
	}

	/**
	 * Set the minimum value of a parameter.
	 * @param parameterIndex the index of the parameter of interest.
	 */
	public void setMinValue(int parameterIndex, double value){
		this.setDouble(value, 0, parameterIndex);
	}

	/**
	 * Set the maximum value of a parameter.
	 * @param parameterIndex the index of the parameter of interest.
	 * @param value the value of the parameter of interest.
	 */
	public void setMaxValue(int parameterIndex, double value){
		this.setDouble(value, 1, parameterIndex);
	}

	/**
	 * Set the default value of a parameter.
	 * @param parameterIndex the index of the parameter of interest.
	 * @param value the value of the parameter of interest.
	 */
	public void setDefaultValue(int parameterIndex, double value){
		this.setDouble(value, 2, parameterIndex);
	}


	/**
	 * Set the default value of a parameter.
	 * @param parameterIndex the index of the parameter of interest.
	 * @param value the value of the parameter of interest.
	 */
	public void setType(int parameterIndex, int type){
		throw new RuntimeException ("Why would you change the data type?");
	}
	/**
	 * Set the resolution of a parameter.
	 * @param parameterIndex the index of the parameter of interest.
	 * @param value the resolution.
	 */
	public void setResolution(int parameterIndex, int resolution) {
		this.res[parameterIndex] = resolution;
	}

	/**
	 * Get the minimum values of all parameters returned as a ParameterPoint.
	 * @param parameterIndex the index of the parameter of interest.
	 * @return A ParameterPoint representing the minimum possible values of all parameters.
	 */
	public ParameterPoint getMinParameterPoint(){
		double [] vals = new double[this.getNumParameters()];
		String [] labels = new String [this.getNumParameters()];
		for (int i = 0 ; i < vals.length ; i++) {
			vals[i] = this.getMinValue(i);
			labels[i] = this.getName(i);
		}
		ParameterPoint ppi = ParameterPointImpl.getParameterPoint(labels, vals);
		return ppi;
	}

	/**
	 * Get the maximum values of all parameters returned as a ParameterPoint.
	 * @param parameterIndex the index of the parameter of interest.
	 * @return A ParameterPoint representing the maximum possible values of all parameters.
	 */
	public ParameterPoint getMaxParameterPoint(){
		String [] labels = new String [this.getNumParameters()];
		double [] vals = new double[this.getNumParameters()];
		for (int i = 0 ; i < vals.length ; i++) {
			vals[i] = this.getMaxValue(i);
			labels[i] = this.getName(i);
		}
		ParameterPoint ppi = ParameterPointImpl.getParameterPoint(labels, vals);
		return ppi;
	}

	/**
	 * Get the default values of all parameters returned as a ParameterPoint.
	 * @param parameterIndex the index of the parameter of interest.
	 * @return A ParameterPoint representing the default values of all parameters.
	 */
	public ParameterPoint getDefaultParameterPoint(){
		double [] vals = new double[this.getNumParameters()];
		String [] labels = new String [this.getNumParameters()];
		for (int i = 0 ; i < vals.length ; i++) {
			vals[i] = this.getDefaultValue(i);
			labels[i] = this.getName(i);
		}
		ParameterPoint ppi = ParameterPointImpl.getParameterPoint(labels, vals);
		return ppi;
	}

	/**
	 * Get the resolution of a parameter.
	 * @param parameterIndex the index of the parameter of interest.
	 * @return a int value representing the number of intervals between the min and max parameter values.
	 */
	public int getResolution(int parameterIndex){
		return res[parameterIndex];
	}

	/**
	 * Get the type of a parameter.
	 * @param parameterIndex the index of the parameter of interest.
	 * @return a int value representing the type of the parameter value as defined in ColumnTypes.
	 */
	public int getType(int parameterIndex) {
		return columns[parameterIndex].getType();
	}

	/**
	 * Get the number of subspaces that defines the space.
	 * @return a int value representing the number subspaces that define the space.
	 */
	public int getNumSubspaces() {
		return this.numSpaces;
	}

	/**
	 * Get the number of parameters in each subspace.
	 * @return a int array of values the number of parameters defining each subspace.
	 */
	public int getSubspaceNumParameters(int subspaceIndex) {
		return this.parameterCount[subspaceIndex];
	}

	/**
	 * Get the subspace index of a parameter.
	 * @param parameterIndex the index of the parameter of interest.
	 * @return a int value representing the subpace index number of parameter.
	 */
	public int getSubspaceIndex(int parameterIndex) {
		for (int i = 0, counter = 0 ; i < this.parameterCount.length; i++) {
			counter += parameterCount[i];
			if (counter > parameterIndex) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Get the subspace parameter index of a parameter.
	 * @param parameterIndex the index of the parameter of interest.
	 * @return a int value representing the subpace index number of parameter.
	 */
	public int getSubspaceParameterIndex(int parameterIndex) {
		for (int i = 0, counter = 0 ; i < this.parameterCount.length; i++) {
			counter += parameterCount[i];
			if (counter > parameterIndex) {
				return parameterIndex - (counter-parameterCount[i]);
			}
		}
		return -1;
	}

	/**
	 * Get a subspace from the space.
	 * @param subspaceIndex the index of the subspace of interest.
	 * @return a ParameterSpace which defines the indicated subspace.
	 */
	public ParameterSpace getSubspace(int subspaceIndex){

		// First find the offset where the subspace starts.
		if (subspaceIndex >= parameterCount.length)
			return null;

		// Find the start and end of the subspace.
		int start = 0;
		for (int i = 0 ; i < subspaceIndex ; i++) {
			start += parameterCount[i];
		}
		int end = start + parameterCount[subspaceIndex];
		int size = end-start;

		// initialize the data from which we will create the new space.
		double [] mins = new double[size];
		double [] maxs = new double[size];
		double [] defaults = new double[size];
		int [] res = new int[size];
		int [] types = new int[size];
		String [] names = new String[size];

		// init the values.
		for (int i = 0 ;start < end; start++, i++) {
			mins[i] = this.getMinValue(start);
			maxs[i] = this.getMaxValue(start);
			defaults[i] = this.getDefaultValue(start);
			res[i] = this.getResolution(start);
			types[i] = this.getType(start);
			names[i] = this.getName(start);
		}

		// Now we have the data, make the parameter space.
		ParameterSpaceImpl psi = new ParameterSpaceImpl();
		psi.createFromData(names,mins,maxs,defaults,res,types);
		return psi;
	}

	/**
	 * Join two ParameterSpaces to produce a single parameter space. This does a deep
	 * copy.
	 * @param firstSpace the first of the two ParameterSpaces to join.
	 * @param secondSpace the second of the two ParameterSpaces to join.
	 * @return a ParameterSpace which defines the indicated subspace.
	 */
	public ParameterSpace joinSubspaces(ParameterSpace firstSpace, ParameterSpace secondSpace) {
		ParameterSpaceImpl psi = new ParameterSpaceImpl();
		// initialize the data from which we will create the new space.
		int size = firstSpace.getNumParameters() + secondSpace.getNumParameters();
		double [] mins = new double[size];
		double [] maxs = new double[size];
		double [] defaults = new double[size];
		int [] res = new int[size];
		int [] types = new int[size];
		String [] names = new String[size];

		// init the values.
		int counter = 0;
		for (int i = 0 ;i < firstSpace.getNumParameters(); i++, counter++) {
			mins[counter] = firstSpace.getMinValue(i);
			maxs[counter] = firstSpace.getMaxValue(i);
			defaults[counter] = firstSpace.getDefaultValue(i);
			res[counter] = firstSpace.getResolution(i);
			types[counter] = firstSpace.getType(i);
			names[counter] = firstSpace.getName(i);
		}
		for (int i = 0 ;i < secondSpace.getNumParameters(); i++, counter++) {
			mins[counter] = secondSpace.getMinValue(i);
			maxs[counter] = secondSpace.getMaxValue(i);
			defaults[counter] = secondSpace.getDefaultValue(i);
			res[counter] = secondSpace.getResolution(i);
			types[counter] = secondSpace.getType(i);
			names[counter] = secondSpace.getName(i);
		}
		psi.createFromData(names,mins,maxs,defaults,res,types);
		psi.addSubspace(firstSpace.getNumParameters());
		psi.addSubspace(secondSpace.getNumParameters());
		return psi;
	}
}