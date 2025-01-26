package crackhubclient.site;

import static crackhubclient.CUI.*;
import crackhubclient.DDGhandler;
import crackhubclient.Main;
import static crackhubclient.Main.*;
import static crackhubclient.Util.*;
import crackhubclient.providers.Provider;

public class FitgirlSite extends Site {

    //private static String[][] list; // format: [entry number] [name, type, link]
    //VARS: sitewide
    private static final String site = "https://fitgirl-repacks.site/";
    //private static final String searchsite = "?s=";
    private static final String sceneLineOfInterest = "<article id=\"";
    private static final String pageNumbers = "<a class=\"page-numbers\"";
    private static final String gamePageLineOfInterest = "<h3>Download Mirrors (Direct Links)</h3>";

    private static linkholder[] allLinks = new linkholder[0]; // format: [index] [provider name] [link(s)...]

    //do not use with large data objects
    private static /* utility */ linkholder[] pushlinkholder(linkholder[] in, linkholder value) {
        linkholder[] a = new linkholder[in.length + 1];
        System.arraycopy(in, 0, a, 0, in.length);
        a[in.length] = value;
        return a;
    }

    @Override
    public void printNFO(String[] choice) throws Exception {
        fetch(choice, true);
        dropshadoWindow(2, 2, 5, 5, "Game Info", NFO_TEXT);
        print("\n");
    }

    @Override
    public String[] fetch(String[] choice) throws Exception {
        return fetch(choice, false);
    }

    private String[] fetch(String[] choice, boolean nochoice) throws Exception {
        allLinks = new linkholder[0];
        if (gamePageHasChanged()) {
            GAME_HTML = webget(choice[2]);
            //curl(choice[2], "game.html");
            gamePageChangeAcknolage();
        }
        if (DDGhandler.checkddosguard(GAME_HTML, choice[2], 10)) {//deal with DDoSguard getting in the way
            DDGtimeout();
            return null;
        }
        //String[] file = load("game.html");
        //handle being flagged by ddosguard......
        boolean read = false;
        boolean readnfo = false;
        NFO_TEXT = new String[0]; //clear
        String singlename = "NULL";
        for (int i = 0; i < GAME_HTML.length; i++) { //parse the download links from the HTML page
            if (GAME_HTML[i].contains("<meta property=\"og:title\" content=\"")) {
                singlename = sanitize(GAME_HTML[i].split("content=\"")[1].split("\"")[0]).replace(":", "-");
            }
            if (GAME_HTML[i].contains(gamePageLineOfInterest)) {
                read = true;
            }
            if (read) {
                boolean unfinished = GAME_HTML[i].contains("<li>SOON");
                String[] s = GAME_HTML[i].split("\"");
                if (GAME_HTML[i].contains("Filehoster:") && GAME_HTML[i + 1].contains("su-spoiler")) { //single
                    String provider = s[6].split("</a>")[0].replaceFirst(">", "");
                    String[] spoilerLinks = new String[0];

                    if (GAME_HTML[i + 1].contains("su-spoiler")) { //next line in the file is a spoiler?
                        int j = 0;
                        boolean nospoiler = false;
                        while (true) { //should never be more than 50 links... right?
                            if (j == GAME_HTML.length) {
                                break;
                            }
                            if (GAME_HTML[i + j].startsWith("<a href=\"")) {
                                String link = sanitize(GAME_HTML[i + j].split("nofollow\">")[1].split("<")[0]) + ": " + GAME_HTML[i + j].split("\"")[1];
                                spoilerLinks = push(spoilerLinks, link);
                            }
                            if (GAME_HTML[i + j].contains("</div></div></li>") || nospoiler) {
                                allLinks = pushlinkholder(allLinks, new linkholder(provider, spoilerLinks, unfinished));
                                i += (j); //skip the things we allready saw
                                break;
                            }
                            j++;
                        }
                    }
                    print(String.format("found: %d links for: %s!", spoilerLinks.length, provider));

                }
                //handle pastebin/privatebin links properly
                if (GAME_HTML[i].contains("<li><a href=\"https://paste.fitgirl-repacks.site") && !GAME_HTML[i + 1].contains("su-spoiler")) {
                    String provider = s[6].split("</a>")[0].replaceFirst(">", "");
                    //push the pastebin link
                    print(String.format("found a pastebin encoded link for: %s!", provider));
                    allLinks = pushlinkholder(allLinks, new linkholder(provider, new String[]{s[1]}, unfinished));
                } else {
                    //handle single links properly
                    if (GAME_HTML[i].contains("https") && GAME_HTML[i].contains("Filehoster")) {
                        String link = sanitize(singlename) + Provider.seperator + GAME_HTML[i].split("\"")[1].split("\"")[0];
                        String provider = GAME_HTML[i].split("\"")[6].split("</a>")[0].replaceFirst(">", "");
                        //push the single file link
                        print(String.format("a link for: %s!", provider));
                        allLinks = pushlinkholder(allLinks, new linkholder(provider, new String[]{link}, unfinished));
                    }
                }

                if (GAME_HTML[i].contains("Game Description")) { //end of download section
                    readnfo = true;
                    NFO_TEXT = push(NFO_TEXT, sanitize(GAME_HTML[i + 1]).replace("</p>", ""));
                }

                if (readnfo) {
                    if (GAME_HTML[i].startsWith("<p>") || GAME_HTML[i].startsWith("<li>")) {
                        NFO_TEXT = push(NFO_TEXT, sanitize(GAME_HTML[i]
                                .replace("<li>", "").replace("</li>", "")
                                .replace("<p>", "").replace("</p>", "")
                                .replace("<br>", "").replace("</br>", "")
                                .replace("<b>", "").replace("</b>", "")
                                .replace("<b>", "").replace("</b>", "")
                        ));
                    }
                }
                if (GAME_HTML[i].contains("</article>")) { //end of page
                    break;
                }
                if (i == GAME_HTML.length - 1) {
                    linkdead();
                }
            }
        }
        if (nochoice) { //if its reading the NFO only, we can pass null because its not going anywhere
            return null;
        }
        boolean optionLoop1 = true;
        while (optionLoop1) { //main menu
            int dlchoice;
            for (int i = 0; i < allLinks.length; i++) {
                print(String.format("\u001B[30;42m[%d]\u001B[0m %s %s ", i, allLinks[i].unfinished ? "(unfinished)" : "", allLinks[i].provider));
            }
            print("download selection "+G("1 - " + (allLinks.length)));

            //used in selecting an item
            String optionLoop1input = Main.br.readLine();
            switch (optionLoop1input) {
                case "b": //back
                    optionLoop1 = false;
                    return new String[]{""};
            }
            dlchoice = Main.numberInput(optionLoop1input);
            if (dlchoice > allLinks.length) {
                return allLinks[allLinks.length].links;
            }
            return allLinks[dlchoice].links;
        } //optionLoop1 ~ main menu
        return null;
    }

