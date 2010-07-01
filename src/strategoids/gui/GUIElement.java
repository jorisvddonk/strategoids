/*
 * This file is part of the Strategoids Real-Time-Strategy game/engine project
 *  and is licensed under the GPLv3.
 * See 'Strategoids.java' for more info.
 */

package strategoids.gui;

import com.sun.opengl.util.j2d.TextRenderer;
import javax.media.opengl.GL;

/**
 * GUI element. Something that can get rendered.
 */
public interface GUIElement {
    public void Render(GL gl, TextRenderer tr);    
}
