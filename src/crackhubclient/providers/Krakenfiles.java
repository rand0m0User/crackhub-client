package crackhubclient.providers;

import static crackhubclient.Util.*;
import java.nio.file.Files;

public class Krakenfiles extends Provider {

    static {
        INFOHEADER = "[krakenfiles downloader]";
    }

//    @Override
//    public void test(String link) {
//        try {
//            URLS = new String[]{"testFile: " + link};
//            run(1, false, true, "testFile.bin");
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
            String fname = URLS[u].split(seperator)[0];
            try {
                if (!Files.exists(createCanonicalFile(out + fname).toPath())) {
                    String url = URLS[u].split(seperator)[1];
                    if (quiet) {
                        print(INFOHEADER + " fetching page from: " + url);
                    }
                    //MAIN LOOP
                    //if (quiet) {
                    print(INFOHEADER + " executing python module with arguments: " + url);
                    //}
                    url = advShellexec("python \"python modules\\krakenfiles resolver\\command_line.py\" " + url).trim();
                    //if (quiet) {
                    print(INFOHEADER + " python module returned: " + url);
                    //}
                    if (url == null) {
                        print(INFOHEADER + "[ERROR]: URL == null");
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
