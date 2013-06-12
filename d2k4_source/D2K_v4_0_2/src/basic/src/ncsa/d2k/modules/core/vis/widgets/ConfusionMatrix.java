package ncsa.d2k.modules.core.vis.widgets;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
import java.util.*;
import java.text.*;
import java.awt.image.*;

import ncsa.d2k.modules.core.datatype.table.*;

/**
	A simple ConfusionMatrix.  Implemented as a JTable inside a JScrollPane.
	This class will calculate the confusion matrix given a Table,
	the index of the output column and the index of its associated
	prediction column.  ConfusionMatrix can also be used as a simple
	table of numbers when the appropriate constructor is used.
*/
public class ConfusionMatrix extends JScrollPane {

	public static void main(String[] args) {
		String[] r = {"one", "two", "three"};
		int[][] d = {{1, 2, 3}, {1, 2, 3}, {1, 2, 3}};
		ConfusionMatrix cm = new ConfusionMatrix(d, r, r);
		JFrame f = new JFrame("");
		f.setSize(400, 400);
		f.getContentPane().add(cm);
		f.setVisible(true);
	}

	public int correct;
	private NumberFormat nf;

	/**
		Create a new ConfusionMatrix
		@param d the data to show
		@param r the row headers
		@param c the column headers
	*/
	public ConfusionMatrix(int[][] d, String[] r, String[] c) {
		super();
		nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(1);
		setupTable(d, r, c);
	}

	/**
		Create a new ConfusionMatrix
		@param vt the Table with the outputs and predictions
		@param o the index of the output column
		@param p the index of the prediction column
	*/
	public ConfusionMatrix(Table vt, int o, int p) {
		super();
		int numRows = vt.getNumRows();
		nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(1);

		// keep the unique outputs and predictions
		HashSet outNames = new HashSet();
		for(int i = 0; i < numRows; i++) {
			String s = vt.getString(i, o);
			if(!outNames.contains(s))
				outNames.add(s);
		}

		// keep the unique outputs and predictions
		/*HashSet predNames = new HashSet();
		for(int i = 0; i < numRows; i++) {
			String s = vt.getString(i, p);
			if(!predNames.contains(s))
				predNames.add(s);
		}*/

		String []outs = new String[outNames.size()];
		String []outlabels = new String[outNames.size()];
		int idx = 0;
		Iterator it = outNames.iterator();
		while(it.hasNext()) {
			String s = (String)it.next();
			outs[idx] = s;
			outlabels[idx] = s;
			idx++;
		}

		String []preds = outs;
		String []predlabels = new String[outNames.size()];
		idx = 0;
		it = outNames.iterator();
		while(it.hasNext()) {
			String s = (String)it.next();
			predlabels[idx] = s;
			idx++;
		}

		// calculate the confusion matrix
		int[][] d = new int[outs.length][outs.length];

		for(int row = 0; row < vt.getNumRows(); row++) {
			int actual = 0;
			int predicted = 0;
			for(int i = 0; i < outs.length; i++) {
				if(vt.getString(row, o).equals(outs[i])) {
					actual = i;
					break;
				}
			}
			for(int i = 0; i < preds.length; i++) {
				if(vt.getString(row, p).equals(preds[i])) {
					predicted = i;
					break;
				}
			}

			//d[predicted][actual]++;
			if(actual < outs.length && predicted < outs.length)
				//d[predicted][actual]++;
                                d[actual][predicted]++;
                        //if(actual != predicted)
                        //  System.out.println("INCOR: "+row);
		}

		correct = 0;
		for(int i = 0; i < outs.length; i++)
			for(int j = 0; j < outs.length; j++)
				if(i == j)
					correct += d[i][j];

		setupTable(d, outlabels, predlabels);
	}

