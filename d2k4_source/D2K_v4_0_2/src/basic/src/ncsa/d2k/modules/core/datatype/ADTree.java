package ncsa.d2k.modules.core.datatype;

import java.util.*;
import ncsa.d2k.modules.core.datatype.table.Table;
import ncsa.d2k.modules.core.datatype.table.basic.MutableTableImpl;

/**
    ADTree is an implementation of the data structure defined by B. Anderson
    and A. Moore in "ADTrees for fast counting and for fast learning of
    association rules".  It stores counts for all possible queries of the type
    " how many x,y and z are there ?" It is able to answer these queries in
    constant time.
    ADTree works only for nominal attributes, cannot handle real numbers.
*/

public class ADTree extends Node {

	int noOfAttributes;
	HashMap values; //stores label to index mappings
	String[] labels;
	TreeSet[] uniqueValues; //unique values for each attribute
	// unique values indexed from 1 to attribute arity
	ArrayList[] indexedUniqueValues;

	/**
	   Create a new ADTree that will hold counts for n attributes
	   @param n number of attributes
	*/
	public ADTree(int n) {
		noOfAttributes = n;
		labels = new String[noOfAttributes + 1];
		uniqueValues = new TreeSet[noOfAttributes + 1];
		indexedUniqueValues = new ArrayList[noOfAttributes + 1];
		for (int i = 1; i <= noOfAttributes; i++) {
			uniqueValues[i] = new TreeSet();
			indexedUniqueValues[i] = new ArrayList();
		}
		values = new HashMap();
	}

	/**
	   Sets the labels for specific attributes
	   @param index attribute number that has this label
	   @param value attribute label
	*/
	public void setLabel(int index, String value) {
		if (debug1)
			System.out.println("set labels " + index + " " + value);
		labels[index] = value;
		values.put((Object) value, (Object) (new Integer(index)));
	}

	/**
	   Get the label for a particular attribute
	   @param index attribute number
	   @return
	*/
	public String getLabel(int index) {
		return labels[index];
	}

	/**
	   Returns the number of attributes
	   @return number of attributes
	*/
	public int getAttributesNum() {
		return noOfAttributes;
	}

	/**
	   Returns the label corresponding to an index
	   @param label value of attribute label
	   @return position of a particular attribute label
	*/
	public int getIndexForLabel(String label) {
		Integer v = (Integer) values.get((Object) label);
		if (v != null)
			return v.intValue();
		else
			return -1;
	}

	/**
	   This method builds the main trunk of the ADTree from a Table.
	   Used mainly for testing.
	   @param vt table containig data
	*/
	public void initializeFromVT(Table vt) {

		HashMap varNode;
		Node node;

		for (int i = 0; i < vt.getNumRows(); i++) {
			node = this;
			node.count++;
			if (debug1)
				System.out.println(" MAIN COUNT : " + node.count + " " + this);

			for (int j = 1; j <= noOfAttributes; j++) {

				if (!node.containsKey(new Integer(j))) {
					HashMap hm = new HashMap();
					hm.put(vt.getString(i, j - 1), new Node());
					node.putVarNode(j, hm);
					if (debug2)
						System.out.println(
							"created and inserted hashmap for  "
								+ vt.getString(i, j - 1)
								+ " with j="
								+ j);
				}
				varNode = node.getVarNode(j);

				if (varNode.containsKey(vt.getString(i, j - 1)))
					node = (Node) varNode.get(vt.getString(i, j - 1));
				else {
					node = new Node();
					varNode.put(vt.getString(i, j - 1), node);
					if (debug2)
						System.out.println(
							"hm does not contain key "
								+ vt.getString(i, j - 1)
								+ " inserted it j ="
								+ j);
				}
				node.count++;
			}
		}

	}

	/**
	   Increments the main ADTree branch counts based on the values
	   read from a line in the file
	   @param nd node root of the branch to be incremented
	   @param values attribute values
	*/
	public void countLine(Node nd, String[] values) {

		HashMap varNode;
		Node node = nd;

		node.count++;
		if (debug1)
			System.out.println(" MAIN COUNT : " + node.count + " " + node);

		for (int j = 1; j <= values.length; j++) {

			if (!node.containsKey(new Integer(j))) {
				HashMap hm = new HashMap();
				hm.put(values[j - 1], new Node());
				node.putVarNode(j, hm);
				if (debug2)
					System.out.println(
						"created and inserted hashmap for  "
							+ values[j
							- 1]
							+ " with j="
							+ j);
			}
			varNode = node.getVarNode(j);

			if (varNode.containsKey(values[j - 1]))
				node = (Node) varNode.get(values[j - 1]);
			else {
				node = new Node();
				varNode.put(values[j - 1], node);
				if (debug2)
					System.out.println(
						"hm does not contain key "
							+ values[j
							- 1]
							+ " inserted it j ="
							+ j);
			}

			((TreeSet) uniqueValues[j]).add(values[j - 1]);
			node.count++;
		}
	}

