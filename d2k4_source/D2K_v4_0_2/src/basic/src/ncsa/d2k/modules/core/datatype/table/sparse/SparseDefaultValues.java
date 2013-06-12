package ncsa.d2k.modules.core.datatype.table.sparse;


import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

/**
 * @author dsears
 *
 */
public class SparseDefaultValues {

        /** this is the default value for longs, ints, and shorts. */
        static private int defaultInt = 0;

        /** default for float double and extended. */
        static private double defaultDouble = 0.0;

        /** default string. */
        static private String defaultString = "";

        /** default boolean. */
        static private boolean defaultBoolean = false;

        /** default char array. */
        static private char[] defaultCharArray = {'\000'};

        /** default byte array. */
        static private byte[] defaultByteArray = {(byte)'\000'};

        /** default char. */
        static private char defaultChar = '\000';

        /** default byte. */
        static private byte defaultByte = (byte)'\000';

        /** default missing object. */
        static private Object defaultObject = null;

        /** return the default missing value for integers, both short, int and long.
         * @returns the integer for missing value.
         */
        static public Object getDefaultObject () {
                return defaultObject;
        }

        /** return the default missing value for integers, both short, int and long.
         * @param the integer for missing values.
         */
        static public void setDefaultObject (Object obj) {
                defaultObject = obj;
        }

        /** return the default missing value for integers, both short, int and long.
         * @returns the integer for missing value.
         */
        static public int getDefaultInt () {
                return defaultInt;
        }

        /** return the default missing value for integers, both short, int and long.
         * @param the integer for missing values.
         */
        static public void setDefaultInt (int newMissingInt) {
                defaultInt = newMissingInt;
        }

        /** return the default missing value for doubles, floats and extendeds.
         * @returns the double for missing value.
         */
        static public double getDefaultDouble () {
                return defaultDouble;
        }

        /** return the default missing value for integers, both short, int and long.
         * @param the integer for missing values.
         */
        static public void setDefaultDouble (double newMissingDouble) {
                defaultDouble = newMissingDouble;
        }

        /** return the default missing value for doubles, floats and extendeds.
         * @returns the double for missing value.
         */
        static public String getDefaultString () {
                return defaultString;
        }

        /** return the default missing value for integers, both short, int and long.
         * @param the integer for missing values.
         */
        static public void setDefaultString (String newMissingString) {
                defaultString = newMissingString;
        }

        /** return the default missing value for doubles, floats and extendeds.
         * @returns the double for missing value.
         */
        static public boolean getDefaultBoolean() {
                return defaultBoolean;
        }

        /** return the default missing value for integers, both short, int and long.
         * @param the integer for missing values.
         */
        static public void setDefaultBoolean(boolean newMissingBoolean) {
                defaultBoolean = newMissingBoolean;
        }

        /** return the default missing value for doubles, floats and extendeds.
         * @returns the double for missing value.
         */
        static public char[] getDefaultChars() {
                return defaultCharArray;
        }

        /** return the default missing value for integers, both short, int and long.
         * @param the integer for missing values.
         */
        static public void setDefaultChars(char[] newMissingChars) {
                defaultCharArray = newMissingChars;
        }

        /** return the default missing value for doubles, floats and extendeds.
         * @returns the double for missing value.
         */
        static public byte[] getDefaultBytes() {
                return defaultByteArray;
        }

        /** return the default missing value for integers, both short, int and long.
         * @param the integer for missing values.
         */
        static public void setDefaultBytes(byte[] newMissingBytes) {
                defaultByteArray = newMissingBytes;
        }

        /** return the default missing value for doubles, floats and extendeds.
         * @returns the double for missing value.
         */
        static public char getDefaultChar() {
                return defaultChar;
        }

        /** return the default missing value for integers, both short, int and long.
         * @param the integer for missing values.
         */
        static public void setDefaultChar(char newMissingChar) {
                defaultChar = newMissingChar;
        }

        /** return the default missing value for doubles, floats and extendeds.
         * @returns the double for missing value.
         */
        static public byte getDefaultByte() {
                return defaultByte;
        }

        /** return the default missing value for integers, both short, int and long.
         * @param the integer for missing values.
         */
        static public void setDefaultByte(byte newMissingByte) {
                defaultByte = newMissingByte;
        }
}
