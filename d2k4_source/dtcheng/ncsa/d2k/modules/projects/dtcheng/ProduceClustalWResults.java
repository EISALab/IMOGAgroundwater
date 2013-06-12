package ncsa.d2k.modules.projects.dtcheng;

import java.awt.datatransfer.Transferable;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;

import ncsa.d2k.core.modules.*;
public class ProduceClustalWResults extends ncsa.d2k.core.modules.OutputModule implements DataProducer {

	ClustalWTransferable transf = null;
	
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
		return "<p>Overview: Produce results for ClustalW program as HTML and plain text.</p>";
	}

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "Produce ClustalW Results";
	}
	boolean debug = true;
	public boolean getDebug() { return debug; }
	public void setDebug (boolean d) { debug = d; }
	
	/* (non-Javadoc)
	 * @see ncsa.d2k.core.modules.ExecModule#doit()
	 */
	protected void doit() throws Exception {
		ClustalWResults cwr = (ClustalWResults) this.pullInput (0);
		transf = new ClustalWTransferable (cwr);
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
}