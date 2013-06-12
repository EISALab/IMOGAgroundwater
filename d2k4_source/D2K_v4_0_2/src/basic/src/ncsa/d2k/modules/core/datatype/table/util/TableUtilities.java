package ncsa.d2k.modules.core.datatype.table.util;

import java.util.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import gnu.trove.*;

/**
 * Contains useful methods to find statistics about columns of a Table.
 * Also methods for comparing/sorting by multiple columns of a Table.
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: </p>
 * @author clutter
 * @version 1.0
 */
public class TableUtilities
{
  public static final int MINIMUM = 0;
  public static final int MAXIMUM = 1;

  private TableUtilities ()
  {
  }

  /**
   * Get all the statistics about a scalar column.  This uses a helper method
   * to calculate the mean, median, variance, standard deviation, minimum,
   * maximum, and first and third quartiles and return them in a ScalarStatistics object.
   * If the column is not numeric, null is returned.   If there are no values in the
   * column, a message is printed to System.out and all the statistics are set to 0 in the object
   * that is returned.  The method handles missing and empty values.
   * @param table the table
   * @param colNum the column number
   * @return the object that encapsulates the statistics
   */

  public static ScalarStatistics getScalarStatistics (Table table, int colNum)
  {
    if (!table.isColumnNumeric (colNum)) {
	return null;
    }

    // Create a list of samples that should be included
    // Make an array of doubles from the list.
    TDoubleArrayList list = new TDoubleArrayList ();
    for (int i = 0; i < table.getNumRows (); i++) {
	if (!table.isValueMissing (i, colNum) && !table.isValueEmpty (i, colNum)) {
	    list.add (table.getDouble (i, colNum));
	}
    }
    double[] d = list.toNativeArray ();

    // Call helper method to compute the needed statistics given the array.
    return (_computeScalarStatistics (d));
  }

  /**
   * Get all the statistics about a scalar column, considering only the rows
   * specified by the boolean array passed in as argument 1.  This uses a helper method
   * to calculate the mean, median, variance, standard deviation, minimum,
   * maximum, and first and third quartiles and return them in a ScalarStatistics object.
   * If the column is not numeric, null is returned.   If there are no values in the
   * column, a message is printed to System.out and all the statistics are set to 0 in the object
   * that is returned.  The method handles missing and empty values.
   * @param flags the rows to consider
   * @param table the table
   * @param colNum the column number
   * @return the object that encapsulates the statistics
   */

  public static ScalarStatistics getScalarStatistics (boolean[]flags,
						      Table table, int colNum)
  {
    if (!table.isColumnNumeric (colNum)) {
	return null;
    }

    // Create a list of samples that should be included
    // Make an array of doubles from the list.
    TDoubleArrayList list = new TDoubleArrayList ();
    for (int i = 0; i < table.getNumRows (); i++) {
	if (flags[i] && !table.isValueMissing (i, colNum) && !table.isValueEmpty (i, colNum)) {
	    list.add (table.getDouble (i, colNum));
	}
    }
    double[] d = list.toNativeArray ();

    // Call helper method to compute the needed statistics given the array.
    return (_computeScalarStatistics (d));

  }

