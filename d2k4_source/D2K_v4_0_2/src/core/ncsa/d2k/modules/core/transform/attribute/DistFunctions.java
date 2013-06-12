package ncsa.d2k.modules.core.transform.attribute;

/***
	DistFunctions

	Contains static functions that are probability
	density functions for continuous distribution types.
	Can also 'integrate' these functions using a rectangular
	approximation method for area under the curve
	The distribution functions come from the National Institute of Standards'
	Engineering Statistics Handbook.  An online version can be found (as of 5/25/01)
	at http://www.itl.nist.gov/div898/handbook/

	@author Peter Groves
	8/6/01
*/

public class DistFunctions {
	///////////////////
	///CLASS VARIABLES
	////////////////////

	/**gives a count of the number of pdf's defined in this class */
	public static int AVAILABLE_PDF_COUNT=8;

	////////////////////
	//DISTRIBUTION ID'S
	///////////////////

	/**The id for the normal distribution*/
	public static final int NORMAL=0;

	/**The id for the uniformdistribution*/
	public static final int UNIFORM=1;

	/**The id for the cauchy distribution*/
	public static final int CAUCHY=2;

	/**The id for the exponential distribution*/
	public static final int EXPONENTIAL=3;

	/**The id for the weibull distribution*/
	public static final int WEIBULL=4;

	/**The id for the lognormaldistribution*/
	public static final int LOGNORMAL=5;

	/**The id for the double exponential distribution*/
	public static final int DOUBLE_EXPONENTIAL=6;

	/**The id for the gamma distribution*/
	public static final int GAMMA=7;

	/**the theshold accuracy level, always equal to .00001*/
	public static final double threshold=.00001;


	////////////////////
	//Some functions for accessing pdf's
	//and info
	//////////////////

	/** calculatePDF

		just calls the appropriate pdf function, but allows the user
		to access it with an int and not have to type the distribution
		function name

		@param params-the shape/scale/location params to pass to the pdf
		@param x - the value we want to calculate the probability for
		@param distType - which family of distribution we want,
							uses the static int's from this class
							(NORMAL, UNIFORM, etc)
	*/


	public static double calculatePDF(double[] params, double x, int distType){
		switch (distType) {
			case NORMAL:{
				return normal(params, x);
			}
			case UNIFORM:{
				return uniform(params, x);
			}
			case CAUCHY:{
				return cauchy(params, x);
			}
			case EXPONENTIAL:{
				return exponential(params, x);
			}
			case WEIBULL:{
				return weibull(params, x);
			}
			case LOGNORMAL:{
				return lognormal(params, x);
			}
			case DOUBLE_EXPONENTIAL:{
				return doubleExponential(params, x);
			}
			case GAMMA:{
				return gamma(params, x);
			}
			default:{
				System.out.println("DistFunctions, calculatePDF(): invalid dist type, returning normal/gaussian");
				return normal(params, x);
			}
		}
	}


	/**
		getNumParams

		returns the number of params the desired distribution needs
		and therefore how long the params array should be that is
		past to that distribution when called

		@param distType - the distribution family name, uses the
							static int's from this class(NORMAL, UNIFORM, etc)

	*/

	public static int getNumParams(int distType){
		switch (distType){
			case NORMAL:{
				return 2;
			}
			case UNIFORM:{
				return 2;
			}
			case CAUCHY:{
				return 2;
			}
			case EXPONENTIAL:{
				return 2;
			}
			case WEIBULL:{
				return 3;
			}
			case LOGNORMAL:{
				return 3;
			}
			case DOUBLE_EXPONENTIAL:{
				return 2;
			}
			case GAMMA:{
				return 3;
			}
			default:{
				System.out.println("DistFunctions, getNumParams(): invalid dist type, returning normal/gaussian");
				return 2;
			}
		}
	}

