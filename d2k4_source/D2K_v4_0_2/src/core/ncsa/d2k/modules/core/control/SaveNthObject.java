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

	int objectCount = 4;

	int objectIndex = 4;

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
		if(this.getFlags()[0] > 0){
			return true;
		}
		if(lastPushed && (this.getFlags()[1] > 0) && (this.getFlags()[2] > 0) ){
			return true;
		}
		return false;
	}

	public void endExecution(){
		super.endExecution();
		savedObjs = null;
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
			/*if(savedObjs.size()>1){
				savedObjs.remove(0);
			}*/
			if(numfires>=maxfires){
				pushOutput(savedObjs.get(objectIndex), 0);
				reset();
				numfires=0;
			}
		}else{
		if(this.getFlags()[0]>0){
			savedObjs.add(pullInput(0));
				numfires++;
			if(debug)
				System.out.println("numfires:"+numfires);
	
		}
		if(lastPushed && (this.getFlags()[1] > 0) && (this.getFlags()[2] > 0)){
			
			objectCount = ((Integer)pullInput(1)).intValue();
			objectIndex = ((Integer)pullInput(2)).intValue();
			maxfires = objectCount;
			
			lastPushed=false;
			
			if(debug)
				System.out.println(this.getAlias() + "maxfires set to "+maxfires);
			return;
		}

		if(!lastPushed && (objectIndex == numfires)){
			pushOutput(savedObjs.get(objectIndex - 1), 0);
		}
		
		if(numfires >= maxfires){
			if(debug)
					System.out.println(this.getAlias()+" numfires >= maxfires");
			numfires -= maxfires;
			
			if(savedObjs.size() != 0){
				if(debug)
						System.out.println(getAlias()+" Pushing");
						
				//remember that objectIndex starts at 1, not 0		
				//pushOutput(savedObjs.get(objectIndex - 1), 0);
				Vector t = savedObjs;
				
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
		return "Consider n objects passing into this module, with indices " +
		" 1 through n. Of those, only the object at index k is passed on." +
		" After n objects pass through, the process starts over. " +
		"Note that the k'th object is not passed until all n objects have been" +
		" pulled in." +
		"The values " +
		"of n (called objectCount) and k (called objectIndex) can either both" +
		" be passed in through the second and third inputs, or can be set in " +
		"properties. Whether the module will wait for the input versions or " +
		"will use those in properties is set by the usePropObjectCount property.";
	}
	
   	public String getModuleName() {
		return "SaveNthObject";
	}
	public String[] getInputTypes(){
		String[] types = {"java.lang.Object",
		"java.lang.Integer",
		"java.lang.Integer"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: return "A stream of objects";
			case 1: return "Number of objects to pull in before reseting";
			case 2: return "The index (out of [1,n] of the objects that will" +
			"be passed on.";
			default: return "No such input";
		}
	}
	
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Saved Object";
			case 1:
				return "Object Count (n)";
			case 2:
				return "Object Index (k)";
			default: return "NO SUCH INPUT!";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {"java.lang.Object"};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: return "The kth object passed in";
			default: return "No such output";
		}
	}
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "Kth object";
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
	public int getObjectIndex(){
		return objectIndex;
	}
	public void setObjectIndex(int i){
		objectIndex = i;
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
			
					

			

								
	
