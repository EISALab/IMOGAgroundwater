package ncsa.d2k.modules.projects.pgroves.vis.interp;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;


/**
	A D2K module to create and pass a <code>VariogramPanel</code>.


	@author pgroves
	*/

public class MakeVariogramPanel extends DataPrepModule 
	implements java.io.Serializable {

	//////////////////////
	//d2k Props
	////////////////////
	
	boolean debug=false;		
	/////////////////////////
	/// other fields
	////////////////////////

	//////////////////////////
	///d2k control methods
	///////////////////////

	public boolean isReady(){
		return super.isReady();
	}
	public void endExecution(){
		wipeFields();
		super.endExecution();
	}
	public void beginExecution(){
		wipeFields();
		super.beginExecution();
	}
	public void wipeFields(){
	}
	
	/////////////////////
	//work methods
	////////////////////
	/*
		does it
	*/
	public void doit() throws Exception{
		if(debug){
			System.out.println(getAlias()+":Firing");
		}

		Table[] binnedVars = (Table[])pullInput(0);
		Table rawVar = (Table)pullInput(1);

		VariogramPanel varPan = new VariogramPanel(binnedVars, rawVar);
		pushOutput(varPan, 0);
	}
		
	
	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return 	
			"Collects information on a variogram model and creates"+
			" a viewable panel for a visualization. Normally connected"+
			" to ShowPanel to be displayed in D2K."+
			"";
	}
	
   	public String getModuleName() {
		return "MakeVariogramPanel";
	}
	public String[] getInputTypes(){
		String[] types = {
			"[Lncsa.d2k.modules.core.datatype.table.Table:",
			"ncsa.d2k.modules.core.datatype.table.Table"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: 
				return "A collection of experimental variograms, assumed to be " +
				"different sets of binned data";
			case 1: 
				return "The raw experimental variogram, which is the data before " +
				" being binned.";
			case 2: 
				return "";
			default: return "No such input";
		}
	}
	
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Binned Experimental Variograms";
			case 1:
				return "Raw Experimental Variogram";
			case 2:
				return "";
			default: return "No such input";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {
			"ncsa.d2k.modules.projects.pgroves.vis.interp.VariogramPanel"};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: 
				return "A special JPanel that visualizes the variogram.";
			case 1:
				return "";
			case 2:
				return "";
			default: return "No such output";
		}
	}
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "Variogram Visualization";
			case 1:
				return "";
			case 2:
				return "";
			default: return "No such output";
		}
	}		
	////////////////////////////////
	//D2K Property get/set methods
	///////////////////////////////
	public void setDebug(boolean b){
		debug=b;
	}
	public boolean getDebug(){
		return debug;
	}
	/*
	public boolean get(){
		return ;
	}
	public void set(boolean b){
		=b;
	}
	public double  get(){
		return ;
	}
	public void set(double d){
		=d;
	}
	public int get(){
		return ;
	}
	public void set(int i){
		=i;
	}

	public String get(){
		return ;
	}
	public void set(String s){
		=s;
	}
	*/
}
			
					

			

								
	
