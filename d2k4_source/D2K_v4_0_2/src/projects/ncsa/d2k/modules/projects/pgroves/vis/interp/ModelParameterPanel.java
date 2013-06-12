package ncsa.d2k.modules.projects.pgroves.vis.interp;


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

import ncsa.gui.Constrain;

import ncsa.d2k.modules.core.datatype.parameter.*;

/**
	@author pgroves
	@date 02/23/04
	*/
public class ModelParameterPanel extends JPanel{

	/** hold the individual parameter editors*/
	ParameterEditor[] editors;

	
	public ModelParameterPanel(ParameterSpace ps){
		
		int numParams = ps.getNumParameters();

		editors = new ParameterEditor[numParams];
		
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		for(int i = 0; i < numParams; i++){
			editors[i] = new ParameterEditor(
				ps.getName(i), ps.getMinValue(i), ps.getMaxValue(i), 
				ps.getDefaultValue(i));
			this.add(editors[i]);
		}
	}
		
	/**
		returns the set parameter value at index i.
		*/			
	public double getValue(int i){
		return editors[i].getValue();
	}

	/**
	Edit the individual parameters. displays the suggested min and max
	*/
class ParameterEditor extends JPanel implements java.io.Serializable{
	//private JTextField minText = new JTextField(6);
	//private JTextField maxText = new JTextField(6);
	private JTextField valueText = new JTextField(6);
	private JLabel maxText;
	private JLabel minText;

	private double defaultMin;
	private double defaultMax;
	private double defaultVal;

	ParameterEditor (String name, double min, double max, double val) {

		// Store the defaults.
		this.defaultVal = val;
		this.defaultMin = min;
		this.defaultMax = max;

		this.setLayout(new GridBagLayout());

		// The first row will contain the editors.
		Constrain.setConstraints( this, new JLabel("Min: "), 0, 0, 1, 1, 
			GridBagConstraints.NONE, GridBagConstraints.NORTH, 0.0, 0.0);
		
		minText = new JLabel(Double.toString(min));
		minText.setToolTipText("Minimum value for the parameter");
		//minText.setText(Double.toString(min));
		
		Constrain.setConstraints( this, minText, 1, 0, 1, 1, 
			GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTH, 0.33, 0.0);

		Constrain.setConstraints( this, new JLabel("Max: "), 2, 0, 1, 1, 
			GridBagConstraints.NONE, GridBagConstraints.NORTH, 0.0, 0.0);
		
		maxText = new JLabel(Double.toString(max));
		maxText.setToolTipText("Maxmimum value for the parameter");
		//maxText.setText(Double.toString(max));
		
		Constrain.setConstraints( this, maxText, 3, 0, 1, 1, 
			GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTH, 0.33, 0.0);

		Constrain.setConstraints(this, new JLabel("Value"), 4, 0, 1, 1, 
			GridBagConstraints.NONE, GridBagConstraints.NORTH, 0.0, 0.0);
		
		valueText.setToolTipText("Input Model Parameter");
		valueText.setText(Double.toString(val));
		
		Constrain.setConstraints(this, valueText, 5, 0, 1, 1, 
			GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTH, 0.33, 0.0);

		this.setBorder(new TitledBorder(new EtchedBorder(), name));
	}
	/**
	 * get the user input value 	 
	 * @return the value for the current max.
	 * @throws java.lang.NumberFormatException
	 */
	double getValue () throws java.lang.NumberFormatException {
		return Double.parseDouble(valueText.getText());
	}

}
}
