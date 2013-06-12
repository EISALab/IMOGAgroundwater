package ncsa.d2k.modules.projects.pgroves.bp;


import ncsa.d2k.core.modules.*;
import java.awt.*;
import javax.swing.*;

import ncsa.d2k.modules.projects.pgroves.vis.falsecolor.*;
import ncsa.d2k.modules.core.datatype.table.Table;

/**
	Applies a false, or pseudo, coloring to a grayscale image. 

	@author pgroves
	@date 01/21/04
	*/

public class FalseColor extends DataPrepModule 
	implements java.io.Serializable {

	//////////////////////
	//d2k Props
	////////////////////
	
	boolean keepSliderStatePersistent=true;
	boolean autoInitSliderRange=true;

	double initialSliderMin=0.0;
	double initialSliderMax=0.0;
	
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
		Table img = (Table)pullInput(0);
		pushOutput(getFalseColorPanel(img), 0);
		
	}
		
	/**
		Does the main work of creating the entire false color vis
		panel from an ImageObject. This is the method that
		should be used directly
		
	*/	

	public JPanel getFalseColorPanel(Table img) throws Exception{

			
		return new FalseColorPanel(img);
		
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
			"ncsa.d2k.modules.core.datatype.table.Table"};
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
		String[] types = {
			"javax.swing.JPanel"};
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
}//FalseColor
			
					

			

								
	
