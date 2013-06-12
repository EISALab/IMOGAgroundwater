


/*
 * Created on Sep 9, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */

package ncsa.d2k.modules.core.transform.table;


import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

import ncsa.d2k.modules.core.transform.StaticMethods;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.gui.*;


/**
 Cascade sorts a MutableTable by sorting the first column and then
 successive columns based on runs in the previous column. A run is
 a collection of similar values in a column.

 */
public class SortTable extends ncsa.d2k.core.modules.HeadlessUIModule {


  public String getInputInfo(int index) {
    switch (index) {
      case 0: return "The Table to sort.";
      default: return "No such input";
    }
  }


  public String[] getInputTypes() {
    String[] types = {"ncsa.d2k.modules.core.datatype.table.MutableTable"};
    return types;
  }


  public String getInputName(int index) {
    switch (index) {
      case 0: return "MutableTable";
      default: return "No such input";
    }
  }


  public String getOutputInfo(int index) {
    switch (index) {
      case 0: return "The sorted Table.";
      default: return "No such output";
    }
  }


  public String[] getOutputTypes() {
    String[] types = {"ncsa.d2k.modules.core.datatype.table.MutableTable"};
    return types;
  }


  public String getOutputName(int index) {
    switch (index) {
      case 0: return "MutableTable";
      default: return "No such output";
    }
  }


  public String getModuleInfo() {
     String info = "<P><b>Overview:</b><br> This module cascade sorts a MutableTable."+
         "</P><p><b>Detailed Description:</b><br> This module provides the user with an "+
         "interface to choose sorting columns for a cascading sort: The whole table is "+
         "being sorted according to the first column that is chosen. If more than one "+
         "column is chosen for the sort for each successive column, a sort is being "+
         "applied on the table only for runs in the previous column.<br>"+
         "A run is a collection of identical values in a column.</P><P>"+
         "<u>Missing Values Handling:</u> This module handles missing values as if they were " +
         "real meaningful values. For Example: If a missing value in a numeric column " +
         "is represented by zero, then this is the value according to which its record " +
         "will be sorted.</P>" +
         "<P><U>Note:</U> If, for example, the user selects a sorting column only for the second "+
         "sort, the module will relate to this selection as if it is the first sort. "+
         "No messages will be given regarding this miss-selection.</P><P><B>"+
         "Data Handling:</b><br> The data values do not change. Only the rows and possibly the columns are reordered.</P>";
/*
    String info = "<p>Overview: ";
    info += "This module cascade sorts a MutableTable by sorting the first column and then successive columns ";
    info += "based on runs in the previous column. A run is a collection of similar values in a column.";
    info += "</p><p>Detailed Description: ";
    info += "This module can be used to sort a table using multiple attributes. For example, the first attribute ";
    info += "sorts the entire table. Then the second attribute sorts the runs from the first attribute.";
    info += "</p><p>Data Handling: ";
    info += "The data values do not change. Only the rows and possibly the columns are reordered.";*/
    return info;
  }


  public String getModuleName() {
    return "Cascade-sort Table";
  }


  protected UserView createUserView() {
    return new SortTableView();
  }


  public String[] getFieldNameMapping() {
    return null;
  }


  private void done(Table table) {
    pushOutput(table, 0);
    viewDone("Done");
  }


  public PropertyDescription[] getPropertiesDescriptions() {
    PropertyDescription[] descriptions = new PropertyDescription[3];

    descriptions[0] = this.supressDescription;


    descriptions[1] = new PropertyDescription("numberOfSorts",
        "Number of attributes to use for sorting",
        "Determines the number of attributes to use for sorting.");


    descriptions[2] = new PropertyDescription("reorderColumns",
        "Reorder columns based on order of attributes used",
        "Determines if columns will be reordered based on order of attributes used.");


    return descriptions;
  }

 //ANCA: added doit method for Headless execution

