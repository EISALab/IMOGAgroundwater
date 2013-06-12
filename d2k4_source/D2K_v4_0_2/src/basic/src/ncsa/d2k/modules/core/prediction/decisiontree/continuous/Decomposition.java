package ncsa.d2k.modules.core.prediction.decisiontree.continuous;
import ncsa.d2k.modules.core.datatype.table.*;
public class Decomposition implements java.io.Serializable
  {
  int    inputIndex;
  double value;

  public boolean evaluate(ExampleTable exampleSet, int e)
    {

    if (exampleSet.getInputDouble(e, inputIndex) > value)
      return true;
    else
      return false;

    }

  // The following methods make "internals" accessible to
  // classes outside the package. Used to write the model
  // in PMML, for example.

  /**
        Get the index of the input feature associated with the decomposition
        @return The index of the input feature associated with the decomposition.
  */
  public int getInputIndex()
    {
    return inputIndex;
    }

  /**
        Get the value used in the comparison for this decomposition
        @return The comparison value for this decomposition.
  */
  public double getValue()
    {
    return value;
    }


  }
