package shapes;
public final class TwoTriangles {
  
  // ***************************************************
  /* THE DATA
   */
  // anticlockwise/counterclockwise ordering
  public static final float[] vertices = {      // position, colour, tex coords
    -0.5f, 0.0f, -0.5f,  0.0f, 1.0f, 0.0f,  0.0f, 1.0f,  // top left
    -0.5f, 0.0f,  0.5f,  0.0f, 1.0f, 0.0f,  0.0f, 0.0f,  // bottom left
     0.5f, 0.0f,  0.5f,  0.0f, 1.0f, 0.0f,  1.0f, 0.0f,  // bottom right
     0.5f, 0.0f, -0.5f,  0.0f, 1.0f, 0.0f,  1.0f, 1.0f   // top right
  };

  public static final float[] rightWallVertices = {      // position, colour, tex coords
    -0.5f, 0.0f, -0.5f,  0.0f, 1.0f, 0.0f,  0.0f, 4.0f,  // top left
    -0.5f, 0.0f,  0.5f,  0.0f, 1.0f, 0.0f,  0.0f, 0.0f,  // bottom left
     0.5f, 0.0f,  0.5f,  0.0f, 1.0f, 0.0f,  8.0f, 0.0f,  // bottom right
     0.5f, 0.0f, -0.5f,  0.0f, 1.0f, 0.0f,  8.0f, 4.0f   // top right
  };

  public static final float[] leftWallVertices = {      // position, colour, tex coords
    -0.5f, 0.0f, -0.5f,  0.0f, 1.0f, 0.0f,  0.0f, 1.0f,  // top left
    -0.5f, 0.0f,  0.5f,  0.0f, 1.0f, 0.0f,  0.0f, 0.0f,  // bottom left
     0.5f, 0.0f,  0.5f,  0.0f, 1.0f, 0.0f,  1.0f, 0.0f,  // bottom right
     0.5f, 0.0f, -0.5f,  0.0f, 1.0f, 0.0f,  1.0f, 1.0f,  // top right

    // window coordinates
     -0.25f, 0.0f, -0.25f,  0.0f, 1.0f, 0.0f,  0.25f, 0.75f,
     -0.25f, 0.0f,  0.25f,  0.0f, 1.0f, 0.0f,  0.25f, 0.25f,
     0.25f, 0.0f,  0.25f,  0.0f, 1.0f, 0.0f,  0.75f, 0.25f,
     0.25f, 0.0f, -0.25f,  0.0f, 1.0f, 0.0f,  0.75f, 0.75f,

     // border
     -0.25f, 0.0f, -0.5f,  0.0f, 1.0f, 0.0f,  0.25f, 1.0f,
     -0.25f, 0.0f,  0.5f,  0.0f, 1.0f, 0.0f,  0.25f, 0.0f,
     0.25f, 0.0f,  0.5f,  0.0f, 1.0f, 0.0f,  0.75f, 0.0f,
     0.25f, 0.0f, -0.5f,  0.0f, 1.0f, 0.0f,  0.75f, 1.0f,
  };
  
  public static final int[] indices = {         // Note that we start from 0!
      0, 1, 2,
      0, 2, 3
  };

  public static final int[] leftWallindices = {         
    0, 1, 8,
    1, 9, 8,
    8, 4, 7,
    8, 7, 11,
    5, 9, 10,
    5, 10, 6,
    11, 10, 3,
    3, 10, 2
  };

}