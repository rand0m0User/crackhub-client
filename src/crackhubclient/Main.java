package crackhubclient;

import crackhubclient.providers.Krakenfiles;
import crackhubclient.providers.Bayfiles;
import crackhubclient.site.CrackHub;
import crackhubclient.site.Fitgirl;
import static crackhubclient.Util.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Main {

    //VARS: state machine
    private static boolean listPageChanged = true;
    private static boolean gamePageChanged = true;

    private static boolean searching = false;
    private static String searchword = "";
    private static final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    public static boolean isSearching() {
        return searching;
    }

    public static String getSearchterm() {
        return searchword;
    }

    public static boolean gamePageHasChanged() {
        return gamePageChanged;
    }

    public static void gamePageChangeAcknolage() {
        gamePageChanged = false;
    }

    public static boolean listPageHasChanged() {
        return listPageChanged;
    }

    public static void changeListPage() {
        listPageChanged = true;
    }

    public static void listPageChangeAcknolage() {
        listPageChanged = false;
    }

    //hopefully a fix for constantly needing to re fetch the page, tripping ddos guard
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
//        args = new String[]{"--testKrakenfiles", "https://krakenfiles.com/view/PTKoD09vQC/file.html"};
//        if (args.length == 2 && "--testKrakenfiles".equals(args[0])) {
//            provider p = new krakenfiles(); //the downloader class
//            p.test(args[1]);
//            return;
//        }
//        args = new String[]{"--testBayfiles", "https://bayfiles.com/F3s42046zd/Five_Nights_at_Freddy_s_Security_Breach_fitgirl_repacks_site_part01_rar"};
//        if (args.length == 2 && "--testBayfiles".equals(args[0])) {
//            provider p = new bayfiles(); //the downloader class
//            p.test(args[1]);
//            return;
//        }

        int page = 1;
        CrackHub cr = new CrackHub();
        Fitgirl.display();
        boolean optionLoop1 = true;
        while (optionLoop1) { //main menu
            try {
                int choice;
                String[][] list = cr.display(page, listPageChanged, searching);
                listPageChanged = false; //it just updated ya see?
                String[] links;
                print(String.format("page: %d/%d", page, cr.getTotalPages()));
                if (searching) {
                    print("selection [1 - " + (list.length) + "] \n[e] exit, [n] next, [p] previous, [r] refresh page [x] exit search");
                } else {
                    print("selection [1 - " + (list.length) + "] \n[e] exit, [n] next, [p] previous, [r] refresh page, [s] set page, [f] redeem fitgirl links, [rf] refresh fitgirl list");//, [t] redeem txt (broken)");
                }
                //used in selecting an item
                String optionLoop1input = br.readLine();
                switch (optionLoop1input) {
                    case "e": //exit
                        optionLoop1 = false;
                        break;
                    case "n": //next
                        page++;
                        listPageChanged = true;
                        continue;
                    case "p": //previous
                        if (page != 1) {
                            page--;
                        }
                        listPageChanged = true;
                        continue;
                    case "s": //set page & search
                        boolean optionLoop2 = true;
                        while (optionLoop2) { //setpage menu
                            print("[b] back, [s] search");
                            print("go to page:");
                            String optionLoop2input = br.readLine();
                            page = numberInput(optionLoop2input);
                            switch (optionLoop2input) {
                                case "s": //search
                                    searching = true;
                                    boolean optionLoop3 = true;
                                    while (optionLoop3) { //search term menu
                                        print("[b] back");
                                        print("enter search term:");
                                        String optionLoop3input = br.readLine();
                                        if ("b".equals(optionLoop3input)) {
                                            searching = false;
                                            searchword = "";
                                        } else {
                                            //back out of "search menu"
                                            searchword = optionLoop3input.replace(" ", "%20");
                                            break;
                                        }
                                        optionLoop3 = false;
                                    } //optionLoop3 ~ search term menu
                                    break;
                                case "b": //back
                                    page = 1;
                                    break;
                                default:
                                    break;
                            }
                            listPageChanged = true;
                            optionLoop2 = false;

                        } //optionLoop2 ~ setpage menu
                        continue;
                    case "r": //refresh
                        listPageChanged = true;
                        continue;
                    case "rf": //refresh fitgirl list
                        Fitgirl.display();
                        continue;
                    //disabled for now (as zippyshare links are no longer being posted and zippyshare is shutting down)

                    //disabled for now (as Bayfiles links are no longer being posted)
                    case "f": // redeem fitgirl links
                        String[] in = load("fitgirl.txt");
                        //Provider bf = new Bayfiles(); //the downloader class
                        print(in.length + " links found!");

                        boolean optionLoop4 = true;
                        while (optionLoop4) { //item selected menu
                            boolean optionLoop5 = true;
                            while (optionLoop5) { //download option menu
                                print("download options:\n[e] back, [b] bat, [a] automatic (may somtimes fail)");
                                switch (br.readLine().toCharArray()[0] + "") { //code crush 1: duplacate code removal
                                    case "a": //download directly
                                        Krakenfiles.init(in);
                                        Krakenfiles.run(3, true, false, "fitgirl");//curl spam, dont be quiet, dir
                                        break;
                                    case "b":
                                        Krakenfiles.init(in);
                                        Krakenfiles.SetDesktop();
                                        Krakenfiles.run(2, true, false, "fitgirl");//generate bat, dont be quiet, dir
                                        break;
                                    case "e": //back to game list
                                        break;
                                    default:
                                        continue;
                                }
                                optionLoop4 = false; //back out of "item selected menu"
                                optionLoop5 = false; //back out of "download option menu"
                                gamePageChanged = true;
                                listPageChanged = false;
                            } //optionLoop3 ~ download option menu
                        }
                        break;
                    case "x": //exit search
                        if (searching) {
                            listPageChanged = true;
                            searching = false;
                            page = 1;
                            searchword = "";
                        }
                        break;
                    default:
                        break;
                }
                choice = numberInput(optionLoop1input);
                listPageChanged = false;
                if (choice == 0) {
                    continue;
                }
                print("[" + choice + "] selected!");
                choice--; //correct for the user typing 1 for list[0]
                //print the NFO automatically
                links = cr.fetch(list[choice]);
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
                    Krakenfiles.init(nfo);
                    Krakenfiles.run(1, false, true, null); //CURL, be quiet, nodir
                    String[] file = loadstring(nfo[0].split(": ")[0]).split("\n");
                    for (String line : file) {
                        print(line);
                    }
                } else {
                    print("no NFO found in that page, data:" + nfo[0]);
                }
                boolean optionLoop2 = true;
                while (optionLoop2) { //item selected menu
                    print("[d] download now, [b] back, [e] exit");//, [n] print NFO");//, [t] save txt (" + list[choice][0] + ".txt)");
                    switch (br.readLine().toCharArray()[0] + "") {
                        case "d": //download now
                            boolean optionLoop3 = true;
                            while (optionLoop3) { //download option menu
                                String[] lnk = cr.fetch(list[choice]);
                                if (lnk == null) {
                                    optionLoop2 = false; //back out of "item selected menu"
                                    gamePageChanged = true;
                                    listPageChanged = false;
                                    break;
                                }
                                boolean error = false;
                                for (String l : lnk) {
                                    if (l.isEmpty()) {
                                        error |= true;
                                    }
                                }
                                if (error) {
                                    print("[WARNING] incomplete link set!!!");
                                }
                                Krakenfiles.init(lnk);
                                print("download options:\n[e] back, [b] bat, [a] automatic (may somtimes fail)");
                                switch (br.readLine().toCharArray()[0] + "") { //code crush 1: duplacate code removal
                                    case "a": //download directly
                                        Krakenfiles.run(3, true, false, list[choice][0]);//curl spam, dont be quiet, dir
                                        break;
                                    case "b":
                                        Krakenfiles.SetDesktop();
                                        Krakenfiles.run(2, true, false, list[choice][0]);//generate bat, dont be quiet, dir
                                        break;
                                    case "e": //back to game list
                                        break;
                                    default:
                                        break;
                                }
                                optionLoop2 = false; //back out of "item selected menu"
                                optionLoop3 = false; //back out of "download option menu"
                                gamePageChanged = true;
                                listPageChanged = false;
                            } //optionLoop3 ~ download option menu
                            break;
                        case "b": //back
                            gamePageChanged = true;
                            listPageChanged = false;
                            optionLoop2 = false; //back out of "item selected" menu
                            choice = 0;
                            break;
                        case "e":
                            optionLoop2 = false; //back out of "item selected" menu
                            optionLoop1 = false; //back out of "main" menu
                            break;
                        default:
                    }
                } //optionLoop2 ~ item selected menu
            } catch (Exception ex) { //pajeet error handling
                print("ERROR:" + ex.getMessage());

            }
        } //optionLoop1 ~ main menu
    }

    public static int numberInput(String in) {
        try {
            return Integer.parseInt(in);
        } catch (Exception e) { //ignore the error if some cactus types "nan"
            if (in.toLowerCase().contains("nan")) {
                print("cactus");
            }
            return 0;
        }
    }
}
