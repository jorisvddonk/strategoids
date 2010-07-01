/*
 * This file is part of the Strategoids Real-Time-Strategy game/engine project
 *  and is licensed under the GPLv3.
 * See 'Strategoids.java' for more info.
 */

package strategoids.drawing;

import javax.media.opengl.GL;

/**
 * 'Drawable' interface. Stuff that implements this interface has a draw()
 * method.
 */
public interface IDrawable {
    public void draw(GL gl);
}
