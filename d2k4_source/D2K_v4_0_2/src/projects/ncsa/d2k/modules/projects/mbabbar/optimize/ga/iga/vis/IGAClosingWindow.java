package ncsa.d2k.modules.projects.mbabbar.optimize.ga.iga.vis;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.ImageIcon;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JToolBar;
import javax.swing.JButton;
import javax.swing.table.*;
import javax.swing.plaf.basic.*;
import javax.swing.border.*;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.userviews.swing.*;
import ncsa.d2k.modules.core.optimize.ga.emo.*;
import ncsa.d2k.modules.core.optimize.ga.nsga.*;
import ncsa.d2k.modules.projects.mbabbar.optimize.ga.iga.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.core.io.file.output.*;
import ncsa.d2k.modules.core.vis.widgets.*;



/**
 * This module displays the selected individuals for comparative rating.
 *
 * @author Meghna Babbar
 */
public class IGAClosingWindow extends UIModule {

////////////////////////////////////////////////////////////////////////////////
// Module methods                                                             //
////////////////////////////////////////////////////////////////////////////////

   public UserView createUserView() {
      return new IGAClosingView();
   }

   /**
    * Not used by this module.
    */
   public String[] getFieldNameMapping() {
      return null;
   }

   public String getInputInfo(int i) {
      if (i == 0)
         return "The <i>IGA NSGA Population</i>.";
      return "No such input";
   }

   public String getInputName(int i) {
      if (i == 0)
         return "Multiobjective IGA NSGA Population";
      return "No such input";
   }

   public String[] getInputTypes() {
      return new String[] {
         "ncsa.d2k.modules.projects.mbabbar.optimize.ga.iga.IGANsgaPopulation"
      };
   }

   public String getModuleInfo() {
      StringBuffer sb = new StringBuffer("<p>Overview: ");
      sb.append("This module is the closing window to the interactive session of the IGA");
      return sb.toString();
   }

   public String getModuleName() {
      return "IGA: Closing Window";
   }

   public String getOutputInfo(int i) {
      if (i == 0)
         return "The <i>IGA NSGA Population</i>.";
      return "No such output";
   }

   public String getOutputName(int i) {
      if (i == 0)
         return "Multiobjective IGA NSGA Population";
      return "No such output";
   }

   public String[] getOutputTypes() {
      return new String[] { "ncsa.d2k.modules.projects.mbabbar.optimize.ga.iga.IGANsgaPopulation"};
    // return null;
   }
////////////////////////////////////////////////////////////////////////////////
// user view                                                                  //
////////////////////////////////////////////////////////////////////////////////

   /**
    * This class uses a <code>NsgaPopulation</code> to display the
    * <code>Objective Tradeoff Scatter Plots</code>.
    */
   public class IGAClosingView extends JUserPane implements ActionListener {

      /** a reference to our parent module */
      protected IGAClosingWindow parent;
      /** Next button */
      protected JButton next;
      /** Quit button */
      protected JButton quit;
      /** JButton Panel for back, quit and next buttons */
      protected JPanel buttonPanel = new JPanel(); //(new GridLayout(2, 2));

      /** the current population */
      private IGANsgaPopulation currentPop;

      abstract class Runner
        implements Runnable {};
      /**
         Initialize the view.  Insert all components into the view.
         @param mod The IGAObjectiveScatterPlotWindow module that owns us
         */
      public void initView(ViewModule mod) {
         parent = (IGAClosingWindow)mod;

            // Main Button Panel
            float h,s,b;
            h = 0.219F;
            s = 0.141F;
            b = 0.823F;
            buttonPanel.setBackground(Color.getHSBColor(h,s,b));
            buttonPanel.setBorder(BorderFactory.createLineBorder(Color.white,2));
            buttonPanel.setPreferredSize(new Dimension(400, 100));


            quit = new JButton("End Session");
            quit.setEnabled(true);
            quit.addActionListener(new AbstractAction() {
              public void actionPerformed(ActionEvent e) {
                viewCancel();
              }
            });



            next = new JButton("Continue Genetic Algorithm");
            next.setEnabled(true);
            next.addActionListener(new AbstractAction() {
              public void actionPerformed(ActionEvent e) {

                currentPop.setMaxGenerations(currentPop.getCurrentGeneration()*2);
                // Testing/////////////////////////////
                for (int i = 0; i < currentPop.getTotalNumIndivsRankedInArchive(); i++){
                    IGANsgaSolution ni = (IGANsgaSolution) currentPop.getIndInHumanRankedPopulationArchive(i);
                    if( ni.getRankedIndivFlag() == true) {
                      System.out.println("Human " + i + " : " + ni.toString());
                    }
                }
                ///////////////////////////////////////
                pushOutput(currentPop, 0);
                viewDone("Done");
              }
            });

            buttonPanel.add(next);
            buttonPanel.add(quit);

     }

      // MenuBar
      public Object getMenu() {
         return null;
      }

      /**
         Called whenever inputs arrive to the module.
         @param input the Object that is the input
         @param idx the index of the input
         */
      public void setInput(Object input, int idx) {

            removeAll();
            currentPop = (IGANsgaPopulation) input;
            revalidate();

            float h,s,b;
            h = 0.168F;
            s = 0.208F;
            b = 0.979F;


            /** Label for welcome message */
            JLabel welcomelabel = new JLabel("End of Interactive Genetic Algorithm Session.");       // create a Label
            welcomelabel.setFont(new Font("Helvetica", Font.BOLD, 18));
            JPanel welcomepnl = new JPanel();
            welcomepnl.add(welcomelabel, BorderLayout.CENTER);

            JPanel pnl = new JPanel(new GridLayout(2, 0));
            welcomepnl.setBorder(BorderFactory.createLineBorder(Color.white,2));
            welcomepnl.setBackground(Color.getHSBColor(h,s,b));

            pnl.setPreferredSize(new Dimension(550, 300));
            pnl.add(welcomepnl);
            pnl.add(buttonPanel);

            this.add(pnl);
      }

      /**
         Perform any clean up to the table and call the finish() method
         on the VerticalTableViewer module.  Since all cells are
         uneditable in this implementation, we simply call the finish()
         method.  A subclass may want to juggle the contents of the table,
         however.
         */
      protected void finishUp() {
       //  pushOutput(table, 0);
         viewDone("Done");
      }

      /**
         This is the ActionListener for the ok and cancel buttons.  The
         finishUp() method is called if ok is pressed.  The viewCancel()
         method of the VerticalTableViewer module is called if cancel is
         pressed.
         @param e the ActionEvent
         */
      public void actionPerformed(ActionEvent e) {
         Object src = e.getSource();
         /*if(src == back)
            finishUp();
         else if(src == quit)
            parent.viewCancel();
         else if(src == next)
            finishUp();
            */
      }

  }//IGAWelcomeView
}//IGAWelcomeWindow
