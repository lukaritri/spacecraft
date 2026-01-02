package shapes;
import gmaths.*;
import helpers.Camera;
import helpers.Light;
import material.*;

import com.jogamp.opengl.*;
import com.jogamp.opengl.util.texture.*;

 /**
 * This class stores the Room. Extended from Floor.java
 * by Steve Maddock.
 *
 * @author    Lucy Lau
 */

public class Room {

  private TextureLibrary textures;

  private Camera camera;
  private Light[] lights;

  private Model floor, backWall, leftWall, rightWall, ceiling;
   
  public Room(GL3 gl, float xSize, float ySize, float zSize, Camera cameraIn, Light[] lightIn) {

    camera = cameraIn;
    lights = lightIn;

    textures = new TextureLibrary();
    textures.add(gl, "cat", "assets/textures/cat.jpg");
    textures.add(gl, "metal", "assets/textures/metal.png");
    textures.add(gl, "metal_floor", "assets\\textures\\metal_floor.png");
    textures.add(gl, "backwall", "assets\\textures\\metal_backwall.png");
    textures.add(gl, "backwall_specular", "assets\\textures\\metal_backwall_specular.png");

    Texture rightTexture = textures.get("cat");
    Texture metalTexture = textures.get("metal");
    Texture floorTexture = textures.get("metal_floor");

    String name = "floor";
    Mesh mesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
    Shader shader = new Shader(gl, "assets/shaders/vs_standard.txt", "assets/shaders/fs_standard_1t.txt");
    Material material = new Material(new Vec3(0.0f, 0.0f, 0.0f), new Vec3(0.3f, 0.3f, 0.3f), new Vec3(0.3f, 0.3f, 0.3f), 32.0f);
    Mat4 modelMatrix = Mat4Transform.scale(xSize,1f,zSize);
    floor = new Model(name, mesh, modelMatrix, shader, material, lights, camera, floorTexture);

    name = "backWall";
    mesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
    shader = new Shader(gl, "assets/shaders/vs_standard.txt", "assets/shaders/fs_standard_2t.txt");
    material = new Material(new Vec3(0.0f, 0.0f, 0.0f), new Vec3(0.3f, 0.3f, 0.3f), new Vec3(0.3f, 0.3f, 0.3f), 64.0f);
    modelMatrix = new Mat4(1);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(xSize,1f,ySize), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundX(90), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.translate(0,ySize*0.5f,-zSize*0.5f), modelMatrix);
    backWall = new Model(name, mesh, modelMatrix, shader, material, lights, camera, textures.get("backwall"), textures.get("backwall_specular"));

    name = "leftWall";
    mesh = new Mesh(gl, TwoTriangles.leftWallVertices.clone(), TwoTriangles.leftWallindices.clone());
    shader = new Shader(gl, "assets/shaders/vs_standard.txt", "assets/shaders/fs_standard_1t.txt");
    material = new Material(new Vec3(0.0f, 0.0f, 0.0f), new Vec3(0.0f, 0.0f, 0.0f), new Vec3(0.3f, 0.3f, 0.3f), 32.0f);
    modelMatrix = new Mat4(1);
    modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundY(-90), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(ySize,1f,zSize), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundZ(-90), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.translate(-xSize*0.5f,ySize*0.5f,0), modelMatrix);
    leftWall = new Model(name, mesh, modelMatrix, shader, material, lights, camera, metalTexture);

    name = "rightWall";
    mesh = new Mesh(gl, TwoTriangles.rightWallVertices.clone(), TwoTriangles.indices.clone());
    shader = new Shader(gl, "assets/shaders/vs_standard.txt", "assets/shaders/fs_standard_1t.txt");
    material = new Material(new Vec3(0.0f, 0.0f, 0.0f), new Vec3(0.0f, 0.0f, 0.0f), new Vec3(0.3f, 0.3f, 0.3f), 16.0f);
    modelMatrix = new Mat4(1);
    modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundY(-90), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(ySize,1f,zSize), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundZ(90), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.translate(xSize*0.5f,ySize*0.5f,0), modelMatrix);
    rightWall = new Model(name, mesh, modelMatrix, shader, material, lights, camera, rightTexture);

    name = "ceiling";
    mesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
    shader = new Shader(gl, "assets/shaders/vs_standard.txt", "assets/shaders/fs_standard_1t.txt");
    material = new Material(new Vec3(0.0f, 0.0f, 0.0f), new Vec3(0.0f, 0.0f, 0.0f), new Vec3(0.3f, 0.3f, 0.3f), 32.0f);
    modelMatrix = new Mat4(1);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(xSize,1f,zSize), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundZ(180), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.translate(0, ySize, 0), modelMatrix);
    ceiling = new Model(name, mesh, modelMatrix, shader, material, lights, camera, metalTexture);

  }

  public void render(GL3 gl) {
    floor.render(gl);
    backWall.render(gl);
    leftWall.render(gl);
    rightWall.render(gl);
    ceiling.render(gl);
  }

}