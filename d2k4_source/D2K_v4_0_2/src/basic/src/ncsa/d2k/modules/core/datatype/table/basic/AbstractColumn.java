package  ncsa.d2k.modules.core.datatype.table.basic;

import  java.util.*;


/**
 * AbstractColumn implements several methods that are common
 * to all Columns.
 */
abstract public class AbstractColumn
        implements Column {
    //static final long serialVersionUID = -2585038024480240364L;
    static final long serialVersionUID = -213911595597217168L;
    private String label;
    private String comment;
    private boolean isNominal;
    private boolean isScalar;
    protected int type;

    /*protected String nominalEmptyValue;
     protected String nominalMissingValue;
     protected double scalarEmptyValue;
     protected double scalarMissingValue;
     */
    protected AbstractColumn () {
        /*setNominalEmptyValue(".");
         setNominalMissingValue("?");
         setScalarMissingValue(new Double(Double.NEGATIVE_INFINITY));
         setScalarEmptyValue(new Double(Double.POSITIVE_INFINITY));
         */
    }

    /**
     Get the label associated with this Column.
     @return the label which describes this Column
     */
    public String getLabel () {
        return  label;
    }

    /**
     Set the label associated with this Column.
     @param labl the label which describes this Column
     */
    public void setLabel (String labl) {
        label = labl;
    }

    /**
     Get the comment associated with this Column.
     @return the comment which describes this Column
     */
    public String getComment () {
        return  comment;
    }

    /**
     Set the comment associated with this Column.
     @param cmt the comment which describes this Column
     */
    public void setComment (String cmt) {
        comment = cmt;
    }

    /**
     * put your documentation comment here
     * @param value
     */
    public void setIsNominal (boolean value) {
        isNominal = value;
        isScalar = !value;
    }

    /**
     * put your documentation comment here
     * @param value
     */
    public void setIsScalar (boolean value) {
        isScalar = value;
        isNominal = !value;
    }

    /**
     * put your documentation comment here
     * @return
     */
    public boolean getIsNominal () {
        return  isNominal;
    }

    /**
     * put your documentation comment here
     * @return
     */
    public boolean getIsScalar () {
        return  isScalar;
    }

    /**
     * put your documentation comment here
     * @return
     */
    public int getType () {
        return  type;
    }

    /**
     * put your documentation comment here
     * @param start
     * @param length
     */
    public void removeRows (int start, int length) {
        int[] toRemove = new int[length];
        int idx = start;
        for (int i = 0; i < length; i++) {
            toRemove[i] = idx;
            idx++;
        }
        removeRowsByIndex(toRemove);
    }

    /**
     Given an array of booleans, will remove the positions in the Column
     which coorespond to the positions in the boolean array which are
     marked true.  If the boolean array and Column do not have the same
     number of elements, the remaining elements will be discarded.
     @param flags the boolean array of remove flags
     */
    public void removeRowsByFlag (boolean[] flags) {
        // keep a list of the row indices to remove
        LinkedList ll = new LinkedList();
        int i = 0;
        for (; i < flags.length; i++) {
            if (flags[i])
                ll.add(new Integer(i));
        }
        for (; i < getNumRows(); i++) {
            ll.add(new Integer(i));
        }
        int[] toRemove = new int[ll.size()];
        int j = 0;
        Iterator iter = ll.iterator();
        while (iter.hasNext()) {
            Integer in = (Integer)iter.next();
            toRemove[j] = in.intValue();
            j++;
        }
        // now call remove by index to remove the rows
        removeRowsByIndex(toRemove);
    }

    /*
     */
    /*	public String getNominalMissingValue() {
     return nominalMissingValue;
     }
     public void setNominalMissingValue(String s) {
     nominalMissingValue = s;
     }
     public Number getScalarMissingValue() {
     return new Double(scalarMissingValue);
     }
     public void setScalarMissingValue(Number val) {
     scalarMissingValue = val.doubleValue();
     }
     public Number getScalarEmptyValue() {
     return new Double(scalarEmptyValue);
     }
     public void setScalarEmptyValue(Number val) {
     scalarEmptyValue = val.doubleValue();
     }
     public String getNominalEmptyValue() {
     return nominalEmptyValue;
     }
     public void setNominalEmptyValue(String s) {
     nominalEmptyValue = s;
     }
     public boolean isValueMissing(int row) {
     if(getIsScalar())
     return getDouble(row) == scalarMissingValue;
     else
     return getString(row).equals(nominalMissingValue);
     }
     public boolean isValueEmpty(int row) {
     if(getIsScalar())
     return getDouble(row) == scalarEmptyValue;
     else
     return getString(row).equals(nominalMissingValue);
     }
     public void setValueToMissing(int row) {
     if(getIsScalar())
     setDouble(scalarMissingValue, row);
     else
     setString(nominalMissingValue, row);
     }
     public void setValueToEmpty(int row) {
     if(getIsScalar())
     setDouble(scalarEmptyValue, row);
     else
     setString(nominalEmptyValue, row);
     }
     */
    //ANCA: method for comparing two Column objects.
    // Could be more efficient but as is used only in Junit tests,
    // less code is more important than speed of execution.
    // should also compare missing and empty arrays
    public boolean equals (Object col) {
        Column column;
        try {
            column = (Column)col;
        } catch (Exception e) {
            return  false;
        }
        /*if(internal.length != column.getNumRows()) return false;
         for (int i =0; i < internal.length; i ++)
         if(internal[i] != column.getInt(i))
         return false;
         return true;
         */
        if (getNumEntries() != column.getNumEntries())
            return  false;
        for (int i = 0; i < getNumEntries(); i++) {
            Object ob1 = getObject(i);
            Object ob2 = column.getObject(i);
            if (!ob1.equals(ob2))
                return  false;
        }
        return  true;
    }
}



