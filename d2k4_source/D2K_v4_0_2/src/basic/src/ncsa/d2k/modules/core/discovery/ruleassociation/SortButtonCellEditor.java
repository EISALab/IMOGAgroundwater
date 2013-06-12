package ncsa.d2k.modules.core.discovery.ruleassociation;
import java.awt.*;
import java.util.*;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;

/**
	Render a rule, the ifs and thens are represented by some
	appropriate graphic.
*/
class SortButtonCellEditor implements TableCellEditor  {
	//JButton confidence;
	//JButton support;
	//JButton last = null;
	JRadioButton confidence;
	JRadioButton support;
	JRadioButton last = null;
	Vector listeners = new Vector();

	/**
	 * Given the buttons we will return from the getCellRenderer.
	 * @param c the confidence button.
	 * @param s the support button.
	 */
	SortButtonCellEditor (JRadioButton c, JRadioButton s) {
		super ();
		this.confidence = c;
		this.support = s;
	}

	// Methods
	public Component getTableCellEditorComponent(JTable jTable, Object object, boolean boolean2,
			int row, int column) {
		if (row == 0) {
			this.last = this.confidence;
		} else {
			this.last = this.support;
		}
		return this.last;
	}

	// Methods
	public Object getCellEditorValue(){
		return new Boolean(true);
	}
	public boolean isCellEditable(EventObject eventObject){ return true; }
	public boolean shouldSelectCell(EventObject eventObject){ return true; }
	public boolean stopCellEditing(){
		fireEditingStopped();
		return true;
	}

	public void cancelCellEditing(){
		fireEditingCanceled();
	}
	public void addCellEditorListener(CellEditorListener cellEditorListener) {
		listeners.addElement(cellEditorListener);
	}
	public void removeCellEditorListener(CellEditorListener cellEditorListener)  {
		listeners.removeElement(cellEditorListener);
	}
	private void fireEditingCanceled() {
		ChangeEvent ce = new ChangeEvent (this);
		for (int i = 0 ; i < listeners.size(); i++)
			((CellEditorListener)listeners.elementAt(i)).editingStopped(ce);
	}
	private void fireEditingStopped() {
		ChangeEvent ce = new ChangeEvent (this);
		for (int i = 0 ; i < listeners.size(); i++)
			((CellEditorListener)listeners.elementAt(i)).editingStopped(ce);
	}
}

