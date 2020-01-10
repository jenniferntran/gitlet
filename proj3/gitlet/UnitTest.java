package gitlet;

import ucb.junit.textui;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/** The suite of all JUnit tests for the gitlet package.
 *  @author Jonathan Yun, Jennifer Tran
 */
public class UnitTest {

    /** Run the JUnit tests in the loa package. Add xxxTest.class entries to
     *  the arguments of runClasses to run other JUnit tests. */
    public static void main(String[] ignored) {
        System.exit(textui.runClasses(UnitTest.class));
    }

    @Test
    public void testTemplate() throws IOException {
        init();
        writeTestFile("filename.txt", "gitlet");
        checkFileContents("filename.txt", "gitlet");
        Main.main("add", "filename.txt");
        close();
    }

    @Test
    public void initTest() throws IOException {
        System.out.println("<-----------init Test----------->");
        init();
        Main.main("global-log");
        close();
    }

    @Test
    public void commitTest() throws IOException {
        System.out.println("<-----------commit Test----------->");
        init();
        Main.main("commit", "here is a commit");
        Main.main("global-log");
        close();
    }

    @Test
    public void anotherCommitTest() throws IOException {
        System.out.println("<-----------anotherCommit Test----------->");
        init();
        writeTestFile("blah.txt", "fucku");
        Main.main("add", "blah.txt");
        Main.main("commit", "here is a commit");
        Main.main("global-log");
        close();
    }

    @Test
    public void addTest() throws IOException {
        System.out.println("<-----------add Test----------->");
        init();
        writeTestFile("add.txt", "hi");
        Main.main("add", "add.txt");
        Main.main("commit", "check add");
        Main.main("global-log");
        close();
    }

    @Test
    public void wtfTest() throws IOException {
        System.out.println("<-----------wtf Test----------->");
        init();
        writeTestFile("wtf.txt", "ew");
        Main.main("add", "wtf.txt");
        Main.main("commit", "check add");
        Main.main("global-log");
        close();
    }

    @Test
    public void anotherAddTest() throws IOException {
        System.out.println("<-----------another add Test----------->");
        init();
        writeTestFile("hi.txt", "dsfukdsjlf");
        Main.main("add", "hi.txt");
        Main.main("commit", "hi add");
        Main.main("global-log");
        close();
    }

    @Test
    public void findTest() throws IOException {
        System.out.println("<-----------find Test----------->");
        init();
        Main.main("commit", "first commit");
        Main.main("commit", "second commit");
        Main.main("log");
        Main.main("find", "first commit");
        close();
    }

    @Test
    public void rmTest() throws IOException {
        System.out.println("<-----------rm Test----------->");
        init();
        writeTestFile("Test1.txt", "ugh");
        writeTestFile("Test2.txt", "um");
        Main.main("add", "Test1.txt");
        Main.main("add", "Test2.txt");
        Main.main("commit", "fuck this project");
        Main.main("rm", "Test1.txt");
        Main.main("commit", "urgh");
        close();
    }

    @Test
    public void logTest() throws IOException {
        System.out.println("<-----------log Test----------->");
        init();
        Main.main("commit", "1st commit");
        Main.main("commit", "2nd commit");
        Main.main("commit", "3rd commit");
        Main.main("log");
        close();
    }

    @Test
    public void globalLogTest() throws IOException {
        System.out.println("<-----------global-log Test----------->");
        init();
        Main.main("commit", "first commit");
        Main.main("commit", "second commit");
        Main.main("commit", "third commit");
        Main.main("global-log");
        close();
    }

    @Test
    public void checkoutTest() throws IOException {
        System.out.println("<-----------checkout test----------->");
        init();
        writeTestFile("Test1.txt", "test");
        Main.main("add", "Test1.txt");
        Main.main("commit", "first commit");
        Main.main("log");
        checkFileContents("Test1.txt", "test");
        writeTestFile("Test1.txt", "hello");
        Main.main("add", "Test1.txt");
        checkFileContents("Test1.txt", "hello");
        Main.main("checkout", "--", "Test1.txt");
        Main.main("add", "Test1.txt");
        Main.main("commit", "second commit");
        checkFileContents("Test1.txt", "test");
        close();
    }

    @Test
    public void statusTest() throws IOException {
        System.out.println("<-----------status test----------->");
        clearDir(gitletDir);
        Main.main("init");
        writeTestFile("wug.txt", "wug");
        writeTestFile("wug2.txt", "wug wug");
        writeTestFile("wug3.txt", "wug wug wug");
        writeTestFile("file1.txt", "file1 contents");
        writeTestFile("file2.txt", "file2 contents");
        writeTestFile("file3.stuff", "");
        Main.main("add", "wug.txt");
        Main.main("add", "wug2.txt");
        Main.main("add", "wug3.txt");
        Main.main("add", "file1.txt");
        Main.main("add", "file2.txt");
        Main.main("commit", "added wugs and files");
        Main.main("add", "wug.txt");
        Main.main("add", "wug2.txt");
        Main.main("rm", "file1.txt");
        Main.main("log");
        close();
    }

    private void init() {
        clearDir(gitletDir);
        Main.main("init");
    }

    private File writeTestFile(String fileName,
                               String contents) throws IOException {
        File file = Utils.join(cwd, fileName);
        Writer w = new OutputStreamWriter(new FileOutputStream(file));
        w.write(contents);
        w.close();
        filesToDelete.add(file);
        return file;
    }

    private void checkFileContents(String fileName, String contents) {
        File file = Utils.join(cwd, fileName);
        assertEquals(contents, Utils.readContentsAsString(file));
    }

    private void close() {
        clearDir(gitletDir);
        for (File file: filesToDelete) {
            Utils.restrictedDelete(file);
        }
    }

    private void clearDir(File wD) {
        List<String> fileNames = Utils.plainFilenamesIn(wD);
        if (fileNames == null) {
            return;
        }
        for (String fileName: fileNames) {
            File file = Utils.join(wD, fileName);
            if (file.isDirectory()) {
                clearDir(file);
            }
            if (!file.delete()) {
                System.out.println("failed to delete");
            }
        }
    }

    private final File cwd = new File(System.getProperty("user.dir"));

    private final File gitletDir = Utils.join(cwd, ".gitlet");

    private ArrayList<File> filesToDelete = new ArrayList<>();

}
