package ncsa.d2k.modules.core.optimize.ga.emo.functions;

import ncsa.d2k.modules.core.datatype.table.transformations.*;
import ncsa.d2k.modules.core.transform.attribute.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.optimize.ga.*;

/**
 * A function that is calculated on a Table using constructions.
 */
public abstract class ConstructionFunction extends Function {
        private Construction construct;

        public ConstructionFunction(Construction con) {
                super(con.label);
                construct = con;
        }

        public Construction getConstruction() {
                return construct;
        }
        
        /**
         * Apply this function to the table, and return the result as an
         * array of doubles.  Neither the table nor the population is modified.
         * @param p
         * @param populationTable
         * @return
         * @throws java.lang.Exception
         */
        public double[] evaluate(Population p, MutableTable populationTable) 
                throws Exception {
                // apply the construction to the table
                
                ColumnExpression ce = new ColumnExpression(populationTable);
                ce.setExpression(construct.expression);
                Object retVal = ce.evaluate();
                
                // return the double[] result
                return (double[]) retVal;
        }

        public void init() {
                // no nothing here.
        }
}
