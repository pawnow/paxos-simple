package pl.edu.agh.iosr.cdm;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Created by domin4815 on 31.10.16.
 */
@Repository
public interface AcceptedProposalRepository extends CrudRepository<AcceptedProposal, Long> {
    @Query("select p from AcceptedProposal p where p.id = " +
            " (select max(pp.id) from AcceptedProposal pp) ")
    Optional<AcceptedProposal> getByMaxId();
}
