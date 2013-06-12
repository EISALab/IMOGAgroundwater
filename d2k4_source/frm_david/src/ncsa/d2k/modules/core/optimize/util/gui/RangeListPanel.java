package ncsa.d2k.modules.core.optimize.util.gui;

import java.awt.*;
import javax.swing.*;
import ncsa.gui.Constrain;

import ncsa.d2k.modules.core.optimize.util.*;

/**
	This is a reusable panel that allows a user to view the 
	ranges specified by an array of Ranges 

	@author Peter Groves
*/
public class RangeListPanel extends JPanel implements java.io.Serializable{

	/**the Range objects that this panel was based on*/
	Range[] origRanges;
	
	/**the RangePanels that hold/draw the info*/
	RangePanel[] rangePanels;

/**
	Constructor<br>

	Makes a default panel for the ranges it's
	given. The number of columns in the text fields
	will default to 8.
	
	@param ranges The Range Objects that define the search space
*/ 

	public RangeListPanel(Range[] ranges){
		
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
	public RangeListPanel(Range[] ranges, int numColumns){
		origRanges=ranges;
		rangePanels=new RangePanel[origRanges.length];

		this.setLayout(new GridBagLayout());

		//the header/table column names panel
		JPanel header=new JPanel();
		JTextField nameField=new JTextField("Param", numColumns);
		JTextField typeField=new JTextField("Type", 4);
		JTextField minField=new JTextField("Min", numColumns);
		JTextField maxField=new JTextField("Max", numColumns);

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

		Constrain.setConstraints(	this, header,
										0, 0,
										1, 1, 
										GridBagConstraints.HORIZONTAL,
										GridBagConstraints.WEST, 
										1, 1);

		//the range panels
		for(int i=0; i<ranges.length; i++){
			rangePanels[i]=new RangePanel(	origRanges[i], 
											true, 
											numColumns);
			Constrain.setConstraints(	this, rangePanels[i],
										0, i+1,
										1, 1, 
										GridBagConstraints.HORIZONTAL,
										GridBagConstraints.WEST, 
										1, 1);
		}
	}

	/**
		getRangeArray<br>
		
		meant to be used mainly when the cells are editable.
		it will compile the contents of the min/max textfields
		and create new Range objects of the types that the 
		original input ranges were
		
		@return the ranges represented by the textfields
	*/
	public Range[] getRangeArray(){

		int size=origRanges.length;
		Range[] newRanges= new Range[size];
		
		for(int i=0; i<size; i++){
			if(origRanges[i] instanceof DoubleRange){
				newRanges[i]=new DoubleRange(
									origRanges[i].getName(),
									rangePanels[i].getMax(),
									rangePanels[i].getMin()
									);
			}
			else if(origRanges[i] instanceof BinaryRange){
				//this gui doesn't really apply
				newRanges[i]=origRanges[i];
				
			}
			else if(origRanges[i] instanceof IntRange){
				newRanges[i]=new IntRange(
									origRanges[i].getName(),
									(int)rangePanels[i].getMax(),
									(int)rangePanels[i].getMin()
									);
			}
		}
		return newRanges;
	}
}
