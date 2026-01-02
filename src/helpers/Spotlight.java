package helpers;

import gmaths.*;

import com.jogamp.opengl.*;

import material.Material;

/**
 * Subclass of Light to create a Spotlight.
 * 
 * @author  Lucy Lau
 */

public class Spotlight extends Light {

  private Vec3 direction;
  private float cutOff;
  private float intensity;

  public Spotlight(GL3 gl, Vec3 direction, float cutOffDegrees, float intensity) {
    super(gl);
    this.direction = direction;
    this.cutOff = (float)Math.toRadians(cutOffDegrees);
    this.intensity = intensity;
    this.setMaterial(new Material(new Vec3(0, 0, 0), new Vec3(0, 0, 0), new Vec3(0, 0, 0), 0));
  }

  public Vec3 getDirection() {
    return direction;
  }

  public void setDirection(Vec3 d) {
    direction = d;
  } 

  public float getCutOff() {
    return cutOff;
  }

  public float getIntensity() {
    return intensity;
  }

  public void setIntensity(float i) {
    intensity = i;
  }

}
