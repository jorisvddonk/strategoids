/*
 * This file is part of the Strategoids Real-Time-Strategy game/engine project
 *  and is licensed under the GPLv3.
 * See 'Strategoids.java' for more info.
 */

package strategoids.gui;

import com.sun.opengl.util.j2d.TextRenderer;
import javax.media.opengl.GL;


/**
 * GUI Line Square. Used for the 'selection box' when dragging multiple ships.
 */
public class GUILineSquare implements GUIElement {
    public double xorg, yorg;
    public double xend, yend;
    public double r=1,g=1,b=1,a=0.2;

    public GUILineSquare(double xorg, double yorg, double xend, double yend) {
        this.xorg = xorg;
        this.yorg = yorg;
        this.xend = xend;
        this.yend = yend;
    }

    public GUILineSquare(double xorg, double yorg, double xend, double yend, double r, double g, double b, double a) {
        this.xorg = xorg;
        this.yorg = yorg;
        this.xend = xend;
        this.yend = yend;
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }


    public void Render(GL gl, TextRenderer tr) {
        Render(gl, tr, xorg, yorg, xend, yend, 1,1,1,0.2);
    }

    public static void Render(GL gl, TextRenderer tr, double xorg, double yorg, double xend, double yend, double r, double g, double b, double a) {
        gl.glLoadIdentity();
        gl.glBegin(GL.GL_QUADS);
         gl.glColor4d(r,g,b,a);
        gl.glVertex3d(xorg, yorg, 0);
        gl.glVertex3d(xorg, yend, 0);
        gl.glVertex3d(xend, yend, 0);
        gl.glVertex3d(xend, yorg, 0);

        gl.glEnd();
    }
}
