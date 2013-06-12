package ncsa.d2k.modules.core.optimize.util;

 /**
  * <P>Captures the output of an InputStream.</P>
  *
  * With acknowledgements to Michael C. Daconta, author of "Java Pitfalls,
  * Time Saving Solutions, and Workarounds to Improve Programs." and his
  * article in JavaWorld "When Runtime.exec() Won't". Adapted from 'StreamGobbler.java'
  * written by smccrory@users.sourceforge.net">Scott McCrory</
  *
  * See the ExecRunner class for a reference implementation.
  *
  * @author <Meghna Babbar</a>.
  */

 import java.io.BufferedReader;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.InputStreamReader;
 import java.io.ObjectInputStream;
 import java.io.ObjectOutputStream;
 import java.io.OutputStream;
 import java.io.PrintWriter;


 class StreamGobbler extends Thread {

     /** The input stream we're gobbling **/
     private InputStream in = null;

     /** The printwriter we'll send the gobbled characters to if asked**/
     private PrintWriter pwOut = null;

     /** Our flag to allow us to safely terminate the monitoring thread **/
     private boolean quit = false;

     /**
      * A simpler constructor for StreamGobbler - defaults to stdout.
      *
      * @param in InputStream
      */
     public StreamGobbler(InputStream in) {

         this.in = in;
         this.pwOut = new PrintWriter(System.out, true);

     }

     /**
      * A more explicit constructor for StreamGobbler where you can tell
      * it exactly where to relay the output to.
      *
      * @param in InputStream
      * @param out OutputStream
      */
     public StreamGobbler(InputStream in, OutputStream out) {

         this.in = in;
         this.pwOut = new PrintWriter(out, true);

     }

     /**
      * A more explicit constructor for StreamGobbler where you can tell
      * it exactly where to relay the output to.
      *
      * @param in InputStream
      * @param pwOut PrintWriter
      */
     public StreamGobbler(InputStream in, PrintWriter pwOut) {

         this.in = in;
         this.pwOut = pwOut;

     }

     /**
      * We override the <code>clone</code> method here to prevent cloning of our class.
      *
      * @throws CloneNotSupportedException To indicate cloning is not allowed
      * @return Nothing ever really returned since we throw a CloneNotSupportedException
      **/
     public final Object clone() throws CloneNotSupportedException {

         throw new CloneNotSupportedException();

     }

     /**
      * Tells the StreamGobbler to quit it's operation.
      * This is safer than using stop() since it uses a semophore checked in the
      * main wait loop instead of possibly forcing semaphores to untimely unlock.
      */
     public void quit() {

         quit = true;

     }

     /**
      * We override the <code>readObject</code> method here to prevent
      * deserialization of our class for security reasons.
      *
      * @param in java.io.ObjectInputStream
      * @throws IOException thrown if a problem occurs
      **/
     private final void readObject(ObjectInputStream in) throws IOException {

         throw new IOException("Object cannot be deserialized");

     }

     /**
      * Gobbles up all the stuff coming from the InputStream and
      * sends it to the OutputStream specified during object construction.
      **/
     public void run() {

         try {

             // Set up the input stream
             InputStreamReader isr = new InputStreamReader(in);
             BufferedReader br = new BufferedReader(isr);

             // Initialize the temporary results containers
             String line = null;

             // Main processing loop which captures the output
             while ((line = br.readLine()) != null) {
                 if (quit) {
                     break;
                 }
                 else {
                     pwOut.println(line);
                 }
             }

         }
         catch (Exception e) {
             e.printStackTrace();
         }

     }

     /**
      * We override the <code>writeObject</code> method here to prevent
      * serialization of our class for security reasons.
      *
      * @param out java.io.ObjectOutputStream
      * @throws IOException thrown if a problem occurs
      **/
     private final void writeObject(ObjectOutputStream out) throws IOException {

         throw new IOException("Object cannot be serialized");

     }

}