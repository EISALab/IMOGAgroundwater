package ncsa.d2k.modules.core.optimize.ga;

import java.io.Serializable;
import ncsa.d2k.modules.core.optimize.util.*;

/**
	This interface defines the requires of an Individual to be used in
	the GA.
*/
public interface Individual extends Solution {

	/**
		Cross the chromosome at the locations in x.
		@param x the location to cross the chomosome.
		@param ind the individual to cross with.
	*/
	public void crossAt (int [] x, Individual ind);

	/**
		The gene at position x was chosen for mutation
		@param x the location to mutate the chomosome.
	*/
	public void mutateGene (int x);

	/**
		Copy the given individual.
		@param cloneMe the individual to copy.
	*/
	public void copy (Individual cloneMe);

	/**
	 * Subclasses must implement the cloneable.
	 */
	public Object clone ();

	/**
		Copy the given individual.
		@param cloneMe the individual to copy.
	*/
	public Object getGenes ();

	/**
		This method will set the dirty flag, the referernce to the
		array passed in should be the same reference gotten from getGenes.
		@param newGenes the new genes.
	*/
	public void setGenes (Object newGenes);

	/**
		Returns true if the fitness needs computed.
		@returns true if the fitness needs computed.
	*/
	public boolean isDirty ();
    public void printFitness(int i);
}
