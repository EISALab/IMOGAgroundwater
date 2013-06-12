package ncsa.d2k.modules.projects.pgroves.vis.interp;


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

import ncsa.d2k.userviews.swing.*;

import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.parameter.impl.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.projects.pgroves.vis.falsecolor.*;

/**
	holds the image and model definition panels

	@author pgroves
	@date 02/23/04
	*/

public class ModelPanel extends JPanel implements 
	ActionListener, java.io.Serializable{

	/** just a number to easily id this panel*/
	int id;

	/** Holds the image data, is unique to this model */
	Table img;

	/** has the info on how to color the image*/
	FalseColorMap colorMap;

	/** where to draw the wells */
	Table wellLocations;

	/** the parameter space info for each of the possible model choices */
	ParameterSpace[] spaces;

	/** The parameter point of the currently selected model. This includes
	an id number for the type of model at index 0, followed by the 
	parameters of that kind of a model. */
	ParameterPoint modelParams;

	////////gui components/////////////
	/**choose which type of model */
	JComboBox modelTypeComboBox;

	/**edit the parameters of the selected type of model*/
	ModelParameterPanel paramPanel;

	/**holds the paramPanel*/
	JScrollPane paramScrollPanel;

	/**if pressed, the selected model type and parameters will be
	pushed out to be evaluated and turned into a map */
	JButton updateButton;
	

	/**
		initializes the panel, including the model selection pane and
		the default image and model.

		
	*/	 
	public ModelPanel(ParameterSpace[] psa, String[] modelNames,
		ParameterPoint defaultModel, Table defaultImage, 
		FalseColorMap colorFunction, Table wellLocationTable,
		ActionListener parent){

		colorMap = colorFunction;
		img = defaultImage;
		spaces = psa;
		wellLocations = wellLocationTable;

		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		//the image display

		//make all the wells represented as circles
		int[] allWells	= new int[wellLocations.getNumRows()];
		for(int i = 0; i < wellLocations.getNumRows(); i++){
			allWells[i] = i;
		}
		this.add(new FalseColorPanel(img, /*colorMap, */
			wellLocations, allWells));
		
		//update button
		this.add(Box.createVerticalStrut(15));
		updateButton = (new JButton("Update Map"));/*{
			public int getIdNumber() {
				return id;
			}
		});*/
		updateButton.addActionListener(parent);
		
		this.add(updateButton);
		//the combo box
		this.add(Box.createVerticalStrut(15));
		modelTypeComboBox = new JComboBox(modelNames);
		modelTypeComboBox.setBorder(new TitledBorder(
			new LineBorder(Color.BLACK), "Model Type"));
		modelTypeComboBox.addActionListener(this);
		this.add(modelTypeComboBox);

		//the parameter panel
		paramPanel = getParamPanel(defaultModel);
		paramScrollPanel = new JScrollPane(paramPanel);
		this.add(paramScrollPanel);
		this.setBorder(new LineBorder(Color.BLACK, 5));
		//this.setBorder(new SoftBevelBorder(BevelBorder.RAISED));
		this.validate();
		this.show();
	}

	/**
		this might change to allow the constructor actually set
		a specific set of parameters. For now i'm assuming
		the default will always be a mean model, so this should
		be fine
	*/
	protected ModelParameterPanel getParamPanel(ParameterPoint modelDef){
		int modelType = (int)modelDef.getValue(0);
		System.out.println("numparams in space: " +
			spaces[modelType].getNumParameters());
		for(int i = 0; i < spaces[modelType].getNumParameters(); i++){
			spaces[modelType].setDefaultValue(i, modelDef.getValue(i+1));
		}
		return new ModelParameterPanel(spaces[modelType]);
		
	}
		
	public ParameterPoint getParameterPoint(){
		int modelIdx = modelTypeComboBox.getSelectedIndex();
		int numParams = spaces[modelIdx].getNumParameters() + 1;
		String[] names = new String[numParams];
		double[] vals = new double[numParams];
		names[0] = "ModelType";
		vals[0] = (double)modelIdx;
		for(int i = 1; i < numParams; i++){
			names[i] = spaces[modelIdx].getName(i - 1);
			vals[i] = paramPanel.getValue(i - 1);
		}
		ParameterPoint pp = ParameterPointImpl.getParameterPoint(names, vals);
		return pp;
	}
	
	public void actionPerformed(ActionEvent e){
		System.out.println("ModelPanel: Action");
		Object src = e.getSource();
		if(src == updateButton){
			System.out.println("Calling update");
		} else if(src == modelTypeComboBox){
			this.remove(paramScrollPanel);
			int modelIdx = modelTypeComboBox.getSelectedIndex();
			paramPanel = new ModelParameterPanel(spaces[modelIdx]);
			paramScrollPanel = new JScrollPane(paramPanel);
			this.add(paramScrollPanel);
			this.validate();
		}
		
	}
}
		

		
