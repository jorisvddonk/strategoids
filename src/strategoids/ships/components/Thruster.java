/*
 * This file is part of the Strategoids Real-Time-Strategy game/engine project
 *  and is licensed under the GPLv3.
 * See 'Strategoids.java' for more info.
 */

package strategoids.ships.components;

import strategoids.Main;
import strategoids.effects.ExhaustLine;

/**
 * A thruster.
 * Used for graphics!
 */
public class Thruster {
    public double xpos;
    public double ypos;
    public double rot;
    public double exhaustspeed;
    public double angle;

    public Thruster(double xpos, double ypos, double rot, double exhaustspeed, double angle) {
        this.xpos = xpos;
        this.ypos = ypos;
        this.rot = rot - 90;
        this.exhaustspeed = exhaustspeed;
        this.angle = angle;
    }

    public void do_effect(double x, double y, double r) {
        //System.out.println("-- " + x + " | " + y);
        double ac1 = Math.cos(Math.toRadians(r-90));
        double as1 = Math.sin(Math.toRadians(r-90));
        Main.strategoids.game.effects.add(new ExhaustLine((xpos * ac1) - (ypos * as1) + x, (xpos * as1) + (ypos * ac1) + y, rot+r+(Main.strategoids.random.nextDouble()*angle)-(Main.strategoids.random.nextDouble()*angle),exhaustspeed));
    }


}
