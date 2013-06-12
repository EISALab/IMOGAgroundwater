
package ncsa.d2k.modules.core.optimize.ga;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.core.optimize.util.*;
import ncsa.d2k.modules.core.optimize.ga.nsga.*;

/**
	Converts the population to a table where each column is an attribute, objective value,
	or fitness.
*/
public class PopulationGenesToTable extends ncsa.d2k.core.modules.DataPrepModule {

	/**
		This pair returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {
		switch (index) {
			case 0: return "";
			default: return "No such input";
		}
	}

	/**
		This pair returns an array of strings that contains the data types for the inputs.
		@return the data types of all inputs.
	*/
	public String[] getInputTypes() {
		String[] types = {"ncsa.d2k.modules.core.optimize.ga.Population"};
		return types;
	}

	/**
		This pair returns the description of the outputs.
		@return the description of the indexed output.
	*/
	public String getOutputInfo(int index) {
		switch (index) {
			case 0: return "";
			default: return "No such output";
		}
	}

	/**
		This pair returns an array of strings that contains the data types for the outputs.
		@return the data types of all outputs.
	*/
	public String[] getOutputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.Table"};
		return types;
	}

	/**
		This pair returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo() {
		return "";
	}

	/**
		PUT YOUR CODE HERE.
	*/
	public void doit() throws Exception {
		Population pop = (Population) this.pullInput (0);

		//Table vt = pop.getTable ();

                Individual[] members = pop.getMembers();
                int popSize = pop.size();
                int numObjectives;
                boolean nsga = false;
                if(pop instanceof NsgaPopulation) {
                  numObjectives = ((NsgaPopulation)pop).getNumObjectives();
                  nsga = true;
                }
                else
                  numObjectives = 1;

                if(members instanceof BinarySolution[]) {
                  int numTraits = ((BinaryRange)pop.getTraits()[0]).getNumBits ();
                  BinaryIndividual [] nis = (BinaryIndividual []) members;
                  boolean [][] dc = new boolean [numTraits][popSize];
                  double [][] objs = new double [numObjectives][popSize];

                  // Populate the double arrays
                  for (int i = 0 ; i < popSize ; i++) {
                          boolean [] genes = (boolean []) nis [i].getGenes ();
                          for (int j = 0 ; j < numTraits ; j++)
                                  dc [j][i] = genes [j];
                          //objs [i] = nis [i].getObjective();
                          for(int j = 0; j < numObjectives; j++) {
                            if(nsga) {
                              objs[j][i] = ((NsgaSolution)nis[i]).getObjective(j);
                            }
                            else
                              objs[j][i] = nis[i].getObjective();
                          }
                  }

                  // Now make the table
                  //BASIC3 MutableTable mt = (MutableTable)DefaultTableFactory.getInstance().createTable(0);
                  MutableTable mt =  new MutableTableImpl(0);
                  int i = 0;
                  for (; i < numTraits ; i++) {
                        //BASIC3  mt.addColumn (dc[i]);
                        BooleanColumn bc = new BooleanColumn(dc[i]);
                        mt.addColumn(bc);
                  		mt.setColumnLabel (Integer.toString(i), i);
                  }
                  for(int j = 0; j < numObjectives; j++, i++) {
                    //BASIC3 mt.addColumn(objs[j]);
                  	DoubleColumn dcol = new DoubleColumn(objs[j]);
                  	mt.addColumn(dcol);
                  	mt.setColumnLabel("Objective "+i, i);
                  }

                  pushOutput(mt, 0);
                }
                else {
                  Table t = pop.getTable();
                  pushOutput(t, 0);
                }
	}


	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "Population to Table";
	}

	/**
	 * Return the human readable name of the indexed input.
	 * @param index the index of the input.
	 * @return the human readable name of the indexed input.
	 */
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Population";
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
				return "Table";
			default: return "NO SUCH OUTPUT!";
		}
	}
}

