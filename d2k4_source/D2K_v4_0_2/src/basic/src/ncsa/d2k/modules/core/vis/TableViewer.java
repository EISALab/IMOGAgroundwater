package ncsa.d2k.modules.core.vis;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.core.io.file.output.*;
import ncsa.d2k.modules.core.vis.widgets.*;
import ncsa.d2k.userviews.swing.*;

/**
 * This module displays the contents of a <code>Table</code>.
 *
 * @author David Clutter
 */
public class TableViewer extends UIModule {

////////////////////////////////////////////////////////////////////////////////
// Module methods                                                             //
////////////////////////////////////////////////////////////////////////////////

   public UserView createUserView() {
      return new TableView();
   }

   /**
    * Not used by this module.
    */
   public String[] getFieldNameMapping() {
      return null;
   }

   public String getInputInfo(int i) {
      if (i == 0)
         return "The <i>Table</i> to be displayed.";
      return "No such input";
   }

   public String getInputName(int i) {
      if (i == 0)
         return "Table";
      return "No such input";
   }

   public String[] getInputTypes() {
      return new String[] {
         "ncsa.d2k.modules.core.datatype.table.Table"
      };
   }

   public String getModuleInfo() {
      StringBuffer sb = new StringBuffer("<p>Overview: ");
      sb.append("This module displays the contents of a <i>Table</i>.");

      sb.append("</p><p>Detailed Description: " );
      sb.append("This module creates a display window and shows the contents ");
      sb.append("of the input <i>Table</i>.  Table entries that contain ");
      sb.append("missing values are indicated by a  ?  in the display. ");

      sb.append("</p><p>The window can be resized and has both horizontal ");
      sb.append("and vertical scroll bars to accommodate large ");
      sb.append("table sizes.  An individual column can be made wider by ");
      sb.append("clicking on the column divider in the labels row and moving ");
      sb.append("it to the right while keeping the mouse button pressed. ");
      sb.append("Releasing the button sets the new column size.");

      sb.append("</p><p>");
      sb.append("Should you wish to limit the number of decimal digits displayed ");
      sb.append("in the table view, please see the <i>maximum fraction digits</i> ");
      sb.append("property. The underlying data will not be affected.");

      sb.append("</p><p>" );
      sb.append("The <i>File</i> pull-down menu offers a <i>Save</i> option to ");
      sb.append("save the displayed table to a tab-delimited file. ");
      sb.append("A file browser window pops up, allowing the user to select ");
      sb.append("where the table should be saved. ");
      sb.append("Missing values in the table appear as blanks in the saved file. ");

      sb.append("</p><p>" );
      sb.append("The <i>Done</i> button closes the table viewer window. ");
      sb.append("The <i>Abort</i> button closes the table viewer window and aborts itinerary execution. ");

      sb.append("</p><p>Known Limitations in Current Release: ");
      sb.append("This module was designed to work with a single input table per itinerary run. ");
      sb.append("It will not work properly if it receives multiple inputs per run. ");
      sb.append("If you accidently direct multiple inputs to the module, it may be necessary ");
      sb.append("to resize the Table Viewer Window before the table contents and <i>Abort</i> ");
      sb.append("and <i>Done</i> buttons are visible and/or operational.   Until you resize, it may ");
      sb.append("seem that you have no way to stop the itinerary and correct the problem. ");

      sb.append("</p><p>Data Handling: ");
      sb.append("This module does not modify its input. The <i>Table</i> ");
      sb.append("is passed, unchanged, as the module's output.");
      sb.append("Only the Table data that is presently visible in the window is requested. ");
      sb.append("For some table representations, in particular those where the table data is not all kept in memory, ");
      sb.append("refocusing the window view on different table cells can result in some noticeable ");
      sb.append("delay while the new table data is loaded. ");

      return sb.toString();
   }

   public String getModuleName() {
      return "Table Viewer";
   }

   public String getOutputInfo(int i) {
      if (i == 0)
         return "The <i>Table</i> that was displayed, unmodified.";
      return "No such output.";
   }

   public String getOutputName(int i) {
      if (i == 0)
         return "Table";
      return "No such output";
   }

   public String[] getOutputTypes() {
      return new String[] {
         "ncsa.d2k.modules.core.datatype.table.Table"
      };
   }

////////////////////////////////////////////////////////////////////////////////
// properties                                                                 //
////////////////////////////////////////////////////////////////////////////////

   protected int maxFractionDigits = -1;
   public int getMaxFractionDigits() { return maxFractionDigits; }
   public void setMaxFractionDigits(int value) { maxFractionDigits = value; }

