package ncsa.d2k.modules.core.vis;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.vis.widgets.*;
import ncsa.d2k.userviews.swing.*;
import ncsa.gui.*;

import ncsa.d2k.modules.core.transform.StaticMethods;

/**
 * This module creates a box-and-whisker plot of scalar <code>Table</code> data
 * that also allows the user to filter data at the ends of the plot.
 *

 */
public class FilterBoxPlot extends HeadlessUIModule {

////////////////////////////////////////////////////////////////////////////////
// Module methods                                                             //
////////////////////////////////////////////////////////////////////////////////

  public String getInputInfo(int i) {
    if (i==0)
      return "A <i>MutableTable</i> with data to be visualized (and optionally filtered).";
    return "NO SUCH INPUT";
  }

  public String getInputName(int i) {
    if (i==0)
      return "Mutable Table";
    return "NO SUCH INPUT";
  }

  public String[] getInputTypes() {
    return new String[] {
        "ncsa.d2k.modules.core.datatype.table.MutableTable"
    };
  }

  public String getModuleInfo() {
    StringBuffer sb = new StringBuffer("<p>Overview: ");
    sb.append("This module creates a box-and-whisker plot of scalar ");
    sb.append("<i>Table</i> data that also allows the user to filter data ");
    sb.append("at the ends of the plot. <BR>Any row containing data greater than ");
    sb.append("the maximum red line or  smaller than the minimum red line, will ");
    sb.append("be removed from the table. You may drag the red boundaries with the ");
    sb.append("mouse arrow.");
    sb.append("</p><p>Data Handling: ");
    sb.append("This module does not modify its input data directly. ");
    sb.append("Rather, its output is a <i>Transformation</i> that can ");
    sb.append("later be applied to filter the data.");
    sb.append("Missing Values Handling: Missing values are ignored as far as the display " +
              "of statistics, and by default are not being filtered.");

    sb.append("</p>");
    return sb.toString();
  }

  public String getModuleName() {
    return "Filter Box Plot";
  }

  public String getOutputInfo(int i) {
    if (i==0)
      return "A <i>Transformation</i> to filter the <i>Table</i>.";
    return "NO SUCH OUTPUT";
  }

  public String getOutputName(int i) {
    if (i==0)
      return "Transformation";
    return "NO SUCH OUTPUT";
  }

  public String[] getOutputTypes() {
    return new String[] {
        "ncsa.d2k.modules.core.datatype.table.Transformation"
    };
  }

  protected UserView createUserView() {
    return new FilterBoxPlotView();
  }

  protected String[] getFieldNameMapping() {
    return null;
  }

  //QA Anca added this:
  //as a headless ui modules, this method is already implemented by the supper class.
 /* public PropertyDescription[] getPropertiesDescriptions() {
   PropertyDescription[] pds = new PropertyDescription[0];
  }*/

////////////////////////////////////////////////////////////////////////////////
// user view                                                                  //
////////////////////////////////////////////////////////////////////////////////

  private class FilterBoxPlotView extends JUserPane implements ActionListener {

    Table table;

    BoxPlotGroup group;

    boolean[] flags;

    ArrayList flist;
    ArrayList slist;

    ArrayList features;
    HashMap map;

    JList list;
    JPanel panel;
    JButton done, abort;



    //JTabbedPane tabbedpane;

    public void initView(ViewModule mod) {}

