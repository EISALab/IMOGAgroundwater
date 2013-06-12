/*&%^1 Do not modify this section. */
package ncsa.d2k.modules.core.optimize.ga;


import ncsa.d2k.modules.core.optimize.ga.emo.*;
import ncsa.d2k.modules.core.vis.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.userviews.*;
import ncsa.d2k.userviews.widgets.*;
import ncsa.gui.Constrain;
import java.awt.*;
import java.awt.event.*;
import ncsa.d2k.modules.core.vis.widgets.*;
import ncsa.d2k.modules.core.optimize.ga.nsga.*;
/*#end^1 Continue editing. ^#&*/
/*&%^2 Do not modify this section. */
/**
	Used to display convergence to some objective function for a Genetic algorithm population.
*/
public class ConvergenceDisplay extends ncsa.d2k.core.modules.UIModule {
/*#end^2 Continue editing. ^#&*/
	/**
		This method returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {
		switch (index) {
			case 0: return "  This input is the target fitness score.";
			default: return "No such input";
		}
	}
	/**
		This method returns an array of strings that contains the data types for the inputs.
		@return the data types of all inputs.
	*/
	public String[] getInputTypes () {
		String[] types = {"ncsa.d2k.modules.core.optimize.ga.Population"};
		return types;
	}
	/**
		This method returns the description of the outputs.
		@return the description of the indexed output.
	*/
	public String getOutputInfo (int index) {
		switch (index) {
			default: return "No such output";
		}
	}
	/**
		This method returns an array of strings that contains the data types for the outputs.
		@return the data types of all outputs.
	*/
	public String[] getOutputTypes () {
		String[] types = {		};
		return types;
	}
	/**
		This method returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo () {
		return "<html>  <head>      </head>  <body>    This module displays a plot which renders the current fitness value over     time, and the target fitness.  </body></html>";
	}

	/**
		Whenever we are done, we will reset the display.
	*/
	public void endExecution () {
		super.endExecution ();
		ConvergenceDisplayView cdv = (ConvergenceDisplayView) this.getUserView ();
		cdv.reset ();
	}

/*&%^8 Do not modify this section. */
/*#end^8 Continue editing. ^#&*/
	/**
		This method is called by D2K to get the UserView for this module.
		@return the UserView.
	*/
	protected UserView createUserView () {

/*&%^9 Do not modify this section. */
		return new ConvergenceDisplayView();
/*#end^9 Continue editing. ^#&*/
	}
	/**
		This method returns an array with the names of each DSComponent in the UserView
		that has a value.  These DSComponents are then used as the outputs of this module.
	*/
	public String[] getFieldNameMapping() {

/*&%^10 Do not modify this section. */
		String[] fieldMap = {};
		return fieldMap;
/*#end^10 Continue editing. ^#&*/
	}

/*&%^11 Do not modify this section. */
/*#end^11 Continue editing. ^#&*/
}

/*&%^12 Do not modify this section. */
/**
	ConvergenceDisplayView
	This is the UserView class.
*/
class ConvergenceDisplayView extends ncsa.d2k.userviews.UserInputPane {
/*#end^12 Continue editing. ^#&*/
	/**
		This method adds the components to a Panel and then adds the Panel
		to the view.
	*/
	LinePlot linePlot = new LinePlot ();
	boolean initedPlot = false;
	double worstYet = 0.0;
	StatusPlot statusPlot = new StatusPlot ();
	double best, worst;

	/**
		this class displays the current fitness, the target fitness, and also
		has some controls to allow alterations on the display.
	*/
	class StatusPlot extends Panel implements ActionListener {

		/** display the convergence value. */
		public Label convField = new Label ("?");

		/** display the current value. */
		public Label valueField = new Label ("?");

		/** buttons to control the resolution of the display. */
		Button incMax, decMax, incMin, decMin;

		/** current resolutions. **/
		Label currentMax = new Label (),
			currentMin = new Label ();

		/** factors to factor the resolution */
		double vert_reducer = 1.0;
		double horiz_reducer = 1.0;

