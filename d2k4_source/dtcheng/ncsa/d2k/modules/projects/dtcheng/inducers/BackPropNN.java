package ncsa.d2k.modules.projects.dtcheng.inducers;

import ncsa.d2k.modules.core.datatype.table.*;

import java.util.*;
import ncsa.d2k.modules.projects.dtcheng.*;
import ncsa.d2k.modules.projects.dtcheng.primitive.*;

// The sigmoid activation function.
public class BackPropNN {

  public int problem_index = 0;

  Random randomNumberGenerator;
  Clock clock = new Clock();

  Net net;

  int NumInputs;
  int NumOutputs;

  double sigmoid(double x) {
    return (1.0 / (1.0 + Math.exp( -x)));
  }

  // This function creates the standard fully-connected feed forward network. //

  public void createStandardNet(
      int numInputs,
      int numOutputs,
      int numHiddenLayers,
      int numHiddensPerLayers,
      double randLimit,
      int seed
      )

  {

    NumInputs = numInputs;
    NumOutputs = numOutputs;

    if (false) {
      System.out.println("randLimit         = " + randLimit);
      System.out.println("numHiddenLayers     = " + numHiddenLayers);
      System.out.println("numHiddensPerLayers = " + numHiddensPerLayers);
      System.out.println("seed               = " + seed);
    }

    Layer layers[];
    int i, j, k;

    if (seed != 0)
      randomNumberGenerator = new Random(seed);
    else
      randomNumberGenerator = new Random();

    net = new Net();
    layers = new Layer[numHiddenLayers + 2];
    net.layers = layers;
    net.numLayers = numHiddenLayers + 2;
    net.numNeurons = NumInputs + NumOutputs + numHiddenLayers * numHiddensPerLayers;
    net.numConnections = (NumInputs + NumOutputs + (numHiddenLayers - 1) * numHiddensPerLayers) * numHiddensPerLayers;

    // creating in layer //
    layers[0] = new Layer();
    layers[0].number = 0;
    layers[0].numNeurons = NumInputs;
    layers[0].neurons = new Neuron[NumInputs];
    for (j = 0; j < NumInputs; j++) {
      layers[0].neurons[j] = new Neuron();
      layers[0].neurons[j].layer = 0;
      layers[0].neurons[j].serial = j;
      layers[0].neurons[j].numInputConnections = 0;
      layers[0].neurons[j].numOutputConnections = numHiddensPerLayers;
      layers[0].neurons[j].bias = 0.0;
      layers[0].neurons[j].biasChange = 0.0;
      layers[0].neurons[j].biasAccumulation = 0.0;
      layers[0].neurons[j].flag = false;
    }

    // creating hidden layers //
    for (i = 0; i < numHiddenLayers; i++) {
      layers[i + 1] = new Layer();
      layers[i + 1].number = i + 1;
      layers[i + 1].numNeurons = numHiddensPerLayers;
      layers[i + 1].neurons = new Neuron[numHiddensPerLayers];
      for (j = 0; j < numHiddensPerLayers; j++) {
        layers[i + 1].neurons[j] = new Neuron();
        layers[i + 1].neurons[j].layer = i + 1;
        layers[i + 1].neurons[j].serial = j;

        if (i == 0)
          layers[i + 1].neurons[j].numInputConnections = NumInputs;
        else
          layers[i + 1].neurons[j].numInputConnections = numHiddensPerLayers;

        if (i == numHiddenLayers - 1)
          layers[i + 1].neurons[j].numOutputConnections = NumOutputs;
        else
          layers[i + 1].neurons[j].numOutputConnections = numHiddensPerLayers;

        layers[i + 1].neurons[j].bias = randomNumberGenerator.nextDouble() * 2.0 * randLimit - randLimit;
        layers[i + 1].neurons[j].biasChange = 0.0;
        layers[i + 1].neurons[j].biasAccumulation = 0.0;
        layers[i + 1].neurons[j].flag = false;
      }
    }

    // creating out layer //
    layers[numHiddenLayers + 1] = new Layer();
    layers[numHiddenLayers + 1].number = numHiddenLayers + 1;
    layers[numHiddenLayers + 1].numNeurons = NumOutputs;
    layers[numHiddenLayers + 1].neurons = new Neuron[NumOutputs];
    for (j = 0; j < NumOutputs; j++) {
      layers[numHiddenLayers + 1].neurons[j] = new Neuron();
      layers[numHiddenLayers + 1].neurons[j].layer = numHiddenLayers + 1;
      layers[numHiddenLayers + 1].neurons[j].serial = j;
      layers[numHiddenLayers + 1].neurons[j].numInputConnections = numHiddensPerLayers;
      layers[numHiddenLayers + 1].neurons[j].numOutputConnections = 0;
      layers[numHiddenLayers + 1].neurons[j].bias = randomNumberGenerator.nextDouble() * 2.0 * randLimit - randLimit;
      layers[numHiddenLayers + 1].neurons[j].biasChange = 0;
      layers[numHiddenLayers + 1].neurons[j].biasAccumulation = 0;
      layers[numHiddenLayers + 1].neurons[j].flag = false;
    }

    // creating out connections for the layers //
    for (i = 0; i < numHiddenLayers + 1; i++) {
      for (j = 0; j < layers[i].numNeurons; j++) {
        layers[i].neurons[j].outputConnections = new Connection[layers[i + 1].numNeurons];
        for (k = 0; k < layers[i + 1].numNeurons; k++) {
          layers[i].neurons[j].outputConnections[k] = new Connection();
          layers[i].neurons[j].outputConnections[k].inputNeuron = layers[i].neurons[j];
          layers[i].neurons[j].outputConnections[k].outputNeuron = layers[i + 1].neurons[k];
          layers[i].neurons[j].outputConnections[k].weight = randomNumberGenerator.nextDouble() * 2.0 * randLimit - randLimit;
          layers[i].neurons[j].outputConnections[k].weightChange = 0.0;
          layers[i].neurons[j].outputConnections[k].weightAccumulation = 0.0;
          layers[i].neurons[j].outputConnections[k].flag = false;
        }
      }
    }

    // creating in connections for the layers //
    for (i = 1; i < numHiddenLayers + 2; i++) {
      for (j = 0; j < layers[i].numNeurons; j++) {
        layers[i].neurons[j].inputConnections = new Connection[layers[i - 1].numNeurons];
        for (k = 0; k < layers[i - 1].numNeurons; k++) {
          layers[i].neurons[j].inputConnections[k] = layers[i - 1].neurons[k].outputConnections[j];
        }
      }
    }

  }

