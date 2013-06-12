package ncsa.d2k.modules.projects.pgroves.vis.falsecolor;

import java.awt.*;
import java.awt.image.*;
import java.awt.color.*;
import javax.swing.*;
import ncsa.d2k.modules.core.datatype.table.Table;

///////////////////////////////////////
import java.awt.geom.*;
import java.awt.event.*;
import com.sun.media.jai.codec.*;
///////////////////////////////////////

/**
	contains only a false color image. lets you see it

*/
public class ColorImagePanel extends JPanel {
//public class ColorImagePanel extends JPanel implements  MouseListener,MouseMotionListener{

	/** a reused image object */
	public BufferedImage colorImage;

	/** the colorImage scaled to the current zoom level*/
	public Image scaledColorImage;
   
        /////////////////////////////////
        // added by Meghna for zoom

	/** the original or base scaling factor*/
	public final double baseScaleRatio = 1;
	
	/** scale width ratio **/
	public double graphicsScaleWidthRatio;

	/** scale height ratio **/
	public double graphicsScaleHeightRatio;
	//////////////////////////////////

	/** tells how to do the mapping from grayscale to color */
	FalseColorMap colorMap;

	/** the grayscale image */
	Table originalImage;

	public ColorImagePanel(Table img, FalseColorMap colorFunction){

		originalImage = img;
		//ColorModel colorMod = ColorModel.getRGBdefault();
		//ColorSpace colorSpc = colorMod.getColorSpace();
		//int imageType = colorSpc.getType();

		int numRows = originalImage.getNumRows();
		int numCols = originalImage.getNumColumns();
                
		this.setPreferredSize(new Dimension(numCols, numRows));

		colorImage = new BufferedImage(
			numCols, numRows, BufferedImage.TYPE_INT_RGB);


		this.setZoom(baseScaleRatio);
		updateColoring(colorFunction);
	}
	public ColorImagePanel(Table img, FalseColorMap colorFunction, double zoom){

		originalImage = img;
		//ColorModel colorMod = ColorModel.getRGBdefault();
		//ColorSpace colorSpc = colorMod.getColorSpace();
		//int imageType = colorSpc.getType();

		int numRows = originalImage.getNumRows();
		int numCols = originalImage.getNumColumns();
                
		this.setPreferredSize(new Dimension(numCols, numRows));

		colorImage = new BufferedImage(
			numCols, numRows, BufferedImage.TYPE_INT_RGB);


		this.setZoom(zoom);
		updateColoring(colorFunction);
                
	}

	/**
		given the new coloring method, updates the stored BufferedImage

		@param colorFunction a new mapping from grayscale to color
	*/

	public void updateColoring(FalseColorMap colorFunction){
		colorMap = colorFunction;

		WritableRaster rast = colorImage.getRaster();
		int width = originalImage.getNumColumns();
		int height = originalImage.getNumRows();

		double val;
		int i, j;
		int[] colors;
		for(i=0; i < height; i++){
			for(j=0; j < width; j++){

				val = originalImage.getDouble(i, j);
				colors = colorMap.getColorComponents(val);
                                
				rast.setPixel(j, i, colors);
			}
		}

		//update the zoomed image
		width *= this.graphicsScaleWidthRatio;
		
		scaledColorImage = colorImage.getScaledInstance(width, -1, 
			Image.SCALE_DEFAULT);

		//Graphics2D g2d = colorImage.createGraphics();
		//g2d.scale(graphicsScaleWidthRatio , graphicsScaleHeightRatio);
	}

	/**
		zooms in by 10% of the original image size
		*/
	public void zoomIn(){
		this.setZoom(this.graphicsScaleHeightRatio + .1);
	}
	/**
		zooms out by 10% of the original image size
		*/
	public void zoomOut(){
		this.setZoom(this.graphicsScaleWidthRatio - .1);	
	}
	/**
		sets the zoom to a specified amount
		*/
	public void setZoom(double zoomFraction){
		this.graphicsScaleWidthRatio = zoomFraction;	
		this.graphicsScaleHeightRatio = zoomFraction;

		int width = (int)(this.originalImage.getNumColumns() * 
			this.graphicsScaleWidthRatio);
		
		scaledColorImage = colorImage.getScaledInstance(width, -1, 
			Image.SCALE_DEFAULT);
		this.setPreferredSize(new Dimension(
			scaledColorImage.getWidth(this),
			scaledColorImage.getHeight(this)));

	}
			
	/** just draws the image*/

	protected void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D)g.create();
		////////////////////////////
		// added by meghna
		//g2d.scale(graphicsScaleWidthRatio , graphicsScaleHeightRatio);
		////////////////////////////
		g2d.setPaint(Color.lightGray);
		g2d.draw(new Rectangle ((int)(graphicsScaleWidthRatio)*this.getWidth(), 
				(int)(graphicsScaleHeightRatio)*this.getHeight()));
		g2d.fill(new Rectangle ((int)(graphicsScaleWidthRatio)*this.getWidth(), 
				(int)(graphicsScaleHeightRatio)*this.getHeight()));
		
		//g2d.drawImage(colorImage, 0, 0, this);
		g2d.drawImage(scaledColorImage, 0, 0, this);

		g2d.dispose();
	}


}
