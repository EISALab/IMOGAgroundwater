package ncsa.d2k.modules.core.transform.binning;

import java.util.*;


/**
 * put your documentation comment here
 */
public class TextualBinDescriptor extends BinDescriptor {
    public HashSet vals;

    /**
     * put your documentation comment here
     * @param     int col
     * @param     String n
     * @param     String[] v
     * @param     String label
     */
    public TextualBinDescriptor (int col, String n, String[] v, String label) {
        super(col, label);
        name = n;
        vals = new HashSet();
        for (int i = 0; i < v.length; i++)
            vals.add(v[i]);
    }

    /**
     * put your documentation comment here
     * @param d
     * @return
     */
    public boolean eval (double d) {
        return  eval(Double.toString(d));
    }

    /**
     * put your documentation comment here
     * @param s
     * @return
     */
    public boolean eval (String s) {
        return  vals.contains(s);
    }
}               // BinColumnsView$TextualBinDescriptor
