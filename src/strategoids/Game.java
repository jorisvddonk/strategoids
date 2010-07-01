/*
 * This file is part of the Strategoids Real-Time-Strategy game/engine project
 *  and is licensed under the GPLv3.
 * See 'Strategoids.java' for more info.
 */
package strategoids;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.opengl.GL;
import strategoids.drawing.PointText;
import strategoids.effects.IEffect;
import strategoids.factions.Faction;
import strategoids.misc.Int2D;
import strategoids.misc.Objectives;
import strategoids.objects.Asteroid;
import strategoids.ships.AI.Order;
import strategoids.ships.AI.Sector;
import strategoids.ships.Ship;
import strategoids.ships.bullets.IBullet;
import strategoids.ships.components.Builder;
import strategoids.ships.components.IComponent;

/**
 * The 'game' object.
 * This class is mostly used for game storage and rendering of both game objects
 * and the GUI. There's also some generic functions that are used by ships.
 * The game class will also process the situation.txt ("map") file.
 */
public class Game {
    //public DrawObjects drawObjects;
    private static boolean THIS_IS_TUTORIAL = true;

    public ArrayList<Ship> ships;
    public ArrayList<Asteroid> asteroids;
    public Ship Player;
    public ArrayList<IEffect> effects;
    public ArrayList<IBullet> bullets;
    public ArrayList<Ship> selectedships;
    public ArrayList<Faction> factions;
    public ArrayList<Order> orders;
    public Map<Int2D, Sector> sectormap;
    public Storage storage;
    public Objectives tutorial_objectives;

    public Game() {
        //init();
        createObjects();
    }

    private void createObjects() {
        sectormap = new HashMap<Int2D, Sector>();
        ships = new ArrayList<Ship>();
        selectedships = new ArrayList<Ship>();
        effects = new ArrayList<IEffect>();
        bullets = new ArrayList<IBullet>();
        factions = new ArrayList<Faction>();
        orders = new ArrayList<Order>();

        asteroids = new ArrayList<Asteroid>();
        asteroids.add(new strategoids.objects.Asteroid());
        tutorial_objectives = new Objectives();
        tutorial_objectives.addObjective("unit_selected","Select a unit with the left mouse button");
        for (String s : tutorial_objectives.getUnmetObjectives()) {
            System.out.println("Objective: " + s);
        }
    }

    public void init() {
        createObjects();

        System.err.println("test");
        //System.err.println(Fortress.FILE_SHAPEFILE);
        //System.err.println(Fortress.FILE_SITUATIONFILE);
        loadSituationFile();
    }

    public void draw(GL gl) {

        for (Int2D i : Main.strategoids.game.sectormap.keySet()) {
            Main.strategoids.game.sectormap.get(i).draw(gl);
        }

        //System.out.println(effects.size() + bullets.size() + " - " + effects.size() + " | " + bullets.size() + "  *  " + Fortress.myout.getstrings().size());
        for (Ship s : ships) {
            s.draw(gl);
        }
        for (IEffect e : effects) {
            e.draw(gl);
        }
        for (IBullet e : bullets) {
            e.draw(gl);
        }
        for (Asteroid a : asteroids) {
            a.draw(gl);
        }
    }

    public ArrayList<PointText> getPointTexts() {
        ArrayList<PointText> rt = new ArrayList<PointText>();
        for (Ship s : ships) {
            rt.add(s.getPointText());
        }
        return rt;
    }

    public void cleanupEffects() {
        for (int i = 0; i < bullets.size(); i++) {
            if (bullets.get(i).mustDestroy()) {
                bullets.remove(i);
                i--;
            }
        }

        for (int i = 0; i < effects.size(); i++) {
            if (effects.get(i).mustDestroy()) {
                effects.remove(i);
                i--;
            }
        }


        //ArrayList<Object> rem = new ArrayList<Object>();

        /*for (IEffect e : effects) {
        if (e.mustDestroy()) {
        rem.add(e);
        }
        }
        for (Object e : rem) {
        effects.remove(e);
        }
        rem.clear();
        for (IBullet e : bullets) {
        if (e.mustDestroy()) {
        rem.add(e);
        }
        }
        for (Object e : rem) {
        bullets.remove(e);
        }
        rem.clear();*/
    }

    public Sector getSector_specific(int int1, int int2) {
        return sectormap.get(new Int2D(int1, int2));
    }

    public Sector getSector_specific(Int2D i) {
        return sectormap.get(i);
    }

