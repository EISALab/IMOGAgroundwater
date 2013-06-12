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
public class AllColumnTests {

	public static void main(String[] args) {
		junit.swingui.TestRunner.run(AllColumnTests.class);
	}

	public static Test suite() {
		TestSuite suite =
			new TestSuite("Test for ncsa.d2k.modules.core.datatype.table.basic");
		//$JUnit-BEGIN$
		suite.addTest(new TestSuite(DoubleColumnTest.class));
		suite.addTest(new TestSuite(FloatColumnTest.class));
		suite.addTest(new TestSuite(IntColumnTest.class));
		suite.addTest(new TestSuite(LongColumnTest.class));
		suite.addTest(new TestSuite(ObjectColumnTest.class));
		suite.addTest(new TestSuite(ShortColumnTest.class));
		suite.addTest(new TestSuite(StringColumnTest.class));
		suite.addTest(new TestSuite(StringObjectColumnTest.class));
		suite.addTest(new TestSuite(BooleanColumnTest.class));
			suite.addTest(new TestSuite(ByteArrayColumnTest.class));
			suite.addTest(new TestSuite(ByteColumnTest.class));
			suite.addTest(new TestSuite(CharArrayColumnTest.class));
			suite.addTest(new TestSuite(CharColumnTest.class));
		//	suite.addTest(new TestSuite(ContinuousByteArrayColumnTest.class));
	//	suite.addTest(new TestSuite(ContinuousCharArrayColumnTest.class));
	//		suite.addTest(new TestSuite(ContinuousStringColumnTest.class));
	
		//$JUnit-END$
		return suite;
	}
}
