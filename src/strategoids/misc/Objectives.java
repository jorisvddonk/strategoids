/*
 * This file is part of the Strategoids Real-Time-Strategy game/engine project
 *  and is licensed under the GPLv3.
 * See 'Strategoids.java' for more info.
 */
package strategoids.misc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * The Objectives object keeps track of objectives and wether they have been
 * met or not.
 */
public class Objectives {

    private HashMap<String, Integer> objectives_statuses;
    private HashMap<String, String> objectives_descriptions;

    public Objectives() {
        objectives_statuses = new HashMap<String, Integer>();
        objectives_descriptions = new HashMap<String, String>();
    }

    public void addObjective(String objective) {
        addObjective(objective, "", 0);
    }

    public void addObjective(String objective, String description) {
        addObjective(objective, description, 0);
    }

    public void addObjective(String objective, String description, Integer initial_setting) {
        objectives_statuses.put(objective, initial_setting);
        objectives_descriptions.put(objective, description);
    }

    public void toggleObjective(String objective) {
        Integer i = objectives_statuses.get(objective);
        if (i != null) {
            if (i == 0) {
                setObjective(objective, 1);
            } else {
                setObjective(objective, 0);
            }
        } else {
            System.err.println("Objectives.toggleObjective: objective `" + objective + "` unknown.");
        }
    }

    public boolean isObjectiveMet(String objective) {
        Integer i = objectives_statuses.get(objective);
        if (i != null) {
            return (i != 0);
        }
        return false;
    }

    public Integer getObjective(String objective) {
        return objectives_statuses.get(objective);
    }

    public void setObjective(String objective, Integer setting) {
        objectives_statuses.put(objective, setting);
    }

    public void unlockObjective(String objective) {
        if (!isObjectiveMet(objective)) {
            System.out.println("Objective unlocked: `" + objective + "` - " + objectives_descriptions.get(objective));
            setObjective(objective, 1);
        }
    }

    public ArrayList<String> getUnmetObjectives() {
        ArrayList<String> retlist = new ArrayList<String>();
        for (String k : objectives_statuses.keySet()) {
            if (!isObjectiveMet(k)) {
                retlist.add(objectives_descriptions.get(k));
            }
        }
        return retlist;
    }
}
