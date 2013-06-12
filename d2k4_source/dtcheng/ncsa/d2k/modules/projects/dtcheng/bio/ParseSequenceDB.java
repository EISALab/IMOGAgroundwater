package ncsa.d2k.modules.projects.dtcheng.bio;

import java.util.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.io.file.*;
import ncsa.d2k.modules.projects.dtcheng.io.*;

public class ParseSequenceDB
    extends ComputeModule {

  private boolean Trace = false;

  public void setTrace(boolean value) {
    this.Trace = value;
  }

  public boolean getTrace() {
    return this.Trace;
  }


  public String getModuleInfo() {
    return "ParseSequenceDB";
  }

  public String getModuleName() {
    return "ParseSequenceDB";
  }

  public String[] getInputTypes() {
    String[] types = {
        "[B"};
    return types;
  }

  public String[] getOutputTypes() {
    String[] types = {
        "java.util.Hashtable",
        "java.util.Hashtable",
        "[[B",
    };
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

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "GeneAASequences";
      default:
        return "No such output";
    }
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "AccessionNumberToAccessionAndSequenceIndexHashTable";
      case 1:
        return "DuplicateAccessionNumberHashTable";
      case 2:
        return "GeneAASequences";
      default:
        return "NO SUCH OUTPUT!";
    }
  }

  int LineCount = 0;
  int acLineCount = 0;
  int acNameCount = 0;
  int SequenceIndex = 0;
  int AccessionNumberIndex = 0;
  int DuplicateACNameIndex = 0;
  Hashtable AccessionNumberToAccessionAndSequenceIndexHashTable = null;
  Hashtable DuplicateAccessionNumberHashTable = null;
  int totalNumAAs = 0;
  int totalNumBytes = 0;
  boolean InSequence = false;
  int numLinesToRead = 0;
  int numAAs = 0;
  int AAIndex = 0;
  int MaxNumSequences = 4000000;
  byte [][] GeneSequences= null;

  public void beginExecution() {

    LineCount = 0;
    acLineCount = 0;
    acNameCount = 0;
    SequenceIndex = 0;
    AccessionNumberIndex = 0;
    DuplicateACNameIndex = 0;
    AccessionNumberToAccessionAndSequenceIndexHashTable = new Hashtable();
    DuplicateAccessionNumberHashTable = new Hashtable();
    totalNumAAs = 0;
    totalNumBytes = 0;
    InSequence = false;
    numLinesToRead = 0;
    numAAs = 0;
    AAIndex = 0;
	GeneSequences= new byte[MaxNumSequences][];
  }

  public void doit() {

    Object object = this.pullInput(0);

    if (object == null) {

      System.out.println("LineCount            = " + LineCount);
      System.out.println("acLineCount          = " + acLineCount);
      System.out.println("acNameCount          = " + acNameCount);
      System.out.println("sqLineCount          = " + SequenceIndex);
      System.out.println("acNameIndex          = " + AccessionNumberIndex);
      System.out.println("DuplicateACNameIndex = " + DuplicateACNameIndex);
      System.out.println("totalNumAAs          = " + totalNumAAs);
      System.out.println("totalNumBytes        = " + totalNumBytes);
      System.out.println("numAAs               = " + numAAs);
      System.out.println("AAIndex              = " + AAIndex);

      this.pushOutput(AccessionNumberToAccessionAndSequenceIndexHashTable, 0);
      this.pushOutput(DuplicateAccessionNumberHashTable, 1);
      this.pushOutput(GeneSequences, 2);

      return;

    }

    byte[] LineBufferArray = (byte[]) object;

    if (Trace) {
      System.out.println(new String(LineBufferArray));
    }

    int length = LineBufferArray.length;

    totalNumBytes += length;

    LineCount++;

    if (InSequence) {

      int ptr = 4;
      int numAAsLeftToRead = numAAs - AAIndex;
      int numBlocksToRead = (numAAsLeftToRead - 1) / 10 + 1;
      if (numBlocksToRead > 6) {
        numBlocksToRead = 6;
      }
      //System.out.println("numBlocksToRead = " + numBlocksToRead);
      for (int blockIndex = 0; blockIndex < numBlocksToRead; blockIndex++) {
        // skip beginning block blank
        ptr += 1;
        numAAsLeftToRead = numAAs - AAIndex;
        int nummAAsInBlockToRead = numAAsLeftToRead;
        if (nummAAsInBlockToRead > 10) {
          nummAAsInBlockToRead = 10;
        }
        //System.out.println("nummAAsInBlockToRead = " + nummAAsInBlockToRead);
        for (int i = 0; i < nummAAsInBlockToRead; i++) {
          //System.out.println("ptr     = " + ptr);
          //System.out.println("AAIndex = " + AAIndex);
          //System.out.println("char    = " + (char) array[ptr]);
          GeneSequences[SequenceIndex][AAIndex] = LineBufferArray[ptr];
          AAIndex++;
          ptr++;
        }
      }

      numLinesToRead--;
      if (numLinesToRead == 0) {
        InSequence = false;
        SequenceIndex++;
      }
    }
    else {

      if ((LineBufferArray[0] == (byte) 'A') &&
          (LineBufferArray[1] == (byte) 'C') &&
          (LineBufferArray[2] == (byte) ' ') &&
          (LineBufferArray[3] == (byte) ' ') &&
          (LineBufferArray[4] == (byte) ' ')) {

        acLineCount++;
        int ptr = 5;
        int NameCount = 0;
        while (ptr < length) {
          if (LineBufferArray[ptr] == (byte) ';') {
            acNameCount++;
            NameCount++;
          }
          ptr++;
        }

        // skip to start of name
        ptr = 5;
        int NameIndex = 0;
        while ((ptr < length) && (NameIndex < NameCount)) {

          int nameStartPtr = ptr;
          while (LineBufferArray[ptr] != (byte) ';') {
            ptr++;
          }
          int nameEndPtr = ptr - 1;
          int nameLength = nameEndPtr - nameStartPtr + 1;
          byte[] name = new byte[nameLength];
          for (int i = 0; i < nameLength; i++) {
            name[i] = LineBufferArray[nameStartPtr + i];
          }


          String nameString = new String(name);
          //System.out.println("nameString = " + nameString);

          NameIndex++;

          if (!AccessionNumberToAccessionAndSequenceIndexHashTable.containsKey(nameString)) {
            int [] AccessionAndSequenceIndex = new int[2];
            AccessionAndSequenceIndex[0] = AccessionNumberIndex;
            AccessionAndSequenceIndex[1] = SequenceIndex;
            AccessionNumberToAccessionAndSequenceIndexHashTable.put(nameString, AccessionAndSequenceIndex);
            AccessionNumberIndex++;
          }
          else {
            System.out.println("error key exists:  nameString = " + nameString);
            if (!DuplicateAccessionNumberHashTable.containsKey(nameString)) {
              DuplicateAccessionNumberHashTable.put(nameString, new Integer(SequenceIndex));
              DuplicateACNameIndex++;
            }
          }

          ptr++;
        }

      }

      if ((LineBufferArray[0] == (byte) 'S') &&
          (LineBufferArray[1] == (byte) 'Q') &&
          (LineBufferArray[2] == (byte) ' ') &&
          (LineBufferArray[3] == (byte) ' ') &&
          (LineBufferArray[4] == (byte) ' ')) {
        int numStart = 16;
        int numEnd = -1;
        int p = numStart;
        while (true) {
          if (LineBufferArray[p] == (byte) ' ') {
            numEnd = p;
            break;
          }
          p++;
        }
        numAAs = FlatFile.ByteStringToInt(LineBufferArray, numStart, numEnd);
        GeneSequences[SequenceIndex] = new byte[numAAs];
        AAIndex = 0;
        totalNumAAs += numAAs;
        numLinesToRead = (numAAs - 1) / 60 + 1;

        InSequence = true;
        if (Trace) {
          System.out.println("numAAs         = " + numAAs);
          System.out.println("numLinesToRead = " + numLinesToRead);
        }
      }

    }
  }

}