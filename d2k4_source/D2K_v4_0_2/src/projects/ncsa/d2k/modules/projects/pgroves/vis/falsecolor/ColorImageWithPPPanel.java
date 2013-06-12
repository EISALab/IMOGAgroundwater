package ncsa.d2k.modules.projects.pgroves.vis.falsecolor;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.awt.color.*;
import javax.swing.*;
import ncsa.d2k.modules.core.datatype.table.Table;

/**
	adds circle and tick marks of well locations to the image panel

	@author Peter Groves
	@date 02/19/04
	*/

public class ColorImageWithPPPanel extends ColorImagePanel{

	/** holds the pixel locations of the wells to draw on top
		of the image */
	Table wells;
        //change this is number of PPs change
        int numberPP;

	/**indicates which of the wells will be circles (indices into
	the wells table)*/
	int[] circleWells;

	/**for speed purposes, cache the locations of the cross mark
	wells (exactly every well location that isn't a circleWell*/
	int[] crossWells;


	/** containes the awt circle objects to draw. cached for speed*/
	Ellipse2D[] wellCircleObjects;

	/** containes the awt line objects to draw to make crosses. cached
	for speed. note: there are two lines for every well*/
	Line2D[] wellCrossObjects;

	/**
		the well values that will be marked beside the wells
		*/
	String[] wellLabels;	
	/** the x,y coordinates of the well labels locations.
	the dimensions of this array are [numWells][2]
	*/
	int[][] wellLabelPositions;

	/** how big the well label text should be when zoomed to 1:1
	*/
	double baseFontSize = 10;
	/** how fast the font will change size relative to the rate the
	image changes size when zoomed*/
	double fontScaleRate = .3;
	/**
		same as superclass, but also requires a table containing the
		well locations (in pixel coordinate system, not geographic
		coordinate system). the circleWellLocations is an array of
		indices that indicate which wells will be circles. all other
		wells will be crosses.

		@param img a grayscale image represented in a Table were the
			column represents the x pixel coordinate and the row the y
			pixel coordinate.
		@param colorFunction a color map with info on which color a
			given intensity value should be shown as
		@param wellLocations a Table with a column of x pixel locations
			and one of y pixel locations
		@param circleWellLocations indices into wellLocations about
			which wells should be shown as circles (the rest will be
			crosses). must be in ascending order
	*/
	public ColorImageWithPPPanel(Table img, FalseColorMap colorFunction, Table wellLocations){ //, int[] circleWellLocations){

		super(img, colorFunction);
                
                this.wells = wellLocations;
                     numberPP = 0;
                        
                     for (int i = 0; i < wells.getNumRows();i++){
                        if( wells.getDouble(i, 2) == 0){
                            numberPP++;
                        }  
                     }
                       
		
		//this.circleWells = circleWellLocations;

		updateWellGlyphs();
		
		updateColoring(colorFunction);

	}

	protected void updateWellGlyphs(){
		if(wells == null)
			return;
		
		int numWells = wells.getNumRows();
		int numCircles = 0;//circleWells.length;
		int numCrosses = numWells - numCircles;
		crossWells = new int[numCrosses];

		int i, j, k;
		j = 0;
		k = 0;
		for(i = 0; i < numWells; i++){
			if((j < numCircles) &&
				(circleWells[j] == i)){
				j++;
			}else{
				crossWells[k] = i;
				k++;
			}
		}

		//lets make the graphics geometry objects
		wellCircleObjects = new Ellipse2D[numCircles];
		wellCrossObjects = new Line2D[numCrosses];

		//hold dimensions
		double x, y;
		//make every mark the same size
		double markWidth = 0.01; //10;
		double radius = .01;
		for(i = 0; i < numCircles; i++){
			x = wells.getDouble(circleWells[i], 0) * this.graphicsScaleWidthRatio;
			y = wells.getDouble(circleWells[i], 1) * this.graphicsScaleHeightRatio;
			//System.out.println("Circle Location "+i+": "+x+", "+y);

			wellCircleObjects[i] = new Ellipse2D.Double(
				x - radius * markWidth,
				y - radius * markWidth,
				markWidth,
				markWidth);
		}
		for(i = 0; i < numCrosses; i++){
                        
			x = wells.getDouble(crossWells[i], 0) * this.graphicsScaleWidthRatio; 
			y = wells.getDouble(crossWells[i], 1) * this.graphicsScaleHeightRatio;
			//System.out.println("Cross Location "+i+": "+x+", "+y);

			wellCrossObjects[i] = new Line2D.Double(
				x , y,
				x , y);

			//wellCrossObjects[i + numCrosses] = new Line2D.Double(
			//	x, y ,
			//	x, y );
		}
		
		wellLabelPositions = new int[numWells][2];
		wellLabels = new String[numWells];
		//assume that the labels are always in the third column of the table
		int labelsColumn = 2;

		String label;
		for(i = 0; i < numWells; i++){
			x = wells.getDouble(i, 0) * this.graphicsScaleWidthRatio; 
			x += markWidth;
			y = wells.getDouble(i, 1) * this.graphicsScaleHeightRatio;
			y -= markWidth * radius;
			wellLabels[i] = "    ";//wells.getString(i, labelsColumn);
			wellLabelPositions[i][0] = (int)x;
			wellLabelPositions[i][1] = (int)y;
		}
	}
	/**
		sets the zoom to a specified amount
		*/
	public void setZoom(double zoomFraction){
		super.setZoom(zoomFraction);
		updateWellGlyphs();
	}

		

