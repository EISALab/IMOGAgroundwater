package ncsa.d2k.modules.core.transform.attribute;

import java.util.HashMap;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.d2k.core.modules.HeadlessUIModule;
import ncsa.d2k.core.modules.UserView;
import ncsa.d2k.core.modules.ViewModule;
import ncsa.d2k.modules.core.datatype.table.ColumnTypes;
import ncsa.d2k.modules.core.datatype.table.MutableTable;
import ncsa.d2k.modules.core.datatype.table.Transformation;
import ncsa.d2k.modules.core.datatype.table.basic.Column;
import ncsa.d2k.modules.core.datatype.table.basic.ColumnUtilities;
import ncsa.d2k.modules.core.datatype.table.basic.DoubleColumn;
import ncsa.d2k.userviews.swing.JUserPane;

import ncsa.d2k.modules.core.transform.StaticMethods;

/**
 * This module presents a user interface for interactive normalization of
 * <code>MutableTable</code> data. Selected numeric columns are normalized
 * (<i>standardized</i>) to a set of values such that the mean of that set
 * is approximately zero and the standard deviation of that set is
 * approximately one.
 * <p>
 * All transformed columns are converted to type <code>double</code>.
 *
 * @author gpape
 */
public class Normalize extends HeadlessUIModule {

/******************************************************************************/
/* UIModule methods                                                           */
/******************************************************************************/

   public UserView createUserView() {
      return new NormalizeUI();
   }

   public String[] getFieldNameMapping() { return null; }

   public String getModuleInfo() {
      StringBuffer sb = new StringBuffer("<p>Overview: ");
      sb.append("This module presents a simple user interface for the ");
      sb.append("interactive normalization (<i>standardization</i>) of ");
      sb.append("numeric <i>MutableTable</i> data. Numeric columns selected ");
      sb.append("by the user are normalized to a set of values such that the ");
      sb.append("mean of that set is approximately zero and the standard ");
      sb.append("deviation of that set is approximately one.</P>");

  /*    sb.append("<P><U>Missing Values Handling:</U> This module handles missing values" +
        " as if they were real meaningful values. For example, if a missing values is represented " +
        "by zero, then after applying the transformation this zero will be changed according " +
        "to the normalizing definition.</P>");
*/
  sb.append("</p><p>Missing Values Handling: Missing values are preserved by " +
              "the output Transformation of this module. Missing values are ignored " +
              "and not being considered during normalization. ");



      sb.append("</p><p>Data Handling: ");
      sb.append("This module does not modify its input data. Rather, its ");
      sb.append("output is a <i>Transformation</i> that can later be used to ");
      sb.append("normalize the specified columns. All transformed columns ");
      sb.append("will be converted to type <i>double</i>.");
      sb.append("</p>");
      return sb.toString();
   }

   public String getModuleName() {
      return "Normalize Values";
   }

   public String getInputInfo(int index) {
      if (index == 0)
         return "A <i>MutableTable</i> with columns to be normalized.";
      return null;
   }

   public String getInputName(int index) {
      if (index == 0)
         return "Mutable Table";
      return null;
   }

   public String[] getInputTypes() {
      return new String[] {
         "ncsa.d2k.modules.core.datatype.table.MutableTable"
      };
   }

   public String getOutputInfo(int index) {
      if (index == 0)
         return "A normalizing transformation for the specified columns.";
      return null;
   }

   public String getOutputName(int index) {
      if (index == 0)
         return "Transformation";
      return null;
   }

   public String[] getOutputTypes() {
      return new String[] {
         "ncsa.d2k.modules.core.datatype.table.Transformation"
      };
   }

//this method is already implemented by super class.
  /*  public PropertyDescription[] getPropertiesDescriptions() {
	return new PropertyDescription[0]; // so that "last expression" property
	// is invisible
    }*/



/******************************************************************************/
/* GUI                                                                        */
/******************************************************************************/

   private class NormalizeUI extends JUserPane implements ActionListener {

      private MutableTable table;

      private JList numericList; // list of available numeric columns
      private JButton abortButton, doneButton;

      private int[] indirection; // points into table, at numeric columns
      private String[] numericLabels;

      private Insets emptyInsets  = new Insets( 0,  0,  0,  0),
                     labelInsets  = new Insets(10, 10, 10, 10),
                     buttonInsets = new Insets( 5,  5,  5,  5);

      public void initView(ViewModule mod) { }

