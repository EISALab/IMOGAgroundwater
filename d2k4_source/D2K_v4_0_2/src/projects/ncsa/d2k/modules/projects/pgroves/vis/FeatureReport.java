package ncsa.d2k.modules.projects.pgroves.vis;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.userviews.swing.JUserPane;
import ncsa.d2k.modules.core.datatype.table.MutableTable;
import ncsa.gui.JEasyConstrainsPanel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
//import ncsa.d2k.modules.core.optimize.util.*;
/*

	@author pgroves
	*/

public class FeatureReport extends VisModule
	{

	//////////////////////
	//d2k Props
	////////////////////


	/////////////////////////
	/// other fields
	////////////////////////
	/*holds the VT's*/
	Object[] tables;

	/*how many VT's to wait For*/
	int tableCount;

	//////////////////////////
	///d2k control methods
	///////////////////////

	public boolean isReady(){
		return super.isReady();
			
	}
	public void endExecution(){
		super.endExecution();
		wipeFields();
		return;
	}
	public void beginExecution(){
		//tables=new ArrayList();
		return;
	}
	protected void wipeFields(){
		tables=null;
		tableCount=-1;
	}

	//////////////////////////
	//special ui module methods
	//////////////////////////
    /**

    */
    public String[] getFieldNameMapping() {
		return null;
    }

	/**
       @return The UserView part of this module.
    */
    public UserView createUserView() {
		return new ClassView();
    }
  /**
    */
    public class ClassView extends JUserPane
		implements java.io.Serializable{

		FeatureReport parentMod;
		int numTablesIn=0;
		/**
	   		Initialize the view.  Insert all components into the view.
	   		@param mod The  uimodule that owns us
		*/
		public void initView(ViewModule mod) {
			parentMod=(FeatureReport)mod;
		}

		public Dimension getPreferredSize() {
			return new Dimension(500, 600);
		}

		/**
	   		Called whenever inputs arrive to the module.
	   		@param input the Object that is the input
	   		@param idx the index of the input
		*/
		public void setInput(Object obj, int index) {
			switch(index){
				case(0):{
					numTablesIn=((Object[])obj).length;
					parentMod.tables=(Object[])obj;
						buildGui();
				}
			}
		}

		/*
			makes the actual gui components
		*/
		public void buildGui(){
			JTabbedPane tabPane=new JTabbedPane();
			this.add(tabPane);
			for(int i=0; i<numTablesIn; i++){
				MutableTable v=(MutableTable)parentMod.tables[i];
				//v.print();
				tabPane.add((i+1)+" Features", new TableSortPane(v));
			}
			numTablesIn=0;
			this.add(tabPane);
		}

	}

	/////////////////////
	//work methods
	////////////////////



	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return "<html>  <head>      </head>  <body>    Puts VerticalTables into tabbed panes.  </body></html>";
	}

   	public String getModuleName() {
		return "Feature Subset Score Visualization";
	}
	public String[] getInputTypes(){
		String[] types = {"java.lang.Object"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: return "An Object[] holding MutableTable's";
			default: return "No such input";
		}
	}

	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Tables";
			default: return "NO SUCH INPUT!";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {		};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			default: return "No such output";
		}
	}
	public String getOutputName(int index) {
		switch(index) {
			default: return "NO SUCH OUTPUT!";
		}
	}
	////////////////////////////////
	//D2K Property get/set methods
	///////////////////////////////

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
	*/
}