   public PropertyDescription[] getPropertiesDescriptions() {

      PropertyDescription[] pds = new PropertyDescription[1];

      pds[0] = new PropertyDescription("maxFractionDigits",
         "Maximum fraction digits displayed",
         "Specifies the maximum number of digits after the decimal point to be displayed for numeric data (-1 for no restriction). The underlying data is not affected.");

      return pds;

   }

   /*
   public PropertyDescription[] getPropertiesDescriptions() {

      // This module doesn't have any properties that the user should edit.
      // so we return an empty list

      PropertyDescription[] pds = new PropertyDescription[0];
      return pds;

   }
   */

////////////////////////////////////////////////////////////////////////////////
// user view                                                                  //
////////////////////////////////////////////////////////////////////////////////

   /**
    * This class uses a <code>TableMatrix</code> to display the
    * <code>Table</code>.
    */
   public class TableView extends JUserPane implements ActionListener {

      TableMatrix matrix;
      /** the table with data */
      protected Table table = null;
      /** a reference to our parent module */
      protected TableViewer parent;
      /** ok button */
      protected JButton ok;
      /** cancel button */
      protected JButton cancel;

      JMenuBar menuBar;
      JMenuItem print;

      /**
         Initialize the view.  Insert all components into the view.
         @param mod The VerticalTableViewer module that owns us
         */
      public void initView(ViewModule mod) {
         parent = (TableViewer)mod;
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

      /**
         Called whenever inputs arrive to the module.  Save a
         reference to the table and call initializeTable().
         @param input the Object that is the input
         @param idx the index of the input
         */
      public void setInput(Object input, int idx) {
         if(idx == 0) {
            removeAll();
            table = (Table)input;
            // a panel to put the buttons on
            JPanel buttonPanel = new JPanel();
            ok = new JButton("Done");
            ok.addActionListener(this);
            cancel = new JButton("Abort");
            cancel.addActionListener(this);
            buttonPanel.add(cancel);
            buttonPanel.add(ok);

            // create the matrix
            if (maxFractionDigits < 0) {
               matrix = new TableMatrix(table);
            }
            else {
               matrix = new TableMatrix(table, maxFractionDigits);
            }

            // add everything to this
            add(matrix, BorderLayout.CENTER);
            add(buttonPanel, BorderLayout.SOUTH);
         }
         // Make sure window name matches current module alias
        // parent.setWindowName( getAlias() );
      }

      /**
         Perform any clean up to the table and call the finish() method
         on the VerticalTableViewer module.  Since all cells are
         uneditable in this implementation, we simply call the finish()
         method.  A subclass may want to juggle the contents of the table,
         however.
         */
      protected void finishUp() {
         pushOutput(table, 0);
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
         if(src == ok)
            finishUp();
         else if(src == cancel)
            parent.viewCancel();
         else if(src == print)
            printVT();
      }

      private void printVT() {
         JFileChooser chooser = new JFileChooser();
         String delimiter = "\t";
         String newLine = "\n";
         String fileName;
         int retVal = chooser.showSaveDialog(null);
         if(retVal == JFileChooser.APPROVE_OPTION)
            fileName = chooser.getSelectedFile().getAbsolutePath();
         else
            return;
         try {
            WriteTableToFile.writeTable(table, delimiter, fileName, true, true);
         }
         catch(IOException e) {

            // e.printStackTrace();

            JOptionPane.showMessageDialog(this,
               "Unable to write to file " + fileName + ":\n\n" + e.getMessage(),
               "Error writing file", JOptionPane.ERROR_MESSAGE);

         }
      }

   }

}
// Start QA Comments
// 3/6/03 - Recv from Greg;  Ruth starts QA
//        - Expanded module info text; Changed module name from view table
//        - to table viewer; Asked developers about "Save" option format,
//        - undoc properties, and labels types.
// 3/24/03  - Added getPropertiesDescriptions() so that no properties the user
//            can't edit are shown. (RA)
// 3/31/03 - Added docs about the save option.
//      Added docs about how to close the display.
//           Added docs about limitations.
//           OK for basic.
// 7/16/03 - Ruth added call to setWindowName so that pop-up consistently has
//           alias as the window name;  before it didn't always work.
//
// WISH: Want to have column types shown, or at least offer an option to do so.
// WISH: Work (or behave consistently) if multiple input tables are received.  Currently
//       it seems to "mangle together" entries from the multiple tables in an inconsistent
//       manner. And, it must be resized sometimes to get the buttons to respond.
//
// End QA Comments
//
//



// 12-25-03: Vered - started qa process for basic 4 release.
//           no changes were made since last release.
//           module is ready.