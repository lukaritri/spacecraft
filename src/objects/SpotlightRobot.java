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
import shapes.TwoTriangles;

import com.jogamp.opengl.*;
import com.jogamp.opengl.util.texture.*;

 /**
 * This class stores the Spotlight Robot.
 * Modified from Robot.java by Dr Steve Maddock.
 *
 * @author    Lucy Lau
 */

public class SpotlightRobot {

  private Camera camera;
  private Light light;

  // spotlight

  private Spotlight spotlight;
  private float spotlightTilt = 45;
  public static float SPOTLIGHT_INTENSITY = 0.8f;

  private Model sphere, sphere2, cube, plane;

  private SGNode robotRoot;
  private TransformNode robotMoveTranslate;

  // dimensions
  private float bodyLength = 2f;
  private float bodyHeight = 1.5f;
  private float antennaHeight = 2f;
  private float casingSize = 0.3f;
  private float bulbHeight = casingSize / 2;
  private float bulbWidth = casingSize;

  // path
  private Vec3 startPos = new Vec3(8, 0, (float)32/3); // update robot's position
  private float currDir = 0; // update robot's direction
  private float velocity = 2; // units per second
  private TransformNode facingDirection;
  private Vec3 currPos = startPos;

  // rotation
  private float angularVelocity = 60; // degrees/s
  private float rotationAngle = 30;
  private TransformNode rotation;

  public SpotlightRobot(GL3 gl, Camera cameraIn, Light lightIn, Texture bodyText, Texture bodyTextSpec, Texture faceText, Texture faceTextSpec, Texture antennaText, Texture antennaTextSpec) {
    this.camera = cameraIn;
    this.light = lightIn;
    this.spotlight = new Spotlight(gl, getSpotlightDirection(), 15, SPOTLIGHT_INTENSITY);
    spotlight.setPosition(getSpotlightPosition());
    spotlight.setCamera(cameraIn);

    sphere = makeSphere(gl, antennaText, antennaTextSpec);
    sphere2 = makeSphere(gl);
    cube = makeCube(gl, bodyText, bodyTextSpec);
    plane = makePlane(gl, faceText, faceTextSpec);

    // root node + initial position node
    robotRoot = new NameNode("root");
    robotMoveTranslate = new TransformNode("robot transform",Mat4Transform.translate(startPos));

    // body parts
    NameNode body = makeBody(gl, bodyHeight, bodyLength, cube, plane);
    NameNode antenna = makeAntenna(gl, antennaHeight, sphere);
    NameNode casing = makeCasing(gl, casingSize, sphere);
    NameNode bulb = makeBulb(gl, bulbWidth, bulbHeight, sphere2);

    // translations
    TransformNode antennaTranslate = new TransformNode("antenna translate", Mat4Transform.translate(0, bodyHeight, 0));
    TransformNode casingTranslate = new TransformNode("casing translate", Mat4Transform.translate(0, antennaHeight, 0));
    TransformNode bulbTranslate = new TransformNode("bulb translate", Mat4Transform.translate(casingSize/2, 0, 0));

    // spotlight

    TransformNode spotlightTiltNode = new TransformNode("spotlight tilt", Mat4Transform.rotateAroundZ(-spotlightTilt));

    // rotation
    rotation = new TransformNode("spotlight rotation", Mat4Transform.rotateAroundY(rotationAngle));

    // facing direction
    facingDirection = new TransformNode("facing direction", Mat4Transform.rotateAroundY(-currDir));

    // scene graph
    robotRoot.addChild(robotMoveTranslate);
        robotMoveTranslate.addChild(facingDirection);
          facingDirection.addChild(body);
        robotMoveTranslate.addChild(antennaTranslate);
          antennaTranslate.addChild(antenna);
            antenna.addChild(casingTranslate);
              casingTranslate.addChild(rotation);
                rotation.addChild(spotlightTiltNode);
                  spotlightTiltNode.addChild(casing);
                    casing.addChild(bulbTranslate);
                      bulbTranslate.addChild(bulb);

    robotRoot.update();
  }

