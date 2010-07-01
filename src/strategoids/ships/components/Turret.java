/*
 * This file is part of the Strategoids Real-Time-Strategy game/engine project
 *  and is licensed under the GPLv3.
 * See 'Strategoids.java' for more info.
 */
package strategoids.ships.components;

import javax.media.opengl.GL;
import strategoids.Main;
import strategoids.drawing.Line;
import strategoids.factions.Faction;
import strategoids.misc.MyMath;
import strategoids.ships.AI.OrderTarget;
import strategoids.ships.bullets.Shell;

/**
 * A turret.
 * Turrets can rotate and point at targets, and then shoot at them!
 * Currently only lasers.
 * TODO: allow other bullet types to be shot from turrets!
 */
public class Turret implements Cloneable {

    double xpos;
    double ypos;
    double rot;
    double lastshot = 0;
    double fire_rate = 100;

    public Turret(double xpos, double ypos, double rot, double fire_rate) {
        this.xpos = xpos;
        this.ypos = ypos;
        this.rot = rot;
        this.fire_rate = fire_rate;
    }

    public Turret(double xpos, double ypos) {
        this.xpos = xpos;
        this.ypos = ypos;
        this.rot = 0;
    }

    public void process() {
        lastshot--;
    }

    public void shoot(Faction faction, double xposBase, double yposBase, double rotBase, OrderTarget tgt) {
        double ac1 = Math.cos(Math.toRadians(rotBase - 90));
        double as1 = Math.sin(Math.toRadians(rotBase - 90));
        double xpos_turret = (xpos * ac1) - (ypos * as1) + xposBase;
        double ypos_turret = (xpos * as1) + (ypos * ac1) + yposBase;
        double angle = MyMath.FixDeg(Math.toDegrees(Math.atan2(tgt.getY() - ypos_turret, tgt.getX() - xpos_turret)));
        rot = angle;
        if (lastshot < 0) {
            Main.strategoids.game.bullets.add(new Shell(xpos_turret, ypos_turret, rot, 4, 25, faction,4,false));
            lastshot = fire_rate;
        }
    }

    public void turn_left() {
        rot += 1;
    }

    public void turn_right() {
        rot -= 1;
    }

    public void setRot(double r) {
        rot = r;
    }

    public void draw(GL gl, double rotBase) {
        gl.glPushMatrix();
        gl.glTranslated(xpos, ypos, 0);
        gl.glRotated(-rotBase, 0, 0, 1);
        gl.glRotated(rot, 0, 0, 1);
        Line.draw_NoIdentityChange(gl, -0.25, 0, 0.25, 0, 1, 1, 1, 1);
        Line.draw_NoIdentityChange(gl, 0, 0, 0, 1, 1, 0, 1, 1);
        gl.glPopMatrix();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }


}
