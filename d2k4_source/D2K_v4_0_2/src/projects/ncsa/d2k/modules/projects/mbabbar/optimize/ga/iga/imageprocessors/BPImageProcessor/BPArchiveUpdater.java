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

public class BPArchiveUpdater extends DataPrepModule
	implements java.io.Serializable {

	//////////////////////
	//d2k Props
	////////////////////

	boolean debug=false;
	/////////////////////////
	/// other fields
	////////////////////////
        IGANsgaPopulation pop;
        MutableTable archivesUpdatedRanksTable;

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
		pop = (IGANsgaPopulation) this.pullInput(0);
                archivesUpdatedRanksTable = (MutableTable) this.pullInput(1);

                updateArchiveRanksInPopulation ();
                // if ((pop != null) && (archivesUpdatedRanksTable != null)) {
                if ((this.getFlags()[0] > 0) && (this.getFlags()[1] > 0)) {
                  pushOutput(pop, 0);
                }

	}

        public void updateArchiveRanksInPopulation (){

          for (int i=0; i< archivesUpdatedRanksTable.getNumRows(); i++){
              int archiveID = archivesUpdatedRanksTable.getInt(i,0);
              double newArchiveRank = archivesUpdatedRanksTable.getDouble(i,1);
              if (archiveID != -1) {
                  IGANsgaSolution picInd = (IGANsgaSolution) pop.getIndInHumanRankedPopulationArchive(archiveID);
                  for (int m=0; m < pop.getNumObjectives(); m++) {
                    if (pop.getIgaQualObj()[m] == true) {
                      picInd.setObjective(m,newArchiveRank);
                    }
                  }
              }

          }

        }
	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return
			"<html><head></head><body><p></p>"+
			" This is an updating modules that updates"+
			" the ranks of archived individuals in a population"+
			""+
			""+
			"</body></html>";
	}

   	public String getModuleName() {
		return "";
	}
	public String[] getInputTypes(){
                String[] types = {"ncsa.d2k.modules.core.optimize.ga.Population",
                      "ncsa.d2k.modules.core.datatype.table.Table"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"population\">    <Text> population. </Text>  </Info></D2K>";
			case 1: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"Table\">    <Text>The table with updated ranks of archived individuals. </Text>  </Info></D2K>";

			default: return "No such input";
		}
	}

	public String getInputName(int index) {
		switch(index) {
			case 0: return "IGA Nsga Population";
			case 1: return "Archived Individuals Rank Table";

			default: return "No such input";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {"ncsa.d2k.modules.core.optimize.ga.Population"};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"population\">    <Text> population. </Text>  </Info></D2K>";

			default: return "No such output";
		}
	}
	public String getOutputName(int index) {
		switch(index) {
			case 0: return "IGA Nsga Population";

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







