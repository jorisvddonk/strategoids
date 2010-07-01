/*
 * This file is part of the Strategoids Real-Time-Strategy game/engine project
 *  and is licensed under the GPLv3.
 * See 'Strategoids.java' for more info.
 */
package strategoids.misc;

import strategoids.drawing.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Random;
import strategoids.factions.Faction;
import strategoids.ships.Ship;
import strategoids.ships.bullets.IBullet.BulletType;
import strategoids.ships.components.Thruster;
import strategoids.ships.components.Turret;

/**
 * Spawns objects and random things.
 * Mostly deprecated.
 */
public class ObjectSpawn {

    static double PI = 3.14159265;

    public static ArrayList<Line> create_gon(int numsides, double globalm_x, double globalm_y, double size, double r, double g, double b) {
        ArrayList<Line> lines = new ArrayList<Line>();
        /*double r = 0;
        double g = 0;
        double b = 0;*/
        for (int i = 0; i < numsides; i++) {
            /*if (i % 3 == 0) {
                r = 1;
                g = 0;
                b = 0;
            }
            if (i % 3 == 1) {
                r = 0;
                g = 1;
                b = 0;
            }
            if (i % 3 == 2) {
                r = 0;
                g = 0;
                b = 1;
            }*/


            lines.add(new Line(globalm_x + Math.cos((((i + 0) * (360 / numsides)) + (360 / (numsides * 2))) * (PI / 180)) * size,
                    globalm_y + Math.sin((((i + 0) * (360 / numsides)) + (360 / (numsides * 2))) * (PI / 180)) * size,
                    globalm_x + Math.cos((((i + 1) * (360 / numsides)) + (360 / (numsides * 2))) * (PI / 180)) * size,
                    globalm_y + Math.sin((((i + 1) * (360 / numsides)) + (360 / (numsides * 2))) * (PI / 180)) * size,
                    r,
                    g,
                    b));
        }
        return lines;
    }

    public static LineObject create_gon_LineObject(int numsides, double size, double r, double g, double b) {
        ArrayList<Line> lines = create_gon(numsides, 0, 0, size,r,g,b);
        return new LineObject(lines);
    }

    public static LineObject create_asteroid_LineObject_1(int numsides, double maxsize, double minsize) {
        Random random = new Random();
        ArrayList<Line> lines = new ArrayList<Line>();
        double size1, size2 = 0;
        double prevsize = 0;
        ArrayList<Double> doubles = new ArrayList<Double>();
        for (int i = 0; i < numsides; i++) {
            doubles.add(minsize + (random.nextDouble() * (maxsize - minsize)));
            //doubles.add(random.nextDouble() * maxsize);
        }
        for (int i = 0; i < numsides; i++) {
            size1 = doubles.get(i);
            size2 = doubles.get((i + 1) % numsides);
            lines.add(new Line(Math.cos((((i + 0) * (360 / numsides)) + (360 / (numsides * 2))) * (PI / 180)) * size1,
                    Math.sin((((i + 0) * (360 / numsides)) + (360 / (numsides * 2))) * (PI / 180)) * size1,
                    Math.cos((((i + 1) * (360 / numsides)) + (360 / (numsides * 2))) * (PI / 180)) * size2,
                    Math.sin((((i + 1) * (360 / numsides)) + (360 / (numsides * 2))) * (PI / 180)) * size2,
                    1,
                    1,
                    1));
        }
        return new LineObject(lines);
    }

    public static LineObject create_asteroid_LineObject_2(int numsides, double maxsize, double minsize) {
        Random random = new Random();
        ArrayList<Line> lines = new ArrayList<Line>();
        double size1, size2 = 0;
        ArrayList<Double> doubles = new ArrayList<Double>();
        double prevsize = (minsize + maxsize) / 2;
        for (int i = 0; i < numsides; i++) {
            prevsize += random.nextDouble() * (minsize + maxsize) / (numsides / 3);
            prevsize -= random.nextDouble() * (minsize + maxsize) / (numsides / 3);
            if (prevsize < minsize) {
                prevsize = minsize;
            }
            if (prevsize > maxsize) {
                prevsize = maxsize;
            }
            doubles.add(prevsize);
        }
        for (int i = 0; i < numsides; i++) {
            size1 = doubles.get(i);
            size2 = doubles.get((i + 1) % numsides);
            lines.add(new Line(Math.cos((((i + 0) * (360 / numsides)) + (360 / (numsides * 2))) * (PI / 180)) * size1,
                    Math.sin((((i + 0) * (360 / numsides)) + (360 / (numsides * 2))) * (PI / 180)) * size1,
                    Math.cos((((i + 1) * (360 / numsides)) + (360 / (numsides * 2))) * (PI / 180)) * size2,
                    Math.sin((((i + 1) * (360 / numsides)) + (360 / (numsides * 2))) * (PI / 180)) * size2,
                    1,
                    1,
                    1));
        }
        return new LineObject(lines);
    }


