package ncsa.d2k.modules.core.prediction.neuralnet;

import ncsa.d2k.modules.core.datatype.parameter.impl.*;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.table.ColumnTypes;
import ncsa.d2k.modules.core.prediction.*;
import ncsa.d2k.core.modules.PropertyDescription;

public class BackPropParamSpaceGenerator extends AbstractParamSpaceGenerator
	implements java.io.Serializable{
	static final String [] func_names = new String [BackPropModel.NUM_PARAMS];
	static {
		func_names[BackPropModel.ACTIVATION_FUNCTION] = "Activation Function";
		func_names[BackPropModel.UPDATE_FUNCTION] = "Training Method";
		func_names[BackPropModel.EPOCHS] = "Epochs";
		func_names[BackPropModel.SEED] = "Seed";
		func_names[BackPropModel.WEIGHT_INIT_RANGE] = "Weight Initial Range";
		func_names[BackPropModel.LEARNING_RATE_FUNCTION] = "Learning Rate Function";
		func_names[BackPropModel.INITIAL_LEARNING_RATE] = "Initial Learning Rate";
		func_names[BackPropModel.FINAL_LEARNING_RATE] = "Final Learning Rate";
		func_names[BackPropModel.HIDDEN_LAYERS] = "Number Hidden Layers";
		func_names[BackPropModel.NODES_IN_LAYER_01] = "Nodes In Layer 1";
		func_names[BackPropModel.NODES_IN_LAYER_02] = "Nodes In Layer 2";
		func_names[BackPropModel.NODES_IN_LAYER_03] = "Nodes In Layer 3";
		func_names[BackPropModel.NODES_IN_LAYER_04] = "Nodes In Layer 4";
	}

	/**
	 * Return a list of the property descriptions.
	 * @return a list of the property descriptions.
	 */
	public PropertyDescription [] getPropertiesDescriptions () {
		PropertyDescription [] pds = new PropertyDescription [13];
		pds[BackPropModel.ACTIVATION_FUNCTION] = new PropertyDescription ("activation",
			func_names[BackPropModel.ACTIVATION_FUNCTION],
			"The weighted sum of the input weights and inputs to every perceptron "+
			"are fed through the activation function to ensure that the output "+
			"is between 0 and 1 (or -1 and 1, depending on which function). These "+
			"functions also must have a few other properties to ensure the back "+
			"propagation function is able to work. "+
			"<ul>"+
			"	<li><u>Elliot</u> - 0 - "+
			"	From a paper by D.L. Elliot. ElliotAct(x)=|x/(1-x)|. This can be "+
			"	computed much faster than Sigmoid or Tanh and usually gives good "+
			"	results."+
			"	<li><u>FastSigmoid</u> - 1 - A linear approximation of the Sigmoid "+
			"	function, which makes it faster but less accurate. "+
			"	<li><u>FastTanh</u> - 2 - A linear approximation of Tanh, similar "+
			"	issues as FastSigmoid. "+
			"	<li><u>Sigmoid</u> - 3 - The function multi-layered perceptron "+
			"	neural "+
			"	networks were first developed with. Normally neural nets that use "+
			"	them are fairly accurate, but the function evaluation requires "+
			"	calculating an exponential, which is computationally expensive "+
			"	(read \"slow\"). "+
			"	<li><u>Tanh</u> - 4 - Another expensive, accurate function. "+
			"</ul>");

		pds[BackPropModel.UPDATE_FUNCTION] = new PropertyDescription (
			"trainingMethod",
			func_names[BackPropModel.UPDATE_FUNCTION],
			"This function defines the order the examples "+
			"are trained on and when the update of the activation weights occurs. "+
			"<ul>"+
			"	<li><u>Incremental BackProp</u> - 0 - The activation weights are "+
			"	updated after every training example is passed through, and the "+
			"	examples are passed through in the order given in the training "+
			"	data. This is slower than Batch BackProp. "+
			"	<li><u>Batch BackProp</u> - 1 - Weights are updated after every epoch "+
			"	(complete pass through all examples), examples are iterated over in "+
			"	order. This is less expensive "+
			"	than Incremental BackProp. In practice, it usually provides better "+
			"	results, as well."+
			"</ul>");

		pds[BackPropModel.EPOCHS] = new PropertyDescription (
			"epochs",
			func_names[BackPropModel.EPOCHS],
			"The number of passes through the training data set "+
			"(iterations) that the training function will do.");


		pds[BackPropModel.SEED] = new PropertyDescription (
			"seed",
			func_names[BackPropModel.SEED],
			"A seed to the random weight initialization. This can't "+
			"really be optimized but trying different values for any parameter setting "+
			"is a good idea as back propagation is capable of finding only the locally "+
			"optimum set of weights.");
		pds[BackPropModel.WEIGHT_INIT_RANGE] = new PropertyDescription (
			"weightInitRange",
			func_names[BackPropModel.WEIGHT_INIT_RANGE],
			"The activation weights will be "+
			"randomly initiallized to values between zero and this value. This is "+
			"particularly useful if the inputs in the data set (independent variables) "+
			"are not scaled to a standard range.");

		pds[BackPropModel.LEARNING_RATE_FUNCTION] = new PropertyDescription (
			"lacc",
			func_names[BackPropModel.LEARNING_RATE_FUNCTION],
			"The learning rate indicates how much of an adjustment to the weights will be done during "+
			"every update. "+
			"Learning acceleration refers to changing "+
			"	the learning rate as the training process proceeds. This can be based on "+
			"	the epoch or the time, and can be any kind of monotonically decreasing "+
			"	function. The purpose of altering the learning rate is to make large "+
			"	adjustments initially when the weights are still near-random and then "+
			"	smaller as the network approaches an optimal solution (think of it as a "+
			"	hill climbing algorithm that takes big steps when it's far from the "+
			"	optimum and takes smaller steps at it approaches the optimum for better "+
			"	accuracy). Currently only Linear by Epoch is implemented, but the "+
			"	infrastructure is such that other methods can easily be added."+
			"	<ul>"+
			"		<li><u>Linear by Epoch</u> - 0 - Starts at the Initial Learning Rate "+
			"		and "+
			"		decreases it the same amount every epoch, such that the final epoch "+
			"		uses a learning rate of Final Learning Rate."+
			"	</ul>");

		pds[BackPropModel.INITIAL_LEARNING_RATE] = new PropertyDescription (
			"initLRate",
			func_names[BackPropModel.INITIAL_LEARNING_RATE],
			"The learning rate of the first epoch.");

		pds[BackPropModel.FINAL_LEARNING_RATE] = new PropertyDescription (
			"finalLRate",
			func_names[BackPropModel.FINAL_LEARNING_RATE],
			"The learning rate of the last epoch.");

		pds[BackPropModel.HIDDEN_LAYERS] = new PropertyDescription (
			"hiddenLayers",
			func_names[BackPropModel.HIDDEN_LAYERS],
			"This is the number of layers of "+
			"perceptrons between the input nodes and the output nodes. Currently "+
			"restricted to be between one and four. This restriction is only in place "+
			"because a more sophisticated parameter selection interface is not in place "+
			". The actual algorithm can handle any number of hidden layers.");

		pds[BackPropModel.NODES_IN_LAYER_01] = new PropertyDescription (
			"nodesPerLayer1",
			func_names[BackPropModel.NODES_IN_LAYER_01],
		   "The number of nodes in first layer. This can be any positive integer, although in practice "+
		   "values greater than 20 usually cause the model building process to run "+
		   "longer than is practical.");

		pds[BackPropModel.NODES_IN_LAYER_02] = new PropertyDescription (
			"nodesPerLayer2",
			func_names[BackPropModel.NODES_IN_LAYER_02],
			"The number of nodes in second layer. This can be any positive integer, although in practice "+
			"values greater than 20 usually cause the model building process to run "+
			"longer than is practical.");

		pds[BackPropModel.NODES_IN_LAYER_03] = new PropertyDescription (
			"nodesPerLayer3",
			func_names[BackPropModel.NODES_IN_LAYER_03],
			"The number of nodes in third layer. This can be any positive integer, although in practice "+
			"values greater than 20 usually cause the model building process to run "+
			"longer than is practical.");

		pds[BackPropModel.NODES_IN_LAYER_04] = new PropertyDescription (
			"nodesPerLayer4",
			func_names[BackPropModel.NODES_IN_LAYER_04],
			"The number of nodes in fourth layer. This can be any positive integer, although in practice "+
			"values greater than 20 usually cause the model building process to run "+
			"longer than is practical.");
		return pds;
	}

	public static final String PARAM_DESCRIPTION=
"	The following are the parameters of the BackProp Neural Network. Parameter"+
"	names are in bold, options are underlined. Numbers following the option "+
"	indicate the index that refers to that option in the parameter object."+
"	<ul>	"+

"	<li><b>Activation function</b>:"+
"		The weighted sum of the input weights and inputs to every perceptron"+
"		are fed through the activation function to ensure that the output"+
"		is between 0 and 1 (or -1 and 1, depending on which function). These"+
"		functions also must have a few other properties to ensure the back"+
"		propagation function is able to work		"+
"		<ul>"+
"			<li><u>Elliot</u> - 0 -"+
"			From a paper by D.L. Elliot. ElliotAct(x)=|x/(1-x)|. This can be "+
"			computed much faster than Sigmoid or Tanh and usually gives good"+
"			results."+
"			<li><u>FastSigmoid</u> - 1 - A linear approximation of the Sigmoid"+
"			function, which makes it faster but less accurate."+
"			<li><u>FastTanh</u> - 2 - A linear approximation of Tanh, similar "+
"			issues as FastSigmoid"+
"			<li><u>Sigmoid</u> - 3 - The function multi-layered perceptron "+
"			neural"+
"			 networks were first developed with. Normally neural nets that use"+
"			 them are fairly accurate, but the function evaluation requires "+
"			 calculating an exponential, which is computationally expensive "+
"			 (read \"slow\")."+
"			<li><u>Tanh</u> - 4 - Another expensive, accurate function. "+
"		</ul>"+

"	<li><b>Training Method</b>:This function defines the order the examples"+
"	are trained on and when the update of the activation weights occurs. "+
"		<ul>"+
"			<li><u>Incremental BackProp</u> - 0 - The activation weights are "+
"			updated after every training example is passesed through, and the"+
"			examples are passed through in the order given in the training"+
"			data. This is slower than Batch BackProp."+
"			<li><u>Batch BackProp</u> - 1 - Weights are updated after every "+
"			epoch"+
"			(complete pass through all examples), examples are iterated over in "+
"			order. This is less expensive"+
"			than Incremental BackProp. In practice, it usually provides better"+
"			results, as well."+
"		</ul>"+

"	<li><b>Epochs</b>: The number of passes through the training data set "+
"	(iterations) that the training function will do."+

"	<li><b>Seed</b>: A seed to the random weight initialization. This can't "+
"	really be optimized but trying different values for any parameter setting "+
"	is a good idea as back propagation is capable of finding only the locally "+
"	optimum set of weights."+

"	<li><b>Weight Initialization Range</b>: The activation weights will be "+
"	randomly initialized to values between zero and this value. This is "+
"	particularly useful if the inputs in the data set (independent variables)"+
"	are not scaled to a standard range."+

"	<li><b>Learning Accelerator</b>: "+
"The learning rate indicates how much of an adjustment to the weights during"+
" every update."+
"Learning acceleration refers to changing "+
"	the learning rate as the training process proceeds. This can be based on "+
"	the epoch or the time, and can be any kind of monotonically decreasing "+
"	function. The purpose of altering the learning rate is to make large "+
"	adjustments initially when the weights are still near-random and then "+
"	smaller as the network approaches an optimal solution (think of it as a "+
"	hill climbing algorithm that takes big steps when it's far from the "+
"	optimum and takes smaller steps at it approaches the optimum for better "+
"	accuracy). Currently only Linear by Epoch is implemented, but the "+
"	infrastructure is such that other methods can easily be added."+
"	<ul>"+
"		<li><u>Linear by Epoch</u> - 0 - Starts at the Initial Learning Rate "+
"		and "+
"		decreases it the same amount every epoch, such that the final epoch"+
"		uses a learning rate of Final Learning Rate."+
"	</ul>"+

"	<li><b>Initial Learning Rate</b>: The learning rate of the first epoch."+

"	<li><b>Final Learning Rate</b>: The learning rate of the last epoch."+

"	<li><b>Number of Hidden Layers</b>:This is the number of layers of "+
"	perceptrons between the input nodes and the output nodes. Currently "+
"	restricted to be between one and four. This restriction is only in place"+
"	because a more sophisticated parameter selection interface is not in place"+
".	The actual algorithm can handle any number of hidden layers."+

"	<li><b>Nodes in Layer[1-4]</b>: The number of nodes in each layer, set"+
"	individually. This can be any positive integer, although in practice "+
"	values greater than 20 usually cause the model building process to run"+
"   longer than is practical.</ul>";



	/**
	 * Returns a reference to the developer supplied defaults. These are
	 * like factory settings, absolute ranges and definitions that are not
	 * mutable.
	 * @return the factory settings space.
	 */
	protected ParameterSpace getDefaultSpace(){

		String[] names=new String[BackPropModel.NUM_PARAMS];
		double[] mins=new double[BackPropModel.NUM_PARAMS];
		double[] maxs=new double[BackPropModel.NUM_PARAMS];
		double[] defaults=new double[BackPropModel.NUM_PARAMS];
		int[] res=new int[BackPropModel.NUM_PARAMS];
		int[] types=new int[BackPropModel.NUM_PARAMS];

		mins[BackPropModel.ACTIVATION_FUNCTION]=0;
		mins[BackPropModel.UPDATE_FUNCTION]=0;
		mins[BackPropModel.EPOCHS]=500;
		mins[BackPropModel.SEED]=-100;
		mins[BackPropModel.WEIGHT_INIT_RANGE]=.1;
		mins[BackPropModel.LEARNING_RATE_FUNCTION]=0;
		mins[BackPropModel.INITIAL_LEARNING_RATE]=.001;
		mins[BackPropModel.FINAL_LEARNING_RATE]=.001;
		mins[BackPropModel.HIDDEN_LAYERS]=0;
		mins[BackPropModel.NODES_IN_LAYER_01]=1;
		mins[BackPropModel.NODES_IN_LAYER_02]=1;
		mins[BackPropModel.NODES_IN_LAYER_03]=1;
		mins[BackPropModel.NODES_IN_LAYER_04]=1;

		maxs[BackPropModel.ACTIVATION_FUNCTION]=4;
		maxs[BackPropModel.UPDATE_FUNCTION]=1;
		maxs[BackPropModel.EPOCHS]=15000;
		maxs[BackPropModel.SEED]=100;
		maxs[BackPropModel.WEIGHT_INIT_RANGE]=20.1;
		maxs[BackPropModel.LEARNING_RATE_FUNCTION]=1;
		maxs[BackPropModel.INITIAL_LEARNING_RATE]=.999;
		maxs[BackPropModel.FINAL_LEARNING_RATE]=.999;
		maxs[BackPropModel.HIDDEN_LAYERS]=4;
		maxs[BackPropModel.NODES_IN_LAYER_01]=20;
		maxs[BackPropModel.NODES_IN_LAYER_02]=20;
		maxs[BackPropModel.NODES_IN_LAYER_03]=20;
		maxs[BackPropModel.NODES_IN_LAYER_04]=20;

		names[BackPropModel.ACTIVATION_FUNCTION]= func_names[0];
		names[BackPropModel.UPDATE_FUNCTION]= func_names[1];
		names[BackPropModel.EPOCHS]= func_names[2];
		names[BackPropModel.SEED]= func_names[3];
		names[BackPropModel.WEIGHT_INIT_RANGE]= func_names[4];
		names[BackPropModel.LEARNING_RATE_FUNCTION]= func_names[5];
		names[BackPropModel.INITIAL_LEARNING_RATE]= func_names[6];
		names[BackPropModel.FINAL_LEARNING_RATE]= func_names[7];
		names[BackPropModel.HIDDEN_LAYERS]= func_names[8];
		names[BackPropModel.NODES_IN_LAYER_01]= func_names[9];
		names[BackPropModel.NODES_IN_LAYER_02]= func_names[10];
		names[BackPropModel.NODES_IN_LAYER_03]= func_names[11];
		names[BackPropModel.NODES_IN_LAYER_04]= func_names[12];

		res[BackPropModel.ACTIVATION_FUNCTION]=1;
		res[BackPropModel.UPDATE_FUNCTION]=1;
		res[BackPropModel.EPOCHS]=50;
		res[BackPropModel.SEED]=4;
		res[BackPropModel.WEIGHT_INIT_RANGE]=40;
		res[BackPropModel.LEARNING_RATE_FUNCTION]=1;
		res[BackPropModel.INITIAL_LEARNING_RATE]=100;
		res[BackPropModel.FINAL_LEARNING_RATE]=100;
		res[BackPropModel.HIDDEN_LAYERS]=1;
		res[BackPropModel.NODES_IN_LAYER_01]=1;
		res[BackPropModel.NODES_IN_LAYER_02]=1;
		res[BackPropModel.NODES_IN_LAYER_03]=1;
		res[BackPropModel.NODES_IN_LAYER_04]=1;

		defaults[BackPropModel.ACTIVATION_FUNCTION]=0;
		defaults[BackPropModel.UPDATE_FUNCTION]=0;
		defaults[BackPropModel.EPOCHS]=3000;
		defaults[BackPropModel.SEED]=3.12;
		defaults[BackPropModel.WEIGHT_INIT_RANGE]=3;
		defaults[BackPropModel.LEARNING_RATE_FUNCTION]=0;
		defaults[BackPropModel.INITIAL_LEARNING_RATE]=.8;
		defaults[BackPropModel.FINAL_LEARNING_RATE]=.1;
		defaults[BackPropModel.HIDDEN_LAYERS]=1;
		defaults[BackPropModel.NODES_IN_LAYER_01]=4;
		defaults[BackPropModel.NODES_IN_LAYER_02]=2;
		defaults[BackPropModel.NODES_IN_LAYER_03]=2;
		defaults[BackPropModel.NODES_IN_LAYER_04]=2;

		types[BackPropModel.ACTIVATION_FUNCTION]=ColumnTypes.INTEGER;
		types[BackPropModel.UPDATE_FUNCTION]=ColumnTypes.INTEGER;
		types[BackPropModel.EPOCHS]=ColumnTypes.INTEGER;
		types[BackPropModel.SEED]=ColumnTypes.DOUBLE;
		types[BackPropModel.WEIGHT_INIT_RANGE]=ColumnTypes.DOUBLE;
		types[BackPropModel.LEARNING_RATE_FUNCTION]=ColumnTypes.INTEGER;
		types[BackPropModel.INITIAL_LEARNING_RATE]=ColumnTypes.DOUBLE;
		types[BackPropModel.FINAL_LEARNING_RATE]=ColumnTypes.DOUBLE;
		types[BackPropModel.HIDDEN_LAYERS]=ColumnTypes.INTEGER;
		types[BackPropModel.NODES_IN_LAYER_01]=ColumnTypes.INTEGER;
		types[BackPropModel.NODES_IN_LAYER_02]=ColumnTypes.INTEGER;
		types[BackPropModel.NODES_IN_LAYER_03]=ColumnTypes.INTEGER;
		types[BackPropModel.NODES_IN_LAYER_04]=ColumnTypes.INTEGER;

		ParameterSpaceImpl space=new ParameterSpaceImpl();
		space.createFromData(names,mins,maxs,defaults,res,types);
		return space;
	}


	/**
	 * returns the information about the module.
	 * @return the information about the module.
	 */
	public String getModuleInfo() {
		return
		"Generates the parameter space for a Back Propagation Neural Net.";
		/*return
		"Generates the parameter space for a Back Propagation Neural Net, "+
		"which can be used to optimize that parameters of the neural net for"+
		" a particular data set. The minimum, maximum, and resolution of the"+
		" following parameters can be modified via this modules properties."+
		" Here is a brief description of the parameters:"+
		PARAM_DESCRIPTION;*/
	}

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "Neural Net Parameter Space Generator";
	}


	/**
	 * returns information about the output at the given index.
	 * @return information about the output at the given index.
	 */
	public String getOutputInfo(int index) {
		switch (index) {
			case 0:
				return "This is the parameter space that will be searched";
			default: return "No such output";
		}
	}

	/**
	 * returns information about the output at the given index.
	 * @return information about the output at the given index.
	 */
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "Parameter Space";
			default: return "NO SUCH OUTPUT!";
		}
	}
}



