package ncsa.d2k.modules.core.datatype.table.transformations;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.core.transform.attribute.*;

/**
 * <code>AttributeTransform</code> is a <code>Transformation</code> which uses
 * <code>ColumnExpression</code>s to construct new attributes from existing
 * attributes in a <code>Table</code>.
 */
public class AttributeTransform implements Transformation {

   private Object[] constructions;

   public AttributeTransform(Object[] constructions) {
      this.constructions = constructions;
   }

   public boolean transform(MutableTable table) {

      if (constructions == null || constructions.length == 0)
         return true;

      for (int i = 0; i < constructions.length; i++) {

         ColumnExpression exp = new ColumnExpression(table);
         Construction current = (Construction)constructions[i];
		 boolean [] missing;
         Object evaluation = null;
         try {
            exp.setExpression(current.expression);
            evaluation = exp.evaluate();
            missing = exp.getMissingValues();
         }
         catch(Exception e) {
            e.printStackTrace();
            return false;
         }

         switch (exp.evaluateType()) {

            case ColumnExpression.TYPE_BOOLEAN: {
               boolean[] data = (boolean[])evaluation;
			   BooleanColumn col = new BooleanColumn(data);
               col.setMissingValues(missing);
               table.addColumn(col);
               } break;
               
            case ColumnExpression.TYPE_BYTE:{
               byte[] data = (byte[])evaluation;
			   ByteColumn col = new ByteColumn(data);
			   col.setMissingValues(missing);
               table.addColumn(col);
               } break;
               
            case ColumnExpression.TYPE_DOUBLE:{
               double[] data = (double[])evaluation;
			   DoubleColumn col = new DoubleColumn(data);
			   col.setMissingValues(missing);
			   table.addColumn(col);
              } break;
               
            case ColumnExpression.TYPE_FLOAT:{
               float[] data = (float[])evaluation;
			   FloatColumn col = new FloatColumn(data);
			   col.setMissingValues(missing);
			   table.addColumn(col);
               } break;
               
            case ColumnExpression.TYPE_INTEGER:{
               int[] data = (int[])evaluation;
			   IntColumn col = new IntColumn(data);
			   col.setMissingValues(missing);
			   table.addColumn(col);
               } break;
               
            case ColumnExpression.TYPE_LONG:{
               long[] data = (long[])evaluation;
			   LongColumn col = new LongColumn(data);
			   col.setMissingValues(missing);
			   table.addColumn(col);
               } break;
               
            case ColumnExpression.TYPE_SHORT:{
               short[] data = (short[])evaluation;
			   ShortColumn col = new ShortColumn(data);
			   col.setMissingValues(missing);
			   table.addColumn(col);
               } break;
               
            case ColumnExpression.TYPE_STRING:{
               String[] data = (String[]) evaluation;
			   StringColumn col = new StringColumn(data);
			   col.setMissingValues(missing);
			   table.addColumn(col);
           	   } break;

            default:
               return false;
         }

         table.setColumnLabel(current.label, table.getNumColumns() - 1);

      }

      return true;
   }
}
