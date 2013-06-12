package ncsa.d2k.modules.core.optimize.ga.emo.gui;

import ncsa.d2k.modules.core.optimize.ga.emo.*;
import ncsa.d2k.modules.core.optimize.ga.nsga.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.vis.widgets.*;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.core.optimize.ga.emo.functions.*;
import ncsa.d2k.modules.core.optimize.util.*;

import ncsa.d2k.userviews.swing.*;

import ncsa.d2k.gui.*;
import ncsa.gui.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import javax.swing.plaf.basic.*;
import java.util.*;

/**
 * A visualization for a multi-objective population in EMO.  Four JTables are
 * used to show the current population, the last cumulative population, the
 * cumulative popualtion from two runs ago, and the cumulative population from
 * three runs ago.  The fitness functions of these populations are displayed in
 * scatterplots.  The rows and columns of the JTables can be changed, and it is
 * possible to plot any combinations of fitness functions on the scatter plots.
 *
 * The scatter plots in the JTables are FitnessPlots.  FitnessPlots allow the
 * user to select points by drawing a box around an area.  The "View Selected"
 * button can then be pressed to view the selected individuals in a table.
     * Selection is only enabled on plots that are not currently being updated; thus
 * the currently evaluating population must be paused in order for selection to
 * be enabled.  The cumulative populations have selection enabled all the time,
 * since they are not being updated continuously.
 */
