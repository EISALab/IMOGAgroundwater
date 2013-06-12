package ncsa.d2k.modules.projects.pgroves.vis.interp;


import ncsa.d2k.core.modules.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import ncsa.d2k.userviews.swing.*;
import java.util.*;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.core.datatype.table.util.TableUtilities;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.parameter.impl.*;

import ncsa.d2k.modules.projects.pgroves.vis.falsecolor.*;
/**
	Allows the user to select a set of models and pushes
	out the parameter points defining them.

	@author pgroves
	@date 02/23/04
	*/

public class ModelArraySelection extends UIModule 
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
		super.endExecution();
	}
	public void beginExecution(){
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
		return new MapSelectView();
    }


  /**
  		Holds the interpolation maps and allows the user
		to add new ones.	
		
	*/
	public class MapSelectView extends JPersistInputPane
		implements java.io.Serializable, ActionListener{
		
		/**a reference to the module that we belong to*/
		ModelArraySelection parent = null;

		/** the pulled in parameter spaces*/
		ParameterSpace[] spaces = null;

		/** the model names */
		String[] modelNames = null;
		
		/**the last pulled in parameter point */
		ParameterPoint currentParams = null;
		/** the last pulled in image */
		Table currentImage = null;

		/**holds the image that is displayed when add map is
		first pressed */ 
		Table dummyImage = null;

		Vector modelPanels;

					
		/** ok button*/
      protected JButton ok;
      /** cancel button*/
      protected JButton cancel;
		/** just the buttons */
		protected JPanel buttonPanel;

		/** the main map viewer panel*/
		protected JPanel mapsPanel;

		/** button to add a new map */
		protected JButton addMap;

		/**the color mapping function*/
		protected FalseColorMap colorMap;
		/** the original data set, which contains well locations and
		the range of predicted values*/
		ExampleTable data;
	
		double prefWidth = 300;
		double prefHeight = 500;
	
		/*	
		ColorBarWidget colorBar;
		SliderControlPanel controls;
		/all the stuff that isn't the image(s) being viewed/
		JPanel controlPanel;

		/ holds the color bar and sliders /
		JPanel colorControlPanel;*/
 
     /**
         Initialize the view.  Insert all components into the view.
         @param mod The ModelArraySelection module that owns us
         */
      public void initView(ViewModule mod) {
         parent = (ModelArraySelection)mod;

			mapsPanel = new JPanel();
			JScrollPane mapScroll = new JScrollPane(mapsPanel);

			// a panel to put the buttons on
			buttonPanel = new JPanel();			
			ok = new JButton("Done");
			ok.addActionListener(this);
			cancel = new JButton("Abort");
			cancel.addActionListener(this);
			buttonPanel.add(cancel);
			buttonPanel.add(ok);

			addMap= new JButton("Add Map");
			addMap.addActionListener(this);
			buttonPanel.add(addMap);

			modelPanels = new Vector();
			

			// add everything to this
			add(mapScroll, BorderLayout.CENTER);
			add(buttonPanel, BorderLayout.SOUTH);			
      }

		
	     /**
         Called whenever inputs arrive to the module. Redraws everything. 
         @param input the Object that is the input
         @param idx the index of the input
         */
      public void setInput(Object input, int idx) {
         switch(idx){
				case 0:{
					System.out.println("idx:"+idx);
					System.out.println(input);
					if(data != null){
						return;
					}else{
						data = (ExampleTable)input;
						initColorMap();
					}
					break;
				}
				case 1: {
					if(spaces != null){
						//must be a dummy input
						return;
					}else{
						spaces = (ParameterSpace[])input;
					}
					break;
				}
				case 2:{
					if(modelNames != null){
						//must be a dummy input
						return;
					}else{
						modelNames = (String[])input;
					}
					break;
				}
				case 3:{
					currentParams = (ParameterPoint)input;
					break;
				}
				case 4:{
					currentImage = (Table)input;
					break;
				}
			}
			addModelPanel();
      }

		protected void addModelPanel(){
			if((currentImage == null) ||
				(currentParams == null) ||
				(data == null) ||
				(modelNames == null) ||
				(spaces == null)){
				return;
			}
			System.out.println("adding panel");
			JPanel pan = new ModelPanel(spaces, modelNames, currentParams,
				currentImage, colorMap, data, this);
			mapsPanel.add(pan);
			modelPanels.add(pan);
			mapsPanel.validate();
			prefWidth = pan.getPreferredSize().getWidth() + 20;
			prefHeight = pan.getPreferredSize().getHeight() + 60;
			this.validate();
			this.repaint();
			wipeFields();
				
		}

		private void wipeFields(){
			dummyImage = currentImage;
			currentImage = null;
			currentParams = null;
		}
		private void wipeAllFields(){
			wipeFields();
			parent = null;
			modelNames = null;
			modelPanels = null;
			spaces = null;
			ok = null;
			cancel = null;
			buttonPanel = null;
			mapsPanel = null;
			addMap = null;
			colorMap = null;
			data = null;
		}

		/** 
			uses information in the data table to initialize the color
			map
		*/
		protected void initColorMap(){
			double[] rng;
			rng = TableUtilities.getMinMax(data, data.getOutputFeatures()[0]);
			colorMap = new LinearColorMap(rng[0], rng[1]);
		}
			
      /**
         Perform any clean up to the table and call the finish() method
         on the VerticalTableViewer module.  Since all cells are
         uneditable in this implementation, we simply call the finish()
         method.  A subclass may want to juggle the contents of the table,
         however.
         */
      protected void finishUp() {
			int numModels = modelPanels.size();
			ParameterPoint[] pps = new ParameterPoint[numModels];
			for(int i = 0; i < numModels; i++){
				pps[i] = ((ModelPanel)modelPanels.get(i)).getParameterPoint();
			}
			pushOutput(pps, 1);
			wipeAllFields();	
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
         if(src == ok){
            finishUp();
         }else if(src == cancel){
				wipeAllFields();
            parent.viewCancel();
			}else if(src == addMap){
				currentImage = this.defaultImage();
				currentParams = this.defaultParams();
				System.out.println("ModelPanel count:"+modelPanels.size());
				addModelPanel();
			}else{
				ModelPanel mod = (ModelPanel)((JButton)src).getParent();
				pushOutput(mod.getParameterPoint(), 0);
				mapsPanel.remove(mod);
				
			}
      }


		protected ParameterPoint defaultParams(){
			String[] names = {"ModelType", "blah", "blah", "blah"};
			double[] vals ={0.0, 0.0, 0.0, 0.0};
			return ParameterPointImpl.getParameterPoint(names, vals);
		}

		protected Table defaultImage(){
			double avg = TableUtilities.mean(data, data.getOutputFeatures()[0]);
			int numRows = dummyImage.getNumRows();
			int numCols = dummyImage.getNumColumns();
			
			MutableTable tbl = new MutableTableImpl(numCols);
			int i, j;
			for(i = 0; i < numCols; i++){
				tbl.setColumn(new DoubleColumn(numRows), i);
				for(j = 0; j < numRows; j++){
					tbl.setDouble(avg, j, i);
				}
			}
			return tbl;
		}

		public Dimension getPreferredSize(){
			Dimension d = new Dimension();
			d.setSize(prefWidth, prefHeight);
			return d;
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
			"Provides an interactive means for selecting an array of"+
			" interpolation model parameters."+
			""+
			"";
	}
	
   	public String getModuleName() {
		return "ModelArraySelection";
	}
	public String[] getInputTypes(){
		String[] types = {
			"ncsa.d2k.modules.core.datatype.ExampleTable",
			"[Lncsa.d2k.modules.core.datatype.parameter.ParameterSpace:",
			"[Ljava.lang.String:",
			"[Lncsa.d2k.modules.core.datatype.parameter.ParameterPoint:",
			"ncsa.d2k.modules.core.datatype.table.Table"
			};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0:
				return "The original data set, where the input features are"+
				" x, y coordinates and the output feature is the feature "+
				"of the interpolation map.";
			case 1: 
				return "A collection of parameter spaces, one for each type"+
				" of model. Only need be passed in once per run.";
			case 2: 
				return "Names that correspond to the models defined in "+
				"Model Parameter Spaces. Passed in once per run.";
			case 3: 
				return "A model definition to add to the collection of models.";
			case 4: 
				return "The image corresponding to the model definition of"+
				" Model Parameter Point.";
			default: return "No such input";
		}
	}
	
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Original Data";
			case 1:
				return "Model Parameter Spaces";
			case 2:
				return "Model Names";
			case 3:
				return "Model Parameter Point";
			case 4:
				return "Image Table";
			default: return "No such input";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {
			"ncsa.d2k.modules.core.datatype.parameter.impl.ParameterPointImpl",
			"[Lncsa.d2k.modules.core.datatype.parameter.impl.ParameterPointImpl",
			};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: 
				return "A model definition (including  a model type index)"+
				" to be used in building a map and then passed back into"+
				" Model Parameter Point at the same time as Image Table";
			case 1:
				return "All of the model definitions that were being viewed"+
				" when this module was dismissed.";
			case 2:
				return "";
			default: return "No such output";
		}
	}
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "Parameter Point";
			case 1:
				return "All Parameter Points";
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
			
					

			

								
	