  void calculateActivation(Neuron neuron, double in_val) {
    int i;
    double neuronInput;
    Connection con, inputConnections[];

    neuronInput = 0.0;

    if (neuron.numInputConnections != 0) {
      inputConnections = neuron.inputConnections;
      for (i = 0; i < neuron.numInputConnections; i++) {
        con = inputConnections[i];
        neuronInput += con.weight * con.inputNeuron.activation;
      }
      neuronInput += neuron.bias;
      neuron.activation = sigmoid(neuronInput);
    }
    else
      neuron.activation = in_val;
  }

  // This function propagates in through a network.

  void propagate(double in[]) {
    int i, l;
    Neuron neurons[];
    Layer layer;

    neurons = net.layers[0].neurons;

    for (i = 0; i < NumInputs; i++)
      calculateActivation(neurons[i], in[i]);

    for (l = 1; l < net.numLayers; l++) {
      layer = net.layers[l];
      neurons = layer.neurons;

      for (i = 0; i < layer.numNeurons; i++)
        calculateActivation(neurons[i], 0.0);
    }

  }

  //  This function evaluates a network.  It propagates the in through the network and updates the out.

  void evaluateNet(double in[], double out[]) {
    int i, l;
    Neuron neurons[];
    Layer layer;

    layer = net.layers[0];
    neurons = layer.neurons;

    for (i = 0; i < layer.numNeurons; i++)
      calculateActivation(neurons[i], in[i]);

    for (l = 1; l < net.numLayers; l++) {
      layer = net.layers[l];
      neurons = layer.neurons;

      for (i = 0; i < layer.numNeurons; i++)
        calculateActivation(neurons[i], 0.0);
    }

    layer = net.layers[net.numLayers - 1];
    neurons = layer.neurons;

    for (i = 0; i < layer.numNeurons; i++)
      out[i] = neurons[i].activation;

  }

  // This function calculates the errror derivative for a neuron.

