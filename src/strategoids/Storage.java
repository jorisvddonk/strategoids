/*
 * This file is part of the Strategoids Real-Time-Strategy game/engine project
 *  and is licensed under the GPLv3.
 * See 'Strategoids.java' for more info.
 */
package strategoids;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.opengl.GL;
import strategoids.drawing.Line;
import strategoids.drawing.LineObject;
import strategoids.factions.Faction;
import strategoids.ships.AI.IShipAICore;
import strategoids.ships.AI.Order;
import strategoids.ships.AI.ShipAICore_Default;
import strategoids.ships.AI.ShipAICore_Sessile;
import strategoids.ships.Ship;
import strategoids.ships.bullets.IBullet.BulletType;
import strategoids.ships.components.Builder;
import strategoids.ships.components.Thruster;
import strategoids.ships.components.Turret;

/**
 * Storage class. Used to parse through the shape.txt file. Will create ship
 * 'blueprints' according to the shape.txt file. Blueprints can then be cloned
 * using cloneship().
 *
 * Furthermore, the Storage class will make OpenGL display lists for all ship
 * types, keep track of them, and allows for rendering of the display lists.
 *
 * NOTE: cloneship will NOT clone the ship's "ship graphic" (lineobject, a
 * collection of lines). Thus, if you clone a ship from a blueprint and then
 * alter the "ship graphic", ALL ships from that ship graphic will have botched
 * hit detection. Thus, before you edit a ship graphic ingame, make sure that
 * you clone the ship graphic lineobject. Also make sure that from then on you
 * don't call the display list anymore.
 */
public class Storage {

    HashMap<String, Integer> openGL_Lists;
    private boolean process;
    private double color_r = 1, color_g = 1, color_b = 1, color_a = 1;
    private Double prevx, prevy;
    private double shapesize = 1;
    private double size = 1;
    private Ship retship;
    private ArrayList<Line> lines;
    public ArrayList<Ship> blueprints;
    public HashMap<String, Integer> build_times;
    public HashMap<String, IShipAICore> shipAICores;

    //Craft a Storage object
    public Storage() {
        buildAICores();
    }

    //parse shape.txt and generate display lists.
    public void Storage_buildGL(GL gl) {
        build_times = new HashMap<String, Integer>();
        System.out.println("storage start");
        processFile();
        craftDisplayLists(gl);
        System.out.println("storage end");
    }

    public void buildAICores() {
        shipAICores = new HashMap<String, IShipAICore>();
        shipAICores.put("default", new ShipAICore_Default());
        shipAICores.put("sessile", new ShipAICore_Sessile());
    }

