package ncsa.d2k.modules.core.discovery.ruleassociation;

import ncsa.d2k.modules.core.datatype.table.*;
import java.io.*;
import java.util.*;

/**
 * This class holds sets of items for ruleassociation. Each set is actually
 * an example containing a list of items the set contains. The sets
 * are represented as integers, with another string array containing the actual
 * bined string representation of the int. This is much more compact than
 * a vertical table typically.
 *
 * Only items that will appear in final rules are included in the output.
 * Those items are the ones for which the attribute is a possible rule
 * antecedent or consequent.
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */
public class ItemSets implements Serializable {
	/** number of examples. */
	public int numExamples;

	/** this array contains a list of attribute names of target attributes. */
	public String [] targetNames = null;

	/** this array contains a list of attribute indices of target attributes. */
	public int [] targetIndices = null;

	/** for each unique item, this hashtable contains it's frequency count and it's
	 *  order in terms of frequency. */
	public HashMap unique = new HashMap ();

	/** this is the list of unique attribute value names ordered by frequency. */
	public String [] names;

	/** for each example contains a boolean array with an entry for each item,
	 *  set to true only if the item is represented in the example or not.  */
	protected boolean [][] itemFlags;

	/** holds some method specific data.*/
	public Object userData;

        public ItemSets(){}

	public ItemSets(Table vt) {
		// number of cols and rows in original table
		int numColumns = vt.getNumColumns();
		int numRows = this.numExamples = vt.getNumRows ();

		// number of Attributes we'll care about & place for them
		int numAttributes = 0;
		int [] attributes = null;

		boolean isExampleTable = vt instanceof ExampleTable;

		// Size and populate attributes to hold the features that are of
		// interest in the table. Also build the list of target feature
		// names.
		// If we have an example table there are 4 possibilities:
		//	1) inputs and outputs chosen, make sure no duplicates
		//         in attributes array. target names = output names
		//	2) inputs but no outputs chosen, treat as if outputs same
		//	   as inputs. size attributes array to hold inputs and
		//	   populate.  target names = input names
		//	3) outputs chosen but no inputs (very weird), treat as
		//	   if inputs same as outputs.   size attributes array to
		//	   hold outputs and populate.   target names = output names
		// 	4) no inputs or outputs chosen, use all columns as inputs
		//	   and outputs.  Size attributes same as number of Columns
		//	   and set target names = output names.
		// If we don't have an example table, behavior same as 4 above.
		//

		if (isExampleTable) {
			ExampleTable et = (ExampleTable)vt;
			int [] inputs = et.getInputFeatures ();
			int [] outputs = et.getOutputFeatures ();
			int inCnt = inputs.length;
			int outCnt = outputs.length;

			// Example Table case 1
			if ( inCnt > 0 && outCnt > 0 ) {
			   int uniqCnt = 0;
			   boolean [] uniqFeatures = new boolean [numColumns]; // max uniq we'll have is all

			   for ( int i = 0; i < inCnt; i++ ) {
			      if ( ! uniqFeatures[ inputs[i] ] ) {
				 uniqFeatures[ inputs[i] ] = true;
				 uniqCnt++;
			      }
			   }
			   for ( int i = 0; i < outCnt; i++ ) {
			      if ( ! uniqFeatures[ outputs[i] ] ) {
				 uniqFeatures[ outputs[i] ] = true;
				 uniqCnt++;
			      }
			   }

			   numAttributes = uniqCnt;
			   attributes = new int [numAttributes];
			   int attIdx = 0;
			   for ( int i = 0; i < numColumns; i++ ) {
			      if ( uniqFeatures[i] ) {
				 attributes[attIdx++] = i;
			      }
			   }
			   targetNames = new String [outCnt];
			   for (int i = 0 ; i < outCnt; i++) {
				targetNames[i] = vt.getColumnLabel( outputs[i] );
			   }

			// Example Table case 2
		        } else if ( inCnt > 0 && outCnt == 0 ) {
			   numAttributes = inCnt;
			   attributes = new int [ numAttributes ];
			   System.arraycopy(inputs, 0, attributes, 0, inCnt);

			   targetNames = new String [inCnt];
			   for (int i = 0 ; i < inCnt; i++) {
				targetNames[i] = vt.getColumnLabel( inputs[i] );
			   }

			// Example Table case 3
			} else if ( inCnt == 0 && outCnt > 0 ) {
			   numAttributes = outCnt;
			   attributes = new int [ numAttributes ];
			   System.arraycopy(outputs, 0, attributes, 0, outCnt);

			   targetNames = new String [outCnt];
			   for (int i = 0 ; i < outCnt; i++) {
				targetNames[i] = vt.getColumnLabel( outputs[i] );
			   }

			// Example Tablee case 4
			} else if ( inCnt == 0 && outCnt == 0 ) {
			   numAttributes = numColumns;
			   attributes = new int [ numAttributes ];
			   targetNames = new String [ numColumns ];

		           for (int i = 0; i < numColumns; i++ ) {
			      attributes[i] = i;
			      targetNames[i] = vt.getColumnLabel( i );
			   }
			}

		} else {
		        // Not an Example Table
			numAttributes = numColumns;
			attributes = new int [numAttributes];
			targetNames = new String [numColumns];

			for (int i = 0; i < numColumns; i++) {
			   attributes[i] = i;
			   targetNames[i] = vt.getColumnLabel( i );
			}
		}

		// Allocate an array of string for each attribute column prefix.
		String [] prefix = new String [numAttributes];

		// Init each prefix, if there is no column label, use our own
		// home brew.
		for (int i = 0 ; i < numAttributes ; i++) {
			String tmp = vt.getColumnLabel( attributes[i] );
			prefix [i] = tmp + "^";
		}

		/** Construct the table containing the unique attributes and their
		 *  counts. */

		int counter = 0;
		char [] chars = new char [1024];
		ArrayList set = new ArrayList();
		for (int i = 0 ; i < numRows ; i++) {
			for (int j = 0 ; j < numAttributes ; j++) {
				String a = prefix[j];
				int alen = a.length();
				String b = vt.getString (i, attributes[j]);
				int blen = b.length();
				if ((alen+blen) > chars.length)
					chars = new char [alen+blen];
				a.getChars(0,alen,chars,0);
				b.getChars(0,blen,chars,alen);
				String item_desc = new String(chars,0,alen+blen);
				int [] cnt_and_id = (int []) unique.get (item_desc);
				if (cnt_and_id == null) {
					cnt_and_id = new int [2];
					cnt_and_id[0] = 1;
					cnt_and_id[1] = counter++;
					unique.put (item_desc, cnt_and_id);
					set.add(cnt_and_id);
				} else
					cnt_and_id[0]++;
			}
		}

		// create the arrays to sort.
		int [][] vals = new int [set.size()][];
		for (int i = 0 ; i < set.size();i++) vals[i] = (int[])set.get(i);
		int [] ind = new int [set.size()];
		for (int i = 0 ; i < set.size() ; i++) ind[i] = i;
		this.quickSort (ind, vals, 0, ind.length-1);
		for (int i = 0 ; i < ind.length ; i++) {
			vals[ind[i]][1] = i;
		}

		// Now we have the order, the index value in the int array keyed by
		// name in the unique names hashmap is set correctly. Now we will just
		// create an array of unique names in the order sorted by frequency.
		names = new String [unique.size()];
		Iterator enum = unique.values().iterator();
		Iterator enum2 = unique.keySet().iterator();
		while (enum.hasNext ()) {
			int [] tmp = (int[]) enum.next ();
			String tmpName = (String) enum2.next ();
			names[tmp[1]] = tmpName;
		}

		// Now construct the new representation of the vertical table, where each
		// entry is represented by an integer.
		int [][] documents = new int [numRows][numAttributes];
		for (int i = 0 ; i < numRows ; i++) {
			for (int j = 0 ; j < numAttributes ; j++) {
				String a = prefix[j];
				int alen = a.length();
				String b = vt.getString (i, attributes[j]);
				int blen = b.length();
				if ((alen+blen) > chars.length)
					chars = new char [alen+blen];
				a.getChars(0,alen,chars,0);
				b.getChars(0,blen,chars,alen);
				String name = new String(chars,0,blen+alen);
				int [] pz = (int[])unique.get(name);
				documents[i][j] = pz[1];
			}
		}

		//////////////////////
		// First, set up the item flags, set the bit associated with each
		// element in each set.
		// this boolean array has a flag for each possible item for each document to
		// indicate if the document contains the item or not.
		itemFlags = new boolean [numRows][unique.size()];
		for (int i = 0 ; i < numRows ; i++)
			for (int j = 0 ; j < numAttributes ; j++)
				itemFlags[i][documents[i][j]] = true;

		// Figure out the indices of those items that are targets
		Iterator keys = unique.keySet().iterator();
		Iterator indxs = unique.values().iterator();
		ArrayList list = new ArrayList ();

		// for each target attributes, see if the inputs include the attribute.
		if (targetNames != null) {
			while (keys.hasNext ()) {
				String name = (String) keys.next ();
				int[] indx = (int[]) indxs.next ();
				for (int i = 0 ; i < targetNames.length; i++) {
					if (name.startsWith (targetNames[i])) {
						list.add (indx);
                                        }
                                }
			}
                }

		// Put the indexes of the targets into the list.
		int size = list.size ();
		if (size != 0) {
			targetIndices = new int [size];
			for (int i = 0 ; i < size ; i++)
				targetIndices[i] = ((int[])list.get (i))[1];
		}

	}

