package crackhubclient.site;

import crackhubclient.Main;
import crackhubclient.Util;
import static crackhubclient.Util.print;
import static crackhubclient.Util.push;
import static crackhubclient.Util.webget;
import crackhubclient.providers.Provider;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Pattern;

public class filecryptSite {
    //private static fc_linkholder[] allLinks = new fc_linkholder[0]; // format: [index] [provider name] [link(s)...]
    //private static String magik = "___1357_S3PER4T0R_2468___";
    public String[] fetchallupdates(String[] links) throws Exception {
        String[] downloadlist = new String[0];
        for (String sl : links) {
            //this likly will still work with the regular webget method :/ (it does, left the old FC_webget just in case)
            String[] PAGE_HTML = webget(sl.split(Provider.seperator)[0]);
            //print("processing: " + sl);
            for (int i = 0; i < PAGE_HTML.length; i++) { //parse the download links from the HTML page
                //(...)e="*.dlc Download" onclick="DownloadDLC(this.getAttribute('data-RaNdOmVaL'));" data-RaNdOmVaL="DEADBEEF">
                //                                 ^trigger parse on this^^^^^^^^ ^IDG^^^^^^^^^^      ^use IDG^^^^^^  ^ID^^^^^
                if (PAGE_HTML[i].contains("DownloadDLC(this.getAttribute(")) {
                    //get the id's refrence from the onclick method
                    String idg = PAGE_HTML[i].split(Pattern.quote("onclick=\"DownloadDLC(this.getAttribute('"))[1].split(Pattern.quote("'));\""))[0];
                    //use IDG to get the real file ID
                    String id = PAGE_HTML[i].split(idg + "=\"")[1].split("\"")[0];
                    //download the *.dlc container now that we have encountered the id in the parsed HTML
                    String dlc = String.join("\r\n", webget("https://filecrypt.co/DLC/" + id + ".html"));
                    //decrypt the link container with the special web request to dcrypt.it (???)
                    String res = decryptDLC(dlc);
                    for (String p : res.split("\"")) { //sort out the compatable link(s)
                        if (p.startsWith("http")) {
                            //TODO: more robust file name reading stuff
                            if (p.contains(Main.downloadersite.NAME)) {
                                downloadlist = push(downloadlist, p.split("#")[1] + Provider.seperator + p);
                                //print(p);
                            }
                        }
                    }
                    break;
                }
            }
        }
        //print new parsed rawlinks
        print(String.join("\r\n", downloadlist));
        return downloadlist;

    }

//    public static String[] fc_webget(String url) throws Exception {
//        try {
//            URL a = new URL(url);
//            HttpURLConnection b = (HttpURLConnection) a.openConnection();
//            b.setRequestMethod("GET");
//            b.setRequestProperty("authority", "filecrypt.co");
//            b.setRequestProperty("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
//            b.setRequestProperty("accept-language", "en-US,en;q=0.9");
//            b.setRequestProperty("cache-control", "max-age=0");
//            b.setRequestProperty("content-type", "application/x-www-form-urlencoded");
//            b.setRequestProperty("dnt", "1");
//            b.setRequestProperty("origin", "https://filecrypt.co");
//            b.setRequestProperty("referer", url);
//            b.setRequestProperty("sec-ch-ua", "\"Chromium\";v=\"140\", \"Not=A?Brand\";v=\"24\", \"Microsoft Edge\";v=\"140\"");
//            b.setRequestProperty("sec-ch-ua-mobile", "?0");
//            b.setRequestProperty("sec-ch-ua-platform", "Windows");
//            b.setRequestProperty("sec-fetch-dest", "document");
//            b.setRequestProperty("sec-fetch-mode", "navigate");
//            b.setRequestProperty("sec-fetch-site", "same-origin");
//            b.setRequestProperty("sec-fetch-user", "?1");
//            b.setRequestProperty("upgrade-insecure-requests", "1");
//            b.setRequestProperty("User-Agent", Util.CLEAN_USER_AGENT);
//
//            try {
//                b.connect();
//            } catch (Exception var7) {
//                var7.printStackTrace();
//            }
//
//            List<String> ll;
//            try (BufferedReader reader = new BufferedReader(
//                    new InputStreamReader(b.getInputStream(), StandardCharsets.UTF_8)
//            )) {
//                ll = new ArrayList<>();
//                String l;
//                while ((l = reader.readLine()) != null) {
//                    ll.add(l);
//                }
//            }
//            return ll.toArray(new String[0]);
//        } catch (Exception var7) {
//            var7.printStackTrace();
//            return new String[0];
//        }
//    }
    
