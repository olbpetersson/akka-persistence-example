package illness;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;
import illness.actors.WriteIllnessActor;
import illness.commands.*;
import illness.commands.ReportIllnessCmd;
import illness.model.Illness;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

import static akka.pattern.PatternsCS.ask;

/**
 * Created by Ola on 2016-06-09.
 */
public class IllnessRESTController extends Controller {

    ActorSystem actorSystem;
    final ActorRef illnessActor;

    @Inject
    public IllnessRESTController(ActorSystem actorSystem) {
        this.actorSystem = actorSystem;
        illnessActor = actorSystem.actorOf(Props.create(WriteIllnessActor.class));
        Logger.info("IllnessRestControllerSystem: "+String.valueOf(this.actorSystem.hashCode()));
    }


    public CompletionStage<Result> snapshot() {
        CompletionStage<Object> resultFuture = ask(illnessActor, new IllnessSnapshotCmd(), 1000).exceptionally(throwable -> throwable.getMessage());
        return resultFuture
                .thenApply( response -> ok(response.toString()));
    }

    public CompletionStage<Result> failureCmd() {

        CompletionStage<Result> resultFuture = ask(illnessActor, new FailureCmd(), 1000).thenApply(response -> ok(response.toString())).exceptionally(throwable -> internalServerError(throwable.getMessage()));
        return resultFuture;
    }


    public CompletionStage<Result> reportIllness(String symptome) {
        CompletionStage<Object> resultFuture = ask(illnessActor, new ReportIllnessCmd(new Illness(symptome)), 10000).exceptionally(throwable -> throwable.getMessage());
        return resultFuture
                .thenApply( response -> ok(response.toString()));

    }

}
