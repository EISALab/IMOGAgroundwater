package ncsa.d2k.modules.core.optimize.ga.emo.gui;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.userviews.swing.*;
import ncsa.gui.Constrain;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import ncsa.d2k.modules.core.optimize.ga.emo.*;
import ncsa.d2k.modules.core.optimize.ga.emo.functions.*;

/**
 * An interface to input the fitness functions when the constraints are
 * calculated by a foreign executable
 * @author David Clutter
 * @version 1.0
 */
public class DefineExternalConstraints extends HeadlessUIModule {

  public String[] getInputTypes() {
    return new String[] {"ncsa.d2k.modules.core.optimize.ga.emo.Parameters"};
  }

  public String[] getOutputTypes() {
    return new String[] {"ncsa.d2k.modules.core.optimize.ga.emo.Parameters"};
  }

  public String getInputInfo(int i) {
    return "The parameters for the EMO problem.";
  }

  public String getOutputInfo(int i) {
    return "The parameters for the EMO problem.";
  }

  public String getInputName(int i) {
    return "EMOParameters";
  }

  public String getOutputName(int i) {
    return "EMOParameters";
  }
  
  public String getModuleName() {
    return "Define External Constraints";  
  }

  public String getModuleInfo() {
    String s = "<p>Overview: ";
    s += "Input constraint violation functions that are calculated by an executable.";
    s += "<p>Detailed Description: ";
    s += "Input all the properties needed to call an executable to evaluate ";
    s += "the constraint violation functions for a population.";
    s += "First, input the name of the constraint.  Next, input the path ";
    s += "to the executable.  Next, input the paths to the input and output files. ";
    s += "Finally, input a weight to assign to this constraint.";
    s += "<p>An executable must read the genes in from a file and write out ";
    s += "a file containing the values of the fitness functions.  The format ";
    s += "of the input file is fixed.  The first line contains the size of the ";
    s += "population.  The second line contains the number of traits.";
    s += "Every following line should contain the values for an individual, ";
    s += "space-delimited.";
    s += "The output file must also follow a specific format.  Each row of ";
    s += "the file must contain the value for this constraint violation function on the ";
    s += "associated individual in the genes file.  Order is preserved.";
    return s;
  }

  protected UserView createUserView() {
    return new ExternalView();
  }

  public String[] getFieldNameMapping() {
    return null;
  }

  private Object[] constraints;
  public void setConstraints(Object[] o) {
    constraints = o;
  }
  public Object[] getConstraints() {
    return constraints;
  }

  /*  Return an array with information on the properties the user may update.
   *  @return The PropertyDescriptions for properties the user may update.
   */
  public PropertyDescription[] getPropertiesDescriptions() {
    PropertyDescription[] props = super.getPropertiesDescriptions();
    return new PropertyDescription[] {props[0]};
  }
  
  public void doit() throws Exception {
    Parameters params = (Parameters)pullInput(0);

    Object[] vals = getConstraints();
    if(vals == null)
        throw new Exception (this.getAlias()+" has not been configured. Before running headless, run with the gui and configure the parameters.");

      // now create the table and add it to the pop info
      int numFunctions = vals.length;

      for(int i = 0; i < numFunctions; i++) {
        Constraint f = (Constraint) vals[i];

        //ff.addExternFitnessFunction(f.name, f.exec, f.input, f.output, b);
        ConstraintExecFunction fef = new ConstraintExecFunction(f.name, 
            System.getProperty("user.dir")+File.separator+f.exec,
            System.getProperty("user.dir")+File.separator+f.input, 
            System.getProperty("user.dir")+File.separator+f.output, 
            f.weight);
        params.addFunction(fef);
      }

      // push out the pop info
      pushOutput(params, 0);
  }  
  

  protected class ExternalView extends JUserPane {
    protected DefaultListModel definedFunctionsModel;
    protected JList definedFunctions;
    protected JButton removeFunction;

    protected JTextField functionName;
    protected JTextField execPath;
    protected JTextField inputFilePath;
    protected JTextField outputFilePath;
//    JComboBox min;
    protected JTextField weightField;

    protected Parameters parameters;
    
