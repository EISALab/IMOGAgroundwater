package ncsa.d2k.modules.core.discovery.cluster.sample;

//==============
// Java Imports
//==============

//===============
// Other Imports
//===============
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.discovery.cluster.gui.properties.*;
import ncsa.d2k.core.modules.*;


public class CoverageSampler extends CoverageSamplerOPT {

  //==============
  // Data Members
  //==============


  //============
  // Properties
  //============

  //Must be > 0
  public int getMaxNumCenters() {
    return _maxNumCenters;
  }

  public void setMaxNumCenters(int mt) {
    _maxNumCenters = mt;
  }

  public int getCutOff() {
    return _cutOff;
  }

  public void setCutOff(int co) {
    _cutOff = co;
  }

  public int getDistanceMetric() {
    return _distanceMetric;
  }

  public void setDistanceMetric(int dm) {
    _distanceMetric = dm;
  }

  //================
  // Constructor(s)
  //================
  public CoverageSampler() {
  }

  //================
  // Public Methods
  //================

  //========================
  // D2K Abstract Overrides

  /**
   * Return a list of the property descriptions.
   * @return a list of the property descriptions.
   */
  public PropertyDescription[] getPropertiesDescriptions() {
    PropertyDescription[] descriptions = new PropertyDescription[5];
    descriptions[0] = new PropertyDescription("maxNumCenters",
                                              "Maximum Samples.",
        "Maximum number of samples to be generated");
    descriptions[1] = new PropertyDescription("cutOff",
                                              "Distance Threshold",
                                     "This property specifies the percent of the max distance to use " +
                                     "as a cutoff value in forming new samples ([1...100].  The max distance between examples " +
                                     "is approximated by taking the min and max of each attribute and forming a " +
                                     "min example and a max example -- then finding the distance between the two.");
    descriptions[2] = new PropertyDescription("distanceMetric",
                                              "Distance Metric",
        "This property determine the type of distance function used to calculate " +
        "distance between two examples." +
        "<p>EUCLIDEAN: \"Straight\" line distance between points.</p>" +
        "<p>MANHATTAN: Distance between two points measured along axes at right angles.</p>" +
        "<p>COSINE: 1 minus the cosine of the angle between the norms of the vectors denoted by two points.</p>"
        );
    descriptions[3] = new PropertyDescription("CheckMissingValues",
        "Check Missing Values",
        "Perform a check for missing values on the table inputs (or not).");
    descriptions[4] = new PropertyDescription("verbose",
        "Verbose Output",
        "Do you want verbose output to the console.");
    return descriptions;
  }


/**
 Return a custom gui for setting properties.
 @return CustomModuleEditor
 */
  public CustomModuleEditor getPropertyEditor(){
    return new CoverageSampler_Props(this, true, true);
  }


/**
   Return a description of a specific input.
   @param i The index of the input
   @return The description of the input
 */
  public String getInputInfo(int parm1) {
    if (parm1 == 0) {
      return "Table to be sampled.";
    } else {
      return "";
    }
  }

/**
   Return the name of a specific input.
   @param i The index of the input.
   @return The name of the input
 */
  public String getInputName(int parm1) {
    if (parm1 == 0) {
      return "Table";
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
        "ncsa.d2k.modules.core.datatype.table.Table"};
    return in;
  }


/**
 Perform the work of the module.
 @throws Exception
 */
  protected void doit() throws java.lang.Exception {

    Table itable = (Table)this.pullInput(0);

    Coverage cover = new Coverage(this.getMaxNumCenters(), this.getCutOff(), this.getDistanceMetric(), this.getVerbose(), this.getCheckMissingValues());

    itable = cover.sample(itable);

    this.pushOutput(itable, 0);

  }


}
