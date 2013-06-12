package ncsa.d2k.modules.core.optimize.util;

/**
 * This solution object is used in solution spaces where 
 * only one objective function value is needed. The parameters
 * used here are all integer.
 */
public class SOIntSolution extends IntSolution implements SOSolution, java.io.Serializable {

	/** This is the single objective value. */
	private double objective = 0.0;

	/**
	 * constructor takes the ranges which are passed to the 
	 * superclass constructor
	 * @param ranges the ranges of parametric values.
	 */
	public SOIntSolution (IntRange [] ranges) {
		super (ranges);
	}
	
	/**
	 * Return the objective value of the individual.
	 * @returns the fitness.
	 */
	public double getObjective () {
		return objective;
	}


	 /* Set the objective value to fit.
	 * @param fit the new fitness.
	 */
	public void setObjective (double fit) {
		objective = fit;
	}
		/**
		print some representation of this solution.
	*/
	public String toString () {
		int [] params = (int[])this.getParameters ();
		StringBuffer sb = new StringBuffer (1024);
		sb.append ('(');
		for (int i = 0 ; i < params.length ; i++) {
			if (i > 0)
				sb.append (',');
			sb.append (Integer.toString (params [i]));
		}
		sb.append (')');
		sb.append (':');
		sb.append (Double.toString (this.getObjective ()));
		return sb.toString ();
	}			
		/** deep copy*/
	public Object clone(){
		IntRange[] newRanges=(IntRange[])this.ranges.clone();
		SOIntSolution newSol=new SOIntSolution(newRanges);
		newSol.setParameters(this.parameters.clone());
		
		newSol.setObjective(this.getObjective());

		return newSol;
	}	
}
