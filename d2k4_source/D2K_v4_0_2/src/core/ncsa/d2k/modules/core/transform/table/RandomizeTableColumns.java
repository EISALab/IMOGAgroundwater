package ncsa.d2k.modules.core.transform.table;


import ncsa.d2k.core.modules.*;
import java.util.*;
import java.io.Serializable;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
/**
   RandomizeColumns takes a Table and randomly rearranges the
   columns.
   @author David Clutter
*/
public class RandomizeTableColumns extends DataPrepModule  {


  //////////////////
  //  PROPERTIES  //
  //////////////////

    /**
       Return a description of the function of this module.
       @return A description of this module.
    */
    public String getModuleInfo() {
		return "<html>  <head>      </head>  <body>    Randomly rearranges the columns of a Table. A Column is chosen at random     and inserted into a new table, until all Columns have been inserted.  </body></html>";
	}

    /**
       Return the name of this module.
       @return The name of this module.
    */
    public String getModuleName() {
		return "RandomizeColumns";
    }

    /**
       Return a String array containing the datatypes the inputs to this
       module.
       @return The datatypes of the inputs.
    */
    public String[] getInputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.MutableTable"};
		return types;
	}

    /**
       Return a String array containing the datatypes of the outputs of this
       module.
       @return The datatypes of the outputs.
    */
    public String[] getOutputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.MutableTable"};
		return types;
	}

    /**
       Return a description of a specific input.
       @param i The index of the input
       @return The description of the input
    */
    public String getInputInfo(int i) {
		switch (i) {
			case 0: return "The Table to randomize.";
			default: return "No such input";
		}
	}

    /**
       Return the name of a specific input.
       @param i The index of the input.
       @return The name of the input
    */
    public String getInputName(int i) {
		return "table";
    }

    /**
       Return the description of a specific output.
       @param i The index of the output.
       @return The description of the output.
    */
    public String getOutputInfo(int i) {
		switch (i) {
			case 0: return "The randomized table.";
			default: return "No such output";
		}
	}

    /**
       Return the name of a specific output.
       @param i The index of the output.
       @return The name of the output
    */
    public String getOutputName(int i) {
    	return "randomizedTable";
    }

    /**
       Perform the calculation.
    */
    public void doit() {
    	//MutableTable vt = (MutableTable)pullInput(0);
		//int columns = vt.getNumColumns();

		// create a new, empty table
		/*TableImpl newVt = (TableImpl)DefaultTableFactory.getInstance().createTable(vt.getNumColumns());
		newVt.setLabel(vt.getLabel());
		newVt.setComment(vt.getComment());
		int colNum = 0;

        Random r = new Random(Seed);
		while(vt.getNumColumns() > 0) {
			// choose a random column
			int col = r.nextInt(vt.getNumColumns());
			Column c = vt.getColumn(col);
			// remove the column from the original table
			vt.removeColumn(col);
			// put the column into the new table
			newVt.setColumn(c, colNum);
			colNum++;
		}
		pushOutput(newVt, 0);
		*/

		rand = new Random(seed);

		MutableTable table = (MutableTable) pullInput(0);
		int columns = table.getNumColumns();
		int j = 0;
		for (int i=0; i<columns; i++){
			j = getRandomNumber(i, columns-1);
			table.swapColumns(i,j);
		}
		pushOutput(table, 0);
	}

	private int seed = 345;

	public void setSeed(int x){
		seed = x;
	}

	public int getSeed(){
		return seed;
	}

	private boolean useSeed = true;

	public void setUseSeed(boolean b) {
		useSeed = b;
	}

	public boolean getUseSeed() {
		return useSeed;
	}

	private transient Random rand;

	/**
		Chooses a random integer between two integers (inclusive)
		@param int m - the lower integer
		@param int n - the higher integer
		@return the pseudorandom integer between m and n (inclusive)
	*/
	public int getRandomNumber(int m, int n){
		if (m == n)
			return m;
		else {
			double rnd = (Math.abs(rand.nextDouble()))*(n-m+1) + m;
			int theNum = (int) (rnd);
			return theNum;
		}
	}
}