/*
 * This file is part of the Strategoids Real-Time-Strategy game/engine project
 *  and is licensed under the GPLv3.
 * See 'Strategoids.java' for more info.
 */
package strategoids.ships.bullets;

import java.awt.geom.Line2D;
import javax.media.opengl.GL;
import strategoids.drawing.Line;
import strategoids.factions.Faction;
import strategoids.misc.MyMath;
import strategoids.ships.AI.OrderTarget;
import strategoids.ships.Ship;

/**
 * A missile!
 * Whoooooooooooooooooooooooooosh.. bang!!!
 */
public class Missile extends strategoids.effects.ExhaustLine implements IBullet {

    private boolean forcedestroy;
    private int lifetime;
    private Faction faction;
    private boolean instahit;
    private double thruster_force;
    private OrderTarget target;

    public Missile(double xpos, double ypos, double initspeed, double rot, double rotoffset, int lifetime, Faction faction, double thruster_force, OrderTarget target) {
        super(xpos, ypos, rot, initspeed);
        //rot = Math.toDegrees(Math.atan2(target.getY() - ypos, target.getX() - xpos));
        movx = Math.cos(Math.toRadians(rot + rotoffset)) * initspeed;
        movy = Math.sin(Math.toRadians(rot + rotoffset)) * initspeed;
        line.cr = 1;
        line.cg = 1;
        line.cb = 0;
        line.ca = 1;
        line.xend = 2;
        this.lifetime = lifetime;
        forcedestroy = false;
        this.faction = faction;
        this.thruster_force = thruster_force;
        this.target = target;
    }

    @Override
    public void process_tick() {
        if (target.targetship.getHitPoints() > 0) {
            lifetime -= 1;

            int i = MyMath.ClosestDegDirection(MyMath.FixDeg(rot), MyMath.FixDeg(Math.toDegrees(Math.atan2(target.getY() - ypos, target.getX() - xpos))));
            double d = MyMath.ClosestDegAngle(MyMath.FixDeg(rot), MyMath.FixDeg(Math.toDegrees(Math.atan2(target.getY() - ypos, target.getX() - xpos))));
            double movangle = MyMath.FixDeg(Math.toDegrees(Math.atan2(movy, movx)));
            int i2 = MyMath.ClosestDegDirection(MyMath.FixDeg(Math.toDegrees(Math.atan2(target.getY() - ypos, target.getX() - xpos))), movangle);
            if (d < 90) {
                //movx += Math.cos(Math.toRadians(rot)) * thruster_force;
                //movy += Math.sin(Math.toRadians(rot)) * thruster_force;
                if (i2 == 1) {
                    movx += Math.cos(Math.toRadians(rot + 40)) * thruster_force;
                    movy += Math.sin(Math.toRadians(rot + 40)) * thruster_force;
                } else if (i2 == -1) {
                    movx += Math.cos(Math.toRadians(rot - 40)) * thruster_force;
                    movy += Math.sin(Math.toRadians(rot - 40)) * thruster_force;
                }
            }
            if (i == -1) {
                rot += 1;
            } else if (i == 1) {
                rot -= 1;
            }
        } else {
            lifetime -= 2;
                    movx += Math.cos(Math.toRadians(rot)) * thruster_force;
                    movy += Math.sin(Math.toRadians(rot)) * thruster_force;
        }
    }

    @Override
    public void draw(GL gl) {
        super.draw(gl);
    }

    public boolean checkCollisionWith(Ship ship) {
        boolean collided = false;
        double xp1 = ship.getX();
        double yp1 = ship.getY();
        double ang1 = Math.toRadians(ship.getRot());
        double ac1 = Math.cos(ang1);
        double as1 = Math.sin(ang1);
        //ArrayList<Line> remlines = new ArrayList<Line>();
        //for (Line j : drawObjects.lineobjects.get(k).lines) {
        double xp2 = xpos;
        double yp2 = ypos;
        double ang2 = Math.toRadians(rot);
        double ac2 = Math.cos(ang2);
        double as2 = Math.sin(ang2);
        for (Line l : ship.getLineObject().lines) {
            if (Line2D.linesIntersect(
                    (l.xorg * ac1) - (l.yorg * as1) + xp1,
                    (l.xorg * as1) + (l.yorg * ac1) + yp1,
                    (l.xend * ac1) - (l.yend * as1) + xp1,
                    (l.xend * as1) + (l.yend * ac1) + yp1,
                    //
                    (line.xorg * ac2) - (line.yorg * as2) + xp2,
                    (line.xorg * as2) + (line.yorg * ac2) + yp2,
                    (line.xend * ac2) - (line.yend * as2) + xp2,
                    (line.xend * as2) + (line.yend * ac2) + yp2)) {
                collided = true;
                //remlines.add(j);
            }
            //}
            //for (Line rl : remlines) {
            //    drawObjects.lineobjects.get(k).lines.remove(rl);
            //}
        }
        return collided;
    }

    public void forceDestroy() {
        forcedestroy = true;
    }

    @Override
    public boolean mustDestroy() {
        return (lifetime < 0 || forcedestroy);
    }

    public Faction getFaction() {
        return faction;
    }

    public double getX() {
        return xpos;
    }

    public double getY() {
        return ypos;
    }

    public boolean isInstaHit() {
        return instahit;
    }
}
