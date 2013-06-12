package ncsa.d2k.modules.core.transform.binning;

import java.util.*;
import java.io.Serializable;

/**
	BinTree is a data structure that allows for the efficient
	classification and counting of items.  This is a three-level tree.
	The first level is indexed by class name.  The second level is
	indexed by attribute name.  The third level contains bins---these
	bins have conditions, and if an item satisfies these conditions, that
	bin's tally will be incremented.  If an item does not fit into any bins,
	a default tally is incremented.  The number of unknown class names and
	attribute names are both kept as well.
*/
public final class BinTree extends HashMap implements Serializable, Cloneable {
	public static final String EQUAL_TO = "=";
	public static final String LESS_THAN = "<";
	public static final String GREATER_THAN = ">";
	public static final String LESS_THAN_EQUAL_TO = "<=";
	public static final String GREATER_THAN_EQUAL_TO = ">=";
public static final String MISSING= "missing";

	/** A lookup table for DefaultTrees */
	private HashMap defaultTree;
	/** The number of unknown classes */
	private int unknownClasses;

	/** The class names */
	private String []classNames;
	/** The attribute names */
	private String []attributeNames;
	/** A quick lookup table for attributes,
		just contains each item in attributeNames */
	private HashMap attributeList;

	// the class totals are incremented each time classify() is called.
	// Since it is called once for each attribute, we must divide the
	// total by the number of attributes to get the real class total
	private HashMap classTotals;
	// the total classified is incremented each time classify() is called.
	// since it is called once for each attribute, it must be divided
	// by the number of attributes to get the real totalClassified
	private int totalClassified;

	public BinTree() {}

	/**
		Create a new BinTree by passing in class and attribute names.
		@param cn the class names
		@param an the attribute names
	*/
	public BinTree(String []cn, String []an) {
		super(cn.length);
		classNames = cn;
		attributeNames = an;

		defaultTree = new HashMap(classNames.length);
		classTotals = new HashMap(classNames.length);
		for(int i = 0; i < classNames.length; i++) {
			put(classNames[i], new ClassTree(attributeNames));
			defaultTree.put(cn[i], new DefaultTree(attributeNames));
			classTotals.put(cn[i], new Integer(0));
		}
		unknownClasses = 0;

		attributeList = new HashMap(attributeNames.length);
		for(int i = 0; i < attributeNames.length; i++)
			attributeList.put(attributeNames[i], attributeNames[i]);
		totalClassified = 0;
	}

	/**<
		Add bins from an equation typed in by the user.
		@param an the attribute name
		@param bn the name to give the new bin
		@param eqn the equation itself
		@param num true if the bin will be numeric, false if it will
			contain text
		@throw DuplicateBinNameException when a bin with this name exists
		@throw MalformedEquationException when the equation cannot be parsed
		@throw AttributeNotFoundException when the attribute does not exist
	*/
	public void addBinFromEquation(String an, String bn, String eqn, boolean num)
		throws DuplicateBinNameException, MalformedEquationException,
		AttributeNotFoundException {

		Iterator i = values().iterator();
		while(i.hasNext()) {
			ClassTree ct = (ClassTree)i.next();
			if(ct != null)
				ct.addBinFromEquation(an, bn, eqn, num);
		}
	}

	public void addMissingValuesBin(String cn, String an)
		throws DuplicateBinNameException, AttributeNotFoundException {

			ClassTree ct = (ClassTree)get(cn);
					ct.addStringBin(an, MISSING, " ");

	}

	/**
		Add a StringBin.  These currently only support equality between
		Strings.
		@param an the attribute name
		@param bn the name to give the new bin
		@param item the item to put in the bin
		@throw DuplicateBinNameException when a bin with this name exists
		@throw AttributeNotFoundException when the attribute does not exist
	*/
	public void addStringBin(String an, String bn, String item)
		throws DuplicateBinNameException, AttributeNotFoundException {

		Iterator i = values().iterator();
		while(i.hasNext()) {
			ClassTree ct = (ClassTree)i.next();
			if(ct != null)
				ct.addStringBin(an, bn, item);
		}
	}

	/**
		Add a numeric bin.
		@param an the attribute name
		@param bn the name to give the bin
		@param op the operator to use in the bin
		@param item the number that goes in the bin
		@throw DuplicateBinNameException when a bin with this name exists
		@throw AttributeNotFoundException when the attribute does not exist
	*/
	public void addNumericBin(String an, String bn, String op, double item)
		throws DuplicateBinNameException, AttributeNotFoundException {

		Iterator i = values().iterator();
		while(i.hasNext()) {
			ClassTree ct = (ClassTree)i.next();
			if(ct != null)
				ct.addNumericBin(an, bn, op, item);
		}
	}

	public void addNumericBin(String an, String bn, double lower, boolean includeLo,
		double upper, boolean includeHi)
		throws DuplicateBinNameException, AttributeNotFoundException {

		Iterator i = values().iterator();
		while(i.hasNext()) {
			ClassTree ct = (ClassTree)i.next();
			if(ct != null)
				ct.addNumericBin(an, bn, lower, includeLo, upper, includeHi);
		}
	}

	public void addStringBin(String an, String bn, String[] items)
		throws DuplicateBinNameException, AttributeNotFoundException {

			 Iterator i = values().iterator();
		while(i.hasNext()) {
			ClassTree ct = (ClassTree)i.next();
			if(ct != null)
				ct.addStringBin(an, bn, items);
		}
	}

	/**
		Remove a bin
		@param an the attribute name
		@param bn the bin name
	*/
	public void removeBin(String an, String bn)
		throws AttributeNotFoundException, BinNotFoundException {

		Iterator i = values().iterator();
		while(i.hasNext()) {
			ClassTree ct = (ClassTree)i.next();
			if(ct != null)
				ct.removeBin(an, bn);
		}
	}

