package ncsa.d2k.modules.core.prediction.decisiontree.widgets;

import ncsa.d2k.core.modules.*;
//import ncsa.d2k.infrastructure.views.*;
import ncsa.d2k.userviews.swing.*;
import ncsa.d2k.gui.*;
import ncsa.gui.Constrain;
import ncsa.gui.JOutlinePanel;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import ncsa.d2k.modules.core.prediction.decisiontree.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

public class SearchPanel extends JPanel implements ActionListener {

  private static final String GREATER_THAN = ">";
  private static final String LESS_THAN = "<";
  private static final String GREATER_THAN_EQUAL_TO = ">=";
  private static final String LESS_THAN_EQUAL_TO = "<=";
  private static final String NOT_EQUAL_TO = "!=";
  private static final String EQUAL_TO = "==";

  private static final String AND = "&&";
  private static final String OR = "||";

  // Population
  JComboBox populationoutputs;
  JComboBox populationoperators;
  JTextField populationvalue;
  JButton populationadd;

  // Percent
  JComboBox percentoutputs;
  JComboBox percentoperators;
  JTextField percentvalue;
  JButton percentadd;

  // Purity
  JComboBox purityoperators;
  JTextField purityvalue;
  JButton purityadd;

  // Split
  boolean scalar;
  JComboBox splitinputs;
  JComboBox splitoperators;
  JComboBox splitvaluebox;
  JTextField splitvaluefield;
  JButton splitadd;

  JComboBox operators;

  JButton replace;
  JButton remove;

  JButton close;
  JButton search;
  JButton clear;

  ArrayList searchlist;
  Viewport nodeindex;
  int searchindex;

  JLabel resultlabel;

  JButton next;
  JButton previous;

  JPanel searchpanel;
  JD2KFrame frame;

  JList conditionlist;
  DefaultListModel listmodel;

  TreeScrollPane treescrollpane;

  NominalViewableDTModel model;