		StatusPlot () {
			this.setLayout (new GridBagLayout ());
			convField.setForeground (Color.green);
			valueField.setForeground (Color.red);

			// create teh panel with the current and convergence values.
			Panel tmp = new Panel ();
			tmp.setLayout (new GridBagLayout ());

			Constrain.setConstraints(tmp, new Label ("Converge at :"),
				0, 0, 1, 1, GridBagConstraints.NONE, GridBagConstraints.EAST, 0.0, 0.0);
			Constrain.setConstraints(tmp, convField,
				1, 0, 1, 1, GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST, 1.0, 0.0);
			Constrain.setConstraints(tmp, new Label ("Current value :"),
				0, 1, 1, 1, GridBagConstraints.NONE, GridBagConstraints.EAST, 0.0, 0.0);
			Constrain.setConstraints(tmp, valueField,
				1, 1, 1, 1, GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST, 1.0, 0.0);

			// Add that panel.
			Constrain.setConstraints(this, tmp,
				0, 0, 1, 1, GridBagConstraints.HORIZONTAL, GridBagConstraints.EAST, 1.0, 0.0);

			// Create a panel to modify the display.
			tmp = new Panel ();
			tmp.setLayout (new GridBagLayout ());

			incMax = new Button ("Zoom in");
			Constrain.setConstraints(tmp, incMax,
				0, 0, 1, 1, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);
			Constrain.setConstraints(tmp, currentMax,
				0, 1, 1, 1, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);
			decMax = new Button ("Pan out");
			Constrain.setConstraints(tmp, decMax,
				0, 2, 1, 1, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);

			incMin = new Button ("Zoom in");
			Constrain.setConstraints(tmp, incMax,
				1, 0, 1, 1, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);
			Constrain.setConstraints(tmp, currentMax,
				1, 1, 1, 1, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);
			decMin = new Button ("Zoom out");
			Constrain.setConstraints(tmp, decMax,
				1, 2, 1, 1, GridBagConstraints.NONE, GridBagConstraints.WEST, 0.0, 0.0);

			incMax.addActionListener (this);
			decMax.addActionListener (this);
			incMin.addActionListener (this);
			decMin.addActionListener (this);

			// Add that conrols.
			Constrain.setConstraints(this, tmp,
				1, 0, 1, 1, GridBagConstraints.NONE, GridBagConstraints.EAST, 0.0, 0.0);
		}

		/**
			When a control button is hit, we change the characteristics of the display.
		*/
		public void actionPerformed (ActionEvent e) {
			if (e.getSource () == incMax)
				vert_reducer *= 2;
			else if (e.getSource () == decMax)
				vert_reducer /= 2;
			else if (e.getSource () == incMin)
				horiz_reducer *= 2;
			else if (e.getSource () == decMin)
				horiz_reducer /= 2;

			System.out.println ("Vert : "+vert_reducer+" horizontal : "+horiz_reducer);
			linePlot.setMin (worst - ((best-worst)*vert_reducer));
		}
	}


	public void initView (ViewModule mod) {
		super.initView (mod);
/*&%^13 Do not modify this section. */
/*#end^13 Continue editing. ^#&*/
		this.add ("Center", linePlot);
		this.add ("North", statusPlot);
	}

	/**
		Reset when we are done with the plot.
	*/
	public void reset () {
		linePlot.reset ();
		initedPlot = false;
		worstYet = 0.0;
	}

	/**
		This method is called whenever an input arrives, and is responsible
		for modifying the contents of any gui components that should reflect
		the value of the input.
		@param input this is the object that has been input.
		@param index the index of the input that has been received.
	*/
	public void setInput(Object o, int index) {
		Population pop = (Population) o;

		// Init the plot, needs max, min and convergence.
		if (initedPlot == false) {
			this.best = pop.getBestFitness ();
			this.worst = pop.getWorstFitness ();
			linePlot.initPlot (pop.getTargetFitness (), this.best, this.worst);
			initedPlot = true;

			// Do the status panel thing.
			statusPlot.convField.setText (Double.toString (pop.getTargetFitness ()));
		}

		// Do the status panel thing.
		statusPlot.valueField.setText (Double.toString (pop.getCurrentMeasure ()));

		// if it is a multi objective population, we will add one point
		// for each of the objectives.
		if (pop instanceof NsgaPopulation) {
			double [] best_fitnesses = ((NsgaPopulation)pop).getBestFitnesses ();
			for (int i = 0 ; i < best_fitnesses.length ; i++)
				linePlot.addPoint (i, best_fitnesses [i]);
			linePlot.repaint ();
		} else
			linePlot.addPoint (0, pop.getCurrentMeasure ());
	}

/*&%^14 Do not modify this section. */
/*#end^14 Continue editing. ^#&*/

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "Convergence Plot";
	}

	/**
	 * Return the human readable name of the indexed input.
	 * @param index the index of the input.
	 * @return the human readable name of the indexed input.
	 */
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Target";
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


