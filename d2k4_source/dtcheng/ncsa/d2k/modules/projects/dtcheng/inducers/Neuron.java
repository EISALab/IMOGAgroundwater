package ncsa.d2k.modules.projects.dtcheng.inducers;



public class Neuron
  {
  int        layer;
  int        serial;
  Connection inputConnections[];
  int        numInputConnections;
  Connection outputConnections[];
  int        numOutputConnections;
  double     bias;
  double     biasChange;
  double     biasAccumulation;
  boolean    flag;         /* 1 if bias is constant, 0 otherwise */
  double     activation;
  double     delta;
  }

