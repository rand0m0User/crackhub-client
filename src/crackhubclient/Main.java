package crackhubclient;

import static crackhubclient.CUI.G;
import crackhubclient.providers.*;
import crackhubclient.site.*;
import static crackhubclient.Util.*;
import org.fusesource.jansi.AnsiConsole;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Main {

    //VARS: state machine
    private static boolean listPageChanged = true;
    private static boolean gamePageChanged = true;

    private static boolean searching = false;
    private static String searchword = "";
    public static final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    public static Krakenfiles kf = new Krakenfiles();
    //public static Datanodes dn = new Datanodes();
    public static FuckingFast ff = new FuckingFast();

    //public static Provider downloadersite = kf;
    public static Provider downloadersite = ff;
    //public static Site site = new CrackHubSite(); //its dead jim...
    public static Site site = new FitgirlSite();

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
        if (args.length != 0 && args[0].equals("-ship")) {
            AnsiConsole.systemInstall();
        }
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

        PreveiwFitgirlSite.display();
        boolean optionLoop1 = true;
        while (optionLoop1) { //main menu
            try {
                int choice;
                String[][] list = site.display(page, listPageChanged, searching);
                listPageChanged = false; //it just updated ya see?
                //String[] links;
                print(String.format("page: %d/%d", page, site.getTotalPages()));
                if (searching) {
                    print("selection " + G("1 - " + (list.length)));
                    print(G("e") + " exit, "
                            + G("n") + " next, "
                            + G("p") + " previous, "
                            + G("r") + " refresh page, "
                            + G("x") + " exit search\u001B[0m\r");
                } else {
                    print("selection " + G("1 - " + (list.length)));
                    print(G("e") + " exit, "
                            + G("n") + " next, "
                            + G("p") + " previous, "
                            + G("r") + " refresh page "
                            + G("s") + " set page,\u001B[0m\r");
                    print("\u001B[0m"
                            + G("f") + " redeem fitgirl links, "
                            + G("rf") + " refresh fitgirl list\u001B[0m\r");
                }
                //used in selecting an item
                String optionLoop1input = br.readLine();
                switch (optionLoop1input) {
                    case "ds": //debug save page
                        site.debugSavePage();
                        break;
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
                            print(G("b") + " back, " + G("s") + " search");
                            print("go to page:");
                            String optionLoop2input = br.readLine();
                            page = numberInput(optionLoop2input);
                            switch (optionLoop2input) {
                                case "s": //search
                                    searching = true;
                                    boolean optionLoop3 = true;
                                    while (optionLoop3) { //search term menu
                                        print(G("b") + " back");
                                        print("enter search term:");
                                        String optionLoop3input = br.readLine();
                                        if ("b".equals(optionLoop3input)) {
                                            searching = false;
                                            searchword = "";
                                        } else {
                                            searchword = optionLoop3input.replace(" ", "%20");
                                            break;
                                        }
                                        optionLoop3 = false; //back out of "search menu"
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
                        PreveiwFitgirlSite.display();
                        continue;
//                    case "bf": //browse fitgirl
//                        site = new FitgirlSite();
//                        downloadersite = dn;
//                        listPageChanged = true;
//                        continue;
                    case "f": // redeem fitgirl links
                        String[] in = load("fitgirl.txt");
                        //Provider bf = new Bayfiles(); //the downloader class
                        print(((in.length == 1) ? "a link" : in.length + " links") + " found!");

                        boolean optionLoop4 = true;
                        while (optionLoop4) { //item selected menu
                            boolean optionLoop5 = true;
                            while (optionLoop5) { //download option menu
                                print(" download options:");
                                print(""
                                        + G("e") + " back, "
                                        + G("b") + " bat, "
                                        + G("a") + " automatic (may somtimes fail)");
                                switch (br.readLine().toCharArray()[0] + "") { //code crush 1: duplacate code removal
                                    case "a": //download directly
                                        downloadersite.init(in);
                                        downloadersite.run(3, true, false, "fitgirl");//curl spam, dont be quiet, dir
                                        break;
                                    case "b":
                                        downloadersite.init(in);
                                        downloadersite.SetDesktop();
                                        downloadersite.run(2, true, false, "fitgirl");//generate bat, dont be quiet, dir
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
                print(G(choice + "") + " selected!");
                choice--; //correct for the user typing 1 for list[0]
                site.printNFO(list[choice]);
                boolean optionLoop2 = true;
                while (optionLoop2) { //item selected menu
                    print(""
                            + G("d") + " download now, "
                            + G("b") + " back, "
                            + G("e") + " exit");//, [n] print NFO");//, [t] save txt (" + list[choice][0] + ".txt)");
                    switch (br.readLine().toCharArray()[0] + "") {
                        case "d": //download now
                            boolean optionLoop3 = true;
                            while (optionLoop3) { //download option menu
                                String[] lnk = site.fetch(list[choice]);
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
                                downloadersite.init(lnk);
                                print("download options:");
                                print(G("e") + " back, "
                                        + G("b") + " bat, "
                                        + G("a") + " automatic (may somtimes fail)");
                                switch (br.readLine().toCharArray()[0] + "") { //code crush 1: duplacate code removal
                                    case "a": //download directly
                                        downloadersite.run(3, true, false, list[choice][0]);//curl spam, dont be quiet, dir
                                        break;
                                    case "b":
                                        downloadersite.SetDesktop();
                                        downloadersite.run(2, true, false, list[choice][0]);//generate bat, dont be quiet, dir
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
                print("\u001B[34;41;5m[ERROR]\u001B[0;31m:" + ex.getMessage());
                ex.printStackTrace();
            }
        } //optionLoop1 ~ main menu
        if (args.length != 0 && args[0].equals("-ship")) {
            AnsiConsole.systemUninstall();
        }
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
