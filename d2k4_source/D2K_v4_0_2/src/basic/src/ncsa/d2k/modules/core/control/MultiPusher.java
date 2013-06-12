package ncsa.d2k.modules.core.control;

import ncsa.d2k.core.modules.*;
/**
	MultiPusher.java

	pushes the input object out N times, where N is a property
	or input

	@author Peter Groves
	7/15/01

	Revised 04/06/03 by pgroves
*/
public class MultiPusher extends ncsa.d2k.core.modules.DataPrepModule
	implements java.io.Serializable
{


	//////////////////////
	//d2k Props
	////////////////////

	/** the number of times to push the object*/
	int N=4;


	/**false- will wait for and use the Integer in input 1
		true - will use the N in properties*/
	private boolean usePropNValue=false;

	boolean debug=false;
	/////////////////////////
	/// other fields
	////////////////////////
	/** the current number of times this module has fired, indicating the
	number of times the input object has been pushed*/
	int numFires=0;

	/** the object that is input (pulled in)*/
	Object obj;

	/** the total number of times this module has fired since the itin began,
		this is used only for debugging*/
	int totalFires=0;

	//////////////////////////
	///d2k control methods
	///////////////////////

	/**isReady()

		At first, checks to see if the object is in and if it should wait for
		input (1) (the Integer N), otherwise returns the superclass's isReady.
		Once it has the object and knows how many times to pass it, returns true
		until it has been triggered all N times.
		*/
	public boolean isReady(){
		if(numFires==0){//this is the 'first' run
			if( (this.getFlags()[0]>0)&&//we have the object to push
				((usePropNValue)||(this.getFlags()[1]>0))){//we have the number of times
				return true;
			}else{
				return false;
			}
		}
		if((0<numFires)&&(numFires<N)){//we have everything, pushing N times
			return true;
		}

		return false;
	}

	public void beginExecution(){
		numFires=0;
		totalFires=0;
		super.beginExecution();
	}

	/////////////////////
	//work methods
	////////////////////

	/**
		does it every time
	*/
	public void doit() throws Exception {
		if(numFires==0){
			obj= pullInput(0);
			if(!usePropNValue){
				Integer I=(Integer)pullInput(1);
				N=I.intValue();
			}
		}


		pushOutput(obj, 0);
		numFires++;
		totalFires++;

		if(debug)
			System.out.println(this.getAlias()+" current numFires:"+numFires+
			"/"+N+", total number fires this execution:"+totalFires);
		if(numFires==N){
			obj=null;
			numFires=0;
		}
	}


	////////////////////////
	/// D2K Info Methods
	/////////////////////

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "MultiPusher";
	}
/**
		This pair returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo() {
		return "Takes any object, pushes it out N times, where N is either set"+
		" as a property or passed in as an input. See property descriptions "+
		" for details on controlling this behavior";
	}


	/**
	 * Return the human readable name of the indexed input.
	 * @param index the index of the input.
	 * @return the human readable name of the indexed input.
	 */
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Object In";
			case 1:
				return "N";
			default: return "NO SUCH INPUT!";
		}
	}

	/**
		This pair returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {
		switch (index) {
			case 0:
				return "The object to be passed multiple times";
			case 1:
				return "The number of times to pass the input object. This module"+
					" must receive exactly one Integer object for every object"+
					" passed to the first input (unless the property <i> Use the "+
					"value of \"N\" from the properties</i> is set to TRUE)";

			default: return "No such input";
		}
	}

	/**
		This pair returns an array of strings that contains the data types
		for the inputs.
		@return the data types of all inputs.
	*/
	public String[] getInputTypes() {
		String[] types = {"java.lang.Object","java.lang.Integer"};
		return types;
	}
	/**
	 * Return the human readable name of the indexed output.
	 * @param index the index of the output.
	 * @return the human readable name of the indexed output.
	 */
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "Object Out";
			default: return "NO SUCH OUTPUT!";
		}
	}

	/**
		This pair returns the description of the outputs.
		@return the description of the indexed output.
	*/
	public String getOutputInfo(int index) {
		switch (index) {
			case 0: return "One of multiple pushes of the object.";
			default: return "No such output";
		}
	}

	/**
		This pair returns an array of strings that contains the data types
		for the outputs.
		@return the data types of all outputs.
	*/
	public String[] getOutputTypes() {
		String[] types = {"java.lang.Object"};
		return types;
	}

	  /**
     * Return a list of the property descriptions.
     * @return a list of the property descriptions.
     */
    public PropertyDescription[] getPropertiesDescriptions() {
      PropertyDescription[] pds = new PropertyDescription[3];
      pds[0] = new PropertyDescription("usePropNValue",
		 "Use the value of \"N\" from the properties",
        "If TRUE, the number of times the object is pushed will be determined"+
		  " by the <i>N - Number Times to Push</i> property. In this case, the "+
		  "module will not wait for the second input before firing. If FALSE,"+
		  " the value of \"N\" will be determined by the second input.");
      pds[1] = new PropertyDescription("timesToFire",
			"N - Number Times to Push",
       	"The number of times to pass the input object");

      pds[2] = new PropertyDescription("debug",
        "Generate Verbose Output",
        "If true, will write to the console the number of times the module has fired, every "+
		  "time it is fired.");
      return pds;
    }


	////////////////////////////////
	//D2K Property get/set methods
	///////////////////////////////
	public void setUsePropNValue(boolean b){
		usePropNValue=b;
	}
	public boolean getUsePropNValue(){
		return usePropNValue;
	}
	public void setTimesToFire(int i){
		N=i;
	}
	public int getTimesToFire(){
		return N;
	}
	public void setDebug(boolean b){
		debug=b;
	}
	public boolean getDebug(){
		return debug;
	}
}








