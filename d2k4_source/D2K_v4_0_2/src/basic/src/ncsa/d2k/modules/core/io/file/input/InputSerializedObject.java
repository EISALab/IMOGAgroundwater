package ncsa.d2k.modules.core.io.file.input;

import java.io.*;
import ncsa.d2k.core.modules.*;

/*
* Reads a serialized object from a file.
* @author David Tcheng
 */
public class InputSerializedObject extends InputModule {

    /**
     * Return the common name of this module.
     * @return The display name for this module.
     */
    public String getModuleName() {
        return "Input Serialized Object";
    }

    /**
     * Return information about the module
     * @return A detailed description of the module.
     */
    public String getModuleInfo () {
      StringBuffer sb = new StringBuffer("<p>Overview: ");
      sb.append("This module reads a Java serialized object from a file.");
      sb.append("</p><p>Detailed Description: In Java, an object can be ");
      sb.append("converted to a stream of bytes and written out to a file ");
      sb.append("in a process called <em>serialization</em>.  ");
      sb.append("</p><p>This module opens the file indicated by the <i>File ");
      sb.append("Name</i> input port and reads the file to reload the Java ");
      sb.append("object that was serialized to the file.  The resulting ");
      sb.append("object is made available on the <i>Java Object</i> output ");
      sb.append("port.");
      sb.append("</p><p>The module will exit with an error if the file cannot ");
      sb.append("be accessed or does not contain a serialized object. </p> ");

      return sb.toString();
    }

    /**
     * Return a String array containing datatypes of the inputs to this module
     * @return The datatypes of the module inputs.
     */
    public String[] getInputTypes () {
        String[] types = {"java.lang.String"};
        return types;
    }

    /**
     * Returns a String array containing datatypes of the output to this module
     * @return The datatypes of the module outputs.
     */
    public String[] getOutputTypes () {
        String[] types = {"java.lang.Object"};
        return types;
    }

    /**
     * Return a description of the specified input port.
     * @param i The index of the input.
     * @return The description of the input.
     */
    public String getInputInfo (int i) {
        switch (i) {
            case 0: return "The filename of the serialized object to be read.";
            default: return "No such input";
        }
    }

    /**
     * Return the name of a specific input.
     * @param i The index of the input.
     * @return The name of the input.
     */
    public String getInputName (int i) {
        switch(i) {
            case 0:
                return "File Name";
            default: return "No such input";
        }
    }

    /**
     * Return the name of a specific output.
     * @param i The index of the output.
     * @return The name of the output.
     */
    public String getOutputInfo (int i) {
        switch (i) {
            case 0: return "The Java object that was read from the file.";
            default: return "No such output";
        }
    }

    /**
     * Return the name of a specified output.
     * @param i The index of the output.
     * @return The name of the output.
     */
    public String getOutputName (int i) {
        switch(i) {
            case 0:
                return "Java Object";
            default: return "No such output";
        }
    }

 //////////
 // Doit //
 //////////
    public void doit () throws Exception {

        String FileName = (String)(pullInput(0));
        ObjectInputStream in = null;

        try {
           in = ModuleUtilities.getObjectInputStream(FileName);
        }
        catch (FileNotFoundException e) {
           throw new FileNotFoundException( "Could not open file: " + FileName +
                                  "\n" + e );
        } catch (SecurityException e) {
           throw new SecurityException( "Could not open file: " + FileName +
                                  "\n" + e );
        }

        Object object = null;
        try {
           object = (Object)in.readObject();
        }
        catch( java.lang.ClassNotFoundException e ) {
           throw new ClassNotFoundException( "Unable to find object class." +
                                             "\n" + e );
        }
        catch (IOException e) {
           throw new IOException( "Unable to deserialize object." +
                                  "\n" + e );
        }

        in.close();
        pushOutput(object, 0);

    }
}
// QA Comments
// 2/12/03 - Handed off to QA by David Clutter
// 2/12/03 - Ruth started QA process.  Renamed output port & module common
//           name; Added some JavaDocs; deleted unused code; added more to
//           module description; added more user-friendly exceptions.
// 2/12/03 - emailed david c & tom re: properties editor when no properties.
//           also icon label -> alias link seems broken
// 2/13/03 - filed bug report on prop ed with 0 prop;  tweaked exception hndlr
// 2/14/03 - checked into basic.
// 2/25/03 - removed getPropertyDescriptions() that returned null as not needed
//           with latest changed to property display.  updated in basic too.
// END QA Comments
