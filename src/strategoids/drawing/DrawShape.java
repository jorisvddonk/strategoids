/*
 * This file is part of the Strategoids Real-Time-Strategy game/engine project
 *  and is licensed under the GPLv3.
 * See 'Strategoids.java' for more info.
 */

package strategoids.drawing;

import javax.media.opengl.GL;
import strategoids.misc.ObjectSpawn;

/**
 * Draws shapes (statically). Currently only a circle.
 */
public class DrawShape {
    public static void drawCircle(double xpos, double ypos, double size, GL gl, double r, double g, double b) {
        if (gl == null) {
            System.err.println("Drawshape.drawCircle: gl is null!");
        }
        ObjectSpawn.create_gon_LineObject(6, size,r,g,b).draw(gl, xpos, ypos, 0);
    }
}
