package ncsa.d2k.modules.core.optimize.util.gui;


import ncsa.d2k.modules.core.optimize.util.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.core.modules.UserView;
import ncsa.d2k.userviews.UserInputPane;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import ncsa.d2k.userviews.swing.*;
import ncsa.d2k.userviews.widgets.swing.*;

/*
	SolutionMaker<br>

	allows a user to create a single solution.
	a space is taken in, the parameter names
	and ranges are displayed and the user
	is allowed to change the value of the parameter
	that will be in the solution that is passed out.
	no error checking is done to see if the input value
	is in the range

	@author pgroves
	*/

public class SolutionMaker extends UIModule
	{

	//////////////////////
	//d2k Props
	////////////////////


	/////////////////////////
	/// other fields
	////////////////////////
	/*this modules instance of ClassView, defined below*/
	ClassView theView;

	//////////////////////////
	///d2k control methods
	///////////////////////

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
		theView=new ClassView();
		return theView;
    }

	/*finishes things up, pushes outputs*/
	public void moduleFinish(Solution sol){
		pushOutput(sol, 0);
		//executionManager.moduleDone(this);
		viewDone("Done");
	}


  /**

  	The actual panel/userview that will be displayed
    */
    public class ClassView extends JUserPane implements ActionListener{

		//buttons at the bottom
		private JButton doneButton;
		private JButton abortButton;
		private JPanel buttonPanel;

		//the parent module
		protected SolutionMaker parentModule;


		//the space object that was pulled in
		protected SolutionSpace space;

		//the panel that holds all the parameters
		protected ParamListPanel paramPanel;

		/**
	   		Initialize the view.  Insert all components into the view.
	   		@param mod The  uimodule that owns us
		*/
		public void initView(ViewModule mod) {

			parentModule = (SolutionMaker)mod;

		}

		/*public Dimension getPreferredSize() {
			return new Dimension(400, 400);
		}*/

		/**
	   		Called whenever inputs arrive to the module.
	   		@param input the Object that is the input
	   		@param idx the index of the input
		*/
		public void setInput(Object obj, int index) {

			space=(SolutionSpace)obj;
			this.removeAll();
			this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

			paramPanel=new ParamListPanel(space.getRanges());
			this.add(paramPanel);

			this.add(makeButtonsPanel());
		}


		public void actionPerformed(ActionEvent e){

			if(e.getSource()==doneButton){
				parentModule.moduleFinish(getSolution());
			}
			if(e.getSource()==abortButton){
				parentModule.viewCancel();
			}

		}
		public Solution getSolution(){
			space.createSolutions(1);
			Solution sol=space.getSolutions()[0];

			double[] params=paramPanel.getParameters();
			for(int i=0; i<space.getRanges().length; i++){

				//using setDoubleParamter so we don't have to worry
				//about types
				sol.setDoubleParameter(params[i], i);
			}
			return sol;
		}

		/** gets a panel that has the abort/done buttons
			on it (and all set up)
			@return a panel w/ abort and done buttons
		*/

		private JPanel makeButtonsPanel(){
			buttonPanel=new JPanel();
			abortButton=new JButton("Abort");
			abortButton.addActionListener(this);
			buttonPanel.add(abortButton);

			doneButton = new JButton("Done");
			doneButton.addActionListener(this);
			buttonPanel.add(doneButton);

			return buttonPanel;

		}

	}

	/////////////////////
	//work methods
	////////////////////
	/*
		does it
	*/
	public void doit() throws Exception{

	}


	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return "<html>  <head>      </head>  <body>    This module is meant to be a generic gui for creatinga single parameter     set for a problem that is set up to use the optimizer objects api. A set     of parameter ranges that are associated with a SolutionSpace object are     used to build a simple ui, and then the user inputparameters are put into     a Solution object and passed. Which type of instance of a solution object     is produced is based on the types of the objects contained in the space     object  </body></html>";
	}

   	public String getModuleName() {
		return "Generic Solution Maker";
	}
	public String[] getInputTypes(){
		String[] types = {"ncsa.d2k.modules.core.optimize.util.SolutionSpace"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: return "The SolutionSpace that we will pick a single point from";
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
		String[] types = {"ncsa.d2k.modules.core.optimize.util.Solution"};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: return "The single point in the solution space (input parameters only)";
			default: return "No such output";
		}
	}
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "Parameter Set";
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







