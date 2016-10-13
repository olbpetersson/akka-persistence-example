package user.actors;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.Logger;
import play.libs.Json;
import user.commands.GetUsersCmd;
import user.commands.CreateUserCmd;
import user.commands.UserFailureCmd;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

import static akka.pattern.PatternsCS.ask;
/**
 * Created by ola on 13/10/16.
 */
public class WebSocketActor extends UntypedActor {


    private final ActorRef out;
    private final ActorRef userActor;

    public WebSocketActor(ActorRef outRef, ActorRef userActor) {
        this.out = outRef;
        this.userActor = userActor;
    }
    public void onReceive(Object message) {
        Logger.info("Received a message");
        CompletionStage<Object> resultFuture = routeMessage(message);
        try {
            out.tell(Json.toJson(resultFuture.toCompletableFuture().get()), self());
        } catch (InterruptedException|ExecutionException e) {
            out.tell("Something went bad :(", self());
            e.printStackTrace();
        }
    }

    private CompletionStage<Object> routeMessage(Object message) {
        Logger.info("Routing the message");
        ObjectNode jsonObject = (ObjectNode) message;
        Object targetMessage;
        if (jsonObject.get("type") != null) {
            switch (jsonObject.get("type").asText()){
                case "CreateUserCmd":
                    CreateUserCmd createUserCmd = Json.fromJson(jsonObject.get("cmd"), CreateUserCmd.class);
                    Logger.info(createUserCmd.toString());
                    targetMessage = createUserCmd;
                    break;
                default:
                    targetMessage = new GetUsersCmd();
            }
        } else{
            targetMessage = new UserFailureCmd();
        }

        return ask(userActor, targetMessage, 1000).exceptionally(throwable -> "This was not good : " + throwable.getMessage());
    }

    public void postStop() throws Exception {
        Logger.info("The websocket was closed :(");
    }

    public static Props props(ActorRef out, ActorRef userActorRef) {
        return Props.create(WebSocketActor.class, out, userActorRef);
    }
}
