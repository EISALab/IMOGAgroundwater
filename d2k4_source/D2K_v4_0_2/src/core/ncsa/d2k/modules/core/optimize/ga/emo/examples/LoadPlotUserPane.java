package ncsa.d2k.modules.core.optimize.ga.emo.examples;


import ncsa.d2k.modules.core.vis.widgets.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.userviews.*;
import ncsa.d2k.userviews.widgets.*;
import ncsa.gui.*;
import java.awt.*;
import java.io.*;
import javax.swing.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
/**
	ScatterPlotUserPane
*/
public class LoadPlotUserPane extends ncsa.d2k.userviews.swing.JUserPane {
	LoadScatterPlotModule module;
	TableImpl table;

	public void initView(ViewModule viewmodule) {
		module = (LoadScatterPlotModule) viewmodule;
	}
	public Module getModule () { return module; }
	public void setInput(Object object, int index) {
		table = (TableImpl) object;

		buildView();
	}

	public void buildView() {
		setLayout(new GridBagLayout());

		try {
		Constrain.setConstraints(this, new GraphEditor(table,
			Class.forName("ncsa.d2k.modules.core.optimize.ga.emo.examples.LoadProblemScatterPlot")),
			0, 0, 1, 1, GridBagConstraints.BOTH,
			GridBagConstraints.NORTHWEST, 1, 1);
		} catch (Exception ex) {
			System.out.println (" the LoadProblemScatterPlot class is not in the modules directory!");
			ex.printStackTrace ();
		}
	}


	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "ScatterPlot";
	}

	/**
	 * Return the human readable name of the indexed input.
	 * @param index the index of the input.
	 * @return the human readable name of the indexed input.
	 */
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "table";
			default: return "NO SUCH INPUT!";
		}
	}

	/**
	 * Return the human readable name of the indexed output.
	 * @param index the index of the output.
	 * @return the human readable name of the indexed output.
	 */
	public String getOutputName(int index) {
		switch(index) {
			default: return "NO SUCH OUTPUT!";
		}
	}
}
