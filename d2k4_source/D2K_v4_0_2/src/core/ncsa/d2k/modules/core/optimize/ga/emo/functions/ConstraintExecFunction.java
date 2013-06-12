package ncsa.d2k.modules.core.optimize.ga.emo.functions;

/**
 * A constraint evaluated by a foreign executable.  The executable must
 * produce the degree of constraint violation for each individual.
 */
public class ConstraintExecFunction extends ExecFunction implements Constraint {
  
        private double weight; 
  
        public ConstraintExecFunction(String n, String e, String i, String o, double w) {
                super(n, e, i, o);
                weight = w;
        }
        
        public double getWeight() {
          return weight;    
        }
}
