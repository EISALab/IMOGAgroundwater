package ncsa.d2k.modules.core.optimize.ga.emo;

public class DecisionVariable implements java.io.Serializable {
  
  private String name;
  private double min;
  private double max;
  private double precision;
  private int stringLength; 
  
  public DecisionVariable(String n, double mn, double mx, double p, int sl) {
    name = n;  
    min = mn; 
    max = mx;
    precision = p;
    stringLength = sl;
  }
  
  public static int calculateStringLength(double min, double max, 
                                          double precision) {
    double numBits = (max - min + 1) / precision;
    numBits = Math.log( (double) numBits);
    numBits = numBits / Math.log(2.0);

    // now we know the number of bits required to represent
    // a number in this interval

    // the string length must be an integer, so we will need to
    // round up if numBits is not an integer

    int sLength = (int) Math.floor(numBits);
    if (sLength < numBits) {
      sLength++;
    }
    
    return sLength;
  }
  
  public static boolean[] convertToBitString(double value, double min, double max, double precision) {
    int strLen = calculateStringLength(min, max, precision);  
    
    double valuePerBit = (max-min)/strLen;
    
    boolean[] retVal = new boolean[strLen];
    double currentValue = min;
    for (int k = 0; k < strLen; k++) {
      double addedValue = k * valuePerBit;
      if( (currentValue + addedValue) <= value) {
        currentValue += addedValue;  
        retVal[k] = true;
      }
      else
        retVal[k] = false;
    }
    
    return retVal; 
  }
  
  public String getName() {
    return name;
  }
  
  public double getMin() {
    return min;
  }
  
  public double getMax() {
    return max;
  }
  
  public double getPrecision() {
    return precision;
  }
  
  public int getStringLength() {
    return stringLength;  
  }
}