package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/** Repo class for Gitlet. Keeps track of all commands and files.
 *  @author Jennifer Tran
 */

public class Repo implements Serializable {

    /******************** INIT ********************/
    /** ARGS: none
     *
     *  Initializes the Gitlet version-control system in the current
     *  directory. */
    public Repo() {
        if (GIT.exists()) {
            if (Utils.plainFilenamesIn(GIT).size() == 0) {
                initialize();
            } else {
                Utils.message("A Gitlet version-control system "
                        + "already exists in the current directory.");
                return;
            }
        } else {
            GIT.mkdirs();
            initialize();
        }
    }

    /** A helper for Repo() init. */
    private void initialize() {
        String msg = "initial commit";
        Commit initial = new Commit(msg);
        Store store = new Store();
        store.hashCommit(initial.returnId(), initial);

        _master = initial;
        store.hashBranch("master", _master.returnId());
        store.setCurBranch("master");

        Utils.writeObject(COMMITSFILE, store);

        _stage = new Stage();
        Utils.writeObject(STAGEFILE, _stage);

        _head = initial;
        Utils.writeObject(HEADFILE, _head);
    }


    /******************** ADD ********************/
    /** ARGS: [file name]
     *
     *  Adds a copy of the file as it currently exists to the staging area.
     *
     *  Saved in the STAGEFILE through the Stage object and _stage instance. */
    public static void add(String... args) {
        String fileName = args[1];
        _stage = Utils.readObject(STAGEFILE, Stage.class);
        Store s = Utils.readObject(COMMITSFILE, Store.class);
        File f = Utils.join(WDIR, fileName);
        if (!f.exists()) {
            Utils.message("File does not exist.");
            System.exit(0);
            return;
        } else {
            String contents = Utils.readContentsAsString(f);
            String c = s.returnHeadCommit().returnContent().get(fileName);
            if (contents.equals(c)) {
                _stage.removeAdd(fileName);
            } else {
                _stage.add(fileName, contents);
            }
            _stage.removeRm(fileName);
            Utils.writeObject(STAGEFILE, _stage);
        }
    }


    /******************** COMMIT ********************/
    /** ARGS: [message]
     *
     *  Saves certain files in the current commit and staging area and
     *  creates a new commit.
     *
     *  Takes Stage objects from the STAGEFILE and Commit objects from
     *  the _head hash map to create contents. Saves commits into the
     *  COMMITSFILE as Store objects. */
    public static void commit(String... args) {
        _stage = Utils.readObject(STAGEFILE, Stage.class);
        if (_stage.returnAdd().isEmpty() && _stage.returnRm().isEmpty()) {
            Utils.message("No changes added to the commit.");
            return;
        }
        if (args[1].equals("")) {
            Utils.message("Please enter a commit message.");
            return;
        }

        HashMap<String, String> addCommits = _stage.returnAdd();
        HashMap<String, String> content = new HashMap<>();
        for (String c: addCommits.keySet()) {
            if (!_stage.returnRm().containsKey(c)) {
                content.put(c, addCommits.get(c));
            }
        }
        Store store = Utils.readObject(COMMITSFILE, Store.class);
        _head = store.returnHeadCommit();
        if (_head != null) {
            for (String s: _head.returnContent().keySet()) {
                if (!content.containsKey(s)
                        && !_stage.returnRm().containsKey(s)) {
                    content.put(s, _head.returnContent().get(s));
                }
            }
        }

        Commit newCommit = new Commit(args[1], content, _head);
        store.hashCommit(newCommit.returnId(), newCommit);
        store.removeBranch(store.returnCurBranch());
        store.hashBranch(store.returnCurBranch(), newCommit.returnId());
        Utils.writeObject(COMMITSFILE, store);

        _stage = new Stage();
        Utils.writeObject(STAGEFILE, _stage);
    }


    /******************** REMOVE ********************/
    /** ARGS: [file name]
     *
     *  Unstages files if they are currently staged.
     *
     *  Removes certain files from the STAGEFILE as Stage objects
     *  and saves them in the Stage class's rm HashMap. */
    public static void rm(String... args) {
        String fileName = args[1];
        _stage = Utils.readObject(STAGEFILE, Stage.class);
        Store s = Utils.readObject(COMMITSFILE, Store.class);
        _head = s.returnHeadCommit();
        File f = Utils.join(WDIR, fileName);
        if (checkUntracked(fileName)) {
            Utils.message("No reason to remove the file.");
            return;
        }
        if (_stage.inAdd(fileName)) {
            _stage.removeAdd(fileName);
        }
        if (_head.returnContent().containsKey(fileName)) {
            _stage.rm(fileName, _head.returnContent().get(fileName));
            Utils.restrictedDelete(f);
        }
        Utils.writeObject(STAGEFILE, _stage);
    }


