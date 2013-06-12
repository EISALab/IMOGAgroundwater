package ncsa.d2k.modules.projects.mbabbar.optimize.ga.iga.predictors;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.optimize.ga.*;
import ncsa.d2k.modules.core.optimize.ga.emo.*;
import ncsa.d2k.modules.core.optimize.ga.nsga.*;
import ncsa.d2k.modules.projects.mbabbar.optimize.ga.iga.*;
/**
	This predicts qualtitative fitnesses or ranks for unranked individuals
        by calling the <code>predictQualfitness</code> method.
*/
public class AvgHFPrediction extends HumanFitnessPredictionModule {

        // Number of 'human ranked' nearest Neighbors to be used
        // to calculate the simple average approximate rank for
        // unranked individuals
        int kNN = 1;

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
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"Prediction Module\">    <Text>This assigns qualitative fitnesses for unranked individuals to be equal to some constant value (best, worst or average). </Text>  </Info></D2K>";
	}

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "IGA: Human Fitness Predictor (Avg KNN)";
	}

	/**
         * returns the number of 'human ranked' neighbors for any unranked individual.
         * This number is then used to calculate the surrogate rank for unranked individual.
	 */
	public int getKnn  () {
		return kNN;
	}

        /**
	 * sets the number of nearest neighbors of 'human ranked' individuals closest
         * to the unranked individuals.
	 */
	public void setKnn  (int numNeigh) {
		kNN = numNeigh;
	}
	/**
		Compute the qualitative fitness for a unranked individuals in the population.
		@param popul: the population with ranked and unranked individuals.
                This method takes the nearest K individuals that have been ranked previously
                by human and assigns thae average fitness of them to the unranked individual.
	*/
	public void predictQualfitness (Population pop){

                IGANsgaPopulation popul = (IGANsgaPopulation) pop;

                // if individuals have been ranked by user:
                if (popul.getTotalNumIndivsRankedInArchive() > 0){

                    for (int m =0; m < popul.getNumObjectives(); m++){
                      if (popul.igaQualObj[m] == true) {
                          // initialize average fitness array
                          double [] avgFits = new double [popul.size()];
                          // calculate average fitnesses of ranked (qualitative user ranks) individual
                          for (int i = 0; i < popul.size();i++) {

                              // find euclidean distances between every individual and
                              // previously ranked individual
                              double [] euclidDist = popul.humanEuclidDistGeneAndObjs((IGANsgaSolution)popul.getMember(i), 2.0);

                              // find 'k' human ranked individuals that have the closest euclidean
                              // distance to the unranked individual
                              int kNeigh [] = new int [kNN];
                              for (int j=0; j < kNN; j++){
                                double smallest = euclidDist[0];
                                int smallestId = 0;
                                for (int k =1; k< euclidDist.length; k++){
                                  if (smallest > euclidDist[k]) {
                                    smallest = euclidDist[k];
                                    smallestId = k;
                                  }
                                }
                                // assign some very large number to euclid distance at the kth smallest number found
                                euclidDist[smallestId] = Double.MAX_VALUE;
                                kNeigh[j] = smallestId;
                              }

                              // find average rank of the k 'human ranked' individuals closest to the unranked individual
                              avgFits[i] = 0.0;
                              for (int j=0; j< kNN; j++){
                                  if (popul.getIndInHumanRankedPopulationArchive(kNeigh[j]) instanceof MONumericIndividual) {
                                    avgFits[i] = avgFits[i] + ((MONumericIndividual)popul.getIndInHumanRankedPopulationArchive(kNeigh[j])).getObjective(m);
                                  }
                                  else {
                                    avgFits[i] = avgFits[i] + ((MOBinaryIndividual)popul.getIndInHumanRankedPopulationArchive(kNeigh[j])).getObjective(m);
                                  }
                              }
                              avgFits[i] = avgFits[i] / kNN;
                          }

                          // assign average fitnesses to unranked individuals
                          for (int i = 0; i < popul.size();i++) {
                              if (((IGANsgaSolution)popul.getMember(i)).getRankedIndivFlag() == false) {
                                 ((IGANsgaSolution)popul.getMember(i)).setObjective(m,avgFits[i]);
                              }
                          }

                      } // If popul.igaQualObj[m]
                    } // For m < popul.getNumObjectives()
                }

                // if individuals have not been ranked by the user:
                else {
                    for (int i = 0; i < popul.size();i++) {
                        for (int j =0; j < popul.getNumObjectives(); j++){
                            if (popul.igaQualObj[j] == true){
                                // initialize all individuals to qual fitnesses of value 0
                                ((IGANsgaSolution)popul.getMember(i)).setObjective(j,1);
                            }
                        }

                    }
                }


        }
}
