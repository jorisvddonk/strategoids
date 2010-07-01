/*
 * This file is part of the Strategoids Real-Time-Strategy game/engine project
 *  and is licensed under the GPLv3.
 * See 'Strategoids.java' for more info.
 */

package strategoids.drawing;

import com.sun.opengl.util.j2d.TextRenderer;
import java.nio.DoubleBuffer;
import java.nio.IntBuffer;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

/**
 * A bit of text at an x and y coordinate.
 */
public class PointText {
    public double x;
    public double y;
    public String text;

    public PointText(double x, double y, String text) {
        this.x = x;
        this.y = y;
        this.text = text;
    }
//
//    public PointText(GL gl, String text, double x, double y) {
//        this.text = text;
//        gl.glLoadIdentity();
//
//        DoubleBuffer modelMatrix = DoubleBuffer.allocate(16);
//        DoubleBuffer projMatrix = DoubleBuffer.allocate(16);
//        IntBuffer viewport = IntBuffer.allocate(4);
//        gl.glGetDoublev(gl.GL_MODELVIEW_MATRIX, modelMatrix);
//        gl.glGetDoublev(gl.GL_PROJECTION_MATRIX, projMatrix);
//        gl.glGetIntegerv(gl.GL_VIEWPORT, viewport);
//        DoubleBuffer db = DoubleBuffer.allocate(100);
//        GLU glu = new GLU();
//        glu.gluProject(x, y, 0, modelMatrix, projMatrix, viewport, db);
//        System.out.println(db.get(0) + " - " + db.get(1));
//        this.x = db.get(0);
//        this.y = db.get(1);
//    }

//    public void draw(GL gl, TextRenderer tr) {
//        Point p = Point.getCoordinatesOfGLPoint(gl, x, y, 0);
//
//        tr.draw(text, (int)p.x, (int)p.y);
//    }
}
