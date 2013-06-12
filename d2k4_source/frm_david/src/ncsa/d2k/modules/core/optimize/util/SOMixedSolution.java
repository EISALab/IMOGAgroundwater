package ncsa.d2k.modules.core.optimize.util;

/**
 * This solution object is used in solution spaces where 
 * only one objective function value is needed. The parameters
 * used here are mixed types.
 */
public class SOMixedSolution extends MixedSolution implements SOSolution, java.io.Serializable {

	/** This is the single objective value. */
	private double objective = 0.0;

	/**
	 * constructor takes the ranges which are passed to the 
	 * superclass constructor
	 * @param ranges the ranges of parametric values.
	 */
	public SOMixedSolution (Range [] ranges) {
		super (ranges);
	}
	
	/**
	 * Return the objective value of the individual.
	 * @returns the fitness.
	 */
	public double getObjective () {
		return objective;
	}

	/**
	 * Set the objective value to fit.
	 * @param fit the new fitness.
	 */
	public void setObjective (double fit) {
		objective = fit;
	}
	/**
		print some representation of this solution.
	*/
	public String toString () {
		double [] params = (double[])this.getParameters ();
		StringBuffer sb = new StringBuffer (1024);
		sb.append ('(');
		for (int i = 0 ; i < params.length ; i++) {
			if (i > 0){
				sb.append (',');
			}
			if	((ranges[i] instanceof BinaryRange) ||
				(ranges[i] instanceof IntRange)){	
			
					sb.append (Integer.toString ((int)params [i]));
			}else{
				sb.append (Double.toString (params [i]));
			}
		}
		sb.append (')');
		sb.append (':');
		sb.append (Double.toString (objective));
		return sb.toString ();
	}			
		/** deep copy*/
	public Object clone(){
		Range[] newRanges=(Range[])this.ranges.clone();
		
		SOMixedSolution newSol=new SOMixedSolution(newRanges);
		newSol.setParameters(this.parameters.clone());
		newSol.setObjective(this.getObjective());
		return newSol;
	}			
		
}
