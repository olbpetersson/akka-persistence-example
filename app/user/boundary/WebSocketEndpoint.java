package user.boundary;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.fasterxml.jackson.databind.JsonNode;
import user.actors.WebSocketActor;
import play.Logger;
import play.mvc.WebSocket;
import play.mvc.LegacyWebSocket;
import user.actors.UserActor;

import javax.inject.Inject;

/**
 * Created by ola on 13/10/16.
 */
public class WebSocketEndpoint {

    private ActorSystem actorSystem;
    private ActorRef userActor;

    @Inject
    public WebSocketEndpoint(ActorSystem actorSystem) {
        this.actorSystem = actorSystem;
        this.userActor = actorSystem.actorOf(Props.create(UserActor.class));
    }

    public LegacyWebSocket<JsonNode> socket() {
        Logger.info("Opening the websocket");
        return WebSocket.withActor(out -> WebSocketActor.props(out, userActor));
    }
}
