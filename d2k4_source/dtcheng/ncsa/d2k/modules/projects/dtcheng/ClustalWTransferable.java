/*
 * Created on Oct 7, 2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ncsa.d2k.modules.projects.dtcheng;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.Serializable;

/**
 * @author redman
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
/**
 * This is the transferable class used to transport the clustalw results.
 * @author redman
 */
class ClustalWTransferable implements Transferable, Serializable {
	static DataFlavor[] flavors = null;
	static {
		try {
			flavors = new DataFlavor [1];
			flavors [0] = new DataFlavor("text/html");
		} catch (ClassNotFoundException cnfe) {
		}
	}
	ClustalWResults cwr;
	ClustalWTransferable (ClustalWResults cawr) {
		cwr = cawr;
	}
		
	/* (non-Javadoc)
	 * @see java.awt.datatransfer.Transferable#getTransferDataFlavors()
	 */
	public DataFlavor[] getTransferDataFlavors() {
		return ClustalWTransferable.flavors;
	}

	/* (non-Javadoc)
	 * @see java.awt.datatransfer.Transferable#isDataFlavorSupported(java.awt.datatransfer.DataFlavor)
	 */
	public boolean isDataFlavorSupported(DataFlavor arg0) {
		return arg0.equals(flavors[0]);
	}

	/* (non-Javadoc)
	 * @see java.awt.datatransfer.Transferable#getTransferData(java.awt.datatransfer.DataFlavor)
	 */
	public Object getTransferData(DataFlavor arg0) throws UnsupportedFlavorException, IOException {
	
		// buffer
		StringBuffer sb = new StringBuffer(10000);	
		if (arg0.equals(flavors[0])) {
			
			// first append the summary.
			sb.append(cwr.summary);
			sb.append("\n<br><br>");
			
			// next the counts.
			sb.append(cwr.counts);
			sb.append("\n<br><br>");
			
			sb.append(cwr.sequences);
			sb.append("\n<br><br>");
			
			/// next do the alignments.
			String [] alignments = cwr.alignments;
			if (alignments != null) {
				sb.append('\n');
				sb.append("<div class=\"resultHeading\">Alignments</div>");
				sb.append('\n');
				sb.append("<div class=\"result\">");
				sb.append("\n<pre>");
				for (int i = 0 ; i < alignments.length ; i++) {
					sb.append(alignments[i]);
				}
				sb.append("</pre>\n");
				sb.append("<br>\n");
				sb.append("</div>");
			}
			
			// Finally do the counts.
			String [] counts = cwr.tree;
			if (counts != null){
				sb.append('\n');
				sb.append("<div class=\"resultHeading\">Tree</div>");
				sb.append('\n');
				sb.append("<div class=\"result\">");
				sb.append('\n');
				for (int i = 0 ; i < counts.length ; i++) {
					sb.append(counts[i]);
					sb.append("<br>");
				}
				sb.append('\n');
				sb.append("</div>");
			}
		} else
			throw new UnsupportedFlavorException(arg0);
		return sb.toString();
	}
}