  /**
   * Helper method called by getScalarStatistics methods to do most of the
   * real computation.
   * @param d
   * @return
   */
  private static ScalarStatistics _computeScalarStatistics (double[]d)
  {

    // Handle sample sizes of 0 and 1.  Standard formulas don't work on these as they
    // cause indexing problems.

    double nsamples = d.length;	// number of samples as a double for later computations

    if (nsamples == 0) {
	System.out.println ("There were no sample values for the specified attribute. "
		             + "Therefore, statistics will be reported as zeros. ");
	return new ScalarStatistics (0, 0, 0, 0, 0, 0, 0, 0);
    }

    if (nsamples == 1) {
	return new ScalarStatistics (d[0], d[0], 0, 0, d[0], d[0], d[0], d[0]);
    }

    // Do the computations that make sense when there is more than 1 sample then
    // sort the array as order is important for future processing.

    double total = 0;
    for (int i = 0; i < nsamples; i++) {
	total += d[i];
    }
    double sample_mean = total / nsamples;

    double sum_of_sq_of_deviation = 0;
    for (int i = 0; i < nsamples; i++) {
	sum_of_sq_of_deviation += (d[i] - sample_mean) * (d[i] - sample_mean);
    }
    double sample_variance = sum_of_sq_of_deviation / (nsamples - 1.0);	// unbiased estimator
    double stddev = Math.sqrt (sample_variance);

    Arrays.sort (d);

    //
    // There is no standard way to compute quartiles, with the main differences
    // between methods related to:
    // 1) whether the quartiles must be taken from the sample set or can be 'computed values'
    // 2) if the quatriles are computed, whether they should be the midpoint of the two closest
    //    data points or a linear interpolation between the two points
    // 3) whether the median in an odd number of samples should be included in both halves
    //    when computing the quartiles.
    // - SAS offers 5 different methods.
    // - Tukey (inventor of box-and-whisker plot), includes median sample in halves and uses
    //   the midpoint between 2 closest data points.
    // - Moore & McCabe  (and the TI83) exclude the median sample and use midpoint.
    // - Excel and S-Plus interpolate (differently than SPSS and MiniTab) and include the sample
    //   median when the sample count is odd.  They produce the same results as Tukey when
    //   the sample size is odd.
    // - Minitab, SPSS, and SAS method 4 use linear interpolation and exclude the midpoint
    //   value when the sample count is odd.   They produce the same results as Moore & McCabe
    //   for odd sample counts.
    //
    // The algorithm used below implements the Minitab/SPSS/SAS Method 4 computation of quartiles.
    //

    // If 2 samples, median same as mean (midpoint between two values)
    // Q1 = min;  Q3 = max;    Seems most sensible compared to what we get with 3 points - the
    // references consulted didn't deal with this small of a sample set.
    if (d.length == 2) {
	return new ScalarStatistics (sample_mean, sample_mean,
				     sample_variance, stddev, d[0], d[1],
				     d[0], d[1]);
    }

    // If 3 samples, median set to sample 2. Q1 = min and Q3 = max.  The formula
    // below would yield these results but also tries to reference d[3] and
    // multiply it by the weight 0, therefore has an array index problem in the
    // computations below.  Hence this special case.
    if (d.length == 3) {
	return new ScalarStatistics (sample_mean, d[1],
				     sample_variance, stddev, d[0], d[2],
				     d[0], d[2]);
    }

    // From here on it's okay to use standard formula without getting into trouble with
    // indicies out of bounds.   The formula is based on online references, in particular
    // SAS Elementary Statistics procedures Keywords and Formulas for Percentile and
    // related statistics.
    // Since our sample array, d, is indexed from 0 and not 1, the array index in the
    // standard formula is decremented when doing the lookup for the actual values.

    double product;
    int productInteger;
    double productDecimal;
    double quartile[] = new double[4];	// quartile[0] won't be used but easier to index from 1

    for (int q = 1; q < 4; q++) {
	product = (nsamples + 1.0) * (q * .25);
	productInteger = (int) Math.floor (product);
	productDecimal = product - productInteger;
	quartile[q] = (1 - productDecimal) * d[productInteger - 1] + (productDecimal) * d[productInteger];
    }

    return new ScalarStatistics (sample_mean, quartile[2], sample_variance,
				 stddev, d[0], d[(int) nsamples - 1],
				 quartile[1], quartile[3]);
  }


  /**
   * Get all the unique values in a specific column of a Table.
   * @param table the table
   * @param colNum the column to find the unique values for
   * @return an array of Strings containing all the unique values in the specified column
   */
  public static String[] uniqueValues (Table table, int colNum)
  {
    int numRows = table.getNumRows ();

    // count the number of unique items in this column
    HashSet set = new HashSet ();
    if (table.hasMissingValues(colNum)) {
		for (int i = 0; i < numRows; i++) {
		 if (!table.isValueMissing(i, colNum)) {
				String s = table.getString (i, colNum);
				if (!set.contains (s)) {
				  set.add (s);
				}
			}
        }
    } else
        for (int i = 0; i < numRows; i++) {
    	    String s = table.getString (i, colNum);
        	if (!set.contains (s)) {
          		set.add (s);
        	}
    	}

    String[]retVal = new String[set.size ()];
    int idx = 0;
    Iterator it = set.iterator ();
    while (it.hasNext ()) {
	  retVal[idx] = (String) it.next ();
	  idx++;
    }
    return retVal;
  }

