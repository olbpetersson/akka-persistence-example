package user.events;

import user.model.User;

import java.io.Serializable;

/**
 * Created by ola on 12/10/16.
 */
public class UserCreatedEvent implements Serializable{
    User user;

    public UserCreatedEvent(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
