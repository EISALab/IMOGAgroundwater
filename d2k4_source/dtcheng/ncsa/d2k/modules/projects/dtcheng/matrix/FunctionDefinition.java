package ncsa.d2k.modules.projects.dtcheng.matrix;

public class FunctionDefinition implements java.io.Serializable {

  public double [] evaluate(double [] x) {
    System.out.println("error!  must define function");
    return null;
  }
  public double evaluate(double x) {
    System.out.println("error!  must define function");
    return Double.NaN;
  }
  public int getNumInputVariables() {
    System.out.println("error!  must define function");
    return -1;
  }
  public int getNumOutputVariables() {
    System.out.println("error!  must define function");
    return -1;
  }

}