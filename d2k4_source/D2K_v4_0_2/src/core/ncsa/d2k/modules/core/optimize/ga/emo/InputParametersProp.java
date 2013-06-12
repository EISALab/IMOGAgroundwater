package ncsa.d2k.modules.core.optimize.ga.emo;

import ncsa.d2k.core.modules.*;


import ncsa.d2k.modules.core.optimize.ga.crossover.*;
import ncsa.d2k.modules.core.optimize.ga.emo.crossover.*;
import ncsa.d2k.modules.core.optimize.ga.mutation.*;
import ncsa.d2k.modules.core.optimize.ga.emo.mutation.*;
import ncsa.d2k.modules.core.optimize.ga.selection.*;
import ncsa.d2k.modules.core.optimize.ga.emo.selection.*;

/**
 * Input all the parameters for EMO in a property editor instead of a UI module.
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class InputParametersProp extends DataPrepModule {

  public String[] getInputTypes() {
    return new String[] {"ncsa.d2k.modules.core.optimize.ga.emo.Parameters"};
  }

  public String[] getOutputTypes() {
    return new String[] {"ncsa.d2k.modules.core.optimize.ga.emo.Parameters"};
  }

  public String getInputInfo(int i) {
    return "";
  }

  public String getOutputInfo(int i) {
    return "";
  }

  public String getModuleInfo() {
    return "";
  }

  private int populationSize = 100;
  public void setPopulationSize(int ps) {
    populationSize = ps;
  }
  public int getPopulationSize() {
    return populationSize;
  }

  private int maxGen = 100;
  public void setMaxGen(int mg) {
    maxGen = mg;
  }
  public int getMaxGen() {
    return maxGen;
  }

  private int tournamentSize = 4;
  public void setTournamentSize(int ts) {
    tournamentSize = ts;
  }
  public int getTournamentSize() {
    return tournamentSize;
  }

  private double crossoverRate = 0.75;
  public void setCrossoverRate(double cr) {
    crossoverRate = cr;
  }
  public double getCrossoverRate() {
    return crossoverRate;
  }

  private double mutationRate = 0.25;
  public void setMutationRate(double mr) {
    mutationRate = mr;
  }
  public double getMutationRate() {
    return mutationRate;
  }

  private double genGap = 1;
  public void setGenGap(double gg) {
    genGap = gg;
  }
  public double getGenGap() {
    return genGap;
  }

  private boolean binaryIndividuals = true;
  public void setBinaryIndividuals(boolean b) {
    binaryIndividuals = b;
  }
  public boolean getBinaryIndividuals() {
    return binaryIndividuals;
  }

  private int crossoverMethod = 0;
  public void setCrossoverMethod(int cm) throws Exception {
    if(cm > CrossoverFactory.NUM_CROSSOVER || cm < 0)
      throw new Exception("Invalid crossover type");
    crossoverMethod = cm;
  }
  public int getCrossoverMethod() {
    return crossoverMethod;
  }

  private double simBinN = 2;
  public void setSimBinN(double n) {
    simBinN = n;
  }
  public double getSimBinN() {
    return simBinN;
  }

  private int mutationMethod = 0;
  public void setMutationMethod(int mm) throws Exception {
    if(mm > MutationFactory.NUM_MUTATION || mm < 0)
      throw new Exception("Invalid mutation type");
    mutationMethod = mm;
  }
  public int getMutationMethod() {
    return mutationMethod;
  }

  private double realMutN = 2;
  public void setRealMutN(double n) {
    realMutN = n;
  }
  public double getRealMutN() {
    return realMutN;
  }

  private double selPres = 2;
  public void setSelPres(double sp) {
    selPres = sp;
  }
  public double getSelPres() {
    return selPres;
  }

  private int selectionMethod = 0;
  public void setSelectionMethod(int sm) throws Exception {
    if(sm > SelectionFactory.NUM_SELECTION || sm < 0)
      throw new Exception("Invalid selection method");
    selectionMethod = sm;
  }
  public int getSelectionMethod() {
    return selectionMethod;
  }

  public PropertyDescription[] getPropertiesDescriptions() {

        PropertyDescription[] pds = new PropertyDescription[13];

        pds[0] = new PropertyDescription(
                "populationSize",
                "Population Size",
                "The size of the first population");
        pds[1] = new PropertyDescription(
                "maxGen",
                "Maximum Generations",
                "The maximum number of generations");
        pds[2] = new PropertyDescription(
                "tournamentSize",
                "Tournament Size",
                "The tournament size");
        pds[3] = new PropertyDescription(
                "binaryIndividuals",
                "Create Binary Individuals",
                "Create binary individuals true/false");
        pds[4] = new PropertyDescription(
                "crossoverMethod",
                "Method of crossover",
                "cc");
        pds[5] = new PropertyDescription(
                "crossoverRate",
                "Crossover Rate",
                "rate");
        pds[6] = new PropertyDescription(
                "genGap",
                "Generation Gap",
                "gg");
        pds[7] = new PropertyDescription(
                "simBinN",
                "Simulated Binary Crossover - N",
                "value of N for simulated binary crossover");
        pds[8] = new PropertyDescription(
                "mutationMethod",
                "Method of mutation",
                "mm");
        pds[9] = new PropertyDescription(
                "mutationRate",
                "mutation rate",
                "rate");
        pds[10] = new PropertyDescription(
                "realMutN",
                "Real Mutation - N",
                "value of N for real mutation");
        pds[11] = new PropertyDescription(
                "selectionMethod",
                "Method of selection",
                "ss");
        pds[12] = new PropertyDescription(
                "selPres",
                "Rank Selection - selective pressure",
                "value of selective pressure for rank selection");
        return pds;
  }


  public void doit() throws Exception {
    Parameters params = (Parameters)pullInput(0);
    params.populationSize = getPopulationSize();
    params.maxGenerations = this.getMaxGen();

    Crossover[] cross = CrossoverFactory.createCrossoverOptions();
    Crossover crossTechnique = cross[this.getCrossoverMethod()];
    crossTechnique.setCrossoverRate(this.getCrossoverRate());
    crossTechnique.setGenerationGap(this.getGenGap());
    // if simulated binary crossover, set N
    Property[] props = ((EMOFunction)crossTechnique).getProperties();
    if(props != null && props.length > 0) {
      Property prop = props[0];
      prop.setValue(new Double(this.getSimBinN()));
    }
    params.crossover = crossTechnique;

    Mutation[] mut = MutationFactory.createMutationOptions();
    Mutation mutationTechnique = mut[getMutationMethod()];
    mutationTechnique.setMutationRate(this.getMutationRate());
    props = ((EMOFunction)mutationTechnique).getProperties();
    if(props != null && props.length > 0) {
      Property prop = props[0];
      prop.setValue(new Double(this.getRealMutN()));
    }
    params.mutation = mutationTechnique;

    Selection[] sel = SelectionFactory.createSelectionOptions();
    Selection selTechnique = sel[getSelectionMethod()];
    selTechnique.setTournamentSize(this.getTournamentSize());
    props = ((EMOFunction)selTechnique).getProperties();
    if(props != null && props.length > 0) {
      Property prop = props[0];
      prop.setValue(new Double(this.getSelPres()));
    }
    params.selection = selTechnique;
    params.createBinaryIndividuals = this.getBinaryIndividuals();

    pushOutput(params, 0);
  }
}