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
public class AcceptedProposal {

    @Id
    private Long id;
    private Integer value;
    private String server;
    private Integer highestAcceptedProposalId;

    public AcceptedProposal() {
    }

    public AcceptedProposal(Long id, Integer value, String server, Integer highestAcceptedProposalId) {
        this.id = id;
        this.value = value;
        this.server = server;
        this.highestAcceptedProposalId = highestAcceptedProposalId;
    }

    public AcceptedProposal(Proposal proposal){
        id = proposal.getId();
        value = proposal.getValue();
        server = proposal.getServer();
        highestAcceptedProposalId = proposal.getHighestAcceptedProposalId();
    }
}