package ncsa.d2k.modules.core.vis.widgets;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

import java.awt.*;

public class ScatterPlot extends Graph {

	public ScatterPlot () {}
	public ScatterPlot(Table table, DataSet[] sets, GraphSettings settings) {
		super(table, sets, settings);
	}

	public void drawDataSet(Graphics2D g2, DataSet set) {
		boolean xcolumn = table.isColumnNumeric(set.x);
		boolean ycolumn = table.isColumnNumeric(set.y);

		int size = table.getNumRows();

		for (int index=0; index < size; index++) {
			double xvalue;
			double yvalue;
			if(xcolumn)
				xvalue = table.getDouble(index, set.x);
			else {
				String v = table.getString(index, set.x);
				xvalue = (double)((Integer)xStringLookup[index].get(v)).intValue();
			}
			if(ycolumn)
				yvalue = table.getDouble(index, set.y);
			else {
				String v = table.getString(index, set.y);
				yvalue = (double)((Integer)yStringLookup[index].get(v)).intValue();
			}

			drawPoint(g2, set.color, xvalue, yvalue);
		}
	}
}
