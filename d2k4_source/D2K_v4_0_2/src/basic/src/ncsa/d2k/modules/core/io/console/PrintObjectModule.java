/*&%^1 DO NOT MODIFY THE CODE TO THE NEXT COMMENT!!! */
package ncsa.d2k.modules.core.io.console;

import ncsa.d2k.core.modules.*;
import java.util.*;
/*#end^1 CONTINUE EDITING FOLLOWING THIS LINE. ^#&*/
/*&%^2 DO NOT MODIFY THE CODE TO THE NEXT COMMENT!!! */
/**
	Print an Object by calling its toString() method.
*/
public class PrintObjectModule extends ncsa.d2k.core.modules.OutputModule {

/*#end^2 CONTINUE EDITING FOLLOWING THIS LINE. ^#&*/
	/**
		This method returns the description of the various inputs.
		@returns the description of the indexed input.
	*/
	public String getInputInfo (int index) {
		switch (index) {
			case 0: return "      This is the object to be printed.  ";
			default: return "No such input";
		}
	}

	/**
		This method returns an array of strings that contains the data types for the inputs.
		@returns the data types of all inputs.
	*/
	public String[] getInputTypes () {
		String[] types = {"java.lang.Object"};
		return types;
	}

	/**
		This method returns the description of the outputs.
		@returns the description of the indexed output.
	*/
	public String getOutputInfo (int index) {
		switch (index) {
			default: return "No such output";
		}
	}

	/**
		This method returns an array of strings that contains the data types for the outputs.
		@returns the data types of all outputs.
	*/
	public String[] getOutputTypes () {
		String[] types = {		};
		return types;
	}

	/**
		This method returns the description of the module.
		@returns the description of the module.
	*/
	public String getModuleInfo () {
		return "<html>  <head>      </head>  <body>    This module will print the input object to the best of it's ability. It     will print a variety of arrays of primitive data types, and will otherwise     invoke the objects toString method and print that.  </body></html>";
	}

