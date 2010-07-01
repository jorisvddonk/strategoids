/*
 * This file is part of the Strategoids Real-Time-Strategy game/engine project
 *  and is licensed under the GPLv3.
 * See 'Strategoids.java' for more info.
 */

package strategoids.drawing;

import javax.media.opengl.GL;

/**
 * A line with x/y start and x/y end, aswell as r/g/b/a values.
 * Can be drawn in a lot of ways.
 */
public class Line implements IDrawable {
    public double xorg, yorg, xend, yend;
    public double cr, cg, cb, ca;

    public Line(double xorg, double yorg, double xend, double yend, double cr, double cg, double cb) {
        this.xorg = xorg;
        this.yorg = yorg;
        this.xend = xend;
        this.yend = yend;
        this.cr = cr;
        this.cg = cg;
        this.cb = cb;
        this.ca = 1;
    }

    public Line(double xorg, double yorg, double xend, double yend, double cr, double cg, double cb, double ca) {
        this.xorg = xorg;
        this.yorg = yorg;
        this.xend = xend;
        this.yend = yend;
        this.cr = cr;
        this.cg = cg;
        this.cb = cb;
        this.ca = ca;
    }

    public static void draw(GL gl, double xorg, double yorg, double xend, double yend, double cr, double cg, double cb, double ca) {
        gl.glLoadIdentity();
        draw_NoIdentityChange(gl, xorg, yorg, xend, yend, cr, cg, cb, ca);
    }

    public static void draw_NoIdentityChange(GL gl, double xorg, double yorg, double xend, double yend, double cr, double cg, double cb, double ca) {
        gl.glBegin(GL.GL_LINES);
         gl.glColor4d(cr, cg, cb, ca);
        gl.glVertex2d(xorg, yorg);
        gl.glVertex2d(xend, yend);
        gl.glEnd();
    }

    public static void drawLite(GL gl, double xorg, double yorg, double xend, double yend) {
        gl.glVertex2d(xorg, yorg);
        gl.glVertex2d(xend, yend);
    }

    public static void drawLite(GL gl, double xorg, double yorg, double xend, double yend, double cr, double cg, double cb, double ca) {
        gl.glColor4d(cr, cg, cb, ca);
        gl.glVertex2d(xorg, yorg);
        gl.glVertex2d(xend, yend);
    }

    public void draw(GL gl) {
        gl.glLoadIdentity();
        gl.glBegin(GL.GL_LINES);
         gl.glColor4d(cr, cg, cb, ca);
        gl.glVertex2d(xorg, yorg);
        gl.glVertex2d(xend, yend);
        gl.glEnd();
    }

    public void draw_forLists(GL gl) {
        //gl.glLoadIdentity();
        //gl.glBegin(GL.GL_LINES);
         //gl.glColor4d(cr, cg, cb, ca);
        gl.glVertex2d(xorg, yorg);
        gl.glVertex2d(xend, yend);
        //gl.glEnd();
    }

    public void draw(GL gl, double xpos, double ypos) {
        gl.glBegin(GL.GL_LINES);
        gl.glColor4d(cr, cg, cb, ca);
        gl.glVertex2d(xpos + xorg, ypos + yorg);
        gl.glVertex2d(xpos + xend, ypos + yend);
        gl.glEnd();
    }

    public void draw(GL gl, double xpos, double ypos, double rot) {
        gl.glLoadIdentity();
        gl.glTranslated(xpos, ypos, 0);
        gl.glRotated(rot, 0, 0, 1);
        gl.glBegin(GL.GL_LINES);
        gl.glColor4d(cr, cg, cb, ca);
        gl.glVertex2d(xorg, yorg);
        gl.glVertex2d(xend, yend);
        gl.glEnd();
        gl.glFlush();
    }

    /*public void colorize() {

        if (cr != -1 && cg != -1 && cb != -1) {
            gl.glColor4d(cr, cg, cb, ca);
        }
        gl.gl
    }*/

    public String toString() {
        return "<Line:" + cr + "," + cg + "," + cb + ":" + ca + "> [" + xorg + " | " + yorg + "] - [" + xend + " | " + yend + "]";
    }
}
