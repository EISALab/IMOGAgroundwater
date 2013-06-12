package ncsa.d2k.modules.core.discovery.ruleassociation;

import java.util.*;
import gnu.trove.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;

/** This class contains the following components:
 * minimumSupport - minimum support specified by a user
 * minimumConfidence - minimum confidence specified by a user
 * numberOfTransactions - number of records in the data set
 * ruleTable - an ExampleTable to hold all rules
 * items - a List to hold all item labels
 * itemSets - a List to hold all frequent item sets. Each frequent item set
 *            is represented by a list of item indexes.
 */
public class RuleTable extends MutableTableImpl {

    private static final int IF = 0;
    private static final int THEN = 1;
    private static final int SUPPORT = 3;
    private static final int CONFIDENCE = 2;

    private double minimumConfidence;
    private double minimumSupport;
    private int numberOfTransactions;
    private int numRulesShowing;
    private List items;
    private List itemSets;
    private LinkedList origItems;
    private LinkedList origItemSets;
    //--------------------

    /**
     * Create a new RuleTable
     * @param rls an ExampleTable to hold all rules
     * @param minCon the minimum confidence specified by a user
     * @param minSupp the minimum support specified by a user
     * @param names list of item labels
     * @param sets list of frequent item sets
     */
    public RuleTable(TableImpl rls, double minCon, double minSupp, int numTrans, List names, List sets) {
        // the support and confidence columns are in the wrong order when we get them.
        Column[] c = {rls.getColumn(0), rls.getColumn(1),
            rls.getColumn(3), rls.getColumn(2)};
        this.columns = c;
        numberOfTransactions = numTrans;
        minimumConfidence = minCon;
        minimumSupport = minSupp;
        this.items = names;
        this.itemSets = sets;
        initializeOriginals();
    }

    /**
     * This method remembers the original values that were in the RuleTable before
     * any alterations.
     */
    private void initializeOriginals(){
      	this.origItems = new LinkedList(items);
	this.origItemSets = new LinkedList(itemSets);
        for(int i = 0; i < origItemSets.size(); i++){
          FreqItemSet I = (FreqItemSet) itemSets.get(i);
          FreqItemSet newItems = new FreqItemSet();
          newItems.support = I.support;
          newItems.numberOfItems = I.numberOfItems;
          int[] array = new int[I.items.size()];
          array = I.items.toNativeArray();
          newItems.items = new TIntArrayList(array);
          origItemSets.set(i, newItems);
          numRulesShowing = getNumRules();
      }
    }

    /**
     * cleanup removes attribute-value combinations from the visualization
     * that are not part of any rule or that don't meet the filter criteria.
     * First the remap field identifies which
     * items(attribute-values) are not used. Then the remap field is used to
     * determine the items to be removed and identify the new value that items
     * should use.  Then it adjusts the frequent item sets.
     * This method By Loretta Auvil
     */
    public void cleanup() {
      // assign value of 1 if attribute-value is used
      int [] remap = new int[items.size()];
      for (int i=0; i < getNumRules(); i++) {
        int[] ante = getRuleAntecedent(i);
        for (int j=0; j < ante.length; j++)  {
          if (ante[j] != -1 && remap[ante[j]] != 1)
            remap[ante[j]] = 1;
        }
        int[] conseq = getRuleConsequent(i);
        for (int j=0; j < conseq.length; j++)   {
          if (conseq[j] != -1 && remap[conseq[j]] != 1)
            remap[conseq[j]] = 1;
        }
      }
      //calculate new index for attribute-value
      int adjustment = 0;
      int len = items.size();
      for (int i=0; i < len; i++)
        if (remap[i] != 1) {
          items.remove(i-adjustment);
          adjustment++;
          remap[i] = -1;
        }
        else
          remap[i] = i-adjustment;
      //adjust the values of frequent item sets by the remap data
      for (int i=0; i < itemSets.size(); i++) {
        FreqItemSet fis = (FreqItemSet) itemSets.get(i);
        len = fis.items.size();
        int j = 0;
        while(j < fis.items.size() && fis.items.get(j) != -1){
          fis.items.set(j, remap[fis.items.get(j)]);
          j++;
        }
      }
    }


