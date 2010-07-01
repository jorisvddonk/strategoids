/*
 * This file is part of the Strategoids Real-Time-Strategy game/engine project
 *  and is licensed under the GPLv3.
 * See 'Strategoids.java' for more info.
 */
package strategoids.ships.bullets;

import java.awt.geom.Line2D;
import strategoids.drawing.Line;
import strategoids.factions.Faction;
import strategoids.ships.Ship;

/**
 * A shell (which is currently used as a laser, oddly. :P).
 * Pew pew pew!
 */
public class Shell extends strategoids.effects.ExhaustLine implements IBullet {

    private boolean forcedestroy;
    private int lifetime;
    private Faction faction;
    private boolean instahit;

    public Shell(double xpos, double ypos, double rot, double initspeed, int lifetime, Faction faction, double length_of_bullet, boolean instahit) {
        super(xpos, ypos, rot, initspeed);
        line.cr = 0;
        line.cg = 0;
        line.cb = 1;
        line.ca = 1;
        line.xend = length_of_bullet;
        this.lifetime = lifetime;
        forcedestroy = false;
        this.faction = faction;
        this.instahit = instahit;
    }

    @Override
    public void process_tick() {
        lifetime -= 1;
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
