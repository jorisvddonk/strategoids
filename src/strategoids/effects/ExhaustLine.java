/*
 * This file is part of the Strategoids Real-Time-Strategy game/engine project
 *  and is licensed under the GPLv3.
 * See 'Strategoids.java' for more info.
 */

package strategoids.effects;

import java.util.ArrayList;
import javax.media.opengl.GL;
import strategoids.drawing.IDrawable;
import strategoids.drawing.Line;
import strategoids.drawing.LineObject;

/**
 * An exhaust line is just a pretty graphical effect.
 */
public class ExhaustLine implements IEffect, IDrawable {
    public double xpos, ypos;
    public double rot;
    public double movx;
    public double movy;

    //public LineObject exhaust;
    public Line line;
    public double friction;

    public ExhaustLine(double xpos, double ypos, double rot, double initspeed) {
        this.xpos = xpos;
        this.ypos = ypos;
        this.rot = rot;
        //ArrayList<Line> tr = new ArrayList<Line>();
        //tr.add(new Line(0, 0, 1, 0, 1, 1, 1));
        //this.exhaust = new LineObject(tr);
        //exhaust.lines.get(0).ca = 1;
        line = new Line(0, 0, 1, 0, 1, 1, 0);
        
        friction = 0.1;
        movx = Math.cos(Math.toRadians(rot)) * initspeed;
        movy = Math.sin(Math.toRadians(rot)) * initspeed;
    }



    public void movement_tick() {
        xpos += movx;
        ypos += movy;
    }

    public void process_tick() {
        //movx -= Math.cos(Math.toRadians(rot)) * friction;
        //movy -= Math.sin(Math.toRadians(rot)) * friction;
        //movx *= 0.9;
        //movy *= 0.9;
        //exhaust.lines.get(0).ca -= 0.1;
        line.ca -= 0.1;
        line.cg -= 0.15;
    }

    public void draw(GL gl) {
        line.draw(gl, xpos, ypos, rot);
    }

    public boolean mustDestroy() {
        return (line.ca < 0);
    }

}