  public static HashSet uniqueValueSet (Table table, int colNum)
  {
    int numRows = table.getNumRows ();

    // count the number of unique items in this column
    HashSet set = new HashSet ();

	if (table.hasMissingValues(colNum)) {
			for (int i = 0; i < numRows; i++) {
			 if (!table.isValueMissing(i, colNum)) {
					String s = table.getString (i, colNum);
					if (!set.contains (s)) {
					  set.add (s);
					}
				}
			}
		} else
			for (int i = 0; i < numRows; i++) {
				String s = table.getString (i, colNum);
				if (!set.contains (s)) {
					set.add (s);
				}
			}

    return set;
  }

  public static boolean isKeyColumn (Table table, int colNum)
  {
    int numRows = table.getNumRows ();

    String[]unique = TableUtilities.uniqueValues (table, colNum);
    //there is no support for missing values - unique no of values could be less
    //than numRows if there were missing values
    if (numRows != unique.length) {
      return false;
    } else {
      return true;
    }
  }

  /**
   * Expand an array by one.
   * @param orig
   * @return
   */
  private static int[] expandArray (int[]orig)
  {
    int[] newarray = new int[orig.length + 1];
    System.arraycopy (orig, 0, newarray, 0, orig.length);
    return newarray;
  }

  /**
   * Take the data from the columna and put it into a double array.
   * @param col the column to get the data from.
   * @return the data from the column represented as a double arry.
   */
  protected static double [] populateDoubleArray (Column col)
  {
	double[] vals = new double[col.getNumRows ()];
	for (int i = 0 ; i < vals.length ; i++)
	  vals[i] = col.getDouble(i);
	return vals;
  }

  public static Table getPDFTable (Table t, int idx, double mean, double stdDev)
  {
    double[] vals = populateDoubleArray(t.getColumn(idx));

    double[] pdfs = new double[t.getNumRows ()];
    for (int i = 0; i < t.getNumRows (); i++) {
	pdfs[i] = pdfCalc (vals[i], mean, stdDev);
    }

    Column[]c = new Column[2];
    c[0] = new DoubleColumn (vals);
    c[1] = new DoubleColumn (pdfs);
    TableImpl tbl = new MutableTableImpl (c);
    return tbl;
  }

  private static double pdfCalc (double X, double mean, double stdDev)
  {
    double exp = Math.pow ((X - mean), 2);
    exp *= -1;
    exp /= (2 * Math.pow (stdDev, 2));

    double numerator = Math.pow (Math.E, exp);
    double denom = (stdDev * Math.pow ((2 * Math.PI), .5));
    return numerator / denom;
  }

  /**
   * Find all the unique values of a column, as well as a count of their
   * frequencies.  A HashMap is returned, with the unique values as the keys
   * and their frequencies as the values.  The frequencies are stored as Integers.
   * @param table
   * @param colNum
   * @return
   */
  public static HashMap uniqueValuesWithCounts (Table table, int colNum)
  {
    int[] outtally = new int[0];
    HashMap outIndexMap = new HashMap ();
    int numRows = table.getNumRows ();
    if (table.hasMissingValues()) {
	for (int i = 0; i < numRows; i++) {
	  if(!table.isValueMissing(i,colNum)) {
	   String s = table.getString (i, colNum);

	   if (outIndexMap.containsKey (s)) {
		   Integer in = (Integer) outIndexMap.get (s);
		   outtally[in.intValue ()]++;
	   } else {
		   outIndexMap.put (s, new Integer (outIndexMap.size ()));
		   outtally = expandArray (outtally);
		   outtally[outtally.length - 1] = 1;
	   }
	  }
     }
    }
    else {

	 for (int i = 0; i < numRows; i++) {
	String s = table.getString (i, colNum);
	if (outIndexMap.containsKey (s)) {
	    Integer in = (Integer) outIndexMap.get (s);
	    outtally[in.intValue ()]++;
	} else {
	    outIndexMap.put (s, new Integer (outIndexMap.size ()));
	    outtally = expandArray (outtally);
	    outtally[outtally.length - 1] = 1;
	}
    }
}
    HashMap retVal = new HashMap ();
    Iterator ii = outIndexMap.keySet ().iterator ();
    while (ii.hasNext ()) {
	String val = (String) ii.next ();
	Integer idx = (Integer) outIndexMap.get (val);
	retVal.put (val, new Integer (outtally[idx.intValue ()]));
    }
    return retVal;
  }


  public static boolean equalTo (Table table, int r1, int c1, int r2, int c2)
  {
    return false;
  }

