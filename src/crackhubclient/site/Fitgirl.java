package crackhubclient.site;

import crackhubclient.DDGhandler;
import static crackhubclient.Util.*;

public class Fitgirl {

    public static void display() {

        try {
            curl("https://fitgirl-repacks.site/", "fitgirl.html");
            if (DDGhandler.checkddosguard("fitgirl.html", "https://fitgirl-repacks.site/", 3)) {//deal with DDoSguard getting in the way
                print("[INFO] DDoSguard timed out!");
                return;
            }
            String[] file = load("fitgirl.html");
            print("parsing HTML...");

            String[] upcoming = new String[0];
            String[] complete = new String[0];
            for (int i = 0; i < file.length; i++) { //parse for real now
                if (file[i].contains("<strong>Upcoming repacks</strong>")) {
                    while (true) {
                        i++;
                        if ("</h3>".equals(file[i])) {
                            break;
                        }
                        //<p><span style="color: #339966;">⇢ (Game)</span><br />
                        if (file[i].contains("</span")) {
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
            print("fitgirl's site info:\nUpcoming repacks:");
            for (String g : upcoming) {
                print("    > " + g);
            }
            print("recent repacks:");
            for (int i = 0; i < 10; i++) {
                print("    > " + complete[i]);
            }
        } catch (Exception ex) {
            print("[ERROR][fitgirl display]: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private static String sanitize(String in) {
        return in.replace("&#8217;", "'").replace("&#8211;", "-").replace("&#8211;", "").replace("&#038;", "&");
    }
}
