package ncsa.d2k.modules.projects.dtcheng.bio;

import java.util.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.io.file.*;

public class ParseFastaSequenceDB
    extends ComputeModule {

  int MaxNumSequenceNames = 100;

  private boolean ConvertToBaseIndices = false;
  public void setConvertToBaseIndices(boolean value) {
    this.ConvertToBaseIndices = value;
  }

  public boolean getConvertToBaseIndices() {
    return this.ConvertToBaseIndices;
  }

  private boolean Trace = false;
  public void setTrace(boolean value) {
    this.Trace = value;
  }

  public boolean getTrace() {
    return this.Trace;
  }

  public String getModuleInfo() {
    return "ParseFastaSequenceDB";
  }

  public String getModuleName() {
    return "ParseFastaSequenceDB";
  }

  public String[] getInputTypes() {
    String[] types = {
        "[B"};
    return types;
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "ByteArray";
      default:
        return "No such input";
    }
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "ByteArray";
      default:
        return "NO SUCH INPUT!";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {
        "[[S",
        "[[B",
    };
    return types;
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "SequenceNames";
      case 1:
        return "SequenceElements";
      default:
        return "No such output";
    }
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "SequenceNames";
      case 1:
        return "SequenceElements";
      default:
        return "NO SUCH OUTPUT!";
    }
  }

  int LineCount = 0;
  int idLineCount = 0;
  int acLineCount = 0;
  int acNameCount = 0;
  int SequenceIndex = 0;
  Hashtable AccessionNumberHashTable = null;
  int totalNumAAs = 0;
  int totalNumBytes = 0;
  int numLinesToRead = 0;
  int SequenceElementIndex = 0;
  int MaxSequenceLength = 10000000;
  int SequenceBufferPtr = -1;
  byte[] SequenceBuffer = new byte[MaxSequenceLength];
  int MaxNumSequences = 4000000;

  String[][] WorkSequenceNames = new String[MaxNumSequences][];
  byte[][] WorkGeneSequences = new byte[MaxNumSequences][];

  public void beginExecution() {

    LineCount = 0;
    idLineCount = 0;
    acLineCount = 0;
    acNameCount = 0;
    SequenceIndex = 0;
    AccessionNumberHashTable = new Hashtable();
    totalNumAAs = 0;
    totalNumBytes = 0;
    numLinesToRead = 0;
    SequenceElementIndex = 0;
    SequenceBufferPtr = 0;
  }

  public void doit() throws Exception {

    boolean ReadNameState;
    boolean ReadSequenceState;
    boolean EndOfFileState;

    // read next line byte array or null indicating end of stream
    Object object = this.pullInput(0);

    // if end of stream, output final results
    if (object == null) {

      if (SequenceBufferPtr > 0) {
        byte[] sequence = new byte[SequenceBufferPtr];
        for (int i = 0; i < SequenceBufferPtr; i++) {
          sequence[i] = SequenceBuffer[i];
        }
        SequenceBufferPtr = 0;
        WorkGeneSequences[SequenceIndex - 1] = sequence;
      }

      System.out.println("LineCount            = " + LineCount);
      System.out.println("idLineCount          = " + idLineCount);
      System.out.println("acLineCount          = " + acLineCount);
      System.out.println("acNameCount          = " + acNameCount);
      System.out.println("GeneIndex            = " + SequenceIndex);
      System.out.println("totalNumAAs          = " + totalNumAAs);
      System.out.println("totalNumBytes        = " + totalNumBytes);
      System.out.println("AAIndex              = " + SequenceElementIndex);

      int numGenesParsed = SequenceIndex;
      String[][] ResultSequenceNames = new String[numGenesParsed][];
      byte[][] ResultSequences = new byte[numGenesParsed][];

      for (int i = 0; i < numGenesParsed; i++) {

        ResultSequenceNames[i] = WorkSequenceNames[i];
        ResultSequences[i] = WorkGeneSequences[i];

        byte[] sequence = ResultSequences[i];
        int length = sequence.length;

        if (false) {
          System.out.println("WorkSequenceNames[" + i + "] = " + WorkSequenceNames[i][0]);
          System.out.println("length = " + length);
        }

        // verify and convert to element indices if necessary
        if (ConvertToBaseIndices) {
          for (int j = 0; j < length; j++) {
            switch (sequence[j]) {
              case (byte) 'a':
                sequence[j] = 0;
                break;
              case (byte) 'A':
                sequence[j] = 0;
                break;
              case (byte) 'c':
                sequence[j] = 1;
                break;
              case (byte) 'C':
                sequence[j] = 1;
                break;
              case (byte) 'g':
                sequence[j] = 2;
                break;
              case (byte) 'G':
                sequence[j] = 2;
                break;
              case (byte) 't':
                sequence[j] = 3;
                break;
              case (byte) 'T':
                sequence[j] = 3;
                break;
              default:
                System.out.println("Error base (" + (char) sequence[j] + ") not recognized");
                break;
            }
          }
        }

        if (false) {
          System.out.println("name = " + ResultSequenceNames[i]);
          System.out.println("seq:");
          for (int j = 0; j < 10; j++) {
            System.out.print( (char) ResultSequences[i][j]);
          }
          System.out.println();
        }
      }

      this.pushOutput(ResultSequenceNames, 0);
      this.pushOutput(ResultSequences, 1);

    }
    else {

      // cast line object into byte array

      byte[] LineByteArray = (byte[]) object;

      int length = LineByteArray.length;

      if (length < 1) {
        return;
      }

      totalNumBytes += length;

      LineCount++;

      if (LineByteArray[0] == (byte) '>') {
        ReadNameState = true;
        ReadSequenceState = false;
        EndOfFileState = false;
      }
      else
      if (length == 0) {
        ReadNameState = false;
        ReadSequenceState = false;
        EndOfFileState = true;
      }
      else {
        ReadNameState = false;
        ReadSequenceState = true;
        EndOfFileState = false;
      }

      if (ReadNameState) {

        if (SequenceBufferPtr > 0) {
          byte[] sequence = new byte[SequenceBufferPtr];
          for (int i = 0; i < SequenceBufferPtr; i++) {
            sequence[i] = SequenceBuffer[i];
          }
          WorkGeneSequences[SequenceIndex - 1] = sequence;
          SequenceBufferPtr = 0;
        }

        //System.out.println("ReadGeneNameState");

        // parse sequence name list
        int NumNames = 0;
        int[] NamePtrs = new int[MaxNumSequenceNames];
        int ptr = 1;
        NamePtrs[0] = 1;
        try {
          while ( (ptr < LineByteArray.length) && (LineByteArray[ptr] != (byte) ' ')) {
            if (LineByteArray[ptr] == (byte) '|') {
              NamePtrs[NumNames + 1] = ptr + 1;
              NumNames++;
            }
            ptr++;
          }
          NamePtrs[NumNames + 1] = ptr + 1;
          NumNames++;
        }
        catch (Exception ex) {
          String line = new String(LineByteArray);
          System.out.println("Error!!!");
          System.out.println(line);
          throw ex;
        }

        if (false)
          System.out.println("NumNames = " + NumNames);

        String[] SequenceNames = new String[NumNames];
        for (int NameIndex = 0; NameIndex < NumNames; NameIndex++) {

          int nameEndPtr = NamePtrs[NameIndex + 1] - 2;
          int nameLength = nameEndPtr - NamePtrs[NameIndex] + 1;
          byte[] name = new byte[nameLength];
          for (int j = 0; j < nameLength; j++) {
            name[j] = LineByteArray[NamePtrs[NameIndex] + j];
          }

          String SequenceName = new String(name);

          if (false) {
            System.out.println("SequenceName = " + SequenceName + " SequenceNameIndex = " + SequenceIndex);
          }

          SequenceNames[NameIndex] = SequenceName;
          if (!AccessionNumberHashTable.containsKey(SequenceName)) {
            AccessionNumberHashTable.put(SequenceName, new Integer(SequenceIndex));
          }
          else {
            System.out.println("warning key exists:  nameString = " + SequenceName);
          }
        }
        WorkSequenceNames[SequenceIndex] = SequenceNames;
        SequenceIndex++;

      }
      else
      if (ReadSequenceState) {
        //System.out.println("ReadSequenceState");

        int NumSequenceElements = length;

        for (int i = 0; i < NumSequenceElements; i++) {
          SequenceBuffer[SequenceBufferPtr++] = LineByteArray[i];
        }
      }
    }
  }

}