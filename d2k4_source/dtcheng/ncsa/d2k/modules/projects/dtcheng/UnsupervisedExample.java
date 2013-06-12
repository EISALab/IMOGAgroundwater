package ncsa.d2k.modules.projects.dtcheng;

public class UnsupervisedExample implements java.io.Serializable {
  public double[] values;
  public int[] history;

  public UnsupervisedExample() {

 }

 public UnsupervisedExample(double[] values, int[] history) {
   this.values = values;
   this.history = history;
 }

}
