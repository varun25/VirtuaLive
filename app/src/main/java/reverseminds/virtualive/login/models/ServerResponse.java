package reverseminds.virtualive.login.models;

/**
 * Created by Kushal I on 28/10/2017.
 */

public class ServerResponse {

    private String result;
    private String message;
    private User user;

    public String getResult() {
        return result;
    }

    public String getMessage() {
        return message;
    }

    public User getUser() {
        return user;
    }

}
