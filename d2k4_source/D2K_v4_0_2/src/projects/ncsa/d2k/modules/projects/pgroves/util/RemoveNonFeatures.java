package ncsa.d2k.modules.projects.pgroves.util;


import ncsa.d2k.core.modules.*;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.parameter.impl.*;

import java.util.ArrayList;


/**
	Removes any columns that aren't part of the input features
	or output features.

	@author pgroves
	@date 05/03/04
*/

public class RemoveNonFeatures extends DataPrepModule 
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

		ExampleTable et = (ExampleTable)pullInput(0);
		
		ArrayList nonFeatures = new ArrayList();

		int[] ins = et.getInputFeatures();
		int[] outs = et.getOutputFeatures();
		int numCols = et.getNumColumns();

		int j, k;

		int[] newIns = new int[ins.length];
		int[] newOuts = new int[outs.length];

		int i = 0;//index into new column[]
		Column[] cols = new Column[ins.length + outs.length];
		for(j = 0; j < ins.length; j++){
			newIns[j] = i;
			cols[i] = ColumnUtilities.copyColumn(et, ins[j]);
			i++;
		}
		for(j = 0; j < outs.length; j++){
			newOuts[j] = i;
			cols[i] = ColumnUtilities.copyColumn(et, outs[j]);
			i++;
		}
		ExampleTableImpl net = null;
		/*if(et instanceof SubsetTableImpl){
			net = new ExampleTableImpl(new MutableTableImpl(cols), 
				((SubsetTableImpl)et).getSubset());
		}else{*/
			net = new ExampleTableImpl(new MutableTableImpl(cols));
		//}
		net.setInputFeatures(newIns);
		net.setOutputFeatures(newOuts);
		net.setTrainingSet(et.getTrainingSet());
		net.setTestingSet(et.getTestingSet());
		net.setLabel(et.getLabel());
		net.setComment(et.getComment());

		if(et instanceof TrainTable){
			net = (ExampleTableImpl)net.getTrainTable();
		}else if(et instanceof TestTable){
			net = (ExampleTableImpl)net.getTestTable();
		}
		/*boolean isNonFeat;
		for(int i = 0; i < numCols; i++){
			isNonFeat = true;
			for(j = 0; j < ins.length && isNonFeat; j++){
				if(ins[j] == i){
					isNonFeat = false;
				}
			}
			for(j = 0; j < outs.length && isNonFeat; j++){
				if(outs[j] == i){
					isNonFeat = false;
				}
			}
			if(isNonFeat){
				nonFeatures.add(new Integer(i));
			}
		}

		for(k = nonFeatures.size() - 1; k >=0; k--){
			et.removeColumn(((Integer)nonFeatures.get(k)).intValue());
		}*/
		

		pushOutput(net, 0);
	}
		
	
	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return 	
			"Takes an example table and removes any columns that aren't"+
			" in the set of input features or output features. Insures"+
			" that inputs come before outputs. Makes a deep copy of all"+
			" data.";
	}
	
   	public String getModuleName() {
		return "RemoveNonFeatures";
	}
	public String[] getInputTypes(){
		String[] types = {
			"ncsa.d2k.modules.core.datatype.table.ExampleTable"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: 
				return "An example table with input and output featues";
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
				return "Original Table";
			case 1:
				return "";
			case 2:
				return "";
			default: return "No such input";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {
			"ncsa.d2k.modules.core.datatype.table.basic.ExampleTableImpl"};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: 
				return "A new table containing the input and output features" +
				" of the input table.";
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
				return "OnlyFeaturesTable";
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
			
					

			

								
	
