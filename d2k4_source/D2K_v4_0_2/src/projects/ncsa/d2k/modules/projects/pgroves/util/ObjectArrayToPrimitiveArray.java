package ncsa.d2k.modules.projects.pgroves.util;

import java.util.*;
import java.lang.reflect.*;

import ncsa.d2k.core.modules.*;


/**
	Takes an object array and copies the values into either 
	an array of the type the members are, or, if possible,
	the primitive values the objects represent.

	@author Peter Groves
	@date 02/16/03
	*/

public class ObjectArrayToPrimitiveArray extends DataPrepModule 
	implements java.io.Serializable {

	//////////////////////
	//d2k Props
	////////////////////
	
	boolean debug=false;		

	boolean usePrimitiveArrays = false;
	/////////////////////////
	/// other fields
	////////////////////////

	//////////////////////////
	///d2k control methods
	///////////////////////

	public boolean isReady(){
		return super.isReady();
	}
	public void endExecution(){
		wipeFields();
		super.endExecution();
	}
	public void beginExecution(){
		wipeFields();
		super.beginExecution();
	}
	public void wipeFields(){
	}
	
	/////////////////////
	//work methods
	////////////////////
	/*
		does it
	*/
	public void doit() throws Exception{
		if(debug){
			System.out.println(getAlias()+":Firing");
		}
		Object[] objs = (Object[])pullInput(0);
		pushOutput(convertToInstanceArray(objs, usePrimitiveArrays), 0);
	}
	/**
		converts an array of objects into an array of 
		the (most specific) instance types of the member
		objects. if possible, can attempt to transfer to
		an array of primitives (eg. an Object array 
		containing only objects of type java.lang.Integer
		can become an array of Integer Objects or int primitives)

		@param objs an object array
		@param toPrimitives if true, will return an
		array of primitives (if the types support it)
		
		@return an array of the most specific type that all
		of the member objects are instances of
	*/ 
	
	public Object convertToInstanceArray(
		Object[] objs, boolean toPrimitives){

		Class cls = mostSpecificType(objs);
		
		if(toPrimitives){
			
			String nm = cls.getName();
			
			if(nm == "java.lang.Double"){
				cls = Double.TYPE;
			}else if (nm == "java.lang.Integer"){
				cls = Integer.TYPE;
			}else if (nm == "java.lang.Boolean"){
				cls = Boolean.TYPE;
			}else if (nm == "java.lang.Character"){
				cls = Character.TYPE;
			}else if (nm == "java.lang.Byte"){
				cls = Byte.TYPE;
			}else if (nm == "java.lang.Short"){
				cls = Short.TYPE;
			}else if (nm == "java.lang.Long"){
				cls = Long.TYPE;
			}else if (nm == "java.lang.Float"){
				cls = Float.TYPE;
			}else{
				//must not have been representable as primitive, 
				//just leave cls the way it is
				System.out.println(this.getAlias() +
				" Was unable to convert class type '"+
				cls.getName() + "' to primitive. Leaving"+
				" as is.");
			}

		}

		//now we have the type, make an instance of an array of
		//the correct dimensions and of that type
		int lng = objs.length;
		Object instArray = Array.newInstance(cls, lng);
		if(debug){
			System.out.println("Array of type '" + cls.getName() + "' and size "+
			lng + " created.");
		}
		
		//just fill it in. supposedly primitives will be
		//'unwrapped' from their Object counterparts
		for(int i = 0; i < lng; i++){
			Array.set(instArray, i, objs[i]);
		}
		if(debug){
			System.out.println("Array succesfully filled in.");
		}
		
		return instArray;
	}
	
	/**
		given an array of objects, returns the most specific
		interface or class that all the objects share in common.

		@param objs a list of objects
		@return the class that they all have in common, for
		which their is no other class that they have in
		common that is a subclass or subinterface
	*/
	
	protected Class mostSpecificType(Object[] objs){
		//first, use a HashMap with the class type as
		//the key and the number of elements in the objs array that
		//are an instance of that class type as the value.
		//any class's at the end that all the objs elements
		//are instances of will make it to 'the next round'
		int numElements = objs.length;
		int i, j;
		
		HashMap classMap = new HashMap(2 * numElements);
		
		Class cls;
		for(i = 0; i < numElements; i++){
			//add the specific class
			cls = objs[i].getClass();
			addToMap(cls, classMap);

			//add all superclasses
			//note: this will not add java.lang.Object, unless
			//it is an array
			while ( (cls = cls.getSuperclass()) != null){
				addToMap(cls, classMap);
			}
		}

		ArrayList commonClasses = getCommonClasses(classMap, numElements);

		//this will not return java.lang.Object, even if it is
		//the most specific possible. 
		Class mostSpecificClass = findMostSpecificClass(commonClasses);
		if(mostSpecificClass != null){
			//since object instances have precedence over interface
			//instances, we declare this the winner
			if(debug){
				System.out.println("Object instance selected as most"+
				" specific:" + mostSpecificClass.getName());
			}
			return mostSpecificClass;
		}

		//if we're still here, there was no common class other than
		//java.lang.Object. now we have to try to find the best
		//interface. do pretty much the same thing, but with interfaces
		//instead (and we'll have to deal with the possibility of
		//multiple inheritance of interfaces)
		
		classMap.clear();
		for(i = 0; i < numElements; i++){
			cls = objs[i].getClass();
			addInterfacesToMap(cls.getInterfaces(), classMap);
		}
		commonClasses = getCommonClasses(classMap, numElements);
		mostSpecificClass = findMostSpecificClass(commonClasses);
		if(mostSpecificClass != null){
			//the best interface	
			if(debug){
				System.out.println("Interface instance selected as most"+
				" specific:" + mostSpecificClass.getName());
			}

			return mostSpecificClass;
		}

		//if we're here, i guess there was nothing in common
		//just return java.lang.Object
		System.out.println(this.getAlias() + ": Unable to find common" +
			" class type. Returning array of type java.lang.Object");
		
		Object ob = new Object();
		return ob.getClass();
	}

	/**
		given a class and a map, adds the class to the map
		and increments its associated value (its counter).
		
		@param map a map with class objects as keys and
			Integer counters as values
		@param classObj a class object to add
	*/
	protected void addToMap(Class classObj, HashMap map){
		if(map.containsKey(classObj)){
			int counter = ((Integer)map.get(classObj)).intValue();
			counter++;
			map.put(classObj, new Integer(counter));
		}else{
			map.put(classObj, new Integer(1));
		}
	}



	/**
		given a HashMap created using addToMap, returns all
		class objects that were shared by all the objects.
		@param map a HashMap with class objects as keys
			and an Integer counter as values
		@param requiredCount the value the counter values must
			be to declare a class as common amongst the objects
		@return all the class objects in the map that have an
			associated value equal to requiredCount
	*/	
	protected ArrayList getCommonClasses(HashMap map, int requiredCount){
		ArrayList meetRequirements = new ArrayList();
		
		Iterator it = map.keySet().iterator();
		int count;
		Object nextKey;
		if(debug){
			System.out.println("Common Classes:");
		}
		while(it.hasNext()){
			nextKey = it.next();
			count = ((Integer)map.get(nextKey)).intValue();
			if(count == requiredCount){
				meetRequirements.add(nextKey);
				if(debug){
					System.out.println("\t" + ((Class)nextKey).getName());
				}

			}
		}
		return meetRequirements;
	}

	/**
		returns the class object (can be an interface) that is the
		most specific (lowest in the sub-class hierarch). If two
		(or more) interfaces are equally good candidates, one will be 
		arbitrarily chosen.

		@param classObjs an array of class objects
		@return the class object that is the most specific
	*/
	protected Class findMostSpecificClass(ArrayList classObjs){
		//traverse the list, if any class is 'assignable' for another,
		//get rid of the one that is a superclass
		int i,j;
		Class cls;
		Class clsCompare;
		for(i = 0; i < classObjs.size(); i++){
			cls = (Class)classObjs.get(i);
			//System.out.println(" i:"+ i +" class:" + cls.getName());
			for(j = 0; j < classObjs.size();){
				if(j == i){
					j++;
					continue;
				}
				clsCompare = (Class)classObjs.get(j);
				//System.out.println(" j:"+j+" compareClass:"+clsCompare.getName());
				//if clsCompare is a superclass, remove it, otherwise,
				//go on to the next one
				if(clsCompare.isAssignableFrom(cls)){
					//System.out.println("   Removing j");
					classObjs.remove(j);
					if(j < i){
						i--;
						//System.out.println("   Dec i to :"+ i);
					}
				}else{
					j++;
					//System.out.println("   Inc j to :"+j);
				}
			}
		}
		if(debug){
			System.out.println("Most specific classes found:");
			for(i = 0; i < classObjs.size(); i++){
				System.out.println(((Class)classObjs.get(i)).getName());
			}
			System.out.println("Returning:" + 
				((Class)classObjs.get(0)).getName());
		}
		return (Class)classObjs.get(0);


	}

	/**
		adds a set of interfaces to a map, and then recursively
		adds all of those interfaces' superinterfaces to the
		map.

		@param interfaces an array of class objects that are
			assumed to be interfaces
		@param map the HashMap to add them to
	*/
	protected void addInterfacesToMap(Class[] interfaces, HashMap map){

		if(interfaces.length == 0){
			//base case, we're done
			return;
		}

		for(int i = 0; i < interfaces.length ; i++){
			addToMap(interfaces[i], map);
			addInterfacesToMap(interfaces[i].getInterfaces(), map);
		}
	}
		
	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return 	
			"Takes an object array and copies the values into either" + 
			" an array of the type the members are, or, if possible," +
			" the primitive values the objects represent. This behaviour "+
			"can be toggled with the usePrimitiveArrays flag (if true, "+
			"will create an int[], if not, an Integer[]"+
			"";
	}
	
   	public String getModuleName() {
		return "ObjectArrayToPrimitiveArray";
	}
	public String[] getInputTypes(){
		String[] types = {
			"[Ljava.lang.Object:"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0: 
				return "An array of objects, but that are all actually of"+
				" the same type.";
			case 1: 
				return "";
			case 2: 
				return "";
			default: return "No such input";
		}
	}
	
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Objects";
			case 1:
				return "";
			case 2:
				return "";
			default: return "No such input";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {
			"java.lang.Object"};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0: 
				return "An array that is either of a specific object instance"+
				" or of some primitive type.";
			case 1:
				return "";
			case 2:
				return "";
			default: return "No such output";
		}
	}
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "Instance Array";
			case 1:
				return "";
			case 2:
				return "";
			default: return "No such output";
		}
	}		
	////////////////////////////////
	//D2K Property get/set methods
	///////////////////////////////
	public void setDebug(boolean b){
		debug=b;
	}
	public boolean getDebug(){
		return debug;
	}
	public boolean getUsePrimitiveArrays(){
		return usePrimitiveArrays;
	}
	public void setUsePrimitiveArrays(boolean b){
		usePrimitiveArrays=b;
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

	public String get(){
		return ;
	}
	public void set(String s){
		=s;
	}
	*/
}
			
					

			

								
	
