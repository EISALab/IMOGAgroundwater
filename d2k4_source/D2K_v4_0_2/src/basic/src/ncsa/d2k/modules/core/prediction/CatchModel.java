package ncsa.d2k.modules.core.prediction;


import ncsa.d2k.core.modules.*;

/**
 * A very simple ModelSelector that takes a model as input and returns it in the
 * getModel() method, "catching" the model in the Generated Models pane.
 */
public class CatchModel extends ModelSelectorModule  {

	public String getModuleName() {
		return "Catch Model";
	}

	public String getModuleInfo() {
          StringBuffer sb = new StringBuffer( "<p>Overview: ");
          sb.append( "This module takes a predictive model as input and \"catches\" it in the <i>Generated Models</i> ");
          sb.append( "session pane.  ");
          sb.append( "From there, it may be permanently saved by the user for reuse in another session. ");

          sb.append( "</p><p>Description: " );
          sb.append( "D2K itineraries are often used to generate models that predict the value of one or more ");
          sb.append( "target (output) attributes based on the values of one or more input attributes. ");
          sb.append( "These predictive models can be saved and applied at a later time to other datasets " );
          sb.append( "with the same input attributes, generating predictions of the target attribute values ");
          sb.append( "for each of the examples in the dataset. ");

          sb.append( "</p><p>This module can be used to capture a predictive model that has been generated ");
          sb.append( "to the <i>Generated Models</i> session pane.  From there it can be saved permanently ");
          sb.append( "and reloaded in another D2K session. ");

          return sb.toString();
	}

	public String[] getInputTypes() {
		String[] types = {"ncsa.d2k.modules.PredictionModelModule"};
		return types;
	}

        public String getInputName(int i) {
		switch(i) {
                  case 0:
                    return "Prediction Model";
                  default:
                    return "No such input.";
		}
	}

        public String getInputInfo(int i) {
		switch (i) {
                  case 0:
                     return "The Prediction Model to catch and make available in the Generated Models session panel.";
                  default:
                     return "No such input";
		}
	}

	public String[] getOutputTypes() {
		return null;
	}

	public String getOutputName(int i) {
		switch(i) {
                  default:
                    return "No such output";
		}
	}

	public String getOutputInfo(int i) {
		switch (i) {
                  default:
                    return "No such output";
		}
	}


	public void beginExecution() {
		theModel = null;
	}

	private ModelModule theModel;

	public void doit() {

//vered qa:
          System.out.println("CatchModel is executing");
          //end qa

		ModelModule mm = (ModelModule)pullInput(0);
		theModel = mm;
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

// Start QA Comments
//  3/28/03 - QA by Ruth;   This module was previousy called "SimpleModelSelector" but
//  it didn't let the user do any type of selection and the module itself didn't do any
//  selection so it was renamed "CatchModel" to be more easily understood by the user.
//  Following the policy of not passing through an input untouched to the output, the
//  module was changed so that it doesn't have an output port anymore.
//  3/30/03 Ready for Basic
// End of QA comments