	/**
		PUT YOUR CODE HERE.
		@param outV the array to contain output object.
	*/
	public void doit () throws Exception {
		synchronized( System.out ) {
			Object inputArg = this.pullInput (0);

			// int[]
			if (inputArg instanceof int[]) {
				int [] ia = (int []) inputArg;
				System.out.println( "Java DataStructure --> int[] , " + ia );
				System.out.println( "{ " );
				for (int i=0; i<ia.length; i++)
					System.out.println(i + ": " + ia[i] + " , " );
				System.out.println( "} " );
			} else if(inputArg instanceof int[][]) {
				int[][] ds = (int[][]) inputArg;

				System.out.println( "Java DataStructure --> int[][]" );
				System.out.println( "{" );
				for (int x = 0; x<ds.length; x++) {
					System.out.print( "{ " );
					for (int y = 0; y<ds[x].length; y++)
						System.out.print( ds[x][y] + " , " );
					System.out.print( "}" );
					System.out.println( " , " );
				}
				System.out.print( " }" );
			} else if (inputArg instanceof int[][][]) {
				int[][][] ds = (int[][][]) inputArg;

				System.out.println( "Java DataStructure --> int[][][] , " + ds );
				System.out.println( "{" );
				for (int x = 0; x<ds.length; x++) {
					System.out.print( "{" );
					for (int y = 0; y<ds[x].length; y++) {
						System.out.print( "{" );
						for (int z = 0; z<ds[x][y].length; z++) {
							if( z<ds[x][y].length )
								System.out.print( ds[x][y][z] + " , " );
							else
								System.out.print( ds[x][y][z] );
						}
						System.out.print( "}" );
						if( y<ds[x].length )
							System.out.print( " , " );
					}

					System.out.print( "}" );
					if( x<ds.length )
						System.out.println( " , " );
					}
				System.out.println( "}" );
			} else if( inputArg instanceof float[] ) {
				float[] fa = (float[]) inputArg;

				System.out.println( "Java DataStructure --> float[] , " + fa );
				System.out.println( "{" );

				for( int i=0;i<fa.length;i++)
						System.out.println(i + ": " + fa[i] + " , " );

				System.out.println( "} " );
			} else if( inputArg instanceof float[][] ) {
				float[][] fa = (float[][]) inputArg;

				System.out.println( "Java DataStructure --> float[][]" );
				System.out.println( "{" );
				for (int x = 0; x<fa.length; x++) {
					System.out.print( "{ " );
					for (int y = 0; y<fa[x].length; y++)
						System.out.print( fa[x][y] + " , " );
					System.out.print( "}" );
					System.out.println( " , " );
				}
				System.out.print( "}" );
			} else if( inputArg instanceof float[][][] ) {
				float[][][] fa = (float[][][]) inputArg;

				System.out.println( "Java DataStructure --> float[][][] , " + fa );
				System.out.println( "{" );
				for (int x = 0; x<fa.length; x++) {
					System.out.print( "{ " );
					for (int y = 0; y<fa[x].length; y++) {
						System.out.print( "{" );
						for (int z = 0; z<fa[x][y].length; z++) {
							if( z<fa[x][y].length )
								System.out.print( fa[x][y][z] + " , " );
							else
								System.out.print( fa[x][y][z] );
						}
						System.out.print( "}" );
						if( y<fa[x].length )
							System.out.print( " , " );
					}

					System.out.print( "}" );
					if( x<fa.length )
						System.out.println( " , " );
				}
				System.out.println( "}" );
			} else if( inputArg instanceof String[][][] ) {
				String[][][] fa = (String[][][]) inputArg;

				System.out.println( "Java DataStructure --> String[][][] , " + fa );
				System.out.println( "{" );
				for (int x = 0; x<fa.length; x++) {
					System.out.print( "{" );
					for (int y = 0; y<fa[x].length; y++) {
						System.out.print( "{" );
						for (int z = 0; z<fa[x][y].length; z++) {
							if( z<fa[x][y].length )
								System.out.print( fa[x][y][z] + " , " );
							else
								System.out.print( fa[x][y][z] );
						}
						System.out.print( "}" );
						if( y<fa[x].length )
							System.out.println( " , " );
					}

					System.out.print( "}" );
					if( x<fa.length )
						System.out.println( " , " );
				}
				System.out.println( "}" );
			} else if (inputArg instanceof byte[]) {
				byte[] ba = (byte[]) inputArg;
				System.out.println( "Java DataStructure --> byte[] , " + ba );
				System.out.println( "{" );
				System.out.println (new String (ba));
				System.out.println( "}" );
			} else if( inputArg instanceof byte[][] ) {
				byte[][] ba = (byte[][]) inputArg;
				System.out.println( "Java DataStructure --> byte[][] , " + ba );
				System.out.println( "{" );
				for( int i=0;i<ba.length;i++)
					if( ba[i] != null )
						System.out.println(i + ": {" + new String(ba[i]) + "}");
					else
						System.out.println(i + ": {null}");

				System.out.println( "}" );
			} else if( inputArg instanceof byte[][][] ) {
				byte[][][] ba = (byte[][][]) inputArg;

				System.out.println( "Java DataStructure --> byte[][][] , " + ba );
				System.out.println( "{" );
				for( int i=0;i<ba.length;i++) {
					System.out.println( "{" );
					for( int i2=0;i2<ba[i].length;i2++)
						if( ba[i][i2] != null )
							System.out.print("  {" + new String(ba[i][i2]) + "}");
						else
							System.out.print("  {null}");
					System.out.println( "}" );
				}

				System.out.println( "}" );
			} else if (inputArg instanceof Hashtable) {
				Hashtable hash = (Hashtable) inputArg;
				Enumeration keys = hash.keys ();
				Enumeration elems = hash.elements ();
				while (keys.hasMoreElements ()) {
					Object key = keys.nextElement ();
					Object value = elems.nextElement ();
					if (key instanceof byte[])
						key = new String ((byte[]) key);
					if (value instanceof byte[])
						value = new String ((byte[]) value);
					System.out.println (":"+key+":"+value+":");
				}
			} else
				System.out.println (inputArg.toString ());
		}/*sync on out*/
	}
/*&%^8 DO NOT MODIFY THE CODE TO THE NEXT COMMENT!!! */
/*#end^8 CONTINUE EDITING FOLLOWING THIS LINE. ^#&*/

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "Print Object";
	}

	/**
	 * Return the human readable name of the indexed input.
	 * @param index the index of the input.
	 * @return the human readable name of the indexed input.
	 */
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Print Object";
			default: return "NO SUCH INPUT!";
		}
	}

	/**
	 * Return the human readable name of the indexed output.
	 * @param index the index of the output.
	 * @return the human readable name of the indexed output.
	 */
	public String getOutputName(int index) {
		switch(index) {
			default: return "NO SUCH OUTPUT!";
		}
	}
}
