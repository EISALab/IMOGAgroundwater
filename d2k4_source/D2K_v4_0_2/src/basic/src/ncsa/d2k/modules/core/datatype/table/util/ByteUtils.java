//package ncsa.d2k.util;
package ncsa.d2k.modules.core.datatype.table.util;

import java.io.*;

public class ByteUtils
	{
	static public final byte minusByte = (byte) '-';
	static public final byte plusByte = (byte) '+';
	static public final byte zeroByte = (byte) '0';
	static public final byte decimalByte = (byte) '.';
	static public final byte eByte = (byte) 'e';
	static public final byte EByte = (byte) 'E';

	/**
		Get the next token defined by the string passed.

		@param token the token to id.
		@param bytes the data to search.
		@param from where to search from.
		@param to where to search to.
	*/
	static public int getTokenEnd( String token, byte[ ] bytes, int from, int to )
		{
		byte [] target = token.getBytes( );
		int len = target.length;
		int currentField = 0;

		for( int i = from ; i < to ; i++ )
			{
			if( bytes[ i ] == target[ currentField ] )
				{
				if( ++currentField == len )
					return i+1;
				}
			else
				currentField = 0;
			}
		return -1;
		}

	/**
		Get the next token defined by the string passed.

		@param token the token to id.
		@param bytes the data to search.
		@param from where to search from.
		@param to where to search to.
	*/
	static public int getTokenStart( String token, byte[ ] bytes, int from, int to )
		{
		byte [] target = token.getBytes( );
		int len = target.length;
		int currentField = 0;

		for( int i = from ; i < to ; i++ )
			{
			if( bytes[ i ] == target[ currentField ] )
				{
				if( ++currentField == len )
					return i-currentField+1;
				}
			else
				currentField = 0;
			}
		return -1;
		}

	/**
		return the position of the first none whitespace character.

		@return the index of the first not whitespace character.
	*/
	static public int skipWhitespace( byte [] buf, int pos, int count)
		{
		while (pos < count)
			{
			switch( buf[ pos ] )
				{
				case (byte) '\n':
				case (byte) '\r':
				case (byte) '\t':
				case (byte) ' ':
					pos++;
					break;

				default:
					return pos;
				}
			}
		return -1;
		}

	/**
		Return the integer value of the bytes starting at the position given. Decimal
		digits are parsed up to the first non-digit and the value is returned.

		@param current the position to start the scan from.
		@param bytes teh data to scan.
	*/
	static public int getInt( byte [] bytes, int current )
		{
		int val = 0;
		while( bytes[ current ] >= '0' && bytes[ current ] <= '9' )
			{
			val *= 10;
			val += ( int )bytes[ current ] - ( int )'0';
			current++;
			}
		return val;
		}

	/**
		Return the integer value of the bytes starting at the position given. Decimal
		digits are parsed up to the first non-digit and the value is returned.

		@param current the position to start the scan from.
		@param bytes teh data to scan.
	*/
	static public int skipInt( byte [] bytes, int current )
		{
		int val = 0;
		while( bytes[ current ] >= '0' && bytes[ current ] <= '9' )
			current++;
		return current;
		}
	/**
		get the word starting at the location passed in.

		@param buf the byte array containing the data.
		@param pos the position of the start of the word.
		@param count the last position to search.
		@return the word at position pos.
	*/
	static public String getWord( byte [] buf, int pos, int count)
		{
		int start = pos;
		for (;pos < count; pos++)
			{
			switch( buf[ pos ] )
				{
				case (byte) '\n':
				case (byte) '\r':
				case (byte) '\t':
				case (byte) ' ':
				case (byte) '"':
				case (byte) '(':
				case (byte) ')':
				case (byte) '}':
				case (byte) '{':
				case (byte) ',':
				case (byte) ';':
				case (byte) ':':
					return new String( buf, start, pos-start );
				}
			}
		return null;
		}

	/**
		Convert bytes to an int value in a platform-independent way.
		The first four bytes are read and an int is generated.  Returns
		0 if an error occured.
		@param bytes the bytes to convert
		@return the value of bytes as an int or 0 if an error occurs
	*/
	static public int toInt( byte[] bytes )
		{
		int retVal = 0;
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
			DataInputStream dis = new DataInputStream(bis);
			retVal = dis.readInt();
			dis.close();
		}
		catch(IOException e) {
		}
		return retVal;
		}

	/**
		Returns the byte value of i in a new byte array of length 4.  Returns 0
		if an error occurs.
		@param i the int to write
		@return the bytes that make up i
	*/
	static public byte[] writeInt( int i )
		{
		byte []retVal = null;
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(bos);
			dos.writeInt(i);
			dos.close();
			dos.flush();
			retVal = bos.toByteArray();
		}
		catch(IOException e) {
		}
		return retVal;
		}

	/**
		Convert bytes to a double value.  Returns 0 if an error occured.
		@param bytes the bytes to convert
		@return the value of bytes as a double or 0 if an error occurs
	*/
	static public double toDouble( byte[] bytes )
		{
		double retVal = 0;
		try {
			if (bytes == null)
				return 0.0;
			String s = new String (bytes);
			return Double.parseDouble(s);
			/*ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
			DataInputStream dis = new DataInputStream(bis);
			retVal = dis.readDouble();
			dis.close();
		}
		catch(IOException e) {
		}*/
		} catch (NumberFormatException nfe) {}
		return retVal;
		}

	/**
		Returns the byte value of i in a new byte array.  Returns 0 if an error
		occurs.
		@param i the double to write
		@return the bytes that make up i
	*/
	static public byte[] writeDouble( double i )
		{
		byte []retVal = null;
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(bos);
			dos.writeDouble(i);
			dos.close();
			dos.flush();
			retVal = bos.toByteArray();
		}
		catch(IOException e) {
		}
		return retVal;
		}

	/**
		Convert bytes to a float value.  Returns 0 if an error occured.
		@param bytes the bytes to convert
		@return the value of bytes as a float or 0 if an error occurs
	*/
	static public float toFloat( byte[] bytes )
		{
		float retVal = 0;
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
			DataInputStream dis = new DataInputStream(bis);
			retVal = dis.readFloat();
			dis.close();
		}
		catch(IOException e) {
		}
		return retVal;
		}

	/**
		Returns the byte value of i in a new byte array.  Returns 0 if an error
		occurs.
		@param i the float to write
		@return the bytes that make up i
	*/
	static public byte[] writeFloat( float i )
		{
		byte []retVal = null;
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(bos);
			dos.writeFloat(i);
			dos.close();
			dos.flush();
			retVal = bos.toByteArray();
		}
		catch(IOException e) {
		}
		return retVal;
		}

	/**
		Convert bytes to a boolean value.  Returns 0 if an error occured.
		@param bytes the bytes to convert
		@return the value of bytes as a boolean or 0 if an error occurs
	*/
	static public boolean toBoolean( byte[] bytes )
		{
		boolean retVal = false;
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
			DataInputStream dis = new DataInputStream(bis);
			retVal = dis.readBoolean();
			dis.close();
		}
		catch(IOException e) {
		}
		return retVal;
		}

	/**
		Returns the byte value of i in a new byte array.  Returns 0 if an error
		occurs.
		@param i the boolean to write
		@return the bytes that make up i
	*/
	static public byte[] writeBoolean( boolean i )
		{
		byte []retVal = null;
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(bos);
			dos.writeBoolean(i);
			dos.close();
			dos.flush();
			retVal = bos.toByteArray();
		}
		catch(IOException e) {
		}
		return retVal;
		}

	/**
		Convert bytes to a short value.  Returns 0 if an error occured.
		@param bytes the bytes to convert
		@return the value of bytes as a short or 0 if an error occurs
	*/
	static public short toShort( byte[] bytes )
		{
		short retVal = 0;
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
			DataInputStream dis = new DataInputStream(bis);
			retVal = dis.readShort();
			dis.close();
		}
		catch(IOException e) {
		}
		return retVal;
		}

	/**
		Returns the byte value of i in a new byte array.  Returns 0 if an error
		occurs.
		@param i the short to write
		@return the bytes that make up i
	*/
	static public byte[] writeShort( short i )
		{
		byte []retVal = null;
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(bos);
			dos.writeShort(i);
			dos.close();
			dos.flush();
			retVal = bos.toByteArray();
		}
		catch(IOException e) {
		}
		return retVal;
		}

	/**
		Convert bytes to a long value.  Returns 0 if an error occured.
		@param bytes the bytes to convert
		@return the value of bytes as a long or 0 if an error occurs
	*/
	static public long toLong( byte[] bytes )
		{
		long retVal = 0;
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
			DataInputStream dis = new DataInputStream(bis);
			retVal = dis.readLong();
			dis.close();
		}
		catch(IOException e) {
		}
		return retVal;
		}

	/**
		Returns the byte value of i in a new byte array.  Returns 0 if an error
		occurs.
		@param i the long to write
		@return the bytes that make up i
	*/
	static public byte[] writeLong( long i )
		{
		byte []retVal = null;
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(bos);
			dos.writeLong(i);
			dos.close();
			dos.flush();
			retVal = bos.toByteArray();
		}
		catch(IOException e) {
		}
		return retVal;
		}

	/**
		Convert bytes to a char value.  Returns 0 if an error occured.
		@param bytes the bytes to convert
		@return the value of bytes as a char or 0 if an error occurs
	*/
	static public char toChar( byte[] bytes )
		{
		char retVal = '0';
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
			DataInputStream dis = new DataInputStream(bis);
			retVal = dis.readChar();
			dis.close();
		}
		catch(IOException e) {
		}
		return retVal;
		}

	/**
		Convert bytes to an  value.  Returns null if an error occured.
		@param bytes the bytes to convert
		@return the value of bytes as an Object or null if an error occurs
	*/
	static public Object toObject( byte[] bytes )
		{
		Object retVal = null;
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
			ObjectInputStream dis = new ObjectInputStream(bis);
			retVal = dis.readObject();
			dis.close();
		}
		catch(IOException e) {
		}
		catch(ClassNotFoundException fe) {
		}
		return retVal;
		}

	/**
		Returns the byte value of i in a new byte array.  Returns 0 if an error
		occurs.
		@param i the Object to write
		@return the bytes that make up i
	*/
	static public byte[] writeObject( Object i )
		{
		byte []retVal = null;
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream dos = new ObjectOutputStream(bos);
			dos.writeObject(i);
			dos.close();
			dos.flush();
			retVal = bos.toByteArray();
		}
		catch(IOException e) {
		}
		return retVal;
		}

	/**
		Convert bytes to a char[] value.  Returns 0 if an error occured.
		@param bytes the bytes to convert
		@return the value of bytes as a char[] or 0 if an error occurs
	*/
	static public char[] toChars( byte[] bytes )
		{
		char[] retVal = new char[bytes.length/2];
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
			DataInputStream dis = new DataInputStream(bis);
			for(int i = 0; i < retVal.length; i++)
				retVal[i] = dis.readChar();
			dis.close();
		}
		catch(IOException e) {
		}
		return retVal;
		}

	/**
		Returns the byte value of i in a new byte array.  Returns 0 if an error
		occurs.
		@param i the char to write
		@return the bytes that make up i
	*/
	static public byte[] writeChar( char i )
		{
		byte []retVal = null;
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(bos);
			dos.writeChar(i);
			dos.close();
			dos.flush();
			retVal = bos.toByteArray();
		}
		catch(IOException e) {
		}
		return retVal;
		}

	/**
		Returns the byte value of i in a new byte array.  Returns 0 if an error
		occurs.
		@param i the char[] to write
		@return the bytes that make up i
	*/
	static public byte[] writeChars( char[] i )
		{
		byte []retVal = null;
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(bos);
			for(int a = 0; a < i.length; a++)
				dos.writeChar(i[a]);
			dos.close();
			dos.flush();
			retVal = bos.toByteArray();
		}
		catch(IOException e) {
		}
		return retVal;
		}
}
