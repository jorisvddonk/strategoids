/*
 * This file is part of the Strategoids Real-Time-Strategy game/engine project
 *  and is licensed under the GPLv3.
 * See 'Strategoids.java' for more info.
 */

package strategoids.effects;

import javax.media.opengl.GL;

/**
 * IEffect is an interface that specifies that something is an effect that can
 * be drawn, processed, moved and destroyed.
 */
public interface IEffect {
    public void movement_tick();
    public void process_tick();
    public void draw(GL gl);
    public boolean mustDestroy();

}
