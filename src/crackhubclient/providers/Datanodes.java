package crackhubclient.providers;

import static crackhubclient.Util.*;

public class Datanodes extends Provider {

    static {
        INFOHEADER = "[datanodes downloader]";
    }

    @Override
    public String HandleLinkExternal(String url) throws Exception {
        print(String.format("%s %s executing python module with arguments: %s", INFOHEADER, PROGRESS, url));
        url = advShellexec("python \"python modules\\resolver\\datanodes\\run.py\" " + url).trim();
        print(String.format("%s %s python module returned: %s", INFOHEADER, PROGRESS, url));
        return url;
    }
}
