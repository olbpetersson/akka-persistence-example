package illness.actors;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import com.fasterxml.jackson.databind.node.ObjectNode;
import illness.commands.IllnessSnapshotCmd;
import illness.commands.ReadIllnessesCmd;
import play.Logger;
import play.libs.Json;
import illness.commands.ReportIllnessCmd;
import illness.commands.FailureCmd;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

import static akka.pattern.PatternsCS.ask;
/**
 * Created by ola on 13/10/16.
 */
public class WebSocketActor extends UntypedActor {


    private final ActorRef out;
    private final ActorRef writeActor;
    private final ActorRef readActor;

    public WebSocketActor(ActorRef outRef, ActorRef writeActor, ActorRef readActor) {
        this.out = outRef;
        this.writeActor = writeActor;
        this.readActor = readActor;
    }
    public void onReceive(Object message) {
        Logger.info("Received a message");
        CompletionStage<Object> resultFuture = routeMessage(message);
        try {
            out.tell(Json.toJson(resultFuture.toCompletableFuture().get()), self());
        } catch (InterruptedException|ExecutionException e) {
            out.tell(Json.toJson("Something went bad :("), self());
            e.printStackTrace();
        }
    }

    private CompletionStage<Object> routeMessage(Object message) {
        ActorRef targetActor = writeActor;
        Logger.info("Routing the message");
        ObjectNode jsonObject = (ObjectNode) message;
        Object targetMessage;
        if (jsonObject.get("type") != null) {
            switch (jsonObject.get("type").asText()){
                case "ReportIllnessCmd":
                    ReportIllnessCmd reportIllnessCmd = Json.fromJson(jsonObject.get("cmd"), ReportIllnessCmd.class);
                    Logger.info(reportIllnessCmd.toString());
                    targetMessage = reportIllnessCmd;
                    break;
                case "SnapshotCmd":
                    IllnessSnapshotCmd illnessSnapshotCmd = new IllnessSnapshotCmd();
                    Logger.info(illnessSnapshotCmd.toString());
                    targetMessage = illnessSnapshotCmd;
                    break;
                case "ReadIllnessesCmd":
                    Logger.info("received ReadIllnessesCmd");
                    targetActor = readActor;
                    targetMessage = new ReadIllnessesCmd();
                    break;
                default:
                    targetMessage = new FailureCmd();
            }
        } else{
            targetMessage = new FailureCmd();
        }

        return ask(targetActor, targetMessage, 1000).exceptionally(throwable -> "This was not good: " + throwable.getMessage());
    }

    public void postStop() throws Exception {
        Logger.info("The websocket was closed :(");
    }

    public static Props props(ActorRef out, ActorRef writeActor, ActorRef readActor) {
        return Props.create(WebSocketActor.class, out, writeActor, readActor);
    }
}
