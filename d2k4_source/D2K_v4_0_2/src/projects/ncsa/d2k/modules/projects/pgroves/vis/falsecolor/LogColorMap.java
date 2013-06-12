package ncsa.d2k.modules.projects.pgroves.vis.falsecolor;

import java.awt.Color;
/**
	a color map that logarithmically scales the the four d2k colors blue, 
	green, yellow, red.

	@author Peter Groves
	@date 01/24/04
*/

public class LogColorMap extends LinearColorMap{

	/** the base of the logarithm to use. defaults to 'e' (natural log)*/
	double base = Math.E;

	/** to find the log base b of a number, we use the natural log divided
	by this, which is the natural log of b. For natural log (the default),
	this is 1*/
	double logDivisor = 1.0d;

	public LogColorMap(double minimum, double maximum, double logBase){
		super(minimum, maximum);
		base = logBase;
		logDivisor = Math.log(logBase);
		this.setMinIntensity(minimum);
		this.setMaxIntensity(maximum);
	}
	
	public int[] getColorComponents(double val){
		val = Math.log(val);
		val /= logDivisor;
		return super.getColorComponents(val);
	}
	////////////////////////////////////////////////////////
	//getters and setters - overwritten to mask the logarithm
	/////////////////////////////////////////////////////////
	public double getMaxIntensity(){
		double val = super.getMaxIntensity() * logDivisor;
		val = Math.exp(val);
		return val;
	}
	public void setMaxIntensity(double d){
		double lg = Math.log(d);
		lg /= logDivisor;
		super.setMaxIntensity(lg);
	}
	
	public double getMinIntensity(){
		double val = super.getMinIntensity() * logDivisor;
		val = Math.exp(val);
		return val;
	}
	public void setMinIntensity(double d){
		double lg = Math.log(d);
		lg /= logDivisor;
		super.setMinIntensity(lg);
	}

	public void setLogBase(double d){
		double lmin = this.getMinIntensity();
		double lmax = this.getMaxIntensity();

		base = d;
		logDivisor = Math.log(base);

		this.setMinIntensity(lmin);
		this.setMaxIntensity(lmax);
	}
}
