package ncsa.d2k.modules.core.optimize.ga;

import ncsa.d2k.core.modules.*;
/**
        Evaluate the new population. The population object does all the work,
        this module will simply invoke the <code>evaluateAll</code> method of the population.
*/
abstract public class EvaluateModule extends ncsa.d2k.core.modules.ComputeModule 	{

        //////////////////////////////////
        // Info methods
        //////////////////////////////////
        /**
                This method returns the description of the various inputs.

                @return the description of the indexed input.
        */
        public String getOutputInfo (int index) {
                switch (index) {
                        case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"population\">    <Text>The output population has all fitness values computed. </Text>  </Info></D2K>";
                        default: return "No such output";
                }
}

        /**
                This method returns the description of the various inputs.

                @return the description of the indexed input.
        */
        public String getInputInfo (int index) {
                switch (index) {
                        case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"population\">    <Text>input population, unevaluated, fitnesses need computing. </Text>  </Info></D2K>";
                        default: return "No such input";
                }
        }

        /**
                This method returns the description of the module.
                @return the description of the module.
        */
        public String getModuleInfo () {
                return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"Evaluate Population\">    <Text>Compute the performance metrics, average current performance, worst and best current performing individuals. </Text>  </Info></D2K>";
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
                Do the evaluation, using the evaluate function provided by the population
                subclass we received.

                @param outV the array to contain output object.
        */
        public void doit () throws Exception {
                Population pop = (Population) this.pullInput(0);
                for (int i = 0 ; i < pop.size (); i++) {
                        Individual ind = pop.getMember (i);
                        if (ind.isDirty ())
                                this.evaluateIndividual (ind);
                }
                this.pushOutput (pop, 0);
        }

        /**
                Compute the fitness for a single individual.
                @param ind the member to compute the fitness of.
        */
        abstract public void evaluateIndividual (Individual memb);
}