    //Semi-deprecated; may be outdated compared to create_from_shapefile_Ship
    //DEPRECATED
    //@Deprecated
    /*public static LineObject create_from_shapefile_LineObject(String filename, String specificshape, double size) {
        double color_r = 1, color_g = 1, color_b = 1, color_a = 1;
        boolean process = true;
        double shapesize = 1*size;
        Double prevx = null, prevy = null;
        ArrayList<Line> lines = new ArrayList<Line>();
        try {
            BufferedReader input = new BufferedReader(new FileReader(filename));
            String read = null;
        while (( read = input.readLine()) != null){
            if (read.startsWith("#")) {
                //System.err.println(read);
            } else {
                String[] splitstring;
                if (read.toLowerCase().startsWith("setcolor ") && process) {
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
                } else if (read.toLowerCase().startsWith("shape ")) {
                    prevx = null;
                    prevy = null;
                    if (!specificshape.equals("") && specificshape != null) {
                        splitstring = read.split(" ");
                        if (specificshape.equals(splitstring[1])) {
                            process = true;
                        } else {
                            process = false;
                        }
                    }
                } else if (read.toLowerCase().startsWith("endshape") && process) {
                    prevx = null;
                    prevy = null;
                    if (!specificshape.equals("") && specificshape != null) {
                        process = false;
                    }
                } else if (read.toLowerCase().startsWith("newline") && process) {
                    prevx = null;
                    prevy = null;
                } else if (read.toLowerCase().startsWith("v ") && process) {
                    splitstring = read.split(" ");
                    splitstring = splitstring[1].split(",");
                    if (prevx != null && prevy != null) {
                        Line l = new Line(prevx*shapesize, prevy*shapesize, Double.valueOf(splitstring[0])*shapesize, Double.valueOf(splitstring[1])*shapesize, color_r, color_g, color_b);
                        l.ca = color_a;
                        lines.add(l);
                        //System.err.println("Line added: " + l.toString());
                        prevx = Double.valueOf(splitstring[0]);
                        prevy = Double.valueOf(splitstring[1]);
                    } else {
                        prevx = Double.valueOf(splitstring[0]);
                        prevy = Double.valueOf(splitstring[1]);
                    }
                }

            }
        }
        } catch (Exception e) {
            System.err.println("Error in create_from_shapefile_LineObject: " + e.getMessage());
            e.printStackTrace(System.err);
        }
        return new LineObject(lines, -90);
    }*/

    //Has the ability to add thrusters and weapons to ships when compared to create_from_shapefile_LineObject. Thus, this function should be used.
    //DEPRECATED
/*public static Ship create_from_shapefile_Ship(String specificshape, double size, double xpos, double ypos, double rot, Faction faction, BufferedReader input) {
        Ship retship = new Ship(xpos, ypos, rot, specificshape, faction);
        double color_r = 1, color_g = 1, color_b = 1, color_a = 1;
        if (faction != null) {
            color_r = faction.r;
            color_g = faction.g;
            color_b = faction.b;
        }
        boolean process = true;
        double shapesize = 1*size;
        Double prevx = null, prevy = null;
        ArrayList<Line> lines = new ArrayList<Line>();
        try {
            //BufferedReader input = new BufferedReader(new FileReader(Fortress.FILE_SHAPEFILE));
            String read = null;
        while (( read = input.readLine()) != null){
            if (read.startsWith("#")) {
                //System.err.println(read);
            } else {
                String[] splitstring;
                if (read.toLowerCase().startsWith("thruster ") && process) {
                    //thruster <int:id(0-3)>,<double:xorg>,<double:yorg>,<double:rot>,<double:exhaustspeed>,<double:angle>
                    splitstring = read.split(" ");
                    splitstring = splitstring[1].split(",");
                    if (splitstring.length == 6) {
                        if (Integer.valueOf(splitstring[0]) >= 0 && Integer.valueOf(splitstring[0]) <= 3) {
                            Thruster t = new Thruster(Double.valueOf(splitstring[1])*shapesize, Double.valueOf(splitstring[2])*shapesize, Double.valueOf(splitstring[3]), Double.valueOf(splitstring[4]), Double.valueOf(splitstring[5]));
                            retship.thrusters.get(Integer.valueOf(splitstring[0])).add(t);
                        }
                    }
                } else if (read.toLowerCase().startsWith("setcolor ") && process && faction == null) { // don't do this if faction is set...
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
                } else if (read.toLowerCase().startsWith("shape ")) {
                    prevx = null;
                    prevy = null;
                    if (!specificshape.equals("") && specificshape != null) {
                        splitstring = read.split(" ");
                        if (specificshape.equals(splitstring[1])) {
                            process = true;
                        } else {
                            process = false;
                        }
                    }
                } else if (read.toLowerCase().startsWith("endshape") && process) {
                    prevx = null;
                    prevy = null;
                    if (!specificshape.equals("") && specificshape != null) {
                        process = false;
                    }
                } else if (read.toLowerCase().startsWith("newline") && process) {
                    prevx = null;
                    prevy = null;
                } else if (read.toLowerCase().startsWith("turret ") && process) {
                    splitstring = read.split(" ");
                    double fire_rate = Double.valueOf(splitstring[2]);
                    splitstring = splitstring[1].split(",");
                    retship.turrets.add(new Turret(Double.valueOf(splitstring[0])*shapesize,Double.valueOf(splitstring[1])*shapesize,0,fire_rate));
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
                        Line l = new Line(prevx*shapesize, prevy*shapesize, Double.valueOf(splitstring[0])*shapesize, Double.valueOf(splitstring[1])*shapesize, color_r, color_g, color_b);
                        l.ca = color_a;
                        lines.add(l);
                        //System.err.println("Line added: " + l.toString());
                        prevx = Double.valueOf(splitstring[0]);
                        prevy = Double.valueOf(splitstring[1]);
                    } else {
                        prevx = Double.valueOf(splitstring[0]);
                        prevy = Double.valueOf(splitstring[1]);
                    }
                }

            }
        }
        } catch (Exception e) {
            System.err.println("Error in create_from_shapefile_Ship: " + e.getMessage());
            e.printStackTrace(System.err);
        }
        retship.ship_graphic = new LineObject(lines, -90);
        return retship;
    }*/
}
