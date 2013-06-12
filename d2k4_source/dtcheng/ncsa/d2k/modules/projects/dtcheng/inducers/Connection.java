package ncsa.d2k.modules.projects.dtcheng.inducers;

import ncsa.d2k.modules.projects.dtcheng.*;

public class Connection
  {
  Neuron  inputNeuron;
  Neuron  outputNeuron;
  double  weight;
  double  weightChange;
  double  weightAccumulation;
  boolean flag; /* 1 if weight is constant, 0 otherwise */
  }