  /**
   * Get both the minimum and maximum of a column.  An array of doubles of length
   * 2 is returned, with the first element being the minimum and the second
   * element being the maximum.
   * @param table
   * @param colNum
   * @return
   */
  public static double[] getMinMax (Table table, int colNum)
  {
    double max = Double.NEGATIVE_INFINITY, min = Double.POSITIVE_INFINITY;
    int numRows = table.getNumRows ();
    double d;

    for (int i = 0; i < numRows; i++) {
	d = table.getDouble (i, colNum);

	if (d > max) {
	    max = d;
	}
	if (d < min) {
	    min = d;
	}
    }

    double[] retVal = new double[2];
    retVal[MINIMUM] = min;
    retVal[MAXIMUM] = max;
    return retVal;
  }

  /**
   * Get the mean of a column of a Table.
   * Will return NaN if no entries in column.
   * @param table
   * @param colNum
   * @return
   */
  public static double mean (Table table, int colNum)
  {
    double sample_mean;
    double total = 0.0;
    int numRows = table.getNumRows ();

    for (int i = 0; i < numRows; i++) {
	total += table.getDouble (i, colNum);
    }

    sample_mean = total / (double) numRows;
    return sample_mean;
  }

  /**
   * Get the median of a column of a Table.
   * Will return NaN if no entries in column.
   * @param table
   * @param colNum
   * @return
   */
  public static double median (Table table, int colNum)
  {
    double sample_median;
    int numRows = table.getNumRows ();

    if (numRows == 0) {
	sample_median = Double.NaN;
    } else if (numRows == 1) {
	sample_median = table.getDouble (0, colNum);
    } else {
	double[]d = populateDoubleArray(table.getColumn(colNum));
	Arrays.sort (d);
	sample_median = (d[(int) Math.floor ((numRows - 1) / 2.0)] +
			 d[(int) Math.ceil  ((numRows - 1) / 2.0)]) / 2.0;
    }
    return sample_median;
  }

  /**
   * Get the standard deviation of a column of a table.
   * Returns 0 if no entries in column.
   * @param table
   * @param colNum
   * @return
   */
  public static double standardDeviation (Table table, int colNum)
  {
    double stddev = Math.sqrt (variance (table, colNum));
    return stddev;
  }

  /**
   * Get the variance of a column of a Table.
   * Returns 0 if no entries in column.
   * @param table
   * @param colNum
   * @return
   */
  public static double variance (Table table, int colNum)
  {
    double sample_variance;
    double nsamples = table.getNumRows();

    if ( nsamples <= 1 ) {
        sample_variance = 0 ;
    } else {
        // Do the computations that make sense when there is more than 1 sample then
        // sort the array as order is important for future processing.

        double total = 0;
        for (int i = 0; i < nsamples; i++) {
            total += table.getDouble (i, colNum);
        }

        double sample_mean = total / nsamples;
        double sum_of_sq_of_deviation = 0;

        for (int i = 0; i < nsamples; i++) {
            sum_of_sq_of_deviation += (table.getDouble (i, colNum) - sample_mean) * (table.getDouble (i, colNum) - sample_mean);
        }
        sample_variance = sum_of_sq_of_deviation / (nsamples - 1.0);	// unbiased estimator
    }

    return sample_variance;
  }


  /** a helpful function for copying data from table to table
   * copies a value from table1 to table2.Assumes columns are of
   * the same type
   */
  public static void setValue (Table t1, int row1, int col1, MutableTable t2, int row2, int col2)
  {

    if (t1.isColumnNumeric (col1)) {
	t2.setDouble (t1.getDouble (row1, col1), row2, col2);
    } else {
	t2.setObject (t1.getObject (row1, col1), row2, col2);
    }
  }

	/** helpful for copying rows of data without instantiating a whole
	 * 'row' object. uses <code>setValue</code> to do the copying.
	 * assumes same number of columns and same column types.
	 *
	 * @param t1 source table
	 * @param row1 the row index of the source table
	 * @param t2 a writable destination table
	 * @param row2 the row index to copy row1's values to
	 *
	 * @return nothing, does the copy in place
	 */
	public static void setRow(Table t1, int row1, MutableTable t2, int row2){
		int numCols = t1.getNumColumns();
		for(int i = 0; i < numCols; i++){
			setValue(t1, row1, i, t2, row2, i);
		}
	}