  void calculateDelta(Neuron neuron, double out_error) {
    int i;
    double error, activation;
    Connection con, cons[];

    error = 0.0;

    if (neuron.numOutputConnections != 0) {
      cons = neuron.outputConnections;
      for (i = 0; i < neuron.numOutputConnections; i++) {
        con = cons[i];
        error += con.weight * con.outputNeuron.delta;
      }
    }
    else
      error = out_error;

    activation = neuron.activation;

    neuron.delta = activation * (1.0 - activation) * error;
  }

  // This function calculates the bias accumulated so far in this epoch.

  void AccumulateBias(Neuron neuron) {
    if (!neuron.flag)
      neuron.biasAccumulation += neuron.delta;
  }

  // This function calculates the bias for a neuron.  Basically, the bias
  // accumulated so far is transferred to the bias slot.

  void CalculateBias(Neuron neuron, double learningRate, double momentum) {
    double db;

    db = 0.0;

    if (!neuron.flag) {
      db = learningRate * neuron.biasAccumulation + momentum * neuron.biasChange;
      neuron.biasAccumulation = 0.0;
      neuron.biasChange = db;
      neuron.bias += db;
    }
  }

  // This function calculates the weight accumulated so far in this epoch.

  void calculateWeightAccumulation(Connection con) {
    if (!con.flag)
      con.weightAccumulation += con.outputNeuron.delta * con.inputNeuron.activation;
  }

  // This function calculates the weight for a neuron.  Basically, the weight
  // accumulated so far is transferred to the weight slot.

  void calculateWeight(Connection con, double learningRate, double momentum) {
    double dw;

    dw = 0.0;

    if (!con.flag) {
      dw = learningRate * con.weightAccumulation + momentum * con.weightChange;
      con.weightAccumulation = 0.0;
      con.weightChange = dw;
      con.weight += dw;
    }
  }

  // This function calculates the error for the net over the ex set.

  double[] CachedInputs = new double[1];
  double[] CachedOutputs = new double[1];

  public double[] predictOutputs(ExampleTable examples, int e) {

    if (CachedInputs.length != examples.getNumInputFeatures()) {
      CachedInputs = new double[NumInputs];
    }
    double[] inputs = CachedInputs;

    for (int i = 0; i < NumInputs; i++) {
      inputs[i] = examples.getInputDouble(e, i);
    }

    Neuron outNeurons[];

    outNeurons = net.layers[net.numLayers - 1].neurons;

    propagate(inputs);

    if (CachedOutputs.length != examples.getNumOutputFeatures()) {
      CachedOutputs = new double[NumOutputs];
    }
    double[] outputs = CachedOutputs;
    for (int o = 0; o < NumOutputs; o++) {
      outputs[o] = outNeurons[o].activation;
    }

    return outputs;
  }

  double CalculateError(ExampleTable examples, boolean printPredictions) {
    int numExamples = examples.getNumRows();

    int e;
    double ss_error;
    double in[] = new double[NumInputs];
    double target[] = new double[NumOutputs];
    Neuron neuron, outNeurons[];

    ss_error = 0.0;

    outNeurons = net.layers[net.numLayers - 1].neurons;
    for (e = 0; e < numExamples; e++) {
      for (int i = 0; i < NumInputs; i++)
        in[i] = examples.getInputDouble(e, i);

      propagate(in);

      for (int o = 0; o < NumOutputs; o++)
        target[o] = examples.getOutputDouble(e, o);

      for (int o = 0; o < NumOutputs; o++) {
        neuron = outNeurons[o];

        double prediction = neuron.activation;

        if (printPredictions)
          System.out.println("nn : " + prediction + " : " + target[o]);

        ss_error += Math.pow((prediction - target[o]), 2.0);
      }
    }
    return Math.sqrt(ss_error / (numExamples * NumOutputs));
  }

  public void train(ExampleTable examples, int epochs, double learningRate, double momentum,
                    boolean incrementalWeightUpdates, boolean calculateErrors, double errorThreshold,
                    int errorCheckNumEpochs,
                    long maxNumWeightUpdates, double maxCPUTime)