      public void setInput(Object obj, int ind) {

         if (ind != 0)
            return;

         table = (MutableTable)obj;
         removeAll();

         // how many numeric columns are there?

         int numNumericColumns = 0, columnType;
         for (int i = 0; i < table.getNumColumns(); i++) {

            columnType = table.getColumnType(i);

            if (columnType == ColumnTypes.BYTE    ||
                columnType == ColumnTypes.DOUBLE  ||
                columnType == ColumnTypes.FLOAT   ||
                columnType == ColumnTypes.INTEGER ||
                columnType == ColumnTypes.LONG    ||
                columnType == ColumnTypes.SHORT) {

               numNumericColumns++;

            }

         }

         // construct list of available numeric columns

         indirection = new int[numNumericColumns];
         numericLabels = new String[numNumericColumns];

         int index = 0;
         for (int i = 0; i < table.getNumColumns(); i++) {

            columnType = table.getColumnType(i);

            if (columnType == ColumnTypes.BYTE    ||
                columnType == ColumnTypes.DOUBLE  ||
                columnType == ColumnTypes.FLOAT   ||
                columnType == ColumnTypes.INTEGER ||
                columnType == ColumnTypes.LONG    ||
                columnType == ColumnTypes.SHORT) {

               indirection[index] = i;

               numericLabels[index] = table.getColumnLabel(i);

               if (numericLabels[index] == null ||
                   numericLabels[index].length() == 0) {

                  numericLabels[index] = "column " + i;

               }

               index++;

            }

         }

         numericList = new JList(numericLabels);

         // set up button panel

         abortButton = new JButton("Abort");
         abortButton.addActionListener(this);
         doneButton = new JButton("Done");
         doneButton.addActionListener(this);
         JLabel buttonFillerLabel = new JLabel(" ");

         JPanel buttonPanel = new JPanel();
         GridBagLayout buttonLayout = new GridBagLayout();
         buttonPanel.setLayout(buttonLayout);

         buttonLayout.addLayoutComponent(abortButton,
            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.WEST, GridBagConstraints.NONE,
            buttonInsets, 0, 0));
         buttonPanel.add(abortButton);

         buttonLayout.addLayoutComponent(buttonFillerLabel,
            new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
            emptyInsets, 0, 0));
         buttonPanel.add(buttonFillerLabel);

         buttonLayout.addLayoutComponent(doneButton,
            new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.EAST, GridBagConstraints.NONE,
            buttonInsets, 0, 0));
         buttonPanel.add(doneButton);

         // if no numeric columns, attach a message to that effect. otherwise,
         // attach the JList.

         GridBagLayout layout = new GridBagLayout();
         setLayout(layout);

         if (numNumericColumns == 0) {

            JLabel noNumericLabel = new JLabel(
               "This table does not have any numeric columns to normalize.");

            layout.addLayoutComponent(noNumericLabel,
               new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
               GridBagConstraints.CENTER, GridBagConstraints.BOTH,
               labelInsets, 0, 0));
            add(noNumericLabel);

         }
         else {

            JScrollPane numericScroll = new JScrollPane(numericList);

            layout.addLayoutComponent(numericScroll,
               new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
               GridBagConstraints.CENTER, GridBagConstraints.BOTH,
               emptyInsets, 0, 0));
            add(numericScroll);

         }

         layout.addLayoutComponent(buttonPanel,
            new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0,
            GridBagConstraints.SOUTH, GridBagConstraints.HORIZONTAL,
            emptyInsets, 0, 0));
         add(buttonPanel);

      }

      public void actionPerformed(ActionEvent event) {

         Object src = event.getSource();

         if (src == abortButton) {
            viewCancel();
         }

         else if (src == doneButton) {
           //headless conversion support
              setNumericLabels(numericList.getSelectedValues());
              //headless conversion support


            if (indirection.length == 0) {
               pushOutput(new NormalizingTransformation(new int[0]), 0);
            }
            else {

               int[] indices = numericList.getSelectedIndices();
               int[] transform = new int[indices.length];

               for (int i = 0; i < indices.length; i++) {
                  transform[i] = indirection[indices[i]];
               }



               pushOutput(new NormalizingTransformation(transform), 0);

            }

            viewDone("Done");

         }

      }

   }

