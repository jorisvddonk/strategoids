/*
 * This file is part of the Strategoids Real-Time-Strategy game/engine project
 *  and is licensed under the GPLv3.
 * See 'Strategoids.java' for more info.
 */

package strategoids.gui;

import com.sun.opengl.util.j2d.TextRenderer;
import javax.media.opengl.GL;


/**
 * GUI label. Text that is rendered using a TextRenderer.
 */
public class GUILabel implements GUIElement {
    public double xpos, ypos;
    public double r, g, b,a, size;
    public String labeltext;
    public ButtonAction buttonaction;

    public GUILabel(double xorg, double yorg, String labeltext) {
        this.xpos = xorg;
        this.ypos = yorg;
        this.labeltext = labeltext;
        this.r = 1;
        this.g = 1;
        this.b = 1;
        this.a = 1;
        this.size = 0.1;
    }

    public void Render(GL gl, TextRenderer tr) {
       Render(gl, tr, labeltext, xpos, ypos, size, r, g, b, a);
    }

    public static void Render(GL gl, TextRenderer tr, String labeltext, double xpos, double ypos, double size, double r, double g, double b, double a) {
        /*Point tmpP = Point.getCoordinatesOfGLPoint(gl, xorg+((xend-xorg)/2), yorg+((yend-yorg)/2),0);
        tr.beginRendering(640, 480);
        tr.setColor(1, 1, 1, 1);
        tr.draw(buttontext, (int)tmpP.x,(int)tmpP.y);
        tr.endRendering();*/

        //tr.begin3DRendering();
        tr.setColor((float)r,(float)g,(float)b,(float)a);
        tr.draw3D(labeltext, (float)xpos,(float)ypos,0f,(float)size);
        //tr.end3DRendering();
    }

    @Override
    public String toString() {
        return "GUIButton: " + labeltext + " " + xpos + " | " + ypos;
    }

}
