package ncsa.d2k.modules.core.transform.binning;

public interface BinCounts {

    public double getMin(int col);
    public double getMax(int col);
    public int getNumRows();

    public int[] getCounts(int col, double[] borders);
    public double getTotal(int col);
}
