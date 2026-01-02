package shapes;
import gmaths.*;
import helpers.Camera;
import helpers.Light;
import material.Material;
import material.Shader;
import material.CubeMapHelper;

import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.texture.*;
import com.jogamp.opengl.util.texture.awt.*;
import com.jogamp.opengl.util.texture.spi.JPEGImage;

/*
 * Skybox class.
 * 
 * Lucy Lau
 */

public class SkyBox {

  private Model skybox;
  private Mat4 modelMatrix;
  
  // animation
  private float yVelocity = 0.5f;
  private float zVelocity = 0.5f;
  private float startAngle = 1f, zAngle = startAngle;
  private float yAngle;
  
  public SkyBox(GL3 gl, Light light, Camera camera, String[] cubeFaces) {

    CubeMapHelper cubeMapHelper = new CubeMapHelper(gl, cubeFaces);
    Texture cubeMap = cubeMapHelper.getCubeMap();

    String name= "skybox";
    Mesh mesh = new CubeMesh(gl, Cube.skyboxVertices.clone());
    Shader shader = new Shader(gl, "assets/shaders/vs_cube.txt", "assets/shaders/fs_cube.txt");
    Material material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0, 0, 0), new Vec3(0, 0, 0), 32.0f);
    modelMatrix = new Mat4(1);
    this.skybox = new Model(name, mesh, modelMatrix, shader, material, light, camera, cubeMap);
  }

  public void render(GL3 gl, double elapsedTime) {
    Mat4 newModel = Mat4.multiply(modelMatrix, updateAnimation(gl, elapsedTime));
    skybox.renderCube(gl, newModel);
  }

  public Mat4 updateAnimation(GL3 gl, double elapsedTime) {
    yAngle = (float)elapsedTime*yVelocity;
    zAngle = startAngle * (float)Math.sin(elapsedTime*zVelocity);
    // System.out.println(zAngle);

    Mat4 m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.rotateAroundY(yAngle));
    m = Mat4.multiply(m, Mat4Transform.rotateAroundZ(zAngle));

    return m;
  }

}
