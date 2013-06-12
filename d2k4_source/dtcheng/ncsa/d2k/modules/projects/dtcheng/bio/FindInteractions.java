package ncsa.d2k.modules.projects.dtcheng.bio;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.core.modules.*;
import java.util.*;
import java.io.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.projects.dtcheng.io.*;
import ncsa.d2k.modules.projects.dtcheng.*;
import ncsa.d2k.modules.projects.dtcheng.primitive.Utility;

public class FindInteractions
    extends ComputeModule {

  int numProperties = 8;

  public PropertyDescription[] getPropertiesDescriptions() {

    PropertyDescription[] pds = new PropertyDescription[numProperties];

    int i = 0;

    pds[i++] = new PropertyDescription(
        "experimentalSystemsCSVList",
        "Experimental Systems",
        "A comma delimited list of experimental systems to use");

    pds[i++] = new PropertyDescription(
        "seedGeneName",
        "Seed Gene Name",
        "Name of the seed gene");

    pds[i++] = new PropertyDescription(
        "expertListGeneNameCSVList",
        "Expert List Gene Names",
        "A comma delimited list of gene names that in the expert list");

    pds[i++] = new PropertyDescription(
        "exclusionListGeneNameCSVList",
        "Exclusion List Gene Names",
        "A comma delimited list of gene names that may not be used to expand pathways");

    pds[i++] = new PropertyDescription(
        "minStrength",
        "Minimum Interaction Strength",
        "Number of different experiment systems necessary for path inclusion");

    pds[i++] = new PropertyDescription(
        "numRounds",
        "Number of Rounds",
        "Number of rounds of graph expansion to perform");

    pds[i++] = new PropertyDescription(
        "maxNumPaths",
        "Maximum Number of Search Paths to Generate",
        "Number of paths that can simultaneously be explored");

    pds[i++] = new PropertyDescription(
        "printExampleValues",
        "Print Example Values",
        "Report the feature values for each example in the example table");
/*
    pds[i++] = new PropertyDescription(
        "visualizationDataPath",
        "Data For Visualization Path",
        "Path of file that provides the visualization");
*/
    return pds;
  }

  private String ExperimentalSystemsCSVList = "all";
  public void setExperimentalSystemsCSVList(String value) {
    this.ExperimentalSystemsCSVList = value;
  }

  public String getExperimentalSystemsCSVList() {
    return this.ExperimentalSystemsCSVList;
  }

  private String SeedGeneName = "YKU70";
  public void setSeedGeneName(String value) {
    this.SeedGeneName = value;
  }

  public String getSeedGeneName() {
    return this.SeedGeneName;
  }

  private String ExpertListGeneNameCSVList = "RAP1,NPL3,GBP2";
  public void setExpertListGeneNameCSVList(String value) {
    this.ExpertListGeneNameCSVList = value;
  }

  public String getExpertListGeneNameCSVList() {
    return this.ExpertListGeneNameCSVList;
  }

  private String ExclusionListGeneNameCSVList = "";
  public void setExclusionListGeneNameCSVList(String value) {
    this.ExclusionListGeneNameCSVList = value;
  }

  public String getExclusionListGeneNameCSVList() {
    return this.ExclusionListGeneNameCSVList;
  }

  private int MinStrength = 2;
  public void setMinStrength(int value) {
    this.MinStrength = value;
  }

  public int getMinStrength() {
    return this.MinStrength;
  }

  private int NumRounds = 11;
  public void setNumRounds(int value) {
    this.NumRounds = value;
  }

  public int getNumRounds() {
    return this.NumRounds;
  }

  private int MaxNumPaths = 1000000;
  public void setMaxNumPaths(int value) {
    this.MaxNumPaths = value;
  }

  public int getMaxNumPaths() {
    return this.MaxNumPaths;
  }

  private boolean PrintExampleValues = true;
  public void setPrintExampleValues(boolean value) {
    this.PrintExampleValues = value;
  }

  public boolean getPrintExampleValues() {
    return this.PrintExampleValues;
  }


  private String VisualizationDataPath = "C:/data/Rivier/InteractionToGraph.csv";
  public void setVisualizationDataPath(String value) {
    this.VisualizationDataPath = value;
  }

  public String getVisualizationDataPath() {
    return this.VisualizationDataPath;
  }



  public String getModuleName() {
    return "Find Interactions";
  }

  public String getModuleInfo() {
    return "This module finds gene interactions through network analysis.  " +
        "AP = Affinity Precipitation; " +
        "TH = Two Hybrid; " +
        "AC = Affinity Chromatography; " +
        "SL = Synthetic Lethality; " +
        "SR = Synthetic Rescue; " +
        "DL = Dosage Lethality; " +
        "PC = Purified Complex; " +
        "BA = Biochemical Assay; " +
        "RC = Reconstituted Complex; " +
        "RL = Rivier Lab; " +
        "SS = Synthetic Sick; ";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "Print Writer";
      case 1:
        return "Interaction Example Table";
      case 2:
        return "interaction db systematic or common gene names";
      case 3:
        return "interaction db experimental system names";
      case 4:
        return "Master Example Table";
      case 5:
        return "master db systematic gene names";
      case 6:
        return "master db common locus names";
    }
    return "";
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "A print writer used to route the results";
      case 1:
        return "An example table with inputs being interacting genes indicies, and output being the interaction index";
      case 2:
        return "An array of strings relating integer indices to interaction db systematic or common gene names";
      case 3:
        return "An array of strings relating integer indices to interaction db experimental system names";
      case 4:
        return "An example table with inputs being systematic genes name indicies, and output being the common locus name index";
      case 5:
        return "An array of strings relating integer indices to master db systematic gene names";
      case 6:
        return "An array of strings relating integer indices to master db common locus names";
    }
    return "";
  }

  public String[] getInputTypes() {
    String[] in = {
        "java.io.PrintWriter",
        "ncsa.d2k.modules.core.datatype.table.ExampleTable",
        "[S",
        "[S",
        "ncsa.d2k.modules.core.datatype.table.ExampleTable",
        "[S",
        "[S"};

    return in;
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "InteractionTable";
    }
    return "";
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "This table contains interactions between all nodes (genes) in all pathways discovered.";
    }
    return "";
  }

  public String[] getOutputTypes() {
    String[] out = {
        "ncsa.d2k.modules.core.datatype.table.MutableTable"
    };
    return out;
  }

  protected Table createStringTable(String[][] stringTableData) {

    int numRows = stringTableData.length;
    int numColumns = stringTableData[0].length;

    Column[] columns = new Column[numColumns];
    for (int CollumnIndex = 0; CollumnIndex < columns.length; CollumnIndex++) {
      int type = ColumnTypes.STRING;
      columns[CollumnIndex] = ColumnUtilities.createColumn(type, numRows);
      String label = "col" + (CollumnIndex + 1);
      if (label != null)
        columns[CollumnIndex].setLabel(label);
    }

    MutableTableImpl ti = new MutableTableImpl(columns);

    for (int rowIndex = 0; rowIndex < numRows; rowIndex++) {
      for (int colIndex = 0; colIndex < numColumns; colIndex++) {
        ti.setString(stringTableData[rowIndex][colIndex], rowIndex, colIndex);
      }
    }

    return ti;
  }

  public void doit() throws Exception {

    PrintWriter out = (PrintWriter)this.pullInput(0);
    ExampleTable interactionDBExampleTable = (ExampleTable)this.pullInput(1);
    int numRowsInInteractionDB = interactionDBExampleTable.getNumRows();

    System.out.println("starting find interactions" + "<BR>");
    {
      int numInputs = interactionDBExampleTable.getNumInputs(0);
      int numOutputs = interactionDBExampleTable.getNumOutputs(0);

      if ((numInputs != 2) || (numOutputs != 1)) {
        System.out.println("wrong table dimension (numInputs != 2) || (numOutputs != 1)" + "<BR>");
        throw new Exception();
      }
    }

    String[] interactionDBSystematicOrCommonGeneNames = (String[])this.pullInput(2);
    int numSystematicGeneNamesInInteractionDB = interactionDBSystematicOrCommonGeneNames.length;

    String[] interactionDBExperimentalSystemNames = (String[])this.pullInput(3);
    int numExperimentalSystemNamesInInteractionDB = interactionDBExperimentalSystemNames.length;

    ExampleTable masterDBExampleTable = (ExampleTable)this.pullInput(4);
    int numMasterDBRows = masterDBExampleTable.getNumRows();

    String[] masterDBSystematicGeneNames = (String[])this.pullInput(5);
    int numSystematicGenesNamesInMasterDB = masterDBSystematicGeneNames.length;

    String[] masterDBCommonLocusNames = (String[])this.pullInput(6);
    int numCommonLocusNamesInMasterDB = masterDBCommonLocusNames.length;


    //System.out.println("numSystematicGenesNamesInMasterDB = " + numSystematicGenesNamesInMasterDB);
    //System.out.println("numSystematicGenesNamesInMasterDB = " + numSystematicGenesNamesInMasterDB);
    //System.out.println("numMasterDBRows = " + numMasterDBRows);

    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    // rename SystmaticGeneNames in gene interaction DB to their common locus names from the mast gene db  //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    Hashtable masterDBCommonLocusNamesHashTable = new Hashtable();
    int masterDBSystematicGeneNameCount = 0;

    for (int j = 0; j < numMasterDBRows; j++) {

      int index = masterDBExampleTable.getOutputInt(j, 0);
      //System.System.out.println("index = " + index);

      String masterDBCommonLocusName = masterDBCommonLocusNames[j];
      String masterDBSystematicGeneName = masterDBSystematicGeneNames[index];

      if (!masterDBCommonLocusNamesHashTable.containsKey(masterDBSystematicGeneName)) {
        masterDBCommonLocusNamesHashTable.put(masterDBSystematicGeneName, masterDBCommonLocusName);
        masterDBSystematicGeneNameCount++;
      }

    }

    for (int i = 0; i < numSystematicGeneNamesInInteractionDB; i++) {
      String interactionDBSystematicGeneName = interactionDBSystematicOrCommonGeneNames[i];
      if (masterDBCommonLocusNamesHashTable.containsKey(interactionDBSystematicGeneName)) {
        interactionDBSystematicOrCommonGeneNames[i] = (String) masterDBCommonLocusNamesHashTable.get(interactionDBSystematicGeneName);
      }

    }

    ////////////////////////////////////////
    //  mark experimental systems to use  //
    ////////////////////////////////////////

    boolean[] UseExperimentalSystem = new boolean[numExperimentalSystemNamesInInteractionDB];

    if (ExperimentalSystemsCSVList.equalsIgnoreCase("all")) {
      for (int i = 0; i < numExperimentalSystemNamesInInteractionDB; i++) {
        UseExperimentalSystem[i] = true;
      }
    }
    else {
      String[] ExperimentalSystems = Utility.parseCSVList(ExperimentalSystemsCSVList);
      int NumExperimentalSystemsInCSVList = ExperimentalSystems.length;

      // mark genes on expert list
      for (int i = 0; i < NumExperimentalSystemsInCSVList; i++) {

        boolean found = false;
        int index = -1;
        for (int j = 0; j < numExperimentalSystemNamesInInteractionDB; j++) {
          if (ExperimentalSystems[i].equalsIgnoreCase(interactionDBExperimentalSystemNames[j])) {
            found = true;
            index = j;
            break;
          }
        }

        if (found) {
          UseExperimentalSystem[index] = true;
        }
        else {
          System.out.println("Error!  Experimental System (" + ExperimentalSystems[i] + ") not found" + "<BR>");
        }
      }
    }
    System.out.println("Experimental Systems Used:<BR>");
    for (int i = 0; i < numExperimentalSystemNamesInInteractionDB; i++) {
      if (UseExperimentalSystem[i]) {
        System.out.println(interactionDBExperimentalSystemNames[i] + "<BR>");
      }
    }

    /////////////////////////////
    //  find seed gene index  //
    /////////////////////////////

    int seedGeneIndex = Utility.stringToIndex(interactionDBSystematicOrCommonGeneNames, SeedGeneName);

    if (seedGeneIndex == -1) {
      System.out.println("Error!  Seed gene (" + SeedGeneName + ") not found" + "<BR>");
      out.flush();
      throw new Exception();
    }

    ////////////////////////////////
    //  find expert gene indices  //
    ////////////////////////////////

    String[] expertGeneNames = Utility.parseCSVList(ExpertListGeneNameCSVList);
    int numExpertListGenes = expertGeneNames.length;

    for (int i = 0; i < numExpertListGenes; i++) {
      System.out.println("expertGeneNames[" + i + "] = " + expertGeneNames[i] + "<BR>");
    }

    int[] expertListGeneIndices = new int[numExpertListGenes];
    int expertListGeneIndex = 0;

    for (int e = 0; e < numExpertListGenes; e++) {

      int index = Utility.stringToIndex(interactionDBSystematicOrCommonGeneNames, expertGeneNames[e]);

      if (index == -1) {
        System.out.println("Error!  Expert gene (" + expertGeneNames[e] + ") not found" + "<BR>");
      }
      else {
        if (index == seedGeneIndex) {
          System.out.println("Error!  Expert gene (" + expertGeneNames[e] + ") is seed gene -- gene removed from expert list" + "<BR>");
        }
        else {
          expertListGeneIndices[expertListGeneIndex] = index;
          expertListGeneIndex++;
        }
      }
    }

    numExpertListGenes = expertListGeneIndex;
    if (true) {
      for (int i = 0; i < numExpertListGenes; i++) {
        System.out.println("expertListGeneIndices[" + i + "] = " + expertListGeneIndices[i] + "<BR>");
      }
    }

    // mark genes on expert list
    boolean[] inExpertList = new boolean[numSystematicGeneNamesInInteractionDB];
    for (int i = 0; i < numExpertListGenes; i++) {
      inExpertList[expertListGeneIndices[i]] = true;
    }

    ///////////////////////////////////
    //  find exclusion gene indices  //
    ///////////////////////////////////

    String[] exclusionGeneNames = Utility.parseCSVList(ExclusionListGeneNameCSVList);
    int numExclusionListGenes = exclusionGeneNames.length;

    for (int i = 0; i < numExclusionListGenes; i++) {
      System.out.println("exclusionGeneNames[" + i + "] = " + exclusionGeneNames[i] + "<BR>");
    }

    int[] exclusionListGeneIndices = new int[numExclusionListGenes];
    int exclusionListGeneIndex = 0;

    for (int e = 0; e < numExclusionListGenes; e++) {

      int index = Utility.stringToIndex(interactionDBSystematicOrCommonGeneNames, exclusionGeneNames[e]);

      if (index == -1) {
        System.out.println("Error!  Exclusion gene (" + exclusionGeneNames[e] + ") not found" + "<BR>");
      }
      else {
        exclusionListGeneIndices[exclusionListGeneIndex] = index;
        exclusionListGeneIndex++;
      }
    }

    numExclusionListGenes = exclusionListGeneIndex;
    if (true) {
      for (int i = 0; i < numExclusionListGenes; i++) {
        System.out.println("exclusionListGeneIndices[" + i + "] = " + exclusionListGeneIndices[i] + "<BR>");
      }
    }

    boolean[] inExclusionList = new boolean[numSystematicGeneNamesInInteractionDB];
    for (int i = 0; i < numExclusionListGenes; i++) {
      inExclusionList[exclusionListGeneIndices[i]] = true;
    }

    //////////////////////////////////
    //  report summary information  //
    //////////////////////////////////

    System.out.println("" + "<BR>");
    System.out.println("Seed Gene               = " + SeedGeneName + "<BR>");
    System.out.println("Num Genes                = " + numSystematicGeneNamesInInteractionDB + "<BR>");
    System.out.println("Num Experimental Systems = " + numExperimentalSystemNamesInInteractionDB + "<BR>");
    System.out.println("Num Interactions         = " + numRowsInInteractionDB + "<BR>");
    System.out.println("" + "<BR>");

    /////////////////////////////////////////////////////////////////////////////////////////////////
    //  count number of different types (experimental systems) of interactions for each gene pair  //
    /////////////////////////////////////////////////////////////////////////////////////////////////

    byte[][] interactionCounts = new byte[numSystematicGeneNamesInInteractionDB][numSystematicGeneNamesInInteractionDB];
    int numInteractionTypes = numExperimentalSystemNamesInInteractionDB + 1;

    if (numInteractionTypes > 30) {
      System.out.println("numInteractionTypes > 30" + "<BR>");
      throw new Exception();
    }
    int[][] interactionTypes = new int[numSystematicGeneNamesInInteractionDB][numSystematicGeneNamesInInteractionDB];

    // count experiment system type interactions //
    for (int i = 0; i < numRowsInInteractionDB; i++) {

      int gene1 = interactionDBExampleTable.getInputInt(i, 0);
      int gene2 = interactionDBExampleTable.getInputInt(i, 1);
      int experimentalSystemType = interactionDBExampleTable.getOutputInt(i, 0);

      if (UseExperimentalSystem[experimentalSystemType] &&
          ((interactionTypes[gene1][gene2] & (1 << experimentalSystemType)) == 0)) {
        interactionCounts[gene1][gene2]++;
        interactionCounts[gene2][gene1]++;

        interactionTypes[gene1][gene2] = interactionTypes[gene1][gene2] | (1 << experimentalSystemType);
        interactionTypes[gene2][gene1] = interactionTypes[gene2][gene1] | (1 << experimentalSystemType);
      }
    }

    // count expert list type interactions //
    if (false)
      for (int i = 0; i < numRowsInInteractionDB; i++) {

        int gene1 = interactionDBExampleTable.getInputInt(i, 0);
        int gene2 = interactionDBExampleTable.getInputInt(i, 1);

        boolean expertListInteraction = false;
        for (int e = 0; e < numExpertListGenes; e++) {

          if (gene1 == expertListGeneIndices[e] || gene2 == expertListGeneIndices[e]) {
            interactionCounts[gene1][gene2]++;
            interactionCounts[gene2][gene1]++;
            interactionTypes[gene2][gene1] = interactionTypes[gene2][gene1] | 1 << numExperimentalSystemNamesInInteractionDB; ;
            interactionTypes[gene1][gene2] = interactionTypes[gene1][gene2] | 1 << numExperimentalSystemNamesInInteractionDB; ;
          }
        }

      }

    int maxPathLength = NumRounds + 1;

    ///////////////////////////////
    //  initialize stable paths  //
    ///////////////////////////////

    int maxNumStablePaths = MaxNumPaths;

    int numStablePaths = 0;
    int[] stablePathLengths = new int[maxNumStablePaths];
    int[][] stablePathGenes = new int[maxNumStablePaths][maxPathLength];

    ///////////////////////////////
    //  initialize search paths  //
    ///////////////////////////////

    int maxNumSearchPaths = MaxNumPaths;

    int numSearchPaths = 0;
    int[] searchPathLengths = new int[maxNumSearchPaths];
    int[][] searchPathGenes = new int[maxNumSearchPaths][maxPathLength];

    searchPathLengths[numSearchPaths] = 1;
    searchPathGenes[numSearchPaths][0] = seedGeneIndex;
    numSearchPaths++;

    int newNumSearchPaths = 0;
    int[] newSearchPathLengths = new int[maxNumSearchPaths];
    int[][] newSearchPathGenes = new int[maxNumSearchPaths][maxPathLength];

    /////////////////////////////////////////////////////////////////////////////
    //  round by round, add to the set of related genes (i.e., the seed set)  //
    /////////////////////////////////////////////////////////////////////////////

    for (int r = 0; r < NumRounds; r++) {

      // report round summary information //

      System.out.println("" + "<BR>");
      System.out.println("" + "<BR>");
      System.out.println("Round #" + (r + 1) + "<BR>");
      System.out.println("numSearchPaths = " + numSearchPaths + "<BR>");
      System.out.println("" + "<BR>");

      if (false) {
        System.out.println("Active Search Paths:" + "<BR>");
        for (int pathIndex = 0; pathIndex < numSearchPaths; pathIndex++) {
          System.out.println("SearchPath #" + (pathIndex + 1) + "<BR>");
          for (int geneIndex = 0; geneIndex < searchPathLengths[pathIndex]; geneIndex++) {
            System.out.println("  Gene: " + interactionDBSystematicOrCommonGeneNames[searchPathGenes[pathIndex][geneIndex]] + "<BR>");
          }
        }
      }

      newNumSearchPaths = 0;

      // identify strong (>= minIntractionStrength) different interaction types) //

      int oldNumPaths = numSearchPaths;
      for (int q = 0; q < oldNumPaths; q++) {

        int endOfPathGene = searchPathGenes[q][searchPathLengths[q] - 1];

        // end path if it ends in expert list
        if (inExpertList[endOfPathGene]) {
          System.out.println("Path " + (q + 1) + " ends in expert list<BR>");
          // add to stable path list
          for (int g = 0; g < searchPathLengths[q]; g++) {
            stablePathGenes[numStablePaths][g] = searchPathGenes[q][g];
          }
          // add last node
          stablePathLengths[numStablePaths] = searchPathLengths[q];
          numStablePaths++;
          continue;
        }

        // end path if it ends in seed gene
        if ((endOfPathGene == seedGeneIndex) && (r > 0)) {
          System.out.println("Path " + (q + 1) + " ends in seed gene<BR>");
          // add to stable path list
          for (int g = 0; g < searchPathLengths[q]; g++) {
            stablePathGenes[numStablePaths][g] = searchPathGenes[q][g];
          }
          // add last node
          stablePathLengths[numStablePaths] = searchPathLengths[q];
          numStablePaths++;
          continue;
        }

        for (int currentGeneIndex = 0; currentGeneIndex < numSystematicGeneNamesInInteractionDB; currentGeneIndex++) {

          if ((interactionCounts[currentGeneIndex][endOfPathGene] >= MinStrength) && (!inExclusionList[currentGeneIndex])) {

            // check to see of gene is alread on path
            boolean geneAlreadyInPath = false;
            for (int g = 0; g < searchPathLengths[q]; g++) {
              if (currentGeneIndex == searchPathGenes[q][g]) {
                geneAlreadyInPath = true;
                break;
              }
            }

            if (!geneAlreadyInPath) {

              if (newNumSearchPaths == maxNumSearchPaths) {
                System.out.println("Error maxNumSearchPaths (" + maxNumSearchPaths + ") exceeded.  <BR>" +
                            "Increase value and rerun if possible.  <BR>");
                return;
              }

              // generate new pathway to explore
              for (int g = 0; g < searchPathLengths[q]; g++) {
                newSearchPathGenes[newNumSearchPaths][g] = searchPathGenes[q][g];
              }
              // add last node
              newSearchPathGenes[newNumSearchPaths][searchPathLengths[q]] = currentGeneIndex;
              newSearchPathLengths[newNumSearchPaths] = searchPathLengths[q] + 1;
              newNumSearchPaths++;
            }

          }

        }
      }

      numSearchPaths = newNumSearchPaths;
      // swap memory
      int[] temp1;
      int[][] temp2;
      temp1 = searchPathLengths;
      searchPathLengths = newSearchPathLengths;
      newSearchPathLengths = temp1;
      temp2 = searchPathGenes;
      searchPathGenes = newSearchPathGenes;
      newSearchPathGenes = temp2;
    }

    System.out.println("Stable Paths:" + "<BR>");
    for (int pathIndex = 0; pathIndex < numStablePaths; pathIndex++) {
      System.out.println("<BR>");
      System.out.println("Path #" + (pathIndex + 1) + "<BR>");
      for (int positionIndex = 0; positionIndex < stablePathLengths[pathIndex]; positionIndex++) {
        System.out.println("  Gene #" + (positionIndex + 1) + ": " + interactionDBSystematicOrCommonGeneNames[stablePathGenes[pathIndex][positionIndex]] + "<BR>");

        // report interaction if neccessary
        if (positionIndex > 0) {
          int gene1 = stablePathGenes[pathIndex][positionIndex - 1];
          int gene2 = stablePathGenes[pathIndex][positionIndex];

          System.out.println("found >= " + MinStrength + " interactions showing interaction between  " +
                      interactionDBSystematicOrCommonGeneNames[gene1] + " and " +
                      interactionDBSystematicOrCommonGeneNames[gene2] + "<BR>");
          for (int j = 0; j < numExperimentalSystemNamesInInteractionDB; j++) {
            if ((interactionTypes[gene1][gene2] & (1 << j)) > 0) {
              System.out.println("  Interaction with Experimental System  " + interactionDBExperimentalSystemNames[j] + "<BR>");
            }
          }
        }
      }

    }

    System.out.println();
    System.out.println();
    System.out.println();
    System.out.println();
    System.out.println();
    System.out.println();
    System.out.println();
    System.out.println();
    System.out.println();
    System.out.println();
    System.out.println();
    System.out.println();

    /*
    System.out.println("Creating File for Path Visualization:");

    FlatFile rio = new FlatFile(VisualizationDataPath, "w", 1000000, true);
    rio.println("gene1,gene2,numInteractions");
    rio.println("String,String,double");
    */

    int numRows = 0;
    for (int pathIndex = 0; pathIndex < numStablePaths; pathIndex++) {
      for (int positionIndex = 0; positionIndex < stablePathLengths[pathIndex] - 1; positionIndex++) {
        numRows++;
      }
    }

    String[][] tableData = new String[numRows][3];

    int rowIndex = 0;

    for (int pathIndex = 0; pathIndex < numStablePaths; pathIndex++) {
      for (int positionIndex = 0; positionIndex < stablePathLengths[pathIndex] - 1; positionIndex++) {

        int numInteractions = 0;
        int gene1 = stablePathGenes[pathIndex][positionIndex];
        int gene2 = stablePathGenes[pathIndex][positionIndex + 1];

        for (int j = 0; j < numExperimentalSystemNamesInInteractionDB; j++) {
          if ((interactionTypes[gene1][gene2] & (1 << j)) > 0) {
            numInteractions++;
          }
        }

        tableData[rowIndex][0] = interactionDBSystematicOrCommonGeneNames[gene1];
        tableData[rowIndex][1] = interactionDBSystematicOrCommonGeneNames[gene2];
        tableData[rowIndex][2] = String.valueOf(numInteractions);
        rowIndex++;
      }
    }
    //rio.close();

    out.flush();

    Table t = createStringTable(tableData);

    this.pushOutput(t, 0);
  }
}
