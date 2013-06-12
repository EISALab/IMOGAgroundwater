package ncsa.d2k.modules.projects.dtcheng.matrix;

public class FDBigPhi
    extends FunctionDefinition {

  final static double OneOverRootTwoPi = 1.0 / Math.sqrt(2.0 * Math.PI);
  final static double xbreak1 = 0.46875;
  final static double xbreak2 = 4.00000;

  final static double[] a = {
      3.16112374387056560e00, 1.13864154151050156e02,
      3.77485237685302021e02, 3.20937758913846947e03,
      1.85777706184603153e-1};
  final static double[] b = {
      2.36012909523441209e01, 2.44024637934444173e02,
      1.28261652607737228e03, 2.84423683343917062e03};
  final static double[] c = {
      5.64188496988670089e-1, 8.88314979438837594e00,
      6.61191906371416295e01, 2.98635138197400131e02,
      8.81952221241769090e02, 1.71204761263407058e03,
      2.05107837782607147e03, 1.23033935479799725e03,
      2.15311535474403846e-8};
  final static double[] d = {
      1.57449261107098347e01, 1.17693950891312499e02,
      5.37181101862009858e02, 1.62138957456669019e03,
      3.29079923573345963e03, 4.36261909014324716e03,
      3.43936767414372164e03, 1.23033935480374942e03};
  final static double[] p = {
      3.05326634961232344e-1, 3.60344899949804439e-1,
      1.25781726111229246e-1, 1.60837851487422766e-2,
      6.58749161529837803e-4, 1.63153871373020978e-2};
  final static double[] q = {
      2.56852019228982242e00, 1.87295284992346047e00,
      5.27905102951428412e-1, 6.05183413124413191e-2,
      2.33520497626869185e-3};

  static final double neg_one_over_rt2 = -1.0 / Math.sqrt(2.0);

  public double[] evaluate(double[] x) {
    double[] result = new double[1];
    result[0] = evaluate(x[0]);
    return result;
  }

  public double fix(double x) {
    if (x < 0) {
      return Math.ceil(x);
    }
    else {
      return Math.floor(x);
    }
  }

  public double evaluate(double X) {
    return 0.5 * erfc(neg_one_over_rt2*(X));
  }

  public double erfc(double x) {

    double absX = Math.abs(x);

    double result = Double.NaN;

    if (absX <= xbreak1) {

      double z = absX * absX;
      double xnum = a[4] * z;
      double xden = z;

      for (int i = 0; i < 3; i++) {
        xnum = (xnum + a[i]) * z;
        xden = (xden + b[i]) * z;
      }
      result = 1.0 - (x * (xnum + a[3]) / (xden + b[3]));
    }
    else
    if (absX < xbreak2) {

      double y = absX;

      double xnum = c[8] * y;
      double xden = y;
      for (int i = 0; i < 7; i++) {
        xnum = (xnum + c[i]) * y;
        xden = (xden + d[i]) * y;
      }
      result = (xnum + c[7]) / (xden + d[7]);

      double z = fix(y * 16.0) / 16.0;
      double del = (y - z) * (y + z);
      result = Math.exp( -z * z) * Math.exp( -del) * result;

    }
    else {

      double y = absX;
      double z = 1 / (y * y);
      double xnum = p[5] * z;
      double xden = z;
      for (int i = 0; i < 4; i++) {
        xnum = (xnum + p[i]) * z;
        xden = (xden + q[i]) * z;
      }
      result = z * (xnum + p[4]) / (xden + q[4]);
      result = (1.0 / Math.sqrt(Math.PI) - result) / y;
      z = fix(y * 16.0) / 16.0;
      double del = (y - z) * (y + z);
      result = Math.exp( -z * z) * Math.exp( -del) * result;

      if (result == Double.POSITIVE_INFINITY) {
        result = 0.0;
      }
    }
    if (x < -xbreak1)
      return 2.0 - result;
    else
      return result;
  }

  public int getNumInputVariables() {
    return 1;
  }

  public int getNumOutputVariables() {
    return 1;
  }

}