package ncsa.d2k.modules.projects.pgroves.vis ;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.userviews.swing.JUserPane;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.gui.JEasyConstrainsPanel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import ncsa.d2k.modules.core.optimize.util.*;
/*
	A simple vis for saving the information of a solution space.
	contains a tabbed pane with the best/worst and constraint info
	and another that has a table showing all the solutions in the
	space

	@author pgroves
	*/

public class OptimizerReport extends VisModule
	{

	//////////////////////
	//d2k Props
	////////////////////


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
		return;
	}
	public void beginExecution(){
		return;
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

		Table vt;
		TableSortPane tsp;


		/**
	   		Initialize the view.  Insert all components into the view.
	   		@param mod The  uimodule that owns us
		*/
		public void initView(ViewModule mod) {
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
			SolutionSpace ss=(SolutionSpace)obj;
			buildSolutionSpaceGui(ss);
		}

		/*
			makes the actual gui components
		*/
		public void buildSolutionSpaceGui(SolutionSpace ss){
			JTabbedPane tabPane=new JTabbedPane();
			this.add(tabPane);

			ss.computeStatistics();
			String info=ss.getSpaceDefinitionString();
			info+=ss.statusString();

			JTextArea spaceInfoArea=new JTextArea(info);
			JScrollPane infoScrollPane=new JScrollPane(spaceInfoArea);
			tabPane.add("Report", infoScrollPane);

			//the second tab of the gui
			JEasyConstrainsPanel solutionTablePanel=new JEasyConstrainsPanel();
			solutionTablePanel.setLayout(new BorderLayout());

			//the vt part
			vt=(Table)ss.getTable();

			tabPane.add("Solutions", new TableSortPane((MutableTable)vt));


		}


	}

	/////////////////////
	//work methods
	////////////////////



	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return "<html>  <head>      </head>  <body>    Prepares a brief report based on information in the (evaluated)solution     space  </body></html>";
	}

   	public String getModuleName() {
		return "Optimizer Report Visualization";
	}
	public String[] getInputTypes(){
		String[] types = {"ncsa.d2k.modules.core.optimize.util.SolutionSpace"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: return "The Solution Space object that has been optimized, or at least evaluated";
			default: return "No such input";
		}
	}

	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Space";
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







