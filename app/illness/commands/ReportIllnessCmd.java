package illness.commands;

import illness.model.Illness;

import java.io.Serializable;

/**
 * Created by Ola on 2016-06-09.
 */
public class ReportIllnessCmd implements Serializable {
    Illness illness;

    public ReportIllnessCmd() {
    }

    public ReportIllnessCmd(Illness illness) {
        this.illness = illness;
    }

    public Illness getIllness() {
        return illness;
    }

    public void setIllness(Illness illness) {
        this.illness = illness;
    }

}
