package ncsa.d2k.modules.core.optimize.util;

/**
 * This is used to create Objective contraints objects, it will
 * return an object which implements ObjectiveConstrains and
 * the range interface depending on what the primitive datatype
 * of the max and min passed to the getObjectiveConstraints method
 * are. For example, if the values passed in are int, then it will
 * return a subclass of IntRange that implements ObjectiveConstraints.
 */
public class ObjectiveConstraintsFactory extends Object {



	/**
	 * returns a subclass of DoubleRange that implements the
	 * ObjectiveConstriants interface.
	 * @param name the name of the parameter.
	 * @param best the best value.
	 * @param worst the worst possible value
	 * @returns an object that appropriately implements Range and
	 *    objectiveConstraints interfaces.
	 */
	static public ObjectiveConstraints getObjectiveConstraints (String name, double best, double worst) {
		if (best > worst)
			return new ObjConstMaxDoubleRange (name, best, worst);
		else
			return new ObjConstMinDoubleRange (name, worst, best);
	}

	/**
	 * returns a subclass of IntRange that implements the
	 * ObjectiveConstriants interface.
	 * @param name the name of the parameter.
	 * @param best the best value.
	 * @param worst the worst possible value
	 * @returns an object that appropriately implements Range and
	 *    objectiveConstraints interfaces.
	 */
	static public ObjectiveConstraints getObjectiveConstraints (String name, int best, int worst) {
		if (best > worst)
			return new ObjConstMaxIntRange (name, best, worst);
		else
			return new ObjConstMinIntRange (name, worst, best);
	}

}

/**
 * This IntRange object maximizes.
 */
class ObjConstMaxIntRange extends IntRange implements ObjectiveConstraints {
	ObjConstMaxIntRange (String name, int max, int min) {
		super (name, max, min);
	}

	public int compare (double a, double b) {
		if (a > b) return 1;
		if (a < b) return -1;
		else return 0;
	}

	/**
	 * returns true, since we are always maximizing.
	 * @returns true since we are always maximizing.
	 */
	public boolean isMaximizing (){ return true; }
}

/**
 * This IntRange object minimizes.
 */
class ObjConstMinIntRange extends IntRange implements ObjectiveConstraints {
	ObjConstMinIntRange (String name, int max, int min) {
		super (name, max, min);
	}
	public int compare (double a, double b) {
		if (a < b) return 1;
		if (a > b) return -1;
		else return 0;
	}

	/**
	 * returns false, since we are always minimizing.
	 * @returns false since we are always minimizing.
	 */
	public boolean isMaximizing (){ return false; }

}


/**
 * This DoubleRange object maximizes.
 */
class ObjConstMaxDoubleRange extends DoubleRange implements ObjectiveConstraints {
	ObjConstMaxDoubleRange (String name, double max, double min) {
		super (name, max, min);
	}

	public int compare (double a, double b) {
		if (a > b) return 1;
		if (a < b) return -1;
		else return 0;
	}

	/**
	 * returns true, since we are always maximizing.
	 * @returns true since we are always maximizing.
	 */
	public boolean isMaximizing (){ return true; }
}

/**
 * This DoubleRange object minimizes.
 */
class ObjConstMinDoubleRange extends DoubleRange implements ObjectiveConstraints {
	ObjConstMinDoubleRange (String name, double max, double min) {
		super (name, max, min);
	}

	public int compare (double a, double b) {
		if (a < b) return 1;
		if (a > b) return -1;
		else return 0;
	}

	/**
	 * returns false, since we are always minimizing.
	 * @returns false since we are always minimizing.
	 */
	public boolean isMaximizing (){ return false; }
}
