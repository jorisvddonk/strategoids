/*
 * This file is part of the Strategoids Real-Time-Strategy game/engine project
 *  and is licensed under the GPLv3.
 * See 'Strategoids.java' for more info.
 */

package strategoids.drawing;

import javax.media.opengl.GL;

/**
 * A LineSquare is primarily used for a 'selection box' style thing. Basically,
 * it's a rectangle non-filled object.
 */
public class LineSquare implements IDrawable {
    public double xorg, yorg;
    public double xend, yend;

    public LineSquare(double xorg, double yorg, double xend, double yend) {
        this.xorg = xorg;
        this.yorg = yorg;
        this.xend = xend;
        this.yend = yend;
    }

    public void draw(GL gl) {
        gl.glLoadIdentity();
        gl.glBegin(GL.GL_LINE_LOOP);
         gl.glColor4d(1,1,1,0.5);
        gl.glVertex3d(xorg, yorg, 0);
        gl.glVertex3d(xorg, yend, 0);
        gl.glVertex3d(xend, yend, 0);
        gl.glVertex3d(xend, yorg, 0);

        gl.glEnd();
    }


}
