package helpers;

/**
 * Class for dealing with timers.
 * 
 * @author  Lucy Lau
 */

public class TimeHelper {

  private double startTime;
  private double savedTime = 0;

  public TimeHelper() {
    startTime = getSeconds();
  }

  public static double getSeconds() {
    return System.currentTimeMillis()/1000.0;
  }

  public double getElapsedTime() {
    return getSeconds() - startTime;
  }

  public double getSavedTime() {
    return savedTime;
  }

  public void setStartTime(double startTime) {
    this.startTime = startTime;
  }

  public void setSavedTime(double savedTime) {
    this.savedTime = savedTime;
  }
}
