package ncsa.d2k.modules.core.optimize.util;

/**
	This subclass of DoubleSolution allows for multiple objective
	values.
*/
public class MODoubleSolution extends DoubleSolution implements MOSolution, java.io.Serializable {

	/** This is the array of objective values. */
	protected double [] objectives;

	/** this is the objective contraints array. */
	protected ObjectiveConstraints [] objectiveConstraints;

	/**
	 * constructor takes the ranges which are passed to the
	 * superclass and the number of objective contraints
	 * @param numObjectives the number of objective values.
	 * @param ranges the ranges of parametric values.
	 */
	public MODoubleSolution (DoubleRange [] ranges, ObjectiveConstraints [] oc) {
		super (ranges);
		objectiveConstraints = oc;
		objectives = new double [oc.length];
	}


	/**
	 * Return the objective value of the individual.
	 * @returns the fitness.
	 */
	public double getObjective (int i) {
		return objectives [i];
	}

	/**
	 * Set the objective value to fit.
	 * @param i the index of the objective to set.
	 * @param fit the new fitness.
	*/
	public void setObjective (int i, double fit) {
		objectives [i] = fit;
	}
	/**
		print some representation of this solution.
	*/
	public String toString () {
		double [] params = (double[])this.getParameters ();
		StringBuffer sb = new StringBuffer (1024);
		sb.append ('(');
		for (int i = 0 ; i < params.length ; i++) {
			if (i > 0)
				sb.append (',');
			sb.append (Double.toString (params [i]));
		}
		sb.append (')');
		sb.append (':');
		for (int i = 0 ; i < objectives.length ;i++) {
			if (i > 0)
				sb.append (',');
			sb.append (Double.toString (objectives [i]));
		}		
		return sb.toString ();
	}	

	/** deep copy*/
	public Object clone(){
		DoubleRange[] newRanges=(DoubleRange[])this.ranges.clone();
		ObjectiveConstraints[] newOcs=(ObjectiveConstraints[])this.objectiveConstraints.clone();
		
		
		MODoubleSolution newSol=new MODoubleSolution(newRanges, newOcs);
		newSol.setParameters(this.parameters.clone());
		for(int i=0; i<objectiveConstraints.length; i++){
			newSol.setObjective(i,this.getObjective(i));
		}
		return newSol;
	}
}
