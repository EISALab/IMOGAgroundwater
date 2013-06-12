package ncsa.d2k.modules.core.optimize.ga.emo.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import ncsa.d2k.gui.*;
import java.io.*;

import ncsa.d2k.modules.core.vis.widgets.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.io.file.output.*;

/**
 * Show a table in a frame, similar to the TableViewer module.
 */
public class TableFrame extends JD2KFrame {
  private Table table; 
  
  public TableFrame(String title, Table table) {
    super(title);
    this.table = table;
    TableMatrix tm = new TableMatrix(table);
    getContentPane().add(tm);
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    
    JMenu fileMenu = new JMenu("File");
    JMenuItem menuItem = new JMenuItem("Save...");
    menuItem.addActionListener(new RunnableAction() {
      public void run() {
        writeTable();
      }
    });
    
    fileMenu.add(menuItem);
    
    JMenuBar menuBar = new JMenuBar();
    menuBar.add(fileMenu);
    
    this.setJMenuBar(menuBar);
  }
  
  private void writeTable() {
    JFileChooser chooser = new JFileChooser();
    String delimiter = ",";
    String newLine = "\n";
    String fileName;
    chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
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