  //////////////////////
  ///Multi-Column Ops
  //////////////////////
  /**
   * multiSortIndex.
   *
   *	takes a table and an array of column indices to sort by.
   *	The resulting array of row indices will contain row pointers
   *	ordered such that rows are ordered by the first column index,
   *	then the second, etc.
   *
   *	@param tbl the table to multisort
   *	@param sortByCols the columns to sort by
   *
   *	@return an int[] of size tbl.getNumRows() representing the
   *	sorted order of tbl
   **/
  public static int[]multiSortIndex (Table tbl, int[] sortByCols)
  {
    int size = tbl.getNumRows ();
    int[]order = new int[size];
    for (int i = 0; i < size; i++) {
	order[i] = i;
    }

    multiQuickSort (tbl, sortByCols, order, 0, size - 1);
    multiInsertionSort (tbl, sortByCols, order);
    return order;
  }

  public static int[]multiSort (Table tbl, int[] sortByCols, int[] order)
  {
    multiQuickSort (tbl, sortByCols, order, 0, order.length - 1);
    multiInsertionSort (tbl, sortByCols, order);
    return order;
  }

  /** multiQuickSort.
   *
   * DO NOT use this function directly, as it does not completely
   * sort the table (an insertion sort must be done afterwards,
   * use multiSortIndex)
   */
  private static void multiQuickSort (Table tbl, int[] sortByCols,
				      int[] order, int l, int r)
  {

    if (r - l <= 3) {
	return;
    }
    int pivot;

    int i = (r + l) / 2;

    if (compareMultiCols (tbl, tbl, order[l], order[i], sortByCols, sortByCols) > 0)
      swap (order, l, i);
    if (compareMultiCols (tbl, tbl, order[l], order[r], sortByCols, sortByCols) > 0)
      swap (order, l, r);
    if (compareMultiCols (tbl, tbl, order[i], order[r], sortByCols, sortByCols) > 0)
      swap (order, i, r);

    swap (order, i, r - 1);

    pivot = r - 1;

    i = l + 1;
    int j = r - 2;

    while (j > i) {

	while ((compareMultiCols (tbl, tbl, order[i], order[pivot], sortByCols, sortByCols) <= 0)
	       && (i < j)) {
	    i++;
	}
	while ((compareMultiCols (tbl, tbl, order[j], order[pivot], sortByCols, sortByCols) >= 0)
	       && (i < j)) {
	    j--;
	}

	if (i < j) {
	    swap (order, i, j);
	}
    }
    swap (order, r - 1, j);

    multiQuickSort (tbl, sortByCols, order, l, i - 1);
    multiQuickSort (tbl, sortByCols, order, j + 1, r);
  }

  /**
   * multiInsertionSort.
   *
   * A slow sort alg meant to be used after the quickSort function
   * contained in this class
   *
   * @param tbl the table to multisort
   * @param sortByCols the columns to sort by
   *
   * @return an int[] of size tbl.getNumRows() representing the
   * sorted order of tbl
   */
	public static void multiInsertionSort (Table tbl, 
			int[] sortByCols, int[] order){

		int size = order.length;
		int i;
		int j;
		int v;

		for (j = 1; j < size; j++) {
			i = j - 1;
			v = order[j];
			while ((i >= 0) && 
					(0 < compareMultiCols(
						tbl, tbl, order[i], v, sortByCols, sortByCols))) {
			
				order[i + 1] = order[i];
				i--;
			}
			order[i + 1] = v;
		}
	}


  /**
   * compareMultiCols.
   *
   *  if the value of the row in table 1 is greater, returns 1.
   *  if they are equal returns 0
   *  if less than, -1
   *
   * e1>e2 -> 1
   * e1=e2 -> 0
   * e1<e2 -> -1
   **/
  public static int compareMultiCols (Table t1, Table t2, int row1, int row2,
				      int[] cols1, int[] cols2)
  {

    int numCompareCols = cols1.length;
    int eq = 0;
    for (int i = 0; i < numCompareCols; i++) {
	eq = compareValues (t1, row1, cols1[i], t2, row2, cols2[i]);
	if (eq != 0) {
	    return eq;
	}
    }
    return 0;
  }

  private static void swap (int[] ar, int i, int j)
  {
    int t = ar[i];
    ar[i] = ar[j];
    ar[j] = t;
  }


public static int compareStrings(String str1, String str2){

    //vered: copied from basic3-0 version:

       // Determine whether the strings are bin names,
        // because bin names need a special method to compare
        if ((str1.indexOf("[")>=0 || str1.indexOf("]")>=0) &&
              (str2.indexOf("[")>=0 ||  str2.indexOf("]")>=0) &&
              str1.indexOf(":")>=0 &&  str2.indexOf(":")>=0) {
            return compareBinNames(str1, str2);

        }
        else { // data type is regular string, not bin name
          return str1.compareTo(str2);

        }

}