    /******************** LOG ********************/
    /** ARGS: none
     *
     *  Displays information about each commit backwards along the
     *  commit tree starting from the head commit until the initial
     *  commit.
     *
     *  Takes the branch HashMap saved in the Store class
     *  in the COMMITSFILE. */
    public static void log() {
        Store s = Utils.readObject(COMMITSFILE, Store.class);
        String branchId = s.returnCurBranch();
        HashMap<String, String> branchMap = s.returnBranchesMap();
        Commit h = s.returnCommitMap().get(branchMap.get(branchId));
        while (h != null) {
            System.out.println("===");
            System.out.println("commit " + h.returnId());
            System.out.println("Date: " + h.returnDate());
            System.out.println(h.returnMsg());
            System.out.println();
            h = h.returnParent();
        }
    }


    /******************** GLOBAL LOG ********************/
    /** ARGS: none
     *
     *  Displays all information about commits ever made.
     *
     *  Takes the commit HashMap saved in the Store class in the
     *  COMMITSFILE. */
    public static void globalLog() {
        Store s = Utils.readObject(COMMITSFILE, Store.class);
        HashMap<String, Commit> map = s.returnCommitMap();
        Collection<Commit> allCommits = map.values();
        for (Commit commit: allCommits) {
            System.out.println("===");
            System.out.println("commit " + commit.returnId());
            System.out.println("Date: " + commit.returnDate());
            System.out.println(commit.returnMsg());
            System.out.println();
        }
    }


    /******************** FIND ********************/
    /** ARGS: [commit message]
     *
     *  Prints out ids of all commits that have the given commit message.
     *
     *  Takes Store objects saved in the COMMITSFILE. */
    public static void find(String... args) {
        String commitMsg = args[1];
        Store allCommits = Utils.readObject(COMMITSFILE, Store.class);
        boolean bool = true;
        for (Commit c: allCommits.returnCommitMap().values()) {
            if (c.returnMsg().equals(commitMsg)) {
                System.out.println(c.returnId());
                bool = false;
            }
        }
        if (bool) {
            Utils.message("Found no commit with that message.");
            return;
        }
    }


    /******************** STATUS ********************/
    /** ARGS: none
     *
     *  Displays what branches currently exist and what files have been
     *  staged or marked for untracking.
     *
     *  Takes Store objects from the
     *  COMMITSFILE and Stage objects from the STAGEFILE. */
    public static void status() {
        Store commits = Utils.readObject(COMMITSFILE, Store.class);
        Stage stageFiles = Utils.readObject(STAGEFILE, Stage.class);
        Collection<String> allFiles = Utils.plainFilenamesIn(WDIR);
        System.out.println("=== Branches ===");
        for (String branchName: commits.returnBranchesMap().keySet()) {
            if (branchName.equals(commits.returnCurBranch())) {
                System.out.println("*" + branchName);
            } else {
                System.out.println(branchName);
            }
        }
        System.out.println();

        System.out.println("=== Staged Files ===");
        for (String fileName: stageFiles.returnAdd().keySet()) {
            System.out.println(fileName);
        }
        System.out.println();

        System.out.println("=== Removed Files ===");
        for (String fileName: stageFiles.returnRm().keySet()) {
            System.out.println(fileName);
        }
        System.out.println();

        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println();

        System.out.println("=== Untracked Files ===");
        System.out.println();
    }

