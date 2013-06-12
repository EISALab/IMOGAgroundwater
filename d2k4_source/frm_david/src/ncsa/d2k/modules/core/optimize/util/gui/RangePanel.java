package ncsa.d2k.modules.core.optimize.util.gui;

import java.awt.*;
import javax.swing.*;

import ncsa.d2k.modules.core.optimize.util.*;
/**	RangePanel

	A single entry in a RangeListPanel. It is a single
	row that contains param name, type, minimum, maximum (both
	defined by input range objects)

	@author Peter Groves
**/
public class RangePanel extends JPanel implements java.io.Serializable{

	//protected JLabel paramNameLabel;
	
	protected JTextField paramNameText;
	protected JTextField minText;
	protected JTextField maxText;
	protected JTextField typeText;
	
	protected boolean editable;


	/** Constructor 

		number of columns in the text fields will default
		to 8.

		@param range the range to be altered/viewed
		@param areBoundsEditable indicates whether
				or not the range's text fields should
				be editable
	*/
	public RangePanel(Range range, boolean areBoundsEditable ){
		editable = areBoundsEditable;
		
		String paramName;

		if(range instanceof NamedRange){
			paramName=range.getName();
		}else{
			paramName="Unnamed Param";
		}
		
		//paramNameLabel=new JLabel(paramName);
		//this.add(paramNameLabel);

		paramNameText=new JTextField(paramName, 8);
		paramNameText.setEditable(false);
		paramNameText.setBorder(BorderFactory.createEmptyBorder());
		this.add(paramNameText);


		String minString;
		String maxString;
		String typeString;
		
		if(range instanceof IntRange){
			minString=Integer.toString((int)range.getMin());
			maxString=Integer.toString((int)range.getMax());
			typeString="int";
		}else if(range instanceof BinaryRange){
			minString=Integer.toString((int)range.getMin());
			maxString=Integer.toString((int)range.getMax());
			typeString="bin";
		}else{
			minString=Double.toString(range.getMin());
			maxString=Double.toString(range.getMax());
			typeString="dbl";
		}
			
		typeText=new JTextField(typeString, 4);	
		minText=new JTextField(minString, 8);
		maxText=new JTextField(maxString, 8);

		minText.setEditable(editable);
		maxText.setEditable(editable);
		typeText.setEditable(false);
		typeText.setHorizontalAlignment(JTextField.CENTER);
		typeText.setBorder(BorderFactory.createEmptyBorder());
		
		this.add(typeText);
		this.add(minText);
		this.add(maxText);
		//paramNameLabel.setPreferredSize(minText.getPreferredSize());
	}

	/** Constructor 

		@param range the range to be altered/viewed
		@param areBoundsEditable indicates whether
					or not the range's text fields should
					be editable
		@param numColumns the number of character columns the textfields
					will have
		
	*/
	public RangePanel(Range range, boolean areBoundsEditable, int numColumns){
		this(range, areBoundsEditable);
		minText.setColumns(numColumns);
		maxText.setColumns(numColumns);
		paramNameText.setColumns(numColumns);
	}
	/*
		returns the value of the 'min' text field
		*/
	public double getMin(){
		double d=Double.parseDouble(minText.getText());
		return d;
	}
	/*
		returns the value of the 'max' text field
		*/
	public double getMax(){
		double d=Double.parseDouble(maxText.getText());
		return d;
	}
	/**
		@return this panel's max as a string
	*/

	public String getStringMax(){
		return maxText.getText();
	}
	/**
		@return this panel's min as a string
	*/
	
	public String getStringMin(){
		return minText.getText();
	}
	
}

	
		
	
