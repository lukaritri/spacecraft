package objects;
import gmaths.*;
import helpers.*;
import material.Material;
import material.Shader;
import scenegraph.*;
import shapes.Cube;
import shapes.Mesh;
import shapes.Model;
import shapes.Sphere;

import com.jogamp.opengl.*;
import com.jogamp.opengl.util.texture.*;

 /**
 * This class stores the rotating globe.
 *
 * @author    Lucy Lau
 */

public class Globe {

  private Camera camera;
  private Light[] lights;

  private double startTime;

  private Model axisSphere, globeSphere, cube;

  private SGNode globeRoot;
  private float xPosition = 4;
  private float zPosition = (float)16/3;
  private TransformNode globeMoveTranslate;

  // rotation
  private float angularVelocity = 60; // degrees/s
  private float rotationAngle = 0;
  private TransformNode rotation;
  private TimeHelper timeHelper;

  public Globe(GL3 gl, Camera cameraIn, Light[] lightIn, Texture boxTexture, Texture boxSpec, Texture axisTexture, Texture axisSpec, Texture globeTexture, Texture globeSpec) {
    this.camera = cameraIn;
    this.lights = lightIn;
    timeHelper = new TimeHelper();

    axisSphere = makeSphere(gl, axisTexture, axisSpec);
    globeSphere = makeSphere(gl, globeTexture, globeSpec);
    cube = makeCube(gl, boxTexture, boxSpec);

    // measurements
    float boxSize = 2f;
    float axisHeight = 3f;
    float globeSize = 2f;

    // root + translate nodes
    globeRoot = new NameNode("globe root");
    globeMoveTranslate = new TransformNode("globe transform", Mat4Transform.translate(xPosition, 0, zPosition));

    TransformNode axisTranslate = new TransformNode("axis translate", Mat4Transform.translate(0, boxSize, 0));
    TransformNode globeTranslate = new TransformNode("globe translate", Mat4Transform.translate(0, axisHeight/2, 0));

    // make pieces
    NameNode box = makeBox(gl, boxSize, cube);
    NameNode axis = makeAxis(gl, axisHeight, axisSphere);
    NameNode globe = makeGlobe(gl, globeSize, globeSphere);

    // rotation
    rotation = new TransformNode("globe rotation", Mat4Transform.rotateAroundY(rotationAngle));

    globeRoot.addChild(globeMoveTranslate);
      globeMoveTranslate.addChild(box);
        box.addChild(axisTranslate);
          axisTranslate.addChild(axis);
            axis.addChild(globeTranslate);
              globeTranslate.addChild(rotation);
                rotation.addChild(globe);

    globeRoot.update();
  }

  // models
  private Model makeSphere(GL3 gl, Texture t1, Texture t2) {
    String name= "sphere";
    Mesh mesh = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());
    Shader shader = new Shader(gl, "assets/shaders/vs_standard.txt", "assets/shaders/fs_standard_2t.txt");
    Material material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
    Mat4 modelMatrix = Mat4.multiply(Mat4Transform.scale(4,4,4), Mat4Transform.translate(0,0.5f,0));
    Model sphere = new Model(name, mesh, modelMatrix, shader, material, lights, camera, t1, t2);
    return sphere;
  } 

  private Model makeCube(GL3 gl, Texture t1, Texture t2) {
    String name= "cube";
    Mesh mesh = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
    Shader shader = new Shader(gl, "assets/shaders/vs_standard.txt", "assets/shaders/fs_standard_2t.txt");
    Material material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
    Mat4 modelMatrix = Mat4.multiply(Mat4Transform.scale(4,4,4), Mat4Transform.translate(0,0.5f,0));
    Model cube = new Model(name, mesh, modelMatrix, shader, material, lights, camera, t1, t2);
    return cube;
  } 

  // pieces
  private NameNode makeBox(GL3 gl, float size, Model cube) {
    NameNode box = new NameNode("box");

    Mat4 m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.scale(size, size, size));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));

    TransformNode boxTransform = new TransformNode("box transform", m);
    ModelNode boxModel = new ModelNode("Cube(box)", cube);

    box.addChild(boxTransform);
    boxTransform.addChild(boxModel);

    return box;
  }

  private NameNode makeAxis(GL3 gl, float height, Model sphere) {
    NameNode axis = new NameNode("axis");

    Mat4 m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.scale(0.2f, height, 0.2f));
    m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));

    TransformNode axisTransform = new TransformNode("axis transform", m);
    ModelNode axisModel = new ModelNode("Sphere(axis)", sphere);

    axis.addChild(axisTransform);
    axisTransform.addChild(axisModel);

    return axis;
  }

  private NameNode makeGlobe(GL3 gl, float size, Model sphere) {
    NameNode globe = new NameNode("globe");

    Mat4 m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.scale(size, size, size));
    // m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));

    TransformNode globeTransform = new TransformNode("globe transform", m);
    ModelNode globeModel = new ModelNode("Sphere(globe)", sphere);

    globe.addChild(globeTransform);
    globeTransform.addChild(globeModel);

    return globe;
  }

  // rotation

  private void updateRotation() {
    rotationAngle = angularVelocity * (float)timeHelper.getElapsedTime();

    rotation.setTransform(Mat4Transform.rotateAroundY(rotationAngle));
    globeRoot.update(); // IMPORTANT – the scene graph has changed
  }

  // render

  public void render(GL3 gl) {
    updateRotation();
    globeRoot.draw(gl);
  }
    
}