  public SearchPanel(TreeScrollPane scrollpane, JD2KFrame parent) {
    treescrollpane = scrollpane;
    frame = parent;

    model = (NominalViewableDTModel) treescrollpane.getViewableModel();
    String[] outputs = model.getUniqueOutputValues();
    String[] inputs = model.getInputs();

    searchlist = new ArrayList();

    // Population search
    populationoutputs = new JComboBox(outputs);

    populationoperators = new JComboBox();
    populationoperators.addItem(GREATER_THAN);
    populationoperators.addItem(LESS_THAN);
    populationoperators.addItem(GREATER_THAN_EQUAL_TO);
    populationoperators.addItem(LESS_THAN_EQUAL_TO);
    populationoperators.addItem(EQUAL_TO);
    populationoperators.addItem(NOT_EQUAL_TO);

    populationvalue = new JTextField(5);

    populationadd = new JButton("Add");
    populationadd.addActionListener(this);

    // Percent search
    percentoutputs = new JComboBox(outputs);

    percentoperators = new JComboBox();
    percentoperators.addItem(GREATER_THAN);
    percentoperators.addItem(LESS_THAN);
    percentoperators.addItem(GREATER_THAN_EQUAL_TO);
    percentoperators.addItem(LESS_THAN_EQUAL_TO);
    percentoperators.addItem(EQUAL_TO);
    percentoperators.addItem(NOT_EQUAL_TO);

    percentvalue = new JTextField(5);

    percentadd = new JButton("Add");
    percentadd.addActionListener(this);

    // Purity search
    purityoperators = new JComboBox();
    purityoperators.addItem(GREATER_THAN);
    purityoperators.addItem(LESS_THAN);
    purityoperators.addItem(GREATER_THAN_EQUAL_TO);
    purityoperators.addItem(LESS_THAN_EQUAL_TO);
    purityoperators.addItem(EQUAL_TO);
    purityoperators.addItem(NOT_EQUAL_TO);

    purityvalue = new JTextField(5);

    purityadd = new JButton("Add");
    purityadd.addActionListener(this);

    // Split attributes
    splitinputs = new JComboBox(inputs);
    splitinputs.addActionListener(this);

    splitoperators = new JComboBox();

    int index = splitinputs.getSelectedIndex();

    if (model.scalarInput(index)) {
      scalar = true;

      splitoperators.addItem(GREATER_THAN);
      splitoperators.addItem(LESS_THAN);
      splitoperators.addItem(GREATER_THAN_EQUAL_TO);
      splitoperators.addItem(LESS_THAN_EQUAL_TO);
      splitoperators.addItem(EQUAL_TO);
      splitoperators.addItem(NOT_EQUAL_TO);

      splitvaluefield = new JTextField(5);
    }
    else {
      scalar = false;

      splitoperators.addItem(EQUAL_TO);
      splitoperators.addItem(NOT_EQUAL_TO);

      splitvaluebox = new JComboBox(model.getUniqueInputValues(index));
    }

    splitadd = new JButton("Add");
    splitadd.addActionListener(this);

    // Search panel
    searchpanel = new JPanel();
    searchpanel.setLayout(new GridBagLayout());

    // Population
    Constrain.setConstraints(searchpanel, new JLabel("Population:"), 0, 0, 1, 1, GridBagConstraints.HORIZONTAL,
                             GridBagConstraints.NORTHWEST, 1, 0, new Insets(5, 5, 5, 5));
    Constrain.setConstraints(searchpanel, populationoutputs, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL,
                             GridBagConstraints.NORTHWEST, 0, 0, new Insets(5, 5, 5, 5));
    Constrain.setConstraints(searchpanel, populationoperators, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL,
                             GridBagConstraints.NORTHWEST, 0, 0, new Insets(5, 5, 5, 5));
    Constrain.setConstraints(searchpanel, populationvalue, 3, 0, 1, 1, GridBagConstraints.HORIZONTAL,
                             GridBagConstraints.NORTHWEST, 0, 0, new Insets(5, 5, 5, 5));
    Constrain.setConstraints(searchpanel, populationadd, 4, 0, 1, 1, GridBagConstraints.HORIZONTAL,
                             GridBagConstraints.NORTHWEST, 0, 0, new Insets(5, 5, 5, 5));

    // Percent
    Constrain.setConstraints(searchpanel, new JLabel("Percent:"), 0, 1, 1, 1, GridBagConstraints.HORIZONTAL,
                             GridBagConstraints.NORTHWEST, 1, 0, new Insets(5, 5, 5, 5));
    Constrain.setConstraints(searchpanel, percentoutputs, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL,
                             GridBagConstraints.NORTHWEST, 0, 0, new Insets(5, 5, 5, 5));
    Constrain.setConstraints(searchpanel, percentoperators, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL,
                             GridBagConstraints.NORTHWEST, 0, 0, new Insets(5, 5, 5, 5));
    Constrain.setConstraints(searchpanel, percentvalue, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL,
                             GridBagConstraints.NORTHWEST, 0, 0, new Insets(5, 5, 5, 5));
    Constrain.setConstraints(searchpanel, percentadd, 4, 1, 1, 1, GridBagConstraints.HORIZONTAL,
                             GridBagConstraints.NORTHWEST, 0, 0, new Insets(5, 5, 5, 5));

    // Purity
    Constrain.setConstraints(searchpanel, new JLabel("Purity:"), 0, 2, 1, 1, GridBagConstraints.HORIZONTAL,
                             GridBagConstraints.NORTHWEST, 1, 0, new Insets(5, 5, 5, 5));
    Constrain.setConstraints(searchpanel, purityoperators, 2, 2, 1, 1, GridBagConstraints.HORIZONTAL,
                             GridBagConstraints.NORTHWEST, 0, 0, new Insets(5, 5, 5, 5));
    Constrain.setConstraints(searchpanel, purityvalue, 3, 2, 1, 1, GridBagConstraints.HORIZONTAL,
                             GridBagConstraints.NORTHWEST, 0, 0, new Insets(5, 5, 5, 5));
    Constrain.setConstraints(searchpanel, purityadd, 4, 2, 1, 1, GridBagConstraints.HORIZONTAL,
                             GridBagConstraints.NORTHWEST, 0, 0, new Insets(5, 5, 5, 5));

    // Split
    Constrain.setConstraints(searchpanel, new JLabel("Split:"), 0, 3, 1, 1, GridBagConstraints.HORIZONTAL,
                             GridBagConstraints.NORTHWEST, 1, 1, new Insets(5, 5, 5, 5));
    Constrain.setConstraints(searchpanel, splitinputs, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL,
                             GridBagConstraints.NORTHWEST, 0, 0, new Insets(5, 5, 5, 5));
    Constrain.setConstraints(searchpanel, splitoperators, 2, 3, 1, 1, GridBagConstraints.HORIZONTAL,
                             GridBagConstraints.NORTHWEST, 0, 0, new Insets(5, 5, 5, 5));

    if (scalar)
      Constrain.setConstraints(searchpanel, splitvaluefield, 3, 3, 1, 1, GridBagConstraints.HORIZONTAL,
                               GridBagConstraints.NORTHWEST, 0, 0, new Insets(5, 5, 5, 5));
    else
      Constrain.setConstraints(searchpanel, splitvaluebox, 3, 3, 1, 1, GridBagConstraints.HORIZONTAL,
                               GridBagConstraints.NORTHWEST, 0, 0, new Insets(5, 5, 5, 5));

    Constrain.setConstraints(searchpanel, splitadd, 4, 3, 1, 1, GridBagConstraints.HORIZONTAL,
                             GridBagConstraints.NORTHWEST, 0, 0, new Insets(5, 5, 5, 5));

    JScrollPane searchscroll = new JScrollPane(searchpanel);
    searchscroll.setMinimumSize(searchscroll.getPreferredSize());

    // Conditions
    conditionlist = new JList();
    listmodel = new DefaultListModel();
    JLabel conditionlabel = new JLabel("Current Conditions");
    conditionlist.setModel(listmodel);

    JScrollPane conditionscroll = new JScrollPane(conditionlist);
    JViewport viewport = new JViewport();
    viewport.setView(conditionlabel);
    conditionscroll.setColumnHeader(viewport);

    // Conditions panel
    JPanel conditionpanel = new JPanel();
    conditionpanel.setLayout(new BorderLayout());
    conditionpanel.add(conditionscroll, BorderLayout.CENTER);

    remove = new JButton("Remove");
    remove.addActionListener(this);

    operators = new JComboBox();
    operators.addItem(AND);
    operators.addItem(OR);

    replace = new JButton("Replace");
    replace.addActionListener(this);

    JPanel conditionbuttons = new JPanel();
    conditionbuttons.setLayout(new GridBagLayout());

    Constrain.setConstraints(conditionbuttons, remove, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL,
                             GridBagConstraints.NORTHWEST, 0, 0, new Insets(2, 2, 2, 2));
    Constrain.setConstraints(conditionbuttons, new JPanel(), 1, 0, 1, 1, GridBagConstraints.HORIZONTAL,
                             GridBagConstraints.NORTHWEST, 1, 1, new Insets(2, 2, 2, 2));
    Constrain.setConstraints(conditionbuttons, operators, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL,
                             GridBagConstraints.NORTHWEST, 0, 0, new Insets(2, 2, 2, 2));
    Constrain.setConstraints(conditionbuttons, replace, 3, 0, 1, 1, GridBagConstraints.HORIZONTAL,
                             GridBagConstraints.NORTHWEST, 0, 0, new Insets(2, 2, 2, 2));

    conditionpanel.add(conditionbuttons, BorderLayout.SOUTH);

    JSplitPane splitpane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, searchscroll, conditionpanel);