 public void doit() throws Exception {
        MutableTable table =(MutableTable) pullInput(0);
        CascadeSort cSort = new CascadeSort(table);


         if(sortOrderNames == null)
           throw new Exception (this.getAlias()+" has not been configured. Before running headless, run with the gui and configure the parameters.");

        int[] sortorder;
        if(sortOrderNames.length == 0)
          sortorder = cSort.getDefaultSortOrder();
        else
          sortorder = getSortOrder(table);

        if(sortorder == null)
          sortorder = cSort.getDefaultSortOrder();

        if(sortorder != null && sortorder.length != 0)
          cSort.sort(sortorder);

          if(this.reorderColumns) cSort.reorder(sortorder);

        pushOutput(table,0);
}//doit

       //vered - added this method, to figure out the cascading sort order
       //according to sortOrderNames
       private int[] getSortOrder(Table table) throws Exception{

         int[] retVal = new int[0];
         HashMap columns = StaticMethods.getAvailableAttributes(table);
         if(columns.size() == 0)
           System.out.println(getAlias() + ": Warning - The input table has no columns.");



         retVal = StaticMethods.getIntersectIds(sortOrderNames, columns);
         if(retVal.length < sortOrderNames.length){


           throw new Exception(getAlias() +
               ": Some of the configured labels were found in the input table. " +
               "\nPlease reconfigure this module.");
         }

         return retVal;
       }//getSortOrder


//vered: added this property, to remember the cascading order of the sort:
       private String[] sortOrderNames;
       public void setSortOrderNames(Object[] names){
         //becasue names holds also default labels as "None"
         //these needed to be removed.
         //counting first how many non-"None" labels are there

         int relevant = 0;
         for (int i=0; i<names.length; i++)
           if(!((String)names[i]).equalsIgnoreCase("none"))
             relevant++;

       sortOrderNames = new String[relevant];
       //i is counter for sortOrderNames
       //j is counter for names
       for (int i=0, j=0; i<relevant; j++){
         String current = (String) names[j];
         if(!current.equalsIgnoreCase("none")){
           sortOrderNames[i] = current;
           i++;
         }//if
       }//for i,j
     }//setSortOrderNames

       public Object[] getSortOrderNames(){return sortOrderNames;}


  private int numberOfSorts = 5;


  public int getNumberOfSorts() {
    return numberOfSorts;
  }


  public void setNumberOfSorts(int sort) {
    numberOfSorts = sort;
  }


  boolean reorderColumns = false;


  public boolean getReorderColumns() {
    return reorderColumns;
  }


  public void setReorderColumns(boolean order) {
    reorderColumns = order;
  }



  private static final String NONE = "None";


  /**
   SortTableView
   */
  private class SortTableView extends ncsa.d2k.userviews.swing.JUserPane {


    MutableTable table;
    int columns;
    int rows;
    int numsort;
    int[] sortorder;
    int sortsize;
    int[] runs = null;
    boolean first = true;


    private SortTableView parent = this;


    //QuickQueue queue, lastqueue;


    JLabel[] sortlabels;
    JComboBox[] sortchoices;


    JButton done, abort;


    public void initView(ViewModule viewmod) {
      //module = (SortTable) viewmod;
    }


    public void setInput(Object object, int inputindex) {
      table = (MutableTable) object;
      columns = table.getNumColumns();
      rows = table.getNumRows();
      numsort = getNumberOfSorts();


      if (numsort > columns)
        numsort = columns;


      sortlabels = new JLabel[numsort];
      for(int index=0; index < numsort; index++) {
        JLabel label = new JLabel((index+1) + ". Sort by: ");
        sortlabels[index] = label;
      }


      String[] columnlabels = new String[columns+1];
      columnlabels[0] = NONE;
      for(int index=0; index < columns; index++) {
        String columnlabel = table.getColumnLabel(index);


        if (columnlabel == null || columnlabel.length() == 0)
           columnlabel = "column " + index;


        columnlabels[index+1] = columnlabel;
      }


      sortchoices = new JComboBox[numsort];
      for(int index=0; index < numsort; index++) {
        JComboBox combobox = new JComboBox(columnlabels);
        sortchoices[index] = combobox;
      }


      buildView();
    }


