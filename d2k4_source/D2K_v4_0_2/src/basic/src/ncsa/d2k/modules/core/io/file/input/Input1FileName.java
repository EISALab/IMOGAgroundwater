package ncsa.d2k.modules.core.io.file.input;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.beans.PropertyVetoException;

import javax.swing.*;

import ncsa.d2k.core.modules.*;
import ncsa.gui.*;

/**
 * InputFileName allows the user to input the name of a single file.  The file
 * name is input in the properties editor.
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */
public class Input1FileName extends InputModule {

    public String[] getInputTypes() {
        return null;
    }

    public String[] getOutputTypes() {
        String[] out = {"java.lang.String"};
        return out;
    }

    public String getInputInfo(int i) {
        return "";
    }

    public String getOutputInfo(int i) {
        return "The name of the file, possibly including the path.";
    }

    public String getOutputName(int i) {
        return "File Name";
    }

    /** Return the name of this module.
     *  @return The display name for this module.
     */
    public String getModuleName() {
        return "Input File Name";
    }

    public String getModuleInfo() {
        String s = "<p>Overview: ";
        s += "This module is used to enter the name of a file. ";
        s += "</p><p>Detailed Description: ";
        s += "The module provides a properties editor that can be used to ";
        s += "enter a file name.  The user can enter the name directly into ";
        s += "a text area or click a 'Browse' button to navigate ";
        s += "the local file system. ";
        s += "</p><p>";
        s += "This module does not perform any checks to verify that ";
        s += "the named file exists and is accessible by the user.  Such ";
        s += "checking does not make sense as the module has no insight into ";
        s += "how the file name will be used - for example, to read an ";
        s += "existing file or to create a new one. A check is performed to ";
        s += "make sure that a file name has been entered and an exception is ";
        s += "thrown if the editor text area is blank. ";
        s += "</p><p>";
        s += "The file name is made available on the <i>File Name</i> output ";
        s += "port.  A path may or may not be included in the file name ";
        s += "string.  The final form shown in the properties editor ";
        s += "text box is sent to the <i>File Name</i> output port. ";
        s += "Typically when the Browser is used, the absolute path is ";
        s += "included.";

        return s;
    }

    /** Return an array with information on the properties the user may update.
     *  @return The PropertyDescriptions for properties the user may update.
     */
    public PropertyDescription[] getPropertiesDescriptions() {
        PropertyDescription[] pds = new PropertyDescription [1];

        pds[0] = new PropertyDescription( "fileName",
                 "File Name",
                 "The name of a file, possibly including the path." );
        return pds;
    }

    /** The file name property */
    private String fileName;

    public void setFileName(String s) throws PropertyVetoException {
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
		"A file name must be entered before the dialog can be closed.",
		 null);
        }

      **
      ** End of commented out section.
      **/

        fileName =  s;
    }

    public String getFileName() {
        return fileName;
    }

    /**
     * Return a custom property editor.
     * @return
     */
    public CustomModuleEditor getPropertyEditor() {
        return new PropEdit();
    }

    private class PropEdit extends JPanel implements CustomModuleEditor {
        private JTextField jtf;

        private PropEdit() {
            setLayout(new GridBagLayout());
			this.setMinimumSize (new Dimension(14, 14));
           
            String name = getFileName();
            jtf = new JTextField(10);
            jtf.setText(name);
 			JButton b0 = new JButton("Browse");
												  
			Constrain.setConstraints(this, new JLabel ("File Name"), 0, 0, 1, 1,
									  GridBagConstraints.NONE,
									  GridBagConstraints.CENTER, 0, 0);
			Constrain.setConstraints(this, jtf, 1, 0, 1, 1,
									  GridBagConstraints.HORIZONTAL,
									  GridBagConstraints.CENTER, 1, 0);				  
			Constrain.setConstraints(this, b0, 2, 0, 1, 1,
									  GridBagConstraints.NONE,
									  GridBagConstraints.CENTER, 0, 0);
			
			b0.addActionListener(new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    JFileChooser chooser = new JFileChooser();

                    String d = getFileName();
                    if(d == null)
                      d = jtf.getText();
                    if(d != null) {
                      File thefile = new File(d);
                      if(thefile.isDirectory())
                        chooser.setCurrentDirectory(thefile);
                      else
                        chooser.setSelectedFile(thefile);
                    }

                    // set the title of the FileDialog
                    StringBuffer sb = new StringBuffer("Select File");
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
                        jtf.setText(fn);
                    }
                }
            });
        }

        public boolean updateModule() throws Exception {
            String f0 = jtf.getText();
            if(f0 != getFileName()) {
                setFileName(f0);
                return true;
            }
            return false;
        }
    }

    public void doit() throws Exception {
        String fn = getFileName();
        if(fn == null || fn.length() == 0)
            throw new Exception(getAlias()+": No file name was given.");
        setFileName(fn);
        pushOutput(fn, 0);
    }
}

// QA Comments
// 2/12/03 - Handed off to QA by David Clutter - replaces Get1FileName
//           using property editor instead of UI
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
//			 done that.
// END QA Comments
