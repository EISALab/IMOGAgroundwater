package ncsa.d2k.modules.core.optimize.random;



import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.parameter.impl.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.core.datatype.table.*;
import java.util.Random;
import java.util.ArrayList;
import ncsa.d2k.core.modules.ComputeModule;
import ncsa.d2k.core.modules.PropertyDescription;
import java.beans.PropertyVetoException;

public class RandomSampling extends ComputeModule {

	/** these are the paramter points to test. */
	int pointsPushed = 0;;

	/** these are the scored paramter points. */
	ArrayList examples;

	public PropertyDescription[] getPropertiesDescriptions () {
		PropertyDescription[] descriptions = new PropertyDescription[7];
		descriptions[0] = new PropertyDescription (
				"minimize",
				"Minimize Objective Score",
				"Set to true if the objective score should be minimize, true if it should be maximized.");
		descriptions[1] = new PropertyDescription (
				"threashhold",
				"Objective Threashhold Value",
				"Stop optimization when this threashhold value is reached.");
		descriptions[2] = new PropertyDescription (
				"maxIterations",
				"Maximum Number of Iterations",
				"Optimization halts when this limit on the number of iterations is exceeded.  ");
		descriptions[3] = new PropertyDescription (
				"seed",
				"Random Number Seed",
				"This integer is use to seed the random number generator which is used to select points in parameter space.");
		descriptions[4] = new PropertyDescription (
				"trace",
				"Trace",
				"Report each scored point in parameter space as it becomes available.");
		descriptions[5] = new PropertyDescription (
				"verbose",
				"Verbose Output",
				"Report each scored point in parameter space as it becomes available, and each parameter point that has been pushed.");
		descriptions[6] = new PropertyDescription (
				"useResolution",
				"Constrain Resolution",
				"If this parameter is set, we will use the resolution defined in the paramter space value for each paramter to define the number of distinct values in the ranges.");

		return descriptions;
	}

	private boolean minimizing = true;
	public void setMinimize (boolean value) {
		minimizing = value;
	}
	public boolean getMinimize () {
		return this.minimizing;
	}

	private double threashhold = 0.0;
	public void setThreashhold (double value) {
		this.threashhold = value;
	}
	public double getThreashhold () {
		return this.threashhold;
	}

	private int maxIterations = 100;
	public void setMaxIterations (int value) throws PropertyVetoException {
		if (value < 1) {
			throw new PropertyVetoException (" < 1", null);
		}
		this.maxIterations = value;
	}
	public int getMaxIterations () {
		return this.maxIterations;
	}

	private int seed = 1;
	public void setSeed (int value) {
		this.seed = value;
	}
	public int getSeed () {
		return this.seed;
	}

	private boolean trace = false;
	public void setTrace (boolean value) {
		this.trace = value;
	}
	public boolean getTrace () {
		return this.trace;
	}

	private boolean verbose = false;
	public void setVerbose (boolean value) {
		this.verbose = value;
	}
	public boolean getVerbose () {
		return this.verbose;
	}

	private boolean useresolution = false;

	public void setUseResolution (boolean value) {
		this.useresolution = value;
	}

	public boolean getUseResolution () {
		return this.useresolution;
	}

	public String getModuleName () {
		return "Random Sample";
	}

	public String getModuleInfo () {
		return "<p>      Overview: Generate random points in a space defined by a parameter space       input"+
			" until we push a user defined maximum number of points, we we       converge to a user defined"+
			" optima.    </p>    <p>      Detailed Description: This module will produce <i>Maximum Number"+
			" of       Iterations</i> points in parameter space, unless it converges before       generating"+
			" that many points. It will produce only one point per       invocation, unless it has already"+
			" produced all the points it is going to       and it is just waiting for scored points to come"+
			" back. This module will       not wait for a scored point to come back before producing the"+
			" next one,       it will produce as many poiints as it can, and it will remain enabled     "+
			"  until all those points are produced, or it has converged. The module       converges if a"+
			" score exceeds the property named <i>Objective Threashhold</i>. The Random Seed can be set to"+
			" a positive value to cause this module to       produce the same points, given the same inputs,"+
			" on multiple runs. <i>      Trace</i> and <i>Verbose Output</i> properties can be set to produce"+
			" a       little or a lot of console output respectively. If <i>Constrain       Resolution</i>"+
			" is not set, the resolution value from the parameter space       object will be ignored. We"+
			" can minimize the objective score by setting       the <i>Minimize Objective Score</i> property"+
			" to true.    </p>";
	}

	public String getInputName (int i) {
		switch(i) {
			case 0:
				return "Control Parameter Space";
			case 1:
				return "Example";
			default: return "NO SUCH INPUT!";
		}
	}

	public String getInputInfo (int i) {
		switch (i) {
			case 0: return "The Control Parameter Space to search";
			case 1: return "The scored parameter point from a previously pushed point in parameter     space.";
			default: return "No such input";
		}
	}

