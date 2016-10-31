package pl.edu.agh.iosr.cdm;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Proposal {
    private long id;
    private int value;

    public Proposal() {
    }

    public Proposal(long id, int value) {
        this.id = id;
        this.value = value;
    }
}
