package user.actors;

import akka.japi.pf.ReceiveBuilder;
import akka.persistence.AbstractPersistentActor;
import akka.persistence.SnapshotOffer;
import com.google.common.collect.Maps;
import user.model.SimpleMessage;
import play.Logger;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;
import user.commands.CreateUserCmd;
import user.commands.GetUserCmd;
import user.commands.UserFailureCmd;
import user.commands.UserSnapshotCmd;
import user.commands.GetUsersCmd;
import user.events.UserCreatedEvent;
import user.model.User;

import java.util.HashMap;

/**
 * Created by Ola on 2016-06-09.
 */
public class UserActor extends AbstractPersistentActor{

    public HashMap<String, User> usersState = Maps.newHashMap();

    @Override
    public PartialFunction<Object, BoxedUnit> receiveRecover() {
        Logger.debug("in the receiveRecover!");
        return ReceiveBuilder
            .match(
                UserCreatedEvent.class, evt -> {
                    Logger.debug("Replaying userCreatedEvent");
                    usersState.put(evt.getUser().getName(), evt.getUser());
                }
            ).match(
                SnapshotOffer.class, cmd -> usersState = (HashMap<String, User>) cmd.snapshot()
            ).build();
    }

    @Override
    public PartialFunction<Object, BoxedUnit> receiveCommand() {
        Logger.info("ReceiveCommand");
        return ReceiveBuilder
            .match(
                CreateUserCmd.class, cmd -> addUser(cmd)
            ).match(
                GetUserCmd.class, cmd -> usersState.get(cmd.getUserName()).getName()
            ).match(
                UserFailureCmd.class, cmd -> { throw new RuntimeException("aouch"); }
            ).match(
                UserSnapshotCmd.class, cmd -> saveSnapshot(usersState.clone())
            ).match(
                GetUsersCmd.class, cmd -> sender().tell(getUsers(), self())
            ).matchAny( o ->
                {
                    Logger.debug("Got unrecognized message" + (o.toString()));
                    unhandled(o);
                }
            ).build();
    }

    private void addUser(CreateUserCmd createUserCmd) {
        User user = createUserCmd.getUser();
        if (user.getName() != null) {
            UserCreatedEvent userCreatedEvent = new UserCreatedEvent(user);
            persist(userCreatedEvent, procedure -> {
                Logger.debug("Adding the user");
                usersState.put(userCreatedEvent.getUser().getName(), userCreatedEvent.getUser());
                Logger.debug("Total size: " +String.valueOf(usersState.size()));
            });
        }
        sender().tell(new SimpleMessage("Created user " + user.getName()), self());
    }

    private HashMap<String, User> getUsers(){
        return usersState;
    }

    @Override
    public String persistenceId() {
        return "user-persistent-id";
    }
}
