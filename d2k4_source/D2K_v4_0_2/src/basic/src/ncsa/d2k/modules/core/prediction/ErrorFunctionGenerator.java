package ncsa.d2k.modules.core.prediction;


import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ncsa.d2k.core.modules.ComputeModule;
import ncsa.d2k.core.modules.CustomModuleEditor;
import ncsa.d2k.core.modules.PropertyDescription;

public class ErrorFunctionGenerator extends ComputeModule {

  //private String DataFilePath = "c:d2k/misc/dtcheng/allstate/ratios.ser";
  //public  void   setDataFilePath (String value) {this.DataFilePath = value;}
  //public  String getDataFilePath ()          {return this.DataFilePath;}

  private String     ErrorFunctionName = "Absolute";
  public  void    setErrorFunctionName (String value) {       this.ErrorFunctionName = value;}
  public  String     getErrorFunctionName ()          {return this.ErrorFunctionName;}

  /**
   * Return a list of the property descriptions.
   * @return a list of the property descriptions.
   */
  public PropertyDescription [] getPropertiesDescriptions () {
	  PropertyDescription [] pds = new PropertyDescription [1];
	  pds[0] = new PropertyDescription ("errorFunctionName", "Error Function", "The name of the error function, can be Absolute, Classification, Likelihood or Variance.");
	  return pds;
  }


  public String getModuleName() {
		return "Error Function";
	}
  public String getModuleInfo() {
		return "<p>      Overview: This module produces a string that identifies an error       function to"+
			" be used with model builders.    </p>    <p>      Detailed Description: Error functions are"+
			" used to measure the accuracy       of a model. There are a number of different ways the accuracy"+
			" can be       measured or represented. The name of the error function is user       selectable"+
			" via a custom properties editor. The properties editor       "+
			" provides the names of all the supported       error functions. The currently supported"+
			" error functions are <i>Absolute</i>, <i>Classification</i>, <i>Likelihood</i> and <i>Variance</i>."+
			" <BR>The absolute       error is the sum of all the differences in the predicted and actual    "+
			"   values. <BR>Classification will only work if there is one output feature. It       will yield"+
			" 0 if there is no classification error, 1 otherwise.       <BR>Likelihood returns the negative of"+
			" the sum of the log of probabilities       of the actual classes. This is useful when using"+
			" Likelihood to guide the       formation of the Probability Density Function based models. <BR>Variance"+
			"       returns the sum of all the squared differences between predicted and       actual output"+
			" values.    </p>";
	}

  public String getInputName(int i) {
		switch(i) {
			default: return "NO SUCH INPUT!";
		}
	}
  public String getInputInfo(int i) {
		switch (i) {
			default: return "No such input";
		}
	}
  public String[] getInputTypes() {
		String[] types = {		};
		return types;
	}



  public String getOutputName(int i) {
		switch(i) {
			case 0:
				return "Error Function";
			default: return "NO SUCH OUTPUT!";
		}
	}
  public String getOutputInfo(int i) {
		switch (i) {
			case 0: return "<p>      This string identifies the error function by name.    </p>";
			default: return "No such output";
		}
	}
  public String[] getOutputTypes() {
		String[] types = {"ncsa.d2k.modules.core.prediction.ErrorFunction"};
		return types;
	}

  /**
   * return a reference a custom property editor to select the percent test
   * and train.
   * @returna reference a custom property editor
   */
  public CustomModuleEditor getPropertyEditor() {
	  return new SetErrorFunction();
  }


  public void doit() throws Exception {

	ErrorFunction errorFunction = new ErrorFunction(ErrorFunction.getErrorFunctionIndex(ErrorFunctionName));

	this.pushOutput(errorFunction, 0);

	}


  /**
   * This panel displays the editable properties of the SimpleTestTrain modules.
   * @author Thomas Redman
   */
  class SetErrorFunction extends JPanel implements CustomModuleEditor {
	final String [] errors = {"Absolute","Classification","Likelihood","Variance"};
	JComboBox errorsSelection = new JComboBox(errors);
	SetErrorFunction() {
		JLabel tt = new JLabel("Error Function");
		tt.setToolTipText(ErrorFunctionGenerator.this.getPropertiesDescriptions()[0].getDescription());
		this.add ("West",tt);
		errorsSelection.setSelectedItem(ErrorFunctionName);
		this.add ("Center",errorsSelection);
	}

	/**
	 * Update the fields of the module
	 * @return a string indicating why the properties could not be set, or null if successfully set.
	 */
	public boolean updateModule() throws Exception {
		String newError = (String) errorsSelection.getSelectedItem();
		if (ErrorFunctionName.equals(newError)) {
			return false;
		}

		// we have a new error function name.
		ErrorFunctionName = newError;
		return true;
	}
  }
}
