package crackhubclient.providers;

import static crackhubclient.Util.*;

public class Bayfiles extends Provider {
    //pretty sure this site died a long time ago???

    static {
        INFOHEADER = "[bayfiles downloader]";
    }

    @Override
    public String HandleLinkExternal(String url) throws Exception {
        String[] file = webget(url);
        for (int i = 1; i < file.length; i++) {
            if (file[i].contains("class=\"btn btn-primary btn-block\"")) {
                url = file[i + 1].split("href=\"")[1].split("\">")[0];
                break;
            }
        }
        return url;
    }
}