	/**
		distributionIntegrate

		calls the theortetical density functions to find an 'integrated' value
		of some interval (between 'high' and 'low'.  the number of
		trapezoids used to approximate the area under the curve
		is determined by trapCount(tc). Will also test to see if the
		threshold accuracy (the field theshold in this class) is reached at
		each iteration.  Uses Romberg Integration as described in:

		Akai, Applied Numerical Methods for Engineers, 1994


		@param params-the shape/scale/location params to pass to the pdf
		@param distType - which family of distribution we want,
							uses the static int's from this class
							(NORMAL, UNIFORM, etc)
		@param low-the lower bound of integration
		@param high - the upper bound of integration
		@param tC - the the number of trapezoids to use in the integration approximation
						must be a power of 2, but the algorithm will automatically round up to
						the nearest one if it's not



	*/
	public static double distributionIntegrate(double[] params, int distType, double low, double high, int tC){
		int trapCount=tC;

	///////////////////////////////////
		//force trapCount to a power of 2

		//the power of 2 base
		double b =Math.log(trapCount)/Math.log(2);
		b=Math.ceil(b);
		int base=(int)b;
		trapCount=(int)Math.pow(2, base);

		//if there is only one trap, base is 0 but
		//we still want to be able to hold one value
		//in the arrays
		if(base==0){
			base=1;
		}

	/////////////////////////////////////
		//the width each trapezoid will have
		double hStep=(high-low)/trapCount;

		//store all the calls to the function in here
		double[] funcPoints=new double[trapCount+1];

		//the point to evaluate the function on
		double x=low;

		//find all the function evaluation points
		for(int s=0; s<funcPoints.length; s++){
			funcPoints[s]=calculatePDF(params, x, distType);
			if(funcPoints[s]<0){
				return Double.NaN;
			}
			x+=hStep;
		}

		//hold the trapezoid areas and extrapolated integrations
		//[a][b]- 'a' indicates how many trapezoids(2^a),
		//  	  'b' indicates the order of extrapolation
		double[][] rombergData=new double[base][base];

		//fill in the first row with trap area info
		//////
		//the running tally of the fp's we've added up
		double fpsum=0;
		//the width of the trapezoids we're currently considering
		double h=hStep*trapCount;
		//the current index stepsize
		int currentStep=(int)trapCount/2;
		//the previous index stepsize
		int previousStep=trapCount;

		//the first point
		fpsum+=.5*funcPoints[0];
		//the last point
		fpsum+=.5*funcPoints[trapCount];
		//the first data entry
		rombergData[0][0]=fpsum*h;


		//now the rest of the trap summations,
		//using fpsum as a tally so that no additions are done
		//more than once
		for(int t=1; t<base; t++){
			int i=currentStep;

			while(i<trapCount){
				fpsum+=funcPoints[i];
				i+=previousStep;
			}
			h/=2;
			rombergData[t][0]=fpsum*h;
			previousStep=currentStep;
			currentStep/=2;
		}

		//now fill in the rest of the values with extrapolations, checking
		//to make sure there is a significant change each time
		for(int i=1; i<base; i++){	//the [b] in rombergData, skipping the first b/c
									//its filled in with trap areas
			for(int j=i; j<base; j++){ // the [a], or row
				rombergData[j][i]=rombergData[j][i-1]+
									(rombergData[j][i-1]-rombergData[j-1][i-1])/
									(Math.pow(4, i)-1);
				if(j==i){
					if((rombergData[i][j]-rombergData[i-1][j-1])>threshold){
						//if(verbose){
						//	System.out.println("DistFunction, Integrate: halted when threshold accuracy reached"+
					//							"after iteration "+i+" of "+base);
						//}
						return rombergData[i][j];
					}
				}
			}
		}
		return rombergData[base-1][base-1];

	}

	/** cummulative distribution

		uses distributionIntegrate to calculate the
		CDF value at point x, with the accuracy being
		determined by the class variable threshold

		@param params-the shape/scale/location params to pass to the pdf
		@param distType - which family of distribution we want,
							uses the static int's from this class
							(NORMAL, UNIFORM, etc)
		@param x - the value at which to evaluate the CDF
	*/


	public static double calculateCDF(double[] params, double x, int distType){
		double sum=0;
		double current=1;

		//the method will involve integrating chunks of the curve
		//until the chunks become insignificant so that we can approximate
		//negative infinity, here we have a hack method of finding a chunk size
		//that will hopefully have something to do with the scale of the pdf

		double stepSize=params[0]+2.2342341/3;

		//more hacking
		int trapCount=(int)(stepSize+3.232312445);

		//start at the value of x and move backward
		double high=x;
		double low=x-stepSize;

		//while the threshold isn't reached
		while (current>threshold){
			current=distributionIntegrate(params, distType, low, high, trapCount);
			sum+=current;

			high=low;
			low-=stepSize;
		}

		return sum;
	}

