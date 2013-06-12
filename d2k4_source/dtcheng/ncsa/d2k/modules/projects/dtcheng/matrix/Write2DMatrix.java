package ncsa.d2k.modules.projects.dtcheng.matrix;

import java.io.*;
import java.lang.Math.*;
import ncsa.d2k.core.modules.*;
import Jama.Matrix;

public class Write2DMatrix
    extends ComputeModule {

  public String getModuleName() {
    return "Write2DMatrix";
  }

  public String getModuleInfo() {
    return "This module computes X+1 for a 2d matrix.  ";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "Matrix";
      case 1:
        return "FileName";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "Matrix";
      case 1:
        return "FileName";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String[] getInputTypes() {
    String[] types = {
        "[[D",
        "java.lang.String",
    };
    return types;
  }

  public String getOutputName(int i) {
    switch (i) {
      default:
        return "Error!  No such output.  ";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      default:
        return "Error!  No such output.  ";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {
     };
    return types;
  }

  public void doit() throws Exception
  {

    double[][] X = (double[][])this.pullInput(0);
    String FileName = (String)this.pullInput(1);

    int NumRows = X.length;
    int NumCols = X[0].length;

    //RandomAccessFile file = new RandomAccessFile(FileName, "rw");
    //File file = new File(FileName);

    //file.writeInt(2);
    //file.writeInt(NumRows);
    //file.writeInt(NumCols);
    //byte [] bytes = {1,2,3,4,5,6,7,8};

    try {
      FileOutputStream file = new FileOutputStream(FileName + ".bin");
      ObjectOutputStream out = new ObjectOutputStream(file);
      out.writeInt(2);
      out.writeInt(NumRows);
      out.writeInt(NumCols);
      out.flush();
      out.close();
    }
    catch (java.io.IOException IOE) {
      System.out.println("IOException");
    }


    int BufferSize = 1000000;
    double [] DoubleBuffer = new double[BufferSize];

    int i = 0;
    int BlockIndex = 0;
    for (int RowIndex = 0; RowIndex < NumRows; RowIndex++) {
      for (int ColIndex = 0; ColIndex < NumCols; ColIndex++) {

        DoubleBuffer[i++] = X[RowIndex][ColIndex];

        if (i == BufferSize) {
          try {
            FileOutputStream file = new FileOutputStream(FileName + BlockIndex + ".bin");
            ObjectOutputStream out = new ObjectOutputStream(file);
            out.writeObject(DoubleBuffer);
            out.flush();
            out.close();
            i = 0;
            BlockIndex++;
          }
          catch (java.io.IOException IOE) {
            System.out.println("IOException");
          }
        }

      }
    }

    // write remaining block
    if (i != 0) {
      double[] DoubleBuffer2 = new double[i];
      System.arraycopy(DoubleBuffer, 0, DoubleBuffer2, 0, i);
      try {
        FileOutputStream file = new FileOutputStream(FileName + BlockIndex + ".bin");
        ObjectOutputStream out = new ObjectOutputStream(file);
        out.writeObject(DoubleBuffer2);
        out.flush();
        out.close();
      }
      catch (java.io.IOException IOE) {
        System.out.println("IOException");
      }
    }

  }

}
