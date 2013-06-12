package ncsa.d2k.modules.core.transform.table;

import ncsa.gui.Constrain;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import java.util.Random;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.beans.PropertyVetoException;

/**
	SimpleTrainTest.java
	The user to select a percentage of the table to be train and test. This
	module provide a custom gui that is a slider indicating the percent of the
	data to be used as training data.

   @author Tom Redman, revised Xiaolei Li, edited R.Aydt
*/
public class SimpleTrainTest extends DataPrepModule
{
	public final int RANDOM = 0;
	public final int SEQUENTIAL = 1;

	boolean debug = false;

	/**
	   Return the name of this module.
	   @return The name of this module.
        */
	public String getModuleName()
	{
		return "Simple Train Test";
	}

	/**
	   Return a description of the function of this module.
	   @return A description of this module.
	*/
	public String getModuleInfo() {
                String sb = "<p>Overview: " ;
                sb += "This module generates a training table and a testing table from the original table. ";

                sb += "</p><p>Detailed Description: " ;
                sb += "This module presents the user with a custom property editor which allows them to ";
                sb += "specify the percentages of the <i>Original Table</i> examples that should be used to build ";
                sb += "train and test tables.   The user can specify whether the train and test examples are selected ";
                sb += "at random or sequentially from the beginning (train data) and the end (test data) of the ";
                sb += "original examples.  If the examples are selected randomly, the user can specify the seed used ";
                sb += "by the random number generator. ";

                sb += "</p><p>";
		sb += "If the train and test percentages sum to more than 100 percent, some examples will appear in ";
                sb += "both the train and test tables.   To change the train and test percentages independently, ";
                sb += "select and drag the side arrows on the GUI display. This is useful when you wish to use more or ";
                sb += "less than 100% of the original examples in populating the train and test tables.  ";
                sb += "To change both percentages at the same time, select and drag the invisible line between ";
                sb += "the percentages. This technique insures that all of the original examples are used to populate ";
                sb += "either the train table or the test table, but no examples are used more than once.  ";

                sb += "</p><p>Data Type Restrictions: " ;
                sb += "Although this module works with tables containing any type of data, many supervised learning ";
                sb += "algorithms will work only on doubles. If one of these algorithms is to be used, the ";
                sb += "conversion to floating point data should take place prior to this module.   ";

                sb += "</p><p>Data Handling: ";
		sb += "This module does not change the original data. It creates an instance of an example table ";
		sb += "that manages the data data differently.  ";

                sb += "</p><p>Scalability: ";
                sb += "This module should scale linearly with the number of rows in the table.  The module needs to ";
                sb += "be able to allocate arrays of integers to hold the indices of the test and train examples.</p>";

                return sb.toString();
	}

	/**
	   Return a String array containing the datatypes the inputs to this
	   module.
	   @return The datatypes of the inputs.
	*/
	public String[] getInputTypes()
	{
		String[] types = {"ncsa.d2k.modules.core.datatype.table.Table"};
		return types;
	}

	/**
	   Return a description of a specific input.
	   @param i The index of the input
	   @return The description of the input
	*/
	public String getInputInfo(int i)
	{
		switch (i) {
		  case 0:
		    return "The table containing the data that will be split into training and testing examples.";
		  default:
		    return "No such input";
		}
	}

	/**
	   Return the name of a specific input.
	   @param i The index of the input.
	   @return The name of the input
	*/
	public String getInputName(int i)
	{
		switch(i) {
		  case 0:
	            return "Original Table";
		  default:
		    return "No such input.";
		}
	}

	/**
	   Return a String array containing the datatypes of the outputs of this
	   module.
	   @return The datatypes of the outputs.
	*/
	public String[] getOutputTypes()
	{
		String[] types = {"ncsa.d2k.modules.core.datatype.table.TrainTable",
			          "ncsa.d2k.modules.core.datatype.table.TestTable"};
		return types;
	}

	/**
	   Return the description of a specific output.
	   @param i The index of the output.
	   @return The description of the output.
	*/
	public String getOutputInfo(int i)
	{
		switch (i) {
		  case 0:
		    return "The table containing the training data.";
		  case 1:
		    return "The table containing the test data.";
		  default:
		    return "No such output";
		}
	}