    private static final String HTML = "<html>";
    private static final String INPUT = "Input: ";
    private static final String BR = "<br>";
    private static final String OUTPUT = "Output: ";
    private static final String WEIGHT = "Weight: ";
    private static final String CLOSE = "</html>";
    
    private Constraint selectedItem;
    private int selectedIndex; 

/*    public Dimension getPreferredSize() {
      return new Dimension(600, 250);
    }*/

    public void paintComponent(Graphics g) {
      Graphics2D g2 = (Graphics2D) g;
      g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                          RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
      super.paintComponent(g2);
    }

    public void initView(ViewModule vm) {
      // the remove button
      JPanel removeButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
      removeFunction = new JButton("Remove");
      // when remove is pressed, remove all the selected elements in the jlist
      removeFunction.addActionListener(new RunnableAction() {
        public void run() {
          Object[] vals = definedFunctions.getSelectedValues();
          for (int i = 0; i < vals.length; i++) {
            definedFunctionsModel.removeElement(vals[i]);
          }
        }
      });
      removeButtonPanel.add(removeFunction);

      // the jlist
      Object[] ff = getConstraints();
      definedFunctionsModel = new DefaultListModel();
      // add the FFs from the last run ..
      if (ff != null) {
        for (int i = 0; i < ff.length; i++) {
          definedFunctionsModel.addElement(ff[i]);
        }
      }
      // the jlist component
      definedFunctions = new JList(definedFunctionsModel) {
        // set the tool tip text for each FF defined
        public String getToolTipText(MouseEvent me) {
          Point p = me.getPoint();
          int idx = locationToIndex(p);
          Constraint ff = (Constraint) definedFunctionsModel.
              elementAt(idx);

          StringBuffer tip = new StringBuffer();
          tip.append(HTML);
          tip.append(INPUT);
          tip.append(ff.input);
          tip.append(BR);
          tip.append(OUTPUT);
          tip.append(ff.output);
          tip.append(BR);
          tip.append(WEIGHT);
          tip.append(ff.weight);
          tip.append(CLOSE);
          return tip.toString();
        }
      };

      definedFunctions.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      final ListSelectionModel rowSel = definedFunctions.getSelectionModel();
      rowSel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      rowSel.addListSelectionListener(new ListSelectionListener() {
        public void valueChanged(ListSelectionEvent e) {
          if(e.getValueIsAdjusting())
            return;
          if(rowSel.isSelectionEmpty())  {
            selectedItem = null;
            functionName.setText("");
            execPath.setText("");
            inputFilePath.setText("");
            outputFilePath.setText("");
            weightField.setText("");

            return;
          }
          else {
            int selRow = rowSel.getMinSelectionIndex();

            selectedItem = (Constraint)definedFunctionsModel.elementAt(selRow);
            selectedIndex = selRow;

            // now fill in the text areas
            execPath.setText(selectedItem.exec);
            functionName.setText(selectedItem.name);
            inputFilePath.setText(selectedItem.input);
            outputFilePath.setText(selectedItem.output);
            //min.setSelectedItem(selectedItem.minmax);
            weightField.setText(Double.toString(selectedItem.weight));
          }
        }
      });

      JScrollPane scroll = new JScrollPane(definedFunctions);
      scroll.setColumnHeaderView(new JLabel("Constraints"));
      scroll.setPreferredSize(new Dimension(175, 150));
      JPanel listPanel = new JPanel(new BorderLayout());
      listPanel.add(Box.createHorizontalStrut(20), BorderLayout.WEST);
      listPanel.add(scroll, BorderLayout.CENTER);
      listPanel.add(removeButtonPanel, BorderLayout.SOUTH);

      JPanel p2 = new JPanel(new BorderLayout());
      p2.add(listPanel, BorderLayout.NORTH);
      p2.add(new JPanel(), BorderLayout.CENTER);

      JPanel mainPanel = new JPanel(new GridBagLayout());
      JButton selectExec = new JButton("...");
      selectExec.addActionListener(new RunnableAction() {
        public void run() {
          JFileChooser jfc = new JFileChooser();
          jfc.setCurrentDirectory(new File(System.getProperty("user.dir")));

          // set the title of the FileDialog
          StringBuffer sb = new StringBuffer("Select File");
          jfc.setDialogTitle(sb.toString());

          // get the file
          String fn;
          int retVal = jfc.showOpenDialog(null);
          if (retVal == JFileChooser.APPROVE_OPTION) {
            File file = jfc.getSelectedFile(); 
            File parent = file.getParentFile(); 
            if(!parent.equals(new File(System.getProperty("user.dir")))) {
              JOptionPane.showMessageDialog(null, "The executable must be located in the working directory, "+System.getProperty("user.dir"),
                                            "Error", JOptionPane.ERROR_MESSAGE);   
              return; 
            }

            fn = file.getName();
          }
          else {
            return;
          }
          execPath.setText(fn);
        }
      });
      JButton selectInput = new JButton("...");
      selectInput.addActionListener(new RunnableAction() {
        public void run() {
          JFileChooser jfc = new JFileChooser();
          jfc.setCurrentDirectory(new File(System.getProperty("user.dir")));

          // set the title of the FileDialog
          StringBuffer sb = new StringBuffer("Select File");
          jfc.setDialogTitle(sb.toString());

          // get the file
          String fn;
          int retVal = jfc.showOpenDialog(null);
          if (retVal == JFileChooser.APPROVE_OPTION) {
            File file = jfc.getSelectedFile(); 
            File parent = file.getParentFile(); 
            if(!parent.equals(new File(System.getProperty("user.dir")))) {
              JOptionPane.showMessageDialog(null, "The input file must be located in the working directory, "+System.getProperty("user.dir"),
                                            "Error", JOptionPane.ERROR_MESSAGE);   
              return; 
            }

            fn = file.getName();
          }
          else {
            return;
          }
          inputFilePath.setText(fn);
        }
      });
      JButton selectOutput = new JButton("...");
      selectOutput.addActionListener(new RunnableAction() {
        public void run() {
          JFileChooser jfc = new JFileChooser();
          jfc.setCurrentDirectory(new File(System.getProperty("user.dir")));

          // set the title of the FileDialog
          StringBuffer sb = new StringBuffer("Select File");
          jfc.setDialogTitle(sb.toString());

          // get the file
          String fn;
          int retVal = jfc.showOpenDialog(null);
          if (retVal == JFileChooser.APPROVE_OPTION) {
            File file = jfc.getSelectedFile(); 
            File parent = file.getParentFile(); 
            if(!parent.equals(new File(System.getProperty("user.dir")))) {
              JOptionPane.showMessageDialog(null, "The output file must be located in the working directory, "+System.getProperty("user.dir"),
                                            "Error", JOptionPane.ERROR_MESSAGE);   
              return; 
            }

            fn = file.getName();
          }
          else {
            return;
          }
          outputFilePath.setText(fn);
        }
      });

      Constrain.setConstraints(mainPanel, new JLabel("Function Name"), 
                               0, 0, 1, 1, 
                               GridBagConstraints.HORIZONTAL,
                               GridBagConstraints.NORTHWEST,
                               1, 1);
      functionName = new JTextField(10);
      Constrain.setConstraints(mainPanel, functionName, 1, 0, 1, 1,
                               GridBagConstraints.HORIZONTAL,
                               GridBagConstraints.NORTHWEST,
                               1, 1);

      Constrain.setConstraints(mainPanel, new JLabel("Path to Executable"), 
                               0, 1, 1, 1, GridBagConstraints.HORIZONTAL,
                               GridBagConstraints.NORTHWEST,
                               1, 1);
      execPath = new JTextField(10);
      Constrain.setConstraints(mainPanel, execPath, 1, 1, 1, 1,
                               GridBagConstraints.HORIZONTAL,
                               GridBagConstraints.NORTHWEST,
                               1, 1);
      Constrain.setConstraints(mainPanel, selectExec, 2, 1, 1, 1,
                               GridBagConstraints.NONE, 
                               GridBagConstraints.NORTHWEST,
                               0, 0);

      Constrain.setConstraints(mainPanel, new JLabel("Input File"), 0, 2, 1, 1,
                               GridBagConstraints.HORIZONTAL, 
                               GridBagConstraints.NORTHWEST,
                               0, 0);
      inputFilePath = new JTextField(10);
      Constrain.setConstraints(mainPanel, inputFilePath, 1, 2, 1, 1,
                               GridBagConstraints.HORIZONTAL,
                               GridBagConstraints.NORTHWEST,
                               1, 1);
      Constrain.setConstraints(mainPanel, selectInput, 2, 2, 1, 1,
                               GridBagConstraints.NONE, 
                               GridBagConstraints.NORTHWEST,
                               0, 0);

      Constrain.setConstraints(mainPanel, new JLabel("Output File"), 0, 3, 1, 1,
                               GridBagConstraints.HORIZONTAL, 
                               GridBagConstraints.NORTHWEST,
                               1, 1);
      outputFilePath = new JTextField(10);
      Constrain.setConstraints(mainPanel, outputFilePath, 1, 3, 1, 1,
                               GridBagConstraints.HORIZONTAL,
                               GridBagConstraints.NORTHWEST,
                               1, 1);
      Constrain.setConstraints(mainPanel, selectOutput, 2, 3, 1, 1,
                               GridBagConstraints.NONE, 
                               GridBagConstraints.NORTHWEST,
                               0, 0);

      Constrain.setConstraints(mainPanel, new JLabel("Weight"), 
                               0, 4, 1, 1,
                               GridBagConstraints.HORIZONTAL, 
                               GridBagConstraints.NORTHWEST,
                               1, 1);
      weightField = new JTextField(10);
      Constrain.setConstraints(mainPanel, weightField, 
                               1, 4, 1, 1,
                               GridBagConstraints.HORIZONTAL, 
                               GridBagConstraints.NORTHWEST,
                               1, 1);
      
      //Object[] items = {MINIMIZE, MAXIMIZE};
      //min = new JComboBox(items);
      //Constrain.setConstraints(mainPanel, min, 1, 4, 1, 1,
      //                         GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,
      //                         0, 0);

      JButton add = new JButton("Add Constraint");
      add.addActionListener(new RunnableAction() {
        public void run() {

          if(selectedItem != null) {
            String nme = functionName.getText();
              if (nme == null || nme.trim().length() == 0) {
                JOptionPane.showMessageDialog(null, "Constraint name not specified",
                                              "Error", JOptionPane.ERROR_MESSAGE);
                return;
              }
              String exec = execPath.getText();
              if (exec == null || exec.trim().length() == 0) {
                JOptionPane.showMessageDialog(null,
                                              "Executable path name not specified",
                                              "Error", JOptionPane.ERROR_MESSAGE);
                return;
              }
              String input = inputFilePath.getText();
              if (input == null || input.trim().length() == 0) {
                JOptionPane.showMessageDialog(null,
                                              "Input file path name not specified",
                                              "Error", JOptionPane.ERROR_MESSAGE);
                return;
              }
              String output = outputFilePath.getText();
              if (output == null || output.trim().length() == 0) {
                JOptionPane.showMessageDialog(null,
                                              "Output file path name not specified",
                                              "Error", JOptionPane.ERROR_MESSAGE);
                return;
              }
              //String minmax = (String) min.getSelectedItem();
              double weight;
              try {
                weight = Double.parseDouble(weightField.getText());
              }
              catch(Exception ex) {
                JOptionPane.showMessageDialog(null,
                                              "Weight not specified",
                                              "Error", JOptionPane.ERROR_MESSAGE);
                return;
              }

              selectedItem.name = nme;
              selectedItem.exec = exec;
              selectedItem.input = input;
              selectedItem.output = output;
              //selectedItem.minmax = minmax;
              selectedItem.weight = weight;

              definedFunctionsModel.set(selectedIndex, selectedItem);
              definedFunctions.getSelectionModel().clearSelection();             
              return;
          }

          String nme = functionName.getText();
          if (nme == null || nme.trim().length() == 0) {
            JOptionPane.showMessageDialog(null, "Function name not specified",
                                          "Error", JOptionPane.ERROR_MESSAGE);
            return;
          }
          String exec = execPath.getText();
          if (exec == null || exec.trim().length() == 0) {
            JOptionPane.showMessageDialog(null,
                                          "Executable path name not specified",
                                          "Error", JOptionPane.ERROR_MESSAGE);
            return;
          }
          String input = inputFilePath.getText();
          if (input == null || input.trim().length() == 0) {
            JOptionPane.showMessageDialog(null,
                                          "Input file path name not specified",
                                          "Error", JOptionPane.ERROR_MESSAGE);
            return;
          }
          String output = outputFilePath.getText();
          if (output == null || output.trim().length() == 0) {
            JOptionPane.showMessageDialog(null,
                                          "Output file path name not specified",
                                          "Error", JOptionPane.ERROR_MESSAGE);
            return;
          }
//          String minmax = (String) min.getSelectedItem();
          double weight;
          try {
            weight = Double.parseDouble(weightField.getText());
          }
          catch(Exception ex) {
            JOptionPane.showMessageDialog(null,
                                          "Weight not specified",
                                          "Error", JOptionPane.ERROR_MESSAGE);
            return;
          }

          // if we get to here, everything checks out ok
          // add the function
          Constraint ff = new Constraint(nme, exec, input, output,
             weight);
          definedFunctionsModel.addElement(ff);

          functionName.setText("");
          execPath.setText("");
          inputFilePath.setText("");
          outputFilePath.setText("");
          weightField.setText("");
        }
      });

      Constrain.setConstraints(mainPanel, add, 1, 5, 1, 1,
                               GridBagConstraints.NONE,
                               GridBagConstraints.WEST,
                               1, 1);

      JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
      JButton done = new JButton("Done");
      // when done is pressed, push out the EMOPopulationParams
      // with the externalFitnessInfo set, and the useExternalFitnessEvaluation
      // flag to true
      done.addActionListener(new RunnableAction() {
        public void run() {
          Object[] functions = definedFunctionsModel.toArray();
          setConstraints(functions);

          // now create the table and add it to the pop info
          int numFunctions = functions.length;

          for(int i = 0; i < numFunctions; i++) {
            Constraint f = (Constraint) functions[i];
            /*boolean b;
            if(f.minmax.equals(MINIMIZE))
              b = true;
            else
              b = false;*/

            //ff.addExternFitnessFunction(f.name, f.exec, f.input, f.output, b);
            ConstraintExecFunction fef = new ConstraintExecFunction(f.name, 
                System.getProperty("user.dir")+File.separator+f.exec,
                System.getProperty("user.dir")+File.separator+f.input, 
                System.getProperty("user.dir")+File.separator+f.output, 
                f.weight);
            parameters.addFunction(fef);
          }

          // push out the pop info
          pushOutput(parameters, 0);
          parameters = null;
          viewDone("Done");
        }
      });
      JButton abort = new JButton("Abort");
      abort.addActionListener(new RunnableAction() {
        public void run() {
          viewCancel();
        }
      });
      buttonPanel.add(abort);
      buttonPanel.add(done);

      JPanel p3 = new JPanel(new BorderLayout());
      p3.add(mainPanel, BorderLayout.NORTH);
      p3.add(new JPanel(), BorderLayout.CENTER);

      JPanel top = new JPanel(new BorderLayout());
      mainPanel.setBorder(new EmptyBorder(1, 10, 5, 0));
      top.add(p3, BorderLayout.CENTER);
      top.add(p2, BorderLayout.EAST);

      JPanel p1 = new JPanel(new BorderLayout());
      p1.add(top, BorderLayout.WEST);
      p1.add(new JPanel(), BorderLayout.CENTER);

      setLayout(new BorderLayout());
      JLabel lbl = new JLabel("Enter Constraints");
      Font f = lbl.getFont();
      Font newFont = new Font(f.getFamily(), Font.BOLD, 16);
      lbl.setFont(newFont);
      lbl.setBorder(new EmptyBorder(10, 10, 10, 0));

      add(lbl, BorderLayout.NORTH);
      add(p1, BorderLayout.CENTER);
      add(buttonPanel, BorderLayout.SOUTH);
    }

    public void setInput(Object o, int i) {
      parameters = (Parameters)o;
    }
  }

  private static class Constraint implements Serializable {
    String name;
    String exec;
    String input;
    String output;
    double weight;

    Constraint(String n, String e, String i, String o, double w) {
      name = n;
      exec = e;
      input = i;
      output = o;
      weight = w;
    }

    public String toString() {
      return name;
    }
  }
}