	/**
		Classify a double value by looking up the class and attribute and
		finding an appropriate bin.  If no bin is found, the default tally
		for this class-attribute combination is incremented.
		@param cn the class name
		@param an the attribute name
		@param value the item to find a bin for
	*/
	public void classify(String cn, String an, double value) {
		ClassTree ct = (ClassTree)get(cn);
		if(ct == null) {
			unknownClasses++;
			return;
		}

		try {
			if(!ct.classify(an, value))
				addDefault(cn, an);
			else {
			//	System.out.println("classified : cn an value " + cn + " " + an + " " + value );
				totalClassified++;
				Integer t = (Integer)classTotals.get(cn);
				classTotals.put(cn, new Integer(t.intValue() + 1));
			}
		}
		catch(AttributeNotFoundException e) {}
	}

	/*public void classifyMissing(String cn, String an) {
			ClassTree ct = (ClassTree)get(cn);
			if(ct == null) {
				unknownClasses++;
				return;
			}
	   try  {
				if(!  ct.classifyMissing(an))
				addDefault(cn,an);
				else {
					totalClassified++;
					Integer t = (Integer)classTotals.get(cn);
					classTotals.put(cn, new Integer(t.intValue() + 1));
				}
	   }  catch(AttributeNotFoundException e ) {}
} */

	/**
		Classify a String value by looking up the class and attribute and
		finding an appropriate bin.  If no bin is found, the default tally
		for this class-attribute combination is incremented.
		@param cn the class name
		@param an the attribute name
		@param value the item to find a bin for
	*/
	public void classify(String cn, String an, String value) {
		ClassTree ct = (ClassTree)get(cn);
		if(ct == null) {
			unknownClasses++;
			return;
		}
		try {
			if(!ct.classify(an, value))
				addDefault(cn, an);
			else {
				totalClassified++;
				Integer t = (Integer)classTotals.get(cn);
				classTotals.put(cn, new Integer(t.intValue() + 1));
			}
		}
		catch(AttributeNotFoundException e) {}
	}

	/**
		Given a class name, attribute name, and a value, find the bin that
		this value falls in.
		@param cn the class name
		@param an the attribute name
		@param value the value
		@return the name of the bin value belongs in
	*/
	public String getBinNameForValue(String cn, String an, double value) {
		ClassTree ct = (ClassTree)get(cn);
		if(ct == null)
			return null;
		return ct.getBinNameForValue(an, value);
	}

	/**
		Given a class name, attribute name, and a value, find the bin that
		this value falls in.
		@param cn the class name
		@param an the attribute name
		@param value the value
		@return the name of the bin value belongs in
	*/
	public String getBinNameForValue(String cn, String an, String value) {
		ClassTree ct = (ClassTree)get(cn);
		if(ct == null)
			return null;
		return ct.getBinNameForValue(an, value);
	}

	/**
		Find the bin that value belongs in, knowing only the attribute name.
		This will probe all bins until a suitable one is found.
		@param an the attribute name
		@param value the value
		@return the name of the bin that value belongs in
	*/
	public String getBinNameForValue(String an, double value) {
		Iterator i = keySet().iterator();
		while(i.hasNext()) {
			String cl = (String)i.next();
			ClassTree ct = (ClassTree)get(cl);
			if(ct == null)
				return null;
			String retVal = ct.getBinNameForValue(an, value);
			if(retVal != null)
				return retVal;
		}
		return null;
	}

	/**
		Find the bin that value belongs in, knowing only the attribute name.
		This will probe all bins until a suitable one is found.
		@param an the attribute name
		@param value the value
		@return the name of the bin that value belongs in
	*/
	public String getBinNameForValue(String an, String value) {
		Iterator i = keySet().iterator();
		while(i.hasNext()) {
			String cl = (String)i.next();
			ClassTree ct = (ClassTree)get(cl);
			if(ct == null)
				return null;
			String retVal = ct.getBinNameForValue(an, value);
			if(retVal != null)
				return retVal;
		}
		return null;
	}

	/**
		Get the total number of items classified.
		@return the total number of items classified
	*/
	public int getTotalClassified() {
		return (int) (totalClassified/attributeNames.length);
	}

    /**
       Set the total number of items classified.
       @return the total number of items classified
    */
    public void setTotalClassified(int t) {
	totalClassified = t;
    }

    /**
       Get the total number of items in a certain class
       @param cn the class of interest
       @return the number of items in class cn
    */
    public int getClassTotal(String cn) {
		Integer i = (Integer)classTotals.get(cn);
		if(i == null)
	 	   return 0;
		int tot = i.intValue();
		return (int)Math.ceil((tot/attributeNames.length));
    }

    /**
                Set the total number of items in a certain class
                @param cn the class of interest
                @param total the number of items in class cn
    */
    public void setClassTotal(String cn, int total) {
		classTotals.put(cn, new Integer(total));
    }

	/**
		Add a default bin for a class and attribute pair.
	*/
	public void addDefault(String cn, String an) {
		DefaultTree dt = (DefaultTree)defaultTree.get(cn);
		if(dt == null)
			return;
		dt.addDefault(an);
	}

	/**
		Get the number of default items.  Default items are items
		that have been attempted to be classified, but do not fit into
		any of the bins.
		@param cn the class name
		@param an the attribute name
		@return the number of default items
	*/
	public int getNumDefault(String cn, String an) {
		DefaultTree dt = (DefaultTree)defaultTree.get(cn);
		if(dt == null)
			return -1;
		return dt.getNumDefault(an);
	}

	/**
		Get the names of the classes
		@return the names of the classes
	*/
	public String[] getClassNames() {
		return classNames;
	}

	/**
		Get the names of the attributes
		@return the names of the attributes.
	*/
	public String[] getAttributeNames() {
		return attributeNames;
	}

	/**
		Get the names of the bins for a specific class and attribute
		@param cn the class name
		@param an the attribute name
		@return the names of the bins for this class-attribute pair
	*/
	public String[] getBinNames(String cn, String an) {
		ClassTree ct = (ClassTree)get(cn);
		if(ct == null)
			return null;
		return ct.getBinNames(an);
	}

	/**
		Get the names of the bins for an attribute
		@param an the attribute name
		@return the names of the bins for this attribute
	*/
	public String[] getBinNames(String an) {
		ClassTree ct = (ClassTree)get(classNames[0]);
		if(ct == null)
			return null;
		return ct.getBinNames(an);
	}

