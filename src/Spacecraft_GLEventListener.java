import gmaths.*;
import helpers.Camera;
import helpers.Light;
import helpers.Spotlight;
import helpers.TimeHelper;
import material.TextureLibrary;
import objects.DancingRobot;
import objects.Globe;
import objects.SpotlightRobot;
import shapes.*;
import material.*;

import com.jogamp.opengl.*;

/*
 * Adapted from Tutorial Ch7 by Steve Maddock.
 */
  
public class Spacecraft_GLEventListener implements GLEventListener {
  
  private static final boolean DISPLAY_SHADERS = false;
  
  public Spacecraft_GLEventListener(Camera camera) {
    this.camera = camera;
    this.camera.setPosition(new Vec3(4f,12f,18f));
  }
  
  // ***************************************************
  /*
   * METHODS DEFINED BY GLEventListener
   */

  /* Initialisation */
  public void init(GLAutoDrawable drawable) {   
    GL3 gl = drawable.getGL().getGL3();
    System.err.println("Chosen GLCapabilities: " + drawable.getChosenGLCapabilities());
    gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f); 
    gl.glClearDepth(1.0f);
    gl.glEnable(GL.GL_DEPTH_TEST);
    gl.glDepthFunc(GL.GL_LESS);
    gl.glFrontFace(GL.GL_CCW);    // default is 'CCW'
    // gl.glEnable(GL.GL_CULL_FACE); // default is 'not enabled'
    gl.glCullFace(GL.GL_BACK);   // default is 'back', assuming CCW
    initialise(gl);