	public String[] getInputTypes () {
		String[] types = {"ncsa.d2k.modules.core.datatype.parameter.ParameterSpace","ncsa.d2k.modules.core.datatype.table.Example"};
		return types;
	}

	public String getOutputName (int i) {
		switch(i) {
			case 0:
				return "Parameter Point";
			case 1:
				return "Optimal Example Table";
			case 2:
				return "Complete Example Table";
			default: return "NO SUCH OUTPUT!";
		}
	}

	public String getOutputInfo (int i) {
		switch (i) {
			case 0: return "The next Parameter Point selected for evaluation";
			case 1: return "An example table consisting of only the Optimal Example(s)";
			case 2: return "An example table consisting of all Examples generated during optimization";
			default: return "No such output";
		}
	}

	public String[] getOutputTypes () {
		String[] types = {"ncsa.d2k.modules.core.datatype.parameter.ParameterPoint","ncsa.d2k.modules.core.datatype.table.ExampleTable","ncsa.d2k.modules.core.datatype.table.ExampleTable"};
		return types;
	}

	private Random randomNumberGenerator = null;

	/**
	 * Init the standard fields
	 */
	public void beginExecution () {
		pointsPushed = 0;
		examples = new ArrayList ();
		best = 0;
		randomNumberGenerator = new Random (seed);
		donePushing = false;
	}

	/**
	 * We trigger whenever we have a scored point as input. We process these first to ensure
	 * we don't fill pipes. Then if we have processed a space fully, we check to see if there
	 * is another space input to process. If none of the above, the we check to see if we still
	 * need to push parameter points out.
	 * @return
	 */
	public boolean isReady () {
		if (this.getFlags()[1] > 0)

		// We are always ready if there are examples to process.
			return true;
		else if (pointsPushed == 0 && this.getFlags()[0] > 0)

		// we have another space to process.
			return true;
		else if (pointsPushed != 0 && !donePushing)

		// We have already gotten the space, but have yet more points to push.
			return true;
		else
			return false;

	}


	ParameterSpace space;
	int best = 0;
	boolean donePushing = false;

	public void doit () {

		// If we have an example process it.
		if (this.getFlags()[1] > 0) {

			// We have an example, put it into our list of examples and we are done.
			// reading examples is given priority because this will keep the pipes from filling
			// up and overflowing.
			Example ex = (Example) this.pullInput (1);
			double newScore = ex.getOutputDouble (0);
			if (examples.size () > 0) {
				double bestScore = ((Example) examples.get (best)).getOutputDouble (0);
				if (minimizing) {
					if (newScore < bestScore) {
						best = examples.size ();
						if (newScore <= threashhold) {
							if (verbose) System.out.println ("\nMEANT THREASHHOLD.\n");
							donePushing = true;
						}
					}
				} else {
					if (newScore > bestScore) {
						best = examples.size ();
						if (newScore >= threashhold) {
							if (verbose) System.out.println ("\nMEANT THREASHHOLD.\n");
							donePushing = true;
						}
					}
				}
			} else {
				// this is the first example, it is always best.
				best = 0;
				if (minimizing) {
					if (newScore <= threashhold) {
						if (verbose) System.out.println ("\nMEANT THREASHHOLD.\n");
						donePushing = true;
					}
				} else {
					if (newScore >= threashhold) {
						if (verbose) System.out.println ("\nMEANT THREASHHOLD.\n");
						donePushing = true;
					}
				}
			}
			examples.add (ex);
			if (trace) {
				this.printExample (examples.size () + " - Acquired example: ", ex);
			}

			// Are we done processing examples for the current space?
			if (examples.size () == pointsPushed & donePushing) {

				// WE have processed all the example. Push the result tables, and get ready
				// for the next batch.
				this.pushOutput (this.getTable (examples), 2);
				Example winner = (Example) examples.get (best);
				ArrayList l = new ArrayList ();
				l.add (winner);
				this.pushOutput (this.getTable (l), 1);
				examples = new ArrayList ();
				pointsPushed = 0;
				donePushing = false;
				space = null;
				if (trace) {
					System.out.println ();
					this.printExample ("Winner: ", winner);
				}
			} else {
				if (!donePushing && pointsPushed != 0) {
					this.pushParameterPoint ();
				}
			}
		} else {

			// OK, so we don't have any examples to process, we must either have a new space
			// to start processing or more points to push.
			if (pointsPushed == 0)
				space = (ParameterSpace) this.pullInput (0);
			this.pushParameterPoint ();
		}
	}

