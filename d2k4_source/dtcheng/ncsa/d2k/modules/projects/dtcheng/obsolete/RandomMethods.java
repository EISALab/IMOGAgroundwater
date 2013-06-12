package ncsa.d2k.modules.projects.dtcheng.obsolete;
import ncsa.d2k.modules.projects.dtcheng.*;
import java.util.Random;

public class RandomMethods
  {
  /*****************************************************************************/
  /* This function returns a random integer between min and max (both          */
  /* inclusive).                                                               */
  /*****************************************************************************/

  public static int randomInt(Random randomNumberGenerator, int min, int max)
    {
    return (int) ((randomNumberGenerator.nextDouble() * (max - min + 1)) + min);
    }

  public static void randomizeIntArray(Random randomNumberGenerator, int [] data, int numElements)
    {
    int temp, rand_index;

    for (int i = 0; i < numElements - 1; i++)
      {
      rand_index       = randomInt(randomNumberGenerator, i + 1, numElements - 1);
      temp             = data[i];
      data[i]          = data[rand_index];
      data[rand_index] = temp;
      }
    }

  public static void randomizeIntArray(Random randomNumberGenerator, int [] data)
    {
    randomizeIntArray(randomNumberGenerator, data, data.length);
    }


  }

