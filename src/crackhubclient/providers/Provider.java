package crackhubclient.providers;

import static crackhubclient.CUI.progressBar;
import crackhubclient.Main;
import crackhubclient.Util;
import static crackhubclient.Util.*;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.regex.Pattern;

public abstract class Provider {

    public static final String[] SUPPORTED_PROVIDERS = {"fuckingfast.co", "datanodes.to", "krakenfiles.com", "bayfiles.com"};

    public static String INFOHEADER;
    public static String NAME;
    public static String[] URLS;
    public static String PROGRESS;

    //used for batch script generation
    public static String[] BATINFO;
    public static String[] BATURLS;
    public static int bUPos = 0;
    public static boolean desktop = false;

    //CONFIG:
    public static boolean generateBat = false;
    public static boolean useSystemDownlads = true;

    //STRUCT: configuration
    //note: it expects URLS to be seperated with this string, so no matter where
    //you got the urls, you have to hand them to this file in the format:
    //file.rar: https://downloadhost.null/file.rar
    public static String seperator = ": ";

    //ENUM: downloader type
    //1: CURL
    //2: BAT
    //3: curl spam
    public static int dLType = 1; //default value is CURL

//    public void test(String link) {
//        try {
//            URLS = new String[]{"testFile: " + link};
//            run(3, false, true, "testFile.bin");
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }
    public void init(String URLS_in_file) throws Exception {
        URLS = load(URLS_in_file.replace("\"", ""));
    }

    public void init(String[] in) {
        URLS = in;
    }

    public void SetDesktop() {
        desktop = true;
    }

    abstract String HandleLinkExternal(String url) throws Exception;

    public void run(
            int dlt, //DL type, 1 = direct, 2 = batch, 3 = curl spam
            boolean quiet, //dont print anything to the console
            boolean nodir, //use program root to download files to
            String outname //name of output file
    ) throws Exception {
        dLType = dlt;
        if (dLType == 2) {
            generateBat = true;
            BATURLS = new String[URLS.length];
            BATINFO = new String[URLS.length];
        } else {
            generateBat = false; //doubble fix
        }
        try {
            outname = outname.split(Pattern.quote("+"))[0];
            outname = outname.split(",")[0];
            outname = outname.replaceAll("[\\\\/:*?\"<>|]", " ");
            outname = outname.trim();
        } catch (Exception e) {
            e.printStackTrace();
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
            //(removed because crackhub has been gone a LOOONG time)
            //save(out + "arc.exe", loadbytearr(".\\tools\\arc.exe"));
            //save(out + "unpack.bat", loadbytearr(".\\tools\\unpack.bat"));
            //never forget to actually save an object under a file name
            if (Main.site.isfg) {
                save(out + "fgext.bat", loadbytearr(".\\tools\\fgext.bat"));
            }
        }

        if (URLS.length == 1 && !URLS[0].endsWith(".rar")) {
            String[] p = URLS[0].split(seperator);
            URLS[0] = p[0] + ".rar" + seperator + p[1];

            if (Main.site.isfg && !nodir && useSystemDownlads) {
                save(out + "fgext.bat", loadbytearr(".\\tools\\fgextsingle.bat"));
            }
        }

        int counter = 0;
        for (int u = 0; u < URLS.length; u++) {
            //print(URLS[u]);
            String fname = URLS[u].split(seperator)[0];
            PROGRESS = progressBar(URLS.length, u);
            try {
                boolean badfile = false;
                try {
                    badfile = checkdownload(createCanonicalFile(out + fname).getPath());
                } catch (Exception e) {
                    //ignore if the file does not exist
                }
                if (badfile) {
                    print(String.format("\u001B[31m%s %s \u001B[0;30;41;5m[INFO]\u001B[0;31m local file \"%s\" is bad... redownloading\u001B[0m", INFOHEADER, PROGRESS, fname));
                }

                //if the file DOES NOT exist and the file IS bad
                if (!Files.exists(createCanonicalFile(out + fname).toPath()) ^ badfile) {
                    String url = URLS[u].split(seperator)[1];
                    if (quiet) {
                        print(String.format("%s %s fetching page from: %s", INFOHEADER, PROGRESS, url));
                    }
                    //MAIN LOOP
                    url = this.HandleLinkExternal(url); //core reconfigureable code
                    if (url == null) {
                        print(String.format("\u001B[31m%s %s \u001B[0;30;41;5m[ERROR]\u001B[0;31m URL == null\u001B[0m", INFOHEADER, PROGRESS));
                        continue;
                    }
                    if (!url.toLowerCase().startsWith("http")) {
                        print(String.format("\u001B[31m%s %s \u001B[0;30;41;5m[ERROR]\u001B[0;31m non-link returned: \"%s\"\u001B[0m", INFOHEADER, PROGRESS, url));
                        continue;
                    }
                    if (generateBat) {
                        BATINFO[counter] = String.format("echo \"%s downloading: %s\"", PROGRESS, url);
                        print(String.format("%s[bat generation] %s %s", INFOHEADER, PROGRESS, BATINFO[counter]));
                    } else {
                        if (quiet) {
                            print(String.format("%s %s downloading: %s", INFOHEADER, PROGRESS, url));
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
                        print(String.format("%s %s [INFO] %s allready exists", INFOHEADER, PROGRESS, URLS[u].split(seperator)[0]));
                        bUPos++;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                print(String.format("\u001B[33m%s %s \u001B[0;33;43;5m[WARNING]\u001B[0;33m ignoring: \"%s\"\u001B[0m", INFOHEADER, PROGRESS, fname));
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

    public static void curlb(String URL, String fileName) throws InterruptedException, IOException {
        URL = URL.replace("\r", "");
        //print("..\\..\\__NBP_tools_dir__\\curl\\curl -L -o .\\" + fileName + " " + URL);
        String cmd = "cmd.exe /c start curl -L -A \"" + Util.CLEAN_USER_AGENT + "\" -o \".\\" + fileName + "\" " + URL + "\"";
        print(String.format("%s %s executing command: %s", INFOHEADER, PROGRESS, cmd));
        Process p = Runtime.getRuntime().exec(cmd);
        p.waitFor();
    }

    public static void download(String URL, String fileName) throws Exception {
        URL = URL.replace("–", "-");
        URL = URL.replace(" ", "%20");
        URL = URL.replace("[", "%5B");
        URL = URL.replace("]", "%5D");
        switch (dLType) {
            case 1:
                curl(URL, fileName);
                break;
            case 2:
                //if (desktop) {
                BATURLS[bUPos] = "curl -k -L -A \"" + Util.CLEAN_USER_AGENT + "\" -o \".\\" + fileName + "\" \"" + URL + "\"";
                //} else {
                //    BATURLS[bUPos] = "curl -k -L -o \".\\" + fileName + "\" \"" + URL + "\"";
                //}
                bUPos++;
                break;
            case 3:
                curlb(URL, fileName);
                break;
            //default:
            //print(curld(URL, fileName));
        }
    }

    //file based error checking
    //* if the rar is less than 512MB, signal an error
    //* if the rar is less than 512MB, signal an error if the file doesnt have the "MD5\0x00" string at len - 28
    //* if the rar is larger than than 512MB, signal an error if the file doesnt end with 8 0x00 bytes
    //* if the bin does not have "lzma" at len - 25, signal an error
    public boolean checkdownload(String f) throws Exception {
        File rar = new File(f);
        String fname = rar.getName();
        String ext = "";
        int di = fname.lastIndexOf('.');
        if (di > 0 && di < fname.length() - 1) {
            ext = fname.substring(di + 1);
        }
        long len = rar.length();
        boolean error = false;
        //check the rar split part files
        if (len != 524288000 && ext.equals("rar")) {
            DataInputStream dis = new DataInputStream(new FileInputStream(rar));
            dis.skip(len - 28);
            int md5str = dis.readInt();
            dis.close();
            if (md5str != 1296315648) { //"MD5\0x00"
                if (len > 524288000) {
                    DataInputStream dis2 = new DataInputStream(new FileInputStream(rar));
                    dis2.skip(len - 8);
                    long zeros = dis2.readLong();
                    dis2.close();
                    if (zeros != 0) {
                        error |= true;
                    }
                } else {
                    error |= true;
                }
            }
        }
        //check any of those arc? bin files
        if (ext.equals("bin")) {
            DataInputStream dis = new DataInputStream(new FileInputStream(rar));
            dis.skip(len - 25);
            int lzmastr = dis.readInt();
            dis.close();
            if (lzmastr != 1819962721) { //"lzma"
                error |= true;
            }
        }

        return error;
    }
}
