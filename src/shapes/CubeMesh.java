package shapes;

import java.nio.FloatBuffer;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;

/*
 * Subclass of Mesh for rendering the skybox.
 */

public class CubeMesh extends Mesh {
  
  public CubeMesh(GL3 gl, float[] vertices) {
    super(gl, vertices, new int[]{0});
    fillBuffers(gl);
  }
  
  public void render(GL3 gl) {
    gl.glBindVertexArray(vertexArrayId[0]);
    gl.glDrawArrays(GL.GL_TRIANGLES, 0, 36);
    gl.glBindVertexArray(0);
  }

  private void fillBuffers(GL3 gl) {
    // this code was from the JOGL tutorial
    gl.glGenVertexArrays(1, vertexArrayId, 0);
    gl.glBindVertexArray(vertexArrayId[0]);
    
    gl.glGenBuffers(1, vertexBufferId, 0);
    gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vertexBufferId[0]);
  
    FloatBuffer fb = Buffers.newDirectFloatBuffer(vertices);
  
    gl.glBufferData(GL.GL_ARRAY_BUFFER, Float.BYTES * vertices.length,
                    fb, GL.GL_STATIC_DRAW);
    
    gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, 3*Float.BYTES, 0);
    gl.glEnableVertexAttribArray(0);
    
    gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
    gl.glBindVertexArray(0);
  }
  
  public void dispose(GL3 gl) {
    gl.glDeleteBuffers(1, vertexBufferId, 0);
    gl.glDeleteVertexArrays(1, vertexArrayId, 0);
    gl.glDeleteBuffers(1, elementBufferId, 0);
  }
}