    // Button panel
    JPanel buttonpanel = new JPanel();
    buttonpanel.setLayout(new GridBagLayout());

    close = new JButton("Close");
    close.addActionListener(this);

    search = new JButton("Search");
    search.addActionListener(this);

    clear = new JButton("Clear");
    clear.addActionListener(this);

    Constrain.setConstraints(buttonpanel, new JPanel(), 0, 0, 1, 1, GridBagConstraints.HORIZONTAL,
                             GridBagConstraints.NORTHWEST, 1, 1, new Insets(2, 2, 2, 2));
    Constrain.setConstraints(buttonpanel, search, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL,
                             GridBagConstraints.NORTHWEST, 0, 0, new Insets(2, 2, 2, 2));
    Constrain.setConstraints(buttonpanel, clear, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL,
                             GridBagConstraints.NORTHWEST, 0, 0, new Insets(2, 2, 2, 2));
    Constrain.setConstraints(buttonpanel, close, 3, 0, 1, 1, GridBagConstraints.HORIZONTAL,
                             GridBagConstraints.NORTHWEST, 0, 0, new Insets(2, 2, 2, 2));

    // Result panel
    JPanel resultpanel = new JPanel();
    resultpanel.setLayout(new GridBagLayout());

    resultlabel = new JLabel("Search Results: ");

