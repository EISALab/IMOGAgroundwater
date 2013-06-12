package ncsa.d2k.modules.projects.pgroves.util;


import java.util.ArrayList;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

/*
	takes the indices of columns that the user wishes to join
	on and does a nested loop join based on equivalency of
	those attributes.

	@author pgroves
	*/

public class SMTableJoin extends TableJoin 
	{

	/////////////
	///d2k props
	////////////
	boolean table1IsPresorted=false;
	boolean table2IsPresorted=false;
	/**
		does a nested loop join on tables t1 and t2.
		this is likely the function that would be overridden 
		if you wanted to implement a more efficient or data-specific
		join method

		@param t1 the first table
		@param t2 the second table
		@param joinColumns1 the columns that t1 was matched with t2 by
		@param joinColumns2 the columns that t2 was matched with t1 by
		

		@return an arraylist that holds a size 2 int array (int[2]) for
				each matching set of rows (those that should be joined)
	**/
	protected ArrayList findMatches(Table t1, Table t2, 
									int[] joinColumns1,
									int[] joinColumns2) {
		

		int outerSize=t1.getNumRows();
		int innerSize=t2.getNumRows();
		Table outer=t1;
		Table inner=t2;

		int numMatchingCols=joinColumns1.length;
		
		int comp;
		boolean areMatching;
		int[] matchPair;
		/*if(debug){
			System.out.println("OuterSize:"+outerSize+" InnerSize:"+innerSize);
		}*/
		//////////////////////////////////////////
		///this is where this subclass changes
		/////////////////////////////////////////

		int[] sorted1;
		if(!table1IsPresorted){
			sorted1=multiSortIndex(t1, joinColumns1);
		}else{
			int size=t1.getNumRows();
			sorted1=new int[size];
			for(int i=0; i<size; i++){
				sorted1[i]=i;
			}
		}
		if(debug){
			System.out.println(getAlias()+": Table 1 Sorted");
		}
		int[] sorted2;
		if(!table2IsPresorted){
			sorted2=multiSortIndex(t2, joinColumns2);
		}else{
			int size=t2.getNumRows();
			sorted2=new int[size];
			for(int i=0; i<size; i++){
				sorted2[i]=i;
			}
		}

		
		if(debug){
			System.out.println(getAlias()+": Table 2 Sorted");
		}


		ArrayList matches=merge(t1,t2, sorted1, sorted2, 
								joinColumns1, joinColumns2);
		if(debug){
			System.out.println(getAlias()+":"+matches.size()+" Matches Found");
		}

		


		//////////////////////////////////////////
		//end subclass changes
		//////////////////////////////////////////
		/*if(debug){
			System.out.println("Matches:"+matches.size());
			
			for(int i=0; i<10/*matches.size()/;i++){
				int[] ar=(int[])matches.get(i);
				System.out.println(ar[0]+","+ar[1]);
			}
			System.out.println();
		}*/
		return matches;
	}

	/**
		takes a table and an array of column indices to sort by.
		The resulting array of row indices will contain row pointers
		ordered such that rows are ordered by the first column index,
		then the second, etc.

		@param tbl the table to multisort
		@param sortByCols the columns to sort by

		@return an int[] of size tbl.getNumRows() representing the 
				sorted order of tbl
	**/
	public static int[] multiSortIndex(Table tbl, int[] sortByCols){
		int size=tbl.getNumRows();
		int[] order=new int[size];
		for(int i=0; i<size; i++){
			order[i]=i;
		}
		
		multiQuickSort(tbl, sortByCols, order, 0, size-1);
		/*if(true){//debug){
			System.out.println("QuickSort:");
			for(int i=0; i<order.length;i++){
				System.out.print(order[i]+": ");
				for(int j=0;j<tbl.getNumColumns();j++){
					try{
						System.out.print(tbl.getString(order[i],j)+",");
					}catch (Exception e){
						System.out.print(",");
					}
				}
				System.out.println();
			}
			System.out.println();
		}*/
		multiInsertionSort(tbl, sortByCols, order);
		/*if(debug){
			System.out.println("InsertionSort");
				for(int i=0; i<1/*order.length/;i++){
				System.out.print(order[i]+": ");
				for(int j=0;j<tbl.getNumColumns();j++){
					try{
						System.out.print(tbl.getString(order[i],j)+",");
					}catch (Exception e){
						System.out.print(",");
					}
				}
				System.out.println();
			}
			System.out.println();
		}*/
		return order;
	}

	private static void multiQuickSort(	Table tbl, int[] sortByCols, int[] order, 
							int l, int r){

		if(r-l<=3){
			return;
		}
		int pivot;

		int i=(r+l)/2;		

		if(compareMultiCols(tbl,tbl,order[l],order[i],sortByCols,sortByCols)>0)
			swap(order, l, i);
		if(compareMultiCols(tbl,tbl,order[l],order[r],sortByCols,sortByCols)>0)
			swap(order, l, r);
		if(compareMultiCols(tbl,tbl,order[i],order[r],sortByCols,sortByCols)>0)
			swap(order, i, r);

		swap(order, i, r-1);

		pivot=r-1;			
		
		i=l+1;	
		int j=r-2;

		while(j>i){

			while(	(compareMultiCols(tbl,tbl,order[i],
						order[pivot],sortByCols,sortByCols)<=0)
						&& (i<j)){
				i++;
			}
			while(	(compareMultiCols(tbl,tbl,order[j],
						order[pivot],sortByCols,sortByCols)>=0)
						&& (i<j)){
				j--;
			}
			
			if(i<j){
				swap(order, i, j);	
			}	
		}
		swap(order, r-1, j);

		multiQuickSort(tbl, sortByCols, order, l, i-1);
		multiQuickSort(tbl, sortByCols, order, j+1, r);
	}

	public static void multiInsertionSort(	Table tbl, int[] sortByCols, 
											int[] order){

		int size=order.length;
		int i;
		int j;
		int v;
		for(j=1; j<size; j++){
			i=j-1;
			v=order[j];
			while((i>=0)&&(0<compareMultiCols(tbl,tbl,order[i],
								v,sortByCols,sortByCols))){
				order[i+1]=order[i];
				i--;
			}
			order[i+1]=v;
		}
	}

		

	/**
		does the 'merge' of a sort merge join. takes two tables and
		an ordered index for each and produces an array list containing
		row numbers from the two tables that matched
		
		@param t1 Table One
		@param t2 Table Two
		@param order1 the sorted order of table one
		@param order2 the sorted order of table two
		@param joinCols1 the column indices of t1 to merge on
		@param joinCols2 the column indices of t2 to merge on
		
		@return an arraylist where every entry is an int[] of size 2, where
				the zero'eth index corresponds to a row index of table 1 and
				the one'th index is a row in table 2. the two rows match
	**/
	public ArrayList merge(	Table t1, Table t2, 
							int[] order1, int[] order2,
							int[] joinCols1, int[] joinCols2){

		ArrayList matches=new ArrayList();
		
		int tr=0;
		int ts=0;
		int gs=0;
		
		int size1=order1.length;
		int size2=order2.length;
		//System.out.println("size1:"+size1+", size2:"+size2);

		int[] match;
		while((tr<size1)&&(gs<size2)){
			
			while((tr<(size1-1))&&
					(0>compareMultiCols(t1,t2,order1[tr],
							order2[gs],joinCols1,joinCols2))){
				tr++;
			}
			//if(tr==size1)
			//	break;
			while((gs<size2)&&
					(0<compareMultiCols(t1,t2,order1[tr],
							order2[gs],joinCols1,joinCols2))){
				gs++;
			}

			ts=gs;
			if(gs<size2){
			while(	(tr<size1)&&
					(0==compareMultiCols(t1,t2,order1[tr],
							order2[gs],joinCols1,joinCols2))){
				ts=gs;
				//System.out.println("\t tr"+tr);


				while((ts<size2)&&
					(0==compareMultiCols(t1,t2,order1[tr],
							order2[ts],joinCols1,joinCols2))){
					match=new int[2];
					match[0]=order1[tr];
					match[1]=order2[ts];
					matches.add(match);
					ts++;
					//System.out.println("\t\t ts"+ts);
				}
				tr++;
			}
			}
			gs=ts;
		}
		return matches;
	}

	/**
		if the value of the row in table 1 is greater, returns 1.
		if they are equal returns 0
		if less than, -1

		e1>e2 -> 1
		e1=e2 -> 0
		e1<e2 -> -1
	**/
	public static int compareMultiCols(Table t1, Table t2, int row1, int row2,
								int[] cols1, int[] cols2){
		
		int numCompareCols=cols1.length;
		int eq=0;
		for(int i=0; i<numCompareCols; i++){
			eq=compareValues(t1,row1,cols1[i],t2,row2,cols2[i]);
			if(eq!=0){
				return eq;
			}
		}
		return 0;
	}

	private static void swap(int[] ar, int i, int j){
		int t=ar[i];
		ar[i]=ar[j];
		ar[j]=t;
	}


	/////////////////
	///d2k prop get/set
	///////////////////
	public boolean getTable2IsPresorted(){
		return table2IsPresorted;
	}
	public void setTable2IsPresorted(boolean b){
		table2IsPresorted=b;
	}
	public boolean getTable1IsPresorted(){
		return table1IsPresorted;
	}
	public void setTable1IsPresorted(boolean b){
		table1IsPresorted=b;
	}

	

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "SMTableJoin";
	}

	/**
	 * Return the human readable name of the indexed input.
	 * @param index the index of the input.
	 * @return the human readable name of the indexed input.
	 */
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "input0";
			case 1:
				return "input1";
			case 2:
				return "input2";
			case 3:
				return "input3";
			default: return "NO SUCH INPUT!";
		}
	}

	/**
	 * Return the human readable name of the indexed output.
	 * @param index the index of the output.
	 * @return the human readable name of the indexed output.
	 */
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "output0";
			default: return "NO SUCH OUTPUT!";
		}
	}
}
			
					

			

								
	
