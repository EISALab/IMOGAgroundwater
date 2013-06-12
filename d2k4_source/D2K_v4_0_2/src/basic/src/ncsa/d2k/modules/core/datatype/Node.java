package ncsa.d2k.modules.core.datatype;

import java.util.*;


/**
   Node is an implementation of an ADTree node. It stores a count and a list
   of varNodes
**/

public class Node {

    // key - attribute index
    // value - HashMap ( key attribute value, value Node) 
    public int count;
    public TreeMap varNodes;

    /**
     Create a new emtpy Node
    */    
    
    public Node( ) {
	count = 0;
	varNodes = null; 
    }
	
    /**
     Create a new Node with the specified count and list of varNodes
     @param cnt the initial count
     @param vNodes list of varNodes
    */
    public Node(int cnt, TreeMap vNodes){
	count = cnt;
	if( vNodes != null) 
	    varNodes = new TreeMap(vNodes);
    }
	

	
    public boolean containsKey(Object key) {
	if (varNodes == null)
	    return false;
	else
	    return varNodes.containsKey(key);
    }

	
    public void putVarNode(int index, HashMap node) {
	if (varNodes == null )
	    varNodes = new TreeMap();
	varNodes.put(new Integer(index), node);
    }
    
    public HashMap getVarNode(int index) {
	if (varNodes == null)
	    return null;
	else
	    return (HashMap)varNodes.get(new Integer(index));
    }
    
    public TreeMap getVarNodes() {
	return varNodes;
    }

    
    /*
      add node nd to this node - add all branches too 
      NOTE: add the counts of the node before or after calling this
            function which does not add these counts.
       @param nd node to be added to this node
    */
      
    public void addNode(Node nd){
	TreeMap vNodes = nd.getVarNodes();

	//System.out.println(" adding node nd1 " + this);
	//System.out.println("to node nd2 " + nd);
	
	if (vNodes != null) {
	    Iterator keys = vNodes.keySet().iterator();
	    Integer key;
	    while(keys.hasNext()) {
		key = ((Integer)keys.next());
		HashMap hm = new HashMap();
		if(varNodes == null ) varNodes = new TreeMap();
		hm = addHashMaps(hm,  (HashMap)vNodes.get(key));
		hm = addHashMaps(hm, (HashMap)varNodes.get(key));
		varNodes.put(key, hm);
	    }
	}
	//	    return this;
	//System.out.println("result node " + this);
    }
    

    
    /**
       add second HashMap to first and returns first 
       @param first HashMap  to be summed
       @param second HashMap  to be summed
       @return HashMap containing the sum of all nodes contained in the maps
    */
    public HashMap addHashMaps(HashMap first, HashMap second) {
	
	if( first == null)
	    return second;

	if(second == null)
	    return first;

	Iterator keys = second.keySet().iterator();
	Node nd; 
	while(keys.hasNext()) {
	    String key = (String) keys.next();
	    //System.out.println("KEY " + key);
	    //System.out.println("1.first " + first);
	    if (first.containsKey(key)) {
		nd = (Node) first.get(key);
		//System.out.println("nd1 " + nd);
		nd.count = nd.count + ((Node)second.get(key)).count;
		nd.addNode((Node)second.get(key));
		//System.out.println("nd2 " + nd);

	    }    
	    else {
		//System.out.println("second " + second);
		nd = new Node(((Node)second.get(key)).count,
			      ((Node)second.get(key)).getVarNodes());
		//System.out.println("nd3 " + nd);
		first.put(key,nd);
	    }
	}
	
	//	System.out.println("returned from addHashMap " + first);
	return first;
    }



    public int getCount() {
	return count;
    }


    public String toString() {
	String result = "";
	if( count != 0 ) result = "node count " + count + " ";
	if (varNodes == null)  return result;
	Iterator keys = varNodes.keySet().iterator();
	Integer key;
	String indent ="";
	while(keys.hasNext()) {
	    key = (Integer) keys.next();
	    for (int i = 0; i < key.intValue() ; i ++)
		indent = indent + " " ;

	    result = result + "varN " + key + " has HM with keys \n" + indent 
			       + ((HashMap)varNodes.get(key));
	    
	    indent = ""; 
	    for (int i = 0; i < key.intValue()-2 ; i ++)
		indent = indent + " " ;
	    result = result + "\n" + indent; //+ "- ";
	}
	return result;
    }
    
}