    /**
     * process the shape.txt file
     */
    public void processFile() {
        process = true;
        blueprints = new ArrayList<Ship>();
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(Main.strategoids.FILE_SHAPEFILE)));
            String read = null;
            while ((read = input.readLine()) != null) {
                if (read.startsWith("#")) {
                    //System.err.println(read);
                } else {
                    processLine(read);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(Storage.class.getName()).log(Level.SEVERE, null, ex);
        }
        for (Ship s : blueprints) {
            System.out.println("Ship in storage: " + s.identifier);
        }
    }

    /**
     * process a single line in the shape.txt file
     * @param read
     */
    public void processLine(String read) {
        String[] splitstring;

        if (read.toLowerCase().startsWith("thruster ") && process) {
            //thruster <int:id(0-3)>,<double:xorg>,<double:yorg>,<double:rot>,<double:exhaustspeed>,<double:angle>
            splitstring = read.split(" ");
            splitstring = splitstring[1].split(",");
            if (splitstring.length == 6) {
                if (Integer.valueOf(splitstring[0]) >= 0 && Integer.valueOf(splitstring[0]) <= 3) {
                    Thruster t = new Thruster(Double.valueOf(splitstring[1]) * shapesize, Double.valueOf(splitstring[2]) * shapesize, Double.valueOf(splitstring[3]), Double.valueOf(splitstring[4]), Double.valueOf(splitstring[5]));
                    retship.thrusters.get(Integer.valueOf(splitstring[0])).add(t);
                }
            }
        } else if (read.toLowerCase().startsWith("setcolor ") && process) {// && faction == null) { // don't do this if faction is set...
            splitstring = read.split(" ");
            splitstring = splitstring[1].split(",");
            color_r = Double.valueOf(splitstring[0]);
            color_g = Double.valueOf(splitstring[1]);
            color_b = Double.valueOf(splitstring[2]);
        } else if (read.toLowerCase().startsWith("setsize ") && process) {
            splitstring = read.split(" ");
            shapesize = Double.valueOf(splitstring[1]) * size;
        } else if (read.toLowerCase().startsWith("setalpha ") && process) {
            splitstring = read.split(" ");
            splitstring = splitstring[1].split(",");
            color_a = Double.valueOf(splitstring[0]);
        } else if (read.toLowerCase().startsWith("setshipaicore ") && process) {
            splitstring = read.split(" ");
            retship.setShipAICore(splitstring[1]);
        } else if (read.toLowerCase().startsWith("shape ")) {
            prevx = null;
            prevy = null;
            //specific to Storage:
            splitstring = read.split(" ");
            lines = new ArrayList<Line>();
            retship = new Ship(0, 0, 0, splitstring[1], new Faction("Blueprint Faction", (byte) 99, 0.2, 0.2, 0.2));
            /*if (!specificshape.equals("") && specificshape != null) {
            splitstring = read.split(" ");
            if (specificshape.equals(splitstring[1])) {
            process = true;
            } else {
            process = false;
            }
            }*/
        } else if (read.toLowerCase().startsWith("endshape") && process) {
            prevx = null;
            prevy = null;
            //specific to Storage:
            retship.ship_graphic = new LineObject(lines, -90);
            blueprints.add(retship);
            retship = null;

            /*if (!specificshape.equals("") && specificshape != null) {
            process = false;
            }*/
        } else if (read.toLowerCase().startsWith("newline") && process) {
            prevx = null;
            prevy = null;
        } else if (read.toLowerCase().startsWith("turret ") && process) {
            splitstring = read.split(" ");
            double fire_rate = Double.valueOf(splitstring[2]);
            splitstring = splitstring[1].split(",");
            retship.turrets.add(new Turret(Double.valueOf(splitstring[0]) * shapesize, Double.valueOf(splitstring[1]) * shapesize, 0, fire_rate));
        } else if (read.toLowerCase().startsWith("hitpoints ") && process) {
            splitstring = read.split(" ");
            retship.hitpoints = Integer.valueOf(splitstring[1]);
        } else if (read.toLowerCase().startsWith("thruster_force ") && process) {
            splitstring = read.split(" ");
            retship.thruster_force = Double.valueOf(splitstring[1]);
        } else if (read.toLowerCase().startsWith("turn_rate ") && process) {
            splitstring = read.split(" ");
            retship.turn_rate = Double.valueOf(splitstring[1]);
        } else if (read.toLowerCase().startsWith("fire_rate ") && process) {
            splitstring = read.split(" ");
            retship.fire_rate = Double.valueOf(splitstring[1]);
        } else if (read.toLowerCase().startsWith("bullet_missile") && process) {
            retship.bulletType = BulletType.missile;
        } else if (read.toLowerCase().startsWith("iscarrier") && process) {
            retship.isCarrier = true;
        } else if (read.toLowerCase().startsWith("v ") && process) {
            splitstring = read.split(" ");
            splitstring = splitstring[1].split(",");
            if (prevx != null && prevy != null) {
                Line l = new Line(prevx * shapesize, prevy * shapesize, Double.valueOf(splitstring[0]) * shapesize, Double.valueOf(splitstring[1]) * shapesize, color_r, color_g, color_b);
                l.ca = color_a;
                lines.add(l);
                //System.err.println("Line added: " + l.toString());
                prevx = Double.valueOf(splitstring[0]);
                prevy = Double.valueOf(splitstring[1]);
            } else {
                prevx = Double.valueOf(splitstring[0]);
                prevy = Double.valueOf(splitstring[1]);
            }
        } else if (read.toLowerCase().startsWith("addcomponent ")) {
            splitstring = read.split(" ");
            if (splitstring[1].equals("builder")) {
                retship.components.add(new Builder(retship));
            }
        }
    }

    /**
     * Create display lists for all ship blueprints.
     * @param gl
     */
    public void craftDisplayLists(GL gl) {
        openGL_Lists = new HashMap<String, Integer>();
        for (Ship s : blueprints) {
            Integer listnr = gl.glGenLists(1);
            gl.glNewList(listnr, gl.GL_COMPILE);
            {
                s.ship_graphic.draw_fordisplaylist(gl);
            }
            gl.glEndList();
            openGL_Lists.put(s.identifier, listnr);
        }
    }

    /**
     * Call a display list based on a ship's identifier
     * @param gl
     * @param list_to_call
     */
    public void callDisplayList_byString(GL gl, String list_to_call) {
        Integer listnr = openGL_Lists.get(list_to_call);
        if (listnr != null) {
            callDisplayList(gl, listnr);
        }
    }

    /**
     * get the glInt of the display list based on the ship's identifier
     * @param list_to_get
     * @return
     */
    public Integer getDisplayList_byString(String list_to_get) {
        return openGL_Lists.get(list_to_get);
    }

    /**
     * Call a display list by display list glInt
     * @param gl
     * @param glUintDisplayList
     */
    public void callDisplayList(GL gl, Integer glUintDisplayList) {
        gl.glCallList(glUintDisplayList);
    }

    /**
     * Clone a ship based on a ship's identifier. Turrets and the like will be
     * properly cloned, but SHIP_GRAPHIC WILL NOT BE CLONED!
     *
     * Implementation note: make sure that all things that need to be cloned
     * are cloned! ;)
     * @param identifier
     * @return
     */
    public Ship cloneShip(String identifier) {
        Ship clonedship = null;
        for (Ship s : blueprints) {
            if (s.identifier.equals(identifier)) {
                try {
                    clonedship = (Ship) s.clone();
                } catch (CloneNotSupportedException ex) {
                    Logger.getLogger(Storage.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        if (clonedship != null) {
            clonedship.orderqueue = new ArrayList<Order>();
            ArrayList<Turret> turrets = new ArrayList<Turret>();
            for (Turret t : clonedship.turrets) {
                try {
                    turrets.add((Turret) t.clone());
                } catch (CloneNotSupportedException ex) {
                    Logger.getLogger(Storage.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            clonedship.turrets = turrets;
            //clonedship.ship_graphic = null;
            return clonedship;
        } else {
            return null;
        }
    }
}
