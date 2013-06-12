package ncsa.d2k.modules.projects.pgroves.vis;

import ncsa.d2k.userviews.swing.JUserPane;
import ncsa.d2k.modules.core.vis.widgets.TableMatrix;
import ncsa.d2k.modules.core.datatype.table.MutableTable;
import ncsa.gui.JEasyConstrainsPanel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class TableSortPane extends JEasyConstrainsPanel
	implements ActionListener, java.io.Serializable{
		MutableTable vt;
		TableMatrix vtm;

		ButtonGroup radios;
		JRadioButton ascendButton;
		JRadioButton descendButton;
		JComboBox columnSelectList;

	public TableSortPane(MutableTable vtt){
			super();
			vt=vtt;
			this.setLayout(new BorderLayout());

			vtm=new TableMatrix(vt);
			JTable vtTable=vtm.getJTable();
			if(vt.getNumColumns()<6)
				vtTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

			Dimension dim=vtTable.getPreferredScrollableViewportSize();
			dim.setSize(dim.getWidth(), 400);

			JPanel vtmHolder=new ResizePanel();
			vtmHolder.setLayout(new BorderLayout());

			Insets ii = new Insets(0, 0, 0, 0);
			vtmHolder.add(vtm, BorderLayout.CENTER);

			//the sort part
			JEasyConstrainsPanel paramHolder = new JEasyConstrainsPanel();
			paramHolder.setLayout(new GridBagLayout());


			//ascend/descend buttons
			radios=new ButtonGroup();
			ascendButton=new JRadioButton("Ascending");
			descendButton=new JRadioButton("Descending");
			radios.add(ascendButton);
			radios.add(descendButton);
			paramHolder.setConstraints(ascendButton, 1,0,1,1,
											GridBagConstraints.NONE,
											GridBagConstraints.CENTER,
											1.0,1.0, ii);
			paramHolder.setConstraints(descendButton, 1,1,1,1,
											GridBagConstraints.NONE,
											GridBagConstraints.CENTER,
											1.0,1.0, ii);
			JButton sortButton=new JButton("Sort");
			sortButton.addActionListener(this);
			paramHolder.setConstraints(sortButton, 0,0,1,2,
											GridBagConstraints.NONE,
											GridBagConstraints.CENTER,
											1.0,2.0, ii);

			//sort by column select
			Object[] colNames=new Object[vt.getNumColumns()];
			for(int i=0; i<colNames.length; i++){
				colNames[i]=vt.getColumnLabel(i);
			}
			columnSelectList=new JComboBox(colNames);


			paramHolder.setConstraints(columnSelectList, 2,0,1,2,
											GridBagConstraints.NONE,
											GridBagConstraints.CENTER,
											1.0,2.0, ii);

			this.add(vtmHolder, BorderLayout.CENTER);
			this.add(paramHolder, BorderLayout.SOUTH);
	}
		class ResizePanel extends JEasyConstrainsPanel
			implements java.io.Serializable{
			public void setBounds(int x, int y, int w, int h) {
				Dimension d = vtm.getJTable().
									getPreferredScrollableViewportSize();
				vtm.getJTable().setPreferredScrollableViewportSize(
					new Dimension((int)d.getWidth(), h));
				super.setBounds(x, y, w, h);
			}
		}
		/*will only be called by the sort button*/
		public void actionPerformed(ActionEvent e){
			//System.out.println("action");
			//get ascend/descend
			boolean ascend=false;
			if(ascendButton.isSelected()){
				ascend=true;
			}
			//get the column to sort by
			int colIndex=0;
			colIndex=columnSelectList.getSelectedIndex();
			//sort
			try{
				vt.sortByColumn(colIndex);
			}catch(Exception ne){
				ne.printStackTrace();
			}
			//reverse the order if necessary
			if(!ascend)
				reverseOrder();

			validate();
			repaint();

		}

		public void reverseOrder(){
			int swapCount=(int)vt.getNumRows()/2;
			int numRows=vt.getNumRows();
			for(int i=0; i<swapCount; i++){
				vt.swapRows(i, numRows-i-1);
			}
		}
}


