package ncsa.d2k.modules.core.io.file.output;

import java.io.*;
import ncsa.d2k.core.modules.*;

/**
OutputSerializedObject.java
Writes a serialized object to a file.
@author David Tcheng
*/
public class OutputSerializedObject extends OutputModule {

    /**
     * Return the common name of this module.
     * @return The display name for this module.
     */
    public String getModuleName() {
        return "Output Serialized Object";
    }

    /**
     * Return information about the module.
     * @return A detailed description of the module.
     */
    public String getModuleInfo() {
      StringBuffer sb = new StringBuffer("<p>Overview: ");
      sb.append("This module writes a serializable Java object to a file.");
      sb.append("</p><p>Detailed Description: In Java, an object that ");
      sb.append("implements the java.io.Serializable interface can be ");
      sb.append("converted to a stream of bytes and written out to a file ");
      sb.append("in a process called <em>serialization</em>. ");
      sb.append("</p><p>This module opens or creates the file indicated by ");
      sb.append("the <i>File Name</i> input port and serializes the Java ");
      sb.append("object from the <i>Java Object</i> input port to ");
      sb.append("that file.");
      sb.append("</p><p>The module will exit with an error if the file ");
      sb.append("cannot be written to, or if the object cannot be serialized.");
      sb.append("</p>");

      return sb.toString();
    }

    /**
     * Return a String array containing datatypes of the inputs to this module
     * @return The datatypes of the module inputs.
     */
    public String[] getInputTypes() {
        String[] types = {"java.lang.Object", "java.lang.String"};
        return types;
    }

    /**
     * Returns a String array containing datatypes of the output to this module
     * @return The datatypes of the module outputs.
     */
    public String[] getOutputTypes() {
        String[] types = {};
        return types;
    }

    /**
     * Return a description of the specified input port.
     * @param i The index of the input.
     * @return The description of the input.
     */
    public String getInputInfo (int i) {
        switch (i) {
            case 0:
              return "The Java object to serialize.";
            case 1:
              return "The name of the file where the serialized object will be written.";
            default:
              return "No such input";
        }
    }

    /**
     * Return the name of a specific input.
     * @param i The index of the input.
     * @return The name of the input.
     */
    public String getInputName(int i) {
        switch(i) {
            case 0:
                return "Java Object";
            case 1:
                return "File Name";
            default:
                return "No such input";
        }
    }

   /**
     * Return the name of a specific output.
     * @param i The index of the output.
     * @return The name of the output.
     */
    public String getOutputInfo(int i) {
        switch (i) {
            default: return "No such output";
        }
    }

    /**
     * Return the name of a specified output.
     * @param i The index of the output.
     * @return The name of the output.
     */
    public String getOutputName(int i) {
        switch(i) {
            default: return "No such output";
        }
    }

    //////////
    // Doit //
    //////////

    public void doit() throws Exception {
        Object object = pullInput(0);
        String FileName = (String)pullInput(1);

        FileOutputStream file = null;
        ObjectOutputStream out = null;

        try {
           file = new FileOutputStream(FileName);
        }
        catch (FileNotFoundException e) {
           throw new FileNotFoundException( "Could not open file: " + FileName +
                                  "\n" + e );
        }
        catch (SecurityException e) {
           throw new SecurityException( "Could not open file: " + FileName +
                                  "\n" + e );
        }

        try {
            out = new ObjectOutputStream(file);
            out.writeObject(object);
        }
        catch (IOException e) {
            throw new IOException( "Unable to serialize object " +
                                   "\n" + e );
        }

        out.flush();
        out.close();
    }
}
// QA Comments
// 2/12/03 - Handed off to QA by David Clutter
// 2/13/03 - Ruth started QA process.  Shortened module common
//           name; Added some JavaDocs; deleted unused code; added more to
//           module description; added more user-friendly exceptions.
// 2/14/03 - checked into basic.  
// 2/25/03 - removed getPropertyDescriptions() that returned null as not needed
//           with latest changed to property display.  updated in basic too.
// END QA Comments
