# Spacecraft
This project involves using modern OpenGL to render a scene in Java. [Link to video demo](https://youtu.be/YPq3Qh4VAHE)

## Running the code

First, make sure you are in the `src` directory.
```
cd src/
```

`java Spacecraft` should work.
```
java Spacecraft
```

Otherwise, try `run.bat` or `run.sh`.

If both of the above fail, you will have to manually compile all java files from the command line.
```
javac gmaths/*.java
javac helpers/*.java
...
javac Spacecraft.java
java Spacecraft
```

>**Note:** it may take a while to render the scene, wait up to 30 seconds.

## Textures

```
The following textures are from http://libnoise.sourceforge.net/examples/textures/index.html
jade.jpg
  2003-2005 Jason Bevins, GNU General Public License.

https://github.com/nasa/NASA-3D-Resources
"All of these resources are free to download and use."
ear0xuu2.jpg

https://www.freepik.com
Designed by Freepik
star.jpg
rusty_metal.jpg
antenna.jpg

https://3dtextures.me/
metal.png

https://www.unsplash.com
galaxy.jpg

https://jaxry.github.io/panorama-to-cubemap/
Used for the skybox.
```

## Acknowledgements
I used the code from chapter 7.3 of the JOGL tutorial as a starting point. Thank you Steve Maddock. And thank you to Joey's LearnOpenGl tutorial for help with the skybox.