    /**
     * This is the same as cleanup(), but the order of the rules has changed due
     * to a user's filtering criteria.
     * @param order the order the rows appear in the RuleVis
     */
    public void cleanup(int[] order) {
      // assign value of 1 if attribute-value is used
      int [] remap = new int[items.size()];
      for (int i=0; i < numRulesShowing; i++) {
        int[] ante = getRuleAntecedent(order[i]);
        for (int j=0; j < ante.length; j++)  {
          if (ante[j] != -1 && remap[ante[j]] != 1)
            remap[ante[j]] = 1;
        }
        int[] conseq = getRuleConsequent(order[i]);
        for (int j=0; j < conseq.length; j++)   {
          if (conseq[j] != -1 && remap[conseq[j]] != 1)
            remap[conseq[j]] = 1;
        }
      }
      //calculate new index for attribute-value
      int adjustment = 0;
      int len = items.size();
      for (int i=0; i < len; i++)
        if (remap[i] != 1) {
          items.remove(i-adjustment);
          adjustment++;
          remap[i] = -1;
        }
        else
          remap[i] = i-adjustment;
      //adjust the values of frequent item sets by the remap data
      for (int i=0; i < itemSets.size(); i++) {
        FreqItemSet fis = (FreqItemSet) itemSets.get(i);
        len = fis.items.size();
        int j = 0;
        while(j < fis.items.size() && fis.items.get(j) != -1){
          fis.items.set(j, remap[fis.items.get(j)]);
          j++;
        }
      }
    }


    /**
     * alphaSort orders the attribute-value combinations alphabetically
     * and then updates the corresponding rule associations (in itemSets)
     * to reflect the new ordering.
     */
    public void alphaSort(){
      //set swapMirror to its starting values
      int [] swapMirror = new int[items.size()];
      for(int i=0; i<items.size(); i++){swapMirror[i] = i;}
      //sort the attribute-value combinations
      int begin = 0;
      while(begin < items.size()){
        ListIterator I = items.listIterator(begin);
        String min = I.next().toString();
        int minIndex = begin;
        for (int i = begin; i < (items.size()-1); i++) {
          String current = I.next().toString();
          if (min.compareTo(current) > 0) {
            min = current;
            minIndex = I.previousIndex();
          }
        }
        //swap the first string with the minimal string
        items.set(minIndex, items.get(begin));
        items.set(begin, min);
        //update the mirror
        int temp = swapMirror[begin];
        swapMirror[begin] = swapMirror[minIndex];
        swapMirror[minIndex] = temp;
        begin++;
      }
      this.reOrderRowVals(swapMirror);
    }


    /**
     * Some 'items' cannot be removed because others reference that same 'items'
     * in memory.  Thus, the important 'items' are remembered in this HashSet.
     * @param order is the current order of the rows in the Vis
     * @return h, the HashSet of 'items' used by the filter criteria
     */
    private HashSet findUsedItems(int[] order){
      HashSet h = new HashSet();
      FreqItemSet F;
      for(int i = 0; i < order.length; i++){
        //the rules contained in order[] are the important rules that need to be seen
        int ante = this.getRuleAntecedentID(order[i]);
        F = (FreqItemSet)itemSets.get(ante);
        h.add(F.items);
        int cons = this.getRuleConsequentID(order[i]);
        F = (FreqItemSet)itemSets.get(cons);
        h.add(F.items);
      }
      return h;
    }

    /**
     * Rules have been filtered and this method prepares the itemSets according
     * to the list of rules that the filter selects.  A -1 value is assigned to
     * the items list in the itemSets if the value is not to be displayed.
     * @param rowList is the array produced by the filter to specify the rules to be removed
     * @param order is the current order of the rules in the Vis.
     */
    public void rulesToDisplay(boolean[] ruleList, int[] order){
      numRulesShowing = order.length;
      HashSet hSet = findUsedItems(order);
      int ante;
      int cons;
      FreqItemSet F;
      for(int c = 0; c < ruleList.length; c++){
        if(ruleList[c] == false){
          ante = this.getRuleAntecedentID(c);
          cons = this.getRuleConsequentID(c);
          F = (FreqItemSet)itemSets.get(ante);
          //'remove' the items not used by the Vis.
          if(!hSet.contains(F.items))
            for(int i = 0; i < F.items.size(); i++)
              F.items.set(i, -1);
          F = (FreqItemSet)itemSets.get(cons);
          if(!hSet.contains(F.items))
            F.items.set(0, -1);
        }
      }
      //remove now unused rows in the Vis
      cleanup(order);
    }