    /**
     * Return 0 if they
     * are the same, greater than zero if element is greater,
     * and less than zero if element is less.
     * Assumes columns are of same type.
     * (added by vered to support sort method of Column objects)
     */

  public static int compareValues (Column c1, int row1, Column c2, int row2){
    int type = c1.getType();

   //the numeric case
   if (type == ColumnTypes.DOUBLE || type == ColumnTypes.INTEGER ||
       type == ColumnTypes.FLOAT  || type == ColumnTypes.LONG || type == ColumnTypes.SHORT) {
       double d1 = c1.getDouble (row1);
       double d2 = c2.getDouble (row2);
       if (d1 == d2) {
           return 0;
       } else if (d1 > d2) {
           return 1;
       } else {
         return -1;
       }
     }//end if column numeric

   int it = -2;

   //the other cases
   switch (type) {

     case (ColumnTypes.STRING): {


            it = compareStrings(c1.getString (row1), c2.getString (row2));
            break;

         /*//vered: was replaced by the above code from basic 3-0 version
          it = t1.getString (row1, col1).compareTo (t2.getString (row2, col2));
         break;*/
     }
     case (ColumnTypes.CHAR_ARRAY): {
         it = compareChars (c1.getChars (row1), c2.getChars (row2));
         break;
     }
     case (ColumnTypes.BYTE_ARRAY): {
         it = compareBytes (c1.getBytes (row1), c2.getBytes (row2));
         break;
     }

     //VERED: 12-11-03
    //changed the case for boolean to return 0 if identical
    //otherwise - 1 if value at (row1,col1) is true. else -1.
     case (ColumnTypes.BOOLEAN): {
           boolean val1 = c1.getBoolean (row1);
           boolean val2 =  c2.getBoolean (row2);
         if ( val1 == val2) {
               it = 0;
           } else {
             if(val1)     it = 1;
             else it = -1;
           }


         break;
     }
     case (ColumnTypes.OBJECT): {
         boolean null1 = false;
         boolean null2 = false;
         Object ob1;
         Object ob2;
         ob1 = c1.getObject (row1);
         if (ob1 == null) {
             null1 = true;
         }
         ob2 = c2.getObject (row2);
         if (ob2 == null) {
             null2 = true;
         }
         if (null1) {
             if (null2) {
                 return 0;
             }
             return -1;
         }
         if (null2)
           return 1;
         if (ob1.equals (ob2))
           return 0;



                return compareStrings(c1.getString(row1), c2.getString(row2));

//vered: commented out - no need after the incorporation of basic 3-0 version.
//	  return t1.getString (row1, col1).compareTo (t2.getString (row2, col2));
     }
     case (ColumnTypes.BYTE): {
         byte[]b1 = new byte[1];
         b1[0] = c1.getByte (row1);
         byte[]b2 = new byte[1];
         b2[0] = c2.getByte (row2);
         it = compareBytes (b1, b2);
         break;
     }
     case (ColumnTypes.CHAR): {
         byte[]b1 = new byte[1];
         b1[0] = c1.getByte (row1);
         byte[]b2 = new byte[1];
         b2[0] = c2.getByte (row2);
         it = compareBytes (b1, b2);
         break;
     }
     default: {
         System.err.println ("TableUtilities:CompareVals: Error");
     }
   }
   return it;

  }//compare values of columns.

