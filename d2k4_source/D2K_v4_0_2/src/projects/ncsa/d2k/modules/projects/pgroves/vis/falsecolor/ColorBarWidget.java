package ncsa.d2k.modules.projects.pgroves.vis.falsecolor;

import java.awt.*;
import javax.swing.*;

/**
	the gui component that shows what the color mapping is. Basically just
	visualizes a FalseColorMap object.

	*/

public class ColorBarWidget extends JComponent {

	/**the range of intensities the band of the image being viewed*/
	double imgMin, imgMax;
	double imgRange;

	/**the range of intensities that indicate the most extreme colors.
	these are the values the sliders (will) set, and also the ones that
	will actually decide how the coloring will be done.*/
	double sliderMin, sliderMax;
	double sliderRange;

	///////////////////////////////////////
      // added by meghna
       /** range of image min and max that the user can specify inorder to view
         *  image between a certain user specified range */
         double userImgMin, userImgMax;
         double userImgRange;
	////////////////////////////////////////

	/**the preferred width the color bar will take on.*/
	int colorbarWidth = 20;

	/** which band in the image should be considered*/
	int band = 0;

	/** dimensions of this component*/
	int preferredWidth=40; //70;
	int preferredHeight= 300;

	/** the number of intensity values that will be included on the
	legend. (they will be equally spaced)*/
	int numStrings = 4;

	/** the object that actually controls the mapping from grayscale to
	color */
	FalseColorMap colorMap;

	/**just used to line up the labels with the black lines */
	int stringOffset = 3;

	/////////////////////////////////////
	// changes made by meghna
	/**
		initialize given the range of intensity values from the image
	*/
	public ColorBarWidget(double min, double max, FalseColorMap coloringMethod) {
		this.setPreferredSize(new Dimension(preferredWidth, preferredHeight));
		//this.setBackground(Color.white);
		imgMin = sliderMin = userImgMin = min;
		imgMax = sliderMax = userImgMax = max;
		sliderRange = sliderMax - sliderMin;
		imgRange = imgMax - imgMin;
                userImgRange = userImgMax - userImgMin;

		colorMap = coloringMethod;
	}
	//////////////////////////////////

	public void setSliderMin(double d){
		sliderMin = d;
		sliderRange = sliderMax - sliderMin;
	}
	public void setSliderMax(double d){
		sliderMax = d;
		sliderRange = sliderMax - sliderMin;
	}


	public void setImageMax(double d){
		imgMax = d;
		imgRange = imgMax - imgMin;
	}

	public void setImageMin(double d){
		imgMin = d;
		imgRange = imgMax - imgMin;
	}

	/////////////////////////////////
	// added by meghna

	public void setUserImageMax(double d){
		userImgMax = d;
		userImgRange = userImgMax - userImgMin;
                setSliderMax(d);
                setImageMax(d);

	}

        public double getUserImageMax (){
                return userImgMax;
        }

	public void setUserImageMin(double d){
		userImgMin = d;
		userImgRange = userImgMax - userImgMin;
                setSliderMin(d);
                setImageMin(d);
	}

        public double getUserImageMin (){
                return userImgMin;
        }

	////////////////////////////////////////

	/** draws the color bar itself, as well as the axis labels.

	*/
	protected void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D)g.create();
		//System.out.println("In paint");
		int x0 = (int)this.getLocation().getX();
		int y0 = (int)this.getLocation().getY()+7;

		int width = this.getWidth();
		int totalHeight = this.getHeight()-14;
		//System.out.println("Height: "+height);

		g2d.clearRect(x0,y0,x0+width,y0+totalHeight);

		int x1,x2,y1,y2;
		x1 = x0 + width - this.colorbarWidth;
		x2 = x0 + width;
		double v = 0;


		//////////////////////////////////////////////
		// changes made by meghna
		//fill in the color bar
		for(int i=0; i<=totalHeight; i++) {
			y1 = y0+(totalHeight-i);
			v = (((double)i/(double)totalHeight) * userImgRange) + userImgMin;

			g2d.setColor(colorMap.getColor(v));
			g2d.drawLine(x1,y1,x2,y1);
		}

		//label the image min and image max locations
		String label;

		g2d.setColor(Color.black);
		x0+=8;
		label = formatDouble(userImgMin);
		g2d.drawString(label, x0, y0 + totalHeight + stringOffset);
		//System.out.println("min label drawn at:"+(y0+totalHeight));

		label = formatDouble(userImgMax);
		g2d.drawString(label, x0, y0  + stringOffset/*+10*/);
		//System.out.println("max label drawn at:"+(y0+10));


		//add the labels that can change
                //System.out.println("Slider Range in paint : " + sliderRange);
                //System.out.println("Slider Min in paint : " + sliderMin);
                //System.out.println("Slider Max in paint : " + sliderMax);
		int variableHeight = (int)(totalHeight * sliderRange / userImgRange/*+10*/);
		int variableMin = (int)(Math.abs((sliderMin - userImgMin)) / userImgRange *
			(double)totalHeight);
               // System.out.println("variable min in paint : " + variableMin);
		int variableInc = variableHeight / (numStrings-1);

		for(int i = 0; i<numStrings; i++){
			y1 = y0 + (totalHeight - (i * variableInc + variableMin));

			v = (double)i / (double)(numStrings - 1) * sliderRange + sliderMin;
			//System.out.println("y1:"+y1+", v:"+v);
			label = formatDouble(v);
			g2d.drawString(label, x0, y1 + stringOffset);
			g2d.drawLine(x1,y1,x2,y1);

		}
		//////////////////////////////////////////////
		g2d.dispose();
	}

	/**
		given a double, returns a properly formatted string for display
		purposes. for now, always shoots for 10 numerals
	*/
	protected String formatDouble(double d){
		if(d >= 1000.0){
			return String.valueOf((int)d);
		}
		else{
			String s = String.valueOf(d);
			if(s.length() < 10){
				return s;
			}else{
				return s.substring(0,5);
			}
		}

	}


}//ColorBar

