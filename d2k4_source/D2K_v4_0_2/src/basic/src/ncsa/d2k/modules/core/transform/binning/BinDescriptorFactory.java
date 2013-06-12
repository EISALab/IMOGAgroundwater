/*
 * Created on Mar 11, 2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package ncsa.d2k.modules.core.transform.binning;

import java.text.NumberFormat;
import ncsa.d2k.modules.core.datatype.table.*;


public class BinDescriptorFactory {
	
	protected static final String
		  EMPTY = "", COLON = " : ", COMMA = ",",
		  DOTS = "...", OPEN_PAREN = "(", CLOSE_PAREN = ")",
		  OPEN_BRACKET = "[", CLOSE_BRACKET = "]";
	
		
	/**
	  * Create a numeric bin that goes from min to max.
	  */
	 public static BinDescriptor createNumericBinDescriptor(int col, double min,
		 double max, NumberFormat nf, Table tbl) {
	   StringBuffer nameBuffer = new StringBuffer();
	   nameBuffer.append(OPEN_PAREN);
	   //ANCA nameBuffer.append(nf.format(min));
	   nameBuffer.append(min);
	   nameBuffer.append(COLON);
	   //ANCA nameBuffer.append(nf.format(max));
	   nameBuffer.append(max);
	   nameBuffer.append(CLOSE_BRACKET);
	   BinDescriptor nb = new NumericBinDescriptor(col, nameBuffer.toString(),
												   min, max,
												   tbl.getColumnLabel(col));
	   return nb;
	 }
	 
	  public static BinDescriptor createMaxNumericBinDescriptor(int col, double min,NumberFormat nf,  Table tbl) {
		StringBuffer nameBuffer = new StringBuffer();
		nameBuffer.append(OPEN_PAREN);
		//ANCA nameBuffer.append(nf.format(min));
		nameBuffer.append(min);
		nameBuffer.append(COLON);
		nameBuffer.append(DOTS);
		nameBuffer.append(CLOSE_BRACKET);
		BinDescriptor nb = new NumericBinDescriptor(col, nameBuffer.toString(),
													min, Double.POSITIVE_INFINITY,
													tbl.getColumnLabel(col));
		return nb;
	  }
	
	/**
			* Create a numeric bin that goes from Double.NEGATIVE_INFINITY to max
			*/
		   public static BinDescriptor createMinNumericBinDescriptor (int col, double max,NumberFormat nf, Table tbl) {
			   StringBuffer nameBuffer = new StringBuffer();
			   nameBuffer.append(OPEN_BRACKET);
			   nameBuffer.append(DOTS);
			   nameBuffer.append(COLON);
			   //ANCA nameBuffer.append(nf.format(max));
			   nameBuffer.append(max);
			   nameBuffer.append(CLOSE_BRACKET);
			   BinDescriptor nb = new NumericBinDescriptor(col, nameBuffer.toString(),
					   Double.NEGATIVE_INFINITY, max, tbl.getColumnLabel(col));
			   return  nb;
		   }
	/**
	   * put your documentation comment here
	   * @param idx
	   * @param name
	   * @param sel
	   * @return
	   */
	  public static BinDescriptor createTextualBin(int idx, String name, String[] vals, Table tbl) {
		return new TextualBinDescriptor(idx, name, vals, tbl.getColumnLabel(idx));
	  }
	  
	  public static BinDescriptor createMissingValuesBin ( int idx, Table tbl) {
	  	String [] vals = { tbl.getMissingString()};
	  	return new TextualBinDescriptor(idx,"Unknown",vals,tbl.getColumnLabel(idx));
	  }

	/**
		* Create a numeric bin that goes from Double.NEGATIVE_INFINITY to Double.POSITIVE_INFINITY
		*/
	   public static BinDescriptor createMinMaxBinDescriptor (int col,Table tbl) {
		   StringBuffer nameBuffer = new StringBuffer();
		   nameBuffer.append(OPEN_BRACKET);
		   nameBuffer.append(DOTS);
		   nameBuffer.append(COLON);
		   nameBuffer.append(DOTS);
		   nameBuffer.append(CLOSE_BRACKET);
		   BinDescriptor nb = new NumericBinDescriptor(col, nameBuffer.toString(),
				   Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, tbl.getColumnLabel(col));
		   return  nb;
	   }

}
