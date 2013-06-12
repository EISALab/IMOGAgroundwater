package ncsa.d2k.modules.core.transform.binning;

import java.io.Serializable;

/**
 * Describes a bin in this object.
 */
public abstract class BinDescriptor implements Serializable {
    public int column_number;
    public String name;
    public String label;

    /**
     * put your documentation comment here
     * @param d
     * @return
     */
    public abstract boolean eval (double d);

    /**
     * put your documentation comment here
     * @param s
     * @return
     */
    public abstract boolean eval (String s);

    /**
     * put your documentation comment here
     * @param     String lbl
     */
    public BinDescriptor (int col, String lbl) {
        column_number = col;
        label = lbl;
    }

    /**
     * put your documentation comment here
     * @return
     */
    public String toString () {
        StringBuffer sb =
                //new StringBuffer(table.getColumnLabel(column_number));
        new StringBuffer(label);
        sb.append(":");
        sb.append(name);
        return  sb.toString();
    }
}               // BinColumnsView$BinDescriptor