	/**
	   Return the name of a specific output.
	   @param i The index of the output.
	   @return The name of the output
	*/
	public String getOutputName(int i)
	{
		switch(i) {
		  case 0:
		    return "Train Table";
		  case 1:
		    return "Test Table";
		  default:
	            return "No such output" ;
		}
	}

	/**
	 * Return a list of the property descriptions.
	 * @return a list of the property descriptions.
	 */
	public PropertyDescription [] getPropertiesDescriptions () {
		PropertyDescription [] pds = new PropertyDescription [4];
		pds[0] = new PropertyDescription ("trainPercent",
				"Train Percentage",
				"The percentage of the data to be used for training the model.");
		pds[1] = new PropertyDescription ("testPercent",
				"Test Percentage",
				"The percentage of the data to be used for testing the model.");
		pds[2] = new PropertyDescription ("samplingMethod",
				"Sampling Method",
				"The method to use when sampling the original examples.  "+
                                "The choices are: "+
				"<p>Random: Train and test examples are drawn randomly from the original table.</p>" +
				"<p>Sequential: Training examples are taken sequentially from the beginning of the "+
                                "original table and testing examples are " +
                                "taken sequentially from the end of the original table. ");
		pds[3] = new PropertyDescription ("seed",
				"Random Seed",
				"Seed for random sampling." +
				"Ignored if Random Sampling is not used.");
		return pds;
	}


	/** percent of dataset to use to test the model. */
	int testPercent = 50;

	/** percent of dataset to use to train the model. */
	int trainPercent = 50;

	/**
	  The type of sampling to use: random or sequential.
	  */
	int samplingMethod;

	/** the seed for the random number generator */
	int seed = 123;

	public void setSamplingMethod(int val)
	{
		samplingMethod = val;
	}

	public int getSamplingMethod()
	{
		return samplingMethod;
	}


	public void setSeed(int i) throws PropertyVetoException
	{
		if (i < 0) {
		    throw new PropertyVetoException ( " Value must be >= 0. ", null);
		}
		else {
			seed = i;
		}
	}

	public int getSeed()
	{
		return seed;
	}

	public void setTestPercent(int i) throws PropertyVetoException {
		if (i < 0 || i > 100) throw new PropertyVetoException("Test percentage must be between 0 and 100.", null);
		testPercent = i;
	}

	public int getTestPercent() {
		return testPercent;
	}

	public void setTrainPercent(int i)  throws PropertyVetoException {
		if (i < 0 || i > 100) throw new PropertyVetoException("Train percentage must be between 0 and 100.", null);
		trainPercent = i;
	}

	public int getTrainPercent() {
		return trainPercent;
	}

	/**
	   Perform the calculation.
	*/
	public void doit() throws Exception{
		Table orig = (Table)this.pullInput(0);

		// This is the number that will be test
		int nr = orig.getNumRows();
		int numTest = (nr*this.getTestPercent())/100;
		int numTrain = (nr*this.getTrainPercent())/100;
		if (numTest < 1 || numTrain < 1) {

                  //debug - vered
                  System.out.println("\n\n table type: " + orig.getClass().getName() + "\n\n");
                  System.out.println("\n\nnumber of rows " + orig.getNumRows() + "\n\n");
                  //end debug
		  throw new Exception (this.getAlias() +
			": The selected table was to small to be practical with the percentages specified.");
                }
		int [] test = new int [numTest];
		int [] train = new int [numTrain];

		// only keep the first N rows
		int [] random = new int [nr];
		for(int i = 0; i < nr; i++) {
			random[i] = i;
		}

		// If we are to select the examples for test and train at random,
		// we need to to shuffle the indices.
		if(samplingMethod == RANDOM) {
			// Shuffle the indices randomly.
			Random r = new Random(seed);
			for(int i = 0; i < nr; i++) {
				int which = (int)(r.nextDouble()*(double)nr);
				if (i != which) {
					int s = random[which];
					random[which] = random[i];
					random[i] = s;
				}
			}
		}

		// do the train assignment, from the start of the array of indices.
		for(int i = 0; i < numTrain; i++) {
			train[i] = random[i];
		}

		// do the test assignment, from the end of the array of indices.
		for(int i = numTest-1, j = nr - 1; i >= 0; i--, j--) {
		   test[i] = random[j];
		}
		if (debug) {
			System.out.println("test set");
			for(int i = 0; i < test.length; i++) {
				if (i > 0) System.out.print(",");
				System.out.print(test[i]);
			}

			// do the train assignment, from the end of the array of indices.
			System.out.println("train set");
			for(int i = 0; i < train.length; i++) {
				if (i > 0) System.out.print(",");
				System.out.print(train[i]);
			}
		}
		ExampleTable et = orig.toExampleTable();
		et.setTestingSet(test);
		et.setTrainingSet(train);
		this.pushOutput(et.getTrainTable(), 0);
		this.pushOutput(et.getTestTable(), 1);

	}

