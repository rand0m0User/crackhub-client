package crackhubclient;

import static crackhubclient.Util.*;
import java.io.IOException;

public class DDGhandler {

    private DDGhandler() {
        //> コンストラクタの記述が無いとSonarQubeがCode Smellと判定してしまうので明示的に何もしないコンストラクタを実装
        //<japmutt.png
    }

    private static final String DDOSGUARD = "<!DOCTYPE html><html><head><title>DDoS-Guard</title>";

    public static boolean checkddosguard(String[] file, String site, int retries) throws IOException, Exception {
        //String[] file = load(fname);
        int count = 0;
        while (file[0].equalsIgnoreCase(DDOSGUARD)) { //(try) and deal with DDoSguard getting in the way
            if (count == retries) {
                print("canceling...");
                return true;
            }
            print("ddosguard tripped... waiting 7 seconds");
            count++;
            try {
                java.lang.Thread.sleep(7000);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            //curl(site, fname);
            //file = load(fname);
            file = webget(site);
        }
        return false;
    }
}
