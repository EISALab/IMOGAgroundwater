package ncsa.d2k.modules.projects.dtcheng.matrix;

public class FDLittlePhi extends FunctionDefinition {

  final static double OneOverRootTwoPi = 1.0 / Math.sqrt(2.0 * Math.PI);

  public double [] evaluate(double [] x) {
    double [] result = new double[1];
    result[0] = OneOverRootTwoPi * Math.exp(-0.5 * x[0] * x[0]);
    return result;
  }
  public double evaluate(double x) {
    double result = OneOverRootTwoPi * Math.exp(-0.5 * x * x);
    return result;
  }
  public int getNumInputVariables() {
    return 1;
  }
  public int getNumOutputVariables() {
    return 1;
  }

}