	/**
	 * Push another paramter point, and update the accounting.
	 */
	private void pushParameterPoint () {
		int numParams = space.getNumParameters ();
		double[] point = new double[numParams];

		// Create one point in parameter space.
		for (int i = 0; i < numParams; i++) {
			double range = space.getMaxValue (i) - space.getMinValue (i);
			if (useresolution) {
				int resolution = space.getResolution (i) - 1;

				// This would be an error on the users part, resolution should never be zero.
				double increment;
				if (resolution <= 0) {
					increment = 0;
					resolution = 1;
				} else
					increment = range / (double) resolution;
				point[i] = space.getMinValue (i) + increment * randomNumberGenerator.nextInt (resolution+1);
			} else
				switch (space.getType (i)) {
					case ColumnTypes.DOUBLE:
						point[i] = space.getMinValue (i) + range * randomNumberGenerator.nextDouble ();
						break;
					case ColumnTypes.FLOAT:
						point[i] = space.getMinValue (i) + range * randomNumberGenerator.nextFloat ();
						break;
					case ColumnTypes.INTEGER:
						if ((int) range == 0) {
							point[i] = space.getMinValue (i);
						} else {
							point[i] = space.getMinValue (i) + randomNumberGenerator.nextInt ((int) (range + 1));
						}
						break;
					case ColumnTypes.BOOLEAN:
						if ((int) range == 0) {
							point[i] = space.getMinValue (i);
						} else {
							point[i] = space.getMinValue (i) + randomNumberGenerator.nextInt ((int) (range + 1));
						}
						break;
				}
		}
		String[] names = new String[space.getNumParameters ()];
		for (int i = 0; i < space.getNumParameters (); i++) {
			names[i] = space.getName (i);
		}
		ParameterPointImpl parameterPoint = (ParameterPointImpl) ParameterPointImpl.getParameterPoint (names, point);
		this.pushOutput (parameterPoint, 0);
		if (trace) System.out.println("Pushed point "+pointsPushed);
		if (verbose)
			this.printExample (pointsPushed + " - Pushed a point : ", parameterPoint);
		if (pointsPushed++ == maxIterations) {
			if (verbose) System.out.println ("\nPushed Max Points.\n");
			donePushing = true;
			if (trace) System.out.println("DONE");
		}
	}

	/**
	 * Given a two d array of doubles, create a table.
	 * @param ss the array of examples to include in the table.
	 * @return
	 */
	public ExampleTable getTable (ArrayList ss) {
		Example ex = (Example) ss.get (0);
		ExampleTable et = (ExampleTable) ex.getTable();
		int numInputs = et.getNumInputs (0);
		int numOutputs = et.getNumOutputs (0);
		int numColumns = numInputs + numOutputs;
		int numExamples = ss.size ();
		double[][] data = new double[numColumns][numExamples];
		String[] labels = new String[numColumns];
		Column[] cols = new Column[numColumns];

		// Construct the column nnames.
		for (int i = 0; i < numInputs; i++)
			labels[i] = et.getInputName (i);
		for (int i = 0; i < numOutputs; i++)
			labels[i + numInputs] = et.getOutputName (i);

		// First populate the double array entries corresponding to the inputs. Each input will go into
		// a different array so that it may arrive in a different column of the resulting table.
		for (int row = 0; row < numExamples; row++) {

			// Get the example for the row.
			ex = (Example) ss.get (row);

			// Put each input into the appropiate column.
			for (int column = 0; column < numInputs; column++) {
				data[column][row] = ex.getInputDouble (column);
			}
		}

		// Now do the outputs.
		for (int row = 0; row < numExamples; row++) {

			// Get the example for the row.
			ex = (Example) ss.get (row);

			// Put each input into the appropiate column.
			for (int column = 0; column < numOutputs; column++) {
				data[column + numInputs][row] = ex.getOutputDouble (column);
			}
		}

		// now create the columns
		for (int i = 0; i < numColumns; i++) {
			cols[i] = new DoubleColumn (data[i]);
			cols[i].setLabel (labels[i]);
		}

		// create the table.
		MutableTable mt = new MutableTableImpl (cols);
		et = mt.toExampleTable ();

		// Construct the input and output arrays.
		int[] inputs = new int[numInputs];
		for (int i = 0; i < numInputs; i++) inputs[i] = i;
		int[] outputs = new int[numOutputs];
		for (int i = 0; i < numOutputs; i++) outputs[i] = i + numInputs;
		et.setInputFeatures (inputs);
		et.setOutputFeatures (outputs);
		return et;
	}

	/**
	 * Just print an example.
	 * @param label
	 * @param ex
	 */
	private void printExample (String label, Example ex) {
		System.out.println (label);
		System.out.println ("  Inputs");
		ExampleTable et = (ExampleTable)ex.getTable();
		int ni = et.getNumInputs(0);
		int no = et.getNumOutputs(0);
		for (int i = 0; i < ni; i++) {
			System.out.println ("    " + et.getInputName (i) + " = " + ex.getInputDouble (i));
		}
		System.out.println ("  Outputs");
		for (int i = 0; i < no; i++) {
			System.out.println ("    " + et.getOutputName (i) + " = " + ex.getOutputDouble (i));
		}
	}
}