    @Override
    public String[][] display(int page, boolean listPageHasChanged, boolean searching) throws Exception {
        isfg = true;
        String siteURL = site;

        if (page != 1) {
            siteURL = site + "page/" + page + "/";
        }
        if (isSearching()) {
            //print("isSearching() true");
            siteURL += "?s=" + getSearchterm();
            changeListPage();
        }
        if (listPageHasChanged) {
            print("fetching page: " + siteURL);
            SITE_HTML = webget(siteURL);
            //curl(siteURL, "scene.html");
        }
        DDGhandler.checkddosguard(SITE_HTML, siteURL, 10);//deal with DDoSguard getting in the way
        //String[] file = load("scene.html");
        print("parsing HTML...");
        int games = 0;
        for (String i : SITE_HTML) {
            if (i.contains(sceneLineOfInterest)) {// && i.contains("category-lossless-repack")) {
                //count all occurences of LineOfInterest
                games++;
            }
        }
        if (games == 0) {
            linkdead();
        }
        //print(games + " found!");
        list = new String[games][3];
        int game = 0;
        int flags = 0;
        String type = "null";
        //an mess to pull relevant data from <article> tags in the HTML and pack it into a [][]
        for (String l : SITE_HTML) {
            //parse for real now
            if (l.contains("<span class=\"cat-links\"><a href=")) {
                flags |= 4;
                String[] articleSplit = l.split("\"");
                type = articleSplit[6].split("</a>")[0].replaceFirst(">", "");
                list[game][1] = type;
            }
            if (l.contains("<h1 class=\"entry-title\"><a href=")) {
                flags |= 3; // name & link populated 
                String[] articleSplit = l.split("\""); //not a normal dash! -------------------------- ↓
                String name = sanitize(articleSplit[6].split("</a>")[0].replaceFirst(">", "")).split(" – ")[0].replace(":", "");
                list[game][0] = name;
                String link = articleSplit[3];
                list[game][2] = link;
                //print the options so we dont have to somewhere else
                //print("[" + (game) + "] type: [" + type + "] " + name);
                print(String.format("\u001B[30;%dm[%d]\u001B[0;%dm type: [%s] %s \u001B[0m", ((game & 1) == 0) ? (byte) 42 : (byte) 46, game + 1, ((game & 1) == 0) ? (byte) 32 : (byte) 36, type, name));
            }
            if (l.contains("<div id=\"jBnskDj9\">") && flags == 7) {
                flags = 0;
                game++;
            }
            if (l.contains(pageNumbers)) {
                //messy oneliner to pull the total number of pages from the HTML
                totalPages = Integer.parseInt(l.split("\">")[1].split("<")[0].replace(",", ""));
            }
        }
        listPageChangeAcknolage();
        return list;

    }

    //because string[] wasnt enough
    static class linkholder {

        public String provider;
        public String[] links;
        public boolean unfinished = false;

        linkholder(String provider, String[] links, boolean unfinished) {
            this.provider = provider;
            this.links = links;
            this.unfinished = unfinished;
        }
    }
}
