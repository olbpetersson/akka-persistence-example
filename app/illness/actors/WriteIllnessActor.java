package illness.actors;

import akka.japi.pf.ReceiveBuilder;
import akka.persistence.AbstractPersistentActor;
import akka.persistence.SaveSnapshotSuccess;
import akka.persistence.SnapshotOffer;
import com.google.common.collect.Maps;
import illness.commands.*;
import illness.model.Illness;
import illness.model.SimpleMessage;
import play.Logger;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;
import illness.commands.ReportIllnessCmd;
import illness.events.IllnessReportedEvt;

import java.util.HashMap;

/**
 * Created by Ola on 2016-06-09.
 */
public class WriteIllnessActor extends AbstractPersistentActor{

    public HashMap<String, Illness> illnessMap = Maps.newHashMap();

    @Override
    public PartialFunction<Object, BoxedUnit> receiveRecover() {
        Logger.debug("in the receiveRecover!");
        return ReceiveBuilder
            .match(
                IllnessReportedEvt.class, evt -> {
                    Logger.debug("Replaying reportedIllnessEvt " + evt.getIllness().getSymptome());
                    illnessMap.put(evt.getIllness().getSymptome(), evt.getIllness());
                }
            ).match(
                SnapshotOffer.class, cmd -> {
                    Logger.debug("Restoring a snapshot!");
                    illnessMap = (HashMap<String, Illness>) cmd.snapshot();
                }
            ).build();
    }

    @Override
    public PartialFunction<Object, BoxedUnit> receiveCommand() {
        Logger.info("ReceiveCommand");
        return ReceiveBuilder
            .match(
                ReportIllnessCmd.class, cmd -> reportIllness(cmd)
            ).match(
                FailureCmd.class, cmd -> { throw new RuntimeException("aouch"); }
            ).match(
                IllnessSnapshotCmd.class, cmd -> saveSnapshot(illnessMap.clone())
            ).match(
                SaveSnapshotSuccess.class, cmd -> Logger.info("We managed to snapshot the state! ")
            ).matchAny( o ->
                {
                    Logger.debug("Got unrecognized message " + (o.toString()));
                    unhandled(o);
                }
            ).build();
    }

    private void reportIllness(ReportIllnessCmd reportIllnessCmd) {
        Illness illness = reportIllnessCmd.getIllness();
        if (illness.getSymptome() != null && !illness.isHealed()) {
            IllnessReportedEvt illnessReportedEvt = new IllnessReportedEvt(illness);
            persist(illnessReportedEvt, procedure -> {
                Logger.debug("Reporting the illness "+illnessReportedEvt.getIllness().getSymptome());
                illnessMap.put(illnessReportedEvt.getIllness().getSymptome(), illnessReportedEvt.getIllness());
                Logger.debug("Total size: " +String.valueOf(illnessMap.size()));
            });
        }
        sender().tell(new SimpleMessage("Reported illness: " + illness.getSymptome()), self());
    }

    @Override
    public String persistenceId() {
        return "user-persistent-id";
    }
}
