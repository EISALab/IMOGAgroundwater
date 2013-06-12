package ncsa.d2k.modules.core.discovery.ruleassociation;

import ncsa.d2k.modules.core.prediction.*;

public interface RulePMMLTags extends PMMLTags {
    public static final String CONSEQUENT = "consequent";
    public static final String ANTECEDENT = "antecedent";
    public static final String SUPPORT = "support";
    public static final String CONFIDENCE = "confidence";
    public static final String ID = "id";
    public static final String VALUE = "value";
    public static final String ASSOC_RULE = "AssociationRule";
    public static final String ITEM_REF = "itemRef";
    public static final String ITEM = "Item";
    public static final String ITEMSET = "Itemset";
    public static final String ITEMREF = "ItemRef";
    public static final String ASSOC_MODEL = "AssociationModel";
    public static final String NUM_TRANS = "numberOfTransactions";
    public static final String MIN_SUP = "minimumSupport";
    public static final String MIN_CON = "minimumConfidence";
    public static final String NUM_ITEM = "numberOfItems";
    public static final String NUM_ITEMSETS = "numberOfItemsets";
    public static final String NUM_RULE = "numberOfRules";
}
