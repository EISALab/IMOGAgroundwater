package ncsa.d2k.modules.core.optimize.util;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

public class SOSolutionSpace implements SolutionSpace, java.io.Serializable{

	protected Range[] ranges;

	protected ObjectiveConstraints objConstraints;

	protected SOSolution[] solutions;

	////////////////////////
	///Statistics Fields
	//////////////////////////

	protected int bestSolution;
	protected int worstSolution;

	protected double average;
	protected double convergenceTarget;

	/////////////////////////
	//constructors
	///////////////////////

	public SOSolutionSpace(Range[] rgs, ObjectiveConstraints oc, double target ){
		ranges=rgs;
		objConstraints=oc;
		convergenceTarget=target;


	}

	////////////////////
	//utility functions
	////////////////////
	/*
		initiallizes this solution space's solution array to hold
		the desired number of solutions
	*/

	public void createSolutions(int solutionCount){
		solutions=new SOSolution[solutionCount];

		//first, find out what type of solutions to make by checking if
		//every range is of the same type, and if not, making a mixedsolution
		boolean typeFound=false;
		int type=0;
		while(!typeFound&&(type<4)){
			int tally=0;
			for(int r=0; r<ranges.length; r++){
				if(ranges[r].getType()==type)
					tally++;
			}
			if(tally==ranges.length){
				typeFound=true;
			}else{
				type++;
			}
		}
		//now make the appropriate solutions
		switch(type){
			case(Range.INTEGER):{
				for(int i=0; i<solutionCount; i++){
					solutions[i]=new SOIntSolution((IntRange[])ranges);
				}
				 break;
			}
			case(Range.DOUBLE):{
				for(int i=0; i<solutionCount; i++){
					solutions[i]=new SODoubleSolution((DoubleRange[])ranges);
				}
				 break;
			}
			case(Range.BINARY):{
				for(int i=0; i<solutionCount; i++){
					solutions[i]=new SOBinarySolution((BinaryRange[])ranges);
				}
				 break;
			}
			default:{//must be mixed range types
				for(int i=0; i<solutionCount; i++){
					solutions[i]=new SOMixedSolution(ranges);
				}
			}
		}

	}

	/**
	 * compute statistics that can be used to measure the success of
	 * the population.
	 */
	public void computeStatistics () {

		int length = solutions.length;

		// save the best, worst individuals and the avg performance.
		int worst_individual = 0;
		int best_individual = 0;
		double ave_current_perf = solutions [0].getObjective ();
		for (int i = 1; i < length; i++) {

			// update current statistics
			ave_current_perf += solutions [i].getObjective ();

			// Compare to best performer.
			if (this.compareSolutions (solutions[best_individual],
					solutions[i]) < 0)
				best_individual = i;

			// compare to worst performer.
			if (this.compareSolutions (solutions[worst_individual],
					solutions[i]) > 0)
				worst_individual = i;
		}

		// These values used in selection process.
		this.setAverage (ave_current_perf / length);
		this.setBestSolution (best_individual);
		this.setWorstSolution(worst_individual);
	}


	/**
		Compare one member to another. This requires knowledge of the
		fitness function which cannot be supplied here, hence must be
		provided in a subclass.
		@returns 1 if member indexed a is greater than b,
				0 if they are equal,
				-1 if member indexed by a is less than b.
	*/
	public int compareSolutions (Solution a, Solution b) {
		double af = ((SOSolution)a).getObjective ();
		double bf = ((SOSolution)b).getObjective ();
		return objConstraints.compare (af, bf);
	}

	/**
	 * Returns a representation of of the population in the form of a
	 * table, where each row represents one individual, one parameter per column, and the last
	 * column containing the objective value.
	 * @returns a table represeting the population.
	 */
	public Table getTable () {

		Table vt;

		int rowCount=solutions.length;
		int colCount=ranges.length+1;

		Column[] cols=new Column[colCount];

		//make a column for each range
		for(int i=0; i<ranges.length; i++){
			Column c;

			if(ranges[i] instanceof DoubleRange){
				c=new DoubleColumn(rowCount);
				for(int j=0; j<rowCount; j++){
					double d=solutions[j].getDoubleParameter(i);
					((DoubleColumn)c).setDouble(d, j);
				}
			}else if(ranges[i] instanceof IntRange){
				c=new IntColumn(rowCount);
				for(int j=0; j<rowCount; j++){
					int in=(int)solutions[j].getDoubleParameter(i);
					((IntColumn)c).setInt(in, j);
				}
			}else if(ranges[i] instanceof BinaryRange){
				c=new BooleanColumn(rowCount);
				for(int j=0; j<rowCount; j++){
					boolean b=(solutions[j].getDoubleParameter(i)>0);
					((BooleanColumn)c).setBoolean(b, j);
				}
			}else{//i guess default to a double column
				c=new DoubleColumn(rowCount);
				for(int j=0; j<rowCount; j++){
					double d=solutions[j].getDoubleParameter(i);
					((DoubleColumn)c).setDouble(d, j);
				}
			}

			c.setLabel(ranges[i].getName());
			cols[i]=c;
		}
		//now the objective
		DoubleColumn objC=new DoubleColumn(rowCount);
		objC.setLabel(objConstraints.getName());
		for(int j=0; j<rowCount; j++){
			objC.setDouble(solutions[j].getObjective(), j);
		}
		cols[colCount-1]=objC;

		vt= new MutableTableImpl(cols);
		return vt;
	}
	/**
		quick sort the solutions on the basis of objective, and return an array
		of integers that indicates the order of the Solutions from best to worst.
		@param populus the population to sort.
	*/
	public int [] sortSolutions() {
		int [] order = new int [this.solutions.length];
		for (int i = 0 ; i < order.length ; i++) order [i] = i;
		return this.sortSolutions (solutions, order);
	}

