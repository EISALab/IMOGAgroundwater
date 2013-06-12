package ncsa.d2k.modules.projects.pgroves.vis.interp;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.color.*;
import javax.swing.*;
import java.awt.geom.*;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.core.datatype.table.util.TableUtilities;
import ncsa.d2k.modules.core.vis.widgets.*;
import ncsa.d2k.modules.projects.pgroves.vis.falsecolor.D2KColors;

/**
	a panel that visualizes the raw variogram data, any number of 
	binned variogram series, and the model that has been fit to
	data. All series can be toggled on and off by the user.

	@author Peter Groves
	@date 03/11/04
*/

public class VariogramPanel extends JPanel 
	implements ActionListener{

	/** the first table is a one row table used as a placeholder
	so that an empty graph w/ the correct axis/title can be made.
	the second is the raw data. the rest are various incarnations
	of the raw data that's been binned*/
	Table[] data;
	/**these will all state that the first column is x and the 
	second is y. */
	DataSet[] sets;
	/** each graph will have near identical settings. the only
	difference will be that the only one that displays any
	axis/title info will be the dummy graph*/
	GraphSettings[] gSettings;

	/**
	these are actually independent JPanels, but we're going to
	trick them into drawing on this panel's Graphics object. each
	layer will represent a data series, except the first one
	which will represent the background
	*/
	ScatterPlot[] graphLayers;

	
	/**
		the main constructor. 

		@param binned an array of tables representing binned versions
			of the variogram (versions of the experimental variogram). the
			first column should be lag distances, h, and the second variogram
			values, gamma(h)
		@param raw a table that is set up like the binned tables, but with
			an entry for each pair of points in the original data set (the data
			the binning of <code>binned</code> is based on.
	*/
	public VariogramPanel(Table[] binned, Table raw){
		int i, j, k;
		
		//the plus two accounts for the raw data and an empty
		//graph that just has the axis/title graphics
		int numLayers = binned.length + 2;
		sets = new DataSet[numLayers];
		gSettings = new GraphSettings[numLayers];

		//the scale will be set according to the raw values
		//only
		double[] xRange = TableUtilities.getMinMax(raw, 0);
		double[] yRange = TableUtilities.getMinMax(raw, 1);

		//this may go away, but check to see if the binned values
		//are outside the range
		double[] txRange, tyRange;
		for(i = 0; i < binned.length; i++){
			txRange = TableUtilities.getMinMax(binned[i], 0);
			tyRange = TableUtilities.getMinMax(binned[i], 1);
			if(txRange[0] < xRange[0]){
				xRange[0] = txRange[0];
			}
			if(txRange[1] > xRange[1]){
				xRange[1] = txRange[1];
			}
			if(tyRange[0] < yRange[0]){
				yRange[0] = tyRange[0];
			}
			if(tyRange[1] > yRange[1]){
				yRange[1] = tyRange[1];
			}
		}

		
		Integer xMin = new Integer(0);
		Integer xMax = new Integer((int)xRange[1]);
		Double xDataMin = new Double(0.0);
		Double xDataMax = new Double(xRange[1]);

		Integer yMin = new Integer(0);
		Integer yMax = new Integer((int)yRange[1]);
		Double yDataMin = new Double(0.0);
		Double yDataMax = new Double(yRange[1]);
		
		String[] names = generateLayerNames(numLayers);
		Color[] clrs = generatePlotColors(numLayers);

		data = initializeDataTables(binned, raw, xDataMin, xDataMax, 
			yDataMin, yDataMax);

		graphLayers = new ScatterPlot[numLayers];

		for(i = 0; i < numLayers; i++){
			
			gSettings[i] = new GraphSettings();
			
			gSettings[i].xminimum = xMin;
			gSettings[i].xmaximum = xMax;
			gSettings[i].yminimum = yMin;
			gSettings[i].ymaximum = yMax;

			gSettings[i].xdataminimum = xDataMin;
			gSettings[i].xdatamaximum = xDataMax;
			gSettings[i].ydataminimum = yDataMin;
			gSettings[i].ydatamaximum = yDataMax;
			
			if(i == 0){
				//the background, leave most things on
				gSettings[i].title = "Experimental Variogram";
				gSettings[i].xaxis = "Lag Distance (h)";
				gSettings[i].yaxis = "Variogram Value (gamma(h))";
				gSettings[i].displaylegend = false;
				gSettings[i].displayscale = true;
				gSettings[i].displaytickmarks = true;
					
			}else{
				//all others, turn off all axis info
				//NOTE: maybe they should be on but empty so
				//that things are scaled correctly
				gSettings[i].displaygrid = false;

				//I don't like doing this, b/c the scale will be
				//drawn over in each layer, but it will take too
				//much re-working of the code in basic to do 
				//anything else
				gSettings[i].displayscale = true;
				gSettings[i].displaylegend = false;
				gSettings[i].displaytickmarks = true;
				gSettings[i].displaytitle = true;
				gSettings[i].displayaxislabels = true;
				gSettings[i].displayaxis = false;
				gSettings[i].title = " ";
				gSettings[i].xaxis = " ";
				gSettings[i].yaxis = " ";
				/*
				gSettings[i].title = "Experimental Variogram";
				gSettings[i].xaxis = "Lag Distance (h)";
				gSettings[i].yaxis = "Variogram Value (gamma(h))";
				*/

			}

			

			sets[i] = new DataSet(names[i], clrs[i], 0, 1);
			DataSet[] setWrapper = new DataSet[1];
			setWrapper[0] = sets[i];
			graphLayers[i] = new ScatterPlot(data[i], setWrapper, gSettings[i]);

			
		}

		this.setPreferredSize(new Dimension(640, 480));


		
	}

	/**
		generates names for the layers. assumes the first
		layer is the background, second is the raw variogram,
		and all others are binned versions of the variogram.
	*/
	protected String[] generateLayerNames(int numLayers){
		
		String[] names = new String[numLayers];
		names[0] = "Graph Background";
		names[1] = "Raw Semivariogram";
		for(int i = 2; i < numLayers; i++){
			names[i] = "Experimental Variogram: " + (i - 2);
		}

		return names;
	}
	
	/**
		generates colors to display the different data series. the
		raw variogram is a light gray. the others just try to be
		as dissimilar to each other as possible. For now just
		rotates 4 standard d2k colors
	*/
	protected Color[] generatePlotColors(int numLayers){
		Color[] clrs = new Color[numLayers];
		
		//the first layer is the backgound, so this shouldn't really
		//ever be used
		clrs[0] = Color.BLACK;

		//the raw data
		clrs[1] = Color.GRAY;

		//TODO: this should be changed to a more robust method
		int colorIdx;
		for(int i = 2; i < numLayers; i++){
			colorIdx = i % 4;
			switch(colorIdx){
				case 0:{
					clrs[i] = D2KColors.D2K_BLUE;
					break;
				}
				case 1:{
					clrs[i] = D2KColors.D2K_RED;
					break;
				}
				case 2:{
					clrs[i] = D2KColors.D2K_GREEN;
					break;
				}
				case 3:{
					clrs[i] = D2KColors.D2K_YELLOW;
					break;
				}
				default:{
					System.out.println("Problem With Color Assignment");
					clrs[i] = Color.MAGENTA;
				}
			}
		}
		return clrs;
	}

	/**
		makes a single array of tables that contain all of the data.
		The first table is generated here and is a 'dummy' table so
		that the background graph has something to work with. Next 
		is the raw variogram, followed by all the bins

		@param raw the raw data fed to the constructor
		@param binned the binned data fed to the constructor
		@param xmin range of data
		@param xmax range of data
		@param ymin range of data
		@param ymax range of data
		@return a single array of data tables
	*/
	protected Table[]	initializeDataTables(Table[] binned, Table raw,
		Double xmin, Double xmax, Double ymin, Double ymax){

		int numLayers = binned.length + 2;
		Table[] tbls = new Table[numLayers];

		int numCols = 2;
		MutableTableImpl backgroundTable = new MutableTableImpl(numCols);
		for(int i = 0; i < numCols; i++){
			backgroundTable.setColumn(new DoubleColumn(2), i);
		}
		backgroundTable.setDouble(xmin.doubleValue(), 0, 0);
		backgroundTable.setDouble(xmax.doubleValue(), 1, 0);
		backgroundTable.setDouble(ymin.doubleValue(), 0, 1);
		backgroundTable.setDouble(ymax.doubleValue(), 1, 1);

		tbls[0] = backgroundTable;

		tbls[1] = raw;
		
		for(int i = 2; i < numLayers; i++){
			tbls[i] = binned[i - 2];
		}
		
		return tbls;
		
	}
		
	public void paintComponent(Graphics g){
		//super.paintComponent(g);
		Graphics2D g2d = (Graphics2D)g.create();


		int wid = this.getWidth();
		int hgt = this.getHeight();
		Color prevCol = g2d.getColor();
		g2d.setColor(Color.WHITE);
		g2d.fill(new Rectangle2D.Double(0, 0, wid, hgt));
		g2d.setColor(prevCol);
		/*	
		graphLayers[0].setSize(wid, hgt);
		graphLayers[0].setPointSize(10);
		graphLayers[0].initOffsets();
		graphLayers[0].setPointSize(5);
		graphLayers[0].paintComponent(g2d);
*/

		int[] pointSizes = generatePointSizes(graphLayers.length);
		
		for(int i = 0; i < graphLayers.length; i++){
			graphLayers[i].setSize(wid, hgt);
			//graphLayers[i].drawDataSet(g2d, sets[i]);
			
			graphLayers[i].setPointSize(pointSizes[i]);

			//System.out.println("painting:"+i);
			graphLayers[i].paintComponent(g2d);
			
		}
	}
			
	public int[] generatePointSizes(int numLayers){
		int[] psz = new int[numLayers];
		psz[0] = 5;
		psz[1] = 2;
		for(int i = 2; i < numLayers; i++){
			psz[i] = 10;
		}
		return psz;
	}
				
		
		
		
	public void actionPerformed(ActionEvent e) {

	}
		
}