	/**
		Perform a quicksort on the data using the Tri-median method to select the pivot.
		@param l the first rule.
		@param r the last rule.
	*/
	protected void quickSort(int [] ind, int [][] vals, int l, int r) {

		int pivot = (r + l) / 2;
		int pivotVal = vals[ind[pivot]][0];

		// from position i=l+1 start moving to the right, from j=r-2 start moving
		// to the left, and swap when the fitness of i is more than the pivot
		// and j's fitness is less than the pivot
		int i = l;
		int j = r;
		while (i <= j) {
			while ((i < r) && (vals[ind[i]][0] > pivotVal))
				i++;
			while ((j > l) && (vals[ind[j]][0] < pivotVal))
				j--;
			if (i <= j) {
				int swap = ind[i];
				ind[i] = ind[j];
				ind[j] = swap;
				i++;
				j--;
			}
		}

		// sort the two halves
		if (l < j)
			quickSort(ind, vals, l, j);
		if (i < r)
			quickSort(ind, vals, j+1, r);
	}

	/**
	 * Returns an 2D array of booleans with one row for each example, and one bit
	 * in each row for each possible item. The bit is set if the item exists in the
	 * set.
	 * @return a 2D array of booleans indicating if an item was purchased or not for each example.
	 */
	public boolean [][] getItemFlags () {
		return itemFlags;
	}

}


// Start QA Comments
//  2/28/03 - Recv from Tom
//  3/*/03  - Ruth, with help from Loretta, starts QA
//          - Several iterations and updates by Tom and Ruth so that
//            it correctly handles Tables (as advertised), handles
//	      ExampleTables with no ins or outs, and handles Example
//	      Tables where some, but not all, attributes used as both
//            Ins and Outs.
// 3/20/03  - Handling of blank column labels now done in parsers to
//            no need to do here any longer.
//	    - Ready for Basic
// End QA Comments