public class MOVis
    extends UIModule {

  public String[] getInputTypes() {
    return new String[] {
        "ncsa.d2k.modules.core.optimize.ga.Population"};
  }

  // 0 = current pop
  // 1 = view decision vars
  // 2 = view genes

  public String[] getOutputTypes() {
    return new String[] {
        "ncsa.d2k.modules.core.optimize.ga.Population"};
    //"ncsa.d2k.modules.core.optimize.ga.Population",
    //"ncsa.d2k.modules.core.optimize.ga.Population"};
  }

  public String getInputInfo(int i) {
    return "";
  }

  public String getInputName(int i) {
    return "EMOPopulation";
  }

  public String getOutputInfo(int i) {
    return "";
  }

  public String getOutputName(int i) {
    if (i == 0) {
      return "EMOPopulation";
    }
    /*    if (i == 1) {
          return "Decision Variables";
        }
        if (i == 2) {
          return "Genes";
        }*/
    return "";
  }
  
  public String getModuleName() {
    return "Multi-Objective Visualization";
  }

  public String getModuleInfo() {
    String s =
        "A visualization for a multi-objective population in EMO.  Four JTables are";
    s +=
        " used to show the current population, the last cumulative population, the";
    s +=
        " cumulative popualtion from two runs ago, and the cumulative population from";
    s +=
        " three runs ago.  The fitness functions of these populations are displayed in";
    s +=
        " scatterplots.  The rows and columns of the JTables can be changed, and it is";
    s +=
        " possible to plot any combinations of fitness functions on the scatter plots.";
    s +=
        " The scatter plots in the JTables are FitnessPlots.  FitnessPlots allow the";
    s +=
        " user to select points by drawing a box around an area.  The View Selected";
    s +=
        " button can then be pressed to view the selected individuals in a table.";
    s +=
        " Selection is only enabled on plots that are not currently being updated; thus";
    s +=
        " the currently evaluating population must be paused in order for selection to";
    s +=
        " be enabled.  The cumulative populations have selection enabled all the time,";
    s += " since they are not being updated continuously.";
    return s;
  }

  protected UserView createUserView() {
    return new MOView();
  }

  public String[] getFieldNameMapping() {
    return null;
  }

  public void beginExecution() {
    if (this.userView != null) {
      ( (MOView) userView).initView(this);
    }
  }
  
  /*  Return an array with information on the properties the user may update.
   *  @return The PropertyDescriptions for properties the user may update.
   */
  public PropertyDescription[] getPropertiesDescriptions() {
    return new PropertyDescription[0];
  }
  

  protected class MOView
      extends JUserPane {

    /** the fitness tables that hold the values of the FF to be graphed */
    protected FitnessTable[] fitnessTables;
    /** the populations: current, last cumulative run, etc */
    protected NsgaPopulation[] populations;
    /** the gui components for the populations */
    protected RunView[] runViews;

    protected boolean paused;

    protected JButton continueButton;
    protected JButton pauseButton;
    //protected JButton viewDecisionVariables;
    //protected JButton viewGenes;

    protected int numDecisionVariables;
    protected int numObjectives;

    protected static final int NUM_VIEWS = 4;
    protected static final int CURRENT = 0;
    protected static final int CUMUL_ONE = 1;
    protected static final int CUMUL_TWO = 2;
    protected static final int CUMUL_THREE = 3;

    protected int run;

    /** temporarily hold the very first pop, so that we can make a cumulative
     * from it and the second pop.
     */
    protected NsgaPopulation tmpPopulation;

    protected static final String CUMULATIVE = "Cumulative ";
    protected static final String RUN = "Run: ";
    protected static final String POP_SIZE = "Population Size: ";
    protected static final String NUM_SOL =
        "Number of Nondominated Solutions: ";
    
    boolean binaryIndividuals;

    /**
     * create the gui components
     * @param vm
     */
    public void initView(ViewModule vm) {
      this.removeAll();
      run = 0;
      paused = false;
      // this will hold the populations
      populations = new NsgaPopulation[NUM_VIEWS];
      // this will hold the tables to be graphed
      fitnessTables = new FitnessTable[NUM_VIEWS];
      // this will hold the gui components
      runViews = new RunView[NUM_VIEWS];

      // initialize everything
      for (int i = 0; i < NUM_VIEWS; i++) {
        populations[i] = null;
        fitnessTables[i] = null;
        runViews[i] = new RunView(i);
      }
      setLayout(new BorderLayout());

      // add the run views
      JPanel bg = new JPanel(new GridLayout(2, 2));
      bg.add(runViews[CURRENT]);
      bg.add(runViews[CUMUL_ONE]);
      bg.add(runViews[CUMUL_TWO]);
      bg.add(runViews[CUMUL_THREE]);

      // put the run views into a scroll pane
      JScrollPane jsp = new JScrollPane(bg);
      jsp.setPreferredSize(new Dimension(750, 425));
      // add the scroll pane to this
      add(jsp, BorderLayout.CENTER);

      // add the buttons
      continueButton = new JButton("Continue");
      continueButton.setToolTipText("Continue execution");
      pauseButton = new JButton("Pause");
      pauseButton.setToolTipText("Pause execution");
      /*viewDecisionVariables = new JButton("View Decision Variables");
             viewDecisionVariables.setToolTipText(
          "View the decision variables of the current population");
             // when View Decision Variables is pressed, push the current pop
             // out on pipe 1
             viewDecisionVariables.addActionListener(new AbstractAction() {
        public void actionPerformed(ActionEvent ae) {
//          pushOutput(populations[CURRENT], 1);
          MutableTable mt = new MutableTableImpl();
          NsgaPopulation pop = populations[CURRENT];
          Parameters params = ( (EMOPopulation) pop).getParameters();
          // now, for each decision variable, add a column
          //int numDecisionVariables = params.decisionVariables.getNumVariables();
          int numDecisionVariables = params.getNumDecisionVariables();
          int size = pop.size();
          double[][] dvs = new double[numDecisionVariables][size];
          for (int i = 0; i < size; i++) {
            double[] vals = pop.getMember(i).toDoubleValues();
            for (int j = 0; j < numDecisionVariables; j++) {
              dvs[j][i] = vals[j];
            }
          }
          java.util.List dvars = params.getDecisionVariables();
          for (int i = 0; i < numDecisionVariables; i++) {
            DoubleColumn dc = new DoubleColumn(dvs[i]);
            DecisionVariable dv = (DecisionVariable) dvars.get(i);
            //dc.setLabel(params.decisionVariables.getVariableName(i));
            dc.setLabel(dv.getName());
            mt.addColumn(dc);
          }
          ( (MutableTableImpl) mt).print();
        }
             });
             viewGenes = new JButton("View Genes");
           viewGenes.setToolTipText("View the genes of the current population");
             // when View Genes is pressed, push the current pop
             // out on pipe 2
             viewGenes.addActionListener(new AbstractAction() {
        public void actionPerformed(ActionEvent ae) {
          pushOutput(populations[CURRENT], 2);
        }
             });*/

      continueButton.setEnabled(false);
      //viewDecisionVariables.setEnabled(false);
      //viewGenes.setEnabled(false);
      continueButton.addActionListener(new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
          continueExecution();
        }
      });

      pauseButton.addActionListener(new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
          pauseExecution();
        }
      });

      JButton stopButton = new JButton("Stop");
      stopButton.setToolTipText("Stop execution");
      stopButton.addActionListener(new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
          viewCancel();
        }
      });
      // add the buttons
      JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
      buttonPanel.add(continueButton);
      buttonPanel.add(pauseButton);
      buttonPanel.add(stopButton);
      //buttonPanel.add(viewDecisionVariables);
      //buttonPanel.add(viewGenes);
      add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * pause..  diable pause button.  enable continue button,
     * view decision variables, and view genes.  enable selection
     * on the run view that shows the current pop
     */
    protected void pauseExecution() {
      // enable selection on the fitness plot
      paused = true;
      pauseButton.setEnabled(false);
      continueButton.setEnabled(true);
      //viewDecisionVariables.setEnabled(true);
      //viewGenes.setEnabled(true);
      runViews[CURRENT].enableSelection();
    }

    /**
     * continue after a pause.  enable the pause button.  disable continue,
     * view decision variables, and view genes.  disable selection on the
     * run view that shows the current pop
     */
    protected void continueExecution() {
      paused = false;
      continueButton.setEnabled(false);
      //viewDecisionVariables.setEnabled(false);
      //viewGenes.setEnabled(false);
      pauseButton.setEnabled(true);
      runViews[CURRENT].disableSelection();
      runViews[CURRENT].removeSelection();
      pushOutput(populations[CURRENT], 0);
    }

    public void setInput(Object o, int i) {
      NsgaPopulation p = (NsgaPopulation) o;
      this.binaryIndividuals = ((EMOPopulation)p).getParameters().createBinaryIndividuals;

      // if this is a new population, reset the cumulative populations
      if (p != populations[CURRENT]) {

        // CUMUL_THREE becomes CUMUL_TWO
        populations[CUMUL_THREE] = populations[CUMUL_TWO];
        fitnessTables[CUMUL_THREE] = fitnessTables[CUMUL_TWO];
        if (populations[CUMUL_THREE] != null) {
          runViews[CUMUL_THREE].disableSelection();
          runViews[CUMUL_THREE].setPopulationTable(fitnessTables[CUMUL_THREE]);
          runViews[CUMUL_THREE].redraw();

          // set the labels..
          runViews[CUMUL_THREE].runLabel.setText(
              runViews[CUMUL_TWO].runLabel.getText());
          runViews[CUMUL_THREE].sizeLabel.setText(
              runViews[CUMUL_TWO].sizeLabel.getText());
          runViews[CUMUL_THREE].solutionsLabel.setText(
              runViews[CUMUL_TWO].solutionsLabel.getText());
          runViews[CUMUL_THREE].enableSelection();
        }

        // CUMUL_TWO becomes CUMUL_ONE
        populations[CUMUL_TWO] = populations[CUMUL_ONE];
        fitnessTables[CUMUL_TWO] = fitnessTables[CUMUL_ONE];
        if (populations[CUMUL_TWO] != null) {
          runViews[CUMUL_TWO].disableSelection();
          runViews[CUMUL_TWO].setPopulationTable(fitnessTables[CUMUL_TWO]);
          runViews[CUMUL_TWO].redraw();

          // set the labels..
          runViews[CUMUL_TWO].runLabel.setText(
              runViews[CUMUL_ONE].runLabel.getText());
          runViews[CUMUL_TWO].sizeLabel.setText(
              runViews[CUMUL_ONE].sizeLabel.getText());
          runViews[CUMUL_TWO].solutionsLabel.setText(
              runViews[CUMUL_ONE].solutionsLabel.getText());
          runViews[CUMUL_TWO].enableSelection();
        }

        // CUMUL_ONE becomes
        if (populations[CUMUL_ONE] == null) {
          //if(tmpPopulation == null)
          if (run == 1) {
            tmpPopulation = populations[CURRENT];
          }
          else if (run > 1) {
            // make a new NewNsgaPopulation from tmpPopulation and current pop
            populations[CUMUL_ONE] = new EMONsgaPopulation(tmpPopulation,
                populations[CURRENT]);
            ((EMONsgaPopulation)populations[CUMUL_ONE]).setParameters( ((EMOPopulation)tmpPopulation).getParameters());
            ( (NewNsgaPopulation) populations[CUMUL_ONE]).filtering();
            // set tmpPopulation to be null.  we don't need it anymore
            tmpPopulation = null;

            // copy pop into a fitness table
            FitnessTable ft = new FitnessTable(populations[CUMUL_ONE].size(),
                                               populations[CUMUL_ONE].
                                               getNumObjectives());
            int rankZero = copyFitnessValuesToFitnessTable(populations[
                CUMUL_ONE],
                ft);
            fitnessTables[CUMUL_ONE] = ft;

            runViews[CUMUL_ONE].disableSelection();
            // set the fitness table
            runViews[CUMUL_ONE].setPopulationTable(fitnessTables[CUMUL_ONE]);

            // set the labels
            StringBuffer sb = new StringBuffer(CUMULATIVE);
            sb.append(RUN);
            sb.append(Integer.toString(run));

            runViews[CUMUL_ONE].runLabel.setText(sb.toString());

            sb = new StringBuffer(POP_SIZE);
            sb.append(Integer.toString(populations[CUMUL_ONE].size()));
            runViews[CUMUL_ONE].sizeLabel.setText(sb.toString());
            sb = new StringBuffer(NUM_SOL);
            sb.append(Integer.toString(rankZero));
            runViews[CUMUL_ONE].solutionsLabel.setText(sb.toString());
            runViews[CUMUL_ONE].redraw();
            runViews[CUMUL_ONE].enableSelection();
          }
        }
        else {
          populations[CUMUL_ONE] = new EMONsgaPopulation(populations[CUMUL_ONE],
              populations[CURRENT]);
            ((EMONsgaPopulation)populations[CUMUL_ONE]).setParameters( ((EMOPopulation)populations[CUMUL_ONE]).getParameters());
          ( (NewNsgaPopulation) populations[CUMUL_ONE]).filtering();

          // copy pop into a fitness table
          FitnessTable ft = new FitnessTable(populations[CUMUL_ONE].size(),
                                             populations[CUMUL_ONE].
                                             getNumObjectives());
          int rankZero = copyFitnessValuesToFitnessTable(populations[CUMUL_ONE],
              ft);
          fitnessTables[CUMUL_ONE] = ft;

          runViews[CUMUL_ONE].disableSelection();
          // set the fitness table
          runViews[CUMUL_ONE].setPopulationTable(fitnessTables[CUMUL_ONE]);

          // set the labels
          StringBuffer sb = new StringBuffer(CUMULATIVE);
          sb.append(RUN);
          sb.append(Integer.toString(run));

          runViews[CUMUL_ONE].runLabel.setText(sb.toString());
          sb = new StringBuffer(POP_SIZE);
          sb.append(Integer.toString(populations[CUMUL_ONE].size()));
          runViews[CUMUL_ONE].sizeLabel.setText(sb.toString());
          sb = new StringBuffer(NUM_SOL);
          sb.append(Integer.toString(rankZero));
          runViews[CUMUL_ONE].solutionsLabel.setText(sb.toString());
          runViews[CUMUL_ONE].redraw();
          runViews[CUMUL_ONE].enableSelection();
        }

        // reset the current population
        populations[CURRENT] = p;
        int currentSize = p.size();
        int numObj = p.getNumObjectives();
        fitnessTables[CURRENT] = new FitnessTable(currentSize, numObj);

        runViews[CURRENT].setPopulationTable(fitnessTables[CURRENT]);

        run++;
        StringBuffer rn = new StringBuffer(RUN);
        rn.append(run);
        runViews[CURRENT].runLabel.setText(rn.toString());

        StringBuffer sz = new StringBuffer(POP_SIZE);
        sz.append(currentSize);
        runViews[CURRENT].sizeLabel.setText(sz.toString());

        if (run == 1) {
          String[] objectiveNames = new String[numObj];
          for (int j = 0; j < numObj; j++) {
            objectiveNames[j] = p.getObjectiveConstraints()[j].getName();
          }
          for (int j = 0; j < 4; j++) {
            runViews[j].om.setObjectiveNames(objectiveNames);
          }
        }
      }

      // now copy the fitness values from the pop into the table
      FitnessTable ft = fitnessTables[CURRENT];
      int numRankZero = copyFitnessValuesToFitnessTable(p, ft);
      StringBuffer rank = new StringBuffer(NUM_SOL);
      rank.append(numRankZero);
      runViews[CURRENT].solutionsLabel.setText(rank.toString());
      runViews[CURRENT].redraw();

      if (p.getCurrentGeneration() == p.getMaxGenerations() - 1) {
        pauseExecution();
      }
      if (!paused) {
        pushOutput(p, 0);
      }
    }

    /**
     * Copy the values of the fitness functions into the table and return
     * the number of rank 0 solutions
     * @param pop
     * @return
     */
    private int copyFitnessValuesToFitnessTable(NsgaPopulation pop,
                                                FitnessTable ft) {
      int size = pop.size();
      int numObj = pop.getNumObjectives();
      int numRankZero = 0;
      for (int j = 0; j < size; j++) {
        NsgaSolution sol = (NsgaSolution) pop.getMember(j);
        if (sol.getRank() == 0) {
          numRankZero++;
        }
        for (int k = 0; k < numObj; k++) {
          ft.setFitnessValue(sol.getObjective(k), j, k);
        }
      }
      return numRankZero;
    }

    // class that holds the jtables and the info area
    protected class RunView
        extends JPanel {
      protected int ROW_HEIGHT = 150;
      protected int ROW_WIDTH = 150;
      protected int NUM_ROW = 2;
      protected int NUM_COL = 2;

      protected FitnessPlotMatrix om;

      protected JLabel runLabel;
      protected JLabel sizeLabel;
      protected JLabel solutionsLabel;

      protected int populationIndex;
      protected JButton viewSelected;
      protected JButton viewDecisionVars;
      protected JButton viewGenes;

      RunView(int index) {
        populationIndex = index;
        setLayout(new BorderLayout());
        om = new FitnessPlotMatrix();

        JPanel pnl = new JPanel(new GridBagLayout());
        Constrain.setConstraints(pnl, (runLabel = new JLabel(" ")),
                                 0, 0, 1, 1,
                                 GridBagConstraints.HORIZONTAL,
                                 GridBagConstraints.WEST,
                                 0, 0);
        Constrain.setConstraints(pnl, (sizeLabel = new JLabel(" ")),
                                 0, 1, 1, 1,
                                 GridBagConstraints.HORIZONTAL,
                                 GridBagConstraints.WEST,
                                 0, 0);
        Constrain.setConstraints(pnl, (solutionsLabel = new JLabel(" ")),
                                 0, 2, 1, 1,
                                 GridBagConstraints.HORIZONTAL,
                                 GridBagConstraints.WEST,
                                 0, 0);
        Constrain.setConstraints(pnl, om,
                                 0, 3, 1, 1,
                                 GridBagConstraints.HORIZONTAL,
                                 GridBagConstraints.WEST,
                                 0, 0);
        add(pnl, BorderLayout.CENTER);

        /*        add(om, BorderLayout.CENTER);
                JPanel labelPanel = new JPanel(new GridLayout(3, 1));
                labelPanel.add( (runLabel = new JLabel(" ")));
                labelPanel.add( (sizeLabel = new JLabel(" ")));
                labelPanel.add( (solutionsLabel = new JLabel(" ")));
                add(labelPanel, BorderLayout.NORTH);
         */

        viewSelected = new JButton("Selected");
        JPanel bPanel = new JPanel();
        bPanel.add(viewSelected);
        add(bPanel, BorderLayout.SOUTH);
        viewSelected.setEnabled(false);
        viewSelected.addActionListener(new AbstractAction() {
          public void actionPerformed(ActionEvent e) {
            TableModel tModel = om.tblModel.getSelected();
            // now we want to make a Table and use a TableFrame
            int cols = tModel.getColumnCount();
            int rows = tModel.getRowCount();
            
            MutableTableImpl mt = new MutableTableImpl(cols);
            for(int i = 0; i < cols; i++) {
              StringColumn sc = new StringColumn(rows);
              sc.setLabel(tModel.getColumnName(i)); 
              mt.setColumn(sc, i);
            }
            
            // now copy the data into the table
            for(int i = 0; i < rows; i++) {
              for(int j = 0; j < cols; j++) {
                String val = (String)tModel.getValueAt(i, j);  
                mt.setString(val, i, j); 
              } 
            }
            
            TableFrame tf = new TableFrame("Selected Individuals", mt);
            tf.pack();
            tf.show();
          }
        });
        viewDecisionVars = new JButton("Decision Variables");
        viewDecisionVars.setEnabled(false);
        viewDecisionVars.addActionListener(new RunnableAction() {
          public void run() {
            Table t = om.tblModel.getDecisionVariables();
            TableFrame tf = new TableFrame("Decision Variables", t); 
            tf.pack();
            tf.show();
          }
        });
        bPanel.add(viewDecisionVars);
        
        viewGenes = new JButton("Genes");
        viewGenes.setEnabled(false);
        viewGenes.addActionListener(new RunnableAction() {
          public void run() {
            Table t = om.tblModel.getGenes();
            TableFrame tf = new TableFrame("Genes", t);
            tf.pack();
            tf.show();
          }
        });
        bPanel.add(viewGenes); 
      }

      /**
         Calculate the size of the scrollable area.
         @return the size of the scrollable area
       */
      protected Dimension calcPreferredScrollableViewportSize(JTable jTable,
          TableModel tm) {
        // make the preferred width of the table inside this scroller
        // to be either the width of all the columns, or MAX_WIDTH,
        // whichever is smaller
        int rowWidth = jTable.getColumnModel().getColumn(0).getPreferredWidth();
        int wid = 0;
        for (int i = 0; i < tm.getColumnCount() - 1; i++) {
          if ( (wid + rowWidth) > 400) {
            break;
          }
          wid += rowWidth;
        }
        // make the preferred height of the table inside this scroller
        // to be either the height of all the rows, or MAX_HEIGHT,
        // whichever is smaller
        int hei = 0;
        int rowHeight = jTable.getRowHeight();
        for (int i = 0; i < tm.getRowCount(); i++) {
          if ( (hei + rowHeight) > 400) {
            break;
          }
          hei += rowHeight;
        }
        return new Dimension(wid, hei);
      }

      void redraw() {
        om.redraw();
      }

      void enableSelection() {
        om.enableSelection();
        viewSelected.setEnabled(true);
        viewDecisionVars.setEnabled(true);
        if(binaryIndividuals)
          viewGenes.setEnabled(true);
      }

      void disableSelection() {
        om.disableSelection();
        viewSelected.setEnabled(false);
        viewDecisionVars.setEnabled(false);
        viewGenes.setEnabled(false);
      }

      void removeSelection() {
        om.removeSelection();
      }

      void setPopulationTable(FitnessTable ft) {
        om.setPopulationTable(ft);
      }

      /**
         Shows all the Graphs in a JTable
       */
      protected class FitnessPlotMatrix
          extends JPanel
          implements java.io.Serializable {

        ObjectiveModel tblModel;
        JTable headerColumn;

        FitnessPlotMatrix() {
          setup();
        }

        void enableSelection() {
          for (int i = 0; i < tblModel.plots.length; i++) {
            for (int j = 0; j < tblModel.plots[i].length; j++) {
              tblModel.plots[i][j].enableSelection();
            }
          }
        }

        void disableSelection() {
          for (int i = 0; i < tblModel.plots.length; i++) {
            for (int j = 0; j < tblModel.plots[i].length; j++) {
              tblModel.plots[i][j].disableSelection();
            }
          }
        }

        void removeSelection() {
          tblModel.plots[0][0].removeAllSelections();
        }

        void redraw() {
          for (int i = 0; i < tblModel.plots.length; i++) {
            for (int j = 0; j < tblModel.plots[i].length; j++) {
              tblModel.plots[i][j].redraw();
            }
          }
          tblModel.fireTableDataChanged();
        }

        void setPopulationTable(FitnessTable ft) {
          tblModel.setPopulationTable(ft);
          tblModel.reinit();
        }

        void setObjectiveNames(String[] names) {
          tblModel.setObjectiveNames(names);

          JComboBox combo = new JComboBox();
          for (int i = 0; i < names.length; i++) {
            combo.addItem(names[i]);
          }
          //ComboRenderer renderer = new ComboRenderer(names);
          headerColumn.setDefaultEditor(String.class,
                                        new DefaultCellEditor(combo));

          EditableHeaderTableColumn col;
          // column 0
//        col = (EditableHeaderTableColumn) jTable.getColumnModel().getColumn(0);
//        col.setHeaderValue(combo.getItemAt(0));
          //col.setHeaderRenderer(renderer);

          // column 1
//        col = (EditableHeaderTableColumn) jTable.getColumnModel().getColumn(1);
//        col.setHeaderValue(combo.getItemAt(0));
          //col.setHeaderRenderer(renderer);
//        col.setHeaderEditor(new DefaultCellEditor(combo));

          for (int i = 0; i < 2; i++) {
            tblModel.setColumnSelection(i, names[i]);
            col = (EditableHeaderTableColumn) jTable.getColumnModel().getColumn(
                i);
            col.setHeaderEditor(new DefaultCellEditor(combo));
            col.setHeaderValue(combo.getItemAt(i));
          }
        }

        void setup() {
          // setup the JTable

          // setup the columns for the matrix
          TableColumnModel cm = new DefaultTableColumnModel() {
            boolean first = true;
            public void addColumn(TableColumn tc) {
              if (first) {
                first = false;
                return;
              }
              tc.setMinWidth(ROW_WIDTH);
              super.addColumn(tc);
            }
          };

          // setup the columns for the row header table
          TableColumnModel rowHeaderModel = new DefaultTableColumnModel() {
            boolean first = true;
            public void addColumn(TableColumn tc) {
              if (first) {
                super.addColumn(tc);
                first = false;
              }
            }
          };

          tblModel = new ObjectiveModel();

          JComboBox combobox = new JComboBox();

          // setup the row header table
          headerColumn = new JTable(tblModel, rowHeaderModel);
          headerColumn.setRowHeight(ROW_HEIGHT);
          headerColumn.setRowSelectionAllowed(false);
          headerColumn.setColumnSelectionAllowed(false);
          headerColumn.setCellSelectionEnabled(false);
          headerColumn.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
          headerColumn.getTableHeader().setReorderingAllowed(false);
          headerColumn.createDefaultColumnsFromModel();
          headerColumn.setDefaultRenderer(String.class,
                                          new RotatedLabelRenderer());
          headerColumn.setDefaultEditor(String.class,
                                        new DefaultCellEditor(combobox));

          // setup the graph matrix
          jTable = new JTable(tblModel, cm);
          jTable.createDefaultColumnsFromModel();
          //jTable.setDefaultRenderer(ImageIcon.class, new GraphRenderer());
          jTable.setDefaultRenderer(JPanel.class, new ComponentRenderer());
          jTable.setDefaultEditor(JPanel.class, new ComponentEditor());

          jTable.setRowHeight(ROW_HEIGHT);
          jTable.setRowSelectionAllowed(false);
          jTable.setColumnSelectionAllowed(false);
          jTable.setCellSelectionEnabled(false);

          //jTable.addMouseListener(this);
          jTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

          TableColumnModel columnModel = jTable.getColumnModel();
          jTable.setTableHeader(new EditableHeader(columnModel));

          String[] items = {
              "ff", "fg"};
          JComboBox combo = new JComboBox(items);

          EditableHeaderTableColumn col;
          // column 0
          col = (EditableHeaderTableColumn) jTable.getColumnModel().getColumn(0);
          col.setHeaderValue(items[0]);
          col.setHeaderEditor(new DefaultCellEditor(combo));

          // column 1
          col = (EditableHeaderTableColumn) jTable.getColumnModel().getColumn(1);
          col.setHeaderValue(items[0]);
          col.setHeaderEditor(new DefaultCellEditor(combo));

          int numRows = jTable.getModel().getRowCount();
          int numColumns = jTable.getModel().getColumnCount();

          int longest = 0;
          // we know that the first column will only contain
          // JLabels...so create them and find the longest
          // preferred width

          TableColumn column;
          // set the default column widths
          for (int i = 0; i < numColumns; i++) {
            if (i == 0) {
              column = headerColumn.getColumnModel().getColumn(i);
              column.setPreferredWidth(20);
            }
            else {
              column = jTable.getColumnModel().getColumn(i - 1);
              column.setPreferredWidth(ROW_WIDTH);
            }
          }

          jTable.setPreferredScrollableViewportSize(new Dimension(
              ROW_WIDTH * 2, ROW_HEIGHT * 2));

          // put the row headers in the viewport
          JViewport jv = new JViewport();
          jv.setView(headerColumn);
          jv.setPreferredSize(headerColumn.getPreferredSize());

          // setup the scroll pane
          JScrollPane sp = new JScrollPane(jTable);
          sp.setRowHeader(jv);
          sp.setHorizontalScrollBarPolicy(
              JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
          sp.setVerticalScrollBarPolicy(
              JScrollPane.VERTICAL_SCROLLBAR_NEVER);

          this.add(sp, BorderLayout.CENTER);
        }

        protected Table table;
        protected JTable jTable = null;

        /**
         * Renderer for a JComboBox
         */
        class ComboRenderer
            extends JComboBox
            implements TableCellRenderer {

          ComboRenderer(String[] items) {
            for (int i = 0; i < items.length; i++) {
              addItem(items[i]);
            }
          }

          public Component getTableCellRendererComponent(
              JTable table, Object value,
              boolean isSelected, boolean hasFocus,
              int row, int column) {
            setSelectedItem(value);
            return this;
          }
        }

        /**
         * A TableColumn with an editable header.
         */
        public class EditableHeaderTableColumn
            extends TableColumn {

          protected TableCellEditor headerEditor;
          protected boolean isHeaderEditable;

          public EditableHeaderTableColumn() {
            setHeaderEditor(createDefaultHeaderEditor());
            isHeaderEditable = true;
          }

          public void setHeaderEditor(TableCellEditor headerEditor) {
            this.headerEditor = headerEditor;
          }

          public TableCellEditor getHeaderEditor() {
            return headerEditor;
          }

          public void setHeaderEditable(boolean isEditable) {
            isHeaderEditable = isEditable;
          }

          public boolean isHeaderEditable() {
            return isHeaderEditable;
          }

          public void setHeaderValue(Object value) {
            super.setHeaderValue(value);
            FitnessPlotMatrix.this.tblModel.setColumnSelection(this.
                getModelIndex() -
                1, (String) value);
          }

          public void copyValues(TableColumn base) {
            modelIndex = base.getModelIndex();
            identifier = base.getIdentifier();
            width = base.getWidth();
            minWidth = base.getMinWidth();
            setPreferredWidth(base.getPreferredWidth());
            maxWidth = base.getMaxWidth();
            headerRenderer = base.getHeaderRenderer();
            headerValue = base.getHeaderValue();
            cellRenderer = base.getCellRenderer();
            cellEditor = base.getCellEditor();
            isResizable = base.getResizable();
          }

          protected TableCellEditor createDefaultHeaderEditor() {
            return new DefaultCellEditor(new JTextField());
          }
        }

        public class EditableHeaderUI
            extends BasicTableHeaderUI {

          protected MouseInputListener createMouseInputListener() {
            return new MouseInputHandler( (EditableHeader) header);
          }

          public class MouseInputHandler
              extends BasicTableHeaderUI.MouseInputHandler {
            private Component dispatchComponent;
            protected EditableHeader header;

            public MouseInputHandler(EditableHeader header) {
              this.header = header;
            }

            private void setDispatchComponent(MouseEvent e) {
              Component editorComponent = header.getEditorComponent();
              Point p = e.getPoint();
              Point p2 = SwingUtilities.convertPoint(header, p, editorComponent);
              dispatchComponent = SwingUtilities.getDeepestComponentAt(
                  editorComponent,
                  p2.x, p2.y);
            }

            private boolean repostEvent(MouseEvent e) {
              if (dispatchComponent == null) {
                return false;
              }
              MouseEvent e2 = SwingUtilities.convertMouseEvent(header, e,
                  dispatchComponent);
              dispatchComponent.dispatchEvent(e2);
              return true;
            }

            public void mousePressed(MouseEvent e) {
              if (!SwingUtilities.isLeftMouseButton(e)) {
                return;
              }
              super.mousePressed(e);

              if (header.getResizingColumn() == null) {
                Point p = e.getPoint();
                TableColumnModel columnModel = header.getColumnModel();
                int index = columnModel.getColumnIndexAtX(p.x);
                if (index != -1) {
                  if (header.editCellAt(index, e)) {
                    setDispatchComponent(e);
                    repostEvent(e);
                  }
                }
              }
            }

            public void mouseReleased(MouseEvent e) {
              super.mouseReleased(e);
              if (!SwingUtilities.isLeftMouseButton(e)) {
                return;
              }
              repostEvent(e);
              dispatchComponent = null;
            }
          }
        }

        public class EditableHeader
            extends JTableHeader
            implements CellEditorListener {
          public final int HEADER_ROW = -10;
          transient protected int editingColumn;
          transient protected TableCellEditor cellEditor;
          transient protected Component editorComp;

          public EditableHeader(TableColumnModel columnModel) {
            super(columnModel);
            setReorderingAllowed(false);
            cellEditor = null;
            recreateTableColumn(columnModel);
          }

          public void updateUI() {
            setUI(new EditableHeaderUI());
            resizeAndRepaint();
            invalidate();
          }

          protected void recreateTableColumn(TableColumnModel columnModel) {
            int n = columnModel.getColumnCount();
            EditableHeaderTableColumn[] newCols = new EditableHeaderTableColumn[
                n];
            TableColumn[] oldCols = new TableColumn[n];
            for (int i = 0; i < n; i++) {
              oldCols[i] = columnModel.getColumn(i);
              newCols[i] = new EditableHeaderTableColumn();
              newCols[i].copyValues(oldCols[i]);
            }
            for (int i = 0; i < n; i++) {
              columnModel.removeColumn(oldCols[i]);
            }
            for (int i = 0; i < n; i++) {
              columnModel.addColumn(newCols[i]);
            }
          }

          /*        public boolean editCellAt(int index) {
                    return editCellAt(index);
                  }*/

          public boolean editCellAt(int index, EventObject e) {
            if (cellEditor != null && !cellEditor.stopCellEditing()) {
              return false;
            }
            if (!isCellEditable(index)) {
              return false;
            }
            TableCellEditor editor = getCellEditor(index);

            if (editor != null && editor.isCellEditable(e)) {
              editorComp = prepareEditor(editor, index);
              editorComp.setBounds(getHeaderRect(index));
              add(editorComp);
              editorComp.validate();
              setCellEditor(editor);
              setEditingColumn(index);
              editor.addCellEditorListener(this);

              return true;
            }
            return false;
          }

          public boolean isCellEditable(int index) {
            if (getReorderingAllowed()) {
              return false;
            }
            int columnIndex = columnModel.getColumn(index).getModelIndex();
            EditableHeaderTableColumn col = (EditableHeaderTableColumn)
                columnModel.getColumn(columnIndex - 1);
            return col.isHeaderEditable();
          }

          public TableCellEditor getCellEditor(int index) {
            int columnIndex = columnModel.getColumn(index).getModelIndex();
            EditableHeaderTableColumn col = (EditableHeaderTableColumn)
                columnModel.getColumn(columnIndex - 1);
            return col.getHeaderEditor();
          }

          public void setCellEditor(TableCellEditor newEditor) {
            TableCellEditor oldEditor = cellEditor;
            cellEditor = newEditor;

            // firePropertyChange
            if (oldEditor != null && oldEditor instanceof TableCellEditor) {
              ( (TableCellEditor) oldEditor).removeCellEditorListener( (
                  CellEditorListener)this);
            }
            if (newEditor != null && newEditor instanceof TableCellEditor) {
              ( (TableCellEditor) newEditor).addCellEditorListener( (
                  CellEditorListener)this);
            }
          }

          public Component prepareEditor(TableCellEditor editor, int index) {
            Object value = columnModel.getColumn(index).getHeaderValue();
            boolean isSelected = true;
            int row = HEADER_ROW;
            JTable table = getTable();
            Component comp = editor.getTableCellEditorComponent(table,
                value, isSelected, row, index);
            if (comp instanceof JComponent) {
              ( (JComponent) comp).setNextFocusableComponent(this);
            }
            return comp;
          }

          public TableCellEditor getCellEditor() {
            return cellEditor;
          }

          public Component getEditorComponent() {
            return editorComp;
          }

          public void setEditingColumn(int aColumn) {
            editingColumn = aColumn;
          }

          public int getEditingColumn() {
            return editingColumn;
          }

          public void removeEditor() {
            TableCellEditor editor = getCellEditor();
            if (editor != null) {
              editor.removeCellEditorListener(this);

              requestFocus();
              remove(editorComp);

              int index = getEditingColumn();
              Rectangle cellRect = getHeaderRect(index);

              setCellEditor(null);
              setEditingColumn( -1);
              editorComp = null;

              repaint(cellRect);
            }
          }

          public boolean isEditing() {
            return (cellEditor == null) ? false : true;
          }

          //
          // CellEditorListener
          //
          public void editingStopped(ChangeEvent e) {
            TableCellEditor editor = getCellEditor();
            if (editor != null) {
              Object value = editor.getCellEditorValue();
              int index = getEditingColumn();
              columnModel.getColumn(index).setHeaderValue(value);
              removeEditor();
            }
          }

          public void editingCanceled(ChangeEvent e) {
            removeEditor();
          }
        }
      }

      /**
         A custom cell renderer that shows an ImageIcon.  A blue border is
         painted around the selected items.
       */
      protected class ComponentRenderer
          extends JPanel
          implements TableCellRenderer {

        ComponentRenderer() {
          setDoubleBuffered(false);
        }

        /**
           Set the icon and paint the border for this cell.
         */
        public Component getTableCellRendererComponent(JTable table,
            Object value, boolean isSelected, boolean hasFocus, int row,
            int column) {
          return (Component) value;
        }
      }

      protected class ComponentEditor
          extends AbstractCellEditor
          implements TableCellEditor {

        Object val;

        ComponentEditor() {
        }

        public Object getCellEditorValue() {
          return val;
        }

        /**
           Set the icon and paint the border for this cell.
         */
        public Component getTableCellEditorComponent(JTable table,
            Object value, boolean isSelected, int row,
            int column) {
          val = value;
          return (Component) val;
        }
      }

      protected class RotatedLabelRenderer
          extends JComponent
          implements TableCellRenderer {
        RotatedLabelRenderer() {
        }

        /**
           Set the icon and paint the border for this cell.
         */
        public Component getTableCellRendererComponent(JTable table,
            Object value, boolean isSelected, boolean hasFocus, int row,
            int column) {
//        JPanel pnl = new JPanel(new BorderLayout());
          Box box = new Box(BoxLayout.Y_AXIS);
          ncsa.gui.RotatedLabel rot = new ncsa.gui.RotatedLabel( (String) value);
          box.add(box.createVerticalGlue());
          box.add(rot);
          box.add(box.createVerticalGlue());
          rot.setBackground(Color.white);
//        pnl.add(rot, BorderLayout.CENTER);
//        return pnl;
          return box;
        }
      }

      /**
         The table's data model.  Keeps a matrix of images that are
         displayed in the table.  The images are created from the
         Graphs.
       */
      protected class ObjectiveModel
          extends AbstractTableModel
          implements SelectionChangedListener {

        TableFitnessPlot[][] plots;

        int[] ySelections;
        int[] xSelections;
        String[] objectiveNames;
        HashMap nameToIndexMap;

        ObjectiveModel() {
          plots = new TableFitnessPlot[2][2];
          for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
              plots[i][j] = new TableFitnessPlot();
              plots[i][j].addSelectionChangedListeners(this);
            }
          }

          ySelections = new int[2];
          xSelections = new int[2];
          for (int i = 0; i < 2; i++) {
            ySelections[i] = i;
            xSelections[i] = i;
          }
        }

        public void selectionChanged() {
          for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {

              plots[i][j].setChanged(true);
              plots[i][j].redraw();
            }
          }
          fireTableDataChanged();
        }
        
        Table getDecisionVariables() {
          NsgaPopulation p = MOView.this.populations[populationIndex];
          Table t = ((EMOPopulation)p).getDecisionVariablesTable();
          return t; 
        }
        
        Table getGenes() {
          NsgaPopulation p = MOView.this.populations[populationIndex];
          Table t = ((EMOPopulation)p).getGenesTable();
          return t; 
        }

        /**
         * Get a table model for the selected points.
         * @return
         */
        TableModel getSelected() {
          // get the list of selected index
          int[] selected = plots[0][0].fitnessTable.getSelectedRows();
          // get the population table
          NsgaPopulation p = MOView.this.populations[populationIndex];
          Table t = p.getTable();
          return new SelectedTableModel(t, selected);
        }

        protected class SelectedTableModel
            extends DefaultTableModel {
          Table tbl;
          int[] selectedRows;

          SelectedTableModel(Table t, int[] rows) {
            tbl = t;
            selectedRows = rows;
          }

          public int getRowCount() {
            if (selectedRows != null) {
              return selectedRows.length;
            }
            else {
              return 0;
            }
          }

          public int getColumnCount() {
            return tbl.getNumColumns();
          }

          public String getColumnName(int i) {
            return tbl.getColumnLabel(i);
          }

          public Object getValueAt(int r, int c) {
            return tbl.getString(selectedRows[r], c);
          }
        }

        void reinit() {
          // now, for all the plots in this row, update the objectives
          for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
              plots[i][j].setObjectives(i, j);
            }
          }
          fireTableDataChanged();
        }

        /**
         * This will change the y objective for all plots in this row.
         * @param index
         * @param value
         */
        void setRowSelection(int index, String value) {
          int val;
          try {
            val = ( (Integer) nameToIndexMap.get(value)).intValue();
          }
          catch (Exception e) {
            return;
          }
          ySelections[index] = val;

          // now, for all the plots in this row, update the objectives
          for (int i = 0; i < 2; i++) {
            plots[index][i].setObjectives(xSelections[i], val);
            plots[index][i].redraw();
          }
          fireTableDataChanged();
        }

        void setColumnSelection(int index, String value) {
          int val;
          try {
            val = ( (Integer) nameToIndexMap.get(value)).intValue();
          }
          catch (Exception e) {
            return;
          }
          xSelections[index] = val;

          // now, for all the plots in this column, update the objectives
          for (int i = 0; i < 2; i++) {
            plots[i][index].setObjectives(val, ySelections[i]);
            plots[i][index].redraw();
          }
          fireTableDataChanged();
        }

        void setObjectiveNames(String[] names) {
          objectiveNames = names;
          nameToIndexMap = new HashMap(names.length);
          for (int i = 0; i < names.length; i++) {
            nameToIndexMap.put(names[i], new Integer(i));
          }
        }

        void setPopulationTable(FitnessTable popTable) {
          for (int i = 0; i < plots.length; i++) {
            for (int j = 0; j < plots[i].length; j++) {
              plots[i][j].setTable(popTable);
              plots[i][j].redraw();
            }
          }
          fireTableDataChanged();
        }

        /**
           There is one more column than there are input features.
           The first column shows the output variables.
         */
        public int getColumnCount() {
          return NUM_COL + 1;
        }

        /**
           There are the same number of rows as output features.
         */
        public int getRowCount() {
          //return outputs.length;
          return NUM_ROW;
        }

        public String getColumnName(int col) {
          if (col == 0) {
            return "";
          }
          else {
            //return et.getColumnLabel(inputs[col-1]);
            return "col";
          }
        }

        public Object getValueAt(int row, int col) {
          if (col == 0) {
            if (objectiveNames == null) {
              return "";
            }
            else {
              return objectiveNames[ySelections[row]];
            }
          }
          else {
            return plots[row][col - 1];
          }
        }

        public void setValueAt(Object value, int row, int col) {
          if (col == 0) {
            setRowSelection(row, (String) value);
          }
        }

        /**
           This must be overridden so that our custom cell renderer is
           used.
         */
        public Class getColumnClass(int c) {
          return getValueAt(0, c).getClass();
        }

        public boolean isCellEditable(int row, int col) {
          if (col == 0) {
            return true;
          }
          return true;
        }
      } // Objective Model

    } // Run View
  } // MO View
  
  class EMONsgaPopulation extends NewNsgaPopulation implements EMOPopulation {
    EMONsgaPopulation(NsgaPopulation p1, NsgaPopulation p2){
      super(p1, p2);
    }
    
    private Parameters parameters; 
    
    /**
     * Get the parameters.
     * @return the parameters
     */
    public Parameters getParameters() {
      return parameters;
    }
  
    /**
     * Set the parameters
     * @param params the new parameters
     */
    public void setParameters(Parameters params) {
      parameters = params;
    }
  
    public Table getDecisionVariablesTable() {
      java.util.List functions = parameters.getFunctions();
  
      int numFunctions = functions.size();
      java.util.List constraints = new LinkedList();
  
      for (int i = 0; i < numFunctions; i++) {
        Function func = (Function) functions.get(i);
        if (func instanceof Constraint) {
          constraints.add(func);
        }
      }
  
      int numGenes = 0;
      int numTraits = this.traits.length;
      Solution nis = (Solution) members[0];
      int numConstraints = nis.getNumConstraints();    
  
      int popSize = this.size();
      double[][] dc = new double[numTraits + numObjectives +2+numConstraints][popSize];
  
      for (int i = 0; i < popSize; i++) {
        Solution ni = (Solution) members[i];
        double[] genes = ni.toDoubleValues();
        int j = 0;
  
        // first do the genes.
        for (; j < numTraits; j++) {
          dc[j][i] = genes[j];
  
          // Now the objectives.
        }
        for (int k = 0; k < numObjectives; k++, j++) {
          dc[j][i] = ((NsgaSolution)ni).getObjective(k);
        }
        for(int k = 0; k < numConstraints; k++, j++) {
          dc[j][i] = ni.getConstraint(k);        
        }
  
        dc[j++][i] = ((NsgaSolution)ni).getRank();
        dc[j++][i] = ((NsgaSolution)ni).getCrowdingDistance();
  
      }
      // Now make the table
      //BASIC3 TableImpl vt = (TableImpl) DefaultTableFactory.getInstance().createTable(0);
      MutableTableImpl vt =  new MutableTableImpl(0);
      int i = 0;
  
      for (; i < numTraits; i++) {
        DoubleColumn col = new DoubleColumn(dc[i]);
        // NsgaSolution nis0 = (NsgaSolution) members[0];
        //if (nis instanceof MONumericIndividual) {
          col.setLabel(this.traits[i].getName());
        /*}
        else {
          col.setLabel("Variable " + i);
        }*/
        vt.addColumn(col);
      }
  
      for (int k = 0; k < numObjectives; k++, i++) {
        DoubleColumn col = new DoubleColumn(dc[i]);
        col.setLabel(this.objectiveConstraints[k].getName());
        vt.addColumn(col);
      }
      for(int k = 0; k < numConstraints; k++, i++) {
        DoubleColumn col = new DoubleColumn(dc[i]);
        col.setLabel(((Function)constraints.get(k)).getName());
        vt.addColumn(col);
      }
  
      DoubleColumn col = new DoubleColumn(dc[i++]);
      col.setLabel("Rank");
      vt.addColumn(col);
      col = new DoubleColumn(dc[i++]);
      col.setLabel("Crowding");
      vt.addColumn(col);
      return vt;
    }
  
    public Table getGenesTable() {
      java.util.List functions = parameters.getFunctions();
  
      int numFunctions = functions.size();
      java.util.List constraints = new LinkedList();
  
      for (int i = 0; i < numFunctions; i++) {
        Function func = (Function) functions.get(i);
        if (func instanceof Constraint) {
          constraints.add(func);
        }
      }
  
      int numGenes = 0;
      int numTraits;
      BinarySolution nis = (BinarySolution) members[0];
      int numConstraints = nis.getNumConstraints();    
      numTraits = this.traits.length;
  
      int popSize = this.size();
      double[][] dc = new double[numTraits + numObjectives + 2+numConstraints][popSize];
  
      for (int i = 0; i < popSize; i++) {
        BinarySolution ni = (BinarySolution) members[i];
        double[] genes = ni.toDouble();
        int j = 0;
  
        // first do the genes.
        for (; j < numTraits; j++) {
          dc[j][i] = genes[j];
  
          // Now the objectives.
        }
        for (int k = 0; k < numObjectives; k++, j++) {
          dc[j][i] = ((NsgaSolution)ni).getObjective(k);
        }
        for(int k = 0; k < numConstraints; k++, j++) {
          dc[j][i] = ni.getConstraint(k);        
        }
  
        dc[j++][i] = ((NsgaSolution)ni).getRank();
        dc[j++][i] = ((NsgaSolution)ni).getCrowdingDistance();
  
      }
      // Now make the table
      //BASIC3 TableImpl vt = (TableImpl) DefaultTableFactory.getInstance().createTable(0);
      MutableTableImpl vt =  new MutableTableImpl(0);
      int i = 0;
  
      for (; i < numTraits; i++) {
        DoubleColumn col = new DoubleColumn(dc[i]);
        // NsgaSolution nis0 = (NsgaSolution) members[0];
        //if (nis instanceof MONumericIndividual) {
          col.setLabel(this.traits[i].getName());
        /*}
        else {
          col.setLabel("Variable " + i);
        }*/
        vt.addColumn(col);
      }
  
      for (int k = 0; k < numObjectives; k++, i++) {
        DoubleColumn col = new DoubleColumn(dc[i]);
        col.setLabel(this.objectiveConstraints[k].getName());
        vt.addColumn(col);
      }
      for(int k = 0; k < numConstraints; k++, i++) {
        DoubleColumn col = new DoubleColumn(dc[i]);
        col.setLabel(((Function)constraints.get(k)).getName());
        vt.addColumn(col);
      }
  
      DoubleColumn col = new DoubleColumn(dc[i++]);
      col.setLabel("Rank");
      vt.addColumn(col);
      col = new DoubleColumn(dc[i++]);
      col.setLabel("Crowding");
      vt.addColumn(col);
      return vt;
    }
    
  }
} // MO Vis