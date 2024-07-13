package crackhubclient.providers;

import static crackhubclient.Util.curl;
import static crackhubclient.Util.load;
import static crackhubclient.Util.print;
import java.io.IOException;

public class Provider {

    public static String INFOHEADER;

    public static String[] URLS;
    //public static String URL;

    //used for batch script generation
    public static String[] BATINFO;
    public static String[] BATURLS;
    public static int bUPos = 0;
    public static boolean desktop = false;

//    //CONFIG:
    public static boolean generateBat = false;
    public static boolean useSystemDownlads = true;
//
//    //STRUCT: configuration
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
    public static void init(String URLS_in_file) throws IOException {
        URLS = load(URLS_in_file.replace("\"", ""));
    }

    public static void init(String[] in) {
        URLS = in;
    }

    public static void SetDesktop() {
        desktop = true;
    }

    public static void run(
            int dlt, //DL type, 1 = direct, 2 = batch, 3 = curl spam
            boolean quiet, //dont print anything to the console
            boolean nodir, //use program root to download files to
            String outname //name of output file
    ) throws Exception {
    }

    public static void curlb(String URL, String fileName) throws InterruptedException, IOException{
        URL = URL.replace("\r", "");
        //print("..\\..\\__NBP_tools_dir__\\curl\\curl -L -o .\\" + fileName + " " + URL);
        String cmd = "cmd.exe /c \"start ..\\..\\__NBP_tools_dir__\\curl\\curl -L -o .\\" + fileName + " " + URL + "\"";
        print(INFOHEADER + " executing command: " + cmd);
        Process p = Runtime.getRuntime().exec(cmd);

        p.waitFor();
    }

    public static void download(String URL, String fileName) throws Exception {
        switch (dLType) {
            case 1:
                curl(URL, fileName);
                break;
            case 2:
                if (desktop) {
                    BATURLS[bUPos] = "..\\Documents\\__NBP_tools_dir__\\curl\\curl -k -L -o \".\\" + fileName + "\" \"" + URL + "\"";
                } else {
                    BATURLS[bUPos] = "..\\..\\__NBP_tools_dir__\\curl\\curl -k -L -o \".\\" + fileName + "\" \"" + URL + "\"";
                }
                bUPos++;
                break;
            case 3:
                curlb(URL, fileName);
                break;
            //default:
            //print(curld(URL, fileName));
        }
    }
}
