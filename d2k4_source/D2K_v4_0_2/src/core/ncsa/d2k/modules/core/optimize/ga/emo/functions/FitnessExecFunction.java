package ncsa.d2k.modules.core.optimize.ga.emo.functions;

/**
 * A fitness function calculated using an executable.
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class FitnessExecFunction
    extends ExecFunction
    implements FitnessFunction {

  boolean minimizing;

  public FitnessExecFunction(String n, String e, String i, String o,
                             boolean min) {
    super(n, e, i, o);
    minimizing = min;
  }

  public boolean isMinimizing() {
    return minimizing;
  }
}
