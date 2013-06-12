/*
 * Created on Aug 20, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ncsa.d2k.modules.core.datatype.table.basic.test;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author anca
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class AllTableTests {

	public static void main(String[] args) {
		junit.swingui.TestRunner.run(AllTableTests.class);
	}

	public static Test suite() {
		TestSuite suite =
			new TestSuite("Test for ncsa.d2k.modules.core.datatype.table.basic");
		//$JUnit-BEGIN$
		suite.addTest(new TestSuite(ExampleTableImplTest.class));
		suite.addTest(new TestSuite(MutableTableImplTest.class));
		suite.addTest(new TestSuite(PredictionTableImplTest.class));
		suite.addTest(new TestSuite(SubsetTableImplTest.class));
		//$JUnit-END$
		return suite;
	}
}
