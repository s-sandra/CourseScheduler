/**
 * This exception handles incorrect formatting of csv files.
 * @author Sandra Shtabnaya
 */
public class IllegalFileException extends Exception {
    String msg;

    public IllegalFileException(String msg){
        this.msg = "FILE FORMATTING ERROR: "    + msg;
    }

    public String getMessage(){
        return msg;
    }
}
