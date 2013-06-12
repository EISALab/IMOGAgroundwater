package ncsa.d2k.modules.core.optimize.util.gui;

import java.awt.*;
import javax.swing.*;

import ncsa.d2k.modules.core.optimize.util.*;

/**	ParamPanel

	A single entry in a ParamListPanel. It is a single
	row that contains param name, type, minimum, maximum (both
	defined by input range objects), and the user-editable
	value field.

	@author Peter Groves
**/

public class ParamPanel extends JPanel implements java.io.Serializable{

	/*holds the user-editable value */
	protected JTextField paramText;

	/*holds the un-editable range, name, type*/
	protected RangePanel rangePanel;
	
	/**
		Constructor

		@param range the range object that defines the bounds
						for this parameter
	*/
	public ParamPanel(Range range){
		rangePanel=new RangePanel(range, false);

		String pString=Double.toString(range.getMin());
		paramText=new JTextField(pString, 8);
		this.add(rangePanel);
		this.add(paramText);
		this.setBorder(BorderFactory.createMatteBorder(
							0,0,1,0, Color.black));

	}
	/**
		Constructor

		@param range the range object that defines the bounds
						for this parameter
		@param numColumns the number of character columns the textfields
						will have
	*/

	public ParamPanel(Range range, int numColumns){
		rangePanel=new RangePanel(range, false, numColumns);
		
		String pString=Double.toString(range.getMin());
		paramText=new JTextField(pString, numColumns);
		this.add(rangePanel);
		this.add(paramText);
		//this.setBorder(BorderFactory.createMatteBorder(
		//					0,0,1,0, Color.lightGray));
	}

	/**
		@return the minimum of the range as defined
				by the input Range object
	*/
	public double getMin(){
		return rangePanel.getMin();
	}
	/**
		@return the maximum of the range as defined
				by the input Range object
	*/

	public double getMax(){
		return rangePanel.getMax();
	}
	/**
		@return this panel's user defined parameter
				as a double
	*/
	public double getParameter(){
		return Double.parseDouble(paramText.getText());
	}
	/**
		@return this panel's user defined parameter
				as a string
	*/	
	public String getStringParameter(){
		return paramText.getText();
	}
	/**
		@return this panel's min as a string
	*/
	public String getStringMin(){
		return rangePanel.getStringMin();
	}
	/**
		@return this panel's max as a string
	*/

	public String getStringMax(){
		return rangePanel.getStringMax();
	}

	/**
		enters the input double into the user
		editable text field
		@param	d	the parameter to set as this
					panel's value
					*/
	
	public void setParameter(double d){
		paramText.setText(Double.toString(d));
	}
	
}

	