	/**
	   Increments the main ADTree branch counts based on the values
	   read from a line in the file
	   @param nd node root of the branch to be incremented
	   @param values attribute values
	*/
	public void countLine(Node nd, ArrayList values) {

		HashMap varNode;
		Node node = nd;

		node.count++;
		if (debug1)
			System.out.println(" MAIN COUNT : " + node.count + " " + node);

		int len = values.size();
		for (int j = 1; j <= len; j++) {
			String value = new String((char[]) values.get(j - 1));

			if (!node.containsKey(new Integer(j))) {
				HashMap hm = new HashMap();
				hm.put(value, new Node());
				node.putVarNode(j, hm);
				if (debug2)
					System.out.println(
						"created and inserted hashmap for  "
							+ value
							+ " with j="
							+ j);
			}
			varNode = node.getVarNode(j);

			if (varNode.containsKey(value))
				node = (Node) varNode.get(value);
			else {
				node = new Node();
				varNode.put(value, node);
				if (debug2)
					System.out.println(
						"hm does not contain key "
							+ value
							+ " inserted it j ="
							+ j);
			}
			uniqueValues[j].add(value);
			node.count++;
		}

	}

	/*
	  method called for each line of the file, identifies all the unique
	  values of the attributes, and stores them in a TreeMap and
	  an indexed ArrayList
	  @param nd root node
	  @return values attribute values
	*/
	public void scanUniqueValues(Node nd, String[] values) {

		Node node = nd;
		node.count++;
		for (int j = 1; j <= values.length; j++) {
			if (((TreeSet) uniqueValues[j]).add(values[j - 1]))
				indexedUniqueValues[j].add(values[j - 1]);
		}
	}

	/*
	  Expands the ADTree skeleton or main trunk created by 'initializeFromVT'
	  or 'countLine'  methods to a full ADTree ( all counts are stored)
	  @param node the node that is going to be expanded. If the node is the
	  root the entire tree will be expanded
	*/
	public void expand(Node node) {

		int lastKey = ((Integer) node.getVarNodes().lastKey()).intValue();

		if (lastKey == noOfAttributes)
			return;

		HashMap hm = node.getVarNode(lastKey);
		Iterator values = hm.values().iterator();
		while (values.hasNext())
			expand((Node) values.next());

		for (int i = lastKey + 1; i <= noOfAttributes; i++)
			expandNode(i, node);
	}

	/*
	  Expands fully only the first level nodes.
	  Does not expand fully the root node.
	  @param node the node where the expansion starts
	 */

	public void expandFirstOnly(Node node) {

		int lastKey = ((Integer) node.getVarNodes().lastKey()).intValue();
		System.out.println("FIRST ONLY LASTKEY IS " + lastKey);

		HashMap hm = node.getVarNode(lastKey);
		Iterator values = hm.values().iterator();
		while (values.hasNext())
			expand((Node) values.next());

	}

	/*
	   Expands 'node' by adding a VarNode for 'index'
	   @param index the number of the VarNode to be created
	   @param node node to be expanded
	   @return HashMap map containing the attribute value mapping for the newly
	   created varNode
	*/

	private HashMap expandNode(int index, Node node) {

		if (debug2)
			System.out.println("EXPANDNODE " + index + " " + node);
		HashMap previousVarNode = node.getVarNode(index - 1);

		if (debug2)
			System.out.println("previousVarNode is " + previousVarNode);
		Iterator values = previousVarNode.values().iterator();

		HashMap hm = new HashMap();
		Node currNode;

		while (values.hasNext()) {
			currNode = (Node) values.next();
			HashMap hmc = currNode.getVarNode(index);
			if (debug2)
				System.out.println("hmc is " + hmc);
			hm = node.addHashMaps(hm, hmc);
		}
		node.putVarNode(index, hm);
		if (debug1)
			System.out.println("EXPANDED NODE IS " + node);

		return hm;
	}

