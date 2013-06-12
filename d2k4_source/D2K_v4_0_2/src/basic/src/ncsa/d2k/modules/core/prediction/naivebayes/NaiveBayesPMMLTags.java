package ncsa.d2k.modules.core.prediction.naivebayes;

import ncsa.d2k.modules.core.prediction.PMMLTags;

public interface NaiveBayesPMMLTags extends PMMLTags {

  public static final String NBM = "NaiveBayesModel";
  public static final String THRESHOLD = "threshold";
  public static final String BAYES_INPUTS = "BayesInputs";
  public static final String BAYES_INPUT = "BayesInput";
  public static final String FIELD_NAME = "fieldName";
  public static final String PAIR_COUNTS = "PairCounts";
  public static final String TARGET_VALUE_COUNTS = "TargetValueCounts";
  public static final String TARGET_VALUE_COUNT = "TargetValueCount";
  public static final String COUNT = "count";
  public static final String DERIVED_FIELD = "DerivedField";
  public static final String DISCRETIZE = "Discretize";
  public static final String DISCRETIZE_BIN = "DiscretizeBin";
  public static final String BIN_VALUE = "binValue";
  public static final String INTERVAL = "Interval";
  public static final String CLOSURE = "closure";
  public static final String LEFT_MARGIN = "leftMargin";
  public static final String RIGHT_MARGIN = "rightMargin";
  public static final String CLOSED_OPEN = "closedOpen";
  public static final String CLOSED_CLOSED = "closedClosed";
  public static final String OPEN_CLOSED = "openClosed";
  public static final String OPEN_OPEN = "openOpen";
  public static final String BAYES_OUTPUT = "BayesOutput";
  public static final String NO_FIELDS = "numberOfFields";
}