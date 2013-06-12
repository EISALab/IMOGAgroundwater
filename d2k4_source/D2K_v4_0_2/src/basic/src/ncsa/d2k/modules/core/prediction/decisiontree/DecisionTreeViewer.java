package ncsa.d2k.modules.core.prediction.decisiontree;

/**
 * <p>Title: </p>
 * <p>Description: Display the decision tree in the text format
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: </p>
 * @author Dora Cai
 * @version 1.0
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.TableColumnModel;

import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.d2k.core.modules.UserView;
import ncsa.d2k.core.modules.ViewModule;
import ncsa.d2k.core.modules.VisModule;
import ncsa.d2k.modules.core.io.sql.GenericMatrixModel;
import ncsa.d2k.modules.core.prediction.decisiontree.rainforest.DecisionForestModel;
import ncsa.d2k.modules.core.prediction.decisiontree.rainforest.DecisionForestNode;
import ncsa.gui.Constrain;
import ncsa.gui.JOutlinePanel;

public class DecisionTreeViewer extends VisModule  {
  File file;
  FileWriter fw;
  static String NOTHING = "";
  String[] outputColumnLabels;
  String[] inputColumnLabels;
  String[] outputs;
  int total;


  // class for each rule extracted from the tree
  public class rule {
    String leftRule;
    String rightRule;
    double coverage;
    double accuracy;
  }
  ArrayList rules;

  String [] columnHeading;
  GenericMatrixModel treeTableModel;
  JTable treeList;

  public DecisionTreeViewer() {
  }

  public String getInputInfo(int index) {
    switch (index) {
      case 0: return "Decision Tree Model.";
      default: return "No such input";
    }
  }

  public String getOutputInfo (int i) {
    switch (i) {
      default: return "No such output";
    }
  }

  public String getModuleInfo () {
    String s = "<p> Overview: ";
    s += "This module displays a decision tree in a tablet form. </p> ";
    s += "<p> Detailed Description: ";
    s += "This module takes a decision tree model, and display the rules in text ";
    s += "using a tablet form. Each rule represents a branch in a tree. The nodes ";
    s += "in the branch form the IF part of the rule, and the leaf of the branch ";
    s += "forms the THEN part of the rule.";
    s += "<p>The rules displayed can be saved into a file. The <i>File</i> pull-down ";
    s += "menu offers a <i>Save</i> option to do this. ";
    s += "A file browser window pops up, allowing the user to select ";
    s += "where the rules should be saved. ";
    return s;

  }

  public String[] getOutputTypes () {
    String[] types = {		};
    return types;
  }

  public String[] getInputTypes() {
    String[] types = {"ncsa.d2k.modules.core.prediction.decisiontree.ViewableDTModel"};
    return types;
  }

  protected String[] getFieldNameMapping () {
    return null;
  }

  public PropertyDescription[] getPropertiesDescriptions() {
    return new PropertyDescription[0];
  }

  /**
  * Return the human readable name of the module.
  * @return the human readable name of the module.
  */
  public String getModuleName() {
    return "Decision Tree Viewer";
  }

  /**
  * Return the human readable name of the indexed input.
  * @param index the index of the input.
  * @return the human readable name of the indexed input.
  */
  public String getInputName(int index) {
    switch(index) {
      case 0:
        return "Decision Tree Model";
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

  private static NumberFormat nf;
  static {
    nf = NumberFormat.getInstance();
    nf.setMaximumFractionDigits(3);
  }

  /**
    Create the UserView object for this module-view combination.
    @return The UserView associated with this module.
  */
  protected UserView createUserView() {
    return new DisplayTreeView();
  }

  public class DisplayTreeView extends ncsa.d2k.userviews.swing.JUserPane
    implements ActionListener {
    JMenuBar menuBar;
    JMenuItem print;

    public void setInput(Object input, int index) {
      ViewableDTModel inputModel = (ViewableDTModel) input;

      DecisionForestModel model = (DecisionForestModel) inputModel;
      DecisionForestNode root = model.getRoot();
      total = root.getTotal();
      outputColumnLabels = model.getOutputColumnLabels();
      inputColumnLabels = model.getInputColumnLabels();
      outputs = model.getUniqueOutputValues();

      rules = new ArrayList();
      ArrayList leftRule = new ArrayList();
      extractRules(leftRule, root);
      //printRule(rules);
      doGUI();
      displayRules();
    }
    /*
    public Dimension getPreferredSize() {
      return new Dimension (600, 500);
    } */

    public void initView(ViewModule mod) {
      menuBar = new JMenuBar();
      JMenu fileMenu = new JMenu("File");
      print = new JMenuItem("Save...");
      print.addActionListener(this);
      fileMenu.add(print);
      menuBar.add(fileMenu);
    }

    public Object getMenu() {
       return menuBar;
    }

    public void doGUI() {
      // Panel to hold outline panels
      JPanel displayTreePanel = new JPanel();
      displayTreePanel.setLayout (new GridBagLayout());

      // Outline panel for rules
      JOutlinePanel treeInfo = new JOutlinePanel("Decision Tree");
      treeInfo.setLayout (new GridBagLayout());
      Constrain.setConstraints(treeInfo, new JLabel("Data Set Size: "),
        0,0,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,1,1);
      Constrain.setConstraints(treeInfo, new JTextField(Integer.toString(total)),
        1,0,3,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.WEST,2,1);
      String labelString = NOTHING;
      for (int colIdx=0; colIdx<inputColumnLabels.length; colIdx++) {
        if (colIdx == 0)
          labelString = labelString + inputColumnLabels[colIdx];
        else
          labelString = labelString + ", " + inputColumnLabels[colIdx];
      }
      Constrain.setConstraints(treeInfo, new JLabel("Input Features: "),
        0,1,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,1,1);
      JTextArea inputList = new JTextArea(5,1);
      inputList.setLineWrap(true);
      inputList.setAutoscrolls(true);
      inputList.setText(labelString);
      inputList.setEditable(false);
      inputList.setBackground(Color.white);
      JScrollPane textPane = new JScrollPane();
      textPane.setAutoscrolls(true);
      textPane.getViewport().add(inputList);
      textPane.setBounds(0,0,5,1);
      Constrain.setConstraints(treeInfo, textPane,
        1,1,3,1,GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,2,1);

      labelString = outputColumnLabels[0];
      Constrain.setConstraints(treeInfo, new JLabel("Output Class: "),
        0,2,1,1,GridBagConstraints.NONE,GridBagConstraints.WEST,1,1);
      Constrain.setConstraints(treeInfo, new JTextField(labelString),
        1,2,3,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.WEST,2,1);

      String[] columnHeading = {"IF","-->","THEN","COVERAGE%","ACCURACY%"};
      treeTableModel = new GenericMatrixModel(rules.size(),5,false,columnHeading);
      TableSorter sorter = new TableSorter(treeTableModel);
      treeList = new JTable(sorter);
      sorter.addMouseListenerToHeaderInTable(treeList);
      TableColumnModel colModel = treeList.getColumnModel();
      colModel.getColumn(0).setPreferredWidth(90);
      colModel.getColumn(1).setPreferredWidth(10);
      colModel.getColumn(2).setPreferredWidth(50);
      colModel.getColumn(3).setPreferredWidth(25);
      colModel.getColumn(4).setPreferredWidth(25);

      colModel.getColumn(0).setHeaderValue("IF");
      colModel.getColumn(1).setHeaderValue("-->");
      colModel.getColumn(2).setHeaderValue("THEN");
      colModel.getColumn(3).setHeaderValue("COVERAGE%");
      colModel.getColumn(4).setHeaderValue("ACCURACY%");


      JScrollPane tablePane = new JScrollPane(treeList);
      treeList.setPreferredScrollableViewportSize (new Dimension(500,300));
      Constrain.setConstraints(treeInfo, tablePane,
        0,4,4,15,GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST,1,1);

      /* Add the outline panel to displayTreePanel */
      Constrain.setConstraints(displayTreePanel, treeInfo,
        0,0,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.WEST,1,1);
      //0,0,3,8,GridBagConstraints.HORIZONTAL,GridBagConstraints.WEST,1,1);
      setLayout (new BorderLayout());
      add(displayTreePanel, BorderLayout.SOUTH);
    }

    public void actionPerformed(ActionEvent e) {
      Object src = e.getSource();

      if (src == print) {
        writeToFile();
      }
    }
  }

  /** extract rules from the decision tree model
   *  @param currLeftRule one path of the tree we have searched so far
   *  @param aNode next node we are going to exam
   */
  protected void extractRules(ArrayList currLeftRule, DecisionForestNode aNode) {
    ArrayList newLeftRule;
    // it is not a leaf node
    if (aNode.getNumChildren() > 0) {
      for (int branchIdx = 0; branchIdx < aNode.getNumChildren(); branchIdx++) {
        newLeftRule = new ArrayList();
        if (currLeftRule.size()>0) {
          newLeftRule = copyArrayList(currLeftRule);
          newLeftRule.add(aNode.getBranchLabel(branchIdx));
        }
        else {
          newLeftRule.add(aNode.getBranchLabel(branchIdx));
        }
        DecisionForestNode newNode = aNode.getChild(branchIdx);
        extractRules(newLeftRule, newNode);
      }
    }
    else { // it is a leaf node
      String rightRule = outputColumnLabels[0] + " = " + aNode.getLabel();
      double coverage = getCoverage(aNode, aNode.getLabel());
      double accuracy = getAccuracy(aNode, aNode.getLabel());

      // remove the redundant predicate. For Example: (age <= 50 and age <= 35) is redundant,
      // should be pruned as age <= 35.
      String prunedLeftRule = prunePredicate(currLeftRule);

      rule aRule = new rule();
      aRule.leftRule = prunedLeftRule;
      aRule.rightRule = rightRule;
      aRule.coverage = coverage;
      aRule.accuracy = accuracy;
      rules.add(aRule);
      currLeftRule = null;
    }
  }

  /** generate a new ArrayList which is the exact copy of the original one
   *  @param origArray original ArrayList
   *  @return a new ArrayList
   */
  protected ArrayList copyArrayList(ArrayList origArray) {
    ArrayList newArray = new ArrayList();
    for (int i=0; i < origArray.size(); i++) {
      newArray.add(origArray.get(i));
    }
    return(newArray);
  }

  private double getCoverage(DecisionForestNode aNode, String classLabel) {
    return(100*(double)aNode.getTotal()/(double)total);
  }

  private double getAccuracy(DecisionForestNode aNode, String classLabel) {
    int correct = aNode.getOutputTally(classLabel);
    int subTotal = aNode.getTotal();
    return(100*(double)correct/(double)subTotal);
  }

  /** remove the redundant predicate. For Example: (age <= 50 and age <= 35) is redundant,
   *  should be pruned as age <= 35
   *  @param leftRule a list of predicates for the left hand side rule
   *  @return a concatenated string including all predicates for the left hand side rule
   */
  private String prunePredicate(ArrayList leftRule) {
    //printArrayList(leftRule);
    String prunedLeftRule = NOTHING;
    for (int idx=0; idx<leftRule.size(); idx++) {
      // It is a numeric predicate, check redundancy and prune it if necessary
      if (leftRule.get(idx).toString().indexOf("<") >= 0 ||
          leftRule.get(idx).toString().indexOf(">=") >= 0) {
        if (pruneIt(leftRule.get(idx).toString(), leftRule)) {
          continue;
        }
        else {
          prunedLeftRule = concatString(prunedLeftRule, leftRule.get(idx).toString());
        }
      }
      else {
        prunedLeftRule = concatString(prunedLeftRule, leftRule.get(idx).toString());
      }
    }
    return prunedLeftRule;
  }

  /** test whether the predicate should be pruned
   *  @param aString one predicate
   *  @param aList the list of predicates
   *  @return ture if the predicate should be pruned
   */
  private boolean pruneIt(String aString, ArrayList aList) {
    int lessAt = -1;
    int equalAt = -1;
    double minVal = Double.NEGATIVE_INFINITY;
    double maxVal = Double.POSITIVE_INFINITY;
    lessAt = aString.indexOf("<");
    equalAt = aString.indexOf("=");
    String labelStr = NOTHING;
    if (lessAt >= 0) {
      // take out the comma from the string
      String tmpString = cutString(aString.substring(lessAt+2,aString.length()),",");
      minVal = Double.parseDouble(tmpString);
      labelStr = aString.substring(0,lessAt+1);
    }
    else if (equalAt >= 0) {
      // take out the comma from the string
      String tmpString = cutString(aString.substring(equalAt+2, aString.length()),",");
      maxVal = Double.parseDouble(tmpString);
      labelStr = aString.substring(0,equalAt+1);
    }
    for (int idx=0; idx<aList.size(); idx++) {
      // verify the predicate containing "<"
      if (lessAt >= 0 && aList.get(idx).toString().indexOf(labelStr)>=0) {
        String valStr = aList.get(idx).toString().substring(lessAt+2, aList.get(idx).toString().length());
        double newVal = Double.parseDouble(cutString(valStr,","));
        if (newVal < minVal) {
          return true;
        }
      }
      // verify the predicate containing ">="
      else if (equalAt >= 0 && aList.get(idx).toString().indexOf(labelStr)>=0) {
        String valStr = aList.get(idx).toString().substring(equalAt+2, aList.get(idx).toString().length());
        double newVal = Double.parseDouble(cutString(valStr,","));
        if (newVal > maxVal) {
          return true;
        }
      }
    }
    return false;
  }

  protected String concatString(String str1, String str2) {
    String newStr = NOTHING;
    if (!str1.equals(NOTHING)) {
          newStr = str1 + ", ";
    }
    newStr = newStr + str2;
    return(newStr);
  }

  protected void displayRules() {
    String leftRule;
    String rightRule;
    // layout of ruleList is: column 1: left handside rule (if rule),
    //                        column 2: symbol "-->",
    //                        column 3: right handside rule (then rule),
    //                        column 4: correct,
    //                        column 5: incorrect.
    //printRule(rules);
    for (int ruleIdx = 0; ruleIdx < rules.size(); ruleIdx++) {
      rule aRule = (rule)rules.get(ruleIdx);
      treeList.setValueAt(aRule.leftRule,ruleIdx,0);
      treeList.setValueAt(" -->",ruleIdx,1);
      treeList.setValueAt(aRule.rightRule,ruleIdx,2);
      treeList.setValueAt(nf.format(aRule.coverage),ruleIdx,3);
      treeList.setValueAt(nf.format(aRule.accuracy),ruleIdx,4);
    }
  }

  protected void writeToFile() {
    JFileChooser chooser = new JFileChooser();
    String delimiter = "\t";
    String newLine = "\n";
    String fileName;
    int retVal = chooser.showSaveDialog(null);
    if(retVal == JFileChooser.APPROVE_OPTION)
       fileName = chooser.getSelectedFile().getAbsolutePath();
    else
       return;
    String s1 = NOTHING;
    String s2 = NOTHING;
    String s3 = NOTHING;
    try {
      fw = new FileWriter(fileName);

      String s = "DECISION TREE: ";
      fw.write(s, 0, s.length());
      fw.write(newLine.toCharArray(), 0, newLine.length());
      fw.write(newLine.toCharArray(), 0, newLine.length());

      s = "Data Set Size: " + total;
      fw.write(s, 0, s.length());
      fw.write(newLine.toCharArray(), 0, newLine.length());

      s = NOTHING;
      for (int colIdx=0; colIdx<inputColumnLabels.length; colIdx++) {
        if (colIdx == 0)
          s = s + inputColumnLabels[colIdx];
        else
          s = s + ", " + inputColumnLabels[colIdx];
      }
      s = "Input Features: " + s;
      fw.write(s, 0, s.length());
      fw.write(newLine.toCharArray(), 0, newLine.length());

      s = outputColumnLabels[0];
      s = "Output Class: " + s;
      fw.write(s, 0, s.length());
      fw.write(newLine.toCharArray(), 0, newLine.length());
      fw.write(newLine.toCharArray(), 0, newLine.length());

      // write the actual data
      for(int rowIdx = 0; rowIdx < treeList.getRowCount(); rowIdx++) {
        s1 = NOTHING;
        s2 = NOTHING;
        s3 = NOTHING;
        for (int colIdx = 0; colIdx < treeList.getColumnCount(); colIdx++) {
          if (colIdx == 0) {
            s1 = "IF (";
            s1 = s1 + treeList.getValueAt(rowIdx, colIdx).toString() + ") ";
          }
          else if (colIdx == 2) {
            s2 = "    THEN (";
            s2 = s2 + treeList.getValueAt(rowIdx, colIdx).toString() + ") ";
          }
          else if (colIdx == 3) {
            s3 = "        with Coverage: ";
            s3 = s3 + treeList.getValueAt(rowIdx, colIdx).toString();
            s3 = s3 + "%";
          }
          else if (colIdx == 4) {
            s3 = s3 + " and Accuracy: ";
            s3 = s3 + treeList.getValueAt(rowIdx, colIdx).toString();
            s3 = s3 + "%";
          }
        }
        fw.write(s1, 0, s1.length());
        fw.write(newLine.toCharArray(), 0, newLine.length());
        fw.write(s2, 0, s2.length());
        fw.write(newLine.toCharArray(), 0, newLine.length());
        fw.write(s3, 0, s3.length());
        fw.write(newLine.toCharArray(), 0, newLine.length());
      }
      fw.flush();
      fw.close();
    }
    catch(IOException e) {
      e.printStackTrace();
    }
  }

  private void printRule(ArrayList al) {
    if (al.size()==0) {
      System.out.println("No value in ArrayList");
    }
    else {
      System.out.println("ArrayList is: ");
      for (int i=0; i<al.size(); i++) {
        rule aRule = (rule) al.get(i);
        System.out.println("Left rule : " + aRule.leftRule);
        System.out.println("Right rule : " + aRule.rightRule);
        System.out.println("Coverage : " + aRule.coverage);
        System.out.println("Accuracy : " + aRule.accuracy);
      }
    }
    System.out.println();
  }

  // take out aChar from aString
  private String cutString (String aString, String aChar) {
    // string does not contain the subString
    if (aString.indexOf(aChar) < 0) {
      return aString;
    }
    else {
      int idx = aString.indexOf(aChar);
      String tmpString = aString.substring(0,idx-1) +
                         aString.substring(idx+1, aString.length()-1);
      return tmpString;
    }
  }

  private void printArrayList(ArrayList al) {
    if (al.size()==0) {
      System.out.println("No value in ArrayList");
    }
    else {
      System.out.println("ArrayList is: ");
      for (int i=0; i<al.size(); i++) {
        System.out.println(al.get(i));
      }
    }
    System.out.println();
  }

}