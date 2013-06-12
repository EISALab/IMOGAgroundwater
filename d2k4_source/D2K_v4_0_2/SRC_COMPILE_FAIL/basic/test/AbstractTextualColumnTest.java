package ncsa.d2k.modules.core.datatype.table.basic.test;

import ncsa.d2k.modules.core.datatype.table.basic.*;
/**
 * @author anca
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public abstract class AbstractTextualColumnTest extends AbstractColumnTest {

	/**
	 * Constructor for IntColumnTest.
	 * @param arg0
	 */
	public AbstractTextualColumnTest(String arg0) {
		super(arg0);
	}

//TODO - fix this test with a meaningful one
	public void testTrim() {
		TextualColumn cFull = (TextualColumn) getFullColumn();
		Column expected = cFull;
	    cFull.trim();
		assertEquals(expected, cFull);
	}

	

}
