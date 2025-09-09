package crackhubclient.providers;

import crackhubclient.Util;
import static crackhubclient.Util.*;
import static crackhubclient.providers.Provider.INFOHEADER;
import java.net.HttpURLConnection;
import java.net.URL;

public class FuckingFast extends Provider {

    static {
        INFOHEADER = "[FuckingFast downloader]";
        NAME = SUPPORTED_PROVIDERS[0];
    }

    @Override
    public String HandleLinkExternal(String url) throws Exception {
        String[] file = webget(url);
        for (int i = 1; i < file.length; i++) {
            if (file[i].contains("window.open(\"https://fuckingfast.co/dl/")) {
                url = file[i].split("\"")[1];

                //stelth to atleast try to look like a real web browser
                //fairly certen this incriments the download counter
                URL a = new URL("https://fuckingfast.co" + file[i + 3].split("'")[1]);
                HttpURLConnection b = (HttpURLConnection) a.openConnection();
                b.setRequestMethod("POST");
                b.setRequestProperty("User-Agent", Util.CLEAN_USER_AGENT);
                b.setDoOutput(true);
                b.connect();
                int r = b.getResponseCode();
                if (r != 204) {
                    print("emulation rejected: " + r);
                }

                ///f/qm5b22yvh5sh/dl
                break;
            }
        }
        return url;
    }

}