	/**
		Get the tally for a specific bin. Returns -1 if it was
		not found.
		@param cn the class name
		@param an the attribute name
		@param bn the bin name
		@return the tally in the specified bin
	*/
	public int getTally(String cn, String an, String bn) {
		ClassTree ct = (ClassTree)get(cn);
		if(ct == null)
			return -1;
		return ct.getTally(an, bn);
	}

	/**
		Get the total for a specific class and attribute.  Returns
		-1 if it was not found.
		@param cn the class name
		@param an the attribute name
		@return the total number of items that are of
			this class and attribute
	*/
	public int getTotal(String cn, String an) {
		ClassTree ct = (ClassTree)get(cn);
		if(ct == null)
			return -1;
		return ct.getTotal(an);
	}

        public void setTotal(String cn, String an, int total) {
		ClassTree ct = (ClassTree)get(cn);
		if(ct == null)
                  ;
		ct.setTotal(an, total);
        }

	/**
		Get the number of unknown classes
		@return the number of unknown classes
	*/
	public int getNumUnknownClasses() {
		return unknownClasses;
	}

	/**
		Get the number of unknown attributes for a class
		@param cn the class name
		@return the number of unknown attributes for this class
	*/
	public int getNumUnknownAttributes(String cn) {
		ClassTree ct = (ClassTree)get(cn);
		if(ct == null)
			return -1;
		return ct.getNumUnknownAttributes();
	}



    // ADDED DC 3.12.2003

    /**
     *  Return true if the lower bound of the specified bin is included,
     *  false otherwise
     */
    public boolean includeLowerBound(String cn, String an, String bn) throws Exception {
		ClassTree ct = (ClassTree)get(cn);
		if(ct == null)
            throw new Exception("The bin could not be found.");
		return ct.includeLowerBound(an, bn);
    }

    public boolean includeUpperBound(String cn, String an, String bn) throws Exception {
		ClassTree ct = (ClassTree)get(cn);
		if(ct == null)
            throw new Exception("The bin could not be found.");
		return ct.includeUpperBound(an, bn);
    }

    public double getLowerBound(String cn, String an, String bn) throws Exception {
		ClassTree ct = (ClassTree)get(cn);
		if(ct == null)
            throw new Exception("The bin could not be found.");
		return ct.getLowerBound(an, bn);
    }

    public double getUpperBound(String cn, String an, String bn) throws Exception {
		ClassTree ct = (ClassTree)get(cn);
		if(ct == null)
            throw new Exception("The bin could not be found.");
		return ct.getUpperBound(an, bn);
    }


    public void setBinTally(String cn, String an, String bn, int tally) throws Exception {
	ClassTree ct = (ClassTree)get(cn);
	if(ct == null)
           throw new Exception("The bin could not be found.");
	ct.setBinTally(an, bn, tally);
    }


    private final String getCondition (String cn, String an, String bn) {
        ClassTree ct = (ClassTree)get(cn);
        if (ct == null)
            return null;
        ClassTree.BinList bl = (ClassTree.BinList)ct.get(an);
        if( bl == null)
            return null;
        ClassTree.Bin b = (ClassTree.Bin)bl.get(bn);
        return b.getCondition(an);
    }

	public final void printAll() {
		System.out.println("UNKNOWN CLASSES: "+unknownClasses);
		Iterator i = keySet().iterator();
		while(i.hasNext()) {
			String key = (String)i.next();
			System.out.println("CLASS: "+key);
			ClassTree cb = (ClassTree)get(key);
			System.out.println("UNKNOWN ATTR: "+getNumUnknownAttributes(key));
			cb.printAll();
		}
		System.out.println("CLASS TOTALS");
		i = classTotals.keySet().iterator();
		while(i.hasNext()) {
			String key = (String)i.next();
			System.out.print("CLASS: "+key);
			System.out.println(getClassTotal(key));
		}
		System.out.println("TOTAL CLASSIFIED: "+getTotalClassified());
	}

	/**
	*/
	public class ClassTree extends HashMap implements Serializable {
		private int unknownAttributes;

		ClassTree() {}

		ClassTree(String []an) {
			super(an.length);
			for(int i = 0; i < an.length; i++)
				put(an[i], new BinList());
			unknownAttributes = 0;
		}

		// add bin stuff goes here
		void addBinFromEquation(String an, String bn, String eq, boolean nu)
			throws MalformedEquationException, DuplicateBinNameException,
				AttributeNotFoundException {

			BinList bl = (BinList)get(an);
			if(bl == null)
				throw new AttributeNotFoundException();
			if(bl.containsKey(bn))
				throw new DuplicateBinNameException();
			bl.put(bn, new Bin(parse(eq, nu)));
		}

		void addStringBin(String an, String bn, String item)
			throws DuplicateBinNameException, AttributeNotFoundException {
			BinList bl = (BinList)get(an);
			if(bl == null)
				throw new AttributeNotFoundException();
			if(bl.containsKey(bn))
				throw new DuplicateBinNameException();
			bl.put(bn, new Bin(new StringEvaluate(item)));
		//	System.out.println("addStringBin called");
		}

		void addNumericBin(String an, String bn, String op, double item)
			throws DuplicateBinNameException, AttributeNotFoundException {
			BinList bl = (BinList)get(an);
			if(bl == null)
				throw new AttributeNotFoundException();
			if(bl.containsKey(bn))
				throw new DuplicateBinNameException();
			bl.put(bn, new Bin(new NumericEvaluate(op, item)));
		}

		void addNumericBin(String an, String bn, double lower, boolean includeLo,
			double upper, boolean includeHi)
			throws DuplicateBinNameException, AttributeNotFoundException {

			BinList bl = (BinList)get(an);
			if(bl == null)
				throw new AttributeNotFoundException();
			if(bl.containsKey(bn))
				throw new DuplicateBinNameException();
			bl.put(bn, new Bin(new BoundedNumericEvaluate(lower, includeLo,
				upper, includeHi)));
		}

		void addStringBin(String an, String bn, String[] items)
			throws DuplicateBinNameException, AttributeNotFoundException {
			BinList bl = (BinList)get(an);
			if(bl == null)
				throw new AttributeNotFoundException();
			if(bl.containsKey(bn))
				throw new DuplicateBinNameException();
			bl.put(bn, new Bin(new MultiStringEvaluate(items)));
			//System.out.println("addMultiStringBin called for binName " +bn + " for attribute  " + an + " with items " + items[0]  + " "  + items[1]);
		}

