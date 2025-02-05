package crackhubclient.providers;

import static crackhubclient.Util.*;

public class Krakenfiles extends Provider {

    static {
        INFOHEADER = "[krakenfiles downloader]";
    }

    @Override
    public String HandleLinkExternal(String url) throws Exception {
        print(String.format("%s %s executing python module with arguments: %s", INFOHEADER, PROGRESS, url));
        url = advShellexec("python \"python modules\\resolver\\krakenfiles\\run.py\" " + url).trim();
        print(String.format("%s %s python module returned: %s", INFOHEADER, PROGRESS, url));
        return url;
    }

}
