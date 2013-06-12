package ncsa.d2k.modules.projects.dtcheng;

import ncsa.d2k.modules.core.io.file.*;

import ncsa.d2k.core.modules.*;
import java.util.Hashtable;
import ncsa.d2k.modules.projects.dtcheng.io.*;
public class CreateIntegratedExamples extends ComputeModule
  {

  private double BICap  = 10000.00;
  public  void   setBICap (double value) {       this.BICap       = value;}
  public  double getBICap ()             {return this.BICap;}

  private boolean TraceCellCount   = true;
  public  void    setTraceCellCount (boolean value) {       this.TraceCellCount       = value;}
  public  boolean getTraceCellCount ()              {return this.TraceCellCount;}

  private boolean KeyIncludesPolicy   = true;
  public  void    setKeyIncludesPolicy (boolean value) {       this.KeyIncludesPolicy       = value;}
  public  boolean getKeyIncludesPolicy ()              {return this.KeyIncludesPolicy;}

  private boolean KeyIncludesYear   = true;
  public  void    setKeyIncludesYear (boolean value) {       this.KeyIncludesYear       = value;}
  public  boolean getKeyIncludesYear ()              {return this.KeyIncludesYear;}

  private boolean KeyIncludesCounty   = true;
  public  void    setKeyIncludesCounty (boolean value) {       this.KeyIncludesCounty       = value;}
  public  boolean getKeyIncludesCounty ()              {return this.KeyIncludesCounty;}

  private boolean KeyIncludesZip   = true;
  public  void    setKeyIncludesZip (boolean value) {       this.KeyIncludesZip       = value;}
  public  boolean getKeyIncludesZip ()              {return this.KeyIncludesZip;}

  private boolean KeyIncludesTract   = true;
  public  void    setKeyIncludesTract (boolean value) {       this.KeyIncludesTract       = value;}
  public  boolean getKeyIncludesTract ()              {return this.KeyIncludesTract;}

  private boolean KeyIncludesBlock   = true;
  public  void    setKeyIncludesBlock (boolean value) {       this.KeyIncludesBlock       = value;}
  public  boolean getKeyIncludesBlock ()              {return this.KeyIncludesBlock;}

  private boolean PrintExamples   = true;
  public  void    setPrintExamples (boolean value) {       this.PrintExamples       = value;}
  public  boolean getPrintExamples ()              {return this.PrintExamples;}

  public String getModuleInfo()
    {
		return "CreateIntegratedExamples";
	}
  public String getModuleName()
    {
		return "CreateIntegratedExamples";
	}

  public String[] getInputTypes()
    {
		String[] types = {"java.lang.Object"};
		return types;
	}

  public String[] getOutputTypes()
    {
		String[] types = {"java.lang.Object"};
		return types;
	}

  public String getInputInfo(int i)
    {
		switch (i) {
			case 0: return "Object";
			default: return "No such input";
		}
	}

  public String getInputName(int i)
    {
		switch(i) {
			case 0:
				return "Object";
			default: return "NO SUCH INPUT!";
		}
	}

  public String getOutputInfo(int i)
    {
		switch (i) {
			case 0: return "Object";
			default: return "No such output";
		}
	}

  public String getOutputName(int i)
    {
		switch(i) {
			case 0:
				return "Object";
			default: return "NO SUCH OUTPUT!";
		}
	}


  int policyOffset  = 0;
  int policyLength  = 9;
  int yearOffset    = 10;
  int yearLength    = 4;
  int mailzipOffset = 307;
  int mailzipLength = 5;
  int countyOffset  = 322;
  int countyLength  = 3;
  int tractOffset   = 326;
  int tractLength   = 6;
  int blockOffset   = 333;
  int blockLength   = 1;
  int msacodeOffset = 335;
  int msacodeLength = 4;



  // read feature values
  int bieexpOffset = 35;
  int bieexpLength = 5;
  int bipaidOffset = 41;
  int bipaidLength = 10;
  int bialaeOffset = 52;
  int bialaeLength = 10;
  int bicaseOffset = 63;
  int bicaseLength = 10;
  int bisuppOffset = 74;
  int bisuppLength = 10;

  int pdeexpOffset = 85;
  int pdeexpLength = 5;
  int pdpaidOffset = 91;
  int pdpaidLength = 8;
  int pdalaeOffset = 100;
  int pdalaeLength = 8;
  int pdcaseOffset = 109;
  int pdcaseLength = 8;
  int pdsuppOffset = 118;
  int pdsuppLength = 8;

  int sseexpOffset = 127;
  int sseexpLength = 5;
  int sspaidOffset = 133;
  int sspaidLength = 9;
  int ssalaeOffset = 143;
  int ssalaeLength = 9;
  int sscaseOffset = 153;
  int sscaseLength = 9;
  int sssuppOffset = 163;
  int sssuppLength = 9;

  int cceexpOffset = 173;
  int cceexpLength = 5;
  int ccpaidOffset = 179;
  int ccpaidLength = 9;
  int ccalaeOffset = 189;
  int ccalaeLength = 9;
  int cccaseOffset = 199;
  int cccaseLength = 9;
  int ccsuppOffset = 209;
  int ccsuppLength = 9;

  int ddeexpOffset = 219;
  int ddeexpLength = 5;
  int ddpaidOffset = 225;
  int ddpaidLength = 8;
  int ddalaeOffset = 234;
  int ddalaeLength = 8;
  int ddcaseOffset = 243;
  int ddcaseLength = 8;
  int ddsuppOffset = 252;
  int ddsuppLength = 8;

  int hheexpOffset = 261;
  int hheexpLength = 5;
  int hhpaidOffset = 267;
  int hhpaidLength = 9;
  int hhalaeOffset = 277;
  int hhalaeLength = 9;
  int hhcaseOffset = 287;
  int hhcaseLength = 9;
  int hhsuppOffset = 297;
  int hhsuppLength = 9;


  Hashtable HashTable = null;
  int       HashIndex = 0;
  int       LineIndex = 0;
  int       MaxNumCells = 1000000;
  double    [][][]  CellSums = null;
  int       [] CellCounts = null;
  int       [] CellCounties = null;
  int       [] CellZips = null;
  int       [] CellTracts = null;
  int       [] CellBlocks = null;
  String    [] Keys = null;

  public void beginExecution()
    {
    LineIndex  = 0;
    HashTable  = new Hashtable();
    HashIndex  = 0;
    CellSums   = new double[MaxNumCells][6][2];
    CellCounts = new int[MaxNumCells];

    if (KeyIncludesPolicy)
      {
      CellCounties = new int[MaxNumCells];
      CellZips = new int[MaxNumCells];
      CellTracts = new int[MaxNumCells];
      CellBlocks = new int[MaxNumCells];
      }

    Keys = new String[MaxNumCells];
    }

  public void doit()
    {
    byte [] byteString = (byte []) this.pullInput(0);

    LineIndex++;

    if (byteString != null)
      {

      // create key

      byte [] key = null;
      int keyLength = 0;
      if (KeyIncludesPolicy) keyLength += policyLength;
      if (KeyIncludesYear  ) keyLength += yearLength;
      if (KeyIncludesCounty) keyLength += countyLength;
      if (KeyIncludesZip   ) keyLength += mailzipLength;
      if (KeyIncludesTract ) keyLength += tractLength;
      if (KeyIncludesBlock ) keyLength += blockLength;

      key = new byte[keyLength];

      {
      int i = 0;

      if (KeyIncludesPolicy)
        {
        System.arraycopy((Object) byteString, policyOffset,   (Object) key, i, policyLength);
        i += policyLength;
        }
      if (KeyIncludesYear)
        {
        System.arraycopy((Object) byteString, yearOffset,   (Object) key, i, yearLength);
        i += yearLength;
        }
      if (KeyIncludesCounty)
        {
        System.arraycopy((Object) byteString, countyOffset, (Object) key, i, countyLength);
        i += countyLength;
        }
      if (KeyIncludesZip)
        {
        System.arraycopy((Object) byteString, mailzipOffset, (Object) key, i, mailzipLength);
        i += mailzipLength;
        }
      if (KeyIncludesTract)
        {
        System.arraycopy((Object) byteString, tractOffset,   (Object) key, i, tractLength);
        i += tractLength;
        }
      if (KeyIncludesBlock)
        {
        System.arraycopy((Object) byteString, blockOffset,   (Object) key, i, blockLength);
        i += blockLength;
        }
      }

      if (false)
        {
        System.out.println("key = " + new String(key, 0, key.length));
        }


      String keyString = new String(key, 0, key.length);

      int cellIndex = -1;
      if (!HashTable.containsKey(keyString))
        {
        cellIndex = HashIndex;

        Keys[HashIndex] = keyString;


        if (KeyIncludesPolicy)
          {
          CellCounties[HashIndex] = FlatFile.ByteStringToInt(byteString,  countyOffset,  countyOffset +  countyLength);
          CellZips    [HashIndex] = FlatFile.ByteStringToInt(byteString, mailzipOffset, mailzipOffset + mailzipLength);
          CellTracts  [HashIndex] = FlatFile.ByteStringToInt(byteString,   tractOffset,   tractOffset +   tractLength);
          CellBlocks  [HashIndex] = FlatFile.ByteStringToInt(byteString,   blockOffset,   blockOffset +   blockLength);
          }

         Integer index = new Integer(HashIndex);
        HashTable.put(keyString, index);
        HashIndex++;

        if (TraceCellCount)
          System.out.println("HashIndex = " + HashIndex + "  LineIndex = " + LineIndex);
        }
      else
        {
        Integer intObject = (Integer) HashTable.get(keyString);
        cellIndex = intObject.intValue();
        }


      FlatFile rio = null;

      double bieexp = rio.ByteStringToDouble(byteString, bieexpOffset, bieexpOffset + bieexpLength - 1);
      double bipaid = rio.ByteStringToDouble(byteString, bipaidOffset, bipaidOffset + bipaidLength - 1);
      double bialae = rio.ByteStringToDouble(byteString, bialaeOffset, bialaeOffset + bialaeLength - 1);
      double bicase = rio.ByteStringToDouble(byteString, bicaseOffset, bicaseOffset + bicaseLength - 1);
      double bisupp = rio.ByteStringToDouble(byteString, bisuppOffset, bisuppOffset + bisuppLength - 1);
      double biloss = bipaid + bialae + bicase + bisupp;
      if (biloss > BICap)
        {
        biloss = BICap;
        }

      double pdeexp = rio.ByteStringToDouble(byteString, pdeexpOffset, pdeexpOffset + pdeexpLength - 1);
      double pdpaid = rio.ByteStringToDouble(byteString, pdpaidOffset, pdpaidOffset + pdpaidLength - 1);
      double pdalae = rio.ByteStringToDouble(byteString, pdalaeOffset, pdalaeOffset + pdalaeLength - 1);
      double pdcase = rio.ByteStringToDouble(byteString, pdcaseOffset, pdcaseOffset + pdcaseLength - 1);
      double pdsupp = rio.ByteStringToDouble(byteString, pdsuppOffset, pdsuppOffset + pdsuppLength - 1);
      double pdloss = pdpaid + pdalae + pdcase + pdsupp;

      double sseexp = rio.ByteStringToDouble(byteString, sseexpOffset, sseexpOffset + sseexpLength - 1);
      double sspaid = rio.ByteStringToDouble(byteString, sspaidOffset, sspaidOffset + sspaidLength - 1);
      double ssalae = rio.ByteStringToDouble(byteString, ssalaeOffset, ssalaeOffset + ssalaeLength - 1);
      double sscase = rio.ByteStringToDouble(byteString, sscaseOffset, sscaseOffset + sscaseLength - 1);
      double sssupp = rio.ByteStringToDouble(byteString, sssuppOffset, sssuppOffset + sssuppLength - 1);
      double ssloss = sspaid + ssalae + sscase + sssupp;

      double cceexp = rio.ByteStringToDouble(byteString, cceexpOffset, cceexpOffset + cceexpLength - 1);
      double ccpaid = rio.ByteStringToDouble(byteString, ccpaidOffset, ccpaidOffset + ccpaidLength - 1);
      double ccalae = rio.ByteStringToDouble(byteString, ccalaeOffset, ccalaeOffset + ccalaeLength - 1);
      double cccase = rio.ByteStringToDouble(byteString, cccaseOffset, cccaseOffset + cccaseLength - 1);
      double ccsupp = rio.ByteStringToDouble(byteString, ccsuppOffset, ccsuppOffset + ccsuppLength - 1);
      double ccloss = ccpaid + ccalae + cccase + ccsupp;

      double ddeexp = rio.ByteStringToDouble(byteString, ddeexpOffset, ddeexpOffset + ddeexpLength - 1);
      double ddpaid = rio.ByteStringToDouble(byteString, ddpaidOffset, ddpaidOffset + ddpaidLength - 1);
      double ddalae = rio.ByteStringToDouble(byteString, ddalaeOffset, ddalaeOffset + ddalaeLength - 1);
      double ddcase = rio.ByteStringToDouble(byteString, ddcaseOffset, ddcaseOffset + ddcaseLength - 1);
      double ddsupp = rio.ByteStringToDouble(byteString, ddsuppOffset, ddsuppOffset + ddsuppLength - 1);
      double ddloss = ddpaid + ddalae + ddcase + ddsupp;

      double hheexp = rio.ByteStringToDouble(byteString, hheexpOffset, hheexpOffset + hheexpLength - 1);
      double hhpaid = rio.ByteStringToDouble(byteString, hhpaidOffset, hhpaidOffset + hhpaidLength - 1);
      double hhalae = rio.ByteStringToDouble(byteString, hhalaeOffset, hhalaeOffset + hhalaeLength - 1);
      double hhcase = rio.ByteStringToDouble(byteString, hhcaseOffset, hhcaseOffset + hhcaseLength - 1);
      double hhsupp = rio.ByteStringToDouble(byteString, hhsuppOffset, hhsuppOffset + hhsuppLength - 1);
      double hhloss = hhpaid + hhalae + hhcase + hhsupp;

      CellSums[cellIndex][0][0] += bieexp;
      CellSums[cellIndex][0][1] += biloss;
      CellSums[cellIndex][1][0] += pdeexp;
      CellSums[cellIndex][1][1] += pdloss;
      CellSums[cellIndex][2][0] += sseexp;
      CellSums[cellIndex][2][1] += ssloss;
      CellSums[cellIndex][3][0] += cceexp;
      CellSums[cellIndex][3][1] += ccloss;
      CellSums[cellIndex][4][0] += ddeexp;
      CellSums[cellIndex][4][1] += ddloss;
      CellSums[cellIndex][5][0] += hheexp;
      CellSums[cellIndex][5][1] += hhloss;

      CellCounts[cellIndex]++;

      this.pushOutput(byteString, 0);
      }
    else
      {


      ////////////////////////
      // print column names //
      ////////////////////////

      if (KeyIncludesPolicy)
        {
        System.out.print("policy,");
        }
      if (KeyIncludesYear)
        {
        System.out.print("year,");
        }
      if (KeyIncludesCounty)
        {
        System.out.print("county,");
        }
      if (KeyIncludesZip)
        {
        System.out.print("zip,");
        }
      if (KeyIncludesTract)
        {
        System.out.print("tract,");
        }
      if (KeyIncludesBlock)
        {
        System.out.print("block,");
        }
      System.out.print("record_count,");

      for (int c = 0; c < 6; c++)
        {

        switch (c)
          {
          case 0: System.out.print("bieexp,biloss,biratio,"); break;
          case 1: System.out.print("pdeexp,pdloss,pdratio,"); break;
          case 2: System.out.print("sseexp,ssloss,ssratio,"); break;
          case 3: System.out.print("cceexp,ccloss,ccratio,"); break;
          case 4: System.out.print("ddeexp,ddloss,ddratio,"); break;
          case 5: System.out.print("hheexp,hhloss,hhratio,"); break;
          }

        }

      System.out.print("exposure_avg,");
      System.out.print("loss_sum");

      if (KeyIncludesPolicy)
        {
        System.out.print(",county,zip,tract,block");
        }

      System.out.println();


      if (PrintExamples)
      {
        for (int h = 0; h < HashIndex; h++)
        {

          // year, tract, r1, r2, ... r6
          int i = 0;
          if (KeyIncludesPolicy)
          {
            System.out.print(new String(Keys[h].getBytes(), i, policyLength));
            System.out.print(",");
            i += policyLength;
          }
          if (KeyIncludesYear)
          {
            System.out.print(new String(Keys[h].getBytes(), i, yearLength));
            System.out.print(",");
            i += yearLength;
          }
          if (KeyIncludesCounty)
          {
            System.out.print(new String(Keys[h].getBytes(), i, countyLength));
            System.out.print(",");
            i += countyLength;
          }
          if (KeyIncludesZip)
          {
            System.out.print(new String(Keys[h].getBytes(), i, mailzipLength));
            System.out.print(",");
            i += mailzipLength;
          }
          if (KeyIncludesTract)
          {
            System.out.print(new String(Keys[h].getBytes(), i, tractLength));
            System.out.print(",");
            i += tractLength;
          }
          if (KeyIncludesBlock)
          {
            System.out.print(new String(Keys[h].getBytes(), i, blockLength));
            System.out.print(",");
            i += blockLength;
          }
          System.out.print(CellCounts[h]);

          for (int c = 0; c < 6; c++)
          {
            System.out.print("," + CellSums[h][c][0]);
            System.out.print("," + CellSums[h][c][1]);

            double ratio = CellSums[h][c][1] / CellSums[h][c][0];

            if (Double.isNaN(ratio))
              System.out.print("," + 0.0);
            else
              System.out.print("," + ratio);
          }

          double exposureSum = 0.0;
          double lossSum     = 0.0;
          for (int c = 0; c < 6; c++)
          {
            exposureSum += CellSums[h][c][0];
            lossSum     += CellSums[h][c][1];
          }

          double exposureAvg = exposureSum / 6.0;
          System.out.print("," + exposureAvg);
          System.out.print("," + lossSum);


          if (KeyIncludesPolicy)
          {
            System.out.print("," + CellCounties[h]);
            System.out.print("," + CellZips    [h]);
            System.out.print("," + CellTracts  [h]);
            System.out.print("," + CellBlocks  [h]);
          }

          System.out.println();
        }
      }
      }
    }
  }
