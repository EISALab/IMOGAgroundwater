package ncsa.d2k.modules.core.vis.widgets;

import javax.swing.*;
import ncsa.d2k.gui.*;
import ncsa.d2k.modules.core.datatype.table.*;

public class ETLinearRegressionWidget extends ETScatterPlotWidget {

	public ETLinearRegressionWidget() {
		super();
	}

	public ETLinearRegressionWidget(ExampleTable table) {
		super(table);
	}

	protected JFrame getHelpWindow() {
		return new ETLRHelpWindow(getHelpString());
	}

	/**
		Create a small graph to be shown in the matrix.
		@param vt the table with the data values
		@param d the DataSets to plot
		@param gs the GraphSettings for this plot
	*/
	protected Graph createSmallGraph(Table vt, DataSet[] d,
		GraphSettings gs) {
		return new LinearRegressionSmall(vt, d, gs);
	}

	/**
		Create a large graph to be shown in the composite window.
		@param vt the table with the data values
		@param d the DataSets to plot
		@param gs the GraphSettings for this plot
	*/
	protected Graph createGraph(Table vt, DataSet[] d,
		GraphSettings gs) {
		return new LinearRegression(vt, d, gs);
	}

	private final class ETLRHelpWindow extends JD2KFrame {
		ETLRHelpWindow(String s) {
			super("About ETLinearRegression");
			JEditorPane jep = new JEditorPane("text/html", s);
			getContentPane().add(new JScrollPane(jep));
			setSize(400, 400);
		}
	}

    protected String getHelpString() {
        StringBuffer sb = new StringBuffer();
        sb.append("<html>");
        sb.append("<body>");
		sb.append("<h2>ETLinearRegression</h2>");
		sb.append("ETLinearRegression displays multiple scatter plots in a grid layout ");
		sb.append("with a line approximated through the data.  ");
		sb.append("This is a small multiple views of data that plot all the chosen input ");
		sb.append("attributes by all the chosen output attributes.  Since each of these ");
		sb.append("grids are a little different, a composite view can be created by ");
		sb.append("highlighting the view you want and clicking the Show Composite ");
		sb.append("button at the bottom of the window.  This will create a new window ");
		sb.append("each time.  The Clear Selected Graphs button will toggle off all ");
		sb.append("the currently highlighted plots.");
        sb.append("</body></html>");
        return sb.toString();
    }

	protected String getMenuDescription() {
		return "About ETLinearRegression...";
	}
}