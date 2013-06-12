package ncsa.d2k.modules.projects.dtcheng.matrix;

public class FDInverseLogistic extends FunctionDefinition {

  public double [] evaluate(double [] x) {
    double [] result = new double[1];
    result[0] = -1.0 * Math.log((1.0 / x[0]) - 1.0);
    return result;
  }
  public double evaluate(double x) {
    double result = -1.0 * Math.log((1.0 / x) - 1.0);
    return result;
  }
  public int getNumInputVariables() {
    return 1;
  }
  public int getNumOutputVariables() {
    return 1;
  }

}