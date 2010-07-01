/*
 * This file is part of the Strategoids Real-Time-Strategy game/engine project
 *  and is licensed under the GPLv3.
 * See 'Strategoids.java' for more info.
 */
package strategoids.misc;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

/**
 * Listens for key presses/releases and modifies an array based on those key
 * presses/releases.
 */
public class MyKeyListener extends KeyAdapter {

    public ArrayList<Integer> keys;

    public MyKeyListener() {
        keys = new ArrayList<Integer>();
    }

    public void keyPressed(KeyEvent e) {
        char i = e.getKeyChar();
        Integer ic = e.getKeyCode();
        if (!keys.contains(ic)) {
            String str = Character.toString(i);
            //System.out.println(e.getKeyText(ic) + "(" + ic + ")" + ", (" + str + ") pressed");
            keys.add(ic);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        char i = e.getKeyChar();
        Integer ic = e.getKeyCode();
        if (keys.contains(ic)) {
            String str = Character.toString(i);
            //System.out.println(e.getKeyText(ic) + "(" + ic + ")" + ", (" + str + ") released");
            keys.remove(ic);
        }
    }
}