    /** Helper function for status(). Checks which files are modified or
     *  deleted and lists them. */
    public static void checkModAndDelete() {
        Store commits = Utils.readObject(COMMITSFILE, Store.class);
        Stage stageFiles = Utils.readObject(STAGEFILE, Stage.class);
        Collection<String> allFiles = Utils.plainFilenamesIn(WDIR);
        for (String f: allFiles) {
            Commit curCommit = commits.returnHeadCommit();
            HashMap<String, String> commitContent = curCommit.returnContent();
            String dirContent = Utils.readContentsAsString(Utils.join(WDIR, f));
            boolean trackedInCommit = curCommit.returnContent().containsKey(f);
            boolean changedInWDIR = !dirContent.equals(commitContent.get(f));
            boolean checkStaged = stageFiles.inAdd(f);
            boolean checkDeleted = false;
            for (String cF: curCommit.returnContent().keySet()) {
                File file = Utils.join(WDIR, cF);
                if (!file.exists()) {
                    checkDeleted = true;
                }
            }
            if (trackedInCommit && !changedInWDIR && !checkStaged) {
                System.out.println(f + " (modified)");
            } else if (!changedInWDIR && checkStaged) {
                System.out.println(f + " (modified)");
            } else if (checkStaged && checkDeleted) {
                System.out.println(f + " (deleted)");
            } else if (!checkStaged && trackedInCommit && checkDeleted) {
                System.out.println(f + " (deleted)");
            }
        }
    }


    /******************** CHECKOUT ********************/
    /** ARGS: --, [file name]
     *  Takes the version of the file in
     *  the head commit and puts it in the working directory.
     *
     *  ARGS: [commit id], --, [file name]
     *  Takes the version of the file in the commit with the given
     *  id and puts it in the working directory.
     *
     *  ARGS: [branch name]
     *  If ARGS contains [branch name]: takes all files in the commit
     *  at the head of the given branch and puts them in the working
     *  directory. Given branch name becomes the current branch,
     *  deletes files tracked in the current branch not present in
     *  the checkout branch, and clears the staging area. */
    public static void checkout(String... args) {
        String fileName;
        String commitId;
        String branchName;
        Store s = Utils.readObject(COMMITSFILE, Store.class);
        _head = s.returnHeadCommit();
        if (args.length == 3) {
            fileName = args[2];
            if (_head.returnContent().containsKey(fileName)) {
                String content = _head.returnContent().get(fileName);
                File f = Utils.join(WDIR, fileName);
                Utils.writeContents(f, content);
            } else {
                Utils.message("File does not exist in that commit.");
                return;
            }
        } else if (args.length == 4) {
            commitId = args[1];
            fileName = args[3];
            if (s.returnCommitMap().containsKey(commitId)) {
                Commit c = s.returnCommitMap().get(commitId);
                if (!c.returnContent().containsKey(fileName)) {
                    Utils.message("File does not exist in that commit.");
                    return;
                }
                File f = Utils.join(WDIR, fileName);
                Utils.writeContents(f, c.returnContent().get(fileName));
            } else {
                Utils.message("No commit with that id exists.");
                return;
            }
        } else if (args.length == 2) {
            checkoutBranch(args);
        }
    }

    /** Helper function for checking out branches. takes in ARGS. */
    public static void checkoutBranch(String... args) {
        String branchName;
        Store s = Utils.readObject(COMMITSFILE, Store.class);
        _head = s.returnHeadCommit();
        branchName = args[1];
        _stage = Utils.readObject(STAGEFILE, Stage.class);
        if (!s.returnBranchesMap().containsKey(branchName)) {
            Utils.message("No such branch exists.");
            return;
        } else if (s.returnCurBranch().equals(branchName)) {
            Utils.message("No need to checkout the current branch.");
            return;
        }
        String headCommitId = s.returnBranchesMap().get(branchName);
        Commit c = s.returnCommitMap().get(headCommitId);
        List<String> wdirFiles = Utils.plainFilenamesIn(WDIR);
        for (String file: c.returnContent().keySet()) {
            if (checkUntracked(file) && c.returnContent().containsKey(file)) {
                Utils.message("There is an untracked file in the way; "
                        + "delete it or add it first.");
                System.exit(0);
                return;
            }
        }
        for (String file: c.returnContent().keySet()) {
            File f = Utils.join(WDIR, file);
            Utils.writeContents(f, c.returnContent().get(file));
        }
        for (String f: wdirFiles) {
            if (!checkUntracked(f) && !c.returnContent().containsKey(f)) {
                File name = Utils.join(WDIR, f);
                Utils.restrictedDelete(name);
            }
        }
        s.setCurBranch(branchName);
        _stage = new Stage();
        Utils.writeObject(COMMITSFILE, s);
        Utils.writeObject(STAGEFILE, _stage);
    }

