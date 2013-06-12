package ncsa.d2k.modules.projects.pgroves.util;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

import java.util.List;


/**
	Takes a mutable table and calls the untransform method provided
	by any reversible transformations the table has brought with itl.
	

	@author pgroves
	*/

public class UntransformTable extends DataPrepModule 
	implements java.io.Serializable {

	//////////////////////
	//d2k Props
	////////////////////
	
	/**Whether to apply and then delete just the last transformation added
		or all of them.*/
	boolean 	allUntransforms = true;
	
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

		MutableTable mt = (MutableTable)pullInput(0);

		List transforms = mt.getTransformations();

		int topOfStack = transforms.size() - 1;
		int stopPoint;
		if(allUntransforms){
			stopPoint = 0;
		}else{
			stopPoint = topOfStack;
		}
		

		for(int i=topOfStack; i >= stopPoint; i--){
			if(transforms.get(i) instanceof ReversibleTransformation){
				ReversibleTransformation rt=
					(ReversibleTransformation)(transforms.get(i));
				rt.untransform(mt);
			}
		}
		if(allUntransforms){
			transforms.clear();
		}else{
			transforms.remove(topOfStack);
		}
		pushOutput(mt, 0);
	}
		
	
	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return 	
			"Applies reversible transformations to a table.+"+
			" Can either reverse only the most recent transformation or all of"+
			" them. If non-reversible transforms have been applied, they will"+
			" simply be skipped, although it should be noted that this will"+
			" likely lead to incorrect results. All transforms that are attemp"+
			"ted to be reversed are removed from the tables list of transforms.";
	}
	
   	public String getModuleName() {
		return "UntransformTable";
	}
	public String[] getInputTypes(){
		String[] types = {
			"ncsa.d2k.modules.core.datatype.table.MutableTable"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: 
				return 
					"A mutable table that has had transformations applied to it.";
			case 1: 
				return "";
			case 2: 
				return "";
			default: return "No such input";
		}
	}
	
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Transformed Table";
			case 1:
				return "";
			case 2:
				return "";
			default: return "No such input";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {
			"ncsa.d2k.modules.core.datatype.table.MutableTable"};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: 
				return "The same table, with the transformations reversed.";
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
				return "Untransformed Table";
			case 1:
				return "";
			case 2:
				return "";
			default: return "No such output";
		}
	}
	
	public PropertyDescription[] getPropertiesDescriptions() {
		PropertyDescription [] pd = new PropertyDescription[1];
		 
		pd[0] = new PropertyDescription(
	   "allUntransforms",
	   "Apply all reverse transformations",
	   "If true, will attempt to reverse all transformations, if false, will" +
	   " only attempt the most recent transformation.");

		return pd;
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
	public boolean getAllUntransforms(){
		return allUntransforms;
	}
	public void setAllUntransforms(boolean b){
		allUntransforms=b;
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
			
					

			

								
	
