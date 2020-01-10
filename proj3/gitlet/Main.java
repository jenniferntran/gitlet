package gitlet;

/** Driver class for Gitlet, the tiny stupid version-control system.
 *  @author Jennifer Tran
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND> .... */
    public static void main(String... args) {
        if (args.length == 0) {
            Utils.message("Please enter a command.");
            return;
        }
        if (args[0].equals("init")) {
            operandsCheck(args, 1);
            new Repo();
        } else if (Repo.GIT.exists()) {
            if (args[0].equals("add")) {
                operandsCheck(args, 2);
                Repo.add(args);
            } else if (args[0].equals("commit")) {
                operandsCheck(args, 2);
                Repo.commit(args);
            } else if (args[0].equals("rm")) {
                operandsCheck(args, 2);
                Repo.rm(args);
            } else if (args[0].equals("log")) {
                operandsCheck(args, 1);
                Repo.log();
            } else if (args[0].equals("global-log")) {
                operandsCheck(args, 1);
                Repo.globalLog();
            } else if (args[0].equals("find")) {
                operandsCheck(args, 2);
                Repo.find(args);
            } else if (args[0].equals("status")) {
                operandsCheck(args, 1);
                Repo.status();
            } else if (args[0].equals("checkout")) {
                callCheckout(args);
            } else if (args[0].equals("branch")) {
                operandsCheck(args, 2);
                Repo.branch(args);
            } else if (args[0].equals("rm-branch")) {
                operandsCheck(args, 2);
                Repo.rmbranch(args);
            } else if (args[0].equals("reset")) {
                operandsCheck(args, 2);
                Repo.reset(args);
            } else {
                Utils.message("No command with that name exists.");
                return;
            }
        } else {
            Utils.message("Not in an initialized Gitlet directory.");
            return;
        }
    }

    /** Checks if there are the correct number of operands when given
     *  a String[] ARGS and an int I. */
    public static void operandsCheck(String[] args, int i) {
        if (args.length != i) {
            Utils.message("Incorrect operands.");
            System.exit(0);
        }
    }

    /** Calls checkout after checking if there are the correct number
     *  of operands in the String[] ARGS. */
    public static void callCheckout(String... args) {
        if (args.length == 2) {
            Repo.checkout(args);
        } else if (args.length == 3) {
            Repo.checkout(args);
        } else if (args.length == 4) {
            Repo.checkout(args);
        } else {
            Utils.message("Incorrect operands.");
            System.exit(0);
        }
    }

}
