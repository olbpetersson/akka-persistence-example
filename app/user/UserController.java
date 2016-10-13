package user;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;
import user.actors.UserActor;
import user.commands.CreateUserCmd;
import user.commands.GetUserCmd;
import user.commands.UserFailureCmd;
import user.commands.UserSnapshotCmd;
import user.commands.GetUsersCmd;
import user.model.User;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

import static akka.pattern.PatternsCS.ask;

/**
 * Created by Ola on 2016-06-09.
 */
public class UserController extends Controller {

    ActorSystem actorSystem;
    final ActorRef userActorRef;

    @Inject
    public UserController(ActorSystem actorSystem) {
        this.actorSystem = actorSystem;
        userActorRef = actorSystem.actorOf(Props.create(UserActor.class));
        Logger.info("UserControllerSystem: "+String.valueOf(this.actorSystem.hashCode()));
    }

    public CompletionStage<Result> getUsers(){
        CompletionStage<Object> resultFuture = ask(userActorRef, new GetUsersCmd(), 1000);
        return resultFuture.thenApply(response -> ok(response.toString()));
    }

    public CompletionStage<Result> snapshotUser() {
        CompletionStage<Object> resultFuture = ask(userActorRef, new UserSnapshotCmd(), 1000).exceptionally(throwable -> throwable.getMessage());
        return resultFuture
                .thenApply( response -> ok(response.toString()));
    }

    public CompletionStage<Result> failUser() {

        CompletionStage<Result> resultFuture = ask(userActorRef, new UserFailureCmd(), 1000).thenApply(response -> ok(response.toString())).exceptionally(throwable -> internalServerError(throwable.getMessage()));
        return resultFuture;
    }

    public CompletionStage<Result> getUser(String name) {

        CompletionStage<Object> resultFuture = ask(userActorRef, new GetUserCmd(name), 1000).exceptionally(throwable -> "This was not good : "+throwable.getMessage());
        return resultFuture
                .thenApply(response -> ok(response.toString()));
    }

    public CompletionStage<Result> createUser(String name) {
        CompletionStage<Object> resultFuture = ask(userActorRef, new CreateUserCmd(new User(name)), 10000).exceptionally(throwable -> throwable.getMessage());
        return resultFuture
                .thenApply( response -> ok(response.toString()));

    }

}
