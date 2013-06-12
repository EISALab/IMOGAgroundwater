package ncsa.d2k.modules.projects.dtcheng;


/******************************************************
 * File: MyDataSinkListener.java
 * created 24.07.2001 21:41:47 by David Fischer, fischer@d-fischer.com
 * Decription: simple data sink listener, used to check for end of stream
 */

import javax.media.datasink.*;


public class MyDataSinkListener implements DataSinkListener {
  boolean endOfStream = false;

  public void dataSinkUpdate(DataSinkEvent event) {
    if (event instanceof javax.media.datasink.EndOfStreamEvent)
      endOfStream = true;
  }


  public void waitEndOfStream(long checkTimeMs) {
    while (!endOfStream) {
      Stdout.log("datasink: waiting for end of stream ...");
      try {Thread.currentThread().sleep(checkTimeMs);
      } catch (InterruptedException ie) {}
    }
    Stdout.log("datasink: ... end of stream reached.");
  }
}
