/*
 * This file is part of the Strategoids Real-Time-Strategy game/engine project
 *  and is licensed under the GPLv3.
 * See 'Strategoids.java' for more info.
 */
package strategoids.drawing;

import java.util.ArrayList;
import javax.media.opengl.GL;

/**
 * A LineObject. Primarily a list of lines that can be drawn.
 * Also has an optional rotation offset.
 */
public class LineObject implements IDrawable {

    public ArrayList<Line> lines;
    public double rotoffset;

    public LineObject(ArrayList<Line> lines) {
        this.lines = lines;
        rotoffset = 0;
    }

    public LineObject(ArrayList<Line> lines, double rotoffset) {
        this.lines = lines;
        this.rotoffset = rotoffset;
    }

    public void addLine(Line line) {
        lines.add(line);
    }

    public void draw(GL gl, double xpos, double ypos, double rot) {
        if (gl != null) {
            gl.glLoadIdentity();
            gl.glTranslated(xpos, ypos, 0);
            gl.glRotated(rot + rotoffset, 0, 0, 1);
            for (Line l : lines) {
                l.draw(gl, 0, 0);
            }
            //gl.glFlush();
        }
    }

    public void draw(GL gl) {
        draw(gl, 0, 0, 0);
    }

    public void draw_fordisplaylist(GL gl) { // for gl createLists
        gl.glBegin(gl.GL_LINES);
        if (gl != null) {
            for (Line l : lines) {
                l.draw_forLists(gl);
            }
        }
        gl.glEnd();
    }
}