    public void buildView() {
      removeAll();
      setLayout(new BorderLayout());


      JPanel scrollpanel = new JPanel();
      scrollpanel.setLayout(new GridBagLayout());


      for(int index=0; index < numsort; index++) {
        Constrain.setConstraints(scrollpanel, sortlabels[index], 0, index, 1, 1, GridBagConstraints.NONE, GridBagConstraints.WEST, 0, 1);
        Constrain.setConstraints(scrollpanel, sortchoices[index], 1, index, 1, 1, GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST, 1, 0);
      }


      JScrollPane scrollpane = new JScrollPane(scrollpanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
      add(scrollpane, BorderLayout.CENTER);


      JPanel buttonpanel = new JPanel();
      abort = new JButton("Abort");
      done = new JButton("Done");


      abort.addActionListener(new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
          viewAbort();
        }
      });


      done.addActionListener(new AbstractAction() {
        public void actionPerformed(ActionEvent e) {


          getSortOrder();


          // check to make sure the user hasn't chosen the same column twice
          HashMap sortMap = new HashMap(); Integer I;
          for (int i = 0; i < sortorder.length; i++) {
             I = new Integer(sortorder[i]);
             if (sortMap.containsKey(I)) {


                JOptionPane.showMessageDialog(parent,
                   "You cannot sort on the same column twice.",
                   "Error", JOptionPane.ERROR_MESSAGE);


                return;
             }
             else {
                sortMap.put(I, I);
             }
          }
		  sort();
          viewDone("Done");
        }
      });


      buttonpanel.add(abort);
      buttonpanel.add(done);
      add(buttonpanel, BorderLayout.SOUTH);
    }


    public Dimension getPreferredSize() {
      return new Dimension(300, 200);
    }


    public void getSortOrder() {
      ArrayList sortList = new ArrayList();

      //vered: headless support
      ArrayList sortListNames = new ArrayList();

      for(int i = 0; i < sortchoices.length; i++) {
        int idx = sortchoices[i].getSelectedIndex();

        //vered - headless support
        String selectedLabel = (String) sortchoices[i].getSelectedItem();
        sortListNames.add(selectedLabel);


        if(idx != 0)
          sortList.add(new Integer(idx-1));
        // else
        //   break;
      }
      sortorder = new int[sortList.size()];
      for(int i = 0; i < sortList.size(); i++)
        sortorder[i] = ((Integer)sortList.get(i)).intValue();
      numsort = sortorder.length;

      //vered - headless support
      setSortOrderNames(sortListNames.toArray());
    }