	/**
	 * return a reference a custom property editor to select the percent test
	 * and train.
	 * @return a reference a custom property editor
	 */
	public CustomModuleEditor getPropertyEditor() {
		return new JSetPercentage(this);
	}

	/**
	 * This panel displays the editable properties of the SimpleTestTrain modules.
	 * @author Thomas Redman
	 */
	class JSetPercentage extends JPanel implements CustomModuleEditor,
	ActionListener {

		/** the module to modify. */
		SimpleTrainTest module;

		/** a slider indicating the percent test. */
		//JSlider slider = new JSlider(1,99);
		TestTrainSlider slider = null;

		/** sequential vs random sampling. */
	    JLabel m_sampling_method_label = null;
  		javax.swing.JComboBox m_sampling_methods = null;

	    String[] sampling_method_labels = {"Random", "Sequential"};
	    String[] sampling_method_desc = {"Randomly sample to build train and test tables. ",
		   "First entries in original table used as training examples and last entries used as testing examples."};

		/** if random sampling is used, these will be exposed. */
	    JLabel m_seed_label = null;
	    JTextField m_seed = null;

		/**
		 * Given the module to change.
		 * @param stt the module.
		 */
		JSetPercentage (SimpleTrainTest stt) {
			Font tmp = new Font ("Serif", Font.PLAIN, 12);
			this.module = stt;
			this.setLayout(new GridBagLayout());
			slider = new TestTrainSlider(stt.getTestPercent(), stt.getTrainPercent());

			m_sampling_method_label = new JLabel();
			m_sampling_method_label.setText("Sample Method: ");
			m_sampling_method_label.setToolTipText("Select method of sampling.");
			m_sampling_method_label.setFont(tmp);

			m_sampling_methods = new JComboBox(sampling_method_labels);
			m_sampling_methods.setEditable(false);
			m_sampling_methods.setToolTipText(sampling_method_desc[stt.getSamplingMethod()]);
			m_sampling_methods.setSelectedIndex(stt.getSamplingMethod());
			m_sampling_methods.setFont(tmp);
			m_sampling_methods.addActionListener(this);

			m_seed_label = new JLabel("Random Seed: ");
			m_seed_label.setFont(tmp);
			m_seed = new JTextField(Integer.toString(stt.getSeed()), 5);
			m_seed.setFont(tmp);

			JLabel label = new JLabel("  Select train and test percentages:  ");
			label.setFont(tmp);
			Constrain.setConstraints(this, label, 0, 0, 1, 1, GridBagConstraints.NONE,
									 GridBagConstraints.WEST, 0.0, 0.0);
			Constrain.setConstraints(this, slider, 0, 1, 1, 1, GridBagConstraints.NONE,
									 GridBagConstraints.CENTER, 0.0, 0.0);
			Constrain.setConstraints(this, label, 0, 2, 1, 1, GridBagConstraints.NONE,
									 GridBagConstraints.WEST, 0.0, 0.0);

			Constrain.setConstraints(this, m_sampling_method_label, 0, 3, 1, 1, GridBagConstraints.NONE,
									 GridBagConstraints.WEST, 0.0, 0.0);

			Constrain.setConstraints(this, m_sampling_methods, 0, 3, 1, 1, GridBagConstraints.NONE,
									 GridBagConstraints.EAST, 0.0, 0.0);

			Constrain.setConstraints(this, m_seed_label, 0, 4, 1, 1, GridBagConstraints.NONE,
									 GridBagConstraints.WEST, 0.0, 0.0);

			Constrain.setConstraints(this, m_seed, 0, 4, 1, 1, GridBagConstraints.NONE,
									 GridBagConstraints.EAST, 0.0, 0.0);

			if (stt.getSamplingMethod() == stt.RANDOM) {
				m_seed.setEnabled(true);
				m_seed_label.setEnabled(true);
			}
			else {
				m_seed.setEnabled(false);
				m_seed_label.setEnabled(false);
			}
		}

		/**
		 * Update the fields of the module
		 * @return a string indicating why the properties could not be set, or null if successfully set.
		 */
		public boolean updateModule() throws Exception {
			int testpercent = slider.getTest();
			int trainpercent = slider.getTrain();

			// Percentages in range 0 to 100.
			if (testpercent < 0 || testpercent > 100) {
				throw new PropertyVetoException("Test percentage must be in range 0 to 100.", null);
			}
			if (trainpercent < 0 || trainpercent > 100) {
				throw new PropertyVetoException("Train percentage must be in range 0 to 100.", null);
			}
			boolean changed = false;
			// set up the properties.
			if (module.getTestPercent() != testpercent) {
				module.setTestPercent(testpercent);
				changed = true;
			}

			if (module.getTrainPercent() != trainpercent) {
				module.setTrainPercent(trainpercent);
				changed = true;
			}
			if (module.getSamplingMethod() != m_sampling_methods.getSelectedIndex()) {
				module.setSamplingMethod(this.m_sampling_methods.getSelectedIndex());
				changed = true;
			}

			try {
				int seed = Integer.parseInt(m_seed.getText());

				if (module.getSeed() != seed) {
					module.setSeed(seed);
					changed = true;
				}
			}
			catch (Exception e) {
				throw new PropertyVetoException("Error in seed field: "
						+ e.getMessage(), null);
			}

			return changed;
		}

		public void actionPerformed(ActionEvent e)
		{
			Object src = e.getSource();

			if (src == this.m_sampling_methods) {
				JComboBox cb = (JComboBox) src;
				m_sampling_methods.setToolTipText(sampling_method_desc[cb.getSelectedIndex()]);

				if (cb.getSelectedIndex() == module.RANDOM) {
					m_seed.setEnabled(true);
					m_seed_label.setEnabled(true);
				}
				else {
					m_seed.setEnabled(false);
					m_seed_label.setEnabled(false);
				}
			}
		}



		class TestTrainSlider extends JComponent implements MouseMotionListener, MouseListener {

			final int THUMB_SIZE=5;
			final String TEST="test";
			final String TRAIN="train";

			/** identifies the mousing operations. */
			final int SET_BOTH = 0;
			final int SET_TEST = 1;
			final int SET_TRAIN = 2;
			int op = SET_BOTH;

			/** the initial position of the thumb. */
			int trainPercentage = 50;

			/** the initial position of the thumb. */
			int testPercentage = 50;

			/** the height of text. */
			int text_height;

			/** the text ascent. */
			int text_ascent;
			/** the text ascent. */
			int text_descent;

			/** the width of the rendered width string. */
			int test_string_width;

			/** the width of the rendered train string. */
			int train_string_width;

			FontMetrics fm = null;
			TestTrainSlider (int pos_test, int pos_train) {
				super();
				this.setFont(new Font("Serif", Font.BOLD, 12));
				fm = this.getFontMetrics(this.getFont());
				this.text_height = fm.getHeight();
				this.text_ascent = fm.getAscent();
				this.text_descent = fm.getDescent();
				this.test_string_width = fm.stringWidth(TEST);
				this.train_string_width = fm.stringWidth(TRAIN);
				this.setTest(pos_test);
				this.setTrain(pos_train);
				this.addMouseMotionListener(this);
				this.addMouseListener(this);
				this.setBackground(Color.yellow);
			}

			/**
			 * This component is always 100 x 150
			 * @return
			 */
			final private int THEIGHT = 202;
			final private int RHEIGHT = THEIGHT-2;
			public Dimension getMinimumSize() {
				return new Dimension (120, THEIGHT);
			}
			public Dimension getPreferredSize() {
				return this.getMinimumSize();
			}
			public Dimension getMaximumSize() {
				return this.getMinimumSize();
			}

			/**
			 * return the percent test.
			 * @return
			 */
			int getTrain() {
				int current = trainPercentage-1;
				current *= 100;
				current /= RHEIGHT;
				return current;
			}

			/**
			 * Set the test value. The percentage passed in is converted to an offset.
			 * @param val the percent test.
			 */
			private void setTrain(int val) {
				trainPercentage = val;
				trainPercentage = trainPercentage * 2;
				trainPercentage++;
			}

			/**
			 * return the percent test.
			 * @return
			 */
			int getTest() {
				int current = testPercentage - 1;
				current = current / 2;
				return 100 - current;
			}

			/**
			 * Set the test value. The percentage passed in is converted to an offset.
			 * @param val the percent test.
			 */
			private void setTest(int val) {
				testPercentage = 100 - val;
				testPercentage = (testPercentage * 2);
				testPercentage++;
			}

			/**
			 * Render the thumb
			 * @param g the graphics object.
			 * @param x1 the x coord of the first point.
			 * @param y1 the y coord of the first point.
			 * @param x2 the x coord of the second point.
			 * @param y2 the y coord of the second point.
			 * @param x3 the x coord of the last point.
			 * @param y3 the y coord of the last point.
			 * @param fill
			 */
			private void paintThumb (Graphics g, int x1, int y1, int x2, int y2, int x3, int y3, boolean fill) {
				int [] Xs = new int [3];
				int [] Ys = new int [3];
				Xs[0] = x1; Xs[1] = x2; Xs[2] = x3;
				Ys[0] = y1; Ys[1] = y2; Ys[2] = y3;
				if (fill) g.fillPolygon(Xs, Ys, 3);
				else g.drawPolygon(Xs, Ys, 3);
			}

			/**
			 * paint the thumb indicator.
			 * @param g
			 */
			private void paintThumb(Graphics g) {
				int comp_width = this.getSize().width;
				int comp_height = this.getSize().height;

				// draw the test thumb

				g.setColor(Color.red.brighter().brighter());
				this.paintThumb(g, 0, trainPercentage - THUMB_SIZE, THUMB_SIZE, trainPercentage,
								0, trainPercentage + THUMB_SIZE, true);

				// draw the line.
				int halfway = 4;
				int ploc = this.trainPercentage + this.text_descent;
				if (ploc < this.text_ascent)
					ploc = this.text_ascent;
				if (ploc > (comp_height - this.text_descent))
					ploc = comp_height - this.text_descent;
				String percent = " "+Integer.toString(this.getTrain())+"% ";
				int stringwidth = fm.stringWidth(percent);
				g.drawString(percent, halfway, ploc);

				// draw the training thumb
				g.setColor(Color.blue.brighter().brighter());
				int right = comp_width - THUMB_SIZE;
				this.paintThumb(g, comp_width, testPercentage - THUMB_SIZE, right, testPercentage,
								comp_width, testPercentage + THUMB_SIZE, true);

				// draw the line.
				ploc = testPercentage + this.text_descent;
				if (ploc < this.text_ascent)
					ploc = this.text_ascent;
				if (ploc > (comp_height - this.text_descent))
					ploc = comp_height - this.text_descent;
				percent = " "+Integer.toString(this.getTest())+"% ";
				stringwidth = fm.stringWidth(percent);
				halfway = right - 4 - stringwidth;
				g.drawString(percent, halfway, ploc);
			}


			final Color lightRed = new Color(255,220,220);
			final Color lightYellow = new Color(225,255,220);
			final Color lightBlue = new Color(220,220,255);

			/**
			 * paint a background color indicating what is test, what is
			 * train, and what is resampled.
			 * @param g
			 */
			private void paintBacking (Graphics g) {
				int right = this.getSize().width - (THUMB_SIZE*2);
				int bottom = this.getSize().height - 2;

				// fill the test percent
				g.setColor(this.lightRed);
				g.fillRect(this.THUMB_SIZE, 1, right, this.trainPercentage-1);

				g.setColor(this.lightBlue);
				g.fillRect(this.THUMB_SIZE, this.testPercentage, right, bottom-this.testPercentage);
				if (this.testPercentage == this.trainPercentage) return;
				if (this.testPercentage < this.trainPercentage) {
					g.setColor(this.lightYellow);
				} else {
					g.setColor(Color.white);
				}
				int amount = this.trainPercentage - this.testPercentage;
				g.fillRect(this.THUMB_SIZE, this.testPercentage, right, amount);
				if (this.testPercentage < this.trainPercentage) {
					g.setColor(Color.black);
					int wdth = fm.stringWidth("resampled");
					g.drawString ("resampled", this.THUMB_SIZE + 24,
								  this.testPercentage + this.text_ascent);
				}
		   }

			/**
			 * Paint the component, a representation of a table of data.
			 * @param g
			 */
			final int vinc = 7;
			final int hinc = 40;
			public void paintComponent(Graphics g) {
				g.setFont (this.getFont());
				int comp_width = this.getSize().width;
				int comp_height = this.getSize().height;
				this.paintBacking(g);
				g.setColor(Color.gray);

				// paint the box around the document.
				g.drawLine (THUMB_SIZE, 0, comp_width - THUMB_SIZE, 0);
				g.drawLine (comp_width - THUMB_SIZE, 0, comp_width - THUMB_SIZE, comp_height - 1);
				g.drawLine (comp_width - THUMB_SIZE, comp_height - 1, THUMB_SIZE, comp_height - 1);
				g.drawLine (THUMB_SIZE, comp_height - 1, THUMB_SIZE, 0);

				// paint some lines to make it look like a table.
				for (int where = vinc + 1 ; where < comp_height ; where += vinc) {
					g.drawLine(THUMB_SIZE, where, comp_width - THUMB_SIZE, where);
				}

				for (int where = hinc + 1 ; where < (comp_width-THUMB_SIZE) ; where += hinc) {
					g.drawLine(where, 1, where, comp_height - 1);
				}

				// paint some lines in there.
				g.setColor(Color.black);
				g.drawString(this.TRAIN, this.THUMB_SIZE + 24,
							 this.text_ascent+1);
				g.drawString(this.TEST, this.THUMB_SIZE + 24,
							 comp_height - (this.text_descent+1));
				this.paintThumb(g);
			}

			// mouse events.

			/**
			 * If the mouse is clicked, the thumb jumps to the vertical point clicked.
			 * @param mouseEvent
			 */
			public void mouseClicked(MouseEvent mouseEvent){
				this.identifyOperation(mouseEvent.getX());
				this.mouseDragged(mouseEvent);
			}
			private void identifyOperation(int x) {
				if (x > this.THUMB_SIZE) {
					if (x < this.getSize().width - THUMB_SIZE) {
						this.op = SET_BOTH;
					} else {
						this.op = SET_TRAIN;
					}
				} else {
					this.op = SET_TEST;
				}
			}

			/**
			 * If the mouse is pressed, the thumb jumps to the vertical point clicked.
			 * @param mouseEvent
			 */
			public void mousePressed(MouseEvent mouseEvent){
				this.identifyOperation(mouseEvent.getX());
				this.mouseDragged(mouseEvent);
			}
			public void mouseReleased(MouseEvent mouseEvent){}
			public void mouseEntered(MouseEvent mouseEvent){}
			public void mouseExited(MouseEvent mouseEvent){}

			/**
			 * The mouse has moved, set the field identified when the operation
			 * started to the new value, if necessary.
			 * @param mouseEvent
			 */
			public void mouseDragged(MouseEvent mouseEvent) {
				int y = mouseEvent.getY();
				int height = this.getSize().height - 1;
				if (y > height) y = height;
				if (y < 1) y = 1;
				switch (op) {
					case SET_BOTH:
						this.trainPercentage = y;
						this.testPercentage = y;
						break;
					case SET_TEST:
						this.trainPercentage = y;
						break;
					case SET_TRAIN:
					   this.testPercentage = y;
					   break;
			   }
			   this.repaint();
			}
			public void mouseMoved(MouseEvent mouseEvent){}
		}
	}
}

// Start QA Comments:
// 7/18/03 - use seed in Random() call.  Updated descriptions and labels. (ra)
// End QA Comments
