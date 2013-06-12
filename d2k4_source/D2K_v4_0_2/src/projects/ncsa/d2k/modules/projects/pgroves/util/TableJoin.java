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

public class TableJoin extends DataPrepModule 
	{

	//////////////////////
	//d2k Props
	////////////////////
	
	boolean debug=true;		
	/////////////////////////
	/// other fields
	////////////////////////


	//////////////////////////
	///d2k control methods
	///////////////////////<F11>

	public boolean isReady(){
		return super.isReady();
	}
	public void endExecution(){
		super.endExecution();
	}
	public void beginExecution(){
		super.beginExecution();
	}
	
	/////////////////////
	//work methods
	////////////////////
	/*
		does it
	*/
	public void doit() throws Exception{
		int[] joinCols1=(int[])pullInput(0);
		int[] joinCols2=(int[])pullInput(1);
		
		Table t1=(Table)pullInput(2);
		Table t2=(Table)pullInput(3);

		TableImpl jt=makeJoinTable(	t1,t2,
									findMatches(t1,t2,joinCols1,joinCols2),
									joinCols1,joinCols2);
		if(debug){
			System.out.println(getAlias()+": Join Table Made");
		}
		pushOutput(jt, 0);
		
	}

	/**
		takes in two tables an arraylist, which contains
		int[] of size 2 that hold indices of matching
		rows from the two tables. This creates a new
		table where all the matching pairs become new
		rows that contain the fields they have in common
		and then all the other fields of table 1 and all
		the fields of table 2.

		@param t1 the first table
		@param t2 the second table
		@param matches and arraylist filled w/ int[2] where matches[0] is 
						an index in t1 and matches[1] is and index in t2
		@param joinColumns1 the columns that t1 was matched with t2 by
		@param joinColumns2 the columns that t2 was matched with t1 by

		@return a TableImpl w/ the first columns being the join columns,
				followed by the other columns of table 1, then those 
				of table 2
	**/
		
						
	protected TableImpl makeJoinTable(	Table t1, Table t2, 
									ArrayList matches,
									int[] joinColumns1,
									int[] joinColumns2){
		
		int numRows=matches.size();
		int numCols=t1.getNumColumns()+t2.getNumColumns()-joinColumns1.length;
		
		int[] subset1=new int[numRows];
		int[] subset2=new int[numRows];

		int[] match;
		for(int i=0; i<numRows; i++){
			match=(int[])matches.get(i);
			subset1[i]=match[0];
			subset2[i]=match[1];
		}
		
		MutableTableImpl joinTable=new MutableTableImpl(numCols);

		int currentCol=0;
		//the join cols
		int numJoinCols=joinColumns1.length;
		
		for(int i=0; i<numJoinCols;i++){
			joinTable.setColumn(
				ColumnUtilities.createColumnSubset(	
					t1, joinColumns1[i], subset1),
				currentCol);
			currentCol++;
		}
		//table 1 columns
		int numCols1=t1.getNumColumns();
		boolean isJoinCol=false;
		int j;
		for(int i=0; i<numCols1;i++){
			isJoinCol=false;
			for(j=0; j<numJoinCols; j++){
				if(joinColumns1[j]==i){
					isJoinCol=true;
				}
			}
			if(!isJoinCol){
				joinTable.setColumn(
					ColumnUtilities.createColumnSubset(	
						t1, i, subset1),
					currentCol);
				currentCol++;
			}
		}
		//table 2 columns
		int numCols2=t2.getNumColumns();
		for(int i=0; i<numCols2;i++){
			isJoinCol=false;
			for(j=0; j<numJoinCols; j++){
				if(joinColumns2[j]==i){
					isJoinCol=true;
				}
			}
			if(!isJoinCol){
				joinTable.setColumn(
					ColumnUtilities.createColumnSubset(	
						t2, i, subset2),
					currentCol);
				currentCol++;
			}
		}
		return joinTable;
	}
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
		
		ArrayList matches=new ArrayList();

		int outerSize=t1.getNumRows();
		int innerSize=t2.getNumRows();
		Table outer=t1;
		Table inner=t2;

		int numMatchingCols=joinColumns1.length;
		
		int comp;
		boolean areMatching;
		int[] matchPair;
		if(debug){
			System.out.println("OuterSize:"+outerSize+" InnerSize:"+innerSize);
		}
		for(int i=0; i<outerSize; i++){
			System.out.println(i);
			for(int j=0; j<innerSize;j++){
				areMatching=true;
				for(int k=0;k<numMatchingCols;k++){
					if(0!=compareValues(outer,i,joinColumns1[k],
										inner,j,joinColumns2[k])){
						areMatching=false;
						break;
					}
				}
				if(areMatching){
					matchPair=new int[2];
					matchPair[0]=i;
					matchPair[1]=j;
					matches.add(matchPair);
				}
			}
		}
		if(debug){
			System.out.println("Matches:");
			
			for(int i=0; i<matches.size();i++){
				int[] ar=(int[])matches.get(i);
				System.out.println(ar[0]+","+ar[1]);
			}
			System.out.println();
		}
		return matches;
	}
	/**
		Return 0 if they
    	are the same, greater than zero if element is greater,
	 	and less than zero if element is less.

		*/

	public static int compareValues(Table t1, int row1, int col1,
									Table t2, int row2, int col2){
		
		int type=t1.getColumnType(col1);
		
		//the numeric case
		if(t1.isColumnNumeric(col1)){
			double d1=t1.getDouble(row1,col1);
			double d2=t2.getDouble(row2,col2);
			if(d1==d2)
				return 0;
			if(d1>d2)
				return 1;

			return -1;
		}
		
		int it=-2;
		//the other cases	
		switch(type){
			/*case (ColumnTypes.INTEGER) : {

				break;
			}
			case (ColumnTypes.FLOAT) : {
				break;
			}
			case (ColumnTypes.SHORT) : {
				break;
			}
			case (ColumnTypes.LONG) : {
				break;
			}
			*/
			case (ColumnTypes.STRING) : {
				it=t1.getString(row1,col1).
						compareTo(t2.getString(row2,col2));
				break;		
			}
			case (ColumnTypes.CHAR_ARRAY) : {
				it=compareChars(t1.getChars(row1,col1),
								t2.getChars(row2,col2));
				break;
			}
			case (ColumnTypes.BYTE_ARRAY) : {
				it=compareBytes(t1.getBytes(row1,col1),
								t2.getBytes(row2,col2));
				break;
			}
			case (ColumnTypes.BOOLEAN) : {
				if(t1.getBoolean(row1,col1)==t2.getBoolean(row2,col2)){
					it=0;
				}else{
					it=1;
				}
				
				break;
			}
			case (ColumnTypes.OBJECT) : {
				boolean null1=false;
				boolean null2=false;
				Object ob1;
				Object ob2;
				ob1=t1.getObject(row1,col1);
				if(ob1==null){
					null1=true;
				}
				ob2=t2.getObject(row2,col2);
				if(ob2==null){
					null2=true;
				}
				if(null1){
					if(null2){
						return 0;
					}
					return -1;
				}
				if(null2)
					return 1;
				if(ob1.equals(ob2))
					return 0;

				return t1.getString(row1,col1).
						compareTo(t2.getString(row2,col2));
			}
			case (ColumnTypes.BYTE) : {
				byte[] b1 = new byte[1];
				b1[0] = t1.getByte(row1,col1);
				byte[] b2 = new byte[1];
				b2[0] = t2.getByte(row2,col2);
				it= compareBytes(b1, b2);
				break;
			}
			case (ColumnTypes.CHAR) : {
				byte[] b1 = new byte[1];
				b1[0] = t1.getByte(row1,col1);
				byte[] b2 = new byte[1];
				b2[0] = t2.getByte(row2,col2);
				it= compareBytes(b1, b2);
				break;
			}/*
			case (ColumnTypes.DOUBLE) : {
				break;

			}*/
			default : {
				System.err.println("ColumnUtilities:CopyColumn: "+
								"Invalid Column Type");
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
    private static int compareBytes (byte[] b1, byte[] b2) {
        if (b1 == null) {
            if (b2 == null)
                return  0;
            else
                return  -1;
        }
        else if (b2 == null)
            return  1;
        if (b1.length < b2.length) {
            for (int i = 0; i < b1.length; i++) {
                if (b1[i] < b2[i])
                    return  -1;
                else if (b1[i] > b2[i])
                    return  1;
            }
            return  -1;
        }
        else if (b1.length > b2.length) {
            for (int i = 0; i < b2.length; i++) {
                if (b1[i] < b2[i])
                    return  -1;
                else if (b1[i] > b2[i])
                    return  1;
            }
            return  1;
        }
        else {
            for (int i = 0; i < b2.length; i++) {
                if (b1[i] < b2[i])
                    return  -1;
                else if (b1[i] > b2[i])
                    return  1;
            }
            return  0;
        }
    }
		
	    /**
	 * Compare two char arrays
     * @param b1 the first char[] to compare
     * @param b2 the second char[] to compare
     * @return -1, 0, 1
     */
    private static int compareChars (char[] b1, char[] b2) {
        if (b1 == null) {
            if (b2 == null)
                return  0;
            else
                return  -1;
        }
        else if (b2 == null)
            return  1;
        if (b1.length < b2.length) {
            for (int i = 0; i < b1.length; i++) {
                if (b1[i] < b2[i])
                    return  -1;
                else if (b1[i] > b2[i])
                    return  1;
            }
            return  -1;
        }
        else if (b1.length > b2.length) {
            for (int i = 0; i < b2.length; i++) {
                if (b1[i] < b2[i])
                    return  -1;
                else if (b1[i] > b2[i])
                    return  1;
            }
            return  1;
        }
        else {
            for (int i = 0; i < b2.length; i++) {
                if (b1[i] < b2[i])
                    return  -1;
                else if (b1[i] > b2[i])
                    return  1;
            }
            return  0;
        }
    }

	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return "<html>  <head>      </head>  <body>    Does a database-style 'join' on two tables based on equivalency of the     inputted join column indices. That is, for every row in table 1 where the     values from the table 1 join columns are equal to the values in table 2's     join columns, a new row is created w/ the first columns being thejoin     columns, followed by the non-join columns of table 1, then the non-join     columns of table 2. Corresponding join indices should therefore correspond     to equivalent fields  </body></html>";
	}
	
   	public String getModuleName() {
		return "Nested Loop Join";
	}
	public String[] getInputTypes(){
		String[] types = {"[I","[I","ncsa.d2k.modules.core.datatype.table.Table","ncsa.d2k.modules.core.datatype.table.Table"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: return "The columns of table 1 that will be matched to columns of table 2";
			case 1: return "The columns of table 2 that will be matched to columns of table 1";
			case 2: return "Table One";
			case 3: return "Table Two";
			default: return "No such input";
		}
	}
	
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Join Columns 1";
			case 1:
				return "Join Columns 2";
			case 2:
				return "Table 1";
			case 3:
				return "Table 2";
			default: return "NO SUCH INPUT!";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {"ncsa.d2k.modules.core.datatype.table.Table"};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: return "The table created by the join";
			default: return "No such output";
		}
	}
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "Join Table";
			default: return "NO SUCH OUTPUT!";
		}
	}		
	////////////////////////////////
	//D2K Property get/set methods
	///////////////////////////////
	public void setDebug(boolean b){
		debug=b;
	}
	public boolean getDebug(){
		return debug;
	}
	/*
	public boolean get(){
		return ;
	}
	public void set(boolean b){
		=b;
	}
	public double  get(){
		return ;
	}
	public void set(double d){
		=d;
	}
	public int get(){
		return ;
	}
	public void set(int i){
		=i;
	}

	public String get(){
		return ;
	}
	public void set(String s){
		=s;
	}
	*/
}
			
					

			

								
	
