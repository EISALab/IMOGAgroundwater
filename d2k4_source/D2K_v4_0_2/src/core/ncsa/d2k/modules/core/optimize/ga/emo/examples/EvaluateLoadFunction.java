package ncsa.d2k.modules.core.optimize.ga.emo.examples;


import ncsa.d2k.modules.core.optimize.util.*;
import ncsa.d2k.modules.core.optimize.ga.*;

/**
	Evaluate the new population. The population object does all the work,
	this module will simply invoke the <code>evaluateAll</code> method of the population.
*/
public class EvaluateLoadFunction extends EvaluateModule {

	//////////////////////////////////
	// Info methods
	//////////////////////////////////

	/**
		This method returns the description of the module.
		@return the description of the module.
	*/
	public String getModuleInfo () {
		return "<html>  <head>      </head>  <body>    Evalute this F1 population.  </body></html>";
	}

	/**
		Evaluate the individuals in this class. Here we are simply maxing
		the objective function.
	*/
	double c1 = 0.0;
	double c2 = 0.0;
	double s1 = 0.0;
	double s2 = 0.0;
	double s3 = 0.0;
	double f1 = 0.0;
	double f2 = 0.0;
	MOSolution mo = null;

	// Compute constraints and check values.
	private void computeConstraints (double [] x) {
		int count =0;
		int ab =0;
		for(ab=0;ab<3;ab++){
			if(x[ab] <= 0)
				count+=1;
		}
		c1=count*3;
		count=0;
		if(Math.abs(s1)>5000)
			count+=1;
		if(Math.abs(s2)>20000)
			count+=1;
		if(Math.abs(s3)>5000)
			count+=1;
		c2=count;

		mo.setObjective (0, mo.getObjective (0) + /*(c1*1e7) +*/ (c2*1e5));
		mo.setObjective (1, mo.getObjective (1) + /*(c1*1e7) +*/ (c2*1e5));
	}

	public void evaluateIndividual (Individual member) {
		this.mo = (MOSolution) member;
		double [] x = (double []) member.getParameters ();
		if(x[0]>0 && x[1]>0 && x[2]>0){
			all_present(x);
			computeConstraints (x);
			return;
		}

		if(x[0]>0 && x[1]>0){
			struct1(x);
			computeConstraints (x);
			return;
		}

		if(x[1]>0 && x[2]>0){
			struct2(x);
			computeConstraints (x);
			return;
		}

		if(x[0]>0 && x[2]>0){
			struct3(x);
			computeConstraints (x);
			return;
		}

		if(x[0]==0 && x[1]==0 && x[2]==0){
			mo.setObjective (0, 1e9);
			mo.setObjective (1, 1e9);
			return;
		}
		mo.setObjective (0, 1e9);
		mo.setObjective (1, 1e9);
	}

