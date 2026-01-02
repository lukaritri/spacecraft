package material;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.jogamp.opengl.*;

import com.jogamp.opengl.util.texture.*;

/**
 * From tutorial. Added loadCubeMap fuction.
 * 
 */

public class TextureLibrary {
  
  private Map<String,Texture> textures;

  public TextureLibrary() {
    textures = new HashMap<String, Texture>();
  }

  public void add(GL3 gl, String name, String filename) {
    Texture texture = loadTexture(gl, filename);
    textures.put(name, texture);
  }

  public Texture get(String name) {
    return textures.get(name);
  }

  // adapted from Joey's LearnOpenGl tutorial
  // author: Lucy Lau
  public Texture loadCubeMap(GL3 gl3, String[] faces) {
    Texture cubeMap = TextureIO.newTexture(GL.GL_TEXTURE_CUBE_MAP);

    try {
      for (int i=0; i<faces.length; i++) {
        File f = new File(faces[i]);
        TextureData data = TextureIO.newTextureData(gl3.getGLProfile(), f, false, null);

        if (data == null) {
          throw new IOException("Could not load texture " + faces[i]);
        }

        cubeMap.updateImage(gl3, data, GL.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i);
      }
      cubeMap.setTexParameteri(gl3, GL3.GL_TEXTURE_MIN_FILTER, GL3.GL_LINEAR);
      cubeMap.setTexParameteri(gl3, GL3.GL_TEXTURE_MAG_FILTER, GL3.GL_LINEAR);
      cubeMap.setTexParameteri(gl3, GL3.GL_TEXTURE_WRAP_S, GL3.GL_CLAMP_TO_EDGE);
      cubeMap.setTexParameteri(gl3, GL3.GL_TEXTURE_WRAP_T, GL3.GL_CLAMP_TO_EDGE); 
    } catch(Exception e) {
      System.out.println("Error loading cubemap: " + e.getLocalizedMessage());
    }
    return cubeMap;

  }

  // mip-mapping is included in the below example
  public static Texture loadTexture(GL3 gl3, String filename) {
    Texture t = null; 
    try {
      File f = new File(filename);
      t = (Texture)TextureIO.newTexture(f, true);
      t.bind(gl3);
      t.setTexParameteri(gl3, GL3.GL_TEXTURE_WRAP_S, GL3.GL_REPEAT);
      t.setTexParameteri(gl3, GL3.GL_TEXTURE_WRAP_T, GL3.GL_REPEAT); 
      t.setTexParameteri(gl3, GL3.GL_TEXTURE_MIN_FILTER, GL3.GL_LINEAR_MIPMAP_LINEAR);
      t.setTexParameteri(gl3, GL3.GL_TEXTURE_MAG_FILTER, GL3.GL_LINEAR);
      gl3.glGenerateMipmap(GL3.GL_TEXTURE_2D);
    }
    catch(Exception e) {
      System.out.println("Error loading texture " + filename); 
    }
    return t;
  }


  public void destroy(GL3 gl3) {
    for (var entry : textures.entrySet()) {
      entry.getValue().destroy(gl3);
    }
  }
}