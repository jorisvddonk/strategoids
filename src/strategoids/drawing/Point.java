/*
 * This file is part of the Strategoids Real-Time-Strategy game/engine project
 *  and is licensed under the GPLv3.
 * See 'Strategoids.java' for more info.
 */

package strategoids.drawing;

import java.nio.DoubleBuffer;
import java.nio.IntBuffer;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import strategoids.Main;

/**
 * Just a point. Primarily used to translate between GL and the rendering of the
 * GL scene in the GUI (so you translate mousement click at 100x100 to a
 * coordinate set in the OpenGL space).
 */
public class Point {
    public double x;
    public double y;
    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "Point[x|y]: " + x + " | " + y;
    }

    public static Point getCoordinatesOfGLPoint(GL gl, double x, double y, double z) {
        gl.glLoadIdentity();

        DoubleBuffer modelMatrix = DoubleBuffer.allocate(16);
        DoubleBuffer projMatrix = DoubleBuffer.allocate(16);
        IntBuffer viewport = IntBuffer.allocate(3);
        gl.glGetDoublev(gl.GL_MODELVIEW_MATRIX, modelMatrix);
        gl.glGetDoublev(gl.GL_PROJECTION_MATRIX, projMatrix);
        gl.glGetIntegerv(gl.GL_VIEWPORT, viewport);
        DoubleBuffer db = DoubleBuffer.allocate(3);
        //glu.gluProject(0, 0, 0, gl.GL_MODELVIEW_MATRIX, gl.GL_PROJECTION_MATRIX, gl.GL_VIEWPORT, db);
        GLU glu = new GLU();
        glu.gluProject(x, y, z, modelMatrix, projMatrix, viewport, db);
        //System.out.println(db.get(0) + " - " + db.get(1));
        return new Point(db.get(0), db.get(1));
    }
    
    public static Point getCoordinatesOfGLPoint(double x, double y, DoubleBuffer projMatrix, DoubleBuffer modelMatrix, IntBuffer viewport) {
        DoubleBuffer db = DoubleBuffer.allocate(3);
        GLU glu = new GLU();
        glu.gluProject(x, y, 0, modelMatrix, projMatrix, viewport, db);

        return new Point(db.get(0), db.get(1));
    }

    public static Point getCoordinatesOfScreenPoint(GL gl, double x, double y) {

        DoubleBuffer modelMatrix = DoubleBuffer.allocate(16);
        DoubleBuffer projMatrix = DoubleBuffer.allocate(16);
        IntBuffer viewport = IntBuffer.allocate(3);
        gl.glGetDoublev(gl.GL_MODELVIEW_MATRIX, modelMatrix);
        gl.glGetDoublev(gl.GL_PROJECTION_MATRIX, projMatrix);
        gl.glGetIntegerv(gl.GL_VIEWPORT, viewport);
        DoubleBuffer db = DoubleBuffer.allocate(3);
        //glu.gluProject(0, 0, 0, gl.GL_MODELVIEW_MATRIX, gl.GL_PROJECTION_MATRIX, gl.GL_VIEWPORT, db);
        GLU glu = new GLU();
        glu.gluUnProject(x, y, (double)0, modelMatrix, projMatrix, viewport, db);
        //System.out.println(db.get(0) + " - " + db.get(1));
        return new Point(db.get(0), db.get(1));
    }

    public static Point getCoordinatesOfScreenPoint(double x, double y, DoubleBuffer projMatrix, DoubleBuffer modelMatrix, IntBuffer viewport) {
        DoubleBuffer db = DoubleBuffer.allocate(3);
        //GLU glu = new GLU();
        Main.strategoids.glu.gluUnProject(x, y, 0, modelMatrix, projMatrix, viewport, db);
        return new Point(db.get(0), db.get(1));
    }

}
