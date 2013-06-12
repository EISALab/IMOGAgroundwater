package ncsa.d2k.modules.core.transform.attribute;

import java.util.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.transformations.*;

/**
 * This module outputs a (reversible) transformation on a
 * <code>MutableTable</code> that replaces unique nominal column values with
 * unique integers.
 */
public class ReplaceNominalsWithInts extends ComputeModule {

   private boolean _printMapping = false;
   public boolean getPrintMapping() { return _printMapping; }
   public void setPrintMapping(boolean value) { _printMapping = value; }

   public void doit() {

      MutableTable mt = (MutableTable)pullInput(0);

      ReplaceNominalsWithIntsTransform transform =
            new ReplaceNominalsWithIntsTransform(mt);

      if (_printMapping)
         System.out.println(transform.toMappingString(mt));

      pushOutput(transform, 0);

   }

   public String getInputInfo(int index) {
      if (index == 0)
         return "The <i>MutableTable</i> to be used in constructing the " +
                "transformation.";
      return null;
   }

   public String getInputName(int index) {
      if (index == 0)
         return "Mutable Table";
      return null;
   }

   public String[] getInputTypes() {
      return new String[] {
         "ncsa.d2k.modules.core.datatype.table.MutableTable"
      };
   }

   public String getModuleInfo() {
      StringBuffer sb = new StringBuffer("<p>Overview: ");
      sb.append("This module constructs a reversible transformation to ");
      sb.append("replace each unique value in every nominal column of a ");
      sb.append("<i>MutableTable</i> with an integer unique to that column.");

/*      sb.append("</P><P><u>Missing Values Handling:</u> This module handles missing values as if they were " +
         "real meaningful values. For Example: If a missing value in a string column " +
         "is represented by '?', then after applying the transformation, a unique integer will "+
         "be assigned to all the missing values");
 */

      sb.append("</P><P><u>Missing Values Handling:</u> This Transformation output by this module" +
                " preserves missing values. " +
           "The internal representation of a missing value will be converted to that of " +
           "and integer missing value, and the fact that the value was missing in the original" +
           "table is preserved in the resulting table.");

      sb.append("</p><p>Data Handling: ");
      sb.append("This module does not modify its input data. Rather, its ");
      sb.append("output is a <i>ReversibleTransformation</i> that can be ");
      sb.append("applied downstream.");
      sb.append("</p>");
      return sb.toString();
   }

   public String getModuleName() {
      return "Replace Nominals With Ints";
   }

   public String getOutputInfo(int index) {
      if (index == 0)
         return "The <i>ReversibleTransformation</i> that performs the " +
                "replacement of column values.";
      return null;
   }

   public String getOutputName(int index) {
      if (index == 0)
         return "Reversible Transformation";
      return null;
   }

   public String[] getOutputTypes() {
      return new String[] {
         "ncsa.d2k.modules.core.datatype.table.ReversibleTransformation"
      };
   }

   public PropertyDescription[] getPropertiesDescriptions() {

      PropertyDescription[] pds = new PropertyDescription[1];

      pds[0] = new PropertyDescription("printMapping",
         "Print Integer Mappings",
         "If enabled, the nominal-to-integer mappings for each column will " +
         "be printed to the console.");

      return pds;

   }

}

/**
 * QA comments:
 * 2-28-03  Vered started qa.
 *          added to module info a note about missing values handling.
 *
 * 11-25-03 Vered started qa:
 *          according to new guide lines - missing values should be preserved. thus
 *          changed module info. reported bug 144 on missing values preservation.
 * 12-05-03 bug 144 was fixed.
 *          module is ready for basic 4.
 *
 * 01-12-04: module is pulled back into qa process.
 * bug 219 - handling of subset tables. table viewer throws an array index out of
 * bounds exception with subset table that had this module's transformation
 * applied to. (fixed)
 *
 * 01-21-04: modules is ready for basic 4
 */
