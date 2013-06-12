package ncsa.d2k.modules.projects.pgroves.vis.falsecolor;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.imageio.*;
import java.util.*;
import java.awt.image.BufferedImage ;

import ncsa.d2k.modules.projects.pgroves.bp.*;
import ncsa.d2k.modules.core.datatype.table.Table;
//import ncsa.d2k.modules.projects.clutter.*;
import ncsa.gui.*;
import ncsa.d2k.gui.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import com.sun.image.codec.jpeg.*;

/**
	Applies a false, or pseudo, coloring to a grayscale image.

	@author pgroves
	@date 01/21/04
	*/

public class FalseColorPanel extends JPanel
	implements ActionListener {

	private boolean debug = true;

	FalseColorMap colorMap;
	public ColorBarWidget colorBar;
	SliderControlPanel controls;
	//ImagePanel iPan;
	Table originalImage;
	double[] range;

	int bandIndex = 0;

	/**all the stuff that isn't the image(s) being viewed*/
	JPanel controlPanel;

	/** holds the color bar and sliders */
	JPanel colorControlPanel;

	/** selection between types of color map */
	//JComboBox colorMapChooser;

	/** zoom in by the default value in the ColorImagePanel*/
	JButton zoomInButton;
	/** zoom out by the default value in the ColorImagePanel*/
	JButton zoomOutButton;

	///////////////////////
	// added by meghna
        /* variables to set the user specific min and max values for images*/
        double userImgMin, userImgMax;
	////////////////////////

	public ColorImagePanel imagePanel;

	public JScrollPane imagePanelHolder;

	/**
		Does the main work of creating the entire false color vis
		panel from an ImageObject. This is the method that
		should be used directly

	*/

	public FalseColorPanel(Table img){

		originalImage = img;
                //added by Abhishek Singh
                Dimension d;
                d =  this.getPreferredSize();

		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		controlPanel = new JPanel();
		controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
		controlPanel.add(Box.createVerticalStrut(10));

		colorControlPanel = new JPanel();
		colorControlPanel.setLayout(
			new BoxLayout(colorControlPanel, BoxLayout.X_AXIS));

		range = this.getRange(img);
		colorMap = new LinearColorMap(range[0], range[1]);
		// colorMap = new LogColorMap(range[0], range[1], 2);

		colorBar = new ColorBarWidget(range[0], range[1], colorMap);
		this.userImgMin = range[0];
		this.userImgMax = range[1];
 
		colorControlPanel.add(colorBar);

		controls = new SliderControlPanel(this);
		colorControlPanel.add(controls);

		controlPanel.add(colorControlPanel);
		controlPanel.add(Box.createVerticalStrut(10));

		//the zoom buttons go below the color control widgets
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		zoomInButton = new JButton("+");
		zoomOutButton = new JButton("-");
		zoomInButton.addActionListener(this);
		zoomOutButton.addActionListener(this);
		buttonPanel.add(zoomInButton);
		buttonPanel.add(zoomOutButton);
		controlPanel.add(buttonPanel);
		

		//////////////////////
		// added by Meghna
		controlPanel.setMaximumSize(new Dimension(100, 200));
		controlPanel.setPreferredSize(new Dimension(100, 200));
		//////////////////////

		this.add(controlPanel);

		imagePanel = new ColorImagePanel(img, colorMap);
                
                ///Added by Meghna & Abhishek to reset background size
		imagePanelHolder = new JScrollPane(imagePanel); 
                imagePanelHolder.setPreferredSize(new Dimension (img.getNumColumns(), img.getNumRows()));
                this.add(imagePanelHolder);
                this.setPreferredSize(new Dimension ((int)(controlPanel.getPreferredSize().getWidth()+imagePanelHolder.getPreferredSize().getWidth()*7),(int)(Math.max(controlPanel.getPreferredSize().getHeight(), imagePanelHolder.getPreferredSize().getHeight())) ));
                ///End Added by Meghna & Abhishek to reset background size
                
		

            /////////////////////
		// added by Meghna 
		refresh();
		/////////////////////
	}

        
        public FalseColorPanel(Table img, Table markPixels){

		originalImage = img;
                //added by Abhishek Singh
                Dimension d;
                d =  this.getPreferredSize();

		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		controlPanel = new JPanel();
		controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
		controlPanel.add(Box.createVerticalStrut(10));

		colorControlPanel = new JPanel();
		colorControlPanel.setLayout(
			new BoxLayout(colorControlPanel, BoxLayout.X_AXIS));

		range = this.getRange(img);
		colorMap = new LinearColorMap(range[0], range[1]);
		// colorMap = new LogColorMap(range[0], range[1], 2);

		colorBar = new ColorBarWidget(range[0], range[1], colorMap);
		this.userImgMin = range[0];
		this.userImgMax = range[1];
 
		colorControlPanel.add(colorBar);

		controls = new SliderControlPanel(this);
		colorControlPanel.add(controls);

		controlPanel.add(colorControlPanel);
		controlPanel.add(Box.createVerticalStrut(10));

		//the zoom buttons go below the color control widgets
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		zoomInButton = new JButton("+");
		zoomOutButton = new JButton("-");
		zoomInButton.addActionListener(this);
		zoomOutButton.addActionListener(this);
		buttonPanel.add(zoomInButton);
		buttonPanel.add(zoomOutButton);
		controlPanel.add(buttonPanel);
		

		//////////////////////
		// added by Meghna
		controlPanel.setMaximumSize(new Dimension(100, 180));
		controlPanel.setPreferredSize(new Dimension(100, 180));
		//////////////////////

		this.add(controlPanel);

		imagePanel = new ColorImageWithPPPanel(img, colorMap, markPixels);
                
                
                ///Added by Meghna & Abhishek to reset background size
		imagePanelHolder = new JScrollPane(imagePanel); 
                imagePanelHolder.setPreferredSize(new Dimension (img.getNumColumns(), img.getNumRows()));
                this.add(imagePanelHolder);
                this.setPreferredSize(new Dimension ((int)(controlPanel.getPreferredSize().getWidth()+imagePanelHolder.getPreferredSize().getWidth()*7),(int)(Math.max(controlPanel.getPreferredSize().getHeight(), imagePanelHolder.getPreferredSize().getHeight())) ));
                ///End Added by Meghna & Abhishek to reset background size
                
		

            /////////////////////
		// added by Meghna 
		refresh();
		/////////////////////
	}

        public FalseColorPanel(Table img, Table markPixels, int WIPP){

		originalImage = img;
                //added by Abhishek Singh
                Dimension d;
                d =  this.getPreferredSize();

		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		controlPanel = new JPanel();
		controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
		controlPanel.add(Box.createVerticalStrut(15));

		colorControlPanel = new JPanel();
		colorControlPanel.setLayout(
			new BoxLayout(colorControlPanel, BoxLayout.X_AXIS));

		range = this.getRange(img);
		colorMap = new LinearColorMap(range[0], range[1]);
		// colorMap = new LogColorMap(range[0], range[1], 2);
                //colorMap.setMaxIntensity(colorMap.getMaxIntensity()*2);
                //colorMap.setMinIntensity(colorMap.getMinIntensity()/2);

		colorBar = new ColorBarWidget(range[0], range[1], colorMap);
		this.userImgMin = range[0];
		this.userImgMax = range[1];
 
		colorControlPanel.add(colorBar);

		controls = new SliderControlPanel(this);
		colorControlPanel.add(controls);

		controlPanel.add(colorControlPanel);
		controlPanel.add(Box.createVerticalStrut(15));

		//the zoom buttons go below the color control widgets
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		zoomInButton = new JButton("+");
		zoomOutButton = new JButton("-");
		zoomInButton.addActionListener(this);
		zoomOutButton.addActionListener(this);
		buttonPanel.add(zoomInButton);
		buttonPanel.add(zoomOutButton);
		controlPanel.add(buttonPanel);
		

		//////////////////////
		// added by Meghna
		controlPanel.setMaximumSize(new Dimension(100, 2000));
		controlPanel.setPreferredSize(new Dimension(100, 200));
		//////////////////////

		this.add(controlPanel);

		imagePanel = new ColorImageWithWIPPPanel(img, colorMap, markPixels);
                
                ///Added by Meghna & Abhishek to reset background size
		imagePanelHolder = new JScrollPane(imagePanel); 
                imagePanelHolder.setPreferredSize(new Dimension (img.getNumColumns(), img.getNumRows()+5));
                this.add(imagePanelHolder);
                this.setPreferredSize(new Dimension ((int)(controlPanel.getPreferredSize().getWidth()+imagePanelHolder.getPreferredSize().getWidth()*3),(int)(Math.min(controlPanel.getPreferredSize().getHeight(), imagePanelHolder.getPreferredSize().getHeight())) ));
                ///End Added by Meghna & Abhishek to reset background size
                
		

            /////////////////////
		// added by Meghna 
		refresh();
		/////////////////////
	}
	/**
		a constructor for when you also want to mark locations
		(such as wells).

		@param img the table that represents the image
		@param markPixels a table with pixel locations to mark in the
			image.
		@param circlePixelSet an array of indices into markPixels that
		indicate which pixels should be represented by circles. Any
		others will be represented by crosses.
	*/
	public FalseColorPanel(Table img, Table markPixels,
		int[]	circlePixelSet){

		this(img);
		//this is dumb - a regular colorimagepanel is made, only
		//to be destroyed before it's displayed (in the other
		//constructor - needs to be cleaned up
		this.remove(imagePanelHolder);
		
                imagePanel = new ColorImageWithWellsPanel(img, colorMap,
			markPixels, circlePixelSet);
		imagePanelHolder = new JScrollPane(imagePanel);
		this.add(imagePanelHolder);

                ///Added by Meghna & Abhishek to reset background size
		imagePanelHolder = new JScrollPane(imagePanel);
                imagePanelHolder.setPreferredSize(new Dimension (img.getNumColumns(), img.getNumRows()));
		this.add(imagePanelHolder);
                this.setPreferredSize(new Dimension ((int)(controlPanel.getPreferredSize().getWidth()+imagePanelHolder.getPreferredSize().getWidth()*7),(int)(Math.max(controlPanel.getPreferredSize().getHeight(), imagePanelHolder.getPreferredSize().getHeight())) ));
                /// end change

		refresh();
	}
	/**
		a constructor for when you also want to mark locations
		(such as wells).

		@param img the table that represents the image
		@param markPixels a table with pixel locations to mark in the
			image.
		@param circlePixelSet an array of indices into markPixels that
		indicate which pixels should be represented by circles. Any
		others will be represented by crosses.
	*/
        
	  //////////////////////////////////////
	  // added by Meghna 

        public double getUserImageMin (){
          return userImgMin;
        }
        public double getUserImageMax (){
          return userImgMax;
        }
        public void setUserImageMin (double d){
                userImgMin = d;
                colorBar.setUserImageMin(d);
                colorMap.setMinIntensity(d);
                colorBar.repaint();
                refresh();
        }
        public void setUserImageMax (double d){
                userImgMax = d;
                colorBar.setUserImageMax(d);
                colorMap.setMaxIntensity(d);
                colorBar.repaint();
                refresh();
        }
	  ////////////////////////////////////////

      ////////////////////////////////////////
      // changes made by meghna
	/** this method assumes it will receive an int between 0 and 100
	representing the percentage of the way up the slider has been set */
	public void setSliderMin(int smn){
		double adjustedMin = colorBar.getUserImageMin(); //range[0];
		//adjustedMin +=  (range[1] - range[0]) * ((double)smn / 100.0d);
                adjustedMin +=  (colorBar.getUserImageMax() - colorBar.getUserImageMin()) * ((double)smn / 100.0d);
		//System.out.println("Slider Min:"+smn+" adjusted:"+adjustedMin);
		colorMap.setMinIntensity(adjustedMin);
		colorBar.setSliderMin(adjustedMin);
		refresh();
	}
	/** this method assumes it will receive an int between 0 and 100
	representing the percentage of the way up the slider has been set */
	public void setSliderMax(int smx){
		double adjustedMax = colorBar.getUserImageMin(); //range[0];
		// adjustedMax +=  (range[1] - range[0]) * ((double)smx / 100.0d);
                adjustedMax +=  (colorBar.getUserImageMax() - colorBar.getUserImageMin()) * ((double)smx / 100.0d);
		//System.out.println("Slider Max:"+smx+" adjusted:"+adjustedMax);
		colorMap.setMaxIntensity(adjustedMax);
		colorBar.setSliderMax(adjustedMax);
		refresh();
	}
      ////////////////////////////////////

	/**
		repaints the false color images after settings have been changed
	*/
	private void refresh(){
		/*try{
		iPan.BuildImagePanel(
			createFalseColorImg(originalImage, bandIndex, colorMap));
		}catch(Exception e){
		}*/
		//System.out.println("Refresh");
		imagePanel.updateColoring(colorMap);
		imagePanelHolder.revalidate();
		this.repaint();

	}

	/**
		finds the min and max of the intensity values of a grayscale
		image. Will return the range as <code>double</code>'s,
		regardless of the primitive type of the image.

		@param img the Image/Table to scan

		@return a double array where [0] is the min and [1] is the max
	*/

	public double[] getRange(Table img){

		double[] range = new double[2];
		int min = 0;
		int max = 1;
		range[min]= Double.MAX_VALUE;
		range[max]=-1* Double.MAX_VALUE;

		double val;
		int i, j;
		int numCols = img.getNumColumns();
		int numRows = img.getNumRows();

		for(i = 0; i < numCols; i++){
			for(j = 0; j < numRows; j++){

				val = img.getDouble(j, i);
				if(val<range[min]){
					range[min]=val;
				}
				if(val>range[max]){
					range[max]=val;
				}
			}
		}
		if(debug){
			System.out.println(" Min:"+range[min]+" Max:"+range[max]);
		}
		return range;
	}

	public void actionPerformed(ActionEvent event){
		if(event.getSource() == zoomInButton){
			imagePanel.zoomIn();
		}else if(event.getSource() == zoomOutButton){
			imagePanel.zoomOut();
		}
		refresh();
	}
}//FalseColor