    /**
     * This method moves the values in the table around according to the
     * array that is passed in using this convention:  newOrder[0] contains
     * the index of the row that should be moved to row 0.
     * @param newOrder is the re-mapping array
     */
    public void reOrderRowVals(int[] newOrder){
      int [] rowMap = new int [items.size()];
      for(int x = 0; x < items.size(); x++)
        rowMap[newOrder[x]] = x;
      int len;
      for (int i=0; i < itemSets.size(); i++) {
        FreqItemSet fis = (FreqItemSet)itemSets.get(i);
        len = fis.items.size();
        for (int j=0; j<len; j++){
          if(fis.items.get(j) < 0)
            j = len;
          else
            fis.items.set(j, rowMap[fis.items.get(j)]);
        }
      }
    }

    /**
     * This method moves the attribute-value combinations around according to the
     * array that is passed in using the same convention as reOrderRowVals.
     * @param newOrder is the re-mapping array
     */
    public void reOrderRowHeads(int[] newOrder){
      LinkedList newList = new LinkedList();
      for(int i = 0; i < items.size(); i++){
        newList.add(i,items.get(newOrder[i]));
      }
      items = newList;
    }

    /**
     * This method moves the rows of the entire table (heads and vals) according
     * to the remapping array newOrder.
     * @param newOrder is the re-mapping array
     */
    public void reOrderRows(int[] newOrder){
      reOrderRowHeads(newOrder);
      reOrderRowVals(newOrder);
    }

    /**
     * This method restores items and itemSets to their original values.
     */
    public void setToOriginal(){
      items = origItems;
      itemSets = origItemSets;
      initializeOriginals();
    }

    /**
     * Get the number of rules showing in the Vis
     * @return the number of rules
     */
    public int getNumRulesShowing(){
      return numRulesShowing;
    }


    /**
     * Sort the rules by confidence.
     */
    public void sortByConfidence() {
        sortByColumn(CONFIDENCE);
    }

    /**
     * Sort the rules by support.
     */
    public void sortBySupport() {
        sortByColumn(SUPPORT);
    }

    public void sortByAntecedent() {
      this.itemSetSort(IF);
    }

    public void sortByConsequent() {
      this.itemSetSort(THEN);
    }


    /**
     * Get the minimum confidence
     * @return the confidence a user specified
     */
    public double getMinimumConfidence() {
        return minimumConfidence;
    }

    /**
     * Get the minimum support
     * @return the support a user specified
     */
    public double getMinimumSupport() {
        return minimumSupport;
    }

    /**
     * Get the number of transactions
     * @return the number of transactions in the data set
     */
    public int getNumberOfTransactions() {
        return numberOfTransactions;
    }

    /**
     * Get the confidence for a specific rule
     * @param row the rule to check.
     * @return the confidence of the rule.
     */
    public double getConfidence(int row) {
        return getDouble(getNumRows()-1-row, CONFIDENCE);
    }

    /**
     * Get the support for a specific rule
     * @param row the rule to check.
     * @return the support of the rule.
     */
    public double getSupport(int row) {
        return getDouble(getNumRows()-1-row, SUPPORT);
    }

    /**
     * Get the antecedent for a specific rule
     * @param row the rule to check.
     * @return the antecedent of the rule represented by a list of item indexes.
     */
    public int[] getRuleAntecedent(int row) {
        int idx = getInt(getNumRows()-1-row, IF);
        FreqItemSet fis = (FreqItemSet)itemSets.get(idx);
        TIntArrayList set = fis.items;
        return set.toNativeArray();
    }

