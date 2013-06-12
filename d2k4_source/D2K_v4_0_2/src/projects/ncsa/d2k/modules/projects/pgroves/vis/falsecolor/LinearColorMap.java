package ncsa.d2k.modules.projects.pgroves.vis.falsecolor;

import java.awt.Color;
/**
	a color map that linearly scales the the four d2k colors blue, green,
	yellow, red.

	@author Peter Groves
	@date 01/24/04
*/

public class LinearColorMap extends FalseColorMap{

	/** hold the rgb values of the base colors for the gradient
		[i][j], where i = colorid, j=red, green, blue
		[0]=red
		[1]=green
		[2]=blue*/
	int[][] baseColors;
	int numCols;
	public LinearColorMap(double minimum, double maximum){
		super(minimum, maximum);
		initColorBase();
	}

	/** currently using d2k module colors. Determines all color information 
	about the colors that will be used. 
	*/
	private void initColorBase(){
		numCols = 4;
		baseColors= new int[numCols][3];
		//red
		baseColors[3][redComp] = D2KColors.D2K_RED.getRed();
		baseColors[3][greenComp] = D2KColors.D2K_RED.getGreen();
		baseColors[3][blueComp] = D2KColors.D2K_RED.getBlue();

		//yellow 
		baseColors[2][redComp] = D2KColors.D2K_YELLOW.getRed();
		baseColors[2][greenComp] = D2KColors.D2K_YELLOW.getGreen();
		baseColors[2][blueComp] = D2KColors.D2K_YELLOW.getBlue();
		
		//green
		baseColors[1][redComp] = D2KColors.D2K_GREEN.getRed();
		baseColors[1][greenComp] = D2KColors.D2K_GREEN.getGreen();
		baseColors[1][blueComp] = D2KColors.D2K_GREEN.getBlue();

		//blue
		baseColors[0][redComp] = D2KColors.D2K_BLUE.getRed();
		baseColors[0][greenComp] = D2KColors.D2K_BLUE.getGreen();
		baseColors[0][blueComp] = D2KColors.D2K_BLUE.getBlue();
	}
	/**
		Given an intensity value from an image, returns a Color object
		based on the current range

		@param val a pixel's intensity values
	*/
	public int[] getColorComponents(double val) {
		
		
		if(val >= max) {
			int col=numCols-1;
			return baseColors[col];
			// new Color(baseColors[col][redComp],
			//	baseColors[col][greenComp],baseColors[col][blueComp]);
		}else if(val <= min) {
			int col=0;
			return baseColors[col];
			//return new Color(baseColors[col][redComp],
			//	baseColors[col][greenComp],baseColors[col][blueComp]);
		}
		
		//find the two base colors our value falls between
		double norm = (val-min)/range;
		norm *= (numCols-1);

		int colLow = (int)norm;
		int colHigh = colLow+1;
		//System.out.println("colLow:"+colLow+" colHigh:"+colHigh+" val:"+val+
		//" norm:"+norm+" min:"+min+" max:"+max+" range:"+range);

		//in a rare case, colHigh can be out of bounds if norm is
		//exactly an integer
		if(colHigh == numCols){
			colLow--;
			colHigh--;
		}
		//interpolate between the two colors

		double frac = norm - (double)colLow;
		int[] rgb = new int[3];
		for(int i=0; i<3; i++){
			rgb[i] = baseColors[colHigh][i] - baseColors[colLow][i];
			rgb[i] = (int)(rgb[i] * frac);
			rgb[i] += baseColors[colLow][i];
		}
		return rgb;
	}


}
