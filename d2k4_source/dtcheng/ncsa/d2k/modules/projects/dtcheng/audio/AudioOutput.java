package ncsa.d2k.modules.projects.dtcheng.audio;

import ncsa.d2k.core.modules.ComputeModule;
import javax.sound.sampled.*;
import ncsa.d2k.modules.projects.dtcheng.*;
import ncsa.d2k.modules.projects.dtcheng.primitive.*;

public class AudioOutput extends ComputeModule
{
  //////////////////
  //  PROPERTIES  //
  //////////////////

  private int        PreLoadPadding = 102400;
  public  void    setPreLoadPadding (int value) {       this.PreLoadPadding = value;}
  public  int     getPreLoadPadding ()          {return this.PreLoadPadding;}

  private int        BufferSize = 102400;
  public  void    setBufferSize (int value) {       this.BufferSize = value;}
  public  int     getBufferSize ()          {return this.BufferSize;}

  public boolean ReadAllBytesBeforePlaying     = true;
  public void    setReadAllBytesBeforePlaying (boolean value) {       this.ReadAllBytesBeforePlaying       = value;}
  public boolean getReadAllBytesBeforePlaying ()              {return this.ReadAllBytesBeforePlaying;}

  private int        ClipBufferSize = 10000000;
  public  void    setClipBufferSize (int value) {       this.ClipBufferSize = value;}
  public  int     getClipBufferSize ()          {return this.ClipBufferSize;}



  ////////////////////
  //  INFO METHODS  //
  ////////////////////

  public String getModuleInfo() {
    return "AudioOutput";
  }
  public String getModuleName() {
    return "AudioOutput";
  }


  public String getInputName(int i) {
    switch (i)
    {
      case 0: return "TriggerObject";
    }
    return "";
  }
  public String[] getInputTypes()
  {
    String[] types = {"java.lang.Object"};
    return types;
  }
  public String getInputInfo(int i)
  {
    switch (i) {
      case 0: return "this input triggers firing of module";
      default: return "No such input";
    }
  }



  public String getOutputName(int i) {
    switch (i) {
    }
    return "";
  }
  public String[] getOutputTypes() {
    String[] types = {""};
    return types;
  }
  public String getOutputInfo(int i) {
    switch (i) {
      default: return "No such output";
    }
  }

  int numChannels      = 2;
  int sampleSizeInBits = 16;
  int frameSize        = numChannels * 2;

  boolean sourceLineStarted = false;
  public void playAudioBuffer(byte [] byteBuffer) throws Exception
  {
    // Move the data until done or there is an
    // error.
    //sourceLine.write( buffer, 0, bufferSize );
    //System.out.println("sourceLine.getMicrosecondPosition() = " + sourceLine.getMicrosecondPosition());

    //while (sourceLine.getMicrosecondPosition() > 0)
    //{
    //System.out.println("sourceLine.getMicrosecondPosition() = " + sourceLine.getMicrosecondPosition());
    //}

    //System.out.println("byteBuffer.length      = " + byteBuffer.length);

    if (true) {
      int spaceAvailable = sourceLine.available();
      //System.out.println("sourceLine.available() = " + sourceLine.available());
      if (true) {
        while (spaceAvailable < byteBuffer.length) {
          spaceAvailable = sourceLine.available();
          System.out.println("waiting for room ... spaceAvailable = " + spaceAvailable);
          wait(1);
        }
      }

      //if (spaceAvailable == BufferSize)
        //System.out.println("Error!  spaceAvailable == BufferSize");
    }
    //System.out.println("BEFORE WRITE sourceLine.available() = " + sourceLine.available());
    //System.out.println("BEFORE WRITE sourceLine.getFramePosition() = " + sourceLine.getFramePosition());
    //System.out.println("BEFORE WRITE sourceLine.getBufferSize() = " + sourceLine.getBufferSize());

    //long pushTimeStart = System.currentTimeMillis();
    sourceLine.write(byteBuffer, 0, byteBuffer.length);
    //for (int i = 0; i < byteBuffer.length; i += 4) {
      //sourceLine.write(byteBuffer, i, 4);
    //}
    //long pushTimeEnd = System.currentTimeMillis();

    //System.out.println("push time = " + (pushTimeEnd - pushTimeStart));
    //System.out.println("AFTER WRITE sourceLine.available() = " + sourceLine.available());
    //System.out.println("AFTER WRITE sourceLine.getFramePosition() = " + sourceLine.getFramePosition());
    //System.out.println("AFTER WRITE sourceLine.getBufferSize() = " + sourceLine.getBufferSize());

    // Continues data line I/O until its buffer is drained.
    //sourceLine.drain();

    // Closes the data line, freeing any resources such
    // as the audio device.
    //sourceLine.close();
  }



