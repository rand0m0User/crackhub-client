package crackhubclient.providers;

import static crackhubclient.Util.*;
import java.nio.file.Files;

public class Bayfiles extends Provider {

    static {
        INFOHEADER = "[bayfiles downloader]";
    }

//    @Override
//    public void test(String link) {
//        try {
//            URLS = new String[]{link};
//            run(3, true, true, "testFile.bin");
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }
    //@Override
    public static void run(
            int dlt, //DL type, 1 = direct, 2 = batch, 3 = curl spam
            boolean quiet, //dont print anything to the console
            boolean nodir, //use program root to download files to
            String outname //name of output file
    ) throws Exception {    //veralog formatting
        dLType = dlt;
        if (dLType == 2) {
            generateBat = true;
            BATURLS = new String[URLS.length];
            BATINFO = new String[URLS.length];
        } else {
            generateBat = false; //doubble fix
        }
        String out;
        if (useSystemDownlads) {
            out = "..\\..\\..\\Downloads\\" + outname + "\\";
        } else {
            out = "out\\" + outname + "\\";
        }

        if (!nodir && useSystemDownlads) {
            createCanonicalFile(out).mkdir();
            //TODO: use an actual fucking copy function
            save(out + "arc.exe", loadbytearr(".\\tools\\arc.exe"));
            save(out + "unpack.bat", loadbytearr(".\\tools\\unpack.bat"));
            //never forget to actually save an object under a file name
        }

        int counter = 0;
        //int obftype = 0;
        for (int u = 0; u < URLS.length; u++) {
            String url = URLS[u].replace("_", ".");
            String fname = url.split("/")[url.split("/").length - 1];
            try {
                if (!Files.exists(createCanonicalFile(out + fname).toPath())) {

                    if (quiet) {
                        print(INFOHEADER + " fetching page from: " + url);
                    }
                    curl(url, "bayfiles.html");
                    String[] file = load("bayfiles.html");
                    for (int i = 1; i < file.length; i++) {
                        if (file[i].contains("class=\"btn btn-primary btn-block\"")) {
                            url = file[i + 1].split("href=\"")[1].split("\">")[0];
                            break;
                        }
                    }

                    if (url == null) {
                        print(INFOHEADER + "[ERROR]: URL == null");
                        continue;
                    }
                    if (generateBat) {
                        BATINFO[counter] = String.format("echo \"%s downloading: %s\"", progressBar(URLS.length, counter), url);
                        print(String.format("%s[bat generation] %s", INFOHEADER, BATINFO[counter]));
                    } else {
                        if (quiet) {
                           print(String.format("%s %s downloading: %s", INFOHEADER, progressBar(URLS.length, u), url));

                        }
                    }
                    if (nodir) { //nodir. useed for printing the NFO
                        download(url, fname);
                    } else {
                        if (desktop) {
                            download(url, String.format("..\\Downloads\\%s\\%s", outname, fname));
                        } else {
                            download(url, out + fname);
                        }
                    }
                } else {
                    if (quiet) {
                        print(String.format("%s[INFO] %s allready exists", INFOHEADER, URLS[u].split(seperator)[0]));
                        bUPos++;
                    }
                }
            } catch (Exception e) {
                print(String.format("%s[WARNING] ignoring: \"%s\"", INFOHEADER, fname));
                //counter++;
            }
            counter++;
        }
        if (generateBat) {
            StringBuilder sb = new StringBuilder();
            sb.append("@echo off\ncolor 0a\n");
            for (int i = 0; i < URLS.length; i++) {
                if (BATINFO[i] != null) {
                    sb.append(BATINFO[i]);
                    sb.append("\n");
                    sb.append(BATURLS[i]);
                    sb.append("\n");
                }
            }
            sb.append("pause");
            if (desktop) {
                save("..\\..\\..\\Desktop\\" + outname + "_generated.bat", sb.toString());
            } else {
                save(outname + "_generated.bat", sb.toString());
            }
            BATINFO = null;
            bUPos = 0; // potential fix for a AIOB bug
            BATURLS = null;

            //shellexec("cmd /c run \"..\\..\\..\\Desktop\\" + outname + "_generated.bat\"");
        }
    }
}
