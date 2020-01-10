package gitlet;

import java.io.Serializable;
import java.util.HashMap;

/** Stage class that keeps track of the current stage. Is the object
 *  stored in the STAGEFILE.
 *  @author Jennifer Tran
 */

public class Stage implements Serializable {

    /** Initializes the Stage class. */
    public Stage() {
        _add = new HashMap<>();
        _rm = new HashMap<>();
    }

    /** Adds to the _add HashMap with the FILENAME as the key and
     *  CONTENTS as the value. */
    public void add(String fileName, String contents) {
        _add.put(fileName, contents);
    }

    /** Adds to the _rm HashMap with the FILENAME as the key and
     *  CONTENTS as the value. */
    public void rm(String fileName, String contents) {
        _rm.put(fileName, contents);
    }

    /** Checks if the file by the FILENAME is located in the
     *  _add HashMap by returning a boolean. */
    public boolean inAdd(String fileName) {
        return _add.containsKey(fileName);
    }

    /** Removes Stage objects from the _add HashMap that takes in
     *  the FILENAME as the key and CONTENTS as the value. */
    public void removeAdd(String fileName, String contents) {
        _add.remove(fileName, contents);
    }

    /** Removes Stage objects from the _add HashMap that takes in
     *  the FILENAME as the key. */
    public void removeAdd(String fileName) {
        _add.remove(fileName);
    }

    /** Removes Stage objects from the _rm HashMap that takes in
     *  the FILENAME as the key. */
    public void removeRm(String fileName) {
        _rm.remove(fileName);
    }

    /** Returns the _add HashMap to access its contents. */
    public HashMap<String, String> returnAdd() {
        return _add;
    }

    /** Returns the _rm HashMap to access its contents. */
    public HashMap<String, String> returnRm() {
        return _rm;
    }

    /** A HashMap that takes in a string of the file name as keys
     *  and a string of the contents as values for Stage objects
     *  that need to be added. */
    private HashMap<String, String> _add;

    /** A HashMap that takes in a string of the file name as keys
     *  and a string of the contents as values for Stage objects
     *  that need to be removed. */
    private HashMap<String, String> _rm;

}
