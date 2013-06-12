package ncsa.d2k.modules.core.optimize.ga.emo.gui;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.userviews.swing.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import ncsa.d2k.modules.core.vis.widgets.*;
import ncsa.d2k.modules.core.optimize.ga.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

import ncsa.d2k.modules.core.optimize.ga.emo.*;
import ncsa.d2k.modules.core.vis.widgets.*;
import ncsa.d2k.gui.*;

/**
 * Plot the current generation vs. the average fitness.
 */
public class SOVis extends UIModule {
  public String[] getInputTypes() {
    return new String[] {
        "ncsa.d2k.modules.core.optimize.ga.Population"};
  }

  public String[] getOutputTypes() {
    return new String[] {
        "ncsa.d2k.modules.core.optimize.ga.Population"
    };
  }

  public String getInputInfo(int i) {
    return "The population to be plotted.";
  }

  public String getInputName(int i) {
    return "EMOPopulation";
  }

  public String getOutputInfo(int i) {
    switch(i) {
      case(0):
        return "The unchanged population.";
      default:
        return "";
    }
  }

  public String getOutputName(int i) {
      return "EMOPopulation";
  }

  public String getModuleInfo() {
    return "Plot the average fitness against the generation number.";
  }
  
  public String getModuleName() {
    return "Single-Objective Visualization";
  }

  protected UserView createUserView() {
    return new SOView();
  }

  public String[] getFieldNameMapping() {
    return null;
  }
  
  /*  Return an array with information on the properties the user may update.
   *  @return The PropertyDescriptions for properties the user may update.
   */
  public PropertyDescription[] getPropertiesDescriptions() {
    return new PropertyDescription[0];
  }

  protected class SOView extends JUserPane {
    JButton done;
    JButton stop;
    JButton viewDecisionVariables;
    JButton viewGenes;

    ScatterPlot scatterPlot;
    MutableTable mt;
    Population pop;
    DataSet[] dataSets;
    GraphSettings graphSettings;

    public void initView(ViewModule vm) {
      scatterPlot = new ScatterPlot();

      done = new JButton("Done");
      done.setEnabled(false);
      done.addActionListener(new AbstractAction() {
        public void actionPerformed(ActionEvent ae) {
          viewDone("done");
        }
      });
      stop = new JButton("Stop");
      stop.addActionListener(new AbstractAction() {
        public void actionPerformed(ActionEvent ae) {
          viewCancel();
        }
      });
      viewDecisionVariables = new JButton("View Decision Variables");
      viewDecisionVariables.addActionListener(new RunnableAction() {
        public void run() {
          Table t = ((EMOPopulation)pop).getDecisionVariablesTable();
          TableFrame frame = new TableFrame("Decision Variables", t);
          frame.pack();
          frame.show();
        }
      });
      viewGenes = new JButton("View Genes");
      viewGenes.addActionListener(new RunnableAction() {
        public void run() {
          Table t = ((EMOPopulation)pop).getGenesTable();
          TableFrame frame = new TableFrame("Genes", t);
          frame.pack();
          frame.show();
        }
      });

      JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
      buttonPanel.add(done);
      buttonPanel.add(stop);
      buttonPanel.add(viewDecisionVariables);
      buttonPanel.add(viewGenes);

      setLayout(new BorderLayout());
      add(scatterPlot, BorderLayout.CENTER);
      add(buttonPanel, BorderLayout.SOUTH);

      dataSets = new DataSet[1];
      dataSets[0] = new DataSet("Generation vs. Average Fitness", Color.black, 0, 1);
      graphSettings = new GraphSettings();
      graphSettings.displayscale = true;
      graphSettings.displaytitle = true;
      graphSettings.displayaxislabels = true;
      graphSettings.xaxis = "Generation";
      graphSettings.yaxis = "Average Fitness";
    }

    public Dimension getPreferredSize() {
      return new Dimension(500, 500);
    }

    public void setInput(Object o, int i) {
      Population p = (Population)o;
      if(p != pop) {
        pop = p;

        mt = new MutableTableImpl(2);
        //BASIC3 mt.setColumn(new int[0], 0);
        mt.setColumn(new IntColumn(0), 0);
        mt.setColumnLabel("Generation", 0);
        //BASIC3 mt.setColumn(new double[0], 1);
        mt.setColumn(new DoubleColumn(0), 1);
        mt.setColumnLabel("Average Fitness", 1);
        scatterPlot.init(mt, dataSets, graphSettings);
        
        done.setEnabled(false);
        viewDecisionVariables.setEnabled(false);
        viewGenes.setEnabled(false);
      }
      p.computeStatistics();

//BASIC3      Object[] row = new Object[2];
//      row[0] = new Integer(p.getCurrentGeneration());
//      row[1] = new Double(((SOPopulation)p).getAverageFitness());
//
//      mt.addRow(row);
      int numRows = mt.getNumRows();
      mt.addRows(1);
      mt.setInt(p.getCurrentGeneration(),numRows,0);
      mt.setDouble(((SOPopulation)p).getAverageFitness(),numRows,1);
      
//      System.out.println("******************");
//      ((MutableTableImpl)mt).print();

      scatterPlot.setTable(mt);
      if(p.getCurrentGeneration() == p.getMaxGenerations()-1) {
        done.setEnabled(true);
        viewDecisionVariables.setEnabled(true);
        Parameters params = ((EMOPopulation)pop).getParameters();
        if(params.createBinaryIndividuals) 
          viewGenes.setEnabled(true);
        else
          viewGenes.setEnabled(false);
      }
      pushOutput(o, 0);
    }
  }
}