  /**
   * Return 0 if they
   * are the same, greater than zero if element is greater,
   * and less than zero if element is less.
   * Assumes columns are of same type.
   */
  public static int compareValues (Table t1, int row1, int col1, Table t2,
				   int row2, int col2)
  {

    int type = t1.getColumnType (col1);

    //the numeric case
    if (t1.isColumnNumeric (col1)) {
	double d1 = t1.getDouble (row1, col1);
	double d2 = t2.getDouble (row2, col2);
	if (d1 == d2) {
	    return 0;
        } else if (d1 > d2) {
	    return 1;
        } else {
	  return -1;
        }
      }

    int it = -2;

    //the other cases
    switch (type) {

      case (ColumnTypes.STRING): {

        it = compareStrings(t1.getString(row1, col1), t2.getString(row2, col2));
        break;
/*
        // Determine whether the strings are bin names,
         // because bin names need a special method to compare
         if ((t1.getString(row1, col1).indexOf("[")>=0 ||
               t1.getString(row1, col1).indexOf("]")>=0) &&
               (t2.getString(row2, col2).indexOf("[")>=0 ||
               t2.getString(row2, col2).indexOf("]")>=0) &&
               t1.getString(row1, col1).indexOf(":")>=0 &&
               t2.getString(row2, col2).indexOf(":")>=0) {
             it = compareBinNames(t1.getString (row1, col1), t2.getString (row2, col2));
             break;
         }
         else { // data type is regular string, not bin name
           it = t1.getString(row1, col1).compareTo(t2.getString(row2, col2));
           break;
         }
         */

	  /*//vered: was replaced by the above code from basic 3-0 version
           it = t1.getString (row1, col1).compareTo (t2.getString (row2, col2));
	  break;*/
      }
      case (ColumnTypes.CHAR_ARRAY): {
	  it = compareChars (t1.getChars (row1, col1), t2.getChars (row2, col2));
	  break;
      }
      case (ColumnTypes.BYTE_ARRAY): {
	  it = compareBytes (t1.getBytes (row1, col1), t2.getBytes (row2, col2));
	  break;
      }

      //VERED: 12-11-03
      //changed the case for boolean to return 0 if identical
      //otherwise - 1 if value at (row1,col1) is true. else -1.
      case (ColumnTypes.BOOLEAN): {


        boolean val1 = t1.getBoolean (row1, col1);
        boolean val2 = t1.getBoolean (row2, col2);
	  if ( val1 == val2) {
	      it = 0;
	  } else {
            if(val1)     it = 1;
            else it = -1;
	  }

	  break;
      }
      case (ColumnTypes.OBJECT): {
	  boolean null1 = false;
	  boolean null2 = false;
	  Object ob1;
	  Object ob2;
	  ob1 = t1.getObject (row1, col1);
	  if (ob1 == null) {
	      null1 = true;
	  }
	  ob2 = t2.getObject (row2, col2);
	  if (ob2 == null) {
	      null2 = true;
	  }
	  if (null1) {
	      if (null2) {
		  return 0;
              }
	      return -1;
	  }
	  if (null2)
	    return 1;
	  if (ob1.equals (ob2))
	    return 0;



          return compareStrings(t1.getString(row1, col1), t2.getString(row2, col2));

  //vered: copied form basic 3-0 version:
/*
                    // Determine whether the objects are bin names,
                    // because bin names need a special method to compare
                    if ((t1.getString(row1, col1).indexOf("[")>=0 ||
                         t1.getString(row1, col1).indexOf("]")>=0) &&
                        (t2.getString(row2, col2).indexOf("[")>=0 ||
                         t2.getString(row2, col2).indexOf("]")>=0) &&
                        t1.getString(row1, col1).indexOf(":")>=0 &&
                        t2.getString(row2, col2).indexOf(":")>=0) {
                        it = compareBinNames(t1.getString (row1, col1), t2.getString (row2, col2));
                        return it;
                    }
                    else { // data type is regular string, not bin name
                      it = t1.getString (row1, col1).compareTo (t2.getString (row2, col2));
                      return it;
                    }
*/
//vered: commented out - no need after the incorporation of basic 3-0 version.
//	  return t1.getString (row1, col1).compareTo (t2.getString (row2, col2));
      }
      case (ColumnTypes.BYTE): {
	  byte[]b1 = new byte[1];
	  b1[0] = t1.getByte (row1, col1);
	  byte[]b2 = new byte[1];
	  b2[0] = t2.getByte (row2, col2);
	  it = compareBytes (b1, b2);
	  break;
      }
      case (ColumnTypes.CHAR): {
	  byte[]b1 = new byte[1];
	  b1[0] = t1.getByte (row1, col1);
	  byte[]b2 = new byte[1];
	  b2[0] = t2.getByte (row2, col2);
	  it = compareBytes (b1, b2);
	  break;
      }
      default: {
	  System.err.println ("TableUtilities:CompareVals: Error");
      }
    }
    return it;
  }
  /**
   * Compare two byte arrays
   * @param b1 the first byte array to compare
   * @param b2 the second byte array to compare
   * @return -1, 0, 1
   */
  private static int compareBytes (byte[] b1, byte[] b2)
  {
    if (b1 == null) {
	if (b2 == null)
	  return 0;
	else
	  return -1;
    } else if (b2 == null) {
	return 1;
    }

    if (b1.length < b2.length) {
	for (int i = 0; i < b1.length; i++) {
	    if (b1[i] < b2[i])
	      return -1;
	    else if (b1[i] > b2[i])
	      return 1;
	}
	return -1;
    } else if (b1.length > b2.length) {
	for (int i = 0; i < b2.length; i++) {
	    if (b1[i] < b2[i])
	      return -1;
	    else if (b1[i] > b2[i])
	      return 1;
	}
	return 1;
    } else {
	for (int i = 0; i < b2.length; i++) {
	    if (b1[i] < b2[i])
	      return -1;
	    else if (b1[i] > b2[i])
	      return 1;
	}
	return 0;
    }
  }

