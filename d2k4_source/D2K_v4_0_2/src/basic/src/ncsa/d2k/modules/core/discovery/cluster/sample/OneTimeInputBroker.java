package ncsa.d2k.modules.core.discovery.cluster.sample;

/**
 * <p>Title: </p>
 * <p>Description: OnetimeInputBroker</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: NCSA Automated Learning Group</p>
 * @author D. Searsmith
 * @version 1.0
 */

//==============
// Java Imports
//==============

//===============
// Other Imports
//===============

import ncsa.d2k.core.modules.*;

public class OneTimeInputBroker
    extends DataPrepModule {

  //==============
  // Data Members
  //==============

  private Object m_readOnce = null;

  //==================
  // Option Accessors

  public boolean getVerbose() {
    return m_verbose;
  }

  public void setVerbose(boolean b) {
    m_verbose = b;
  }

  private boolean m_verbose = false;

  //================
  // Constructor(s)
  //================
  public OneTimeInputBroker() {
  }

  //================
  // Public Methods
  //================

  //========================
  // D2K Abstract Overrides

  /**
     Return the name of this module.
     @return The name of this module.
   */
  public String getModuleName() {
    return "Onetime Input Broker";
  }


  /**
   * Return array of property descriptors for this module.
   * @return array
   */
  public PropertyDescription[] getPropertiesDescriptions() {
    PropertyDescription[] descriptions = new PropertyDescription[1];

    descriptions[0] = new PropertyDescription("verbose",
                                              "Verbose Output",
        "Do you want verbose output to the console.");

    return descriptions;
  }

  /**
     Return a description of a specific input.
     @param i The index of the input
     @return The description of the input
   */
  public String getInputInfo(int parm1) {
    if (parm1 == 0) {
      return "Object that is received once but output with every firing.";
    } else if (parm1 == 1) {
      return "Objects received repeatedly and passed through with each firing.";
    } else {
      return "";
    }
  }

  /**
     Return a description of a specific input.
     @param i The index of the input
     @return The description of the input
   */
  public String getInputName(int parm1) {
    if (parm1 == 0) {
      return "Onetime Object";
    } else if (parm1 == 1) {
      return "Repeating Object";
    } else {
      return "";
    }
  }

  /**
     Return a String array containing the datatypes the inputs to this
     module.
     @return The datatypes of the inputs.
   */
  public String[] getInputTypes() {
    String[] in = {
        "java.lang.Object", "java.lang.Object"};
    return in;
  }

  /**
    Return information about the module.
    @return A detailed description of the module.
   */
  public String getModuleInfo() {
    String s = "<p>Overview: ";
    s += "Take two inputs, one of which occurs only once, and produce two outputs that both always fire";
    s += "</p>";

    s += "<p>Detailed Description: ";
    s += "Take two inputs, the first of which occurs only once.  Having stored the singularly ";
    s += "occurring input upon first receipt, each time the second input is received, always output two outputs -- ";
    s += "the stored reference to the first input and the second input simply passed through.";
    s += "</p>";

    s += "<p>Data Type Restrictions: ";
    s += "Any object can be input to either input port.";
    s += "</p>";

    s += "<p>Data Handling: ";
    s += "This module does not modify anything.";
    s += "</p>";
    return s;
  }

  /**
     Return the description of a specific output.
     @param i The index of the output.
     @return The description of the output.
   */
  public String getOutputInfo(int parm1) {
    if (parm1 == 0) {
      return "Object that is received once but output with every firing.";
    } else if (parm1 == 1) {
      return "Objects received repeatedly and passed through with each firing.";
    } else {
      return "";
    }
  }

  /**
     Return the name of a specific output.
     @param i The index of the output.
     @return The name of the output
   */
  public String getOutputName(int parm1) {
    if (parm1 == 0) {
      return "Onetime Object";
    } else if (parm1 == 1) {
      return "Repeating Object";
    } else {
      return "";
    }
  }

  /**
     Return a String array containing the datatypes of the outputs of this
     module.
     @return The datatypes of the outputs.
   */
  public String[] getOutputTypes() {
    String[] out = {
        "java.lang.Object", "java.lang.Object"};
    return out;
  }

  /**
     Code to execute before doit.
   */
  public void beginExecution() {
    m_readOnce = null;
  }

  /**
     Code to execute at end of itinerary run.
   */
  public void endExecution() {
    super.endExecution();
    m_readOnce = null;
  }

  /**
     Conditions for module firing.
     @return boolean
   */
  public boolean isReady() {
    if (m_readOnce == null) {
      if ( (this.getFlags()[0] > 0) && (this.getFlags()[1] > 0)) {
        return true;
      } else {
        return false;
      }
    } else if (this.getFlags()[1] == 0) {
      return false;
    } else {
      return true;
    }
  }

  /**
   Perform the work of the module.
   @throws Exception
   */
  protected void doit() throws java.lang.Exception {
    try {
      if (m_readOnce == null) {
        m_readOnce = this.pullInput(0);
      }
      Object out = this.pullInput(1);

      if (getVerbose()) {
        System.out.println("Input Broker pushing: " + this.getAlias());
      }
      pushOutput(m_readOnce, 0);
      pushOutput(out, 1);

    }
    catch (Exception ex) {
      ex.printStackTrace();
      System.out.println(ex.getMessage());
      System.out.println("ERROR: OnetimeInputBroker.doit()");
      throw ex;
    }
  }

}