	/**
		quick sort the population on the basis of fitness, and return an array
		of integers that indicates the order of the Individuals from best to worst.
		@param populus the population to sort.
	*/
	public int [] sortSolutions (int [] order) {
		this.quickSort (solutions, 0, order.length-1, order);
		return order;
	}

	/**
		quick sort the population on the basis of fitness, and riseturn an array
		of integers that indicates the order of the Individuals from best to worst.
		@param populus the population to sort.
	*/
	public int [] sortSolutions (Solution [] populus, int [] order) {
		this.quickSort (populus, 0, order.length-1, order);
		return order;
	}

	/**
		This is the recursive quicksort procedure.
		@param populus the individuals to sort
		@param l the left starting point.
		@param r the right end point.
		@param order the list of indices of individuals to sort.
	*/
	private void quickSort(Solution [] populus, int l, int r, int [] order) {

		// This is the (poorly chosen) pivot value.
		/*Solution pivot = (Solution) ((Solution) populus [order [(r + l) / 2]]).clone ();*/
		Solution pivot = populus [order [(r + l) / 2]];
		// from position i=l+1 start moving to the right, from j=r-2 start moving
		// to the left, and swap when the fitness of i is more than the pivot
		// and j's fitness is less than the pivot
		int i = l;
		int j = r;
		while (i <= j) {
			while ((i < r) && (this.compareSolutions (populus [order [i]], pivot) > 0))
				i++;
			while ((j > l) && (this.compareSolutions (populus [order [j]], pivot) < 0))
				j--;
			if (i <= j) {
				int swap = order [i];
				order [i] = order [j];
				order [j] = swap;
				i++;
				j--;
			}
		}

		// sort the two halves
		if (l < j)
			quickSort(populus, l, j, order);
		if (i < r)
			quickSort(populus, i, r, order);
	}

	/**
		Simply shuffle the values in the integer array. This is used to
		shuffle the order of the members.
		@param array the array of integers to shuffle.
	*/
	public void shuffleIndices (int [] array) {

		// randomly shuffle pointers to new structures
		int popSize = array.length;
		for (int i=0; i < array.length; i++) {
			int j = (int) (Math.random () * (popSize-1));
			int temp = array[j];
			array[j] = array[i];
			array[i] = temp;
		}
	}

	///////////////////////////
	///print/status functions
	////////////////////////////

	/**
	 * Construct a string representing the current status of the population, best members,
	 * maybe worst members, whatever.
	 */
	public String statusString () {
		StringBuffer sb = new StringBuffer (1024);
		sb.append ("\nBest performance ");
		sb.append (solutions[bestSolution].getObjective ());
		sb.append (" looking for ");
		sb.append (this.convergenceTarget);
		sb.append ("\n    -");
		sb.append (solutions[bestSolution]);

		sb.append ('\n');
		sb.append ("Worst performance : ");
		sb.append (solutions[worstSolution].getObjective ());
		sb.append ('\n');
		sb.append ("Average performance : ");
		sb.append (this.getAverage());
		sb.append ('\n');
		return sb.toString ();
	}

	/* returns a string with info on the ranges, obj constraints, etc
	*/
	public String getSpaceDefinitionString(){
		StringBuffer sb=new StringBuffer();

		sb.append("\nRanges:\n");
		sb.append("\t_Name_\t_Min_\t_Max_\n");
		for(int i=0; i<ranges.length; i++){
			sb.append("\t");
			sb.append(ranges[i].getName());
			sb.append("\t");
			sb.append(ranges[i].getMin());
			sb.append("\t");
			sb.append(ranges[i].getMax());
			sb.append("\n");
		}
		sb.append("\nObjective Constraint:\n");
		sb.append("\t_Name_\t_Is Maximizing?_\n");
		sb.append("\t");
		sb.append(objConstraints.getName());
		sb.append("\t");
		sb.append(objConstraints.isMaximizing());
		sb.append("\n");
		return(sb.toString());

	}

	/* returns all the solutions as strings, one per line
	*/
	public String getAllSolutionStrings(){
		StringBuffer sb=new StringBuffer();
		for(int i=0; i<solutions.length; i++){
			sb.append(solutions[i]);
		}
		return sb.toString();
	}


	////////////////////////////
	///get/set methods for fields
	////////////////////////////
	public void setRanges(Range[] paramRanges){
		ranges=paramRanges;
	}
	public Range[] getRanges(){
		return ranges;
	}

	public void setObjectiveConstraints(ObjectiveConstraints oc){
		objConstraints=oc;
	}

	public ObjectiveConstraints getObjectiveConstraints(){
		return objConstraints;
	}

	public void setSolutions(Solution[] sols){
		solutions=(SOSolution[])sols;
	}
	public Solution[] getSolutions(){
		return solutions;
	}

	/**
		Set the average objective value, stored for use in other modules
		@param newFitness the new average value
	*/
	public void setAverage (double newAverage) {
		this.average = newAverage;
	}

	/**
		This method returns the average objective value.
		@returns the average value.
	*/
	public double getAverage () {
		return this.average;
	}
		/**
	*/
	public void setBestSolution (int best) {
		this.bestSolution= best;
	}

	/**
	*/
	public int getBestSolution () {
		return bestSolution;
	}

	/**
	*/
	public void setWorstSolution (int wrst) {
		this.worstSolution = wrst;
	}

	/**
	*/
	public int getWorstSolution () {
		return worstSolution;
	}

	public double getConvergenceTarget() {
		return this.convergenceTarget;
	}

	public void setConvergenceTarget(double d) {
		this.convergenceTarget=d;
	}

}
