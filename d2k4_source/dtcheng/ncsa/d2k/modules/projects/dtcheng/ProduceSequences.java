package ncsa.d2k.modules.projects.dtcheng;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.Serializable;

import ncsa.d2k.core.modules.DataProducer;
public class ProduceSequences extends ncsa.d2k.core.modules.OutputModule implements DataProducer {

	SequencesTransferable transf = null;
	
	/**
	 * returns information about the input at the given index.
	 * @return information about the input at the given index.
	 */
	public String getInputInfo(int index) {
		switch (index) {
			case 0: return "<p>These are the results of the ClustalW program.</p>";
			default: return "No such input";
		}
	}

	/**
	 * returns information about the output at the given index.
	 * @return information about the output at the given index.
	 */
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "ClustalW Results";
			default: return "NO SUCH INPUT!";
		}
	}

	/**
	 * returns string array containing the datatypes of the inputs.
	 * @return string array containing the datatypes of the inputs.
	 */
	public String[] getInputTypes() {
		String[] types = {"ncsa.d2k.modules.projects.dtcheng.ClustalWResults"};
		return types;
	}

	/**
	 * returns information about the output at the given index.
	 * @return information about the output at the given index.
	 */
	public String getOutputInfo(int index) {
		switch (index) {
			default: return "No such output";
		}
	}

	/**
	 * returns information about the output at the given index.
	 * @return information about the output at the given index.
	 */
	public String getOutputName(int index) {
		switch(index) {
			default: return "NO SUCH OUTPUT!";
		}
	}

	/**
	 * returns string array containing the datatypes of the outputs.
	 * @return string array containing the datatypes of the outputs.
	 */
	public String[] getOutputTypes() {
		String[] types = {		};
		return types;
	}

	/**
	 * returns the information about the module.
	 * @return the information about the module.
	 */
	public String getModuleInfo() {
		return "<p>Overview: Produce sequences fror ClustalW program as HTML.</p>";
	}

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "Produce ClustalW Sequences";
	}
	boolean debug = true;
	public boolean getDebug() { return debug; }
	public void setDebug (boolean d) { debug = d; }
	
	/* (non-Javadoc)
	 * @see ncsa.d2k.core.modules.ExecModule#doit()
	 */
	protected void doit() throws Exception {
		ClustalWResults cwr = (ClustalWResults) this.pullInput (0);
		transf = new SequencesTransferable (cwr);
		if (debug) {
			Object tmp = transf.getTransferData(transf.flavors[0]);
			String is = (String) tmp;
			System.out.println(is);
		}
	}

	/* (non-Javadoc)
	 * @see ncsa.d2k.core.modules.DataProducer#getData()
	 */
	public Transferable getData() {
		return transf;
	}
	
	/**
	 * This transferable object only contains the tree, it will discard the rest of the results
	 * in the ClustalWResults object.
	 * @author redman
	 */
	class SequencesTransferable implements Transferable, Serializable {
		
		/** the flavors we support. */
		DataFlavor[] flavors;
		
		/** the results from ClustalW. */
		ClustalWResults cwr;
		
		SequencesTransferable (ClustalWResults cawr) throws ClassNotFoundException {
			cwr = cawr;
			flavors = new DataFlavor [1];
			flavors[0] = new DataFlavor ("text/html");
		}
		
		/* (non-Javadoc)
		 * @see java.awt.datatransfer.Transferable#getTransferDataFlavors()
		 */
		public DataFlavor[] getTransferDataFlavors() {
			return this.flavors;
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
				sb.append(cwr.sequences);
				//sb.append("\n<br><br>");
			} else
				throw new UnsupportedFlavorException(arg0);
			return sb.toString();
		}
	}
}