	/**
		randomGen

		generates a random number with a probability
		in the specified distribution.  Uses a very simple
		rejection algorithm as described in
		<u>Numerical Recipes in C</u> section 7.3

		@param params-the shape/scale/location params to pass to the pdf
		@param distType - which family of distribution we want,
							uses the static int's from this class
							(NORMAL, UNIFORM, etc)
		@param lowBound - the lower bound for the output
		@param hiBound - the upper bound for the output
	*/

	public static double randomGen(double[] params, int distType, double lowBound, double hiBound){

		boolean reject=true;
		double rand=0.0;
		double answ=0.0;
		double L=(hiBound-lowBound);

		//java.util.Random uniformGen=new java.util.Random();


		while(reject){

			rand=L*Math.random();

			answ=rand+lowBound;

			//get another random number between 0 and 1, if the number is
			//between 0 and the probability of rand, accept it, if not,
			//reject it

			if(Math.random()<=calculatePDF(params, answ, distType)){
				reject=false;
			}
		}
		return answ;
	}

	////////////////////////////////////////////////////////
	///// Here are the pdf's
	///////////////////////////////////////////////////////

	/**normal

		f(x)=e^{(-(x-a)^2/(2b^2)}/{b(2pi^.5))}
		a-location=params[0]
		b-scale=params[1]
	*/
	public static double normal(double[] params, double x){
		double a=params[0];
		double b=params[1];

		double t2=2*b*b;
		double t1=Math.exp((-1*(x-a)*(x-a))/t2);
		double t3=b*Math.sqrt(2*3.14159265);

		return (t1/t3);

	}

	/**uniform

		f(x)= 1/(b-a)

		a-lowbound=params[0]
		b-highbound=params[1]
	*/

	public static double uniform(double[] params, double x){
		double a=params[0];
		double b=params[1];

		if(a>b){
			b=params[0];
			a=params[1];
		}

		if(x>b||x<a){
			return 0.0;
		}
		return(1/(b-a));
	}

	/**cauchy

		f(x)=1/{a*pi*(1+((x-b)/a)^2)}

		a-scale=params[1]
		b-location=params[0]
	*/

	public static double cauchy(double[] params, double x){
		double a=params[1];//s
		double b=params[0];//t

		double t1=a*3.14159265;
		double t2=(x-b)/a;

		return 1/(t1*(1+t2*t2));
	}

	/**Exponential

		f(x)=(1/b)e^(-(x-a)/b)

		a-location=params[0]
		b-shape=params[1]

	*/
	public static double exponential(double[] params, double x){
		double a=params[0];
		double b=params[1];

		double t1=-1*(x-a)/b;

		return (1/b)*Math.exp(t1);
	}

	/**weibull

		f(x)=(b/a)*{((x-c)/a)^(b-1)}*exp(-((x-c)/a)^b)

		*for x>=c
		a-scale=params[0]
		b-shape=params[1]
		c-location=params[2]

	*/

	public static double weibull	(double[] params, double x){
		double a=params[0];
		double b=params[1];
		double c=params[2];

		if(x<c){
			return 0.0;
		}

		double t1=b/a;
		double tt=(x-c)/a;
		double t2=Math.pow(tt, (b-1));
		double t3=Math.exp(-1*Math.pow(tt, b));

		return t1*t2*t3;

	}

	/**lognormal

		f(x)=1/{(x-t)*s*(2pi)^.5}*exp(-.5*(ln(x-t)-m)^2/s^2)

		s-shape=params[0]
		t-location=params[1]
		m-scale=params[2]

		**this formula comes from Johnson, Kotz, _Continuous Univariate Distributions -1_


	*/
	public static double lognormal(double[] params, double x){
		double s=params[0];
		double t=params[1];
		double m=params[2];

		if(x<t){
			return 0.0;
		}

		double kk=x-t;
		double k1=(Math.log(kk)-m)/s;
		double k2=Math.exp(-.5*k1*k1);
		double k3=kk*s*Math.sqrt(2*3.14159265);

		return k2/k3;
	}

	/**doubleExponential

		f(x) = {exp(-abs((x-a)/b))}/(2b)

		a-location=params[0]
		b-scale=params[1]
	*/

	public static double doubleExponential(double[] params, double x){
		double a=params[0];
		double b=params[1];

		double tt=(x-a)/b;

		if(tt>0){
			tt=-tt;
		}

		double t1=Math.exp(tt);

		return .5*t1/b;
	}



