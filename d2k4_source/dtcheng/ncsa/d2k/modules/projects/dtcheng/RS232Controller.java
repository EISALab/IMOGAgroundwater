package ncsa.d2k.modules.projects.dtcheng;

import ncsa.d2k.modules.projects.dtcheng.*;

import java.io.*;
import java.util.*;
import javax.comm.*;

public class RS232Controller
    implements SerialPortEventListener {

  public int readerBufferFillPosition;
  public int readerBufferReadPosition;

  static CommPortIdentifier portId;
  static Enumeration portList;

  InputStream inputStream;
  OutputStream outputStream;
  SerialPort serialPort;

  String portName;

  int BaudRate = 9600;
  byte EOLByte = 10;

  int TimeOut = 2000;
  int RS232BufferSize = 200;
  int ReaderBufferSize = 200;
  byte ReaderBuffer[];
  int NumBytesInBuffer;
  byte LineBuffer[];
  int LineBufferSize = 10000;
  int BufferValidNumBytes;

  public RS232Controller(String portName, int baudRate, byte eolByte) {

    this.portName = portName;
    this.BaudRate = baudRate;
    this.EOLByte = eolByte;
    this.ReaderBuffer = new byte[ReaderBufferSize];
    this.LineBuffer = new byte[LineBufferSize];
    this.readerBufferFillPosition = 0;
    this.readerBufferReadPosition = 0;

    portList = CommPortIdentifier.getPortIdentifiers();

    System.out.println("portList = " + portId);
    while (portList.hasMoreElements()) {
      portId = (CommPortIdentifier) portList.nextElement();
      System.out.println("portId = " + portId);
      System.out.println("portIdgetPortType() = " + portId.getPortType());
      if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
        if (portId.getName().equals(portName)) {
          break;
        }
      }
    }

    System.out.println("RS232Input found " + portId.getName());

    try {
      serialPort = (SerialPort) portId.open("SimpleReadApp", TimeOut);
    }
    catch (PortInUseException e) {}


    try {
      serialPort.setFlowControlMode(serialPort.FLOWCONTROL_NONE);
    }
    catch (UnsupportedCommOperationException ex) {
    }


    try {
      inputStream = serialPort.getInputStream();
    }
    catch (IOException e) {}

    try {
      outputStream = serialPort.getOutputStream();
    }
    catch (IOException e) {}

    try {
      serialPort.addEventListener(this);
    }
    catch (TooManyListenersException e) {}

    serialPort.notifyOnDataAvailable(true);
    serialPort.notifyOnBreakInterrupt(true);
    serialPort.notifyOnCarrierDetect(true);
    serialPort.notifyOnCTS(true);
    serialPort.notifyOnDataAvailable(true);
    serialPort.notifyOnDSR(true);
    serialPort.notifyOnFramingError(true);
    serialPort.notifyOnOutputEmpty(true);
    serialPort.notifyOnOverrunError(true);
    serialPort.notifyOnParityError(true);
    serialPort.notifyOnRingIndicator(true);

    try {
      serialPort.setSerialPortParams(BaudRate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
    }
    catch (UnsupportedCommOperationException e) {}

  }

  public void close() {
    serialPort.close();
  }

  public byte readByte() {

    //wait for next byte if necessary
    while (readerBufferFillPosition == readerBufferReadPosition) {
      try {
        Thread.sleep(1);
      }
      catch (InterruptedException e) {}
    }

    byte returnValue = ReaderBuffer[readerBufferReadPosition];

    readerBufferReadPosition++;
    if (readerBufferReadPosition == ReaderBufferSize)
      readerBufferReadPosition = 0;

    return returnValue;
  }

  public byte[] readLine() {
    int numBytesInLineBuffer = 0;

    while (true) {
      byte value = readByte();

      if (value == EOLByte) {
        break;
      }

      LineBuffer[numBytesInLineBuffer++] = value;
    }

    byte values[] = new byte[numBytesInLineBuffer];

    for (int i = 0; i < numBytesInLineBuffer; i++) {
      values[i] = LineBuffer[i];
    }

    return values;
  }

  int da_count = 0;
  byte rs232Buffer[] = new byte[RS232BufferSize];
  public void serialEvent(SerialPortEvent event) {
    switch (event.getEventType()) {
      case SerialPortEvent.BI:
        System.out.println("BI");
        break;
      case SerialPortEvent.OE:
        System.out.println("OE");
        break;
      case SerialPortEvent.FE:
        System.out.println("FE");
        break;
      case SerialPortEvent.PE:
        System.out.println("PE");
        break;
      case SerialPortEvent.CD:
        System.out.println("CD");
        break;
      case SerialPortEvent.CTS:
        //System.out.println("CTS");
        break;
      case SerialPortEvent.DSR:
        System.out.println("DSR");
        break;
      case SerialPortEvent.RI:
        System.out.println("BI");
        break;
      case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
        //System.out.println("OUTPUT_BUFFER_EMPTY");
        break;

      case SerialPortEvent.DATA_AVAILABLE:
        System.out.println("DATA_AVAILABLE count = " + da_count);
          da_count++;
        int numBytes = 0;
        try {
          while (inputStream.available() > 0) {
            numBytes = inputStream.read(rs232Buffer);
            for (int i = 0; i < numBytes; i++) {
              System.out.println("byte" + i + " = " + rs232Buffer[i]);
              ReaderBuffer[readerBufferFillPosition++] = rs232Buffer[i];
              if (readerBufferFillPosition == ReaderBufferSize)
                readerBufferFillPosition = 0;
            }
          }
        }
        catch (IOException e) {}
        break;

    }
  }

  public int bufferNumBytes() {
    if (readerBufferFillPosition >= readerBufferReadPosition)
      return (readerBufferFillPosition - readerBufferReadPosition);
    else
      return (readerBufferFillPosition + (ReaderBufferSize - readerBufferReadPosition));
  }

  public int writeByte(byte value) {
    byte[] bytes = new byte[1];
    bytes[0] = value;
    writeBytes(bytes);
    return 0;
  }

  public void writeBytes(byte[] bytes) {
    try {
      outputStream.write(bytes);
      //outputStream.flush();
    }
    catch (IOException ex) {
      System.out.println("error in writeByte -- IOException");
    }
  }

}