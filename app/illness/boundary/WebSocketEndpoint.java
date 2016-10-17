package illness.boundary;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.persistence.query.PersistenceQuery;
import akka.persistence.query.journal.leveldb.javadsl.LeveldbReadJournal;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import com.fasterxml.jackson.databind.JsonNode;
import illness.actors.ReadIllnessActor;
import illness.actors.WebSocketActor;
import play.Logger;
import play.mvc.WebSocket;
import play.mvc.LegacyWebSocket;
import illness.actors.WriteIllnessActor;

import javax.inject.Inject;

/**
 * Created by ola on 13/10/16.
 */
public class WebSocketEndpoint {

    private ActorSystem actorSystem;
    private ActorRef writeActor;
    private ActorRef readActor;

    @Inject
    public WebSocketEndpoint(ActorSystem actorSystem) {
        this.actorSystem = actorSystem;
        this.writeActor = actorSystem.actorOf(Props.create(WriteIllnessActor.class));

        LeveldbReadJournal journal =
                PersistenceQuery.get(actorSystem).getReadJournalFor(LeveldbReadJournal.class,
                        LeveldbReadJournal.Identifier());
        Materializer materializer = ActorMaterializer.create(actorSystem);
        this.readActor = actorSystem.actorOf(ReadIllnessActor.props(journal, materializer));
    }

    public LegacyWebSocket<JsonNode> socket() {
        Logger.info("Opening the websocket");
        return WebSocket.withActor(out -> WebSocketActor.props(out, writeActor, readActor));
    }
}
