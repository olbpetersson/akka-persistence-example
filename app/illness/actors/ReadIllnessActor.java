package illness.actors;

import akka.NotUsed;
import akka.actor.AbstractActor;
import akka.japi.pf.ReceiveBuilder;
import akka.persistence.query.journal.leveldb.javadsl.LeveldbReadJournal;
import akka.actor.Props;
import akka.stream.Materializer;
import akka.stream.javadsl.Source;
import illness.commands.ReadIllnessesCmd;
import play.Logger;

/**
 * Created by ola on 16/10/16.
 */
public class ReadIllnessActor extends AbstractActor{

    private LeveldbReadJournal journal;
    private Materializer materializer;

    public ReadIllnessActor(LeveldbReadJournal journal, Materializer materializer){
        this.journal = journal;
        this.materializer = materializer;

        receive(ReceiveBuilder.match(
                ReadIllnessesCmd.class, cmd -> {
                    Logger.debug("Matched illnessCmd");
                    sender().tell(getAllPersistenceIds(), self());
                }
        ).matchAny(o ->
                {
                    Logger.debug("Got unrecognized message " + (o.toString()));
                    unhandled(o);
                }
        ).build());
    }

    public String getAllPersistenceIds(){
        Logger.info("returning persistence ids");
        Source<String, NotUsed> source = journal.allPersistenceIds();
        source
            .map(e -> e.toUpperCase())
                .runForeach(System.out::println, materializer)
                    .handle((done, failure) -> NotUsed.getInstance()
        );
        return "hej";
    }

    public static Props props(LeveldbReadJournal journal, Materializer materializer) {
        return Props.create(ReadIllnessActor.class, journal, materializer);
    }

}