	/*
	  getCount implements an OR like expresion and returns the sums of counts
	  of all queries.
	  @param nd - root of the ADTree
	  @param pairs  - contains a list of HashMaps where each HashMap
	  contains (attributeName ,value) mappings
	  @return count number for the gived expression/query
	*/

	public int getCount(Node nd, ArrayList pairs) {

		int ors = pairs.size();
		HashMap varNode;
		Node node;
		int count = 0;

		for (int i = 0; i < ors; i++) {

			node = nd;
			HashMap hm = (HashMap) pairs.get(i);
			TreeMap attrValues = new TreeMap();
			Iterator it = hm.keySet().iterator();
			String stringKey;
			while (it.hasNext()) {
				stringKey = (String) it.next();
				Integer key = new Integer(getIndexForLabel(stringKey));
				attrValues.put(key, hm.get(stringKey));
			}

			int tmp = getCount(node, attrValues);
			if (tmp == -1)
				return -1;
			count = count + tmp;

		}

		return count;
	}

	/*
	  getDirectCount implements an OR like expresion and returns the sums
	  of counts of all queries. It uses getDirectCount
	  @param pairs  - contains a list of HashMaps where each HashMap
	  contains (attributeName ,value) mappings
	  @param nd - root of the ADTree
	  @return count number for the gived expression/query
	*/
	public int getDirectCount(Node nd, ArrayList pairs) {

		int ors = pairs.size();
		HashMap varNode;
		Node node;
		int count = 0;

		for (int i = 0; i < ors; i++) {
			node = nd;
			HashMap hm = (HashMap) pairs.get(i);
			TreeMap attrValues = new TreeMap();
			Iterator it = hm.keySet().iterator();
			String stringKey;
			while (it.hasNext()) {
				stringKey = (String) it.next();
				Integer key = new Integer(getIndexForLabel(stringKey));
				attrValues.put(key, hm.get(stringKey));
			}

			int tmp = getDirectCount(1, node, attrValues);
			if (tmp == -1)
				return -1;
			count = count + tmp;
		}
		return count;
	}

	/*
	  getCount implements an AND like expresion and returns the sums of counts
	  of all queries.
	  @param nd - root of the ADTree
	  @param values - array of attribute values
	  @param attribute - array of attribute indexes
	  @return - the no of the records that have attribute[i] = value
	            for every i
	
		NOTE : attribute indexes should be in ascending order
	*/

	public int getCount(Node nd, String[] values, int[] attribute) {

		int len = attribute.length;
		HashMap varNode;
		Node node = nd;

		Integer firstKey = (Integer) varNodes.firstKey();
		if (firstKey.intValue() > attribute[0])
			System.err.println("cannot get the desired count from this tree");

		for (int i = 0; i < len; i++) {
			System.out.println("attribute[i] " + attribute[i]);
			System.out.println("values[i] " + values[i]);
			varNode = node.getVarNode(attribute[i]);
			if (varNode == null) {
				System.err.println("ADTree is not expanded: Trying to expand");
				expand(node);
				varNode = node.getVarNode(attribute[i]);
				//		    return -1;
			}
			if (varNode.containsKey(values[i]))
				node = (Node) varNode.get(values[i]);
			else
				return 0;
		}
		return node.getCount();
	}

	/**
	
	  getCount implements an AND like expresion and returns the sums of counts
	  of all queries.
	  @param nd - root of the ADTree
	  @param values - map of attribute, value pairs
	  @return - the no of the records that have attribute[i] = value
	            for every i
	
		NOTE : attribute indexes should be in ascending order
	
	*/
	public int getCount(Node nd, TreeMap values) {

		HashMap varNode;
		Node node = nd;
		Iterator it = values.keySet().iterator();
		int index;
		Object key;
		while (it.hasNext()) {
			key = it.next();
			index = ((Integer) key).intValue();
			varNode = node.getVarNode(index);
			if (varNode == null) {
				System.err.println("ADTree is not expanded: Trying to expand");
				if (debug1)
					System.out.println("node to expand " + node);
				expand(node);
				//System.out.println("expanded  " + node);
				varNode = node.getVarNode(index);
				//System.out.println("varNode " + varNode);
				//		return -1;
			}
			if (varNode.containsKey(values.get(key)))
				node = (Node) varNode.get(values.get(key));
			else
				return 0;
		}

		return node.getCount();

	}

