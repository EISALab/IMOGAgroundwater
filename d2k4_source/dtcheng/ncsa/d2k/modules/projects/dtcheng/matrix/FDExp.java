package ncsa.d2k.modules.projects.dtcheng.matrix;

public class FDExp extends FunctionDefinition {

  public double [] evaluate(double [] x) {
    double [] result = new double[1];
    result[0] = Math.exp(x[0]);
    return result;
  }
  public double evaluate(double x) {
    double result = Math.exp(x);
    return result;
  }
  public int getNumInputVariables() {
    return 1;
  }
  public int getNumOutputVariables() {
    return 1;
  }

}