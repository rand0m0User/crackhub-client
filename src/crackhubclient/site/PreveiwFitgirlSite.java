package crackhubclient.site;

import static crackhubclient.CUI.dropshadoWindow;
import static crackhubclient.Util.*;
import java.util.Arrays;

public class PreveiwFitgirlSite {

    public static void display() {
        try {
            //curl("https://fitgirl-repacks.site/", "fitgirl.html");
            String[] file = webget("https://fitgirl-repacks.site/");
//            if (DDGhandler.checkddosguard(file, "https://fitgirl-repacks.site/", 3)) {//deal with DDoSguard getting in the way
//                print("[INFO] DDoSguard timed out!");
//                return;
//            }
            //String[] file = load("fitgirl.html");
            print("parsing HTML...");

            String[] upcoming = new String[0];
            String[] complete = new String[0];
            //for (String s : file) { //debug :DDDDD
            //    print(s);
            //}
            for (int i = 0; i < file.length; i++) { //parse for real now
                if (file[i].contains("rel=\"bookmark\">Upcoming Repacks</a></h1>") || file[i].contains("<strong>Upcoming repacks</strong>")) {
                    while (true) {
                        i++;
                        if ("</h3>".equals(file[i])) {
                            break;
                        }
                        //<p><span style="color: #339966;">⇢ (Game)</span><br />
                        if (file[i].contains("</span") && file[i].contains("⇢")) {
                            String game = file[i].split("⇢")[1].split("</span")[0].trim();
                            if (!game.contains("More Switch/PS3 Games")) {
                                upcoming = push(upcoming, sanitize(game));
                            }
                        }
                    }
                }
                if (file[i].contains("srcset=\"\" alt=\"")) {
                    complete = push(complete, sanitize(file[i].split("srcset=\"\" alt=\"")[1].split("\" class=\"")[0]));
                }
            }
//                if (file[i].contains("style=\"\" srcset=\"\" alt=\"")) {
//                    complete = push(complete, sanitize(file[i].split("srcset=\"\" alt=\"")[1].split("\" class=")[0]));
//                }
//                if (file[i].contains("class=\"\" aria-label=\"")) {
//                    complete = push(complete, sanitize(file[i].split("aria-label=\"")[1].split("\"><span")[0]));
//                }

            //now print the parsed info
            print("fitgirl's site info:");
            for (int i = 0; i < upcoming.length; i++) {
                upcoming[i] = "> " + upcoming[i];
            }
            Arrays.sort(upcoming);
            dropshadoWindow(2, 2, 5, 5, "Upcoming repacks", upcoming, true);
            print("\n");
            for (int i = 0; i < complete.length; i++) {
                complete[i] = "> " + complete[i].split("-")[0];
            }
            dropshadoWindow(2, 2, 5, 5, "recent repacks", complete, true);
            print("\n");
        } catch (Exception ex) {
            print("[ERROR][fitgirl display]: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private static String sanitize(String in) {
        return in.replace("&#8217;", "'").replace("&#8211;", "-").replace("&#038;", "&");
    }
}
