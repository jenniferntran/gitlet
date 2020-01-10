package gitlet;

import java.io.Serializable;
import java.util.HashMap;

/** A storage of all commits as a hash map that stores commits based
 *  on its SHA-1 ID. Is the object stored in the COMMITSFILE.
 *  @author Jennifer Tran
 */

public class Store implements Serializable {

    /** Initializes a Store object. Takes in a COMMIT.*/
    public Store() {
    }

    /** Adds a Commit to the commit HashMap that adds the Commit's
     *  SHA-1 ID as the key and the Commit COMMIT as the value. */
    public void hashCommit(String id, Commit commit) {
        _commitMap.put(id, commit);
    }

    /** Returns the commit HashMap to access all commits through its
     *  SHA-1 id. */
    public HashMap<String, Commit> returnCommitMap() {
        return _commitMap;
    }

    /** Adds a branch to the branch HashMap that adds the BRANCHNAME String
     *  as the key and the SHA-1 ID String of the Commit as the value. */
    public void hashBranch(String branchName, String id) {
        _branches.put(branchName, id);
    }

    /** Removes the String BRANCHNAME from the _branches HashMap. */
    public void removeBranch(String branchName) {
        _branches.remove(branchName);
    }


    /** Sets the current branch pointer to the String BRANCHNAME. */
    public void setCurBranch(String branchName) {
        _curBranch = branchName;
    }

    /** Returns the current branch String. */
    public String returnCurBranch() {
        return _curBranch;
    }

    /** Returns the branches HashMap. */
    public HashMap<String, String> returnBranchesMap() {
        return _branches;
    }

    /** Returns the head Commit. */
    public Commit returnHeadCommit() {
        String hCommitId = returnBranchesMap().get(returnCurBranch());
        Commit headCommit = returnCommitMap().get(hCommitId);
        return headCommit;
    }

    /** The current branch as a branch name String. */
    private String _curBranch;

    /** A HashMap of commits that takes the Commit id string as the key
     *  and the Commit as the value. */
    private HashMap<String, Commit> _commitMap = new HashMap<String, Commit>();

    /** A HashMap of branches that takes the branch's branch name string
     *  as the key and the branch Commit's SHA-1 id as the value. */
    private HashMap<String, String> _branches = new HashMap<String, String>();

}