    public Sector getSector(double x, double y) {
        return getSector_specific((int) ((x < 0 ? x - Main.strategoids.SECTORSIZE : x) / Main.strategoids.SECTORSIZE), (int) ((y < 0 ? y - Main.strategoids.SECTORSIZE : y) / Main.strategoids.SECTORSIZE));
    }

    public Int2D getSector_Int2D(double x, double y) {
        return new Int2D((int) ((x < 0 ? x - Main.strategoids.SECTORSIZE : x) / Main.strategoids.SECTORSIZE), (int) ((y < 0 ? y - Main.strategoids.SECTORSIZE : y) / Main.strategoids.SECTORSIZE));
    }

    public void fixSector(Ship i) {
        Int2D int2d = getSector_Int2D(i.getX(), i.getY());
        Sector s = i.getSector();
        if (s != null) {
            if (s.sector_number != int2d) {
                if (s != null) {
                    removeShipfromSector(i, s);
                }
                s = getSector_specific(int2d);
                if (s == null) {
                    s = new Sector(int2d);
                }
                sectormap.put(int2d, s);
                s.addShip(i);
                i.setSector(s);
            }
        } else {
            s = getSector_specific(int2d);
            if (s == null) {
                s = new Sector(int2d);
            }
            sectormap.put(int2d, s);
            s.addShip(i);
            i.setSector(s);
        }
    }

    public void destroyandremoveShip(Ship i) {
        Sector s = i.getSector();
        if (s != null) {
            removeShipfromSector(i, s);
        }
        if (selectedships.contains(i)) {
            selectedships.remove(i);
        }
        ships.remove(i);
    }

    public void removeShipfromSector(Ship i, Sector s) {
        s.remShip(i);
        if (s.ships.size() == 0) {
            sectormap.remove(s.sector_number);
        }
    }

    public Ship genRandomShip(String shiptype, double x, double y, double maxdistance, Faction faction) {
        Double dist = Main.strategoids.random.nextDouble() * maxdistance;
        Double arc = Main.strategoids.random.nextDouble() * 360;
        //BufferedReader input = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(Strategoids.FILE_SHAPEFILE)));
//        Ship s = ObjectSpawn.create_from_shapefile_Ship(shiptype, 1,
//                x + Math.cos(arc) * dist,
//                y + Math.sin(arc) * dist,
//                Strategoids.random.nextDouble() * 360, faction, input);
        Ship s = create_ship_from_storage(shiptype, x + Math.cos(arc) * dist, y + Math.sin(arc) * dist, Main.strategoids.random.nextDouble() * 360, faction);
        return s;
    }

    public void genRandomShip_add(String shiptype, double x, double y, double maxdistance, Faction faction) {
        Ship s = genRandomShip(shiptype, x, y, maxdistance, faction);
        s.order_attackMove(0, 0);
        ships.add(s);
    }