    /******************** BRANCH ********************/
    /** ARGS: [branch name]
     *
     *  Creates a new branch with the given name and points it at the
     *  current head node. Stored in the branch HashMap as a Store
     *  object in the COMMITSFILE. */
    public static void branch(String... args) {
        String branchName = args[1];
        Store s = Utils.readObject(COMMITSFILE, Store.class);
        if (s.returnBranchesMap().containsKey(branchName)) {
            Utils.message("A branch with that name already exists.");
            return;
        } else {
            s.hashBranch(branchName, s.returnHeadCommit().returnId());
            Utils.writeObject(COMMITSFILE, s);
        }
    }


    /******************** REMOVE BRANCH ********************/
    /** ARGS: [branch name]
     *
     *  Deletes the pointer associated with a branch with the given
     *  name. */
    public static void rmbranch(String... args) {
        String branchName = args[1];
        Store s = Utils.readObject(COMMITSFILE, Store.class);
        if (!s.returnBranchesMap().containsKey(branchName)) {
            Utils.message("A branch with that name does not exist.");
            return;
        } else if (s.returnCurBranch().equals(branchName)) {
            Utils.message("Cannot remove the current branch.");
            return;
        } else {
            s.removeBranch(branchName);
            Utils.writeObject(COMMITSFILE, s);
        }
    }


    /******************** RESET ********************/
    /** ARGS: [commit id]
     *
     *  Checks out all the files tracked by the given commit, removes
     *  tracked files not present in that commit, and moves the current
     *  branch's head to that commit node. */
    public static void reset(String... args) {
        String commitId = args[1];
        Store s = Utils.readObject(COMMITSFILE, Store.class);
        List<String> wdirFiles = Utils.plainFilenamesIn(WDIR);
        Commit given = s.returnCommitMap().get(commitId);
        if (!s.returnCommitMap().containsKey(commitId)) {
            Utils.message("No commit with that id exists.");
            return;
        }
        for (String f: wdirFiles) {
            if (checkUntracked(f) && given.returnContent().containsKey(f)) {
                Utils.message("There is an untracked file in the way; "
                        + "delete it or add it first.");
                System.exit(0);
                return;
            }
        }
        _head = s.returnHeadCommit();
        _stage = Utils.readObject(STAGEFILE, Stage.class);
        for (String file: given.returnContent().keySet()) {
            File f = Utils.join(WDIR, file);
            Utils.writeContents(f, given.returnContent().get(file));
        }
        for (String f: wdirFiles) {
            if (!checkUntracked(f) && !given.returnContent().containsKey(f)) {
                File name = Utils.join(WDIR, f);
                Utils.restrictedDelete(name);
            }
        }
        String branchName = s.returnCurBranch();
        s.removeBranch(branchName);
        s.hashBranch(branchName, given.returnId());
        s.setCurBranch(branchName);
        _stage = new Stage();
        Utils.writeObject(STAGEFILE, _stage);
    }


    /******************** HELPERS ********************/
    /** Returns the boolean true if a file FILENAME is untracked. */
    public static boolean checkUntracked(String fileName) {
        boolean check = false;
        Stage stage = Utils.readObject(STAGEFILE, Stage.class);
        Store s = Utils.readObject(COMMITSFILE, Store.class);
        File f = Utils.join(WDIR, fileName);
        _head = s.returnHeadCommit();
        if (f.exists()) {
            if (!stage.inAdd(fileName)
                    && !_head.returnContent().containsKey(fileName)) {
                check = true;
            }
        }
        return check;
    }


    /******************** INSTANCES ********************/
    /** An instance of the master branch as a commit. Stored in a HashMap
     *  in the STORE class. */
    private static Commit _master;

    /** An instance of the current head pointer as a commit. */
    private static Commit _head;

    /** An instance of the current stage. Utilized in the STAGE class. */
    private static Stage _stage;


    /******************** FILES & DIRECTORIES ********************/
    /** The working directory. */
    static final String WDIR = System.getProperty("user.dir");

    /** The gitlet directory. */
    static final File GIT = Utils.join(WDIR, ".gitlet");

    /** A file that keeps track of all commits saved in the git file.
     *  Utilized with the STORE class. */
    static final File COMMITSFILE = Utils.join(GIT, "commits");

    /** A file that keeps track of the current stage saved in the git
     *  file. Utilized with the STAGE class. */
    static final File STAGEFILE = Utils.join(GIT, "stage");

    /** A file that keeps track of the current head saved in the
     *  head file. */
    static final File HEADFILE = Utils.join(GIT, "head");

}
