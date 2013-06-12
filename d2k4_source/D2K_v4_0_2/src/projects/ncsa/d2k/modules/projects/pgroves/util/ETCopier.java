package ncsa.d2k.modules.projects.pgroves.util;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.basic.ExampleTableImpl;
/*
	just calls copy() on an ET and pushes the original ET
	and the copy
	
	@author pgroves 03/06/02
	*/

public class ETCopier extends DataPrepModule 
	implements java.io.Serializable{

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
		super.endExecution();
	}
	public void beginExecution(){
		super.beginExecution();
	}
	
	/////////////////////
	//work methods
	////////////////////
	/*
		does it
	*/
	public void doit() throws Exception{
		ExampleTableImpl et=(ExampleTableImpl)pullInput(0);
		ExampleTableImpl et2=(ExampleTableImpl)et.copy();
		pushOutput(et2, 0);
		pushOutput(et, 1);

	}
		
	
	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return "Makes a Deep Copy of an ExampleTable by calling ExampleTableImpl.copy()";
	}
	
   	public String getModuleName() {
		return "";
	}
	public String[] getInputTypes(){
		String[] s= {"ncsa.d2k.modules.core.datatype.table.basic.ExampleTableImpl"};
		return s;
	}

	public String getInputInfo(int index){
		switch (index){
			case(0): {
				return "";
			}
			default:{
				return "No such input.";
			}
		}
	}
	
	public String getInputName(int index) {
		switch (index){
			case(0): {
				return "Original Table";
			}
			default:{
				return "No such input.";
			}
		}
	}
	public String[] getOutputTypes(){
		String[] s={"ncsa.d2k.modules.core.datatype.table.basic.ExampleTableImpl",
					"ncsa.d2k.modules.core.datatype.table.basic.ExampleTableImpl"};
		return s;
	}

	public String getOutputInfo(int index){
		switch (index){
			case(0): {
				return "";
			}case(1): {
				return "";
			}

			default:{
				return "No such output.";
			}
		}
	}
	public String getOutputName(int index) {
		switch (index){
			case(0): {
				return "Deep Copy";
			}case(1): {
				return "Original Table";
			}

			default:{
				return "No such output.";
			}
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
			
					

			

								
	