    public void loadSituationFile() {
        BufferedReader input = null;
        Long file_hash = gethash(Main.strategoids.FILE_SITUATIONFILE);
        try {
            //input = new BufferedReader(new FileReader(Fortress.FILE_SITUATIONFILE.getFile()));
            System.err.println("loading file");
            input = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(Main.strategoids.FILE_SITUATIONFILE)));
            System.err.println("loaded file.. OMG!");
            String read = null;
            ArrayList<String> forloop = new ArrayList<String>();
            int forloop_count = 0;
            boolean inforloop = false;
            while ((read = input.readLine()) != null) {
                if (read.startsWith("#")) {
                    //System.err.println(read);
                } else if (read.startsWith("FOR")) {
                    String[] temps = read.split(" ");
                    forloop.clear();
                    forloop_count = Integer.valueOf(temps[1]);
                    inforloop = true;
                } else if (read.startsWith("ROF")) {
                    if (inforloop) {
                        for (int i = 0; i < forloop_count; i++) {
                            for (String s : forloop) {
                                loadSituationFile_processLine(s); //for loop
                            }
                        }
                    }
                    inforloop = false;
                }
                if (read.startsWith("FIXEDSEED")) {
                    Main.strategoids.random.setSeed(file_hash);
                    System.out.println("FixedSeed: random value: " + Main.strategoids.random.nextLong());
                } else {
                    if (inforloop) {
                        forloop.add(read);
                    } else {
                        loadSituationFile_processLine(read);
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                input.close();
            } catch (IOException ex) {
                Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void loadSituationFile_processLine(String procline) {
        BufferedReader input = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(Main.strategoids.FILE_SHAPEFILE)));
        if (procline.startsWith("newfaction ")) {  //#newfaction <string:name> <double:r>,<double:g>,<double:b> <int:faction identifier> - creates a faction
            ArrayList<String> templist = Main.strategoids.string_matches(procline, "\"[^\"]+\"|[^\\W]+");
            factions.add(new Faction(templist.get(1).replace("\"", ""), Integer.valueOf(templist.get(5)).byteValue(), Double.valueOf(templist.get(2)), Double.valueOf(templist.get(3)), Double.valueOf(templist.get(4))));
            System.out.println("loadSituationFile: Adding " + factions.get(factions.size() - 1));
        }
        if (procline.startsWith("spawnship ")) { //#spawnship <int:faction identifier> <string:ship type> <double:x>,<double:y> <double:rotation (degrees)> - creates a new ship
            ArrayList<String> templist = Main.strategoids.string_matches(procline, "\"[^\"]+\"|[^\\s|,]+");
            //ships.add(ObjectSpawn.create_from_shapefile_Ship(templist.get(2).replace("\"", ""), 1d, Double.valueOf(templist.get(3)), Double.valueOf(templist.get(4)), Double.valueOf(templist.get(5)), getFaction(Integer.valueOf(templist.get(1)).byteValue()), input));
            ships.add(create_ship_from_storage(templist.get(2).replace("\"", ""), Double.valueOf(templist.get(3)), Double.valueOf(templist.get(4)), Double.valueOf(templist.get(5)), getFaction(Integer.valueOf(templist.get(1)).byteValue())));
        }
        if (procline.startsWith("spawnship_random ")) { //#spawnship_random <int:faction identifier> <string:ship type> <double:x>,<double:y> <double:max distance from x/y> - creates a new ship at random coordinates
            ArrayList<String> templist = Main.strategoids.string_matches(procline, "\"[^\"]+\"|[^\\s|,]+");
            ships.add(genRandomShip(templist.get(2).replace("\"", ""), Double.valueOf(templist.get(3)), Double.valueOf(templist.get(4)), Double.valueOf(templist.get(5)), getFaction(Integer.valueOf(templist.get(1)).byteValue())));
        }
        if (procline.startsWith("order_attackmove")) {
            String[] tempstrings = procline.split(" ");
            tempstrings = tempstrings[1].split(",");
            ships.get(ships.size() - 1).order_attackMove(Double.valueOf(tempstrings[0]), Double.valueOf(tempstrings[1]));
        }
        if (procline.startsWith("SETSEED")) {
            String[] tempstrings = procline.split(" ");
            Main.strategoids.random.setSeed(Long.valueOf(tempstrings[1]));
            System.out.println("setting seed... Random value: " + Main.strategoids.random.nextLong());
        }
    }

    public Faction getFaction(byte b) {
        for (Faction f : factions) {
            if (f.fID == b) {
                return f;
            }
        }
        return null;
    }

    /**
     * Gets a hash from a given file. Uses the MD5 algorithm, but rather than 
     * displaying it as a string, returns it as a long. (for use with
     * random.setseed())
     * @param file
     * @return
     */
    public Long gethash(String file) {
        {
            BufferedInputStream bis = null;
            try {
                bis = new BufferedInputStream(this.getClass().getResourceAsStream(file));
                byte[] by = new byte[1];
                MessageDigest m = MessageDigest.getInstance("MD5");
                while (bis.available() > 0) {
                    bis.read(by);
                    m.update(by);
                }
                return new BigInteger(m.digest()).longValue();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return -1L;
        }
    }

    public void create_Storage(GL gl) {
        storage = new Storage();
        storage.Storage_buildGL(gl);
    }

    /**
     * TODO: move component cloning to Storage.cloneship()?
     * @param identifier
     * @param xpos
     * @param ypos
     * @param rot
     * @param faction
     * @return
     */
    public Ship create_ship_from_storage(String identifier, Double xpos, Double ypos, Double rot, Faction faction) {
        Ship s = Main.strategoids.game.storage.cloneShip(identifier);
        s.ship_graphic_displayList = Main.strategoids.game.storage.getDisplayList_byString(s.identifier);

        LinkedList<IComponent> newComponents = new LinkedList<IComponent>();
        for (IComponent c : s.components) {
            if (c.getComponentType() == IComponent.ComponentType.builder) {
                newComponents.add(new Builder(s));
            }
        }
        s.components = newComponents;

        if (xpos != null) {
            s.xpos = xpos;
        }
        if (ypos != null) {
            s.ypos = ypos;
        }
        if (faction != null) {
            s.faction = faction;
        }
        if (rot != null) {
            s.rot = rot;
        }
        return s;
    }


    public void checkTutorial_b(String tutorialID) {
        if (THIS_IS_TUTORIAL) {
            tutorial_objectives.unlockObjective(tutorialID);
        }
    }
}