    /**
     Find the indices where a run ends in a column.  A run is a collection of
     similar values in a column.  The column is assumed to be sorted.
     Returns an array whose values are the indices of the
     rows that end a run.
     @param col the column of interest
     @return
     */
  /* private int[] findRuns(int col) {
      if (first) {
        //System.out.println("FindRuns: "+table.getColumnLabel(col));
        ArrayList runList = new ArrayList();
        if(table.isColumnScalar(col)) {
          double currentVal = table.getDouble(0, col);
          for(int i = 1; i < table.getNumRows(); i++) {
            double rowVal = table.getDouble(i, col);
            if(rowVal != currentVal) {
              runList.add(new Integer(i-1));
              currentVal = rowVal;
            }
          }
        }
        else {
          String currentVal = table.getString(0, col);
          for(int i = 1; i < table.getNumRows(); i++) {
            String rowVal = table.getString(i, col);
            if(!rowVal.equals(currentVal)) {
              runList.add(new Integer(i-1));
              currentVal = rowVal;
            }
          }
        }
        runList.add(new Integer(table.getNumRows()-1));
        int[] retVal = new int[runList.size()];
        for(int i = 0; i < retVal.length; i++) {
          retVal[i] = ((Integer)runList.get(i)).intValue();
          //System.out.println("Run["+i+"]: "+retVal[i]);
        }
        return retVal;
      } else {
        //Sort through the runs
        ArrayList runList = new ArrayList();
        if(table.isColumnScalar(col)) {
          double currentVal = table.getDouble(0, col);
          for (int i = 0; i <= runs[0]; i++) {
            double rowVal = table.getDouble(i, col);
            if (rowVal != currentVal) {
              runList.add(new Integer(i-1));
              currentVal = rowVal;
            }
          }
          for (int j = 1; j < runs.length; j++) {
            runList.add(new Integer(runs[j-1]));
            for (int i = runs[j-1] + 1; i <= runs[j]; i++) {
              double rowVal = table.getDouble(i, col);
              if (rowVal != currentVal) {
                runList.add(new Integer(i-1));
                currentVal = rowVal;
              }
            }
          }
        } else {
          String currentVal = table.getString(0, col);
          for (int i = 0; i <= runs[0]; i++) {
            String rowVal = table.getString(i, col);
            if (!rowVal.equals(currentVal)) {
              runList.add(new Integer(i-1));
              currentVal = rowVal;
            }
          }
          for (int j = 1; j < runs.length; j++) {
            runList.add(new Integer(runs[j-1]));
            for (int i = runs[j-1] + 1; i <= runs[j]; i++) {
              String rowVal = table.getString(i, col);
              if (!rowVal.equals(currentVal)) {
                runList.add(new Integer(i-1));
                currentVal = rowVal;
              }
            }
          }
        }
        runList.add(new Integer(table.getNumRows()-1));
        int[] retVal = new int[runList.size()];
        for(int i = 0; i < retVal.length; i++) {
          retVal[i] = ((Integer)runList.get(i)).intValue();
          //System.out.println("Run["+i+"]: "+retVal[i]);
        }
        return retVal;
      }
    } */


//ANCA: Took CascadeSort outside the module to make it reusable.
    /**
     * Sort the table for each column selected
     */
   private void sort() {

     CascadeSort cSort = new CascadeSort(table);
     cSort.sort(sortorder);

         if(getReorderColumns())
                         reorder();

          // Reset global variables
         runs = null;
         sortorder = null;
         first = true;
         pushOutput(table, 0);

    }

    /*
   private void sort() {
      if(sortorder.length > 0) {
        table.sortByColumn(sortorder[0]);
        for(int i = 1; i < sortorder.length; i++) {
          // Now find the runs in the (i-1)th column and do a
          // table.sortByColumn(col, begin, end) for each run
          runs = findRuns(sortorder[i-1]);
          first = false;


          // Do the first sort outside the loop
          table.sortByColumn(sortorder[i], 0, runs[0]);
          for(int j = 1; j < runs.length; j++) {
            // Now sort from the end of the last run to the end
            // of the current run
            table.sortByColumn(sortorder[i], runs[j-1]+1, runs[j]);
          }
        }
      }
      if(getReorderColumns())
        reorder();
      // Reset global variables
      runs = null;
      sortorder = null;
      first = true;
      pushOutput(table, 0);
    } */


//      Reorder columns
         public void reorder() {


                int[] indirection = new int[table.getNumColumns()];


                for (int i = 0; i < indirection.length; i++)
                   indirection[i] = i;


                for(int i = 0; i < sortorder.length; i++) {


                   table.swapColumns(i, indirection[sortorder[i]]);


                   int tmp = indirection[i];
                   indirection[i] = indirection[sortorder[i]];
                   indirection[sortorder[i]] = tmp;


                }


    }


  }


  public void initialize(int[] array) {
    for (int index=0; index < array.length; index++)
      array[index] = index;
  }
}


/**
 * QA comments:
 * 3-4-03 Vered started qa process.
 *        changed module info.
 * 3-6-03 added to module info a note about missing vlaue handling
 *
 * 11-04-03 Vered started QA process
 *          properties editor allows insertion of nominal values for property -
 *          Number of attributes to user for sorting. [bug 116] fixed in toolkit level
 * 12-05-03 missing values are sorted as regular values. the bug is actually in
 *          the Column's method sort(). [bug 158] fixed
 * 12-11-03 bug 164 is also fixed
 *          module is ready for basic.
 */