 /**
  * Compare two char arrays
  * @param b1 the first char[] to compare
  * @param b2 the second char[] to compare
  * @return -1, 0, 1
  */
  private static int compareChars (char[] b1, char[] b2)
  {
    if (b1 == null) {
	if (b2 == null)
	  return 0;
	else
	  return -1;
    } else if (b2 == null) {
        return 1;
    }

    if (b1.length < b2.length) {
	for (int i = 0; i < b1.length; i++) {
	    if (b1[i] < b2[i])
	      return -1;
	    else if (b1[i] > b2[i])
	      return 1;
	}
	return -1;
    } else if (b1.length > b2.length) {
	for (int i = 0; i < b2.length; i++) {
	    if (b1[i] < b2[i])
	      return -1;
	    else if (b1[i] > b2[i])
	      return 1;
	}
	return 1;
    } else {
	for (int i = 0; i < b2.length; i++) {
	    if (b1[i] < b2[i])
	      return -1;
	    else if (b1[i] > b2[i])
	      return 1;
	}
	return 0;
    }
  }



  //vered: following methods are incorporated from basic 3-0 version:
  /**
     * Compare the minimum values of two bin names
     * @param b1 the first bin name to compare
     * @param b2 the second bin name to compare
     * @return -1, 0, 1
     */

    private static int compareBinNames (String b1, String b2)
    {
      double f1 = getBinMin(b1);
      double f2 = getBinMin(b2);
      if (f1 == f2)
        return 0;
      else if (f1 > f2)
        return 1;
      else if (f1 < f2)
        return -1;
      return -1;
    }

    /**
       * Get the minimum value from the bin name.
       * @param s the bin name
       * @return the minimum value from the bin name
       */
    private static double getBinMin(String s) {



      String minStr;
      int idx=0;
      for (int i=0; i<s.length(); i++) {
        if (s.charAt(i) == ':') {
          minStr = s.substring(1,i);



          // get rid of thousand comma
          String newStr="";
          for (int j=0; j<minStr.length(); j++) {
            if (minStr.charAt(j) != ',') {
              newStr = newStr + minStr.charAt(j);
            }
          }

          try {
            return Double.parseDouble(newStr);
          }
          catch (NumberFormatException e) {  // the number is negative infinity
            return Double.NEGATIVE_INFINITY;
          }
        }
      }
      return 0;  // should never reach here
    }


}

// Start QA Comments
// 7/1/03 - Ruth
//        Related to getScalarStatistics:
//        - Added tests for cases where fewer than 4 values in column.
//          Previously could get divide by zero and array index out of bounds errors.
//        - Removed max and min which can be easily found from sorted array so no need to
//          track while list being built.
//        - Corrected algorithm for computing quartiles and added more documentation
//          about which method of many possible ones were used.   Original code seemed
//          not to take into account that d indexed from 0, in addition to causing
//          errors when number of samples was small.
//        - Created new private method _computeScalarStatistics and moved bulk of
//          code there so it can be shared by 2 getScalarStatistics methods.
//        - Changed test on flags in method getScalarStatistics that only includes
//          rows specified by the flags.  Before row was included if corresponding flags
//          value was false.  I think it should be the reverse behavior (include if flags
//          true.
//        Related to other methods:
//        - median:  Corrected off-by-one error in indexing.
//        - stddev:  Changed to use variance method rather than duplicating code.
//        - several places: Deleted code that was commented out and removed code that wasn't
//          needed - seems several cut/paste into methods and many values computed but never used.
//        - tried to make formatting more uniform - in part because I accidently typed some
//          key combination while editing that messed it all up and I didn't want to lose my code
//          changes.
// WISH:  All methods need to be extended to work with missing and empty values and to be careful
//        about tables with few or no entries.
//
// END QA Comments