    public void layoutPanes() {
      removeAll();

      //tabbedpane = new JTabbedPane();

      features = new ArrayList();
      map = new HashMap();

      for (int column = 0; column<table.getNumColumns(); column++) {
        if (table.isColumnScalar(column)) {
          String label = table.getColumnLabel(column);
          features.add(label);

          FilterBoxPlotPane boxplotpane = new FilterBoxPlotPane(flist, slist, group, table, column);
          group.add(boxplotpane);

          map.put(label, boxplotpane);




          //tabbedpane.add(table.getColumnLabel(column), boxplotpane);
        }
      }
      if (features.size() == 0)
      	throw new RuntimeException ("No scalar columns found in the table.");
      list = new JList(features.toArray());

      panel = new JPanel();
      panel.setBorder(BorderFactory.createEtchedBorder());

      list.addListSelectionListener(new ListSelectionListener() {
        public void valueChanged(ListSelectionEvent event) {
          String label = (String) list.getSelectedValue();
          FilterBoxPlotPane pane = (FilterBoxPlotPane) map.get(label);

          panel.removeAll();
          panel.add(pane);
          panel.revalidate();
          panel.repaint();
        }
      });


      list.setSelectedIndex(0);

      done = new JButton("Done");
      abort = new JButton("Abort");

      done.addActionListener(this);
      abort.addActionListener(this);

      JPanel buttonpanel = new JPanel();
      buttonpanel.add(abort);
      buttonpanel.add(done);

      setLayout(new GridBagLayout());
      Constrain.setConstraints(this, new JScrollPane(list), 0, 0, 1, 1, GridBagConstraints.BOTH, GridBagConstraints.NORTHWEST, 1, 1);
      Constrain.setConstraints(this, panel, 1, 0, 1, 1, GridBagConstraints.BOTH, GridBagConstraints.NORTHWEST, 0, 0);
      Constrain.setConstraints(this, buttonpanel, 0, 1, 2, 1, GridBagConstraints.BOTH, GridBagConstraints.CENTER, 1, 1);

      //add(tabbedpane, BorderLayout.CENTER);
      //add(buttonpanel, BorderLayout.SOUTH);
    }

    public void setInput(Object object, int input) {
      if (input==0) {
        table = (Table) object;

        group = new BoxPlotGroup(this);

        flags = new boolean[table.getNumRows()];
        for (int index = 0; index<flags.length; index++) {
          flags[index] = true;
        }

        flist = new ArrayList();
        slist = new ArrayList();

        flist.add(flags);
        slist.add(new Integer(table.getNumRows()));

        layoutPanes();
      }
    }

    public void actionPerformed(ActionEvent event) {
      Object source = event.getSource();

      if (source==done) {

        //headless conversion support
        Set set = map.keySet();

        Object[] arr = set.toArray();




        //setAttributes(arr);
        //boolean[] isChanged = new boolean[arr.length];
        int counter = 0;


        for(int i=0; i<arr.length; i++){
          FilterBoxPlotPane temp = (FilterBoxPlotPane) map.get((String)arr[i]);

          if(temp.getChanged())
            counter++;

        }//for i

        double[] _min = new double[counter];
        double[] _max = new double[counter];
        String[] atts = new String[counter];

        for(int i=0, j=0; i<arr.length; i++){
          FilterBoxPlotPane temp = (FilterBoxPlotPane) map.get( (String) arr[i]);
          if(temp.getChanged()){
             _min[j] = temp.getLower();
             _max[j] = temp.getUpper();
             atts[j] = (String)arr[i];
             j++;
          }//if temp
        }//for i

        setMin(_min);
        setMax(_max);
        setAttributes(atts);

        //headless conversion support

        boolean[] flags = (boolean[]) flist.get(flist.size()-1);

        // MutableTable mtable = (MutableTable) table;
        // mtable.removeRowsByFlag(flags);
        // pushOutput((Table) mtable, 0);




        for (int index = 0; index<flags.length; index++){
          flags[index] = !flags[index];

        }




        pushOutput(new BooleanFilterTransformation(flags), 0);

        viewDone("Done");
      }

      if (source==abort)
        viewAbort();
    }

    public Dimension getPreferredSize() {
      return new Dimension(500, 375);
    }

  }

////////////////////////////////////////////////////////////////////////////////
// output Transformation                                                      //
////////////////////////////////////////////////////////////////////////////////

  private class BooleanFilterTransformation implements Transformation {

    private boolean[] flags;

    BooleanFilterTransformation(boolean[] flags) {
      this.flags = flags;
    }

