package ncsa.d2k.modules.core.datatype.table.util;

/**
 * A simple structure to hold statistics about a scalar column.
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: </p>
 * @author clutter
 * @version 1.0
 */
public class ScalarStatistics {

  double mean;
  double median;
  double variance;
  double standardDeviation;
  double firstquartile;
  double thirdquartile;
  double minimum;
  double maximum;

  /**
   * Construct a new ScalarStatistics
   * @param ave
   * @param med
   * @param var
   * @param sd
   * @param min
   * @param max
   */
  ScalarStatistics(double ave, double med, double var, double sd, double min, double max) {
    mean = ave;
    median = med;
    variance = var;
    standardDeviation = sd;
    minimum = min;
    maximum = max;
  }

  /**
   * Construct a new ScalarStatistics
   * @param ave
   * @param med
   * @param var
   * @param sd
   * @param min
   * @param max
   * @param fq
   * @param tq
   */
  ScalarStatistics(double ave, double med, double var, double sd, double min, double max, double fq, double tq) {
    this(ave, med, var, sd, min, max);

    firstquartile = fq;
    thirdquartile = tq;
  }

  /**
   * Get the mean.
   * @return the mean
   */
  public double getMean() {
    return mean;
  }

  /**
   * Get the median.
   * @return the median
   */
  public double getMedian() {
    return median;
  }

  /**
   * Get the variance.
   * @return the variance
   */
  public double getVariance() {
    return variance;
  }

  /**
   * Get the standard deviation
   * @return the standard deviation
   */
  public double getStandardDeviation() {
    return standardDeviation;
  }

  /**
   * Get the minimum.
   * @return the minimum
   */
  public double getMinimum() {
    return minimum;
  }

  /**
   * Get the maximum
   * @return the maximum
   */
  public double getMaximum() {
    return maximum;
  }

  /**
   * Get the first quartile
   * @return the first quartile
   */
  public double getFirstQuartile() {
    return firstquartile;
  }

  /**
   * Get the third quartile
   * @return the third quartile
   */
  public double getThirdQuartile() {
    return thirdquartile;
  }
}