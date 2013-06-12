package ncsa.d2k.modules.core.transform.binning;
import ncsa.d2k.modules.core.datatype.table.Table;
import ncsa.d2k.modules.core.datatype.table.ExampleTable;
import java.util.*;
import ncsa.d2k.modules.core.transform.StaticMethods;

/**
 * <p>Title: </p>
 * <p>Description: This is a suppoty class for binning Headless UI modules.
 * its methods will be called by the doit method.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author: vered goren
 * @version 1.0
 */

public class BinningUtils {
  public BinningUtils() {
  }


  public static boolean validateBins(Table t, BinDescriptor[] binDes, String commonName) throws Exception{

    if(binDes == null)
      throw new Exception (commonName + " has not been configured. Before running headless, run with the gui and configure the parameters.");

      if(binDes.length == 0){

      System.out.println(commonName + ": No bins were configured. The transformation will be an empty one.");
       return true;
    }

//validating relevancy of bins to the input table.
    HashMap columns = StaticMethods.getAvailableAttributes(t);

    for (int i=0; i<binDes.length; i++){
      if(!columns.containsKey(binDes[i].label.toUpperCase()))
        throw new Exception(commonName + ": Bin " +  binDes[i].toString() + " does not match any column label in the input table. Please reconfigure this module.");
//
    }//for
    return true;
  }



  public static boolean validateBins(HashMap colMap, BinDescriptor[] binDes, String commonName) throws Exception{

   if(binDes == null)
     throw new Exception (commonName + " has not been configured. Before running headless, run with the gui and configure the parameters.");

     if(binDes.length == 0){
     System.out.println(commonName + ": No bins were configured. The transformation will be an empty one.");
      return true;
   }


   for (int i=0; i<binDes.length; i++){
    if(!colMap.containsKey(binDes[i].label.toUpperCase()))
      throw new Exception(commonName+ ": Bin " +  binDes[i].toString() + " does not match any column label in the database table." +
                                       " Please reconfigure this module via a GUI run so it can run Headless.");

  }//for

   return true;
 }
 
 public static BinDescriptor[] addMissingValueBins(Table tbl, BinDescriptor[] bins ) {
 
	HashMap colIndexLookup = new HashMap(tbl.getNumColumns());
			for (int i = 0; i < tbl.getNumColumns(); i++) {
				colIndexLookup.put(tbl.getColumnLabel(i), new Integer(i));
			}

			// need to figure out which columns have been binned:
			boolean[] binRelevant = new boolean[tbl.getNumColumns()];
			for (int i = 0; i < binRelevant.length; i++)
				binRelevant[i] = false;
			for (int i = 0; i < bins.length; i++) {
				Integer idx = (Integer) colIndexLookup.get(bins[i].label);
				if (idx != null) {
					binRelevant[idx.intValue()] = true;
					// System.out.println("relevant column " + idx.intValue());
				}
				//else
				//   System.out.println("COLUMN LABEL NOT FOUND!!!");
				//binRelevant[bins[i].column_number] = true;
			}
 
 
 	ArrayList unknowBins = new ArrayList();
 	int numColumns = tbl.getNumColumns();
 	for (int i=0; i < numColumns; i++) {
 		if (binRelevant[i])
		 	  if(tbl.getColumn(i).hasMissingValues())
 	  			unknowBins.add( BinDescriptorFactory.createMissingValuesBin(i,tbl));
 	 	}
 	
 	BinDescriptor[] newbins = new BinDescriptor[bins.length + unknowBins.size()];
 	int i;
 	for (i =0; i < bins.length; i++)
 		newbins[i]=bins[i];
 	Iterator it = unknowBins.iterator();
 	while (it.hasNext())
 		newbins[i++] = (BinDescriptor)it.next();
 
 	return newbins;	
 }
 
/* 
 public static BinDescriptor[] addMissingValueBins(ExampleTable tbl, BinDescriptor[] bins ) {
 	
	 ArrayList unknowBins = new ArrayList();
	 int [] inputs = tbl.getInputFeatures();
	 int []outputs = tbl.getOutputFeatures();
	 for (int i=0; i < inputs.length; i++)
	   if(tbl.getColumn(inputs[i]).hasMissingValues())
		 unknowBins.add( BinDescriptorFactory.createMissingValuesBin(inputs[i],tbl));
 	
	for (int i=0; i < outputs.length; i++)
		   if(tbl.getColumn(outputs[i]).hasMissingValues())
			 unknowBins.add( BinDescriptorFactory.createMissingValuesBin(outputs[i],tbl));
 	
	 BinDescriptor[] newbins = new BinDescriptor[bins.length + unknowBins.size()];
	 int i;
	 for (i =0; i < bins.length; i++)
		 newbins[i]=bins[i];
	 Iterator it = unknowBins.iterator();
	 while (it.hasNext())
		 newbins[i++] = (BinDescriptor)it.next();
 
	 return newbins;	
  }
 
*/

}