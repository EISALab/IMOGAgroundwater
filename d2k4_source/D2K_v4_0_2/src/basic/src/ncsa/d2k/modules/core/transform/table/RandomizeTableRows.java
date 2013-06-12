package ncsa.d2k.modules.core.transform.table;


import ncsa.d2k.core.modules.*;
import java.util.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
/**
	RandomizeTableRows.java
*/
public class RandomizeTableRows extends ncsa.d2k.core.modules.DataPrepModule
{
	/**
		This pair returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {
		switch (index) {
			case 0: return "The table to have rows randomized";
			default: return "NO SUCH INPUT!";
		}
	}

	/**
		This pair returns an array of strings that contains the data types for the inputs.
		@return the data types of all inputs.
	*/
	public String[] getInputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.MutableTable"};
		return types;
	}

	/**
		This pair returns the description of the outputs.
		@return the description of the indexed output.
	*/
	public String getOutputInfo(int index) {
		switch (index) {
			case 0: return "The resulting table with rows randomly reordered.";
			default: return "NO SUCH OUTPUT!";
		}
	}

	/**
		This pair returns an array of strings that contains the data types for the outputs.
		@return the data types of all outputs.
	*/
	public String[] getOutputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.MutableTable"};
		return types;
	}

	/**
		This pair returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo() {
		return "<p>Overview: This module randomly reorders the rows of a Table. </p><p>Detailed Description:"+
			" The rows in the Table are randomly swapped to ensure a random ordering of the data. A"+
			" <i>Seed</i> may be specified for initializing the random number generator. If you specify"+
			" a <i>Seed</i>, remember to set the <i>Use Seed Value Indicated</i> flag to TRUE. Otherwise, it will"+
			" generate a random number without a seed initialization.    </p>    <p> Data Handling:"+
			" This module does its work on the data in place, so it doesn't need to allocate more memory."+
			" However, it does actually swap the rows of the data.    </p>    <p> Scalability:"+
			" For large data sets this module could do a lot of row swapping, which would mean a lot"+
			" of data movement. </p>";
	}

	/**
		Randomizes the rows of the Table using swapping
	*/
	public void doit() throws Exception {

		if(useSeed)
			rand = new Random(seed);
		else
			rand = new Random();

		MutableTable table = (MutableTable) pullInput(0);
		int numRow = table.getNumRows();

		int j = 0;
		for (int i=0; i<numRow; i++){
			j = getRandomNumber(i, numRow-1);
			table.swapRows(i,j);
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

        public PropertyDescription [] getPropertiesDescriptions() {
          PropertyDescription [] pds = new PropertyDescription [2];

          pds[0] = new PropertyDescription("seed","Seed",
                   "The Seed is used to initialize the random number generator.");

          pds[1] = new PropertyDescription("useSeed",
                   "Use Seed Value Indicated",
                   "The random number generator can be called with or without an initialization seed value. "+
                   "This flag indicates whether the seed value given should be used.");

          return pds;
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

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "Randomize Table Rows";
	}

	/**
	 * Return the human readable name of the indexed input.
	 * @param index the index of the input.
	 * @return the human readable name of the indexed input.
	 */
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Original Table";
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
				return "Randomized Table";
			default: return "NO SUCH OUTPUT!";
		}
	}
}

// QA Comments
// 2/20/03 - Handed off to QA by Loretta Auvil
// 2/21/03 - Anca started QA process. Testing didn't show any problem. Good performanc
// 2/21/03 - Very clean and well documented. checked into basic.
// END QA Comments


      /**
       *
       * QA Comments
       * 10/23/03 - Vered started QA process.
       * does not handle missing values well. bug in implementation of swapRows.
       * no swapping is done for the missing values array... apparently this was fixed [03-11-03]
       * works with MutableTableImpl, ExampleTableImpl [11-03-03]
 *
 *       12-05-03 - module is ready for basic 4.

        */


