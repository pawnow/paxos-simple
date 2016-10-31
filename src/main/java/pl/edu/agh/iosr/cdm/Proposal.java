package pl.edu.agh.iosr.cdm;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Builder
@Entity

public class Proposal {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    private Integer value;

    public Proposal() {
    }

    public Proposal(Long id, Integer value) {
        this.id = id;
        this.value = value;
    }
}
