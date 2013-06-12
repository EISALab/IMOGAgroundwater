package ncsa.d2k.modules.projects.pgroves.vis.falsecolor;

import java.awt.Color;
/**
	abstract class that provides a mapping between a single intensity
	value and an rgb color.

	@author Peter Groves
	@date 01/24/04
*/

abstract public class FalseColorMap extends Object{

	/** The minimum intensity value to consider as a unique value. Any
	intensity value at or below this should return the same color 
	that represents the low end of the scale*/
	double min;
	
	/** The maximum intensity value to consider as a unique value. Any
	intensity value at or above this should return the same color 
	that represents the high end of the scale*/
	double max;


	/** just (max-min) cached */
	double range;

	/** just to keep track of the different components in arrays that
	are passed around*/
	final int redComp = 0;
	/** just to keep track of the different components in arrays that
	are passed around*/
	final int greenComp = 1;
	/** just to keep track of the different components in arrays that
	are passed around*/
	final int blueComp = 2;
	
	public FalseColorMap(double minimum, double maximum){
		min=minimum;
		max=maximum;
		range = max - min;
	}


	/** gives the rgb values in an int array of size 3. values are
	between 0 and 255 */
	abstract public int[] getColorComponents(double intensityValue);


	/** returns a java style color object that corresponds to the
	passed in grayscale value*/
	public Color getColor(double intensityValue){
		int[] comps = getColorComponents(intensityValue);
		return new Color(comps[redComp], comps[greenComp], comps[blueComp]);
	}

	/** returns the rgb color info packed into a single int. for use
	with the default sRGB color space returned by java.awt.image.ColorModel

	@return an int (32 bits) where the first 8 bits are alpha info, next 8
		are red, then green, then blue 
	*/
	public int getPackedColor(double intensityValue){
		int[] comps = getColorComponents(intensityValue);
		int col = /*0xff << 24 |*/ comps[redComp] << 16 | 
			comps[greenComp] << 8 | comps[blueComp];
		return col;	
	}
		

	/////////////////////
	//getters and setters
	////////////////////
	public double getMaxIntensity(){
		return max;
	}
	public void setMaxIntensity(double d){
		max=d;
		range = max - min;
	}
	public double getMinIntensity(){
		return min;
	}
	public void setMinIntensity(double d){
		min=d;
		range = max - min;
	}
}
