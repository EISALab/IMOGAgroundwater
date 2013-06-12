package ncsa.d2k.modules.core.transform.attribute;


import java.io.Serializable;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

public class RandomGenerator extends ncsa.d2k.core.modules.DataPrepModule  {

	public String getOutputInfo (int index) {
		switch (index) {
			case 0: return "A DoubleColumn containing the randomly generated numbers";
			default: return "No such output";
		}
	}

	public String getInputInfo (int index) {
		switch (index) {
			default: return "No such input";
		}
	}

	public String getModuleInfo () {
		return "<html>  <head>      </head>  <body>    Produces a set of randomly generated numbers with the distribution     indicated in props.<br>PROPS:    <ul>      <li>        setSize - the size of the set of numbers to generate      </li>      <li>        LowerBound/UpperBound - define the range of the set (make sure they         make sense for the params of the distribution function)      </li>      <li>        DistFamily - the distribution type        <ul>          <li>            0-normal          </li>          <li>            1-Uniform          </li>          <li>            2-Cauchy          </li>          <li>            3-Exponential          </li>          <li>            4-Weibull          </li>          <li>            5-lognormal          </li>          <li>            6-Double Exponential          </li>          <li>            7-Gamma          </li>        </ul>      </li>      <li>        Param1- usually the location, but check the api      </li>      <li>        Param2- usually scale, check the api      </li>      <li>        Param3- sometimes shape, sometimes not used, check api      </li>    </ul>  </body></html>";
	}

	//////////////////////////////////
	// Type definitions.
	//////////////////////////////////

	public String[] getInputTypes () {
		String[] types = {		};
		return types;
	}

	public String[] getOutputTypes () {
		String[] types = {"ncsa.d2k.modules.core.datatype.table.basic.DoubleColumn"};
		return types;
	}

	//set like 'the set of numbers', not like 'set it to 1'
	public int setSize=1000;

	public void setSetSize(int i){
		setSize=i;
	}
	public int getSetSize(){
		return setSize;
	}
	public int distFamily=0;

	public int getDistFamily(){
		return distFamily;
	}
	public void setDistFamily(int i){
		distFamily=i;
	}
	///define the range
	public double lowerBound=-1;
	public double upperBound=1;
	public double getLowerBound(){
		return lowerBound;
	}
	public double getUpperBound(){
		return upperBound;
	}
	public void setLowerBound(double d){
		lowerBound=d;
	}
	public void setUpperBound(double d){
		upperBound=d;
	}


	//going to hold all the params here
	public double[] param=new double[3];
	///p1
	public double getParam0(){
		return param[0];
	}
	public void setParam0(double d){
		param[0]=d;
	}
	////p2
	public double getParam1(){
		return param[1];
	}
	public void setParam1(double d){
		param[1]=d;
	}
	///p3
	public double getParam2(){
		return param[2];
	}
	public void setParam2(double d){
		param[2]=d;
	}

	public void doit(){
		double[] numbers=new double[setSize];
		for(int i=0; i<setSize; i++){
			numbers[i]=DistFunctions.randomGen(param, distFamily, lowerBound, upperBound);
		}
		DoubleColumn col=new DoubleColumn(numbers);
		col.setLabel("RandomNumbers");

		pushOutput(col, 0);

	}

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "RandomGenerator";
	}

	/**
	 * Return the human readable name of the indexed input.
	 * @param index the index of the input.
	 * @return the human readable name of the indexed input.
	 */
	public String getInputName(int index) {
		switch(index) {
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
				return "output0";
			default: return "NO SUCH OUTPUT!";
		}
	}
}