	public void updateColoring(FalseColorMap colorFunction){
		super.updateColoring(colorFunction);

		/*if(wellCircleObjects != null){
			Graphics2D g2d= colorImage.createGraphics();
			g2d.setPaint(Color.BLACK);
			g2d.setStroke(new BasicStroke(0.01f)); //(new BasicStroke(3.0f));
			g2d.setComposite(AlphaComposite.SrcOver);
			//the circles and crosses
			int numMarks = wellCircleObjects.length;
			int i;
			for(i = 0; i < numMarks; i++){
				g2d.draw(wellCircleObjects[i]);
				//System.out.println(wellCircleObjects[i]);
			}

			numMarks = wellCrossObjects.length;
			for(i = 0; i < numMarks; i++){
				g2d.draw(wellCrossObjects[i]);
			}
			g2d.dispose();
		}*/
	}


	/**
		overridden from the superclass so that it also draws the wells.
		*/

	protected void paintComponent(Graphics g){
		//super.paintComponent(g);
		//System.out.println("In paint");

		Graphics2D g2d = (Graphics2D)g.create();
		//////////////////////////////////
		// added by meghna
		/*g2d.scale(graphicsScaleWidthRatio , graphicsScaleHeightRatio);*/
		///////////////////////////////////
		g2d.setPaint(Color.lightGray);
		g2d.draw(new Rectangle ((int)(graphicsScaleWidthRatio)*this.getWidth(), 
				(int)(graphicsScaleHeightRatio)*this.getHeight()));
		g2d.fill(new Rectangle ((int)(graphicsScaleWidthRatio)*this.getWidth(), 
				(int)(graphicsScaleHeightRatio)*this.getHeight()));

		int width = (int)(originalImage.getNumColumns() * 
			this.graphicsScaleWidthRatio);
		//Image scaledColorImage = colorImage.getScaledInstance(width, -1, 
		//	Image.SCALE_DEFAULT);
		//g2d.drawImage(colorImage, 0, 0, this);
		g2d.drawImage(scaledColorImage, 0, 0, this);

		g2d.setStroke(new BasicStroke(2.0f));
		//g2d.setPaint(Color.DARK_GRAY);
		//g2d.setComposite(AlphaComposite.SrcOver);
		//the circles and crosses
		int numMarks = wellCircleObjects.length;
		int i;
		for(i = 0; i < numMarks; i++){
			g2d.draw(wellCircleObjects[i]);
                        
			//System.out.println(wellCircleObjects[i]);
		}

		numMarks = wellCrossObjects.length;
		for(i = 0; i < numMarks; i++){
                        if(i<numberPP){
                            g2d.setPaint(Color.BLACK);
                            g2d.draw(wellCrossObjects[i]);
                        } else{
			    g2d.setPaint(Color.WHITE);
                            g2d.draw(wellCrossObjects[i]);                   
                        }
		}
		numMarks = wellLabels.length;
		//the font size will only increase or decrease at one tenth the rate
		//of the zoom
		Font oldFont = g2d.getFont();
		//this.baseFontSize = oldFont.getSize2D();
		double size = this.baseFontSize * 
				((graphicsScaleWidthRatio - this.baseScaleRatio) * 
				this.fontScaleRate + 1);
		//System.out.println("new font size:" + size +" baseScale:" +
		//	this.baseScaleRatio + " ratio:" + this.graphicsScaleWidthRatio);
		Font newFont = new Font(oldFont.getFontName(), oldFont.getStyle(), 
				(int)size);
		g2d.setFont(newFont);
		for(i = 0; i < numMarks; i++){
			g2d.drawString(wellLabels[i], wellLabelPositions[i][0], 
					wellLabelPositions[i][1]);
		}
		
		g2d.dispose();
	}


}




