package gitlet;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/** Commit class for Gitlet.
 *  @author Jennifer Tran
 */

public class Commit implements Serializable {

    /** Creates a new Commit. Takes in a Commit MSG, a HashMap of
     *  CONTENT, and a Commit PARENT. */
    public Commit(String msg, HashMap<String, String> content, Commit parent) {
        _msg = msg;
        _content = content;
        _parent = parent;
        Date date = new Date();
        String datePattern = "E MMM dd HH:mm:ss yyyy Z";
        DateFormat dateFormat = new SimpleDateFormat(datePattern);
        _date = dateFormat.format(date);
        _id = createHashId();
    }

    /** Creates an initial Commit. Takes in a Commit MSG. */
    public Commit(String msg) {
        _msg = msg;
        _content = new HashMap<>();
        _parent = null;
        _date = "Thu Jan 1 00:00:00 1970 +0000";
        _id = createHashId();
    }

    /** Returns a unique SHA-1 id String for a commit based on the commit
     *  message, content, parent, and date. */
    public String createHashId() {
        String content;
        String parent;
        if (_content == null) {
            content = "";
        } else {
            content = _content.toString();
        }
        if (_parent == null) {
            parent = "";
        } else {
            parent = _parent.toString();
        }
        String all = _msg + content + parent + _date;
        return Utils.sha1(all);
    }

    /** Returns the Commit's unique SHA-1 id String. */
    public String returnId() {
        return _id;
    }

    /** Returns the Commit's content HashMap. */
    public HashMap<String, String> returnContent() {
        return _content;
    }

    /** Returns the current Commit's parent Commit. */
    public Commit returnParent() {
        return _parent;
    }

    /** Returns the current Commit's date String. */
    public String returnDate() {
        return _date;
    }

    /** Returns the current Commit's commit message String. */
    public String returnMsg() {
        return _msg;
    }

    /** The current Commit's date string. */
    private String _date;

    /** The current Commit's commit message String. */
    private String _msg;

    /** A HashMap of the current commit's content that saves the file name
     *  as the key and the content as the value. */
    private HashMap<String, String> _content;

    /** The current commit's SHA-1 id String. */
    private String _id;

    /** The current Commit's parent commit. */
    private Commit _parent;

}