	/*
	  getDirectCount - gets a count by using only the ADTree skeleton,
	  expanding the needed branches and then discarding them
	  @param pos -position where the search for the node containing
	  the count will begin
	  @param nd - root of the ADTree
	  @param values - map of attribute, value pairs
	  @return the no of the records that have attribute = value for all
	  (attribute, value) pairs in the TreeMap
	*/

	public int getDirectCount(int pos, Node nd, TreeMap values) {

		HashMap varNode;
		Node node = nd;
		Node newNode = new Node();

		//System.out.println("values.size " + values.size());
		//System.out.println("node is  " + nd);
		if (values.size() == 0)
			return node.getCount();

		Object key = values.firstKey();
		int index = ((Integer) key).intValue();
		Object value = values.get(key);
		//System.out.println("key " + key + " value " + value);
		Node sumed = findNode(pos, index, node, value);

		if (sumed == null) // there was no node with the desired value
			sumed = new Node(); // create an empty node for sumed
		newNode.count = newNode.count + sumed.count;
		newNode.addNode(sumed);

		System.gc();

		SortedMap mp = values.tailMap(new Integer(index + 1));
		TreeMap next = new TreeMap(mp);

		return getDirectCount(index + 1, newNode, next);
		//return getCount(newNode,next);

	}

	/**
	   Finds a node starting at a position and node for a given attribute
	   and value. If node does not exist create a new one.
	   @param position - postion to start the search
	   @param index - attribute number
	   @param nd - node from where to start searching
	   @param value - attribute value
	   @return Node found or created
	*/
	private Node findNode(int position, int index, Node nd, Object value) {

		HashMap varNode;

		//System.out.println("index " + index + " position " + position);
		if (index == position) {
			varNode = nd.getVarNode(position);
			//    System.out.println("returning " + (Node)varNode.get(value));
			return (Node) varNode.get(value);
		}

		varNode = nd.getVarNode(position);
		Iterator it = varNode.keySet().iterator();
		Node newNode = new Node();
		while (it.hasNext()) {
			Object key = it.next();
			//System.out.println("key is " + (String) key);
			Node node =
				findNode(position + 1, index, (Node) varNode.get(key), value);
			if (node != null) {
				newNode.count = newNode.count + node.count;
				newNode.addNode(node);
			}
		}
		//System.out.println("returning newNode" + newNode);
		return newNode;
	}

	/*
	  getUniqueValues - returns all possible values that the attribute
	  identified by index can take.
	   @param index attribute position in the list of attributes
	   @return array of unique attribute values
	*/
	public String[] getUniqueValues(int index) {

		// the commented code works only when the tree is fully expanded and
		// there is no uniqueValues variable.
		/*
		HashMap hm = this.getVarNode(index);
		if (hm != null ) {
		    Object [] values = (hm.keySet().toArray());
		    String [] result = new String[values.length];
		    for (int i = 0; i < values.length ; i++)
			result[i] = (String) values[i];
		    return result;
		}
		System.out.println("ADTree has not been initialized");
		return null;
		*/

		TreeSet uniques = (TreeSet) uniqueValues[index];
	    
	    
		String[] result;
		MutableTableImpl tbl = new MutableTableImpl();
		String missingStringValue = tbl.getMissingString();
		//find if there are missing values and return only unique non missing values
		uniques.remove(missingStringValue);
		
		//System.out.println("ADTREE : missing values for column " + index + " are " + missing);

		Iterator itnew = uniques.iterator();
		int i = 0;
			result = new String[uniques.size()];
			//	System.out.println("uniques size " + uniques.size());
			while (itnew.hasNext()) {
				String item = (String) itnew.next();
				result[i++] = item;
				//			System.out.println("NONMissingValue " + item);
		}
		return result;

	}

	public TreeSet getUniqueValuesTreeSet(int index) {
		//		find if there are missing values and return only unique non missing values
		MutableTableImpl tbl = new MutableTableImpl();
		String missingStringValue = tbl.getMissingString();
		TreeSet uniques = (TreeSet) uniqueValues[index];
		uniques.remove(missingStringValue);
		return uniques;
	}
	//first level of debug
	boolean debug1 = false;

	public void setDebug1(boolean b) {
		debug1 = b;
	}

	public boolean getDebug1() {
		return debug1;
	}

	//second level of debug
	boolean debug2 = false;
	public void setDebug2(boolean b) {
		debug2 = b;
	}

	public boolean getDebug() {
		return debug2;
	}

}
