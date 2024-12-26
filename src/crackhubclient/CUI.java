package crackhubclient;

import static crackhubclient.Util.print;
import java.util.ArrayList;
import java.util.List;

public class CUI {

    private CUI() {
        //> コンストラクタの記述が無いとSonarQubeがCode Smellと判定してしまうので明示的に何もしないコンストラクタを実装
        //<japmutt.png
    }

    //color text
    public static String G(String i) {
        return "\u001B[30;42m[" + i + "]\u001B[0m";
    }

    public static void dropshadoWindow(int h, int w, int posx, int posy, String title, String[] items) {
        dropshadoWindow(h, w, posx, posy, title, items, false);
    }

    //put a red '>' at the first chunk, used for fitgirl startup info
    public static void dropshadoWindow(int h, int w, int posx, int posy, String title, String[] items, boolean nfo) {
        byte windowcolor = (byte) 34; //blue
        byte shadowColor = (byte) 32; //green
        byte textColor = (byte) 30; //black
        if ((w & 1) != 1) { //if width is odd, make it even (this is important because each 1 width is 2 charicters 
            w++;
        }
        //items[] logic
        int max = 80;
        //logic to limit string length so it doesnt break the box
        List<String> result = new ArrayList<>();
        for (String s : items) {
            while (s.length() > max) {
                result.add(s.substring(0, max));
                s = s.substring(max);
            }
            result.add(s);
        }
        items = result.toArray(new String[0]);

        //auto resize the 'window'
        int longest = 0;
        for (String s : items) {
            longest = (longest <= s.length()) ? s.length() : longest;
        }
        if ((longest >> 1) > w - 2) {
            w = (longest >> 1) + 4; // /2
        }
        if (items.length > h - 3) {
            h = items.length + 3;
        }

        //title logic
        String titlefinal = "[ " + title + ((((title.length() + 4) & 1) == 1) ? " ]═" : " ]");
        char[] t = titlefinal.toCharArray();
        if (w - 2 < t.length >> 1) {
            w = (titlefinal.length() >> 1) + 2;
        }
        int numchunks = t.length >> 1;
        int chunk = 0;
        //prebuilt escape sequences
        String ESC = "\033[";
        String RST = "\033[0m";
        String esccolor = ESC + windowcolor + "m";
        String bgesccolor = ESC + (windowcolor + 10) + "m";
        String txtesccolor = ESC + (windowcolor + 10) + ";" + textColor + "m";
        for (int hi = 0; hi < h; hi++) {
            boolean hiz = hi == 0;
            boolean hih = hi == (h - 1);
            int schunk = 0;
            for (int wi = 0; wi < w; wi++) {
                //bit pack the 4 desired "states" into a number and switch based apon the result
                switch ((hiz ? 1 : 0) | (hih ? 2 : 0) | ((wi == 0) ? 4 : 0) | ((wi == (w - 1)) ? 8 : 0)) {
                    case 1: //first line exclusively 
                        if (chunk != (numchunks << 1)) {
                            System.out.print(bgesccolor + t[chunk] + t[chunk + 1] + RST);
                            chunk += 2;
                            break;
                        }
                    //fall through to the next print statement THEN break
                    case 2: //last line exclusively 
                        System.out.print(bgesccolor + "══" + RST);
                        break;
                    case 4: //left side wall
                        System.out.print(bgesccolor + "║" + RST + esccolor + "█" + RST);
                        break;
                    case 5: //0, 0 (top left) corner
                        System.out.print(bgesccolor + "╔═" + RST);
                        break;
                    case 6: //h, 0 (bottom left) corner
                        System.out.print(bgesccolor + "╚═" + RST);
                        break;
                    case 8: //right side wall
                        System.out.print(esccolor + "█" + RST + bgesccolor + "║" + RST);
                        break;
                    case 9: //0, w (top right) corner
                        System.out.print(bgesccolor + "═╗" + RST);
                        break;
                    case 10: //h, w (bottom right) corner
                        System.out.print(bgesccolor + "═╝" + RST);
                        break;
                    default:
                        //print items or fill the backround
                        if ((hi - 1) >= items.length) {
                            System.out.print(esccolor + "██" + RST);
                        } else {
                            String itemfinal = items[hi - 1] + ((((items[hi - 1].length()) & 1) == 1) ? " " : "");
                            char[] s = itemfinal.toCharArray();
                            int numschunks = s.length >> 1;
                            if (schunk != (numschunks << 1)) {
                                if (wi == 1 && nfo && s[0] == '>') {
                                    System.out.print(ESC + (windowcolor + 10) + ";31m" + s[schunk] + s[schunk + 1] + RST);

                                } else {
                                    System.out.print(txtesccolor + s[schunk] + s[schunk + 1] + RST);
                                }
                                schunk += 2;
                            } else {
                                System.out.print(esccolor + "██" + RST);
                            }
                        }
                        break;
                }
            }
            //edge of the drop shadow on all exept the first line
            System.out.print(((hiz) ? "" : ESC + shadowColor + "m" + "▓▓") + RST + "\r\n");
        }
        //bottom of the drop shaddow
        for (int wi = -1; wi < w; wi++) {
            System.out.print(ESC + shadowColor + "m" + ((wi == -1) ? "  " : "▓▓") + RST);
        }
    }

    public static String progressBar(int total, int progress) {
        progress++;
        return String.format("[%d/%d](%f%s)", progress, total, ((float) progress / total) * 100f, "%");
    }

    //duplicate errors 
    public static void linkdead() {
        print("\u001B[30;41;5m[ERROR]\u001B[0;31m link is dead\u001B[0m");
    }

    public static void DDGtimeout() {
        print("\u001B[30;41;5m[ERROR]\u001B[0;31m DDoSguard timed out!\u001B[0m");
    }
}
