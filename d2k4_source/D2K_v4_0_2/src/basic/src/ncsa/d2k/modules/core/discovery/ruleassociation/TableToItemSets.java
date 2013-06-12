package ncsa.d2k.modules.core.discovery.ruleassociation;




import ncsa.d2k.modules.core.datatype.table.*;

/**
	TableToItemSets.java
*/
public class TableToItemSets extends ncsa.d2k.core.modules.DataPrepModule
{
	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "Table To Item Sets";
	}

	/**
	 * Return the human readable name of the indexed input.
	 * @param index the index of the input.
	 * @return the human readable name of the indexed input.
	 */
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Table";
			default:
				return "No such input";
		}
	}

	/**
		This method returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {
		switch (index) {
			case 0:
				return "The table that items and sets will be extracted from.";
			default:
				return "No such input";
		}
	}

	/**
		This method returns an array of strings that contains the data types for the inputs.
		@return the data types of all inputs.
	*/
	public String[] getInputTypes () {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.Table"};
		return types;
	}

	/**
	 * Return the human readable name of the indexed output.
	 * @param index the index of the output.
	 * @return the human readable name of the indexed output.
	 */
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "Item Sets";
			default:
				return "No such output";
		}
	}

	/**
		This method returns the description of the outputs.
		@return the description of the indexed output.
	*/
	public String getOutputInfo (int index) {
		switch (index) {
			case 0:
				return "The items of interest that were found in the table and " +
				       "a representation of the items that occur together in the table.";

			default: return "No such output";
		}
	}

	/**
		This method returns an array of strings that contains the data types for the outputs.
		@return the data types of all outputs.
	*/
	public String[] getOutputTypes () {
		String[] types = {"ncsa.d2k.modules.core.discovery.ruleassociation.ItemSets"};
		return types;
	}

	/**
		This method returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo () {
                StringBuffer sb =  new StringBuffer("<p>Overview: ");
                sb.append( "This module reads a Table and extracts from it items for use ");
                sb.append( "in mining association rules with the Apriori algorithm. ");
                sb.append( "</p><p>Detailed Description: ");
                sb.append( "This module takes as input a Table or an Example Table, and extracts items ");
		sb.append( "that are used by the Apriori rule association algorithm. ");
		sb.append( "An item is an [attribute,value] pair that occurs in the input table. ");
                sb.append( "The module uses information from the original table to determine which ");
		sb.append( "attributes should be used to form items being considered as possible rule antecedents ");
		sb.append( "and rule consequents. ");
		sb.append( "A compact representation is created indicating which items are contained in ");
		sb.append( "rows in the original table. ");
		sb.append( "The items and other information used by the Apriori algorithm are written ");
		sb.append( "to the <i>Item Sets</i> output port. ");

		sb.append( "</p><p>" );
		sb.append( "If a Table or an Example Table with no specified input or output attributes is loaded, ");
		sb.append( "all attributes (columns) will be used to form items being considered as possible antecedents ");
		sb.append( "and consequents for the association rules. " );
		sb.append( "If an Example Table with only input attributes or only output attributes is loaded, " );
		sb.append( "the chosen attributes will be used to form items considered as possible rule antecedents and ");
		sb.append( "possible rule consequents. " );
		sb.append( "If an Example Table with both input and output attributes is loaded, the inputs will be " );
		sb.append( "used to form items considered as possible rule antecedents, ");
		sb.append( "and the outputs used to form items considered as possible rule consequents. " );

		sb.append( "</p><p> ");
                sb.append( "The computational complexity of the Apriori algorithm depends on ");
		sb.append( "the number of possible antecedents and consequents, so narrowing the search prior to this step is ");
		sb.append( "highly recommended.   Use the module <i>Choose Attributes</i> to specify the subset of table ");
		sb.append( "attributes that are of interest. ");
                sb.append( "If the table has continuous attributes as possible rule antecedents or targets, ");
                sb.append( "a <i>Binning</i> module should be used prior to this module to reduce the number of possible ");
                sb.append( "values for those continuous attributes. ");

		sb.append( "</p><p>" );
		sb.append( "In a typical itinerary the <i>Item Sets</i> output port from this module is connected to ");
		sb.append( "a <i>Generate Multiple Outputs</i> " );
		sb.append( "module and then to an <i>Apriori</i> module which forms frequent itemsets based on ");
		sb.append( "a minimum support value, and to a <i>Compute Confidence</i> module which forms ");
		sb.append( "association rules that satisfy a minimum confidence value. ");

                sb.append( "</p><p>Limitations: ");
                sb.append( "The <i>Apriori</i> and <i>Compute Confidence</i> modules currently ");
                sb.append( "build rules with a single item in the consequent.  ");

		sb.append( "</p><p>Data Handling: " );
		sb.append( "This module does not modify the input Table in any way. ");

		sb.append( "</p><p>Scalability: " );
		sb.append( "A representation of each row of the table is stored in memory. The representation is usually ");
		sb.append( "smaller than the original data.    </p>" );

		return sb.toString();
	}

	/**
		PUT YOUR CODE HERE.
	*/
	public void doit () throws Exception {
		ItemSets iss = new ItemSets((Table)this.pullInput(0));

		this.pushOutput(iss,0);
	}

}

// Start QA Comments
// 2/28/03 Recv from Tom
// 3/11/03 Ruth starts QA;
//       - Renamed TableToItemSets instead of ConvertTableToItemSets (class)
//         and Table To Sets (module name).   Updated documentation.
// 3/18/03 Removed Target Attributes output port.  That information is now available
//         in ItemSets and all modules that used Target Attributes already get ItemSets.
// 3/20/03 Ready for Basic.
// End QA Comments