    //networking yay!!!
    public static String decryptDLC(String dlc) throws Exception {
        try {
            URL a = new URL("http://dcrypt.it/decrypt/paste");
            HttpURLConnection b = (HttpURLConnection) a.openConnection();
            b.setRequestMethod("POST");
            b.setRequestProperty("User-Agent", Util.CLEAN_USER_AGENT);
            b.setRequestProperty("Accept", "application/json, text/javascript, */*");
            b.setRequestProperty("X-Requested-With", "XMLHttpRequest");
            b.setRequestProperty("Origin", "http://dcrypt.it");
            b.setRequestProperty("Connection", "keep-alive");
            b.setRequestProperty("Referer", "http://dcrypt.it/");
            b.setDoOutput(true);
            // Request body data
            dlc = dlc.replace("=", "%3D").replace("/", "%2F").replace("+", "%2B");
            String requestData = "content=" + dlc;
            b.connect();
            // Write data to the output stream
            try (OutputStream os = b.getOutputStream()) {
                byte[] input = requestData.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
            BufferedReader e = new BufferedReader(new InputStreamReader(b.getInputStream()));
            return e.readLine();
        } catch (Exception var7) {
            var7.printStackTrace();
            return "err";
        }
    }

//    //do not use with large data objects
//    public static /* utility */ fc_linkholder[] fc_pushlinkholder(fc_linkholder[] in, fc_linkholder value) {
//        fc_linkholder[] a = new fc_linkholder[in.length + 1];
//        System.arraycopy(in, 0, a, 0, in.length);
//        a[in.length] = value;
//        return a;
//    }
//
//    //because string[] wasnt enough
//    public static class fc_linkholder {
//
//        public String provider;
//        public String[] links;
//        public boolean unfinished = false;
//
//        fc_linkholder(String provider, String[] links, boolean unfinished) {
//            this.provider = provider;
//            this.links = links;
//            this.unfinished = unfinished;
//        }
//    }
}

//                if (GAME_HTML[i].contains("<td class=\"status\"><i class=\"online small\">")) {
//                    String linebreak = GAME_HTML[i].split("<td ")[0].trim();
//                    String[] linklines = GAME_HTML[i].trim().split(linebreak);
//                    for (String linkline : linklines) {
//                        if (!linkline.startsWith("<td class=\"status\">")) {
//                            continue;
//                        }
//                        //<td class="status"><i class="online small"></i></td><td title="archive.rar">archive.rar<span>
//                        //selecting only:                                                ^^^^^^^^^^^
//                        //print(linkline);
//                        String name = linkline.split("<td title=\"")[1].split("\">")[0];
//                        String domain = linkline.split("class=\"external_link\">")[1].split("</a></span>")[0];
//                        names = push(names, domain + magik + name);
//                    }
//                }
//SEPSEPSEP
//                                for (String n : names) {
//                                    //if (n.startsWith(p.split("//")[1].split("/")[0])) {
//                                    print(p + ", " + n.split(magik)[0]);
//                                    if (p.contains(n.split(magik)[0])) {
//                                        
//                                        downloadlist = push(downloadlist, n.split(magik)[1] + Provider.seperator + p);
//                                        break;
//                                    }
//                                }
