package user.commands;

import user.model.User;

import java.io.Serializable;

/**
 * Created by Ola on 2016-06-09.
 */
public class CreateUserCmd implements Serializable {
    User user;

    public CreateUserCmd() {
    }

    public CreateUserCmd(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
