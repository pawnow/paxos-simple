package pl.edu.agh.iosr.cdm;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Builder
@Entity
@ToString
public class Proposal {

    @Id
    private Long id;
    private Integer value;

    public Proposal() {
    }

    public Proposal(Long id, Integer value) {
        this.id = id;
        this.value = value;
    }
}
