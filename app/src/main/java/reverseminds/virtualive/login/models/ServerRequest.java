package reverseminds.virtualive.login.models;

/**
 * Created by Kushal I on 28/10/2017.
 */

public class ServerRequest {

    private String operation;
    private User user;

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
