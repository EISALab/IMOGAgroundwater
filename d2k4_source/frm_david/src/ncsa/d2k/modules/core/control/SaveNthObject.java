package ncsa.d2k.modules.core.control;


import ncsa.d2k.core.modules.*;
import java.util.Vector;

/*
	@author pgroves
	*/

public class SaveNthObject extends ComputeModule
	{

	//////////////////////
	//d2k Props
	////////////////////
	boolean debug=false;

	int objectCount=4;

	boolean usePropObjectCount=false;

	/////////////////////////
	/// other fields
	////////////////////////

	private Vector savedObjs;
	protected int numfires;
	protected int maxfires;

	boolean lastPushed;

	//////////////////////////
	///d2k control methods
	///////////////////////

	public boolean isReady(){
		//if(inputFlags[0]>0){
		if(getInputPipeSize(0)>0){
			return true;
		}
		if(lastPushed&&(getInputPipeSize(1)>0)){
			return true;
		}
		return false;
	}

	public void endExecution(){
		super.endExecution();
	}
	public void beginExecution(){
		reset();
		super.beginExecution();
	}

	private void reset(){
		//just so it doesn't think it's done before it even starts
		if(usePropObjectCount){
			maxfires=objectCount;
		}else{
			maxfires=Integer.MAX_VALUE;
		}
		lastPushed=true;
		savedObjs=new Vector();
		//numfires=0;
		//savedObj=null;
	}

	/////////////////////
	//work methods
	////////////////////
	/*
		does it
	*/
	public void doit() throws Exception{
		if(usePropObjectCount){
			numfires++;
			savedObjs.add(pullInput(0));
			if(savedObjs.size()>1){
				savedObjs.remove(0);
			}
			if(numfires>=maxfires){
				pushOutput(savedObjs.get(0), 0);
				reset();
				numfires=0;
			}
		}else{
		//if(inputFlags[0]>0){
		if(getInputPipeSize(0)>0){
			savedObjs.add(pullInput(0));
				numfires++;
			if(debug)
				System.out.println("numfires:"+numfires);

		}
		//if(lastPushed&&(inputFlags[1]>0)){
		if(lastPushed&&(getInputPipeSize(1)>0)){
			maxfires=((Integer)pullInput(1)).intValue();
			lastPushed=false;
			if(debug)
				System.out.println("maxfires set to "+maxfires);
			return;
		}

		if(numfires>=maxfires){
			if(debug)
					System.out.println(getAlias()+" numfiles>=maxfires");
			numfires-=maxfires;
			if(savedObjs.size()!=0){
				if(debug)
						System.out.println(getAlias()+" Pushing");
				pushOutput(savedObjs.get(maxfires-1), 0);
				Vector t=savedObjs;

				reset();//this will re-init savedObjs

				//put the leftovers in the new vector
				for(int i=maxfires-1; i<t.size();i++){
					savedObjs.add(t.get(i));
				}
			}else
				return;
		}
		}
	}


	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return "<paragraph>  <head>  </head>  <body>    <p>          </p>  </body></paragraph>";
	}

   	public String getModuleName() {
		return "";
	}
	public String[] getInputTypes(){
		String[] types = {"java.lang.Object","java.lang.Integer"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: return "";
			case 1: return "";
			default: return "No such input";
		}
	}

	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Saved Object";
			case 1:
				return "Object Count";
			default: return "NO SUCH INPUT!";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {"java.lang.Object"};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: return "";
			default: return "No such output";
		}
	}
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "The Nth object";
			default: return "NO SUCH OUTPUT!";
		}
	}
	////////////////////////////////
	//D2K Property get/set methods
	///////////////////////////////
	public boolean getDebug(){
		return debug;
	}
	public void setDebug(boolean b){
		debug=b;
	}
	public boolean getUsePropObjectCount(){
		return usePropObjectCount;
	}
	public void setUsePropObjectCount(boolean b){
		usePropObjectCount=b;
	}
	public int getObjectCount(){
		return objectCount;
	}
	public void setObjectCount(int i){
		objectCount=i;
	}
	/*
	public boolean get(){
		return ;
	}
	public void set(boolean b){
		=b;
	}
	public double  get(){
		return ;
	}
	public void set(double d){
		=d;
	}
	public int get(){
		return ;
	}
	public void set(int i){
		=i;
	}
	*/
}







