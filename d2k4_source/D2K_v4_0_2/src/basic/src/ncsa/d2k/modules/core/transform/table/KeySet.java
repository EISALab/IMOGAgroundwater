package ncsa.d2k.modules.core.transform.table;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */


     public class KeySet {
        String[] keys;

        KeySet(String[] k) {
                keys = k;
        }

        public boolean equals(Object o) {
                KeySet other = (KeySet)o;
                String [] otherkeys = other.keys;

                if(otherkeys.length != keys.length)
                        return false;

                for(int i = 0; i < keys.length; i++)
                        if(!keys[i].equals(otherkeys[i]))
                                return false;
                return true;
        }

        public String toString() {
                StringBuffer sb = new StringBuffer();
                for(int i = 0; i < keys.length; i++) {
                        sb.append(keys[i]);
                        sb.append(" ");
                }
                return sb.toString();
        }

        public int hashCode() {
                int result = 37;
                for(int i = 0; i < keys.length; i++)
                        result *= keys[i].hashCode();
                return result;
        }
}//KeySet
