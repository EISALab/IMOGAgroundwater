package ncsa.d2k.modules.core.transform.binning;


/**
 * put your documentation comment here
 */
public class NumericBinDescriptor extends BinDescriptor {
    public double min, max;

    /**
     * put your documentation comment here
     * @param     int col
     * @param     String n
     * @param     double mn
     * @param     double mx
     * @param     String label
     */
    public NumericBinDescriptor (int col, String n, double mn, double mx, String label) {
        super(col, label);
		//System.out.println("NumericBinDescriptor min " + mn + " max " + mx);
        column_number = col;
        name = n;
        min = mn;
        max = mx;
    }

    /**
     * put your documentation comment here
     * @param d
     * @return
     */
    public boolean eval (double d) {
        return  (d > min && d <= max);
    }

    /**
     * put your documentation comment here
     * @param s
     * @return
     */
    public boolean eval (String s) {
        try {
            double d = Double.parseDouble(s);
            return  eval(d);
        } catch (NumberFormatException e) {
            return  false;
        }
    }
}               // BinColumnsView$NumericBinDescriptor