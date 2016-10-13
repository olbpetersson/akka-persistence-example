package user.model;

import java.io.Serializable;

/**
 * Created by Ola on 2016-06-09.
 */
public class User implements Serializable{
    public String name;

    public User(){}

    public User(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                '}';
    }
}
