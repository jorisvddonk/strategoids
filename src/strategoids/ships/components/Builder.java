/*
 * This file is part of the Strategoids Real-Time-Strategy game/engine project
 *  and is licensed under the GPLv3.
 * See 'Strategoids.java' for more info.
 */
package strategoids.ships.components;

import java.util.ArrayList;
import strategoids.Main;
import strategoids.misc.MyMath;
import strategoids.ships.Ship;

/**
 * Builder component.
 * Can spawn new ships!
 */
public class Builder implements IComponent {

    public static int BUILD_DEFAULTTIME = 100;
    public ArrayList<String> queue;
    public int timeout;
    public Ship parentship;

    public Builder(Ship parentship) {
        queue = new ArrayList<String>();
        this.parentship = parentship;
        timeout = 0;
    }

    public void process() {
        timeout--;
        if (timeout < 0) {
            if (queue.size() > 0) {
                Main.strategoids.game.ships.add(Main.strategoids.game.create_ship_from_storage(queue.get(0), parentship.getX(), parentship.getY(), MyMath.FixDeg(parentship.getRot() + 180), parentship.getFaction()));
                queue.remove(0);
            }
            resetTimeout();
        }
    }

    public void add(String shiptobuild) {
        queue.add(shiptobuild);
        if (queue.size() == 1) {
            resetTimeout();
        }
    }

    public void resetTimeout() {
        if (queue.size() > 0) {
            Integer t = Main.strategoids.game.storage.build_times.get(queue.get(0));
            if (t == null) {
                timeout = BUILD_DEFAULTTIME;
            } else {
                timeout = t;
            }
        }
    }

    @Override
    public ComponentType getComponentType() {
        return ComponentType.builder;
    }
}
