package objects;
import gmaths.*;
import helpers.Camera;
import helpers.Light;
import material.Material;
import material.Shader;
import scenegraph.*;
import shapes.Cube;
import shapes.Mesh;
import shapes.Model;
import shapes.Sphere;

import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;
import com.jogamp.opengl.util.texture.*;
import com.jogamp.opengl.util.texture.awt.*;
import com.jogamp.opengl.util.texture.spi.JPEGImage;

 /**
 * This class stores the Dancing Robot.
 * Modified from Robot.java by Dr Steve Maddock.
 *
 * @author    Lucy Lau
 */

public class DancingRobot {

  private Camera camera;
  private Light[] lights;

  private Model sphere, headSphere;

  private SGNode robotRoot;
  private float xPosition = -4;
  private float zPosition = -(float)16/3;
  private TransformNode robotMoveTranslate;

  // animation
  private TransformNode footRotate, lowerBodyRotate, leftArmRotate, rightArmRotate, headRotate;

  private float footRotationAngle = 30, footRotationAngleStart = footRotationAngle;
  private float lowerBodyRotationAngle = 30, lowerBodyRotationAngleStart = lowerBodyRotationAngle;
  private float headRotationAngle = 0;

  private float leftArmRotationAngle = 60, leftArmRotationAngleStart = leftArmRotationAngle;
  private float rightArmRotationAngle = 60, rightArmRotationAngleStart = rightArmRotationAngle;

  private float footRotationSpeed = 2;
  private float lowerBodyRotationSpeed = 2;
  private float armSpeed = 5;
  private float headSpeed = 60;
   
  public DancingRobot(GL3 gl, Camera cameraIn, Light[] lightIn, Texture headTexture) {

    this.camera = cameraIn;
    this.lights = lightIn;

    sphere = makeSphere(gl);
    headSphere = makeHeadSphere(gl, headTexture);

    // robot

    float lowerBodyWidth = 1f;
    float lowerBodyHeight = 1f;

    float footWidth = 0.5f;
    float footHeight = 1f;

    float bodyHeight = 2.5f;
    float bodyWidth = 2f;

    float headHeight = 1.7f;
    float headWidth = 2f;
    float headDepth = 2f;

    float armLength = 2.5f;
    float armScale = 0.5f;

    float earScale = 0.5f;
    
    robotRoot = new NameNode("root");
    robotMoveTranslate = new TransformNode("robot transform",Mat4Transform.translate(xPosition,0,zPosition));
    
    // make pieces
    NameNode foot = makeFoot(gl, footWidth, footHeight, sphere);
    NameNode lowerBody = makeLowerBody(gl, lowerBodyHeight, lowerBodyWidth, footHeight, sphere);
    NameNode body = makeBody(gl, bodyWidth, bodyHeight, footHeight+lowerBodyHeight, sphere);
    NameNode head = makeHead(gl, headHeight, headWidth, headDepth, headSphere);
    NameNode leftArm = makeLeftArm(gl, bodyWidth, armLength, armScale, sphere);
    NameNode rightArm = makeRightArm(gl, bodyWidth, armLength, armScale, sphere);
    NameNode leftEar = makeLeftEar(gl, earScale, headWidth, footHeight+lowerBodyHeight+bodyHeight+headHeight, sphere);
    NameNode rightEar = makeRightEar(gl, earScale, headWidth, footHeight+lowerBodyHeight+bodyHeight+headHeight, sphere);

    // translate each piece to build robot
    TransformNode translateLowerBody = new TransformNode("translate lower body", Mat4Transform.translate(0, footHeight, 0));
    TransformNode translateBody = new TransformNode("translate body", Mat4Transform.translate(0, lowerBodyHeight, 0));
    TransformNode translateHead = translateToTop("head", bodyHeight);
    TransformNode translateLeftArm = new TransformNode("translate left arm", Mat4Transform.translate(-bodyWidth/4, 0, 0));
    TransformNode translateRightArm = new TransformNode("translate right arm", Mat4Transform.translate(bodyWidth/4, 0, 0));;
    TransformNode translateLeftEar = translateToTop("left ear", headHeight);
    TransformNode translateRightEar = translateToTop("right ear", headHeight);

    // animation
    footRotate = new TransformNode("rotateAroundZ("+footRotationAngle+")", Mat4Transform.rotateAroundZ(0));
    lowerBodyRotate = new TransformNode("rotateAroundZ("+lowerBodyRotationAngle+")", Mat4Transform.rotateAroundZ(0));
    leftArmRotate = new TransformNode("rotateAroundZ("+leftArmRotationAngle+")", Mat4Transform.rotateAroundZ(120));
    rightArmRotate = new TransformNode("rotateAroundZ("+rightArmRotationAngle+")", Mat4Transform.rotateAroundZ(-120));
    headRotate = new TransformNode("rotateAroundY("+headRotationAngle+")", Mat4Transform.rotateAroundY(0));

    // create scene graph
    robotRoot.addChild(robotMoveTranslate);
      robotMoveTranslate.addChild(footRotate);
        footRotate.addChild(foot);

          // lower body
          foot.addChild(translateLowerBody);
            translateLowerBody.addChild(lowerBodyRotate);
              lowerBodyRotate.addChild(lowerBody);

            // body
            lowerBody.addChild(translateBody);
              translateBody.addChild(body);

              // head
              body.addChild(translateHead);
                translateHead.addChild(headRotate);
                  headRotate.addChild(head);

                // ears
                head.addChild(translateLeftEar);
                  translateLeftEar.addChild(leftEar);
                head.addChild(translateRightEar);
                  translateRightEar.addChild(rightEar);

              // arms
              head.addChild(translateLeftArm);
                translateLeftArm.addChild(leftArmRotate);
                  leftArmRotate.addChild(leftArm);
              head.addChild(translateRightArm);
                translateRightArm.addChild(rightArmRotate);
                  rightArmRotate.addChild(rightArm);
    
    robotRoot.update();  // IMPORTANT - don't forget this

  }