	void all_present(double [] x){

		double u1,v1,u2,v2,u3,v3,d1,d2,d3;
		double pu1,pv1,pu2,pv2,pu3,pv3,e,pu,pv;

		pu1 = -14142.135;
		pv1 = 14142.135;
		pu2 = 0;
		pv2 = 30000;
		pu3 = 28284.2712;
		pv3 = 28284.2712;

		u1 = 10*((x[0] + 2*Math.sqrt(2)*x[1] + x[2])*pu1 + (x[0]-x[2])*pv1)/(1e7*(x[0]*x[1] + Math.sqrt(2)*x[0]*x[2] + x[1]*x[2]));
		v1 = 10*(pv1*(x[0]+x[2]) - (x[0]-x[2])*pu1)/(1e7*(x[0]*x[1] + Math.sqrt(2)*x[0]*x[2] + x[1]*x[2]));
		u2 = 10*((x[0] + 2*Math.sqrt(2)*x[1] + x[2])*pu2 + (x[0]-x[2])*pv2)/(1e7*(x[0]*x[1] + Math.sqrt(2)*x[0]*x[2] + x[1]*x[2]));
		v2 = 10*(pv2*(x[0]+x[2]) - (x[0]-x[2])*pu2)/(1e7*(x[0]*x[1] + Math.sqrt(2)*x[0]*x[2] + x[1]*x[2]));
		u3 = 10*((x[0] + 2*Math.sqrt(2)*x[1] + x[2])*pu3 + (x[0]-x[2])*pv3)/(1e7*(x[0]*x[1] + Math.sqrt(2)*x[0]*x[2] + x[1]*x[2]));
		v3 = 10*(pv3*(x[0]+x[2]) - (x[0]-x[2])*pu3)/(1e7*(x[0]*x[1] + Math.sqrt(2)*x[0]*x[2] + x[1]*x[2]));

		d1 = u1*u1 + v1*v1;
		d2 = u2*u2 + v2*v2;
		d3 = u3*u3 + v3*v3;

		if(d1>d2 && d1>d3){
				pu = pu1;
				pv = pv1;
				mo.setObjective (0, Math.sqrt(d1));
//              printf("U v %f %f\n",u1,v1);
		}
		else if(d2>d3 && d2>d1){
				pu = pu2;
				pv = pv2;
				mo.setObjective (0, Math.sqrt(d2));
//              printf("U v %f %f\n",u2,v2);
		}
		else{
				pu = pu3;
				pv = pv3;
				mo.setObjective (0, Math.sqrt(d3));
//              printf("U v %f %f\n",u3,v3);
		}

//              printf("U v %f %f\n",pu,pv);
		mo.setObjective (1, (Math.sqrt(2)*(x[0]+x[2]) + x[1]));
		s1 = ((Math.sqrt(2)*x[1] + x[2])*pu + x[2]*pv)/(x[0]*x[1] + Math.sqrt(2)*x[0]*x[2] + x[1]*x[2]);
		s2 = (pv*(x[0]+x[1]) - (x[0]-x[2])*pu)/(x[0]*x[1] + Math.sqrt(2)*x[0]*x[2] + x[1]*x[2]);
		s3 =  ((Math.sqrt(2)*x[1] + x[0])*pu*(-1) + x[0]*pv)/(x[0]*x[1] + Math.sqrt(2)*x[0]*x[2] + x[1]*x[2]);
//      printf("%f %f %f %f %f\n",f[0],f[1],s1,s2,s3);
	}

	void struct1(double [] x){

		double u1,v1,u2,v2,u3,v3,d1,d2,d3;
			double pu1,pv1,pu2,pv2,pu3,pv3,e,pu,pv;

		pu1 = -14142.135;
		pv1 = 14142.135;
		pu2 = 0;
		pv2 = 30000;
		pu3 = 28284.2712;
		pv3 = 28284.2712;

		u1 = 10*(2*Math.sqrt(2)*pu1/x[0] - (pv1-pu1)/x[1])/1e7;
		u2 = 10*(2*Math.sqrt(2)*pu2/x[0] - (pv2-pu2)/x[1])/1e7;
		u3 = 10*(2*Math.sqrt(2)*pu3/x[0] - (pv3-pu3)/x[1])/1e7;
		v1 = 10*(pv1-pu1)/(x[1]*1e7);
		v2 = 10*(pv2-pu2)/(x[1]*1e7);
		v3 = 10*(pv3-pu3)/(x[1]*1e7);

		d1 = u1*u1 + v1*v1;
		d2 = u2*u2 + v2*v2;
		d3 = u3*u3 + v3*v3;

		if(d1>d2 && d1>d3){
				pu = pu1;
				pv = pv1;
				mo.setObjective (0, Math.sqrt(d1));
//              printf("U v %f %f\n",u1,v1);
		}
		else if(d2>d3 && d2>d1){
				pu = pu2;
				pv = pv2;
				mo.setObjective (0, Math.sqrt(d2));
//              printf("U v %f %f\n",u2,v2);
		}
		else{
				pu = pu3;
				pv = pv3;
				mo.setObjective (0, Math.sqrt(d3));
//              printf("U v %f %f\n",u3,v3);
		}

//              printf("U v %f %f\n",pu,pv);
		mo.setObjective (1, (Math.sqrt(2)*x[0] + x[1]));

		s1 = pu*Math.sqrt(2)/x[0];
		s2 = (pv-pu)/x[1];
		s3 = 0;

	}

