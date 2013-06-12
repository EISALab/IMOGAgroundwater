package ncsa.d2k.modules.projects.mbabbar.optimize.ga.iga.imageprocessors.util;


import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.event.*;

import ncsa.d2k.modules.projects.pgroves.vis.falsecolor.*;
import ncsa.d2k.modules.projects.pgroves.bp.*;
import ncsa.d2k.modules.core.datatype.table.Table;

/**
	Array for FalseColorPanel
	*/

public class FalseColorPanelArray implements java.io.Serializable {

        // This stores the false color panels in an array
	public FalseColorPanel [][] falColPan ;

        // This stores any text title related to the false color panels
        public String [][] panelTitles ;

        // This stores the IDs of the GA individuals whose false color panels are
        // being passed in.
        public int [] individualIds;


	public FalseColorPanelArray(FalseColorPanel [][] fCPanel, String [][] titles, int [] ids){
                falColPan = fCPanel;
                panelTitles = titles;
                individualIds = ids;
      	}

}//FalseColor Panel Array