	/**
	a bunch of functions concerning the gamma distribution**
	**/


	/** gamma   - the pdf

		f(x)=[{((x-a)/b)^(g-1)}*exp{-(x-a)/b}]/{b*sub(g)}

			sub(k)=(k-1)*sub(k-1)

		a=location=params[0]
		b=scale=params[1]
		g=shape=params[2]

			g>=1
			x>a
	*/
	public static double gamma(double[] params, double x){
		double a=params[0];
		double b=params[1];
		double g=params[2];

		double j1=(x-a)/b;
		double j2=b*Math.exp(gammaln((double)g));

		return (Math.pow(j1, g-1)*Math.exp(-1*j1))/j2;

	}


	/**
		gammaln

		returns the natural logarithm of the gamma function
		as described in Numerical Recipes in C, sec 6.1
	*/
	public static double gammaln(double xx){

		double x, y, tmp, ser;
		double[] cof =new double[6];
		cof[0]=76.18009172947146;
		cof[1]=-86.50532032941677;
		cof[2]=24.01409824083091;
		cof[3]=-1.231739572450155;
		cof[4]=0.1208650973866179e-2;
		cof[5]=-0.5395239384953e-5;

		y=xx;
		x=xx;
		tmp=x+5.5;
		tmp-= (x+.5)*Math.log(tmp);
		ser=1.000000000190015;
		for(int j=0; j<=5; j++){
			ser += cof[j]/(++y);
		}
		return -tmp+Math.log(2.5066282746310005*ser/x);
	}
	/**
		computes the incomplete gamma function as described in
		Numerical Recipes(NR) in C, section 6.2
		(where it is named gammp(double, double))

		this is the definition contained there, and differs
		from that in the NIST Stats handbook.  this function
		is the equivalent of the NIST Handbook's GammaDistribution
		CDF with the location set to 0, scale to 1, and shape to the
		input 'a'

	*/

	public static double incompleteGamma(double a, double x){
		if((x<0.0)||(a<=0.0)){
			System.err.println("ChiSquared: IncompleteGamma: a="+a+" x="+x+", which is bad");
		}
		if(x<(a+1.0)){
			return gammaSer(a, x);
		}else{
			double gcf= gammaCF(a, x);
			return (1-gcf);
		}
	}

	/**
		gammaSer

		the incomplete gamma function evaluated by its series representation
		from NR in C
		basically a helper function for incompleteGamma(double, double)
	*/

	private static double gammaSer(double a, double x){
		double sum, del, ap;
		double gammser=0.0;
		double gln=gammaln(a);

		int ITMAX=100;
		double EPS=3.0e-7;

		if(x<=0.0){
			if(x==0){
				return 0.0;
			}
			System.err.println("DistFunctions: gammaSer: x<0");
			return -1;
		}
		ap=a;
		del=1.0/a;
		sum=del;
		for(int n=1; n<=ITMAX; n++){
			++ap;
			del *=x/ap;
			sum+=del;
			if(Math.abs(del) < (Math.abs(sum)*EPS)){
				gammser=sum*Math.exp(-x+a*Math.log(x)-gln);
				return gammser;
			}
		}
		System.err.println("DistFunctions: gammaSer: 'a' too large");
		return gammser;
	}

	/**
		gammaCF

		the incomplete gamma function evaluated by its continued fraction representation
		from NR in C
		basically a helper function for incompleteGamma(double, double)
	*/
	private static double gammaCF(double a, double x){
		int ITMAX=100;
		double EPS=3.0e-7;
		double FPMIN=1.0e-30;

		double gln=gammaln(a);

		double an, b, c, d, del, h;

		b=x+1.0-a;
		c=1.0/FPMIN;
		d=1.0/b;
		h=d;

		int i;
		for(i=1; i<=ITMAX; i++){
			an=-i*(i-a);
			b +=2.0;
			d=an*d+b;
			if(Math.abs(d)< FPMIN){
				d=FPMIN;
			}
			c=b+an/c;
			if(Math.abs(c)<FPMIN){
				c=FPMIN;
			}
			d=1.0/d;
			del=d*c;
			h *=del;
			if(Math.abs(del-1.0) <EPS){
				break;
			}
		}
		if(i>ITMAX){
			System.err.println("DistFunctions: gammaCF: 'a' too large, did not converge");
		}
		return Math.exp(-x+a*Math.log(x)-gln)*h;
	}
}

