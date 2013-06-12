package ncsa.d2k.modules.core.optimize.ga.emo.examples;


import ncsa.d2k.modules.core.optimize.ga.*;
import ncsa.d2k.modules.core.optimize.ga.nsga.*;

/**
        Evaluate the new population. The population object does all the work,
        this module will simply invoke the <code>evaluateAll</code> method of the population.
*/
public class EvalFinancialPopulation extends EvaluateModule {

        static final double [] Alpha = {.063, -.031, .102, .123, .203};
        static final double [] Beta = {.476, 1.217, .213, .691, .901};
        static final double [] SigmaEISquared = {.003591, .002622, .001154, .002703, .005978};

                        //////////////////////////////////
        // Info methods
        //////////////////////////////////

        /**
                This method returns the description of the module.
                @return the description of the module.
        */
        public String getModuleInfo () {
                return "<html>  <head>      </head>  <body>    Evalute this F1 population.  </body></html>";
        }

        /**
                Evaluate the individuals in this class. Here we are simply maxing
                the objective function.
        */
        public void evaluateIndividual (Individual member) {
                MONumericIndividual ni = (MONumericIndividual) member;

                // Compute F1
                double [] genes = (double []) ni.getGenes ();

                // If the sum of the parameters is greater than 1, it is a constraint
                // violation.
                double sum = 0.0;
                for (int i = 0 ; i < 5 ; i++)
                        sum += genes [i];
                if (sum > 1.0)
                        //((NsgaSolution)member).setConstraint (sum - 1.0);
                        ((NsgaSolution)member).setConstraint (sum - 1.0, 0);
                else
                        //((NsgaSolution)member).setConstraint (0.0);
                        ((NsgaSolution)member).setConstraint (0.0, 0);

                // Compute f1
                int n = 5;
                double Ep1 = 0.0;
                double Ep2 = 0.0;
                for (int i = 0 ; i < 5 ; i++) {
                        Ep1 += genes [i] * Alpha [i];
                        Ep2 += genes [i] * Beta [i] * .191;
                }
                ni.setObjective (0, Ep1 + Ep2);

                // Compute f2
                double a = 0.0;
                for (int i = 0 ; i < 5 ; i++) {
                        double x = genes [i];
                        double bi = Beta [i];
                        a += (x * x) * (bi * bi) * .001191;
                }

                double b = 0.0;
                for (int i = 0 ; i < 5 ; i++) {
                        for (int j = 0 ; j < 5 ; j++) {
                                if (j == i)
                                        continue;

                                double xi = genes [i];
                                double xj = genes [j];
                            double bi = Beta [i];
                            double bj = Beta [j];
                            b += (xi * xj) * (bi * bj) * .001191;
                        }
                }

                double c = 0.0;
                for (int i = 0 ; i < 5 ; i++) {
                        double x = genes [i];
                        double m2 = SigmaEISquared [i];
                        c += (x * x) * m2;
                }
                ni.setObjective(1, a + b + c);
        }

        /**
         * Return the human readable name of the module.
         * @return the human readable name of the module.
         */
        public String getModuleName() {
                return "Evaluate Population";
        }

        /**
         * Return the human readable name of the indexed input.
         * @param index the index of the input.
         * @return the human readable name of the indexed input.
         */
        public String getInputName(int index) {
                switch(index) {
                        case 0:
                                return "population";
                        default: return "NO SUCH INPUT!";
                }
        }

        /**
         * Return the human readable name of the indexed output.
         * @param index the index of the output.
         * @return the human readable name of the indexed output.
         */
        public String getOutputName(int index) {
                switch(index) {
                        case 0:
                                return "population";
                        default: return "NO SUCH OUTPUT!";
                }
        }
}