		/**
			Remove a bin
		*/
		void removeBin(String an, String bn)
			throws BinNotFoundException {
			BinList bl = (BinList)get(an);

			if(bl == null)
				throw new BinNotFoundException();
			Bin b = (Bin)bl.remove(bn);
			bl.decrementTotal(b.tally);
		}

		/**
			Find a suitable bin for a double item
		*/
		boolean classify(String an, double val)
			throws AttributeNotFoundException {
			BinList bl = (BinList)get(an);
			if(bl == null) {
				// this is an unknown attribute.
				// we cannot classify it further
				unknownAttributes++;
				throw new AttributeNotFoundException();
			}
			Iterator i = bl.values().iterator();
			boolean binFound = false;
			while(!binFound && i.hasNext()) {
				Bin b = (Bin)i.next();
				if(b.eval(val)) {
					//System.out.println("classifiyng : an value "  + an + " " + val );
					binFound = true;
					bl.incrementTotal();
				}
			}
			return binFound;
		}


		/**
					Find a suitable bin for a double item
				*/
			/*	boolean classifyMissing(String an)
					throws AttributeNotFoundException {
					BinList bl = (BinList)get(an);
					if(bl == null) {
						// this is an unknown attribute.
						// we cannot classify it further
						unknownAttributes++;
						throw new AttributeNotFoundException();
					}
					Bin b =(Bin) bl.get(MISSING);
					if (b==null) {
						try {
							this.addStringBin(an,MISSING,"");
						} catch (DuplicateBinNameException e) {}
						b = (Bin) bl.get(MISSING);
					}
					b.incrementTally();
					bl.incrementTotal();
					return true;
					} */

		String getBinNameForValue(String an, double val) {
			BinList bl = (BinList)get(an);
			if(bl == null)
				return null;
			Iterator i = bl.keySet().iterator();
			boolean binFound = false;
			while(!binFound && i.hasNext()) {
				String bName = (String)i.next();
				Bin b = (Bin)bl.get(bName);
				if(b.eval(val, false))
					return bName;
			}
			return null;
		}

		String getBinNameForValue(String an, String val) {
			BinList bl = (BinList)get(an);
			if(bl == null)
				return null;
			Iterator i = bl.keySet().iterator();
			boolean binFound = false;
			while(!binFound && i.hasNext()) {
				String bName = (String)i.next();
				Bin b = (Bin)bl.get(bName);
				if(b.eval(val, false))
					return bName;
			}
			return null;
		}


		/**
			Find a suitable bin for a String item
		*/
		boolean classify(String an, String val)
			throws AttributeNotFoundException {
			BinList bl = (BinList)get(an);
			if(bl == null) {
				// this is an unknown attribute.
				// we cannot classify it further
				unknownAttributes++;
				throw new AttributeNotFoundException();
			}
			Iterator i = bl.values().iterator();
			boolean binFound = false;
			while(!binFound && i.hasNext()) {
				Bin b = (Bin)i.next();
				if(b.eval(val)) {
					binFound = true;
					bl.incrementTotal();
				}
			}
			return binFound;
		}

		/**
			Get the names of the bins for this attribute
		*/
		String []getBinNames(String an) {
			BinList bl = (BinList)get(an);
			String []retVal;
			if(bl != null) {
				retVal = new String[bl.size()];
				Iterator i = bl.keySet().iterator();
				int idx = 0;

				while(i.hasNext()) {
					retVal[idx] = (String)i.next();
					idx++;
				}
			}
			else
				retVal = new String[0];
			return retVal;
		}

		int getTally(String an, String bn) {
			BinList bl = (BinList)get(an);
			if(bl == null)
				return -1;
			Bin b = (Bin)bl.get(bn);
			if(b == null)
				return -1;
			return b.tally;
		}

		int getTotal(String an) {
			BinList bl = (BinList)get(an);
			if(bl == null)
				return -1;
			return bl.getTotal();
		}

                void setTotal(String an, int total) {
		  BinList bl = (BinList)get(an);
		  if(bl == null)
                    ;
		  bl.setTotal(total);
                }

		int getNumUnknownAttributes() {
			return unknownAttributes;
		}

		private void printAll() {
			Iterator i = keySet().iterator();
			while(i.hasNext()) {
				String key = (String)i.next();
				System.out.println("ATTR: "+key);
				BinList bl = (BinList)get(key);
				Iterator it = bl.keySet().iterator();
				while(it.hasNext()) {
					String bname = (String)it.next();
					System.out.print("BIN: "+bname);
					Bin b = (Bin)bl.get(bname);
					System.out.println(" COUNT: "+b.tally);
				}
			}
		}

		Evaluate parse(String s, boolean numeric)
			throws MalformedEquationException {
			//System.out.println("PARSE: "+s);

			// base case: we make a Numeric or StringEval
			// we hit the base case if there are no && or || in S
			if((s.indexOf(AND) == -1) && (s.indexOf(OR) == -1)) {
				// return a Numeric or StringEval
				if(numeric) {
					//System.out.println("create Numeric Eval object: "+s);
					return createNumericEvaluate(s);
				}
				else {
					//System.out.println("create String Eval object: "+s);
					return createStringEvaluate(s);
				}
			}
			// otherwise strip the conjunctions and recurse
			// find the outermost conjunction
			//System.out.println("getPhraseList: "+s);
			PhraseList pl = getPhraseList(s);
			//System.out.println("PL: "+pl.part1);
			//System.out.println("PL: "+pl.conjunction);
			//System.out.println("PL: "+pl.part2);

			if(pl == null)
				throw new MalformedEquationException();

			if(pl.conjunction.trim().equals(AND)) {
				//System.out.println(pl.conjunction+": "+s);
				return new AndList(pl.part1, pl.part2, numeric);
			}
			else if(pl.conjunction.trim().equals(OR)) {
				//System.out.println(pl.conjunction+": "+s);
				return new OrList(pl.part1, pl.part2, numeric);
			}
			else
				throw new MalformedEquationException();
		}