  public void playClipBuffer(byte [] byteBuffer, int numBytes) throws Exception
  {

    sourceLine.start();
    sourceLine.write(byteBuffer, 0, numBytes);

    while (sourceLine.isActive()) {
      wait(1);
      //System.out.println("sourceLine.getFramePosition() = " + sourceLine.getFramePosition());
      //System.out.println("sourceLine.available() = " + sourceLine.available());
    }

  }



  public void addToClipBuffer(byte [] byteBuffer) throws Exception
  {
    int numBytes = byteBuffer.length;
    System.arraycopy(byteBuffer, 0, clipBuffer, clipPosition, numBytes);
    clipPosition += numBytes;
  }



  boolean InitialExecution = false;
  byte [] clipBuffer = null;
  int     clipPosition = -1;
  SourceDataLine sourceLine = null;
  public void beginExecution()
  {
    InitialExecution = true;

    if (ReadAllBytesBeforePlaying) {
      clipPosition = 0;
      if ((clipBuffer == null) || (clipBuffer.length != ClipBufferSize))
        clipBuffer = new byte[ClipBufferSize];
    }


    if (sourceLine == null)
    {
      AudioFormat audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100.0F, sampleSizeInBits, numChannels, frameSize, 44100.0F, false);
      //System.out.println("AudioPlayer.playAudioBuffer audio format: " + audioFormat );

      DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat );
      if ( !AudioSystem.isLineSupported( info ) ) {
        System.out.println("AudioPlayer.playAudioBuffer does not " + " handle this type of audio." );
        return;
      }

      try
      {
        sourceLine = (SourceDataLine) AudioSystem.getLine( info );
        /*
        Line.Info sourceLineInfo = sourceLine.getLineInfo();
        System.out.println("sourceLine.Info.getMinBufferSize() = " + sourceLineInfo.getMinBufferSize() );
        System.out.println("sourceLine.Info.getMaxBufferSize() = " + sourceLineInfo.getMaxBufferSize() );
        */

        sourceLine.open(audioFormat,  BufferSize);
        //sourceLine.open(audioFormat);
        setBufferSize(sourceLine.getBufferSize());
        System.out.println("AudioOutput BufferSize = " + BufferSize);

    if (!sourceLineStarted) {
      sourceLine.start();
      sourceLineStarted = true;
    }


        //sourceLine.start();
      }
      catch (LineUnavailableException e) {
        e.printStackTrace();
      }
    }

  }


  void wait (int time)  throws Exception {
    try {
      synchronized (Thread.currentThread()) {
        Thread.currentThread().sleep(time);
      }
      } catch (Exception e) {System.out.println("wait error!!!"); throw e;}
  }

  byte [] audioPreLoadBuffer = null;
  public void doit() throws Exception
  {
    if (InitialExecution) {
      InitialExecution = false;
      if (!ReadAllBytesBeforePlaying) {
        audioPreLoadBuffer = new byte[PreLoadPadding];
        playAudioBuffer(audioPreLoadBuffer);
        System.out.println("DOIT sourceLine.available() = " + sourceLine.available());

      }
    }
    byte [] audioBuffer = (byte []) this.pullInput(0);

    if (audioBuffer == null) {
      // play buffer as a clip
      if (ReadAllBytesBeforePlaying) {
        playClipBuffer(clipBuffer, clipPosition);
      }
      // Closes the data line, freeing any resources such
      // as the audio device.
      //sourceLine.close();

      beginExecution();
      return;
    }

    if (ReadAllBytesBeforePlaying) {
      addToClipBuffer(audioBuffer);
    }
    else {
      playAudioBuffer(audioBuffer);
    }

  }
}
