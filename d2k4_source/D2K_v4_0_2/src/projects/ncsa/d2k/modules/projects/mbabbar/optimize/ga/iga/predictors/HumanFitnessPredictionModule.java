package ncsa.d2k.modules.projects.mbabbar.optimize.ga.iga.predictors;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.optimize.ga.*;
import ncsa.d2k.modules.core.optimize.ga.emo.*;
/**
	This predicts qualtitative fitnesses or ranks for unranked individuals
        by calling the <code>predictQualfitness</code> method.
*/
abstract public class HumanFitnessPredictionModule extends ncsa.d2k.core.modules.ComputeModule 	{

	//////////////////////////////////
	// Info methods
	//////////////////////////////////
	/**
		This method returns the description of the various inputs.

		@return the description of the indexed input.
	*/
	public String getOutputInfo (int index) {
		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"population\">    <Text>The output population with qualitative fitness values predicted for all individuals. </Text>  </Info></D2K>";
			default: return "No such output";
		}
}

	/**
		This method returns the description of the various inputs.

		@return the description of the indexed input.
	*/
	public String getInputInfo (int index) {
		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"population\">    <Text>input population, with ranked and unranked individuals. </Text>  </Info></D2K>";
			default: return "No such input";
		}
	}

	/**
		This method returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo () {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"Prediction Module\">    <Text>This predicts qualitative fitnesses for unranked individuals. </Text>  </Info></D2K>";
	}

	//////////////////////////////////
	// Type definitions.
	//////////////////////////////////

	public String[] getInputTypes () {
		String[] types = {"ncsa.d2k.modules.core.optimize.ga.Population"};
		return types;
	}

	public String[] getOutputTypes () {
		String[] types = {"ncsa.d2k.modules.core.optimize.ga.Population"};
		return types;
	}

	/**
		Do the prediction for unranked individuals, using the predictQualfitness method.
                Overwrite this method to create modules that use different techniques for predictions.

	*/
	public void doit () throws Exception {
		Population pop = (Population) this.pullInput(0);

                this.predictQualfitness(pop);

		this.pushOutput (pop, 0);
	}

	/**
		Compute the qualitative fitness for a unranked individuals in the population.
		@param popul: the population with ranked and unranked individuals.
                Overwrite this method in your module that inherits this class.
	*/
	abstract public void predictQualfitness (Population popul);
}