		NumericEvaluate createNumericEvaluate(String s)
			throws MalformedEquationException {
			int loc;
			double d;

			if( ((loc = s.indexOf(operators[0])) != -1) ||
				((loc = s.indexOf(operators[1])) != -1) ) {
				String []t = breakUp(s, loc, operators[0].length());
				if(attributeList.containsKey(t[0])) {
					try {
						d = Double.parseDouble(t[2]);
					}
					catch(Exception e) {
						throw new MalformedEquationException();
					}
					return new NumericEvaluate(LESS_THAN_EQUAL_TO, d);
				}
				try {
					d = Double.parseDouble(t[0]);
				}
				catch(Exception e) {
					throw new MalformedEquationException();
				}
				return new NumericEvaluate(LESS_THAN_EQUAL_TO, d);
			}
			else if( ((loc = s.indexOf(operators[2])) != -1) ||
				((loc = s.indexOf(operators[3])) != -1) ) {
				String []t = breakUp(s, loc, operators[2].length());
				if(attributeList.containsKey(t[0])) {
					try {
						d = Double.parseDouble(t[2]);
					}
					catch(Exception e) {
						throw new MalformedEquationException();
					}
					return new NumericEvaluate(GREATER_THAN_EQUAL_TO, d);
				}
				try {
					d = Double.parseDouble(t[0]);
				}
				catch(Exception e) {
					throw new MalformedEquationException();
				}
				return new NumericEvaluate(GREATER_THAN_EQUAL_TO, d);
			}
			else if((loc = s.indexOf(operators[4])) != -1) {
				String []t = breakUp(s, loc, operators[4].length());
				if(attributeList.containsKey(t[0])) {
					try {
						d = Double.parseDouble(t[2]);
					}
					catch(Exception e) {
						throw new MalformedEquationException();
					}
					return new NumericEvaluate(EQUAL_TO, d);
				}
				try {
					d = Double.parseDouble(t[0]);
				}
				catch(Exception e) {
					throw new MalformedEquationException();
				}
				return new NumericEvaluate(EQUAL_TO, d);
			}
			else if((loc = s.indexOf(operators[5])) != -1) {
				String []t = breakUp(s, loc, operators[5].length());
				if(attributeList.containsKey(t[0])) {
					try {
						d = Double.parseDouble(t[2]);
					}
					catch(Exception e) {
						throw new MalformedEquationException();
					}
					return new NumericEvaluate(EQUAL_TO, d);
				}
				try {
					d = Double.parseDouble(t[0]);
				}
				catch(Exception e) {
					throw new MalformedEquationException();
				}
				return new NumericEvaluate(EQUAL_TO, d);
			}
			else if((loc = s.indexOf(operators[6])) != -1) {
				String []t = breakUp(s, loc, operators[6].length());
				if(attributeList.containsKey(t[0])) {
					try {
						d = Double.parseDouble(t[2]);
					}
					catch(Exception e) {
						throw new MalformedEquationException();
					}
					return new NumericEvaluate(LESS_THAN, d);
				}
				try {
					d = Double.parseDouble(t[0]);
				}
				catch(Exception e) {
					throw new MalformedEquationException();
				}
				return new NumericEvaluate(LESS_THAN, d);
			}
			else if((loc = s.indexOf(operators[7])) != -1) {
				String []t = breakUp(s, loc, operators[7].length());
				if(attributeList.containsKey(t[0])) {
					try {
						d = Double.parseDouble(t[2]);
					}
					catch(Exception e) {
						throw new MalformedEquationException();
					}
					return new NumericEvaluate(GREATER_THAN, d);
				}
				try {
					d = Double.parseDouble(t[0]);
				}
				catch(Exception e) {
					throw new MalformedEquationException();
				}
				return new NumericEvaluate(GREATER_THAN, d);
			}
			throw new MalformedEquationException();
		}

		StringEvaluate createStringEvaluate(String s)
			throws MalformedEquationException {
			int loc;
			//System.out.println("createStringEvaluate called");
			// perhaps check for > or < and throw malfomed eq exception first?

			if((loc = s.indexOf(operators[4])) != -1) {
				String []t = breakUp(s, loc, operators[4].length());
				if(attributeList.containsKey(t[0]))
					return new StringEvaluate(t[2]);
				return new StringEvaluate(t[0]);
			}
			else if((loc = s.indexOf(operators[5])) != -1) {
				String []t = breakUp(s, loc, operators[5].length());
				if(attributeList.containsKey(t[0]))
					return new StringEvaluate(t[2]);
				return new StringEvaluate(t[0]);
			}
			throw new MalformedEquationException();
		}

		private String []operators = { LESS_THAN_EQUAL_TO, "=<",
							GREATER_THAN_EQUAL_TO, "=>",
							"==", EQUAL_TO, LESS_THAN, GREATER_THAN };

		private String []breakUp(String s, int loc, int len) {
			String []retVal = new String[3];
			retVal[0] = s.substring(0, loc).trim();
			retVal[1] = s.substring(loc, loc+len).trim();
			retVal[2] = s.substring(loc+len).trim();
			return retVal;
		}

		private int countCharacter(String s, char c) {
			char []str = s.toCharArray();
			int retVal = 0;

			for(int i = 0; i < str.length; i++) {
				if(str[i] == c)
					retVal++;
			}
			return retVal;
		}

		private PhraseList getPhraseList(String s)
			throws MalformedEquationException {

			//System.out.println("getPhraseList: "+s);
			if( (s.indexOf(AND) == -1) && (s.indexOf(OR) == -1) )
				return null;

			int numOpen = countCharacter(s, open);
			int numClose = countCharacter(s, close);

			if(numOpen != numClose)
				throw new MalformedEquationException();

			// loop through string
			// when #open == #close, break it up
			char []str = s.toCharArray();
			numOpen = 0;
			numClose = 0;
			for(int i = 0; i < str.length; i++) {
				if(str[i] == open)
					numOpen++;
				else if(str[i] == close)
					numClose++;
				if((numOpen == numClose) && (i != 0)) {
					String s1 = s.substring(1, i);

					int j = i+1;
					for(; j < str.length; j++) {
						if(str[j] == open)
							break;
					}
					String con = s.substring(i+1, j).trim();
					String s2 = s.substring(j+1, str.length-1);
					//System.out.println("1: "+s1);
					//System.out.println("2: "+s2);
					//System.out.println("C: "+con);

					return new PhraseList(s1, con, s2);
				}
			}
			return null;
		}

