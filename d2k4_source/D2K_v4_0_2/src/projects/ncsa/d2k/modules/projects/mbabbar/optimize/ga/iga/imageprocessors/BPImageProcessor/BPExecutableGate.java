package ncsa.d2k.modules.projects.mbabbar.optimize.ga.iga.imageprocessors.BPImageProcessor;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.optimize.ga.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.core.datatype.table.transformations.*;
import ncsa.d2k.modules.core.optimize.util.*;
import ncsa.d2k.modules.core.optimize.ga.emo.*;
import ncsa.d2k.modules.core.optimize.ga.nsga.*;
import ncsa.d2k.modules.projects.mbabbar.optimize.ga.iga.*;
import java.io.Serializable;

import ncsa.d2k.modules.projects.mbabbar.optimize.ga.iga.imageprocessors.util.*;

/**
 * This acts like a gate to allow the executable that calculates pictures
 * for archived individuals to complete, before it passes the population, etc
 * to next executable that calculates interpolation for new indivduals.
 */

public class BPExecutableGate extends DataPrepModule
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
		IGANsgaPopulation pop = (IGANsgaPopulation) this.pullInput(0);
                MutableTable popTable= (MutableTable) this.pullInput(1);
                FalseColorPanelArray fcPanelArr = (FalseColorPanelArray) this.pullInput(2);

                //if ((pop != null) && (popTable != null) && (fcPanelArr != null)) {
                if ((fcPanelArr != null)) {
		    pushOutput(pop, 0);
                    pushOutput(popTable, 1);
                    pushOutput(fcPanelArr, 2);
                }

	}

	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return
			"<html><head></head><body><p></p>"+
			" This is a gate for inputs and outputs"+
			" that allows one executable to finish before next one begins"+
			""+
			""+
			"</body></html>";
	}

   	public String getModuleName() {
		return "";
	}
	public String[] getInputTypes(){
                String[] types = {"ncsa.d2k.modules.core.optimize.ga.Population",
                      "ncsa.d2k.modules.core.datatype.table.Table",
                      "ncsa.d2k.modules.projects.mbabbar.optimize.ga.iga.imageprocessors.util.FalseColorPanelArray"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"population\">    <Text> population. </Text>  </Info></D2K>";
			case 1: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"Table\">    <Text>The table with population info: quantitative objectives, selected individuals,  images/applets info for selected individuals. </Text>  </Info></D2K>";
                    	case 2:	return "An Array with panels that have the grid image with wells drawn.";
			default: return "No such input";
		}
	}

	public String getInputName(int index) {
		switch(index) {
			case 0: return "IGA Nsga Population";
			case 1: return "IGA Table";
			case 2:	return "Images JPanel Array";
			default: return "No such input";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {"ncsa.d2k.modules.core.optimize.ga.Population",
                      "ncsa.d2k.modules.core.datatype.table.Table",
                      "ncsa.d2k.modules.projects.mbabbar.optimize.ga.iga.imageprocessors.util.FalseColorPanelArray"};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"population\">    <Text> population. </Text>  </Info></D2K>";
			case 1: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"Table\">    <Text>The table with population info: quantitative objectives, selected individuals,  images/applets info for selected individuals. </Text>  </Info></D2K>";
                        case 2:	return "An Array with panels that have the grid image with wells drawn.";
			default: return "No such output";
		}
	}
	public String getOutputName(int index) {
		switch(index) {
			case 0: return "IGA Nsga Population";
			case 1: return "IGA Table";
			case 2: return "Images JPanel Array";
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