/******************************************************************************/
/* Transformation                                                             */
/******************************************************************************/

   private class NormalizingTransformation implements Transformation {

      private int[] indices; // numeric column indices in the table

      NormalizingTransformation(int[] indices) {
         this.indices = indices;
      }

      public boolean transform(MutableTable table) {

         if (indices.length == 0 || table.getNumRows() == 0) {
            // no transformation is necessary
            return true;
         }

         // loop over all relevant numeric column indices in the table
         for (int count = 0; count < indices.length; count++) {

            double[] data = new double[table.getNumRows()];
			boolean [] missing = new boolean[data.length];

            int index = indices[count];

            // data first represents the data from the table:
            for (int j = 0; j < data.length; j++) {
               data[j] = table.getDouble(j, index);
               missing[j] = table.isValueMissing(j, index);
            }

            // calculate mean
            double mean = 0;
            int totalNotMissing = 0;
            for (int j = 0; j < data.length; j++)
               if (!missing[j]) {
                  totalNotMissing++;
               	  mean += data[j];
               }

            mean /= totalNotMissing;

            //QA output - vered.
            //System.out.println("the mean of this datast is: " + mean);

            // data now represents differences from the mean:
            for (int j = 0; j < data.length; j++)
               if (!missing[j])
                  data[j] = data[j] - mean;

            // calculate sum of squares of differences
            double sq_diff_sum = 0;
            for (int j = 0; j < data.length; j++)
               if (!missing[j])
                  sq_diff_sum += (data[j] * data[j]);

            // calculate sample variance
            double sample_variance = 0;

            if (totalNotMissing == 1)
               sample_variance = sq_diff_sum;
            else
               sample_variance = sq_diff_sum / (totalNotMissing - 1);

            // calculate sample standard deviation
            double sample_std_dev = Math.sqrt(sample_variance);

//QA output - vered.
            //System.out.println("the standard deviation of this datast is: " + sample_std_dev);

            // divide to normalize data:
            if (sample_std_dev == 0.0) {
               for (int j = 0; j < data.length; j++)
               	  if (!missing[j])
                     data[j] = 0;
            }
            else {
               for (int j = 0; j < data.length; j++)
			      if (!missing[j])
			   	     data[j] = data[j] / sample_std_dev;
            }

            Column newColumn = ColumnUtilities.toDoubleColumn(table.getColumn(index));
            table.setColumn(newColumn, index);
            for (int entry = 0 ; entry < data.length ;entry++) {
                if (table.isValueMissing (entry, index))
                    table.setDouble(table.getMissingDouble(), entry, index);
                else
                    table.setDouble(data[entry], entry, index);
            }
         }

         return true;

      }

   }


//headless conversion support

   private String[] numericLabels;
   public Object[] getNumericLabels(){return numericLabels;}
   public void setNumericLabels(Object[] labels){
     numericLabels = new String[labels.length];

     for (int i=0; i<labels.length; i++){
       numericLabels[i] = (String) labels[i];

     }
   }

   public void doit() throws Exception{
     MutableTable _table = (MutableTable)pullInput(0);

     int[] transform = new int[0]; //with this array the normalization trasform will be build

     if(numericLabels == null)
       throw new Exception (this.getAlias()+" has not been configured. Before running headless, run with the gui and configure the parameters.");


          HashMap availableNumericColumns = new HashMap();
          for (int i=0; i<_table.getNumColumns(); i++)
            if(_table.isColumnNumeric(i))
              availableNumericColumns.put(_table.getColumnLabel(i).toUpperCase(), new Integer(i));

           if(availableNumericColumns.size() == 0){
             System.out.println(getAlias() + ": Warning - Table " + _table.getLabel() +
                                " has no numeric columns. The transformation will be " +
                                "an empty one");
              //pushOutput(new NormalizingTransformation(transform), 0);
              //return;
           }


     if( numericLabels.length ==0){
       System.out.println(getAlias() + ": no numeric columns were selected. " +
                          "the transformation will be an empty one.\n");
        pushOutput(new NormalizingTransformation(transform), 0);
       return;
     }


     //finding out how many columns are in the intersection between
     //numericLabels and the available numeric columns in the table




        transform = StaticMethods.getIntersectIds(numericLabels, availableNumericColumns);
/*        int numNumeric = 0;
        for (int i = 0; i < numericLabels.length; i++)
          if (availableNumericColumns.containsKey(numericLabels[i]))
            numNumeric++;
*/

        if(transform.length < numericLabels.length){
          String str, label;
          label = _table.getLabel();
          if( label == null || label.length() == 0)
            str = "The input table";
          else str = "Table " + label;
          throw new Exception(getAlias() + ": " + str +
                              " does not contain all of the configured numeric columns." +
                              " Please reconfigure this module via a GUI run so it can run Headless.");
        //pushOutput(new NormalizingTransformation(transform), 0);
        //return;

        }

        /*
         transform = new int[numNumeric];
        for (int i = 0; i < numericLabels.length; i++)
          if (availableNumericColumns.containsKey(numericLabels[i]))
            transform[i] = ( (Integer) availableNumericColumns.get(numericLabels[
                i])).intValue();
*/
        pushOutput(new NormalizingTransformation(transform), 0);


   }
//headless conversion support



}//Normalize


/**
 * QA comments:
 * 2-28-03  Vered started qa.
 *          added to module info a note about missing values handling.
 * 3-24-03 Anca: added getPropertyDescription()
 *
 * 11-04-03 Vered started QA process.
 *          Module is ready unless handling of missing values will change.
 * 11-25-03 due to new guide lines regarding missing values - this module needs to go
 *          under some changes. missing values should be preserved. [bug 146]
 * 12-04-03 bug 146 was fixed.
 * 12-04-03 module is ready for basic 4
 *
 * 01-12-04: module is pulled back into qa process.
 * a subset table that had a normalizing transformation applied to cannot be veiwed
 * by a table viewer. the table veiwer throws and array index out of bound exception
 * [bug 218] (fixed)
 *
 * 01-21-04: module is ready for basic 4
 *
 */
