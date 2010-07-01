/*
 * This file is part of the Strategoids Real-Time-Strategy game/engine project
 *  and is licensed under the GPLv3.
 * See 'Strategoids.java' for more info.
 */

package strategoids.misc;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * An output stream thingy. Used to 'fetch' the error stream data so that it
 * can be rendered in-game.
 */
public class MyOutputstream extends OutputStream{
    public class MyString {
        public String string;
        public long date;

        public MyString(String string) {
            this.string = string;
            this.date = System.currentTimeMillis();
        }
    }


    private String s;
    private ArrayList<MyString> strings;
    public MyOutputstream(String s) {
        this.s = s;
        strings = new ArrayList<MyString>();
    }

    @Override
    public void write(int b) throws IOException {
        s = s + "" + String.valueOf((char)b);
        if (b == 10) {
            strings.add(new MyString(s));
            s = "";
        }
        System.out.write(b);
    }

    public String getstring() {
        String temps = "";
        for(MyString ms : strings) {
            if (ms.date + 2000 > System.currentTimeMillis()) {
                temps += ms.string;
            }
        }
        if (temps.equals("")) {
            strings.clear();
        }
        return temps;
    }

    public ArrayList<String> getstrings() {
        ArrayList<String> temps = new ArrayList<String>();
        for (int i = 0; i < strings.size(); i++) {
            if (strings.get(i).date + 5000 > System.currentTimeMillis()) {
                temps.add(strings.get(i).string);
            } else {
                strings.remove(i);
                i--;
            }
        }
        //System.out.println("strings array size: " + strings.size());
        return temps;

        /*ArrayList<String> temps = new ArrayList<String>();
        for(MyString ms : strings) {
            if (ms.date + 5000 > System.currentTimeMillis()) {
                temps.add(ms.string);
            }
        }
        if (temps.size() == 0) {
            strings.clear();
        }
        //System.out.println("strings array size: " + strings.size());
        return temps;*/
    }

}
