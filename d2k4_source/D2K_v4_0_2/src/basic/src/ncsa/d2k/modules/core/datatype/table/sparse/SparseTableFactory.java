package ncsa.d2k.modules.core.datatype.table.sparse;

import ncsa.d2k.modules.core.datatype.table.TableFactory;
import ncsa.d2k.modules.core.datatype.table.Table;
import ncsa.d2k.modules.core.datatype.table.ExampleTable;
import ncsa.d2k.modules.core.datatype.table.PredictionTable;
import ncsa.d2k.modules.core.datatype.table.TestTable;
import ncsa.d2k.modules.core.datatype.table.TrainTable;

/**
 * Title:        Sparse Table
 * Description:  Sparse Table projects will implement data structures compatible to the interface tree of Table, for sparsely stored data.
 * Copyright:    Copyright (c) 2002
 * Company:      ncsa
 * @author vered goren
 * @version 1.0
 */

public class SparseTableFactory implements TableFactory {



  public SparseTableFactory() {
  }

  public Table createTable() {
    return new SparseExampleTable();
  }

  public Table createTable(int numColumns) {
      return new SparseExampleTable(0, numColumns);
  }

  public ExampleTable createExampleTable(Table table) {
      return table.toExampleTable();
  }

  public PredictionTable createPredictionTable(ExampleTable et) {
    return et.toPredictionTable();
  }

  public TestTable createTestTable(ExampleTable et) {
    return (TestTable) et.getTestTable();
  }
  public TrainTable createTrainTable(ExampleTable et) {
    return (TrainTable) et.getTrainTable();
  }
}
