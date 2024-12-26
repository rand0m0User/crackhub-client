package crackhubclient.site;

import static crackhubclient.CUI.*;
import crackhubclient.DDGhandler;
import crackhubclient.Main;
import static crackhubclient.Main.*;
import static crackhubclient.Util.*;

public class CrackHubSite extends Site {

    //VARS: sitewide
    private static final String site = "https://scene.crackhub.site/page/";
    //private static final String searchsite = "?s=";
    private static final String sceneLineOfInterest = "<p class=\"read-more-container\"><a title=\"";
    private static final String pageNumbers = "<a class=\"page-numbers\" href=\"";
    private static final String gamePageLineOfInterest = "<pre class=\"wp-block-code\">";

    //i know, useless to update code reguarding a dead site, it was a good DDL site other
    //than using zippyshare then krakenfiles was a pain in the rear, warenting this very client to be written...
    @Override
    public void printNFO(String[] choice) throws Exception { //not like this will ever work again
        String[] links = this.fetch(choice);
        String[] nfo = new String[1];
        //a fix for console spam because the old one expected 
        //the NFO link to be first 100% of the time
        for (String link : links) {
            if (link.contains(".nfo: ")) {
                nfo[0] = link;
                break;
            }
        }
        if (nfo[0] != null) {
            Main.kf.init(nfo);
            Main.kf.run(1, false, true, null); //CURL, be quiet, nodir
            String[] file = loadstring(nfo[0].split(": ")[0]).split("\n");
            for (String line : file) {
                print(line);
            }
        } else {
            print("no NFO found in that page, data:" + nfo[0]);
        }
        dropshadoWindow(2, 2, 5, 5, "Game Info", nfo);
    }

    public String[] fetch(String[] choice) throws Exception {
        String[] links;
        if (gamePageHasChanged()) {
            //curl(choice[2], "game.html");
            GAME_HTML = webget(choice[2]);
            gamePageChangeAcknolage();
        }
        if (DDGhandler.checkddosguard(GAME_HTML, choice[2], 10)) {//deal with DDoSguard getting in the way
            DDGtimeout();
            return null;
        }
        //String[] file = load("game.html");
        //handle being flagged by ddosguard......

        for (int i = 0; i < GAME_HTML.length; i++) { //parse the download links from the HTML page
            if (GAME_HTML[i].contains(gamePageLineOfInterest)) {
                int lines = 0;
                for (int c = i; c < GAME_HTML.length; c++) {
                    if (GAME_HTML[c].contains("</pre>")) {
                        break;
                    } else {
                        lines++;
                    }
                    if (c == GAME_HTML.length - 10) {
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
                            links[l] = GAME_HTML[i].split(">")[1];//dirty hack
                        } catch (Exception e) {
                            i++;
                            links[l] = GAME_HTML[i];
                        }
                        first = false;
                    } else {
                        links[l] = GAME_HTML[i + l];
                    }
                }
                print(lines + " links found!");
                return links;
            }
            if (i == GAME_HTML.length - 1) {
                linkdead();
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
            SCENE_HTML = webget(siteURL);
            //curl(siteURL, "scene.html");
        }
        DDGhandler.checkddosguard(SCENE_HTML, siteURL, 10);//deal with DDoSguard getting in the way
        //String[] file = load("scene.html");
        print("parsing HTML...");
        int games = 0;
        for (String i : SCENE_HTML) {
            if (i.contains(sceneLineOfInterest)) {
                //count all occurences of LineOfInterest
                games++;
            }
        }
        if (games == 0) {
            linkdead();
        }
        //print(games + " found!");
        list = new String[games][3];
        int game = 0; //waste 4 bytes of ram on a counter

        for (int i = 0; i < SCENE_HTML.length; i++) { //parse for real now
            if (SCENE_HTML[i].contains(sceneLineOfInterest)) {
                //an mess to pull relevant data from <article> tags in the HTML and pack it into a [][]
                String[] articleSplit = SCENE_HTML[i].split("\"");
                String name = articleSplit[3];
                list[game][0] = name;
                String link = articleSplit[7];
                list[game][2] = link;
                String type = SCENE_HTML[i + 6].split("\"")[6].split("/")[0].replace(">", "").replace("<", "");
                //minor code cleanup 
                list[game][1] = type;
                //print the options so we dont have to somewhere else
                game++;
                //print("[" + (game) + "] type: [" + type + "] " + name);
                print(String.format("\u001B[30;%dm[%d]\u001B[0;%dm type: [%s] %s \u001B[0m", ((game & 1) == 0) ? (byte) 42 : (byte) 46, game + 1, ((game & 1) == 0) ? (byte) 32 : (byte) 36, type, name));
            }
            if (SCENE_HTML[i].contains(pageNumbers)) {
                //messy oneliner to pull the total number of pages from the HTML
                totalPages = Integer.parseInt(SCENE_HTML[i].split("\">")[2].split(">")[1].split("<")[0].replace(",", ""));
            }
        }
        listPageChangeAcknolage();
        return list;
    }
}
