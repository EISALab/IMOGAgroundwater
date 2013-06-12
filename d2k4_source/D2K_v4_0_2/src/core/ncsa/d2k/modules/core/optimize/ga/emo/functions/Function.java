package ncsa.d2k.modules.core.optimize.ga.emo.functions;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.optimize.ga.*;

/**
 * A function is a calculation made using the decision variables and
 * any other functions on a population.
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public abstract class Function implements java.io.Serializable {
        private String name;

        public Function(String nme) {
                name = nme;
	}                                    	

        public String getName() {
                return name;
        }
        public void setName(String s) {
                name = s;
        }

        public abstract double[] evaluate(Population p, MutableTable mt) 
                throws Exception;

        public abstract void init();
}
