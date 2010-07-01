/*
 * This file is part of the Strategoids Real-Time-Strategy game/engine project
 *  and is licensed under the GPLv3.
 * See 'Strategoids.java' for more info.
 */

package strategoids.objects;

import javax.media.opengl.GL;
import strategoids.drawing.LineObject;
import strategoids.misc.ObjectSpawn;

/**
 * An asteroid!
 */
public class Asteroid {
    double xpos;
    double ypos;
    LineObject graphic;
    double rot;

    public Asteroid() {
        graphic = ObjectSpawn.create_asteroid_LineObject_2(20, 10, 1);
        xpos = 0;
        ypos = 0;
        rot = 0;
    }

    public void movement_tick() {
        rot += 0.01;
    }

    public void draw(GL gl) {
        graphic.draw(gl, xpos, ypos, rot);
    }
}
