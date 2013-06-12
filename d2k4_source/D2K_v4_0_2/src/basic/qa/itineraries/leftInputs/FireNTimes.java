package ncsa.d2k.modules.qa;

import ncsa.d2k.core.modules.*;
public class FireNTimes extends ncsa.d2k.core.modules.ComputeModule {

  private String str="default";
  public void setOutputString(String s){str = s;}
  public String getOutputString(){return str;}


  private int times=10;
  public void setTimes(int i){times = i;}
  public int getTimes(){return times;}


  public PropertyDescription[] getPropertiesDescriptions(){
    PropertyDescription[] pd = new PropertyDescription[2];
    pd[0] = new PropertyDescription("outputString", "output string", "this string will be the output of this module");
    pd[1] = new PropertyDescription("times", "number of times to fire", "this is the number of times this module will fire, must be heigher than 1.");
    return pd;
  }

  private int counter;
  public void beginExecution(){counter = 0;}

  public boolean isReady(){return counter < times;}
  public void doit(){

    pushOutput(str, 0);
    counter++;
  }


	/**
	 * returns information about the input at the given index.
	 * @return information about the input at the given index.
	 */
	public String getInputInfo(int index) {
		switch (index) {
			default: return "No such input";
		}
	}

	/**
	 * returns information about the output at the given index.
	 * @return information about the output at the given index.
	 */
	public String getInputName(int index) {
		switch(index) {
			default: return "NO SUCH INPUT!";
		}
	}

	/**
	 * returns string array containing the datatypes of the inputs.
	 * @return string array containing the datatypes of the inputs.
	 */
	public String[] getInputTypes() {
		String[] types = {		};
		return types;
	}

	/**
	 * returns information about the output at the given index.
	 * @return information about the output at the given index.
	 */
	public String getOutputInfo(int index) {
		switch (index) {
			case 0: return "<p>      this string is the same as the string set for the output string attribute    </p>";
			default: return "No such output";
		}
	}

	/**
	 * returns information about the output at the given index.
	 * @return information about the output at the given index.
	 */
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "output string";
			default: return "NO SUCH OUTPUT!";
		}
	}

	/**
	 * returns string array containing the datatypes of the outputs.
	 * @return string array containing the datatypes of the outputs.
	 */
	public String[] getOutputTypes() {
		String[] types = {"java.lang.String"};
		return types;
	}

	/**
	 * returns the information about the module.
	 * @return the information about the module.
	 */
	public String getModuleInfo() {
		return "<p>      this module will fire N times. N being set by the second property.    </p>    <p>      it outputs the string that"+
			" was set before the run of the itinerary.    </p>";
	}

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "Fire N Time";
	}
}
