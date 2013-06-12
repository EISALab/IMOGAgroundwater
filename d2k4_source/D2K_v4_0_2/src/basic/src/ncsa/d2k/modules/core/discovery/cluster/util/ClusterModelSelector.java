package ncsa.d2k.modules.core.discovery.cluster.util;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.*;
import ncsa.d2k.modules.core.discovery.cluster.*;

/**
 * A very simple ModelSelector that takes a ClusterModel as input and returns it in the
 * getModel() method.  The model is passed as output, unchanged.
 */
public class ClusterModelSelector extends ModelSelectorModule  {

        public String getModuleInfo() {
          String s = "<p>Overview: ";
          s += "A simple ModelSelector that takes a ClusterModel and passes it to the model ";
          s += "jump-up pane. ";
          s += "</p>";

          s += "<p>Detailed Description: ";
          s += "See the user's guide for information on how to work with models in the workspace ";
          s += "jump-up pane.";
          s += "</p>";

          s += "<p>Data Handling: ";
          s += "The ClusterModel is passed as output unchanged. ";
          s += "</p>";

          return s;
        }

        public String[] getInputTypes() {
                String[] types = {"ncsa.d2k.modules.core.discovery.cluster.ClusterModel"};
                return types;
        }

        public String[] getOutputTypes() {
                String[] types = {"ncsa.d2k.modules.core.discovery.cluster.ClusterModel"};
                return types;
        }

        public String getInputInfo(int i) {
                switch (i) {
                        case 0: return "A ClusterModel";
                        default: return "No such input";
                }
        }

        public String getInputName(int i) {
                switch(i) {
                        case 0:
                                return "ClusterModel";
                        default: return "NO SUCH INPUT!";
                }
        }

        public String getOutputInfo(int i) {
                switch (i) {
                        case 0: return "The model that was passed in, unchanged.";
                        default: return "No such output";
                }
        }

        public String getOutputName(int i) {
                switch(i) {
                        case 0:
                                return "ClusterModel";
                        default: return "NO SUCH OUTPUT!";
                }
        }

        public String getModuleName() {
                return "Cluster Model Selector";
        }

        public void beginExecution() {
                theModel = null;
        }

        private ModelModule theModel;

        public void doit() {
                ClusterModel mm = (ClusterModel)pullInput(0);
                theModel = (ModelModule)mm;
                pushOutput(mm, 0);
        }

        /**
     * Return the model that was passed in.
         * @return the model that was passed in.
         */
        public ModelModule getModel() {
        ModelModule mod = theModel;
        theModel = null;
                return mod;
        }
}
