package ncsa.d2k.modules.core.datatype.table.transformations;

/**
 * <code>AttributeConstruction.Construction</code> is a simple inner class
 * used to store the label and contents of an expression.
 */
public class Construction implements java.io.Serializable {

   public String label, expression;

   public Construction(String label, String expression) {
      this.label = label;
      this.expression = expression;
   }

   public String toString() {
      return label + ": " + expression;
   }

}
