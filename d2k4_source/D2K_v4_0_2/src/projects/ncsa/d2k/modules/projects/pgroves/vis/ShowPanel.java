package ncsa.d2k.modules.projects.pgroves.vis;


import ncsa.d2k.core.modules.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import ncsa.d2k.userviews.swing.*;
/**
	Displays a passed in JPanel with OK and ABORT buttons added
	below. This code is basically TableViewer with the TableMatrix
	stripped out and replaced by the passed in JPanel.


	@author pgroves
	@date 01/21/04
	*/

public class ShowPanel extends UIModule 
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

	//////////////////////////
	//special ui module methods
	//////////////////////////
	
	/**
	* Not used by this module.
	*/
	public String[] getFieldNameMapping() {
		return null;
	}

	/**
       @return The UserView part of this module.
    */
    public UserView createUserView() {
		return new PanelView();
    }


  /**
  		Holds the buttons and the past in JPanel
    */
	public class PanelView extends JUserPane
		implements java.io.Serializable, ActionListener{
		
		/**a reference to the module that we belong to*/
		ShowPanel parent = null;
		
		/** ok button*/
      protected JButton ok;
      /** cancel button*/
      protected JButton cancel;
 
     /**
         Initialize the view.  Insert all components into the view.
         @param mod The ShowPanel module that owns us
         */
      public void initView(ViewModule mod) {
         parent = (ShowPanel)mod;
      }
	     /**
         Called whenever inputs arrive to the module. Redraws everything. 
         @param input the Object that is the input
         @param idx the index of the input
         */
      public void setInput(Object input, int idx) {
         if(idx == 0) {
            this.removeAll();
            JPanel panelIn = (JPanel)input;
				
            // a panel to put the buttons on
            JPanel buttonPanel = new JPanel();
            ok = new JButton("Done");
            ok.addActionListener(this);
            cancel = new JButton("Abort");
            cancel.addActionListener(this);
            buttonPanel.add(cancel);
            buttonPanel.add(ok);

            // add everything to this
            add(panelIn, BorderLayout.CENTER);
            add(buttonPanel, BorderLayout.SOUTH);
         }
      }
			
      /**
         Perform any clean up to the table and call the finish() method
         on the VerticalTableViewer module.  Since all cells are
         uneditable in this implementation, we simply call the finish()
         method.  A subclass may want to juggle the contents of the table,
         however.
         */
      protected void finishUp() {
         viewDone("Done");
      }

      /**
         This is the ActionListener for the ok and cancel buttons.  The
         finishUp() method is called if ok is pressed.  The viewCancel()
         method of the parent module is called if cancel is
         pressed.
         @param e the ActionEvent
         */
      public void actionPerformed(ActionEvent e) {
         Object src = e.getSource();
         if(src == ok)
            finishUp();
         else if(src == cancel)
            parent.viewCancel();
      }



	}	
	/////////////////////
	//work methods
	////////////////////
	/**
		does not get called 
	*/
	public void doit() throws Exception{
		if(debug){
			System.out.println(getAlias()+":Firing");
		}
	}
		
	
	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return 	
			""+
			""+
			""+
			"";
	}
	
   	public String getModuleName() {
		return "";
	}
	public String[] getInputTypes(){
		String[] types = {"javax.swing.JPanel"};
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
				return "JPanel";
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
			
					

			

								
	
