package ncsa.d2k.modules.core.optimize.ga.emo.functions;

import ncsa.d2k.modules.core.datatype.table.transformations.*;

/**
 * A variable is neither a fitness function nor a constraint.  Usually used
 * in the calculation of other functions.
 */
public class Variable extends ConstructionFunction {
        public Variable(Construction cons) {
                super(cons);
        }
}
