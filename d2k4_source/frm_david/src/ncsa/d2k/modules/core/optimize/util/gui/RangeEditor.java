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

	Allows a user to change the max and min values of
	a parameter search space defined by a SolutionSpace
	object.

	@author pgroves
	*/

public class RangeEditor extends UIModule
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
	public void moduleFinish(SolutionSpace space){
		pushOutput(space, 0);
	//executionManager.moduleDone(this);
		viewDone("Done");

	}


  /**
    */
    public class ClassView extends JUserPane implements ActionListener{

		//buttons at the bottom
		private JButton doneButton;
		private JButton abortButton;
		private JPanel buttonPanel;

		protected RangeEditor parentModule;

		/* the input, and later the output, space*/
		protected SolutionSpace space;

		/*the panel that holds the range info*/
		protected RangeListPanel rangePanel;


		/**
	   		Initialize the view.  Insert all components into the view.
	   		@param mod The  uimodule that owns us
		*/
		public void initView(ViewModule mod) {
			parentModule = (RangeEditor)mod;

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
			this.removeAll();

			space=(SolutionSpace)obj;

			rangePanel=new RangeListPanel(space.getRanges());
			this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			this.add(rangePanel);
			this.add(makeButtonsPanel());
		}


		public void actionPerformed(ActionEvent e){

			if(e.getSource()==doneButton){

				parentModule.moduleFinish(this.getNewSpace());
			}
			if(e.getSource()==abortButton){
				parentModule.viewCancel();
			}

		}

		/**
			returns a new solution space object that is
			the one past in but with the ranges set differently
			@return the SolutionSpace defined by the user's input
					values
		*/
		private SolutionSpace getNewSpace(){
			Range[] newRanges=rangePanel.getRangeArray();
			//have to do this for casting purposes down the line
			for(int i=0; i<newRanges.length; i++){
				space.getRanges()[i]=newRanges[i];
			}
			return space;
		}

		/* gets a panel that has the abort/done buttons
			on it (and all set up)
			@return a panel w/ done and abort buttons
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
		return "<html>  <head>      </head>  <body>    Allows a user to change the min and max values of aparameter/bias search     space in an optimization problem.  </body></html>";
	}

   	public String getModuleName() {
		return "Range Editor";
	}
	public String[] getInputTypes(){
		String[] types = {"ncsa.d2k.modules.core.optimize.util.SolutionSpace"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: return "The Solution Space that defines the ranges of theparameters in an optimization problem";
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
		String[] types = {"ncsa.d2k.modules.core.optimize.util.SolutionSpace"};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: return "A space with the same internal types as the one passed in, but with min/max values that were defined by the user";
			default: return "No such output";
		}
	}
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "Edited Space";
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







