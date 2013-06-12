package ncsa.d2k.modules.core.io.file.input;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.beans.PropertyVetoException;

import javax.swing.*;

import ncsa.d2k.core.modules.*;
import ncsa.gui.*;

/**
 * InputsFileNames allows the user to input the names of two file.  The file
 * names are input in the properties editor.
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */
public class Input2FileNames extends InputModule {

    public String[] getInputTypes() {
        return null;
    }

    public String[] getOutputTypes() {
        String[] out = {"java.lang.String", "java.lang.String"};
        return out;
    }

    public String getInputInfo(int i) {
        return "";
    }

    public String getOutputInfo(int i) {
        if(i == 0)
            return  "The name of the first file, possibly including the path.";
        else
            return "The name of the second file, possibly including the path.";
    }

    public String getOutputName(int i) {
        if(i == 0)
            return "File Name 1";
        else
            return "File Name 2";
    }
   /** Return the name of this module.
     *  @return The display name for this module.
     */
    public String getModuleName() {
        return "Input 2 File Names";
    }

    public String getModuleInfo() {
        String s = "<p>Overview: ";
        s += "This module is used to enter the names of two files. ";
        s += "</p><p>Detailed Description: ";
        s += "The module provides a properties editor that can be used to ";
        s += "enter two file names.  The user can enter the names directly ";
        s += "into text areas or click 'Browse' buttons to navigate ";
        s += "the local file system. ";
        s += "</p><p>";
        s += "This module does not perform any checks to verify that ";
        s += "the named files exist and are accessible by the user.  Such ";
        s += "checking does not make sense as the module has no insight into ";
        s += "how the file names will be used - for example, to read ";
        s += "existing files or to create new ones. A check is performed to ";
        s += "make sure that file names have been entered and an exception is ";
        s += "thrown if either editor text area is blank. ";
        s += "</p><p>";
        s += "The file names are made available on the <i>File Name 1</i> ";
        s += "and <i>File Name 2</i> output ports. ";
        s += "A path may or may not be included in each file name ";
        s += "string.  The final forms shown in the properties editor ";
        s += "text boxes are sent to the output ports. ";
        s += "Typically when the Browser is used, the absolute path is ";
        s += "included.";

        return s;
    }

    /** Return an array with information on the properties the user may update.
     *  @return The PropertyDescriptions for properties the user may update.
     */
    public PropertyDescription[] getPropertiesDescriptions() {
        PropertyDescription[] pds = new PropertyDescription [2];

        pds[0] = new PropertyDescription( "fileName0",
                 "File Name 1",
                 "The name of the first file, possibly including the path." );
        pds[1] = new PropertyDescription( "fileName1",
                 "File Name 2",
                 "The name of the second file, possibly including the path.");
        return pds;
    }

    /** file name0 property */
    private String fileName0;

    public void setFileName0(String s) throws PropertyVetoException {

     /**
      ** Remove checks as too annoying... would like to add them back if
      ** module info eventually available w/o triggering this so just
      ** commenting out this section for now.
      **
        // here we check for length of 0 but not for null as we don't want this
        // to get thrown if an itinerary is saved/reloaded without the
        // property dialog being used
        if ( s != null && s.length() == 0) {
 	    throw new PropertyVetoException(
                "File Name 1 must be entered before the dialog can be closed.",
                 null);
        }
      **
      ** End of commented out section.
      **/

        fileName0 =  s;;
    }

    public String getFileName0() {
        return fileName0;
    }


    /** file name1 property */
    private String fileName1;

    public void setFileName1(String s) throws PropertyVetoException {

     /**
      ** Remove checks as too annoying... would like to add them back if
      ** module info eventually available w/o triggering this so just
      ** commenting out this section for now.
      **
        // here we check for length of 0 but not for null as we don't want this
        // to get thrown if an itinerary is saved/reloaded without the
        // property dialog being used
        if ( s != null && s.length() == 0) {
 	    throw new PropertyVetoException(
                "File Name 2 must be entered before the dialog can be closed.",
                 null);
        }
      **
      ** End of commented out section.
      **/

        fileName1 =  s;

    }

    public String getFileName1() {
        return fileName1;
    }

    /**
     * Return a custom property editor.
     * @return
     */
    public CustomModuleEditor getPropertyEditor() {
        return new PropEdit();
    }

    private class PropEdit extends JPanel implements CustomModuleEditor {
        private JTextField fn0;
        private JTextField fn1;

