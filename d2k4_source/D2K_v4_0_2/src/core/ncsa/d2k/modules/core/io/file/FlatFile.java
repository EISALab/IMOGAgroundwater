package ncsa.d2k.modules.core.io.file;

import java.io.*;
import java.util.*;

public class FlatFile
    extends Exception {
  /***********/
  /* Globals */
  /***********/

  static int MaxNumColumns = 10000;

  public boolean ReadWholeLines = true;

  byte x;
  public String FileName;
  public long FileLength;
  String Direction;
  int BufferSize;
  public byte Buffer[];

  long Position;
  int BufferIndex;
  int ByteIndex;
  int NumLeftoverBytes = 0;

  int BufferNumBytes;
  int RealBufferNumBytes;

  public int LineNumBytes;

  public boolean EOF = false;

  RandomAccessFile File;

  public byte DelimiterByte = (byte) ',';
  public byte EOLByte = 10; // new line = 10 (unix) !!!
  public static byte SpaceByte = (byte) ' ';
  public static byte TabByte = 9;
  public static byte UnixEOLByte = 10;
  public static byte MacEOLByte = 13;
  public static byte QuoteByte = (byte) '"';

  final static byte ZeroByte = (byte) '0';
  final static byte MinusByte = (byte) '-';
  final static byte PlusByte = (byte) '+';
  final static byte DecimalByte = (byte) '.';
  final static byte LowerEByte = (byte) 'e';
  final static byte UpperEByte = (byte) 'E';

  public int LineStart;
  public int LineEnd;
  public int[] ColumnStarts = new int[MaxNumColumns];
  public int[] ColumnEnds = new int[MaxNumColumns];
  public int NumColumns;

  public boolean ReportOpenings = false;

  public FlatFile(String fileName, String direction, int bufferSize,
                  boolean readWholeLines) throws Exception {
    this.ReadWholeLines = readWholeLines;

    if (ReportOpenings)
      System.out.println("opening " + fileName);

    this.FileName = fileName;
    this.Direction = direction;
    this.BufferSize = bufferSize;
    BufferIndex = 0;
    NumLeftoverBytes = 0;
    Position = 0;
    Buffer = new byte[BufferSize];
    BufferNumBytes = 0;
    RealBufferNumBytes = 0;
    ByteIndex = 0;
    LineNumBytes = 0;
    EOF = false;

    try {
      if (Direction.equals("r")) {
        File = new RandomAccessFile(FileName, "r");
        FileLength = File.length();
        if (File == null) {

          EOF = true;
          System.out.println("user.dir: " + System.getProperty("user.dir"));
          System.out.println("couldn't open file: " + FileName);
          throw new Exception();
        }
      }
      else
      if (Direction.equals("w")) {
        eraseFile(FileName);
        File = new RandomAccessFile(FileName, "rw");
        if (File == null) {
          System.out.println("user.dir: " + System.getProperty("user.dir"));
          System.out.println("couldn't open file: " + FileName);
          throw new Exception();
        }
      }
    }
    catch (Exception e) {
      System.out.println("user.dir: " + System.getProperty("user.dir"));
      System.out.println("couldn't open file: " + FileName);
      throw new Exception();
    }

    positionByByte(0L);
  }

  final static int DefaultBufferSize = 500000;
  public FlatFile(String fileName, String direction) throws Exception {
    new FlatFile(fileName, direction, DefaultBufferSize, false);
  }

  public void close() throws Exception {
    if (File != null) {
      if (Direction.equals("w")) {
        writeNextBuffer();
      }
      try {
        File.close();
      }
      catch (Exception e) {
        System.out.println("couldn't close file");
        throw new Exception();
      }

    }
  }

  public void finalize() throws Exception {
    if (File != null) {
      try {
        File.close();
      }
      catch (Exception e) {
        System.out.println(e);
        e.printStackTrace();
        System.out.println("error:  file.close()");
        throw new Exception();
      }
    }

  }

  public void eraseFile(String fileName) {
    try {
      File file = new File(fileName);
      file.delete();
    }
    catch (Exception e) {
      System.out.println("eraseFile(" + fileName + ") failed");
    }

  }

  public boolean fileExists(String fileName) {
    try {
      RandomAccessFile file = new RandomAccessFile(fileName, "r");
      file.close();
      return true;
    }
    catch (Exception e) {
      return false;
    }
  }

  public void positionByByte(long newPosition) throws Exception {

    //System.out.println("newPosition = " + newPosition);

    BufferIndex = (int) (newPosition / (long) BufferSize);
    try {
      File.seek(BufferIndex * BufferSize);
    }
    catch (Exception e) {
      System.out.println("error:  File.seek(BufferIndex * BufferSize)");
      throw new Exception();
    }

    if (Direction.equals("r"))
      readNextBuffer();
    ByteIndex = (int) (newPosition % BufferSize);

    Position = newPosition;

  }

  public void positionByFraction(double newFraction) throws Exception {
    long newPosition = (long) (newFraction * FileLength);

    positionByByte(newPosition);
  }

  void readNextBuffer() throws Exception {

    try {

      if (NumLeftoverBytes > 0) {

        //System.out.println("shifting " + NumLeftoverBytes);

        int offset = RealBufferNumBytes - NumLeftoverBytes;
        for (int i = 0; i < NumLeftoverBytes; i++) {
          Buffer[i] = Buffer[offset + i];
        }
      }

      int numBytesRead = File.read(Buffer, NumLeftoverBytes,
                                   BufferSize - NumLeftoverBytes);

      //System.out.println("numBytesRead = " + numBytesRead);

      int actualNumBytesRead = numBytesRead;
      if (numBytesRead == -1) {
        actualNumBytesRead = 0;
      }

      RealBufferNumBytes = NumLeftoverBytes + actualNumBytesRead;

      if ( (numBytesRead == -1) && (RealBufferNumBytes == 0))
        EOF = true;

      NumLeftoverBytes = 0;

      if (ReadWholeLines) {
        for (int i = RealBufferNumBytes - 1; i >= 0; i--) {
          if (Buffer[i] == EOLByte)
            break;
          NumLeftoverBytes++;
        }
      }

      BufferNumBytes = RealBufferNumBytes - NumLeftoverBytes;

      if (BufferNumBytes == 0) // !!! does this work
        EOF = true;

        //System.out.println("NumLeftoverBytes = " + NumLeftoverBytes);
        //System.out.println("RealBufferNumBytes = " + RealBufferNumBytes);
        //System.out.println("BufferNumBytes = " + BufferNumBytes);

      BufferIndex++;

      ByteIndex = 0;

      //System.out.println("Buffer[BufferNumBytes - 1] = " + Buffer[BufferNumBytes - 1]);
      //System.out.println("readNextBuffer(); BufferNumBytes =" + BufferNumBytes);

    }
    catch (Exception e) {
      System.out.println("readNextBuffer");
      throw new Exception();
    }

  }

  void writeNextBuffer() throws Exception {

    try {
      File.write(Buffer, 0, ByteIndex);

      BufferIndex++;

      ByteIndex = 0;
    }
    catch (Exception e) {
      System.out.println("writeNextBuffer");
      throw new Exception();
    }

  }

  public byte readByte() throws Exception {
    //if (Position >= FileLength)
    //{
    //EOF = true;
    //return -1;
    //}

    if (ByteIndex >= BufferNumBytes)
      readNextBuffer();

      //Position++;

    return Buffer[ByteIndex++];
  }

  public char readI8() throws Exception {
    if (ByteIndex >= BufferSize)
      readNextBuffer();

    byte value = Buffer[ByteIndex++];

    if (value >= 0)
      return (char) value;
    else
      return (char) (256 + value);
  }

  public void writeByte(byte x) throws Exception {

    if (ByteIndex >= BufferSize)
      writeNextBuffer();

      //Position++;

    Buffer[ByteIndex++] = x;

  }

  public void skipToNextLine() throws Exception {
    while (true) {
      byte x;

      x = readByte();

      if (EOF)
        return;

      if (x == (byte) EOLByte) {
        return;
      }
    }
  }

  public void readLine() throws Exception {

    int localByteIndex = ByteIndex;
    int localBufferNumBytes = BufferNumBytes;
    byte localEOLByte = EOLByte;

    if (localByteIndex >= localBufferNumBytes) {
      readNextBuffer();
      localBufferNumBytes = BufferNumBytes;
      localByteIndex = ByteIndex;
    }

    LineStart = localByteIndex;

    int columnIndex = 0;
    ColumnStarts[columnIndex] = LineStart;

    while (true) {

      if (Buffer[localByteIndex] == DelimiterByte) {
        ColumnEnds[columnIndex] = localByteIndex;
        columnIndex++;
        ColumnStarts[columnIndex] = localByteIndex + 1;
        //System.out.println("columnIndex = " + columnIndex);
      }

      if (Buffer[localByteIndex++] == localEOLByte) {
        ColumnEnds[columnIndex] = localByteIndex - 2;
        break;
      }
    }

    NumColumns = columnIndex + 1;

    //System.out.println("NumColumns = " + NumColumns);

    LineEnd = localByteIndex;

    LineNumBytes = LineEnd - LineStart;

    ByteIndex = localByteIndex;

    //System.out.println("localByteIndex = " + columnIndex);
    //System.out.println("ByteIndex = " + ByteIndex);

  }

  public void writeLine(byte bytes[], int startIndex, int endIndex) throws
      Exception {
    for (int i = startIndex; i < endIndex; i++)
      writeByte(bytes[i]);
  }

  public void writeIntAsByteString(int sum) throws Exception {
    int startIndex;
    int endIndex;

    if (sum == 0) {
      writeByte(ZeroByte);
      return;
    }

    if (sum < 0) {
      writeByte( (byte) '-');
      sum = -sum;
    }

    int base = 1000000000;

    if (sum >= base) {
      System.out.println("sum >= base");
      throw new Exception();
    }

    while (base > sum) {
      base /= 10;
      //System.out.println("base = " + base);
    }

    if (base < 1)
      base = 1;

    while (base > 0) {

      int digit = sum / base;

      //System.out.println("digit = " + digit);
      //System.out.println("base = " + base);

      switch (digit) {
        case 0:
          writeByte( (byte) (ZeroByte + 0));
          break;
        case 1:
          writeByte( (byte) (ZeroByte + 1));
          break;
        case 2:
          writeByte( (byte) (ZeroByte + 2));
          break;
        case 3:
          writeByte( (byte) (ZeroByte + 3));
          break;
        case 4:
          writeByte( (byte) (ZeroByte + 4));
          break;
        case 5:
          writeByte( (byte) (ZeroByte + 5));
          break;
        case 6:
          writeByte( (byte) (ZeroByte + 6));
          break;
        case 7:
          writeByte( (byte) (ZeroByte + 7));
          break;
        case 8:
          writeByte( (byte) (ZeroByte + 8));
          break;
        case 9:
          writeByte( (byte) (ZeroByte + 9));
          break;
      }

      sum = sum - digit * base;

      base /= 10;
    }

  }

  public void writeIntAsByteStringPadded(int sum, int base) throws Exception {
    int startIndex;
    int endIndex;

    if (sum < 0) {
      writeByte( (byte) '-');
      sum = -sum;
    }

    if (sum >= base) {
      System.out.println("sum >= base");
      throw new Exception();
    }

    base /= 10;

    while (base > 0) {

      int digit = sum / base;

      //System.out.println("digit = " + digit);
      //System.out.println("base = " + base);

      switch (digit) {
        case 0:
          writeByte( (byte) (ZeroByte + 0));
          break;
        case 1:
          writeByte( (byte) (ZeroByte + 1));
          break;
        case 2:
          writeByte( (byte) (ZeroByte + 2));
          break;
        case 3:
          writeByte( (byte) (ZeroByte + 3));
          break;
        case 4:
          writeByte( (byte) (ZeroByte + 4));
          break;
        case 5:
          writeByte( (byte) (ZeroByte + 5));
          break;
        case 6:
          writeByte( (byte) (ZeroByte + 6));
          break;
        case 7:
          writeByte( (byte) (ZeroByte + 7));
          break;
        case 8:
          writeByte( (byte) (ZeroByte + 8));
          break;
        case 9:
          writeByte( (byte) (ZeroByte + 9));
          break;
      }

      sum = sum - digit * base;

      base /= 10;
    }

  }

  public void print(byte value) throws Exception {
    writeByte(value);
  }

  public void print(int value) throws Exception {
    writeIntAsByteString(value);
  }

  public void print(double value) throws Exception {

    if (value < 0.0) {
      writeByte( (byte) '-');
      value = -value;
    }

    int decimalBase = 1000;

    int preDecimalValue = (int) (value * decimalBase + 0.5) / decimalBase;
    int postDecimalValue = (int) (value * decimalBase + 0.5) % decimalBase;

    writeIntAsByteString(preDecimalValue);
    print( (byte) '.');
    writeIntAsByteStringPadded(postDecimalValue, decimalBase);
  }

  public void print(String string) throws Exception {
    byte bytes[] = string.getBytes();

    for (int i = 0; i < bytes.length; i++)
      writeByte(bytes[i]);
  }

  public void println() throws Exception {
    writeByte(EOLByte);
  }

  public void println(byte value) throws Exception {
    print(value);
    println();
  }

  public void println(int value) throws Exception {
    print(value);
    println();
  }

  public void println(double value) throws Exception {
    print(value);
    println();
  }

  public void println(String string) throws Exception {
    print(string);
    println();
  }

  public static int ByteStringToInt(byte byteString[], int startIndex,
                                    int endIndex) {
    int sum = 0;
    int sign = 1;
    for (int i = startIndex; i < endIndex; i++) {
      switch (byteString[i]) {
        case MinusByte:
          sign = sign * -1;
          break;

        case ZeroByte + 0:
          sum = sum * 10;
          break;
        case ZeroByte + 1:
          sum = sum * 10 + 1;
          break;
        case ZeroByte + 2:
          sum = sum * 10 + 2;
          break;
        case ZeroByte + 3:
          sum = sum * 10 + 3;
          break;
        case ZeroByte + 4:
          sum = sum * 10 + 4;
          break;
        case ZeroByte + 5:
          sum = sum * 10 + 5;
          break;
        case ZeroByte + 6:
          sum = sum * 10 + 6;
          break;
        case ZeroByte + 7:
          sum = sum * 10 + 7;
          break;
        case ZeroByte + 8:
          sum = sum * 10 + 8;
          break;
        case ZeroByte + 9:
          sum = sum * 10 + 9;
          break;

        default:
          sum = sum * 10;
          break;
      }
    }
    return sum * sign;
  }

  public static double ByteStringToDouble(byte byteString[], int startIndex,
                                          int endIndex) {
    double sum = 0.0;
    int expSum = 0;
    int decimalIndex = -1;
    int sign = 1;
    int expSign = 1;
    boolean exponent = false;
    double returnValue;

    int stringIndex;

    for (stringIndex = startIndex; stringIndex < endIndex && !exponent;
         stringIndex++) {
      switch (byteString[stringIndex]) {
        case DecimalByte:
          decimalIndex = stringIndex;
          break;

        case MinusByte:
          sign = sign * -1;
          break;

        case ZeroByte + 0:
          sum = sum * 10;
          break;
        case ZeroByte + 1:
          sum = sum * 10 + 1;
          break;
        case ZeroByte + 2:
          sum = sum * 10 + 2;
          break;
        case ZeroByte + 3:
          sum = sum * 10 + 3;
          break;
        case ZeroByte + 4:
          sum = sum * 10 + 4;
          break;
        case ZeroByte + 5:
          sum = sum * 10 + 5;
          break;
        case ZeroByte + 6:
          sum = sum * 10 + 6;
          break;
        case ZeroByte + 7:
          sum = sum * 10 + 7;
          break;
        case ZeroByte + 8:
          sum = sum * 10 + 8;
          break;
        case ZeroByte + 9:
          sum = sum * 10 + 9;
          break;

        case LowerEByte:
        case UpperEByte:
          exponent = true;

        default:
          sum = sum * 10;
          break;
      }
    }

    int numDecimals;

    if (decimalIndex == -1) {
      numDecimals = 0;
    }
    else {
      numDecimals = stringIndex - decimalIndex - 1;
    }

    for (int i = 0; i < numDecimals; i++) {
      sum /= 10.0;
    }

    if (!exponent) {
      returnValue = sign * sum;
    }
    else {

      switch (byteString[stringIndex]) {
        case MinusByte:
          expSign = -1;
          break;
        case PlusByte:
          expSign = 1;
          break;
      }

      expSum = 0;
      stringIndex++;
      for (int i = 0; i < 2; i++) {
        switch (byteString[stringIndex]) {
          case (byte) (ZeroByte + 0):
            expSum = expSum * 10;
            break;
          case (byte) (ZeroByte + 1):
            expSum = expSum * 10 + 1;
            break;
          case (byte) (ZeroByte + 2):
            expSum = expSum * 10 + 2;
            break;
          case (byte) (ZeroByte + 3):
            expSum = expSum * 10 + 3;
            break;
          case (byte) (ZeroByte + 4):
            expSum = expSum * 10 + 4;
            break;
          case (byte) (ZeroByte + 5):
            expSum = expSum * 10 + 5;
            break;
          case (byte) (ZeroByte + 6):
            expSum = expSum * 10 + 6;
            break;
          case (byte) (ZeroByte + 7):
            expSum = expSum * 10 + 7;
            break;
          case (byte) (ZeroByte + 8):
            expSum = expSum * 10 + 8;
            break;
          case (byte) (ZeroByte + 9):
            expSum = expSum * 10 + 9;
            break;
        }
        stringIndex++;
      }

      returnValue = sign * sum * Math.pow(10.0, expSign * expSum);
    }

    return returnValue;
  }

}