    danceRobotTimeHelper = new TimeHelper();
    lightTimeHelper = new TimeHelper();
    spotlightRobotTimeHelper = new TimeHelper();
    spotlightTimeHelper = new TimeHelper();
    skyboxTimeHelper = new TimeHelper();
  }
  
  /* Called to indicate the drawing surface has been moved and/or resized  */
  public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    GL3 gl = drawable.getGL().getGL3();
    gl.glViewport(x, y, width, height);
    float aspect = (float)width/(float)height;
    camera.setPerspectiveMatrix(Mat4Transform.perspective(45, aspect));
  }

  /* Draw */
  public void display(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    render(gl);
  }

  /* Clean up memory, if necessary */
  public void dispose(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    light.dispose(gl);
    // room.dispose(gl);
    danceRobot.dispose(gl);
    // spotlightRobot.dispose(gl);
  }
  
  
  // ***************************************************
  /* INTERACTION
   *
   *
   */
   
  private boolean isDancing = false;
  private boolean spotlightRobotIsMoving = true;
  private boolean showLight = true;
  private boolean showSpotlight = true;
   
  public void toggleDance() {
    isDancing = !isDancing;

    if (isDancing) {
      danceRobotTimeHelper.setStartTime(TimeHelper.getSeconds());
    }
  }

  public void toggleSpotlightMovement() {
    spotlightRobotIsMoving = !spotlightRobotIsMoving;

    if (spotlightRobotIsMoving) {
      spotlightRobotTimeHelper.setStartTime(TimeHelper.getSeconds() - spotlightRobotTimeHelper.getSavedTime());
      spotlightTimeHelper.setStartTime(TimeHelper.getSeconds() - spotlightTimeHelper.getSavedTime());
    } else {
      spotlightRobotTimeHelper.setSavedTime(spotlightRobotTimeHelper.getElapsedTime());
      spotlightTimeHelper.setSavedTime(spotlightTimeHelper.getElapsedTime());
    }
  }

  public void toggleLight() {
    showLight = !showLight;
  }

  public void toggleSpotlight() {
    showSpotlight = !showSpotlight;
  }

  
  // ***************************************************
  /* THE SCENE
   * Now define all the methods to handle the scene.
   * This will be added to in later examples.
   */

  // textures
  private TextureLibrary textures;

  private Camera camera;
  private Light light;
  private Room room;

  // skybox
  private SkyBox skybox;
  
  // objects
  private DancingRobot danceRobot;
  private SpotlightRobot spotlightRobot;
  private Globe globe;

  private Spotlight spotlight;

  private void initialise(GL3 gl) {
    createRandomNumbers();

    textures = new TextureLibrary();
    textures.add(gl, "container_diffuse", "assets/textures/container2.jpg");
    textures.add(gl, "container_specular", "assets/textures/container2_specular.jpg");
    textures.add(gl, "earth_diffuse", "assets\\textures\\ear0xuu2.jpg");
    textures.add(gl, "earth_specular", "assets\\textures\\ear0xuu2_specular.jpg");
    textures.add(gl, "dancing_head", "assets\\textures\\dance_robot_face_happy.png");
    textures.add(gl, "rusty", "assets\\textures\\rusty_metal.jpg");
    textures.add(gl, "rusty_spec", "assets\\textures\\rusty_metal_spec.jpg");
    textures.add(gl, "rusty_face", "assets\\textures\\rusty_face.jpg");
    textures.add(gl, "rusty_face_spec", "assets\\textures\\rusty_face_spec.jpg");
    textures.add(gl, "antenna", "assets\\textures\\antenna.jpg");
    textures.add(gl, "antenna_spec", "assets\\textures\\antenna_spec.jpg");
    
    light = new Light(gl);
    light.setCamera(camera);

    spotlightRobot = new SpotlightRobot(gl, camera, light, textures.get("rusty"), textures.get("rusty_spec"),
                                                            textures.get("rusty_face"), textures.get("rusty_face_spec"),
                                                            textures.get("antenna"), textures.get("antenna_spec"));
    spotlight = spotlightRobot.getSpotight();

    // skybox
    String[] skyboxTextures = new String[] {
      "assets\\textures\\skybox\\px.png",
      "assets\\textures\\skybox\\nx.png",
      "assets\\textures\\skybox\\ny.png",
      "assets\\textures\\skybox\\py.png",
      "assets\\textures\\skybox\\pz.png",
      "assets\\textures\\skybox\\nz.png",
    };
    skybox = new SkyBox(gl, light, camera, skyboxTextures);

    // room and lights
    Light[] lights = {light, spotlight};
    room = new Room(gl, 24f, 16f, 32f, camera, lights);

    // objects
    danceRobot = new DancingRobot(gl, camera, lights, textures.get("dancing_head")); 
    globe = new Globe(gl, camera, lights, 
                      textures.get("container_diffuse"), textures.get("container_specular"), 
                      textures.get("container_diffuse"), textures.get("container_specular"),
                      textures.get("earth_diffuse"), textures.get("earth_specular"));

    
  }
  
  private void render(GL3 gl) {
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

    skybox.render(gl, skyboxTimeHelper.getElapsedTime());

    // light UI controls

    Material m = light.getMaterial();

    if (showLight) {
      m.setDiffuse(0.8f, 0.8f, 0.8f);
      m.setSpecular(0.8f, 0.8f, 0.8f);
      light.setMaterial(m);
      light.setPosition(getLightPosition());
      light.render(gl);
    } else {
      m.setDiffuse(0.1f, 0.1f, 0.1f);
      m.setSpecular(0, 0, 0);
      light.setMaterial(m);
    }

    if (showSpotlight) {
      spotlight.setIntensity(SpotlightRobot.SPOTLIGHT_INTENSITY);
    } else {
      spotlight.setIntensity(0);
    }

    // robot UI controls/animation

    if (spotlightRobotIsMoving) {
      spotlightRobot.updatePosition(spotlightRobotTimeHelper);
      spotlightRobot.updateRotation(spotlightTimeHelper.getElapsedTime());
      spotlight.setPosition(spotlightRobot.getSpotlightPosition());
      spotlight.setDirection(spotlightRobot.getSpotlightDirection());
    }
    spotlightRobot.render(gl);

    globe.render(gl);

    // proximity test for when spotlight robot is near dancing robot
    float distance = Vec3.subtract(danceRobot.getPosition(), spotlightRobot.getPosition()).magnitude();

    if (isDancing || distance < 7.5f) {
      danceRobot.updateDance(danceRobotTimeHelper.getElapsedTime());
    } else {
      danceRobot.resetPosition();
    }

    danceRobot.render(gl);
    room.render(gl);
  }

  
  // The light's position is continually being changed, so needs to be calculated for each frame.
  private Vec3 getLightPosition() {
    double elapsedTime = lightTimeHelper.getElapsedTime();
    float x = 5.0f*(float)(Math.sin(Math.toRadians(elapsedTime*50)));
    float y = 8f;
    float z = 5.0f*(float)(Math.cos(Math.toRadians(elapsedTime*50)));
    return new Vec3(x,y,z);   
  }

  
  // ***************************************************
  /* TIME
   */ 
  
  private TimeHelper danceRobotTimeHelper;
  private TimeHelper spotlightRobotTimeHelper;
  private TimeHelper spotlightTimeHelper;
  private TimeHelper lightTimeHelper;
  private TimeHelper skyboxTimeHelper;

  // ***************************************************
  /* An array of random numbers
   */ 
  
  private int NUM_RANDOMS = 1000;
  private float[] randoms;
  
  private void createRandomNumbers() {
    randoms = new float[NUM_RANDOMS];
    for (int i=0; i<NUM_RANDOMS; ++i) {
      randoms[i] = (float)Math.random();
    }
  }
  
}