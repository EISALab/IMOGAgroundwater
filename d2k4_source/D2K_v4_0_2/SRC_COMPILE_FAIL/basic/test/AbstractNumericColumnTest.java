package ncsa.d2k.modules.core.datatype.table.basic.test;

import ncsa.d2k.modules.core.datatype.table.basic.*;
/**
 * @author anca
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public abstract class AbstractNumericColumnTest extends AbstractColumnTest {

	/**
	 * Constructor for IntColumnTest.
	 * @param arg0
	 */
	public AbstractNumericColumnTest(String arg0) {
		super(arg0);
	}

	public void testGetMin() {
		NumericColumn cFull = (NumericColumn) getFullColumn();
		double expected = 0.0;
		double actual = cFull.getMin();
		double delta = getDelta();
		assertEquals(expected, actual, delta);
	}

	public void testGetMax() {
		NumericColumn cFull = (NumericColumn) getFullColumn();
		double expected = 3.0;
		double actual = cFull.getMax();
		double delta = getDelta();
		assertEquals(expected, actual, delta);
	}

}
