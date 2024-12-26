package crackhubclient.site;

import static crackhubclient.Util.*;

public abstract class Site {

    //save HTML in memory instead of constantly writing files
    String[] GAME_HTML = new String[0];
    String[] SITE_HTML = new String[0];
    String[] SCENE_HTML = new String[0];
    String[] NFO_TEXT = new String[0];

    public final void debugSavePage() throws Exception {
        save("game.html", GAME_HTML);
        save("fitgirl.html", SITE_HTML);
        save("scene.html", SCENE_HTML);
        save("game.nfo", NFO_TEXT);
    }

    public boolean isfg = false;

    public static String[][] list; // format: [entry number] [name, type, link]

    public int totalPages = 0;

    public int getTotalPages() {
        return totalPages;
    }

    //abstract methods
    public abstract String[][] display(int page, boolean listPageHasChanged, boolean searching) throws Exception;

    public abstract String[] fetch(String[] choice) throws Exception;

    public abstract void printNFO(String[] choice) throws Exception;
}
