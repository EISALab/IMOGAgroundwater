package ncsa.d2k.modules.core.optimize.util.gui;

import java.awt.*;
import javax.swing.*;
import ncsa.gui.Constrain;

import ncsa.d2k.modules.core.optimize.util.*;

/**
	This is a reusable panel that allows a user to view the 
	ranges specified by an array of Ranges and enter a 
	specific instance of a solution's parameter set.
	The parameters can then be pulled from the panel 
	with a single method call.

	@author Peter Groves
*/

public class ParamListPanel extends JPanel implements java.io.Serializable{

	/**the Range objects that this panel was based on*/
	Range[] origRanges;
	/**The ParamPanels that hold the ranges and parameters*/
	ParamPanel[] paramPanels;

/**
	Constructor<br>

	Makes a default panel for the ranges it's
	given
	
	@param ranges The Range Objects that define the search space
*/ 
	public ParamListPanel(Range[] ranges){
		
		this(ranges, 8);
	}
/**
	Constructor<br>

	Makes a panel for the ranges it's given. The
	user can specify the number of character columns
	each text field will have, as well.
	
	@param ranges The Range Objects that define the search space
	@param numColumns the number of character columns the textfields
						will have
*/ 
	public ParamListPanel(Range[] ranges, int numColumns){
		origRanges=ranges;
		paramPanels=new ParamPanel[origRanges.length];

		this.setLayout(new GridBagLayout());

		//the header/table column names panel
		JPanel header=new JPanel();
		JTextField nameField=new JTextField("Param", numColumns);
		JTextField typeField=new JTextField("Type", 4);
		JTextField minField=new JTextField("Min", numColumns);
		JTextField maxField=new JTextField("Max", numColumns);
		JTextField valField=new JTextField("Value", numColumns);

		nameField.setEditable(false);
		nameField.setBorder(BorderFactory.createEmptyBorder());
		nameField.setHorizontalAlignment(JTextField.CENTER);
		header.add(nameField);
		
		typeField.setEditable(false);
		typeField.setBorder(BorderFactory.createEmptyBorder());
		typeField.setHorizontalAlignment(JTextField.CENTER);
		header.add(typeField);
		
		minField.setEditable(false);
		minField.setBorder(BorderFactory.createEmptyBorder());
		minField.setHorizontalAlignment(JTextField.CENTER);
		header.add(minField);
		
		maxField.setEditable(false);
		maxField.setBorder(BorderFactory.createEmptyBorder());
		maxField.setHorizontalAlignment(JTextField.CENTER);
		header.add(maxField);

		valField.setEditable(false);
		valField.setBorder(BorderFactory.createEmptyBorder());
		valField.setHorizontalAlignment(JTextField.CENTER);
		header.add(valField);		
		header.setBorder(BorderFactory.createMatteBorder(
							3,0,3,0, Color.black));

		Constrain.setConstraints(	this, header,
										0, 0,
										1, 1, 
										GridBagConstraints.HORIZONTAL,
										GridBagConstraints.WEST, 
										1, 1);

		//the range panels
		for(int i=0; i<ranges.length; i++){
			paramPanels[i]=new ParamPanel(	origRanges[i], 
											numColumns);
			Constrain.setConstraints(	this, paramPanels[i],
										0, i+1,
										1, 1, 
										GridBagConstraints.HORIZONTAL,
										GridBagConstraints.WEST, 
										1, 1);
		}
	}
	/**
		returns the parameters entered into the textfields,
		in the order their respective ranges were in, as doubles

		@return double[] the input parameters
		*/
	public double[] getParameters(){

		int size=origRanges.length;
		double[] newParams= new double[size];
		
		for(int i=0; i<size; i++){
			newParams[i]=paramPanels[i].getParameter();	
		}
		
		return newParams;
	}

	/**
		sets a textfield's value to the given parameter

		@param d the parameter double value
		@param position the index of the textfield/parameter
						to set to the input value
	*/
	public void setParameter(double d, int position){
		paramPanels[position].setParameter(d);
	}
}
