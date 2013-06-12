package ncsa.d2k.modules.core.datatype.sort;

/*
This function sorts a 2-d double array by row based on the first column value.
*/
public class QuickSort
  {

  public static double[][] sort(double[][] A)
    {
    return doSort(A, 0, A.length-1);
    }

  private static double [][] doSort(double[][] A, int p, int r)
    {
    if (p < r)
      {
      int q = partition(A, p, r);
      doSort(A, p, q);
      doSort(A, q+1, r);
      }
    return A;
    }

  private static int partition(double[][] A, int p, int r)
    {
    double x = A[p][0];

    int i = p-1;
    int j = r+1;

    while(true)
      {
      do
        {
        j--;
        }
      while(A[j][0] > x);

      do
        {
        i++;
        }
      while(A[i][0] < x);

      if(i < j)
        {
        double [] temp = A[i];
        A[i] = A[j];
        A[j] = temp;
        }
      else
        return j;
      }
    }
}
