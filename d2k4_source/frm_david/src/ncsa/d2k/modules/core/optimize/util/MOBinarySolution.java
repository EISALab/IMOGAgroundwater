package ncsa.d2k.modules.core.optimize.util;

/**
	This subclass of BinarySolution allows for multiple objective
	values.
*/
public class MOBinarySolution extends BinarySolution implements MOSolution, java.io.Serializable {

	/** This is the array of objective values. */
	protected double [] objectives;

	/** these are the range objects for the objectives. */
	protected ObjectiveConstraints [] objectiveConstraints;
	
	/**
	 * constructor takes the ranges which are passed to the 
	 * superclass and the number of objective contraints
	 * @param numObjectives the number of objective values.
	 * @param ranges the ranges of parametric values.
	 */
	public MOBinarySolution (BinaryRange [] ranges, ObjectiveConstraints [] oc) {
		super (ranges);
		objectives = new double [oc.length];
		objectiveConstraints = oc;
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
		boolean [] params = (boolean[])this.getParameters ();
		StringBuffer sb = new StringBuffer (1024);
		sb.append ('(');
		for (int i = 0 ; i < params.length ; i++) {
			if (i > 0){
				sb.append (',');
			}
			if(params[i]){	
				sb.append ("1");
			}
			else{
				sb.append("0");	
			}
		}
		sb.append (')');
		sb.append (':');

		for(int i=0; i<objectives.length ;i++) {
			if (i > 0)
				sb.append (',');
			sb.append (Double.toString (objectives [i]));
		}		
		return sb.toString ();
	}		
	
	/*a deep copy*/
	public Object clone(){

		BinaryRange[] newRanges=(BinaryRange[])this.ranges.clone();
		ObjectiveConstraints[] newOcs=(ObjectiveConstraints[])this.objectiveConstraints.clone();
		
		MOBinarySolution newSol=new MOBinarySolution(newRanges, newOcs);
		
		newSol.setParameters(this.parameters.clone());
		
		for(int i=0; i<objectiveConstraints.length; i++){
			newSol.setObjective(i,this.getObjective(i));
		}
		return newSol;

	}
}
