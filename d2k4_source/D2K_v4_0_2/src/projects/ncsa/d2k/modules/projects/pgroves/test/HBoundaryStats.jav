package ncsa.d2k.modules.projects.pgroves.test;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

import ncsa.d2k.modules.projects.pgroves.census.*;
import ncsa.d2k.modules.projects.i2k.common.*;

/*


	@author pgroves
	*/

public class HBoundaryStats extends DataPrepModule 
	implements java.io.Serializable {

	//////////////////////
	//d2k Props
	////////////////////
	
	boolean debug=true;		
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
		HBoundary[] hba=(HBoundary[])pullInput(0);
		int numPoints=0;
		for(int h=0;h<hba.length;h++){
			for(int b=0;b<hba[h].GetNumBnd();b++){
				numPoints+=hba[h].GetNumBndPts(b);
			}
		}
		int numStoredPoints=hba[0].getAllPoints().numpts;
		int hSize=numStoredPoints*2*8;
		hSize+=numPoints*4;

		int uSize=8*2*numPoints;
		System.out.println("NumPts:"+numPoints+", numStored:"+numStoredPoints);
		
		System.out.println("Hsize:"+hSize+"B, USize:"+uSize+"B, diff:"+
			(uSize-hSize)+"B");


		//convert to MB
		double hSizem=((double)hSize)/(1024*1024);
		double uSizem=((double)uSize)/(1024*1024);

		System.out.println("Hsize:"+hSizem+"MB, USize:"+uSizem+"MB, diff:"+
			(uSizem-hSizem)+"MB");
	}
		
	
	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return 	
			"<html><head></head><body><p></p>"+
			""+
			""+
			""+
			""+
			"</body></html>";
	}
	
   	public String getModuleName() {
		return "";
	}
	public String[] getInputTypes(){
		String[] types = {
			"[Lncsa.d2k.modules.projects.pgroves.census.HBoundary:"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: 
				return "";
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
				return "";
			case 1:
				return "";
			case 2:
				return "";
			default: return "No such input";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: 
				return "";
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
				return "";
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
			
					

			

								
	
