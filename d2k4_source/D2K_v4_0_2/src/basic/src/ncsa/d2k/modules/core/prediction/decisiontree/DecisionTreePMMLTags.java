package ncsa.d2k.modules.core.prediction.decisiontree;

import ncsa.d2k.modules.core.prediction.PMMLTags;

public interface DecisionTreePMMLTags extends PMMLTags {

  public static final String TREE_MODEL = "TreeModel";
  public static final String SCORE = "score";
  public static final String SCORE_DISTRIBUTION = "ScoreDistribution";
  public static final String RECORD_COUNT = "recordCount";
}