    /**
     * Get the ID of the antecedent for a specific rule
     * @param row the rule to check
     * @return the frequent set ID for the antecedent of the rule.
     */
    public int getRuleAntecedentID(int row) {
        return getInt(getNumRows()-1-row, IF);
    }

    /**
     * Get the consequent for a specific rule
     * @param row the rule to check for.
     * @return the consequent of the rule represented by a list of item indexes.
     */
    public int[] getRuleConsequent(int row) {
        int idx = getInt(getNumRows()-1-row, THEN);
        FreqItemSet fis = (FreqItemSet)itemSets.get(idx);
        TIntArrayList set = fis.items;
        return set.toNativeArray();
    }

    /**
     * Get the ID of the consequent for a specific rule
     * @param row the rule to check for.
     * @return the frequent set ID for the consequent of the rule.
     */
    public int getRuleConsequentID(int row) {
        return getInt(getNumRows()-1-row, THEN);
    }

    /**
     * Get the number of rules.
     * @return the number of rules for the data set.
     */
    public int getNumRules() {
        return getNumRows();
    }

    /**
     * Get the list of names.
     * @return the list of item labels.
     */
    public List getNamesList() {
        return items;
    }

    /**
     * Get the list of FreqItemSets
     * @return the list of FreqItemSets.
     */
    public List getItemSetsList() {
        return itemSets;
    }

        /**
     * Sort the table using one of the item set columns as the key.
     * @param col the column index (must be IF or THEN)
     */
    private void itemSetSort(int col) {
      doItemSetSort(0, getNumRows() - 1, col);
    }

         /**
          * Implement the quicksort algorithm.  Partition and
          * recursively call doItemSetSort.
          * @param p the beginning index
          * @param r the ending index
          * @param col the column index (must be IF or THEN)
         * @return a sorted array of floats
          */
         private void doItemSetSort (int p, int r, int col) {
                 if (p < r) {
                         int q = itemSetSortPartition(p, r, col);
                         doItemSetSort(p, q, col);
                         doItemSetSort(q + 1, r, col);
                 }
         }

         /**
          Rearrange the table in place.
          @param p the beginning index
          @param r the ending index
          @param col the column index (must be IF or THEN)
         @return the new partition point
          */
         private int itemSetSortPartition (int p, int r, int col) {
                 int i = p - 1;
                 int j = r + 1;

                 while (true) {
                         do {
                                 j--;
                         } //while (A[j] > x);
                  while(compareItemSetRows(j, p, col) < 0);
                         do {
                                 i++;
                         } //while (A[i] < x);
                  while(compareItemSetRows(i, p, col) > 0);
                         if (i < j) {
                            swapRows(i, j);
                         }
                         else
                                 return  j;
                 }
         }

         /**
          * Compare two rows that contain item sets.
          * @param i the first row
          * @param j the second row
          * @param column the column index (must be IF or THEN)
          * @return -1, 0, or 1 (see Comparable)
          */
    private int compareItemSetRows(int i, int j, int column) {
      FreqItemSet f1;
      FreqItemSet f2;

      if(column == IF) {
        f1 = (FreqItemSet)this.itemSets.get(getInt(i, IF));
        f2 = (FreqItemSet)this.itemSets.get(getInt(j, IF));
      }
      else {
        f1 = (FreqItemSet)this.itemSets.get(getInt(i, THEN));
        f2 = (FreqItemSet)this.itemSets.get(getInt(j, THEN));
      }

      String s1 = itemSetAsString(f1);
      String s2 = itemSetAsString(f2);

      return s1.compareTo(s2);
    }

    /**
     * Get a representation of an item set as a String.  Items are comma-separated.
     * @param f the frequent item set
     * @return a comma separated list of the items in the frequent item set
     */
    private String itemSetAsString(FreqItemSet f) {
      if(f.items != null) {
        StringBuffer sb = new StringBuffer();
        int[] ar = f.items.toNativeArray();
        for (int i = 0; i < ar.length; i++) {
          sb.append(items.get(ar[i]));
          if (i != ar.length - 1)
            sb.append(",");
        }
        return sb.toString().toLowerCase();
      }
      else
        return "";
    }
}