	void struct2(double []x){

		double u1,v1,u2,v2,u3,v3,d1,d2,d3;
			double pu1,pv1,pu2,pv2,pu3,pv3,e,pu,pv;

		pu1 = -14142.135;
		pv1 = 14142.135;
		pu2 = 0;
		pv2 = 30000;
		pu3 = 28284.2712;
		pv3 = 28284.2712;

		u1 = 10*(-2*Math.sqrt(2)*pu1/x[2] + (pv1+pu1)/x[1])/1e7;
		u2 = 10*(-2*Math.sqrt(2)*pu2/x[2] + (pv2+pu2)/x[1])/1e7;
		u3 = 10*(-2*Math.sqrt(2)*pu3/x[2] + (pv3+pu3)/x[1])/1e7;
		v1 = 10*(pv1+pu1)/(x[1]*1e7);
		v2 = 10*(pv2+pu2)/(x[1]*1e7);
		v3 = 10*(pv3+pu3)/(x[1]*1e7);

		d1 = u1*u1 + v1*v1;
		d2 = u2*u2 + v2*v2;
		d3 = u3*u3 + v3*v3;

		if(d1>d2 && d1>d3){
				pu = pu1;
				pv = pv1;
				mo.setObjective (0, Math.sqrt(d1));
//              printf("U v %f %f\n",u1,v1);
		}
		else if(d2>d3 && d2>d1){
				pu = pu2;
				pv = pv2;
				mo.setObjective (0, Math.sqrt(d2));
//              printf("U v %f %f\n",u2,v2);
		}
		else{
				pu = pu3;
				pv = pv3;
				mo.setObjective (0, Math.sqrt(d3));
//              printf("U v %f %f\n",u3,v3);
		}

 //             printf("U v %f %f\n",pu,pv);
		mo.setObjective (1, (Math.sqrt(2)*x[2] + x[1]));

		s1 = 0;
		s2 = (pv+pu)/x[1];
		s3 = -Math.sqrt(2)*pu/x[2];

	}

	void struct3(double [] x){

		double u1,v1,u2,v2,u3,v3,d1,d2,d3;
			double pu1,pv1,pu2,pv2,pu3,pv3,e,pu,pv;

		pu1 = -14142.135;
		pv1 = 14142.135;
		pu2 = 0;
		pv2 = 30000;
		pu3 = 28284.2712;
		pv3 = 28284.2712;

		u1 = 10*((pu1+pv1)/x[0] - (pv1-pu1)/x[2])/(Math.sqrt(2)*1e7);
		u2 = 10*((pu2+pv2)/x[0] - (pv2-pu2)/x[2])/(Math.sqrt(2)*1e7);
		u3 = 10*((pu3+pv3)/x[0] - (pv3-pu3)/x[2])/(Math.sqrt(2)*1e7);
		v1 = 10*((pv1-pu1)/x[2] + (pu1+pv1)/x[0])/(Math.sqrt(2)*1e7);
		v2 = 10*((pv2-pu2)/x[2] + (pu2+pv2)/x[0])/(Math.sqrt(2)*1e7);
		v3 = 10*((pv3-pu3)/x[2] + (pu3+pv3)/x[0])/(Math.sqrt(2)*1e7);

		d1 = u1*u1 + v1*v1;
		d2 = u2*u2 + v2*v2;
		d3 = u3*u3 + v3*v3;

		if(d1>d2 && d1>d3){
				pu = pu1;
				pv = pv1;
				mo.setObjective (0, Math.sqrt(d1));
//              printf("U v %f %f\n",u1,v1);
		}
		else if(d2>d3 && d2>d1){
				pu = pu2;
				pv = pv2;
				mo.setObjective (0, Math.sqrt(d2));
//              printf("U v %f %f\n",u2,v2);
		}
		else{
				pu = pu3;
				pv = pv3;
				mo.setObjective (0, Math.sqrt(d3));
//              printf("U v %f %f\n",u3,v3);
		}
  //            printf("U v %f %f\n",pu,pv);

		mo.setObjective (1, (Math.sqrt(2)*(x[0]+x[2]) ));

		s1 = (pu+pv)/(Math.sqrt(2)*x[0]);
		s2 = 0;
		s3 = (pv-pu)/(Math.sqrt(2)*x[2]);

	}

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "Evaluate Population";
	}

	/**
	 * Return the human readable name of the indexed input.
	 * @param index the index of the input.
	 * @return the human readable name of the indexed input.
	 */
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "population";
			default: return "NO SUCH INPUT!";
		}
	}

	/**
	 * Return the human readable name of the indexed output.
	 * @param index the index of the output.
	 * @return the human readable name of the indexed output.
	 */
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "population";
			default: return "NO SUCH OUTPUT!";
		}
	}
}
