package illness.events;

import illness.model.Illness;

import java.io.Serializable;

/**
 * Created by ola on 12/10/16.
 */
public class IllnessReportedEvt implements Serializable{
    Illness illness;

    public IllnessReportedEvt(Illness illness) {
        this.illness = illness;
    }

    public Illness getIllness() {
        return illness;
    }

    public void setIllness(Illness illness) {
        this.illness = illness;
    }
}