        PropEdit() {
            setLayout(new GridBagLayout());
            fn0 = new JTextField(20);
            fn0.setText(getFileName0());
            fn1 = new JTextField(20);
            fn1.setText(getFileName1());

            // first file name
            JButton b0 = new JButton("Browse");
			JButton b1 = new JButton("Browse");
 
			Constrain.setConstraints(this, new JLabel ("File Name 1"), 0, 0, 1, 1,
									  GridBagConstraints.NONE,
									  GridBagConstraints.CENTER, 0, 0);
			Constrain.setConstraints(this, fn0, 1, 0, 1, 1,
									  GridBagConstraints.HORIZONTAL,
									  GridBagConstraints.CENTER, 1, 0);
			Constrain.setConstraints(this, b0, 2, 0, 1, 1,
									  GridBagConstraints.NONE,
									  GridBagConstraints.CENTER, 0, 0);
									  
			Constrain.setConstraints(this, new JLabel ("File Name 2"), 0, 1, 1, 1,
									  GridBagConstraints.NONE,
									  GridBagConstraints.CENTER, 0, 0);
			Constrain.setConstraints(this, fn1, 1, 1, 1, 1,
									  GridBagConstraints.HORIZONTAL,
									  GridBagConstraints.CENTER, 1, 0);
			Constrain.setConstraints(this, b1, 2, 1, 1, 1,
									  GridBagConstraints.NONE,
									  GridBagConstraints.CENTER, 0, 0);
			
           b0.addActionListener(new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    JFileChooser chooser = new JFileChooser();

                    String d = getFileName0();
                    if(d == null)
                      d = fn0.getText();
                    if(d != null) {
                      File thefile = new File(d);
                      if(thefile.isDirectory())
                        chooser.setCurrentDirectory(thefile);
                      else
                        chooser.setSelectedFile(thefile);
                    }

                    // set the title of the FileDialog
                    StringBuffer sb = new StringBuffer("Select File 1");
                    chooser.setDialogTitle(sb.toString());

                    // get the file
                    String fn;
                    int retVal = chooser.showOpenDialog(null);
                    if(retVal == JFileChooser.APPROVE_OPTION)
                        fn = chooser.getSelectedFile().getAbsolutePath();
                    else
                        return;

                    if(fn != null) {
                        // display the path to the chosen file
                        //jtf.setText(fileName);
                        //setFileName0(fn);
                        fn0.setText(fn);
                    }
                }
            });

            b1.addActionListener(new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    JFileChooser chooser = new JFileChooser();

                    String d = getFileName1();
                    if(d == null)
                      d = fn1.getText();
                    if(d != null) {
                      File thefile = new File(d);
                      if(thefile.isDirectory())
                        chooser.setCurrentDirectory(thefile);
                      else
                        chooser.setSelectedFile(thefile);
                    }

                    // set the title of the FileDialog
                    StringBuffer sb = new StringBuffer("Select File 2");
                    chooser.setDialogTitle(sb.toString());

                    // get the file
                    String fn;
                    int retVal = chooser.showOpenDialog(null);
                    if(retVal == JFileChooser.APPROVE_OPTION)
                        fn = chooser.getSelectedFile().getAbsolutePath();
                    else
                        return;

                    if(fn != null) {
                        // display the path to the chosen file
                        //jtf.setText(fileName);
                        //setFileName1(fn);
                        fn1.setText(fn);
                    }
                }
            });
        }

        public boolean updateModule() throws Exception {
            boolean didChange = false;
            String f0 = fn0.getText();
            if(f0 != getFileName0()) {
                setFileName0(f0);
                didChange = true;
            }
            String f1 = fn1.getText();
            if(f1 != getFileName1()) {
                setFileName1(f1);
                didChange = true;
            }
            return didChange;
        }
    }

    public void doit() throws Exception {
        String fn0 = getFileName0();
        String fn1 = getFileName1();
        if(fn0 == null || fn0.length() == 0)
            throw new Exception(getAlias()+": File name 1 was not given.");

        if(fn1 == null || fn1.length() == 0)
            throw new Exception(getAlias()+": File name 2 was not given.");
        setFileName0(fn0);
        setFileName1(fn1);
        pushOutput(fn0, 0);
        pushOutput(fn1, 1);
    }
}
// QA Comments
// 2/12/03 - Handed off to QA by David Clutter - replaces Get2FileNames
//           using property editor instead of UI.
// 2/12/03 - Ruth started QA process.  Updated documentation; added a couple
//           methods to help users see information;  added except handler
//           in property setter method
// 2/12/03 - emailed david c to see if text box could be variable size
// 2/13/03 - text box std as is;  david c pointed out error in except handler
//           when module saved/reloaded w/o setting property name.
//           ruth fixed error (with help) and committed;  still throw exception
//           if property dialog/info scanned w/o entering filename. not best
//           but seems no option as setter called when dialog closed.
// 2/14/03 - checked into basic.
// 5/16/03 - don't throw exception if no filename entered in prop dialog - instead
//           only at runtime. need better way to check if only info scanned or
//           if edit done. for now, none, and annoying, so removed check added 2/13.
// 9/19/03 - For 4.0 it is now possible to make text box size variable, and I have
//		     done that.
// END QA Comments
