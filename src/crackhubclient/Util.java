package crackhubclient;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringEscapeUtils;

public class Util {

    private Util() {
        //> コンストラクタの記述が無いとSonarQubeがCode Smellと判定してしまうので明示的に何もしないコンストラクタを実装
        //<japmutt.png
    }

    public static String CLEAN_USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.7 (KHTML, like Gecko) Chrome/16.0.912.75 Safari/535.7";

    public static String sanitize(String in) {
        return StringEscapeUtils.unescapeHtml4(in).replace("–", "-");
//        return in.replace("&#8217;", "'")
//                .replace("&#8211;", "-")
//                .replace("&#038;", "&")
//                .replace("&#8220;", "\"")
//                .replace("&#8221", "\"");
    }

    //System.out.println("this sucks to use!!!");
    public static void print(String str) {
        System.out.println(str);
    }

    public static File createCanonicalFile(String path) {
        File f = new File(path);
        try {
            return f.getCanonicalFile();
        } catch (IOException e) {
            return f;
        }
    }

    public static String[] load(String fn) throws Exception {
        return (new String(Files.readAllBytes(new File(fn).getCanonicalFile().toPath()), "UTF-8")).split("\n");
    }

    public static String loadstring(String fn) throws Exception {
        return new String(Files.readAllBytes(new File(fn).getCanonicalFile().toPath()), "Cp437"); //fixed the NFO loading bug
    }

    public static byte[] loadbytearr(String fn) throws Exception {
        return Files.readAllBytes(new File(fn).getCanonicalFile().toPath());
    }

    public static void save(String fn, byte[] f) throws Exception {
        Files.write(new File(fn).getCanonicalFile().toPath(), f);
    }

    public static void save(String fn, String[] f) throws Exception {
        String fs = "";
        for (String s : f) {
            fs += (s + "\n");
        }
        save(fn, fs);
    }

    public static void save(String fn, String f) throws Exception {
        Path path = Paths.get(fn);
        Files.write(path, f.getBytes());
    }

    public static void curl(String url, String fileName) throws Exception {
        url = url.replace("\r", "");
        String cmd = String.format("curl -L -o .\\%s %s", fileName, url);
        Process p = Runtime.getRuntime().exec(cmd);
        try {
            p.waitFor();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    public static String[] webget(String url) throws Exception {
        try {
            URL a = new URL(url);
            HttpURLConnection b = (HttpURLConnection) a.openConnection();
            b.setRequestMethod("GET");
            b.setRequestProperty("User-Agent", CLEAN_USER_AGENT);
            long unixTimestamp = Instant.now().getEpochSecond();
            String c = load(".\\DDG_cookie.txt")[0];
            b.setRequestProperty("Cookie", c + unixTimestamp);
            b.setDoInput(true);
            
            try {
                b.connect();
            } catch (Exception var7) {
                var7.printStackTrace();
            }
            
            List<String> ll;
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(b.getInputStream(), StandardCharsets.UTF_8)
            )) {
                ll = new ArrayList<>();
                String l;
                while ((l = reader.readLine()) != null) {
                    ll.add(l);
                }
            }
            return ll.toArray(new String[0]);
        } catch (Exception var7) {
            var7.printStackTrace();
            return new String[0];
        }
    }

    //shell execute that gets the return value of the executed process
    public static String advShellexec(String cmd) throws Exception {
        java.util.Scanner s = new java.util.Scanner(Runtime.getRuntime().exec(cmd).getInputStream()).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    //do not use with large data objects
    public static /* utility */ String[] push(String[] in, String value) {
        String[] a = new String[in.length + 1];
        System.arraycopy(in, 0, a, 0, in.length);
        a[in.length] = value;
        return a;
    }
}
