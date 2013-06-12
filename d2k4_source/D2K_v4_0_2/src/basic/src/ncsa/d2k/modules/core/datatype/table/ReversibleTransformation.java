package ncsa.d2k.modules.core.datatype.table;

/**
 * A ReversibleTransformation is a Transformation on a Table that can
 * be reversed to get the original data back.
 */
public interface ReversibleTransformation extends Transformation {

	static final long serialVersionUID = -7108033494319142753L;

	/**
	 * Undo this transformation to get the original data back.
	 * @param table the Table to reverse-transform
	 * @return the Table with the transformations done by this object reversed
	 */
	public boolean untransform(MutableTable table);
}