		/**
		*/
		public class BinList extends HashMap implements Serializable {
			int total;

			BinList() {
				super();
				total = 0;
			}

			void incrementTotal() {
				total++;
			}

			int getTotal() {
				return total;
			}

            public void setTotal( int t) {
				total = t;
		    }

			void decrementTotal(int d) {
				total = total - d;
			}
		}

		/**
		*/
		private class PhraseList implements Serializable {
			private String part1;
			private String conjunction;
			private String part2;

			PhraseList() {}

			PhraseList(String s, String c, String s2) {
				part1 = s;
				conjunction = c;
				part2 = s2;
			}
		}

		/**
		*/
		private class Evaluate implements Serializable {
		    boolean eval(String val) { return false; }
		    boolean eval(double val) { return false; }
		    boolean eval(int val) { return false; }
		    void print() {};
		    String getCondition(String an) { return an; }
		    ArrayList getAttrValuePair(String an) {return new ArrayList();}
		    Evaluate() {}
		}

		/**
		*/
		public class Bin extends Evaluate implements Serializable {
			private int tally;
			private Evaluate item;

			Bin() {}

			Bin(Evaluate e) {
				item = e;
				tally = 0;
			}

			boolean eval(String v) {
				boolean retVal = item.eval(v);
				if(retVal)
					tally++;
				return retVal;
			}

			boolean eval(String v, boolean incTally) {
				boolean retVal = item.eval(v);
				if(retVal && incTally)
					tally++;
				return retVal;
			}

			boolean eval(double v) {
				boolean retVal = item.eval(v);
				if(retVal)
					tally++;
				return retVal;
			}

			boolean eval(double v, boolean incTally) {
				boolean retVal = item.eval(v);
				if(retVal && incTally)
					tally++;
				return retVal;
			}

			boolean eval(int v) {
				boolean retVal = item.eval(v);
				if(retVal)
					tally++;
				return retVal;
			}

			boolean eval(int v, boolean incTally) {
				boolean retVal = item.eval(v);
				if(retVal && incTally)
					tally++;
				return retVal;
			}

			void print() {
				item.print();
				System.out.println(EMPTY);
			}

		    public String getCondition(String an) {
                        return item.getCondition(an);
                    }

		    public ArrayList getAttrValuePair(String an) {
                        return item.getAttrValuePair(an);
                    }

                    public void setTally(int n) {
                        tally = n;
                    }

			public void incrementTally() {
								   tally = tally + 1;
							   }


		}

		/**
		 Loops through the list and only returns true when all items
		in the list are true.
		*/
		private class AndList extends Evaluate implements Serializable {
			private LinkedList items;

			AndList() {
				items = new LinkedList();
			}

			AndList(String s1, String s2, boolean numeric)
				throws MalformedEquationException {
				this();
				items.add(parse(s1, numeric));
				items.add(parse(s2, numeric));
			}

			void addItem(Evaluate e) {
				items.add(e);
			}

			boolean eval(String v) {
				Iterator i = items.listIterator();
				while(i.hasNext()) {
					Evaluate ev = (Evaluate)i.next();
					boolean retVal = ev.eval(v);
					if(!retVal)
						return false;
				}
				return true;
			}

			boolean eval(double v) {
				Iterator i = items.listIterator();
				while(i.hasNext()) {
					Evaluate ev = (Evaluate)i.next();
					boolean retVal = ev.eval(v);
					if(!retVal)
						return false;
				}
				return true;
			}

			boolean eval(int v) {
				Iterator i = items.listIterator();
				while(i.hasNext()) {
					Evaluate ev = (Evaluate)i.next();
					boolean retVal = ev.eval(v);
					if(!retVal)
						return false;
				}
				return true;
			}

			void print() {
				Iterator i = items.listIterator();
				while(i.hasNext()) {
					System.out.print("(");
					Evaluate ev = (Evaluate)i.next();
					ev.print();
					System.out.print(")");
					if(i.hasNext())
						System.out.print(" && ");
				}


			}

		    String getCondition(String an) {
			Iterator i = items.listIterator();
			String cond = "";
			while(i.hasNext()) {
			    cond = cond + " ( ";
			    Evaluate ev = (Evaluate)i.next();
			    cond =cond + ev.getCondition(an) + " )";
			    if(i.hasNext())
				cond = cond + " AND ";
			}
			return cond;
		    }

                    ArrayList getAttrValuePair(String an) {
                        Iterator i = items.listIterator();
                        ArrayList curr;
                        ArrayList prev = new ArrayList();
                        while(i.hasNext()) {
                            Evaluate ev = (Evaluate)i.next();
                            if(ev.getAttrValuePair(an)!=null) {
                                curr = ev.getAttrValuePair(an);
                                Iterator it1 = curr.iterator();
                                Iterator it2 = prev.iterator();
                                ArrayList cond = new ArrayList();
                                while(it1.hasNext())
                                    while(it2.hasNext()) {
                                        HashMap hm1 = (HashMap) it1.next();
                                        HashMap hm2 = (HashMap) it2.next();
                                        hm1.putAll(hm2);
                                        cond.add(hm1);
                                        prev = cond;
                                    }
                            }
                        }
                        return prev;
                    }


		}

		/**
		*/
		private class OrList extends Evaluate implements Serializable {
			LinkedList items;

			OrList() {
				items = new LinkedList();
			}

			OrList(String s1, String s2, boolean numeric)
				throws MalformedEquationException {
				this();
				items.add(parse(s1, numeric));
				items.add(parse(s2, numeric));
			}

			void addItem(Evaluate e) {
				items.add(e);
			}

			boolean eval(String v) {
				Iterator i = items.listIterator();
				while(i.hasNext()) {
					Evaluate ev = (Evaluate)i.next();
					boolean retVal = ev.eval(v);
					if(retVal)
						return true;
				}
				return false;
			}

