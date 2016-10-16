package illness.actors;

import akka.persistence.query.journal.leveldb.javadsl.LeveldbReadJournal;
import akka.actor.ActorRef;
import akka.actor.Props;

/**
 * Created by ola on 16/10/16.
 */
public class ReadIllnessActor {



    public static Props props(ActorRef out, LeveldbReadJournal journal) {
        return Props.create(ReadIllnessActor.class, journal);
    }
}