  // shapes

  private Model makeSphere(GL3 gl, Texture t1, Texture t2) {
    String name= "sphere";
    Mesh mesh = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());
    Shader shader = new Shader(gl, "assets/shaders/vs_standard.txt", "assets/shaders/fs_standard_2t.txt");
    Material material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
    Mat4 modelMatrix = Mat4.multiply(Mat4Transform.scale(4,4,4), Mat4Transform.translate(0,0.5f,0));
    Model sphere = new Model(name, mesh, modelMatrix, shader, material, light, camera, t1, t2);
    return sphere;
  } 

  private Model makeSphere(GL3 gl) {
    String name= "sphere";
    Mesh mesh = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());
    Shader shader = new Shader(gl, "assets/shaders/vs_standard.txt", "assets/shaders/fs_light_01.txt");
    Material material = new Material();
    Mat4 modelMatrix = Mat4.multiply(Mat4Transform.scale(4,4,4), Mat4Transform.translate(0,0.5f,0));
    Model sphere = new Model(name, mesh, modelMatrix, shader, material, light, camera);
    return sphere;
  } 

  private Model makeCube(GL3 gl, Texture t1, Texture t2) {
    String name= "cube";
    Mesh mesh = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
    Shader shader = new Shader(gl, "assets/shaders/vs_standard.txt", "assets/shaders/fs_standard_2t.txt");
    Material material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
    Mat4 modelMatrix = Mat4.multiply(Mat4Transform.scale(4,4,4), Mat4Transform.translate(0,0.5f,0));
    Model cube = new Model(name, mesh, modelMatrix, shader, material, light, camera, t1, t2);
    return cube;
  } 

  private Model makePlane(GL3 gl, Texture t1, Texture t2) {
    String name = "plane";
    Mesh mesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
    Shader shader = new Shader(gl, "assets/shaders/vs_standard.txt", "assets/shaders/fs_standard_2t.txt");
    Material material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
    Mat4 modelMatrix = Mat4Transform.scale(4,4,4);
    Model plane = new Model(name, mesh, modelMatrix, shader, material, light, camera, t1, t2);
    return plane;
  }

  // body parts

  private NameNode makeBody(GL3 gl, float width, float length, Model cube, Model plane) {
    NameNode body = new NameNode("body");

    Mat4 m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.scale(length, width, width));
    m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));

    TransformNode bodyTransform = new TransformNode("body transform", m);
    ModelNode bodyModel = new ModelNode("Cube(body)", cube);

    // add face

    NameNode face = new NameNode("face");
    m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.translate(-length/4, 0, 0));
    m = Mat4.multiply(m, Mat4Transform.rotateAroundZ(90));
    m = Mat4.multiply(m, Mat4Transform.rotateAroundY(-90));

    TransformNode faceTransform = new TransformNode("face transform", m);
    ModelNode faceModel = new ModelNode("Plane(face)", plane);

    body.addChild(bodyTransform);
      bodyTransform.addChild(face);
        face.addChild(faceTransform);
          faceTransform.addChild(faceModel);
        face.addChild(bodyModel);

    return body;
  }

  private NameNode makeAntenna(GL3 gl, float height, Model sphere) {
    NameNode antenna = new NameNode("antenna");

    Mat4 m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.scale(0.15f, height, 0.15f));
    m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));

    TransformNode antennaTransform = new TransformNode("antenna transform", m);
    ModelNode antennaModel = new ModelNode("Sphere(antenna)", sphere);

    antenna.addChild(antennaTransform);
    antennaTransform.addChild(antennaModel);

    return antenna;

  }

  private NameNode makeCasing(GL3 gl, float size, Model sphere){
    NameNode casing = new NameNode("casing");

    Mat4 m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.scale(size, size, size));

    TransformNode casingTransform = new TransformNode("casing transform", m);
    ModelNode casingModel = new ModelNode("Sphere(casing)", sphere);

    casing.addChild(casingTransform);
    casingTransform.addChild(casingModel);

    return casing;
  }

  private NameNode makeBulb(GL3 gl, float width, float height, Model sphere) {
    NameNode bulb = new NameNode("bulb");

    Mat4 m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.scale(width, height, height));

    TransformNode bulbTransform = new TransformNode("bulb transform", m);
    ModelNode bulbModel = new ModelNode("Sphere(bulb)", sphere);

    bulb.addChild(bulbTransform);
    bulbTransform.addChild(bulbModel);

    return bulb;
  }

  // rotation

  public void updateRotation(double elapsedTime) {
    rotationAngle = angularVelocity * (float)elapsedTime;

    rotation.setTransform(Mat4Transform.rotateAroundY(rotationAngle));
    robotRoot.update(); // IMPORTANT – the scene graph has changed
  }

  public Vec3 getSpotlightDirection() {
    float theta = (float)Math.toRadians(spotlightTilt);
    float phi = (float)Math.toRadians(rotationAngle);

    double x = Math.cos(theta) * Math.cos(phi);
    double y = -Math.sin(theta);
    double z = -Math.cos(theta) * Math.sin(phi);

    return Vec3.normalize(new Vec3((float)x, (float)y, (float)z));
  }

  // path

  public void updatePosition(TimeHelper timeHelper) {
    double elapsedTime = timeHelper.getElapsedTime();
    float distance = velocity * (float)elapsedTime;

    if (currDir == 0) {
      currPos = new Vec3(startPos.x-distance, startPos.y, startPos.z);
      if (currPos.x <= -8) { // change direction if reach a corner
        startPos = new Vec3(-8f, 0, (float)32/3);
        currPos = startPos;
        timeHelper.setStartTime(TimeHelper.getSeconds());
        currDir = 90; // rotate
        facingDirection.setTransform(Mat4Transform.rotateAroundY(-currDir));
      }
    } else if (currDir == 90) {
      currPos = new Vec3(startPos.x, startPos.y, startPos.z-distance);
      if (currPos.z <= -(float)32/3) { // change direction if reach a corner
        startPos = new Vec3(-8f, 0, -(float)32/3);
        currPos = startPos;
        timeHelper.setStartTime(TimeHelper.getSeconds());
        currDir = 180; // rotate
        facingDirection.setTransform(Mat4Transform.rotateAroundY(-currDir));
      }
    } else if (currDir == 180) {
      currPos = new Vec3(startPos.x+distance, startPos.y, startPos.z);
      if (currPos.x >= 8) { // change direction if reach a corner
        startPos = new Vec3(8f, 0, -(float)32/3);
        currPos = startPos;
        timeHelper.setStartTime(TimeHelper.getSeconds());
        currDir = 270; // rotate
        facingDirection.setTransform(Mat4Transform.rotateAroundY(-currDir));
      }
    } else {
      currPos = new Vec3(startPos.x, startPos.y, startPos.z+distance);
      if (currPos.z >= (float)32/3) { // change direction if reach a corner
        startPos = new Vec3(8f, 0, (float)32/3);
        currPos = startPos;
        timeHelper.setStartTime(TimeHelper.getSeconds());
        currDir = 0; // rotate
        facingDirection.setTransform(Mat4Transform.rotateAroundY(-currDir));
      }
    }

    robotMoveTranslate.setTransform(Mat4Transform.translate(currPos));
    robotRoot.update();
  }

  public Vec3 getPosition() {
    return currPos;
  }

  public Vec3 getSpotlightPosition() {
    return new Vec3(currPos.x, bodyHeight+antennaHeight, currPos.z);
  }

  // render

  public void render(GL3 gl) {
    robotRoot.draw(gl);
  }

  // get spotlight

  public Spotlight getSpotight() {
    return spotlight;
  }
  
}
