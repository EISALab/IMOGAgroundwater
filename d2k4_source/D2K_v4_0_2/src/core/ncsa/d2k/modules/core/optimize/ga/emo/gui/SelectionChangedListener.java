package ncsa.d2k.modules.core.optimize.ga.emo.gui;

/**
 * A SelectionChangedListener is called whenever the selected points on a FitnessPlot
 * change.  Implementors of this interface register themselves directly with
 * the fitness plot.
 */
public interface SelectionChangedListener {
  public void selectionChanged();
}