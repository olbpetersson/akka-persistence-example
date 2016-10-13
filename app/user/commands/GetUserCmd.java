package user.commands;

/**
 * Created by Ola on 2016-06-09.
 */
public class GetUserCmd {
    String userName;

    public GetUserCmd(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
