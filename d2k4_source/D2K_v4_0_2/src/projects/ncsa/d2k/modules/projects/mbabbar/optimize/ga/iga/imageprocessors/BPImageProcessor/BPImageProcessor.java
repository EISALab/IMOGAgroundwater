package ncsa.d2k.modules.projects.mbabbar.optimize.ga.iga.imageprocessors.BPImageProcessor;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.optimize.ga.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.core.optimize.ga.emo.*;
import ncsa.d2k.modules.core.optimize.ga.nsga.*;
import ncsa.d2k.modules.projects.mbabbar.optimize.ga.iga.*;
import ncsa.d2k.modules.projects.mbabbar.optimize.ga.iga.imageprocessors.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import javax.imageio.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

/**
	Processes images/GUI applets for population by invoking the <code>imageProcessor</code> method.
*/
public class BPImageProcessor extends ImageProcessingModule 	{

	//////////////////////////////////
	// Info methods
	//////////////////////////////////
                /**
		This method returns the names of the various inputs.

		@return the name of the indexed input.
	*/
	public String getOutputName (int index) {
		switch (index) {
			case 0: return "IGA Nsga Population";
			case 1: return "Table";
                        default: return "No such output";
		}
        }

	/**
		This method returns the names of the various inputs.

		@return the name of the indexed input.
	*/
	public String getInputName (int index) {
		switch (index) {
			case 0: return "IGA Nsga Population";
			case 1: return "Table";
                        default: return "No such input";
		}
	}

	/**
		This method returns the description of the various inputs.

		@return the description of the indexed input.
	*/
	public String getOutputInfo (int index) {
		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"population\">    <Text> population. </Text>  </Info></D2K>";
			case 1: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"Table\">    <Text>The table with population info: quantitative objectives, selected individuals,  images/applets info for selected individuals. </Text>  </Info></D2K>";
                        default: return "No such output";
		}
        }

	/**
		This method returns the description of the various inputs.

		@return the description of the indexed input.
	*/
	public String getInputInfo (int index) {
		switch (index) {
			case 0: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"population\">    <Text> population. </Text>  </Info></D2K>";
			case 1: return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"Table\">    <Text>The table with quantitative fitnesses and updated individuals after passing through a selection filter. </Text>  </Info></D2K>";
                        default: return "No such input";
		}
	}

	/**
		This method returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo () {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><D2K>  <Info common=\"Image Processing Module\">    <Text> This takes in the population and table of selected individuals and processes them to create relevant images for them. </Text>  </Info></D2K>";
	}

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "IGA: BP Image Processor";
	}

	/**
		Process Images that are plume pictures for the selected individuals in the population.
		This is specific to the Monitoring Problems
                @param popul: the population.
                @param populTable: Table with selected individuals.
	*/
	public void imageProcessor (Population pop, Table popTable){

        }


}








