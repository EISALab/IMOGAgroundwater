package ncsa.d2k.modules.core.optimize.ga.nsga;



import ncsa.d2k.modules.core.vis.widgets.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import java.awt.*;


/**
	ScatterPlot2D.java
*/
public class DisplayFront extends ncsa.d2k.core.modules.UIModule
{
	/** this is the minimum value in the x direction to scale the plot. */
	double xmin = 0.0;
	public double getXMin () { return xmin; }
	public void setXMin (double nxm) { xmin = nxm; }

	/** this is the maximum value in the x direction to scale the plot. */
	double xmax = 1.0;
	public double getXMax () { return xmax; }
	public void setXMax (double nxm) { xmax = nxm; }

	/** this is the minimum value in the y direction to scale the plot. */
	double ymin = 0.0;
	public double getYMin () { return ymin; }
	public void setYMin (double nxm) { ymin = nxm; }

	/** this is the max value in the y direction to scale the plot. */
	double ymax = 1.0;
	public double getYMax () { return ymax; }
	public void setYMax (double nxm) { ymax = nxm; }

	/**
		This pair returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {
		switch (index) {
			case 0: return "         ";
			default: return "No such input";
		}
	}

	/**
		This pair returns an array of strings that contains the data types for the inputs.
		@return the data types of all inputs.
	*/
	public String[] getInputTypes() {
		String[] types = {"ncsa.d2k.modules.core.optimize.ga.Population"};
		return types;
	}

	/**
		This pair returns the description of the outputs.
		@return the description of the indexed output.
	*/
	public String getOutputInfo(int index) {
		switch (index) {
			default: return "No such output";
		}
	}

	/**
		This pair returns an array of strings that contains the data types for the outputs.
		@return the data types of all outputs.
	*/
	public String[] getOutputTypes() {
		String[] types = {		};
		return types;
	}

	/**
		This pair returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo() {
		return "<paragraph>  <head>  </head>  <body>    <p>          </p>  </body></paragraph>";
	}

	/**
		PUT YOUR CODE HERE.
	*/
	public void doit() throws Exception {
	}


	/**
		This pair is called by D2K to get the UserView for this module.
		@return the UserView.
	*/
	protected UserView createUserView() {
		return new FrontScatterDisplay ();
	}

	/**
		This pair returns an array with the names of each DSComponent in the UserView
		that has a value.  These DSComponents are then used as the outputs of this module.
	*/
	public String[] getFieldNameMapping() {
		return null;
	}
}

/**
	Display the front as a scatter plot.
*/
class FrontScatterDisplay extends ncsa.d2k.userviews.swing.JUserInputPane {

	/** the display module. */
	DisplayFront module;

	/** table contains the stuff to plot. */
	//BASIC3 TableImpl table = null;
	MutableTableImpl table = null;
	
	/** graph displaying the scatter plot. */
	ScatterPlot graph;

	/** the dataset. */
	DataSet [] set = new DataSet [1];

	/** graph characteristics. */
	GraphSettings settings;

	public void initView (ViewModule viewmodule) {
		super.initView (viewmodule);
		module = (DisplayFront) viewmodule;
	}

	/**
	 * got a new dataset.
	 * @param object the population.
	 * @param index the index of the input, here always 0.
	 */
	public void setInput (Object object, int index) {

		// get the population and stuff.
		NsgaPopulation pop = (NsgaPopulation) object;
		int numObjs = pop.getNumObjectives ();
		NsgaSolution [] nis = (NsgaSolution []) pop.getMembers ();
		int num = nis.length;

		synchronized (this) {
			if (table == null || table.getNumRows () != num) {
				if (graph != null)
					remove (graph);

				//BASIC3 table = (TableImpl)DefaultTableFactory.getInstance().createTable(2);
				table =  new MutableTableImpl(2);
				double [] objx = new double [num];
				double [] objy = new double [num];

				for (int i = 0 ; i < num ; i++) {
					objy [i] = nis [i].getObjective (0);
					objx [i] = nis [i].getObjective (1);
				}

				DoubleColumn f1 = new DoubleColumn (objx);
				DoubleColumn f2 = new DoubleColumn (objy);
				table.setColumn (f1, 0);
				table.setColumn (f2, 1);

				// Init the settings.
				settings = new GraphSettings();

				// Get the labels for the objectives from the obj constraints object.
				String lbl = pop.getObjectiveConstraints()[0].getName ();
				settings.xaxis = lbl == null ? "F1" : lbl;
				lbl = pop.getObjectiveConstraints()[1].getName ();
				settings.yaxis = lbl == null ? "F2" : lbl;
				settings.title = settings.xaxis + " vs " + settings.yaxis;
				settings.xdataminimum = new Double (module.getXMin ());
				settings.xdatamaximum = new Double (module.getXMax ());
				settings.ydataminimum = new Double (module.getYMin ());
				settings.ydatamaximum = new Double (module.getYMax ());
				set [0] = new DataSet (settings.title, Color.black, 0, 1);
				graph = new ScatterPlot (table, set, settings);
				graph.setPointSize (2);
				graph.setMinimumSize (new Dimension (500, 500));
				graph.setPreferredSize (new Dimension (500, 500));

				// the first time we build the view.
				buildView();
			} else {

				// Reinitialize the plot, grab the values, put in a table.
				DoubleColumn f1 = (DoubleColumn) table.getColumn (0);
				DoubleColumn f2 = (DoubleColumn) table.getColumn (1);
				double [] objx = (double []) f1.getInternal ();
				double [] objy = (double[])f2.getInternal();

				for (int i = 0 ; i < num ; i++) {
					objy [i] = nis [i].getObjective (0);
					objx [i] = nis [i].getObjective (1);
				}

				// Init and repaint the graph
				graph.init (table, set, settings);
				graph.repaint ();
			}
		}
	}

	/**
	 * Add the one simple graph panel to this display
	 */
	public void buildView() {
		add (graph);
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