  // models

  private Model makeHeadSphere(GL3 gl, Texture t1) {
    String name= "sphere";
    Mesh mesh = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());
    Shader shader = new Shader(gl, "assets/shaders/vs_standard.txt", "assets/shaders/fs_standard_1t.txt");
    Material material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
    Mat4 modelMatrix = Mat4.multiply(Mat4Transform.scale(4,4,4), Mat4Transform.translate(0,0.5f,0));
    Model sphere = new Model(name, mesh, modelMatrix, shader, material, lights, camera, t1);
    return sphere;
  } 

  private Model makeSphere(GL3 gl) {
    String name= "sphere";
    Mesh mesh = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());
    Shader shader = new Shader(gl, "assets/shaders/vs_standard.txt", "assets/shaders/fs_standard_0t.txt");
    Material material = new Material(new Vec3(1.0f, 1.0f, 1.0f), new Vec3(0.9f, 1.0f, 0.95f), new Vec3(1.0f, 1.0f, 1.0f), 64.0f);
    Mat4 modelMatrix = Mat4.multiply(Mat4Transform.scale(4,4,4), Mat4Transform.translate(0,0.5f,0));
    Model sphere = new Model(name, mesh, modelMatrix, shader, material, lights, camera);
    return sphere;
  }

  // body pieces

  private NameNode makeFoot(GL3 gl, float footWidth, float footHeight, Model sphere) {
    NameNode foot = new NameNode("foot");

    Mat4 m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.scale(footWidth, footHeight, footWidth));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));

    TransformNode footTransform = new TransformNode("foot transform", m);
    ModelNode footShape = new ModelNode("Sphere(foot)", sphere);

    foot.addChild(footTransform);
    footTransform.addChild(footShape);

    return foot;
  }

  private TransformNode translateToTop(String name, float yOffset) {
    TransformNode translateToTopNode = new TransformNode("translate to top("+name+")", Mat4Transform.translate(0,yOffset,0));
    return translateToTopNode;
  }

  private NameNode makeLowerBody(GL3 gl, float bodyHeight, float bodyWidth, float footHeight, Model sphere) {
    NameNode lowerBody = new NameNode("lower body");

    Mat4 m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.scale(bodyWidth, bodyHeight, bodyWidth));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));

    TransformNode lowerBodyTransform = new TransformNode("lower body transform", m);
    ModelNode lowerBodyShape = new ModelNode("Sphere(lower body)", sphere);

    lowerBody.addChild(lowerBodyTransform);
    lowerBodyTransform.addChild(lowerBodyShape);
    
    return lowerBody;
  }

  private NameNode makeBody(GL3 gl, float bodyWidth, float bodyHeight, float yOffset, Model sphere) {
    NameNode body = new NameNode("body");

    Mat4 m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.scale(bodyWidth,bodyHeight,1));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));

    TransformNode bodyTransform = new TransformNode("body transform", m);
    ModelNode bodyShape = new ModelNode("Cube(body)", sphere);

    body.addChild(bodyTransform);
    bodyTransform.addChild(bodyShape);
    return body;
  }
    
  private NameNode makeHead(GL3 gl, float height, float width, float depth, Model sphere) {
    NameNode head = new NameNode("head"); 

    Mat4 m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.rotateAroundY(180));
    m = Mat4.multiply(m, Mat4Transform.scale(width, height, depth));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));

    TransformNode headTransform = new TransformNode("head transform", m);
    ModelNode headShape = new ModelNode("Sphere(head)", sphere);

    head.addChild(headTransform);
    headTransform.addChild(headShape);
    return head;
  }

  private NameNode makeLeftEar(GL3 gl, float earScale, float headWidth, float yOffset, Model sphere) {
    NameNode leftEar = new NameNode("left ear");

    Mat4 m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.translate(-headWidth / 4, 0, 0));
    m = Mat4.multiply(m, Mat4Transform.scale(earScale, earScale, earScale));

    TransformNode leftEarTransform = new TransformNode("left ear transform", m);
    ModelNode leftEarShape = new ModelNode("Sphere(left ear)", sphere);

    leftEar.addChild(leftEarTransform);
    leftEarTransform.addChild(leftEarShape);

    return leftEar;
  }

  private NameNode makeRightEar(GL3 gl, float earScale, float headWidth, float yOffset, Model sphere) {
    NameNode rightEar = new NameNode("right ear");

    Mat4 m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.translate(headWidth / 4, 0, 0));
    m = Mat4.multiply(m, Mat4Transform.scale(earScale, earScale, earScale));

    TransformNode rightEarTransform = new TransformNode("right ear transform", m);
    ModelNode rightEarShape = new ModelNode("Sphere(right ear)", sphere);

    rightEar.addChild(rightEarTransform);
    rightEarTransform.addChild(rightEarShape);

    return rightEar;
  }

  private NameNode makeLeftArm(GL3 gl, float bodyWidth, float armLength, float armScale, Model sphere) {
    NameNode leftArm = new NameNode("left arm");
    
    Mat4 m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.scale(armScale, armLength, armScale));
    m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));

    TransformNode leftArmTransform = new TransformNode("left arm transform", m);
    ModelNode leftArmShape = new ModelNode("Sphere(left arm)", sphere);

    leftArm.addChild(leftArmTransform);
    leftArmTransform.addChild(leftArmShape);

    return leftArm;
  }

  private NameNode makeRightArm(GL3 gl, float bodyWidth, float armLength, float armScale, Model sphere) {
    NameNode leftArm = new NameNode("right arm");
    
    Mat4 m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.scale(armScale, armLength, armScale));
    m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));

    TransformNode leftArmTransform = new TransformNode("right arm transform", m);
    ModelNode leftArmShape = new ModelNode("Sphere(right arm)", sphere);

    leftArm.addChild(leftArmTransform);
    leftArmTransform.addChild(leftArmShape);

    return leftArm;
  }

  public void render(GL3 gl) {
    robotRoot.draw(gl);
  }

  // animation

  public void updateDance(double elapsedTime) {
    footRotationAngle = footRotationAngleStart*(float)Math.sin(footRotationSpeed*elapsedTime);
    lowerBodyRotationAngle = lowerBodyRotationAngleStart*(float)Math.sin(lowerBodyRotationSpeed*elapsedTime + Math.toRadians(90));
    headRotationAngle = (float)elapsedTime*headSpeed;

    leftArmRotationAngle = leftArmRotationAngleStart + 10*(float)Math.sin(armSpeed*elapsedTime);
    rightArmRotationAngle = rightArmRotationAngleStart + 10*(float)Math.sin(armSpeed*elapsedTime);

    footRotate.setTransform(Mat4Transform.rotateAroundZ(footRotationAngle));
    lowerBodyRotate.setTransform(Mat4Transform.rotateAroundZ(lowerBodyRotationAngle));
    headRotate.setTransform(Mat4Transform.rotateAroundY(headRotationAngle));

    leftArmRotate.setTransform(Mat4Transform.rotateAroundZ(leftArmRotationAngle));
    rightArmRotate.setTransform(Mat4Transform.rotateAroundZ(-rightArmRotationAngle));

    robotRoot.update(); // IMPORTANT – the scene graph has changed
  }

  public void resetPosition() {
    footRotate.setTransform(Mat4Transform.rotateAroundZ(0));
    lowerBodyRotate.setTransform(Mat4Transform.rotateAroundZ(0));
    headRotate.setTransform(Mat4Transform.rotateAroundY(0));

    leftArmRotate.setTransform(Mat4Transform.rotateAroundZ(120));
    rightArmRotate.setTransform(Mat4Transform.rotateAroundZ(-120));

    robotRoot.update(); // IMPORTANT – the scene graph has changed
  }

  public Vec3 getPosition() {
    return new Vec3(xPosition, 0, zPosition);
  }

  public void dispose(GL3 gl) {
    sphere.dispose(gl);
  }
}