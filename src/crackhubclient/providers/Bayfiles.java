package crackhubclient.providers;

import static crackhubclient.Util.*;

public class Bayfiles extends Provider {

    static {
        INFOHEADER = "[bayfiles downloader]";
    }

    @Override
    public String HandleLinkExternal(String url) throws Exception {
        print(String.format("%s %s fetching page from: %s", INFOHEADER, PROGRESS, url));
        curl(url, "bayfiles.html");
        String[] file = load("bayfiles.html");
        for (int i = 1; i < file.length; i++) {
            if (file[i].contains("class=\"btn btn-primary btn-block\"")) {
                url = file[i + 1].split("href=\"")[1].split("\">")[0];
                break;
            }
        }
        return url;
    }
}