  {

    int numExamples = examples.getNumRows();

    if (false) {
      System.out.println("epochs                   = " + epochs);
      System.out.println("learningRate             = " + learningRate);
      System.out.println("incrementalWeightUpdates = " + incrementalWeightUpdates);
      System.out.println("calculateErrors          = " + calculateErrors);
      System.out.println("errorThreshold           = " + errorThreshold);
      System.out.println("errorCheckNumEpochs      = " + errorCheckNumEpochs);
      System.out.println("maxNumWeightUpdates      = " + maxNumWeightUpdates);
      System.out.println("maxCPUTime               = " + maxCPUTime);
    }

    //  record time and calculate time to stop network //
    Clock clock = new Clock();
    double cpuStopTime = clock.getTime() + maxCPUTime;

    int e, epochIndex, check_count, rate_count;
    int j, k, l, i, o;
    double targ, act, error = 0.0, error_rate;
    Neuron neuron, neurons[];
    Connection con;
    Layer layer;
    long numWeightUpdatesPerExample, numWeightUpdates;
    int numLayers;
    int numHiddenLayers, nextReportNum;
    double in[] = new double[NumInputs];
    double target[] = new double[NumOutputs];

    check_count = 0;

    //  calculate the number of parameter updates per example //

    numLayers = net.numLayers;
    numHiddenLayers = numLayers - 2;

    numWeightUpdatesPerExample = net.numNeurons + net.numConnections;
    numWeightUpdates = 0;

    nextReportNum = 10000000;

    boolean stop = false;

    for (epochIndex = 0; (epochIndex < epochs) && !stop; epochIndex++) {
      for (e = 0; (e < numExamples); e++) {

        for (i = 0; i < NumInputs; i++)
          in[i] = examples.getInputDouble(e, i);

        for (o = 0; o < NumOutputs; o++)
          target[o] = examples.getOutputDouble(e, o);

          //System.out.println(in[0] + " : " + target[0]);

        propagate(in);

        targ = 0.0;
        act = 0.0;
        double output_error = 0.0;

        neurons = net.layers[numHiddenLayers + 1].neurons;

        for (j = 0; j < NumOutputs; j++) {
          neuron = neurons[j];
          targ = target[j];
          act = neuron.activation;
          output_error = targ - act;
          calculateDelta(neuron, output_error);
          AccumulateBias(neuron);
          if (incrementalWeightUpdates)
            CalculateBias(neuron, learningRate, momentum);
        }

        for (l = numHiddenLayers; l >= 0; l--) {
          layer = net.layers[l];
          for (j = 0; j < layer.numNeurons; j++) {
            neuron = layer.neurons[j];
            for (k = 0; k < neuron.numOutputConnections; k++) {
              con = neuron.outputConnections[k];
              calculateWeightAccumulation(con);
              if (incrementalWeightUpdates)
                calculateWeight(con, learningRate, momentum);
            }
            calculateDelta(neuron, 0.0);
            AccumulateBias(neuron);
            if (incrementalWeightUpdates)
              CalculateBias(neuron, learningRate, momentum);
          }
        }

        if ((cpuStopTime != Double.MAX_VALUE) && (clock.getTime() > cpuStopTime)) {
          if (false) {
            System.out.println("numWeightUpdates =" + numWeightUpdates);
          }
          stop = true;
        }

        numWeightUpdates += numWeightUpdatesPerExample;

        if (numWeightUpdates >= nextReportNum) {
          System.out.println(nextReportNum + " weights updated, example = " + (e + 1) + ", epoch = " + (epochIndex + 1));
          System.out.flush();
          nextReportNum += 10000000;
        }
        if (numWeightUpdates > maxNumWeightUpdates) {
          stop = true;
        }
      }

      if (!incrementalWeightUpdates) {
        for (l = numHiddenLayers + 1; l >= 0; l--) {
          layer = net.layers[l];
          for (j = 0; j < layer.numNeurons; j++) {
            neuron = layer.neurons[j];
            for (k = 0; k < neuron.numOutputConnections; k++) {
              con = neuron.outputConnections[k];
              calculateWeight(con, learningRate, momentum);
              CalculateBias(neuron, learningRate, momentum);
            }
          }
        }
      }

      if (calculateErrors || stop) {
        if (epochIndex == check_count || stop) {
          if (false)
            System.out.println();

          error = CalculateError(examples, false);
          if (false)
            System.out.println("training set error = " + error);

          if (error <= errorThreshold) {
            stop = true;
          }

          check_count += errorCheckNumEpochs;
        }
      }

    }

  }

}