    public boolean transform(MutableTable mt) {
      try {
		for (int i = flags.length-1 ; i >= 0 ; i--){
					 if (flags[i]) mt.removeRow(i);
				   }
             }
      catch (Exception e) {
        return false;
      }
      // 4/7/02 commented out by Loretta...
      // this add gets done by applyTransformation
      // mt.addTransformation(this);
      return true;
    }

  }


  //headless conversion support

   private String[] attributes; //names of each attribute.

   public void setAttributes(Object[] atts){
   attributes = (String[])atts;
 }
   public Object[] getAttributes(){return attributes;}

   //for each attribute, saving its lower and upper boundaries.
   private double[] max;
   private double[] min;

   public void setMax(Object _max){max = (double[])_max;}
   public Object getMax(){return max;}

   public void setMin(Object _min){min = (double[])_min;}
   public Object getMin(){return min;}


   public void doit () throws Exception {

     Table _table = (Table)pullInput(0);
     boolean[] flags = new boolean[_table.getNumRows()];
     for (int i=0; i<flags.length; i++) flags[i] = false;
       if(attributes == null )
         throw new Exception(this.getAlias()+" has not been configured. Before running headless, run with the gui and configure the parameters.");

     if( attributes.length == 0){
       System.out.println(getAlias()+": No attributes were chosed to be filtered. " +
                          "The transformation will be an empty one");
        pushOutput(new BooleanFilterTransformation(flags), 0);
        return;
     }//if attributes

    HashMap scalarMap = StaticMethods.getScalarAttributes(_table);
    if(scalarMap.size() == 0){
      System.out.println(getAlias()+": Warning - Table " + _table.getLabel() +
                         " has no scalar columns.");
      //pushOutput(new BooleanFilterTransformation(flags), 0);
        //return;
    }//if scalar map

    boolean[] relevant = StaticMethods.getRelevant(attributes, scalarMap);

    if(relevant.length < attributes.length){
      throw new Exception(getAlias()+
          ": Some of the configured attributes are not scalar and/or in table "
          + _table.getLabel() + ". Please reconfigure the module so it can run Headless.");
      //pushOutput(new BooleanFilterTransformation(flags), 0);
        //return;
    }//if relevant

    for(int i=0; i<relevant.length; i++)
      if(relevant[i]){
        //getting the index of attributes[i] into _table.
        int idx = ((Integer)scalarMap.get(attributes[i].toUpperCase())).intValue();
        filter(idx, i, flags, _table);
      }




    pushOutput(new BooleanFilterTransformation(flags), 0);



   }//doit

   /**
    * creates the filter for <codE>table</code> according to min and max arrays.
    * for each value is column no. <code>column</codE> in <codE>table</code>,
    * if it is not in the boundaries of <code>[min[column], max[column]]</code>
    *  then its flag will be marked true.
    *
    * @param column - column number to check its values. (index into <code>table</table>
    * @param att - index of tested filtered attribute into attributes array.
    * @param flags  - array of booleans. flags[i] = true means this row is marked for removal.
    * @param table  - a table to have its values at column no. <code>column</codE>
    *                 to be checked and marked for filteration
    */
    private void filter(int column, int att, boolean[] flags, Table table){





     //going over the flags
     for (int i=0; i<flags.length; i++)
       if(!flags[i]) //if this row is not yet marked for removal
         //if the value is not in the boundaries
        if(!table.isValueMissing(i, column) &&
         (table.getDouble(i, column) < min[att] || table.getDouble(i, column) > max[att])){
           //mark this row for removal
           flags[i] = true;



         }



   }//filter



   //headless conversion support


}//FilterBoxPlot


 /**
 * QA comments
 *
 * 01-01-04:
 * Vered started qa process.
 * added to module into documentation about missing values handling (As regular ones).
 *
 * 01-04-04:
 * bug 195 - what to do about missing values. awaiting decision regarding this one.
 * right now missing values are not participating in the statistics but do get
 * to be filtered as regular ones. added this to module info.
 *
 * 01-06-04:
 * bug 195 is fixed. the module will no longer ignore missing values.
 * modules is ready fro basic.
*/