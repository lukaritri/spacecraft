package material;

import java.io.File;
import java.io.IOException;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;
import com.jogamp.opengl.util.texture.Texture;

/**
 * Class for dealing with cubemap textures.
 * Adapted from Joey's LearnOpenGl tutorial.
 * 
 * @author  Lucy Lau
 */

public class CubeMapHelper {

  private Texture cubeMap;
  
  public CubeMapHelper(GL3 gl, String[] cubeFaces) {
    this.cubeMap = loadCubeMap(gl, cubeFaces);
  }

  // get
  public Texture getCubeMap() {
    return cubeMap;
  }

  // load cube map from files
  private Texture loadCubeMap(GL3 gl, String[] cubeFaces) {
    Texture cubeMap = new Texture(GL3.GL_TEXTURE_CUBE_MAP);
    TextureData data;

    try {
      for (int i=0; i<cubeFaces.length; i++) {
        File f = new File(cubeFaces[i]);
        data = TextureIO.newTextureData(gl.getGLProfile(), f, false, null);

        if (data == null) {
          throw new IOException("Could not load texture " + cubeFaces[i]);
        }

        cubeMap.updateImage(gl, data, GL3.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i);
      } 
    } catch(Exception e) {
      System.out.println("Error loading cubemap: " + e.getLocalizedMessage());
    }

    cubeMap.setTexParameteri(gl, GL3.GL_TEXTURE_MIN_FILTER, GL3.GL_LINEAR);
    cubeMap.setTexParameteri(gl, GL3.GL_TEXTURE_MAG_FILTER, GL3.GL_LINEAR);
    cubeMap.setTexParameteri(gl, GL3.GL_TEXTURE_WRAP_S, GL3.GL_CLAMP_TO_EDGE);
    cubeMap.setTexParameteri(gl, GL3.GL_TEXTURE_WRAP_T, GL3.GL_CLAMP_TO_EDGE); 

    return cubeMap;
  }

}
