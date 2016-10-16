package illness.model;

import java.io.Serializable;

/**
 * Created by ola on 16/10/16.
 */
public class Illness implements Serializable{

    private String symptome;
    private boolean healed;

    public Illness() {
    }

    public Illness(String symptome) {
        this.symptome = symptome;
        this.healed = false;
    }

    public Illness(String symptome, boolean healed) {
        this.symptome = symptome;
        this.healed = healed;
    }

    public String getSymptome() {
        return symptome;
    }

    public void setSymptome(String symptome) {
        this.symptome = symptome;
    }

    public boolean isHealed() {
        return healed;
    }

    public void setHealed(boolean healed) {
        this.healed = healed;
    }
}