    next = new JButton("Next");
    next.addActionListener(this);

    previous = new JButton("Previous");
    previous.addActionListener(this);

    Constrain.setConstraints(resultpanel, resultlabel, 3, 0, 1, 1, GridBagConstraints.HORIZONTAL,
                             GridBagConstraints.WEST, 1, 1, new Insets(2, 2, 2, 2));
    Constrain.setConstraints(resultpanel, next, 4, 0, 1, 1, GridBagConstraints.HORIZONTAL,
                             GridBagConstraints.NORTHWEST, 0, 0, new Insets(2, 2, 2, 2));
    Constrain.setConstraints(resultpanel, previous, 5, 0, 1, 1, GridBagConstraints.HORIZONTAL,
                             GridBagConstraints.NORTHWEST, 0, 0, new Insets(2, 2, 2, 2));

    setLayout(new GridBagLayout());
    Constrain.setConstraints(this, splitpane, 0, 0, 1, 1, GridBagConstraints.BOTH,
                             GridBagConstraints.NORTHWEST, 1, 1, new Insets(2, 2, 2, 2));
    Constrain.setConstraints(this, buttonpanel, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL,
                             GridBagConstraints.NORTHWEST, 0, 0, new Insets(2, 2, 2, 2));
    Constrain.setConstraints(this, new JSeparator(SwingConstants.HORIZONTAL), 0, 2, 1, 1, GridBagConstraints.HORIZONTAL,
                             GridBagConstraints.NORTHWEST, 0, 0, new Insets(2, 2, 2, 2));
    Constrain.setConstraints(this, resultpanel, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL,
                             GridBagConstraints.NORTHWEST, 0, 0, new Insets(2, 2, 2, 2));
  }

  public void actionPerformed(ActionEvent event) {
    Object source = event.getSource();

    if (source==populationadd) {
      String attribute = (String) populationoutputs.getSelectedItem();
      String operator = (String) populationoperators.getSelectedItem();
      String svalue = populationvalue.getText();
      double dvalue = 0;

      try {
        dvalue = Double.parseDouble(svalue);
      }
      catch (Exception exception) {
        exception.printStackTrace();
      }

      PopulationCondition condition = new PopulationCondition(attribute, operator, dvalue);
      listmodel.addElement(condition);

      populationvalue.setText("");
    }

    else if (source==percentadd) {
      String attribute = (String) percentoutputs.getSelectedItem();
      String operator = (String) percentoperators.getSelectedItem();
      String svalue = percentvalue.getText();
      double dvalue = 0;

      try {
        dvalue = Double.parseDouble(svalue);
      }
      catch (Exception exception) {
      }

      PercentCondition condition = new PercentCondition(attribute, operator, dvalue);
      listmodel.addElement(condition);

      percentvalue.setText("");
    }

    else if (source==purityadd) {
      String operator = (String) purityoperators.getSelectedItem();
      String svalue = purityvalue.getText();
      double dvalue = 0;

      try {
        dvalue = Double.parseDouble(svalue);
      }
      catch (Exception exception) {
      }

      PurityCondition condition = new PurityCondition(operator, dvalue);
      listmodel.addElement(condition);

      purityvalue.setText("");
    }

    else if (source==splitinputs) {
      int index = splitinputs.getSelectedIndex();

      if (model.scalarInput(index)&&!scalar) {
        scalar = true;

        splitoperators.removeAllItems();
        splitoperators.addItem(GREATER_THAN);
        splitoperators.addItem(LESS_THAN);
        splitoperators.addItem(GREATER_THAN_EQUAL_TO);
        splitoperators.addItem(LESS_THAN_EQUAL_TO);
        splitoperators.addItem(EQUAL_TO);
        splitoperators.addItem(NOT_EQUAL_TO);

        splitvaluefield = new JTextField(5);

        searchpanel.remove(splitvaluebox);
        Constrain.setConstraints(searchpanel, splitvaluefield, 3, 3, 1, 1, GridBagConstraints.HORIZONTAL,
                                 GridBagConstraints.NORTHWEST, 0, 0, new Insets(5, 5, 5, 5));

        revalidate();
        repaint();
      }
      else if (!model.scalarInput(index)&&scalar) {
        scalar = false;

        splitoperators.removeAllItems();
        splitoperators.addItem(EQUAL_TO);
        splitoperators.addItem(NOT_EQUAL_TO);

        splitvaluebox = new JComboBox(model.getUniqueInputValues(index));

        searchpanel.remove(splitvaluefield);
        Constrain.setConstraints(searchpanel, splitvaluebox, 3, 3, 1, 1, GridBagConstraints.HORIZONTAL,
                                 GridBagConstraints.NORTHWEST, 0, 0, new Insets(5, 5, 5, 5));

        revalidate();
        repaint();
      }
    }

    else if (source==splitadd) {
      String attribute = (String) splitinputs.getSelectedItem();
      String operator = (String) splitoperators.getSelectedItem();

      if (scalar) {
        try {
          String svalue = splitvaluefield.getText();
          double dvalue = Double.parseDouble(svalue);

          SplitCondition condition = new SplitCondition(attribute, operator, dvalue);
          listmodel.addElement(condition);

          splitvaluefield.setText("");
        }
        catch (Exception exception) {
        }
      }
      else {
        try {
          String svalue = (String) splitvaluebox.getSelectedItem();

          SplitCondition condition = new SplitCondition(attribute, operator, svalue);
          listmodel.addElement(condition);
        }
        catch (Exception exception) {
        }
      }
    }

    else if (source==remove) {
      int selected = conditionlist.getSelectedIndex();

      if (selected!=-1)
        listmodel.remove(selected);
    }

    else if (source==replace) {
      String operator = (String) operators.getSelectedItem();

      int[] indices = conditionlist.getSelectedIndices();
      if (indices.length<2)
        return;

      Condition first = (Condition) listmodel.getElementAt(indices[0]);
      Condition second = (Condition) listmodel.getElementAt(indices[1]);
      Condition three = new CompoundCondition(first, second, operator);

      listmodel.removeElementAt(indices[0]);
      listmodel.removeElementAt(indices[1]-1);
      listmodel.add(0, three);
    }

    else if (source==close) {
      frame.setVisible(false);
      listmodel.removeAllElements();
    }

    else if (source==search) {
      searchlist.clear();
      searchindex = -1;

      Object[] conditions = listmodel.toArray();

      if (conditions.length==1) {
        Condition condition = (Condition) conditions[0];
        searchTree(condition, treescrollpane.getViewRoot());

        resultlabel.setText("Search Results: "+searchlist.size()+" nodes");
        repaint();
        treescrollpane.repaint();
      }
    }

    else if (source==clear) {
      searchlist.clear();
      searchindex = -1;

      if (nodeindex!=null)
        nodeindex.setSearchBackground(false);

      resultlabel.setText("Search Results: ");

      repaint();
      treescrollpane.clearSearch();
      treescrollpane.repaint();
    }

    else if (source==next) {
      if (searchlist.size()>0) {
        searchindex++;

        if (searchindex==searchlist.size())
          searchindex = 0;

        if (nodeindex!=null)
          nodeindex.setSearchBackground(false);

        Viewport node = (Viewport) searchlist.get(searchindex);

        nodeindex = node;
        nodeindex.setSearchBackground(true);

        updateViewport(node);
      }
    }

    else if (source==previous) {
      if (searchlist.size()>0) {
        searchindex--;

        if (searchindex<0)
          searchindex = searchlist.size()-1;

        if (nodeindex!=null)
          nodeindex.setSearchBackground(false);

        Viewport node = (Viewport) searchlist.get(searchindex);

        nodeindex = node;
        nodeindex.setSearchBackground(true);

        updateViewport(node);
      }
    }
  }

  // Update viewport in tree scroll pane to show node
  void updateViewport(Viewport node) {
    double xnode = node.x;
    double ynode = node.y;

    double nodeheight = node.getViewHeight();
    double nodewidth = node.getViewWidth();

    double scale = treescrollpane.getScale();

    JViewport viewport = treescrollpane.viewport;

    Dimension dimension = viewport.getExtentSize();
    double viewportwidth = dimension.getWidth();
    double viewportheight = dimension.getHeight();

    dimension = viewport.getViewSize();
    double viewwidth = dimension.getWidth();
    double viewheight = dimension.getHeight();

    double xviewport = scale*xnode-viewportwidth/2;
    double yviewport = scale*(ynode+nodeheight/2)-viewportheight/2;

    if (xviewport<0)
      xviewport = 0;
    if (xviewport>viewwidth-viewportwidth)
      xviewport = viewwidth-viewportwidth;
    if (yviewport<0)
      yviewport = 0;
    if (yviewport>viewheight-viewportheight)
      yviewport = viewheight-viewportheight;

    treescrollpane.scroll((int) xviewport, (int) yviewport);
  }

  // Recursively search tree
  void searchTree(Condition condition, Viewport node) {
    boolean evaluation;

    if (node.isLeaf()) {
      evaluation = condition.evaluate(condition, node);
      node.setSearch(evaluation);

      if (evaluation)
        searchlist.add(node);

      return;
    }

    for (int index = 0; index<node.getNumChildren(); index++) {
      Viewport child = node.getChild(index);
      searchTree(condition, child);
    }

    evaluation = condition.evaluate(condition, node);
    node.setSearch(evaluation);

    if (evaluation)
      searchlist.add(node);
  }

  // Encapsulates a search criterion
  protected class Condition {
    Condition first, second;

    String attribute;
    String operator;
    double value;

    boolean evaluate(Condition condition, Viewport node) {
      boolean expression;

      if (condition instanceof CompoundCondition) {
        expression = evaluate(condition.first, node);

        if (condition.operator==AND)
          expression = expression&&evaluate(condition.second, node);
        else if (condition.operator==OR)
          expression = expression||evaluate(condition.second, node);
      }
      else {
        return node.evaluate(condition);
      }

      return expression;
    }
  }

  // Combines two conditions to form a boolean expression
  class CompoundCondition extends Condition {
    CompoundCondition(Condition first, Condition second, String operator) {
      this.first = first;
      this.second = second;
      this.operator = operator;
    }

    public String toString() {
      return toString(this);
    }

    String toString(Condition condition) {
      String expression;

      if (condition instanceof CompoundCondition) {
        expression = "("+toString(condition.first)+" "+condition.operator;
        expression = expression+" "+toString(condition.second)+")";
      }
      else
        return condition.toString();

      return expression;
    }
  }

  class PopulationCondition extends Condition {
    PopulationCondition(String attribute, String operator, double value) {
      this.attribute = attribute;
      this.operator = operator;
      this.value = value;
    }

    public String toString() {
      return "Population: "+attribute+" "+operator+" "+value;
    }
  }

  class PercentCondition extends Condition {
    PercentCondition(String attribute, String operator, double value) {
      this.attribute = attribute;
      this.operator = operator;
      this.value = value;
    }

    public String toString() {
      return "Percent: "+attribute+" "+operator+" "+value;
    }
  }

  class PurityCondition extends Condition {
    PurityCondition(String operator, double value) {
      this.attribute = "Purity";
      this.operator = operator;
      this.value = value;
    }

    public String toString() {
      return "Purity: "+operator+" "+value;
    }
  }

  class SplitCondition extends Condition {
    String svalue;
    boolean scalar;

    SplitCondition(String attribute, String operator, double value) {
      this.attribute = attribute;
      this.operator = operator;
      this.value = value;

      scalar = true;
    }

    SplitCondition(String attribute, String operator, String svalue) {
      this.attribute = attribute;
      this.operator = operator;
      this.svalue = svalue;

      scalar = false;
    }

    public String toString() {
      if (scalar)
        return "Split: "+attribute+" "+operator+" "+value;
      else
        return "Split: "+attribute+" "+operator+" "+svalue;
    }
  }
}