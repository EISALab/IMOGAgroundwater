package ncsa.d2k.modules.core.discovery.ruleassociation;

import ncsa.d2k.modules.core.datatype.table.*;
//import java.io.*;
import java.util.*;
import gnu.trove.*;

/**
 * <p>Title: FreqItemSet
 * <p>Description: This class holds a frequent item set for ruleassociation.
 * Each object contains 3 components: support, number of items, and
 * the list of integers. The list of integers represents the items mapping
 * to the itemLabels
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: NCSA ALG</p>
 * @author Dora Cai
 * @version 1.0
 */
public class FreqItemSet implements java.io.Serializable {
    public TIntArrayList items; // indexes in the ArrayList itemLabels
    public double support; // support for the frequent item set
    public int numberOfItems; // confidence for the frequent item set

    public int hashCode() {
        if (items != null) {
            StringBuffer sb = new StringBuffer();
            int[] ar = items.toNativeArray();
            Arrays.sort(ar);
            for(int i = 0; i < ar.length; i++) {
                sb.append(Integer.toString(ar[i]));
                if (i != ar.length-1)
                    sb.append(",");
            }
            return sb.toString().hashCode();
        }
        else
          return super.hashCode();
    }
}