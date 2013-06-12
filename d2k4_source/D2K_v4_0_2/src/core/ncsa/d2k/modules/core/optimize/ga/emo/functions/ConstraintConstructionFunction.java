package ncsa.d2k.modules.core.optimize.ga.emo.functions;

import ncsa.d2k.modules.core.datatype.table.transformations.*;
import ncsa.d2k.modules.core.transform.table.*;
import ncsa.d2k.modules.core.transform.attribute.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.optimize.ga.*;

/**
 * A ConstraintConstructionFunction is a constraint that is represented by 
 * a Construction.  The expression for a constraint looks something like this:<br><br>
 * 
 * width > 50<br><br>
 * 
 * Thus, the constraint evaluates to true or false.  The degree of constraint
 * violation is calculated using the constraint violation functions.  In the
 * example above, the violation function is:<br><br>
 * 
 * width - 50<br><br>
 * 
 * Thus, if width = 75, the resulting violation is 25.
 * 
 * Conjunctions and disjunctions of constraints are also allowed.  For example,
 * this constraint:<br><br>
 * 
 * (width > 50) && (height < 10)<br><br>
 * 
 * generates two constraint violation functions:<br><br>
 * 
 *  width = 50<br>
 *  height - 10<br><br>
 * 
 * When evaluating the constraints, the sum of the violation functions is
 * used as the degree of constraint violation. 
 * 
 * In the evaluate() method, this function first determines whether the
 * constraint is violated or not for each indidvidual, using a FilterExpression.   
 * Then, for each violation function, it determines the degree of violation.  
 * The degrees are added together to get one number for each individual.  If
 * the constraint was violated, the number for that individual will be the
 * sum of the violations, if the constraint was not violated, the number will 
 * be zero.
 */
public class ConstraintConstructionFunction extends ConstructionFunction implements Constraint {
        private Construction[] violationFunctions;
        private double weight;

        public ConstraintConstructionFunction(Construction filter, 
                                              Construction[] viol,
                                              double w) {
                super(filter);
                violationFunctions = viol;
                weight = w;
        }
        
        public double getWeight() {
          return weight;
        }

          public double[] evaluate(Population p, MutableTable mt) 
                throws Exception {          
                // evalute the constraint construction (filter)
                FilterExpression fe = new FilterExpression(mt, true);
                fe.setExpression(getConstruction().expression);
                Object filterRes = fe.evaluate();
                
                boolean[] flags = (boolean[])filterRes;

                // next, evaluate all violation functions
                // for each row, add up the violation
                int numViol = violationFunctions.length;
                
                double[][] violationResults = new double[numViol][];
                
                for(int i = 0; i < numViol; i++) {
                  ColumnExpression ce = new ColumnExpression(mt);  
                  ce.setExpression(violationFunctions[i].expression); 
                  Object ret = ce.evaluate();
                  double[] r = (double[])ret; 
                  violationResults[i] = r;
                }
                
                int size = p.size();
                double[] sums = new double[size];
                for(int i = 0; i < size; i++) {
                  // if the constraint was violated,
                  // then add up the violation results
                  // and set it
                  if(flags[i]) {
                    double sum = 0;
                    for(int j = 0; j < numViol; j++) {
                      sum += violationResults[j][i];  
                    }
                    sums[i] = sum; 
                  }
                  // otherwise set the violation for this row to 0
                  else {
                    sums[i] = 0;  
                  }
                }
                
                // the return value is the violation sums	
                return sums;
        }
}