			boolean eval(double v) {
				Iterator i = items.listIterator();
				while(i.hasNext()) {
					Evaluate ev = (Evaluate)i.next();
					boolean retVal = ev.eval(v);
					if(retVal)
						return true;
				}
				return false;
			}

			boolean eval(int v) {
				Iterator i = items.listIterator();
				while(i.hasNext()) {
					Evaluate ev = (Evaluate)i.next();
					boolean retVal = ev.eval(v);
					if(retVal)
						return true;
				}
				return false;
			}

			void print() {
				Iterator i = items.listIterator();
				while(i.hasNext()) {
					System.out.print("(");
					Evaluate ev = (Evaluate)i.next();
					ev.print();
					System.out.print(")");
					if(i.hasNext())
						System.out.print(" || ");
				}
			}

		    String getCondition(String an) {
			Iterator i = items.listIterator();
			String cond ="";
			while(i.hasNext()) {
			    cond = cond +"(";
			    Evaluate ev = (Evaluate)i.next();
			    cond = cond + ev.getCondition(an);
			    cond = cond +")";
			    if(i.hasNext())
				cond = cond +" OR ";
			}
			return cond;
		    }

		    ArrayList getAttrValuePair(String an) {
                        Iterator i = items.listIterator();
                        ArrayList cond = new ArrayList();
                        while(i.hasNext()) {
                            Evaluate ev = (Evaluate)i.next();
                            if(ev.getAttrValuePair(an)!=null)
                                cond.add(ev.getAttrValuePair(an));
                        }
                        return cond;
                    }


		}

		/**
		*/
		private class NumericEvaluate extends Evaluate implements Serializable {
			private String operator;
			private double value;

			NumericEvaluate() {}

			NumericEvaluate(String op, double v) {
				operator = op;
				value = v;
				//System.out.println("new NE: "+op+" "+v);
			}

			boolean eval(String s) {
				try {
					Double d = Double.valueOf(s);
					return eval(d.doubleValue());
				}
				catch(Throwable t) {
					return false;
				}
			}

			boolean eval(double d) {
				if(operator.equals(EQUAL_TO))
					return (value == d);
				if(operator.equals(LESS_THAN))
					return (value > d);
				if(operator.equals(GREATER_THAN))
					return (value < d);
				if(operator.equals(LESS_THAN_EQUAL_TO))
					return (value >= d);
				if(operator.equals(GREATER_THAN_EQUAL_TO))
					return (value <= d);
				return false;
			}

			boolean eval(int i) {
				return eval((double)i);
			}

			void print() {
				System.out.print(" "+operator+" "+value);
			}


		    String getCondition(String an) {
			return an + SPACE + operator + SPACE + value;
		    }

		    ArrayList getAttrValuePair(String an) {
			if ( operator.equals(EQUAL_TO)) {
			    HashMap hm = new HashMap();
			    hm.put(an,String.valueOf(value));
			    ArrayList ar = new ArrayList(1);
			    ar.add(hm);
			    return ar;
			}
			else return null;
                    }

		}

		/**
		*/
		private class StringEvaluate extends Evaluate implements Serializable {
			String value;

			StringEvaluate() {}

			StringEvaluate(String v) {
				value = v;
			}

			boolean eval(String s) {
				return value.trim().equals(s.trim());
			}

			boolean eval(double d) {
				return eval(Double.toString(d));
			}

			boolean eval(int i) {
				return eval(Integer.toString(i));
			}

			void print() {
				System.out.print(" == "+value);
			}


		    String getCondition(String an) {
		    	return value + EQUAL_TO + an; }


	    ArrayList getAttrValuePair(String an) {
		HashMap hm = new HashMap();
		hm.put(an,value);
		ArrayList ar = new ArrayList(1);
		ar.add(hm);
		return ar;
	    }
		}

		private class MultiStringEvaluate extends Evaluate implements Serializable {
			HashSet values;

			MultiStringEvaluate() {
                          values = new HashSet();
                        }

			MultiStringEvaluate(String[] vals) {
                                values = new HashSet();
				for(int i = 0; i < vals.length; i++)
					values.add(vals[i]);
			}

			boolean eval(String s) {
				return values.contains(s);
			}

			boolean eval(double d) {
				return eval(Double.toString(d));
			}

			boolean eval(int i) {
				return eval(Integer.toString(i));
			}

			// FIX ME
			void print() {
				//
			}

			// FIX ME by David - FIXED by Anca
			String getCondition(String an) {
				Iterator i = values.iterator();
				//System.out.println("values iterator for attr " + an + " is " + values.size());
        	String condition = "";
				while(i.hasNext()) {
					condition = condition + an + " = '" + i.next() + "'";
					if (i.hasNext()) condition = condition + " OR " ;
					//System.out.println("condition is " + condition);
				}
				condition = " ( " + condition + " )";
				return condition;
			}

      // TO BE TESTED
			ArrayList getAttrValuePair(String an) {
				Iterator i = values.iterator();
				ArrayList ar = new ArrayList(values.size());
				while(i.hasNext()) {
					HashMap hm = new HashMap();
					hm.put(an, i.next());
					ar.add(hm);
				}
				return ar;
			}
		}


		private class BoundedNumericEvaluate extends Evaluate {
			double lower;
			double upper;
			boolean includeLower;
			boolean includeUpper;

			BoundedNumericEvaluate() {}

			BoundedNumericEvaluate(double low, boolean includeLow,
				double high, boolean includeHi) {
				//	System.out.println("created BoundedNumEval low " + low + " high " + high);
				lower = low;
				includeLower = includeLow;
				upper = high;
				includeUpper = includeHi;
			}

			boolean eval(String s) {
				return false;
			}

			boolean eval(double d) {
				//System.out.println("evaluate d " +d + " upper " + upper + " lower " + lower);
				if(includeLower) {
					if(includeUpper) {
						return (d >= lower) && (d <= upper);
					}
					else
						return (d >= lower) && (d < upper);
				}
				if(includeUpper)
					return (d > lower) && (d <= upper);
				else
					return (d > lower) && (d < upper);
			}

			boolean eval(int i) {
				return eval((double)i);
			}

			// FIX ME
			void print() {
				//
			}