	/**
		Setup the row headers.
	*/
	private void setupTable(int[][] d, String[] r, String[] c) {
		MatrixModel tblModel = new MatrixModel(d, r, c);
		JTable tmp = new JTable();
        Font font = tmp.getFont();

        BufferedImage bi = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		Graphics g = bi.createGraphics();
        g.setFont(font);
		FontMetrics fm = g.getFontMetrics();
		int max = 0;
		for (int i = 0; i < r.length; i++) {
			if(fm.stringWidth(c[i]) > max)
				max = fm.stringWidth(c[i]);
		}

		final int mm = max;
		TableColumnModel cm = new DefaultTableColumnModel() {
			boolean first = true;
			public void addColumn(TableColumn tc) {
				if(first) { first = false; return; }
				//tc.setMinWidth(100);
				tc.setMinWidth(mm);
				super.addColumn(tc);
			}
		};

		max = 0;
		for (int i = 0; i < r.length; i++) {
			if(fm.stringWidth(r[i]) > max)
				max = fm.stringWidth(r[i]);
		}

		final int mx = max;
		// setup the columns for the row header table
		TableColumnModel rowHeaderModel = new DefaultTableColumnModel() {
			boolean first = true;
			public void addColumn(TableColumn tc) {
				if(first) {
					//tc.setMinWidth(100);
					tc.setMinWidth(mx);
					super.addColumn(tc);
					first = false;
				}
			}
		};

		// setup the row header table
		JTable table = new JTable(tblModel, cm);
		table.createDefaultColumnsFromModel();

		JTable headerColumn = new JTable(tblModel, rowHeaderModel);
		headerColumn.createDefaultColumnsFromModel();
		headerColumn.setMaximumSize(new Dimension(75, 10000));
		table.setSelectionModel(headerColumn.getSelectionModel());

		JViewport jv = new JViewport();
		jv.setView(headerColumn);
		jv.setPreferredSize(headerColumn.getPreferredSize());

		setViewportView(table);
		setRowHeader(jv);
		table.getTableHeader().setReorderingAllowed(false);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		//jTable.setPreferredScrollableViewportSize(d);
		setHorizontalScrollBarPolicy(
			JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		setVerticalScrollBarPolicy(
			JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
	}

	/**
		The TableModel for the ConfusionMatrix
	*/
	class MatrixModel extends AbstractTableModel {
		//int[][] tallydata;
		//String[] rowNames;
		//String[] colNames;

		String[][] data;

		MatrixModel(int[][] d, String[] r, String[] c) {
			int colLength = c.length+3;
			int rowLength = r.length+4;

			data = new String[rowLength][colLength];

			// do the zeroth row
			data[0][0] = "Prediction =";
			for(int i = 0; i < c.length; i++)
				data[0][i+1] = c[i];
			data[0][colLength-2] = "";

			data[1][0] = "Ground Truth";
			for(int i = 0; i < c.length; i++)
				data[1][i+1] = "";
			data[1][colLength-2] = "RECALL";
			data[1][colLength-1] = "Type II Error";

			for(int i = 0; i < c.length; i++)
				data[i+2][0] = c[i];
			data[rowLength-2][0] = "PRECISION";
			data[rowLength-1][0] = "Type I Error";

			// for each row
			for(int i = 0; i < r.length; i++) {
				int total = 0;
				for(int j = 0; j < c.length; j++) {
					total += d[i][j];
					data[i+2][j+1] = Integer.toString(d[i][j]);
				}

                double percent = 0;
                if(total != 0)
				    percent = ((double)d[i][i])/total;
				data[i+2][colLength-2] = nf.format(percent*100)+"%";
				data[i+2][colLength-1] = nf.format((1-percent)*100)+"%";
			}

			// for each column
			for(int j = 0; j < c.length; j++) {
				int total = 0;
				for(int i = 0; i < r.length; i++) {
					total += d[i][j];
				}
				double percent = 0;
                if(total != 0)
				    percent = ((double)d[j][j])/total;
				data[rowLength-2][j+1] = nf.format(percent*100)+"%";
				data[rowLength-1][j+1] = nf.format((1-percent)*100)+"%";
			}
		}

		public String getColumnName(int col) {
			return "";
		}

		public int getRowCount() {
			return data.length;
		}

		public int getColumnCount() {
			return data[0].length;
		}

		public Object getValueAt(int row, int col) {
			return data[row][col];
		}

		public boolean isCellEditable(int row, int col) {
			return false;
		}
	}
}
