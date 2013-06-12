package ncsa.d2k.modules.projects.pgroves.test;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

import ncsa.d2k.modules.projects.pgroves.census.*;
import ncsa.d2k.modules.projects.i2k.io.shapefile.ReadDbf;
import ncsa.d2k.modules.projects.i2k.io.BndNeighborsIO;
import ncsa.d2k.modules.projects.i2k.common.*;

/*
	@author pgroves
	*/

public class NeighReadTest extends InputModule 
	{

	//////////////////////
	//d2k Props
	////////////////////
	
	boolean printDbf=true;		
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
		String neighFilename=(String)pullInput(0);
		String dbfFilename=(String)pullInput(1);

		BndNeighbors neighs=(BndNeighbors)BndNeighborsIO.ReadOut(neighFilename);

		ReadDbf dbfReader=new ReadDbf();
		if(printDbf)
			dbfReader.setDebug(true);
		
		Table dbf=dbfReader.readDbf(dbfFilename);

		for(int i=0;i<neighs.getNumBnd();i++){
			System.out.println(dbf.getString(i,0));
			System.out.print("\t");
			for(int j=0;j<neighs.getNumNeighbors(i);j++){
				System.out.print(
					dbf.getString(neighs.getNeighbor(i,j),0));
				System.out.print(",");
			}
			System.out.println("\n");
		}
		
	}
		
	
	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return 	"<html><head></head><body><p></p>"+
		
				"</body></html>";
	}
	
   	public String getModuleName() {
		return "";
	}
	public String[] getInputTypes(){
		String[] types = {"java.lang.String","java.lang.String"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: return "";
			default: return "No such input";
		}
	}
	
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "neighbor filename";
			case 1:
				return "dbf filename";
					
			default: return "NO SUCH INPUT!";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: return "";
			default: return "No such output";
		}
	}
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "";
			default: return "NO SUCH OUTPUT!";
		}
	}		
	////////////////////////////////
	//D2K Property get/set methods
	///////////////////////////////
	public void setPrintDbf(boolean b){
		printDbf=b;
	}
	public boolean getPrintDbf(){
		return printDbf;
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
			
					

			

								
	
