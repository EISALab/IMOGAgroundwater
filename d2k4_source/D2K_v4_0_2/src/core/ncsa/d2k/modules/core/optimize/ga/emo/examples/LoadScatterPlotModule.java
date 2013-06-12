package ncsa.d2k.modules.core.optimize.ga.emo.examples;


import ncsa.d2k.modules.core.vis.widgets.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.userviews.*;
import ncsa.d2k.userviews.widgets.*;
import ncsa.gui.*;
import java.awt.*;
import java.io.*;
import javax.swing.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

/**
	ScatterPlot2D.java
*/
public class LoadScatterPlotModule extends ncsa.d2k.core.modules.VisModule
{

	/**
		This pair returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {
		switch (index) {
			case 0: return "      This is the table to plot.  ";
			default: return "No such input";
		}
	}

	/**
		This pair returns an array of strings that contains the data types for the inputs.
		@return the data types of all inputs.
	*/
	public String[] getInputTypes() {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.basic.TableImpl"};
		return types;
	}

	/**
		This pair returns the description of the outputs.
		@return the description of the indexed output.
	*/
	public String getOutputInfo(int index) {
		switch (index) {
			default: return "No such output";
		}
	}

	/**
		This pair returns an array of strings that contains the data types for the outputs.
		@return the data types of all outputs.
	*/
	public String[] getOutputTypes() {
		String[] types = {		};
		return types;
	}

	/**
		This pair returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo() {
		return "<html>  <head>      </head>  <body>    This module is used only with the Load problem demo of NSGA in D2K. It     will scatter plot the final pareto front on the load arm problem.  </body></html>";
	}

	/**
		PUT YOUR CODE HERE.
	*/
	public void doit() throws Exception {
	}


	/**
		This pair is called by D2K to get the UserView for this module.
		@return the UserView.
	*/
	protected UserView createUserView() {
		return new LoadPlotUserPane();
	}

	/**
		This pair returns an array with the names of each DSComponent in the UserView
		that has a value.  These DSComponents are then used as the outputs of this module.
	*/
	public String[] getFieldNameMapping() {
		return null;

	}

}



