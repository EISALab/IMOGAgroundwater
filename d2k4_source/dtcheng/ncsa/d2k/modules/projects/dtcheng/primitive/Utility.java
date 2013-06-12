package ncsa.d2k.modules.projects.dtcheng.primitive;
import java.util.Random;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.core.datatype.table.*;

public class Utility
  {

    /*****************************************************************************/
    /* This function returns a random integer between min and max (both          */
    /* inclusive).                                                               */
    /*****************************************************************************/

    static public int randomInt(Random generator, int min, int max) {
      return (int) ( (generator.nextDouble() * (max - min + 1)) + min);
    }

    static public void randomizeIntArray(Random generator, int[] data, int numElements) throws Exception {
      int temp, rand_index;

      for (int i = 0; i < numElements - 1; i++) {
        rand_index = randomInt(generator, i + 1, numElements - 1);
        temp = data[i];
        data[i] = data[rand_index];
        data[rand_index] = temp;
      }
    }

    static public int unsignedByteToInt(byte byte1) {
      if (byte1 >= 0)
        return (int) byte1;
      else
        return (int) 256 + (int) byte1;
    }


    static public int audioBytesToInt(byte byte1, byte byte2) {
      int unsignedInt = unsignedByteToInt(byte2) * 256 + unsignedByteToInt(byte1);
      if (unsignedInt >= 32768)
        return unsignedInt - 65536;
      else
        return unsignedInt;
    }

    static public String[] parseCSVList(String list) {

      int length = list.length();

      if (length == 0) {
        return new String[0];
      }

      // count number of commas
      int numCommas = 0;
      for (int i = 0; i < length; i++) {
        if (list.charAt(i) == ',') {
          numCommas++;
        }
      }
      int numStrings = numCommas + 1;
      String[] strings = new String[numStrings];

      // parse gene names into strings
      int stringIndex = 0;
      int lastCharIndex = 0;
      for (int i = 0; i < length; i++) {
        if (list.charAt(i) == ',') {
          strings[stringIndex++] = list.substring(lastCharIndex, i);
          lastCharIndex = i + 1;
        }
      }
      strings[stringIndex++] = list.substring(lastCharIndex, length);

      return strings;
    }

    static public int stringToIndex(String[] strings, String string) {

      int numStrings = strings.length;
      int index = -1;

      for (int i = 0; i < numStrings; i++) {
        if (strings[i].equals(string)) {
          index = i;
          break;
        }
      }

      return index;
    }


    /**
     * Given a two d array of doubles, create a table.
     * @param data
     * @return
     */
    static public ExampleTable getTable(double[][] data, String[] inputNames, String[] outputNames, int[] inputs, int[] outputs, int count) {
      Column[] cols = new Column[data.length];
      int index = 0;
      for (int i = 0; i < inputs.length; i++, index++) {
        if (data.length != count) {
          double[] tmp = new double[count];
          System.arraycopy(data[index], 0, tmp, 0, count);
          data[index] = tmp;
        }
        cols[index] = new DoubleColumn(data[index]);
        cols[index].setLabel(inputNames[i]);
      }
      for (int i = 0; i < outputs.length; i++, index++) {
        if (data.length != count) {
          double[] tmp = new double[count];
          System.arraycopy(data[index], 0, tmp, 0, count);
          data[index] = tmp;
        }
        cols[index] = new DoubleColumn(data[index]);
        cols[index].setLabel(outputNames[i]);
      }
      MutableTable mt = new MutableTableImpl(cols);
      ExampleTable et = mt.toExampleTable();
      et.setInputFeatures(inputs);
      et.setOutputFeatures(outputs);
      return et;
    }



  }

