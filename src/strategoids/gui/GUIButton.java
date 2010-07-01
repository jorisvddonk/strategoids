/*
 * This file is part of the Strategoids Real-Time-Strategy game/engine project
 *  and is licensed under the GPLv3.
 * See 'Strategoids.java' for more info.
 */

package strategoids.gui;

import com.sun.opengl.util.j2d.TextRenderer;
import java.awt.geom.Rectangle2D;
import javax.media.opengl.GL;


/**
 * GUI button!
 */
public class GUIButton implements GUIElement {
    public double xorg, yorg, xend, yend;
    public double r, g, b, a;
    public String buttontext;
    public ButtonAction buttonaction;

    public GUIButton(double xorg, double yorg, double xend, double yend, String buttontext) {
        this.xorg = xorg;
        this.yorg = yorg;
        this.xend = xend;
        this.yend = yend;
        this.buttontext = buttontext;
        this.r = 1;
        this.g = 0;
        this.b = 0;
        this.a = 0.5;
        this.buttonaction = null;
    }

    public void Render(GL gl, TextRenderer tr) {
        gl.glBegin(gl.GL_LINE_LOOP);
        gl.glColor3d(r,g,b);
        gl.glVertex2d(xorg, yorg);
        gl.glVertex2d(xend, yorg);
        gl.glVertex2d(xend, yend);
        gl.glVertex2d(xorg, yend);
        gl.glEnd();

        gl.glBegin(gl.GL_QUADS);
        gl.glColor4d(r,g,b,a);
        gl.glVertex2d(xorg, yorg);
        gl.glVertex2d(xend, yorg);
        gl.glVertex2d(xend, yend);
        gl.glVertex2d(xorg, yend);
        gl.glEnd();



        /*Point tmpP = Point.getCoordinatesOfGLPoint(gl, xorg+((xend-xorg)/2), yorg+((yend-yorg)/2),0);
        tr.beginRendering(640, 480);
        tr.setColor(1, 1, 1, 1);
        tr.draw(buttontext, (int)tmpP.x,(int)tmpP.y);
        tr.endRendering();*/

        tr.begin3DRendering();
        tr.setColor((float)r,(float)g,(float)b,1);
        tr.draw3D(buttontext, (float)(xorg+((xend-xorg)/2)),(float)(yorg+((yend-yorg)/2)),0f,0.1f);
        tr.end3DRendering();
    }

    @Override
    public String toString() {
        return "GUIButton: " + buttontext + " " + xorg + " | " + yorg + "    -    " + xend + " | " + yend;
    }

    public void performAction() {
        if (buttonaction != null) {
            buttonaction.doAction();
        }
    }

    public void setButtonAction(ButtonAction buttonaction) {
        this.buttonaction = buttonaction;
    }



}