			// FIX ME by David - FIXED by Anca
			String getCondition(String an) {
				boolean eliminateLower = false;
				boolean eliminateUpper = false;
				//Double upperValue = new Double(upper);
				if (Double.isInfinite(lower)) eliminateLower = true;
				if (Double.isInfinite(upper))  eliminateUpper = true;
			//	System.out.println("upper " + upper + " lower " + lower);
			//	System.out.println("eliminateUpper " + eliminateUpper + " eliminateLower " + eliminateLower);
				if (eliminateLower && eliminateUpper) return an + " is not null";
	      if (eliminateLower) {
					if (includeUpper)
						return an + " <= " + upper;
					else  // do not includeUpper
						return an + " < " + upper;
	      }

				if (eliminateUpper) {
						if(includeLower)
							return an + " >= " + lower;
						else
							return an + " > " + lower;
				}

			  if(includeLower) {
					if(includeUpper) {
						return an + " >= " + lower + " AND " + an + " <= " + upper;
					}
					else
					  return an + " >= " + lower + " AND " + an + " < " + upper;
				} else {
					  if(includeUpper)
							return an + " > " + lower + " AND " + an + " <= " + upper;
						else
							return an + " > " + lower + " AND " + an + " < " + upper;
				}
			}


			// FIX ME by David - Cannot be implemented for ADTrees by Anca
			ArrayList getAttrValuePair(String an) {
				return null;
			}
		}

    /**
     *  Return true if the lower bound of the specified bin is included,
     *  false otherwise
     */
    boolean includeLowerBound(String an, String bn) throws BinNotFoundException {
		BinList bl = (BinList)get(an);
                //System.out.println("an: " + an + "bn: " + bn);
		if(bl == null)
		    throw new BinNotFoundException();
		Bin b = (Bin)bl.get(bn);
        Evaluate e = b.item;
        if(e instanceof BoundedNumericEvaluate)
            return ((BoundedNumericEvaluate)e).includeLower;
        else if(e instanceof NumericEvaluate)
            // what to do?
            return false;
        else
            throw new BinNotFoundException();
    }

    boolean includeUpperBound(String an, String bn) throws BinNotFoundException {
		BinList bl = (BinList)get(an);

		if(bl == null)
		    throw new BinNotFoundException();
		Bin b = (Bin)bl.get(bn);
        Evaluate e = b.item;
        if(e instanceof BoundedNumericEvaluate)
            return ((BoundedNumericEvaluate)e).includeUpper;
        else if(e instanceof NumericEvaluate)
            // what to do?
            return false;
        else
            throw new BinNotFoundException();
    }

    double getLowerBound(String an, String bn) throws BinNotFoundException {
		BinList bl = (BinList)get(an);

		if(bl == null)
		    throw new BinNotFoundException();
		Bin b = (Bin)bl.get(bn);
        Evaluate e = b.item;
        if(e instanceof BoundedNumericEvaluate)
            return ((BoundedNumericEvaluate)e).lower;
        else if(e instanceof NumericEvaluate)
            // what to do?
            return 0;
        else
            throw new BinNotFoundException();
    }

    double getUpperBound(String an, String bn) throws BinNotFoundException {
//        return 0;
		BinList bl = (BinList)get(an);

		if(bl == null)
		    throw new BinNotFoundException();
		Bin b = (Bin)bl.get(bn);
        Evaluate e = b.item;
        if(e instanceof BoundedNumericEvaluate)
            return ((BoundedNumericEvaluate)e).upper;
        else if(e instanceof NumericEvaluate)
            // what to do?
            return 0;
        else
            throw new BinNotFoundException();
    }


    void setBinTally(String an, String bn, int tally) throws BinNotFoundException {
      BinList bl = (BinList) get(an);
      bl.total += tally;

      if (bl == null)
        throw new BinNotFoundException();
      Bin b = (Bin) bl.get(bn);
      if(b == null)
        throw new BinNotFoundException();
      else
        b.tally = tally;
    }
  }

    private class DefaultTree extends HashMap implements Serializable {

		DefaultTree() {}

		DefaultTree(String []an) {
			super(an.length);
			for(int i = 0; i < an.length; i++)
				put(an[i], new Integer(0));
		}

		void addDefault(String an) {
			Integer i = (Integer)get(an);
			int num = i.intValue();
			num++;
			put(an, new Integer(num));
		}

		int getNumDefault(String an) {
			Integer i = (Integer)get(an);
			if(i == null)
				return -1;
			else
				return i.intValue();
		}
	}
  public class BinNotFoundException extends Exception {}
  public class DuplicateBinNameException extends Exception {}
  public class MalformedEquationException extends Exception {}
  public class AttributeNotFoundException extends Exception {}

  	private static final String AND = "&&";
  	private static final String OR = "||";
	private static final char open = '(';
	private static final char close = ')';
	private static final String EMPTY = "";
	private static final String SPACE = " ";


        //vered - qa
         public boolean equals(Object obj){
           if(obj instanceof BinTree)
             return equals((BinTree)obj);
           else {
             System.out.println("the second object is not of type BinTree");
             return false;

           }
         }


        public boolean equals(BinTree bt){

          boolean retVal = true;

          if (!attributeList.equals(bt.attributeList)){
            retVal = false;
            System.out.println("attribute List are not equal!");
          }

          Arrays.sort(attributeNames);
          Arrays.sort(bt.attributeNames);
          if (!Arrays.equals(attributeNames, bt.attributeNames)){
            retVal = false;
            System.out.println("attribute Names are not equal!");
          }

          Arrays.sort(classNames);
          Arrays.sort(bt.classNames);
          if(!Arrays.equals(classNames, bt.classNames)){
            retVal = false;
            System.out.println("class Names are not equal!");
          }

          if(!classTotals.equals(bt.classTotals)){
            retVal = false;
            System.out.println("class totals are not equal!");
          }

          if(!defaultTree.equals(bt.defaultTree)){
            retVal = false;
            System.out.println("default trees are not equal!");
          }

          if(this.totalClassified != bt.totalClassified ){
            retVal = false;
            System.out.println("total classified are not equal!");
          }
          if(this.unknownClasses != bt.unknownClasses){
            retVal = false;
            System.out.println("unknown classes are not equal!");
          }



          return retVal;
        }
}


