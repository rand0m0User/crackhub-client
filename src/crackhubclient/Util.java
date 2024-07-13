package crackhubclient;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

public class Util {

    private Util() {
        //> コンストラクタの記述が無いとSonarQubeがCode Smellと判定してしまうので明示的に何もしないコンストラクタを実装
        //<japmutt.png
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

    public static String[] load(String fn) throws IOException {
        return (new String(Files.readAllBytes(new File(fn).getCanonicalFile().toPath()), "UTF-8")).split("\n");
    }

    public static String loadstring(String fn) throws IOException {
        return new String(Files.readAllBytes(new File(fn).getCanonicalFile().toPath()), "Cp437"); //fixed the NFO loading bug
    }

    public static byte[] loadbytearr(String fn) throws IOException {
        return Files.readAllBytes(new File(fn).getCanonicalFile().toPath());
    }

    public static void save(String fn, byte[] f) throws IOException {
        Files.write(new File(fn).getCanonicalFile().toPath(), f);
    }

    public static void save(String fn, String f) throws IOException {
        Path path = Paths.get(fn);
        Files.write(path, f.getBytes());
    }

    public static void curl(String url, String fileName) throws IOException {
        url = url.replace("\r", "");
        String cmd = String.format("..\\..\\__NBP_tools_dir__\\curl\\curl -L -o .\\%s %s", fileName, url);
        Process p = Runtime.getRuntime().exec(cmd);
        try {
            p.waitFor();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    //shell execute that gets the return value of the executed process
    public static String advShellexec(String cmd) throws IOException {
        java.util.Scanner s = new java.util.Scanner(Runtime.getRuntime().exec(cmd).getInputStream()).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public static String progressBar(int total, int progress) {
        progress++;
        return String.format("[%d/%d](%f%s)", progress, total, ((float) progress / total) * 100f, "%");
    }

    //do not use with large data objects
    public static /* utility */ String[] push(String[] in, String value) {
        String[] a = new String[in.length + 1];
        System.arraycopy(in, 0, a, 0, in.length);
        a[in.length] = value;
        return a;
    }
}
