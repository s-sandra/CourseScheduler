/**
 * This exception handles incorrect formatting of csv files.
 * @author Sandra Shtabnaya
 */
public class IllegalFileFormatException extends Exception {
    String msg;

    public IllegalFileFormatException(String msg){
        this.msg = "FILE FORMATTING ERROR: "    + msg;
    }

    public String getMessage(){
        return msg;
    }
}
