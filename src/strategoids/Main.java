/**
 * This file is part of the Strategoids Real-Time-Strategy game/engine project
 *  and is licensed under the GPLv3.
 * See 'Strategoids.java' for more info.
 *
 * This file is Based on NetBeans "JOGL Application" template, which states:
 ** author: Brian Paul (converted to Java by Ron Cemer and Sven Goethel) *
 ** This version is equal to Brian Paul's version 1.2 1999/10/21
 * ..and is licensed under the SGI Free Software License B
 * (see: http://kenai.com/projects/jogl/sources/jogl-git/content/LICENSE.txt)
 * ..and hence compatible with the GPLv3
 * (see: http://www.gnu.org/licenses/license-list.html#GPLCompatibleLicenses)
 */




/**
 * Main.java
 *
 * OpenGL initialisation happens here, and the main Strategoids instance will be
 * spawned here aswell!
 */
package strategoids;

import com.sun.opengl.util.Animator;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLEventListener;

public class Main implements GLEventListener {

    public static Strategoids strategoids;

    public static void main(String[] args) {

        Frame frame = new Frame("Strategoids Development Version");
        GLCanvas canvas = new GLCanvas();

        strategoids = new Strategoids(canvas);


        canvas.addGLEventListener(new Main());
        frame.add(canvas);
        frame.setSize(1280, 1024);
        final Animator animator = new Animator(canvas);
        frame.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                // Run this on another thread than the AWT event queue to
                // make sure the call to Animator.stop() completes before
                // exiting
                new Thread(new Runnable() {

                    public void run() {
                        animator.stop();
                        System.exit(0);
                    }
                }).start();
            }
        });
        // Center frame
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        animator.start();
    }

    public void init(GLAutoDrawable drawable) {
        // Use debug pipeline
        // drawable.setGL(new DebugGL(drawable.getGL()));

        GL gl = drawable.getGL();
        strategoids.init(gl);

        // Enable VSync
        gl.setSwapInterval(1);

        // Setup the drawing area and shading mode
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl.glShadeModel(GL.GL_FLAT); // try setting this to GL_FLAT and see what happens.
        gl.glBlendFunc(gl.GL_SRC_ALPHA, gl.GL_ONE_MINUS_SRC_ALPHA);
        gl.glEnable(gl.GL_BLEND);
        gl.glAlphaFunc(gl.GL_GREATER, 0.1f);
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL gl = drawable.getGL();
        strategoids.reshape(drawable, x, y, width, height, gl);
    }

    public void display(GLAutoDrawable drawable) {
        if (strategoids.requireScreenDimensionsrecalc) {
            reshape(drawable, strategoids.width, strategoids.height, strategoids.width, strategoids.height);
            strategoids.requireScreenDimensionsrecalc = false;
        }

        GL gl = drawable.getGL();

        // Clear the drawing area
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        gl.glMatrixMode(GL.GL_MODELVIEW);

        strategoids.display(gl);




        // Flush all drawing operations to the graphics card
        gl.glFlush();
        drawable.swapBuffers();
        //dolock.releaseR();
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
    }
}

