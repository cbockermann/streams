/**
 * 
 */
package profiler.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author chris
 *
 */
public class TexCompiler {

    static Logger log = LoggerFactory.getLogger(TexCompiler.class);

    public final static String PDFLATEX = "/sw/bin/pdflatex";
    public final static String CONVERT = "/usr/local/bin/convert";

    public final static String[] SEARCH_PATHS = { "/bin", "/usr/bin", "/usr/local/bin", "/opt/local/bin", "/sw/bin" };

    public final static String LATEX_BEGIN = "\\documentclass[a4,10pt]{article}\n" + "\\usepackage{amsmath}\n"
            + "\\begin{document}\n" + "\\pagestyle{empty}\n" + "\\begin{displaymath}\n" + "";

    public final static String LATEX_END = "\n" + "\\end{displaymath}\n" + "\\end{document}";

    String convert = null;
    String pdflatex = null;
    Double resolution = 150.0d;
    Double scale = 100.0d;

    public TexCompiler() {

        if (convert == null) {
            convert = findExecutable(SEARCH_PATHS, "convert");
        }

        if (pdflatex == null) {
            pdflatex = findExecutable(SEARCH_PATHS, "pdflatex");
        }
    }

    public boolean isComplete() {
        return convert != null && pdflatex != null;
    }

    private String findExecutable(String[] paths, String cmd) {
        for (String path : paths) {
            File f = new File(path + File.separator + cmd);
            if (f.isFile()) {
                if (f.canExecute()) {
                    log.debug("Found command '{}' at '{}'", cmd, f.getAbsolutePath());
                    return f.getAbsolutePath();
                } else {
                    log.error("Not allowed to execute {}", f.getAbsolutePath());
                }
            }
        }

        return null;
    }

    public void compile(File texFile, File outputDirectory) throws Exception {
        final String old = System.getProperty("user.dir");
        try {
            long time = System.currentTimeMillis();
            File pwd = new File(texFile.getParentFile().getAbsolutePath());

            File pdfFile = new File(pwd.getAbsolutePath() + File.separator + texFile.getName().replace(".tex", ".pdf"));
            File auxFile = new File(pwd.getAbsolutePath() + File.separator + texFile.getName().replace(".tex", ".aux"));
            File logFile = new File(pwd.getAbsolutePath() + File.separator + texFile.getName().replace(".tex", ".log"));

            System.setProperty("user.dir", texFile.getParentFile().getAbsolutePath());
            long start = System.currentTimeMillis();
            String exec = pdflatex + " -interaction batchmode -output-directory " + pwd.getAbsolutePath() + " "
                    + texFile.getAbsolutePath();
            exec = pdflatex + " " + texFile.getAbsolutePath();
            log.debug("Compiling file: {}", exec);
            Runtime rt = Runtime.getRuntime();
            Process p = rt.exec(exec);

            if (p.waitFor() != 0) {
                log.error("Failed to compile tex-file!");
                dumpErrors(p);
                throw new Exception("Failed to compile tex-file!");
            }
            log.debug("LaTeX compilation took {} ms", System.currentTimeMillis() - start);
            if (log.isTraceEnabled())
                dump(p);

            long end = System.currentTimeMillis();
            log.info("Compiling latex-file'{}' took {} ms", texFile, (end - time));

            //
            // cleanup
            //
            pdfFile.delete();
            auxFile.delete();
            logFile.delete();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.setProperty("user.dir", old);
        }
    }

    private static void dump(Process p) {
        try {
            BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = r.readLine();
            while (line != null) {
                log.debug("Output: {}", line);
                line = r.readLine();
            }
            r.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void dumpErrors(Process p) {
        try {
            BufferedReader r = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            String line = r.readLine();
            while (line != null) {
                log.error("Error: {}", line);
                line = r.readLine();
            }
            r.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
