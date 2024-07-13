package crackhubclient.site;

import crackhubclient.DDGhandler;
import static crackhubclient.Main.*;
import static crackhubclient.Util.*;

public class CrackHub {

    private static String[][] list; // format: [entry number] [name, type, link]

    //VARS: sitewide
    private static final String site = "https://scene.crackhub.site/page/";
    //private static final String searchsite = "?s=";
    private static final String sceneLineOfInterest = "<p class=\"read-more-container\"><a title=\"";
    private static final String pageNumbers = "<a class=\"page-numbers\" href=\"";
    private static final String gamePageLineOfInterest = "<pre class=\"wp-block-code\">";

    private int totalPages = 0;

    public int getTotalPages() {
        return totalPages;
    }

    public String[] fetch(String[] choice) throws Exception {
        String[] links;
        if (gamePageHasChanged()) {
            curl(choice[2], "game.html");
            gamePageChangeAcknolage();
        }
        if (DDGhandler.checkddosguard("game.html", choice[2], 10)) {//deal with DDoSguard getting in the way
            print("[INFO] DDoSguard timed out!");
            return null;
        }
        String[] file = load("game.html");
        //handle being flagged by ddosguard......

        for (int i = 0; i < file.length; i++) { //parse the download links from the HTML page
            if (file[i].contains(gamePageLineOfInterest)) {
                int lines = 0;
                for (int c = i; c < file.length; c++) {
                    if (file[c].contains("</pre>")) {
                        break;
                    } else {
                        lines++;
                    }
                    if (c == file.length - 10) {
                        print("[CRITICAL ERROR!] closing '</pre>' tag never hit! THIS SHOULD NEVER EVER HAPPEN!!!");
                    }
                }
                links = new String[lines];
                boolean first = true;
                for (int l = 0; l < lines; l++) {
                    if (first) {
                        //fixed a bug where a newline after "<pre class="wp-block-code">"
                        //would crash, this was normally not the case
                        try {
                            links[l] = file[i].split(">")[1];//dirty hack
                        } catch (Exception e) {
                            i++;
                            links[l] = file[i];
                        }
                        first = false;
                    } else {
                        links[l] = file[i + l];
                    }
                }
                print(lines + " links found!");
                return links;
            }
            if (i == file.length - 1) {
                print("[ERROR] link is dead");
            }
        }
        return null;
    }

    public String[][] display(int page, boolean listPageHasChanged, boolean searching) throws Exception {

        String siteURL = site + page + "/";
        if (isSearching()) {
            //print("isSearching() true");
            siteURL += "?s=" + getSearchterm();
            changeListPage();
        }
        if (listPageHasChanged) {
            print("fetching page: " + siteURL);
            curl(siteURL, "scene.html");
        }
        DDGhandler.checkddosguard("scene.html", siteURL, 10);//deal with DDoSguard getting in the way
        String[] file = load("scene.html");
        print("parsing HTML...");
        int games = 0;
        for (String i : file) {
            if (i.contains(sceneLineOfInterest)) {
                //count all occurences of LineOfInterest
                games++;
            }
        }
        if (games == 0) {
            print("[ERROR] link is dead");
        }
        //print(games + " found!");
        list = new String[games][3];
        int game = 0; //waste 4 bytes of ram on a counter

        for (int i = 0; i < file.length; i++) { //parse for real now
            if (file[i].contains(sceneLineOfInterest)) {
                //an mess to pull relevant data from <article> tags in the HTML and pack it into a [][]
                String[] articleSplit = file[i].split("\"");
                String name = articleSplit[3];
                list[game][0] = name;
                String link = articleSplit[7];
                list[game][2] = link;
                String type = file[i + 6].split("\"")[6].split("/")[0].replace(">", "").replace("<", "");
                //minor code cleanup 
                list[game][1] = type;
                //print the options so we dont have to somewhere else
                game++;
                //print("[" + (game) + "] type: [" + type + "] " + name);
                print(String.format("[%d] type: [%s] %s", game, type, name));
            }
            if (file[i].contains(pageNumbers)) {
                //messy oneliner to pull the total number of pages from the HTML
                totalPages = Integer.parseInt(file[i].split("\">")[2].split(">")[1].split("<")[0].replace(",", ""));
            }
        }
        listPageChangeAcknolage();
        return list;
    }
}
