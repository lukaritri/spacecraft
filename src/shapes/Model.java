package shapes;
import gmaths.*;
import helpers.Camera;
import helpers.Light;
import helpers.Spotlight;
import material.Material;
import material.Shader;

import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.texture.*;
import com.jogamp.opengl.util.texture.awt.*;
import com.jogamp.opengl.util.texture.spi.JPEGImage;

public class Model {

  private String name;
  private Mesh mesh;
  private Mat4 modelMatrix;
  private Shader shader;
  private Material material;
  private Camera camera;
  private Light[] lights;
  private Texture diffuse;
  private Texture specular;

  public Model() {
    name = null;
    mesh = null;
    modelMatrix = null;
    material = null;
    camera = null;
    lights = null;
    shader = null;
  }

  public Model(String name, Mesh mesh, Mat4 modelMatrix, Shader shader, Material material, Light[] lights,
    Camera camera, Texture diffuse, Texture specular) {
    this.name = name;
    this.mesh = mesh;
    this.modelMatrix = modelMatrix;
    this.shader = shader;
    this.material = material;
    this.lights = lights;
    this.camera = camera;
    this.diffuse = diffuse;
    this.specular = specular;
  }

  public Model(String name, Mesh mesh, Mat4 modelMatrix, Shader shader, Material material, Light light,
    Camera camera, Texture diffuse, Texture specular) {
    this.name = name;
    this.mesh = mesh;
    this.modelMatrix = modelMatrix;
    this.shader = shader;
    this.material = material;
    this.lights = new Light[]{light};
    this.camera = camera;
    this.diffuse = diffuse;
    this.specular = specular;
  }

  public Model(String name, Mesh mesh, Mat4 modelMatrix, Shader shader, Material material, Light[] lights,
      Camera camera, Texture diffuse) {
    this(name, mesh, modelMatrix, shader, material, lights, camera, diffuse, null);
  }

  public Model(String name, Mesh mesh, Mat4 modelMatrix, Shader shader, Material material, Light light,
      Camera camera, Texture diffuse) {
    this(name, mesh, modelMatrix, shader, material, light, camera, diffuse, null);
  }

  public Model(String name, Mesh mesh, Mat4 modelMatrix, Shader shader, Material material, Light[] lights,
      Camera camera) {
    this(name, mesh, modelMatrix, shader, material, lights, camera, null, null);
  }

  public Model(String name, Mesh mesh, Mat4 modelMatrix, Shader shader, Material material, Light light,
      Camera camera) {
    this(name, mesh, modelMatrix, shader, material, light, camera, null, null);
  }

  public void setName(String s) {
    this.name = s;
  }

  public void setMesh(Mesh m) {
    this.mesh = m;
  }

  public void setModelMatrix(Mat4 m) {
    modelMatrix = m;
  }

  public void setMaterial(Material material) {
    this.material = material;
  }

  public void setShader(Shader shader) {
    this.shader = shader;
  }

  public void setCamera(Camera camera) {
    this.camera = camera;
  }

  public void setLights(Light[] lights) {
    this.lights = lights;
  }

  public void setDiffuse(Texture t) {
    this.diffuse = t;
  }

  public void setSpecular(Texture t) {
    this.specular = t;
  }

  public void renderName(GL3 gl) {
    System.out.println("Name = " + name);
  }

  public void render(GL3 gl) {
    render(gl, modelMatrix);
  }

  // rendering skybox
  public void renderCube(GL3 gl) {
    renderCube(gl, modelMatrix);
  }

  // second version of render is so that modelMatrix can be overriden with a new parameter
  public void render(GL3 gl, Mat4 modelMatrix) {
    if (mesh_null()) {
      System.out.println("Error: null in model render");
      return;
    }

    Mat4 mvpMatrix = Mat4.multiply(camera.getPerspectiveMatrix(), Mat4.multiply(camera.getViewMatrix(), modelMatrix));
    shader.use(gl);
    shader.setFloatArray(gl, "model", modelMatrix.toFloatArrayForGLSL());
    shader.setFloatArray(gl, "mvpMatrix", mvpMatrix.toFloatArrayForGLSL());

    shader.setVec3(gl, "viewPos", camera.getPosition());

    shader.setInt(gl,"numLights", lights.length);

    for (int i=0; i<lights.length; i++) {
      shader.setVec3(gl, "lights["+i+"].position", lights[i].getPosition());
      shader.setVec3(gl, "lights["+i+"].ambient", lights[i].getMaterial().getAmbient());
      shader.setVec3(gl, "lights["+i+"].diffuse", lights[i].getMaterial().getDiffuse());
      shader.setVec3(gl, "lights["+i+"].specular", lights[i].getMaterial().getSpecular());

      if (lights[i].getClass() == Spotlight.class) {
        Spotlight s = (Spotlight) lights[i];
        shader.setInt(gl, "lights["+i+"].isSpotlight", 1);
        shader.setVec3(gl, "lights["+i+"].direction", s.getDirection());
        shader.setFloat(gl, "lights["+i+"].cutOff", s.getCutOff());
        shader.setFloat(gl, "lights["+i+"].intensity", s.getIntensity());
      } else {
        shader.setInt(gl, "lights["+i+"].isSpotlight", 0);
        shader.setVec3(gl, "lights["+i+"].direction", new Vec3(0, 0, 0));
        shader.setFloat(gl, "lights["+i+"].cutOff", 0);
        shader.setFloat(gl, "lights["+i+"].intensity", 0);
      }
    }

    shader.setVec3(gl, "material.ambient", material.getAmbient());
    shader.setVec3(gl, "material.diffuse", material.getDiffuse());
    shader.setVec3(gl, "material.specular", material.getSpecular());
    shader.setFloat(gl, "material.shininess", material.getShininess());

    if (diffuse!=null) {
      shader.setInt(gl, "first_texture", 0);  // be careful to match these with GL_TEXTURE0 and GL_TEXTURE1
      gl.glActiveTexture(GL.GL_TEXTURE0);
      diffuse.bind(gl);
    }
    if (specular!=null) {
      shader.setInt(gl, "second_texture", 1);
      gl.glActiveTexture(GL.GL_TEXTURE1);
      specular.bind(gl);
    }

    // then render the mesh
    mesh.render(gl);
  }

  // rendering skybox
  public void renderCube(GL3 gl, Mat4 modelMatrix) {
    if (mesh_null()) {
      System.out.println("Error: null in model render");
      return;
    }

    Mat4 view = camera.getViewMatrix();
    view.removeTranslation(); // so skybox moves with camera

    Mat4 viewModel = Mat4.multiply(view, modelMatrix);

    gl.glDepthMask(false);

    shader.use(gl);
    shader.setFloatArray(gl, "projection", camera.getPerspectiveMatrix().toFloatArrayForGLSL());
    shader.setFloatArray(gl, "view", viewModel.toFloatArrayForGLSL());

    if (diffuse!=null) {
      shader.setInt(gl, "cubeMap", 0);  // be careful to match these with GL_TEXTURE0 and GL_TEXTURE1
      gl.glActiveTexture(GL.GL_TEXTURE_CUBE_MAP);
      diffuse.bind(gl);
    }

    // then render the mesh
    mesh.render(gl);

    gl.glDepthMask(true);
  }

  private boolean mesh_null() {
    return (mesh==null);
  }

  public void dispose(GL3 gl) {
    mesh.dispose(gl);  // only need to dispose of mesh
  }

}