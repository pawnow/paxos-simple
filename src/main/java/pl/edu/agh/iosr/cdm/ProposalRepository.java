package pl.edu.agh.iosr.cdm;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Created by domin4815 on 31.10.16.
 */
@Repository
public interface ProposalRepository extends CrudRepository<Proposal, Long> {

    @Query("select p from Proposal p where p.id = " +
            " (select max(pp.id) from Proposal pp) ")
    Optional<Proposal> getByMaxId();

    @Modifying
    @Query("update Proposal p set p.value=?3 where p.key = ?1 and p.id=?2")
    void updateValueForProposalWithKeyAndId(String key, Long id, Integer value);

    @Query("select p from Proposal p where p.key = ?1")
    List<Proposal> getProposalsForKey(String key);

    @Query("select p from Proposal p where p.id = " +
            " (select max(pp.id) from Proposal pp) and p.key = ?1")
    Proposal getByMaxIdForKey(String key);
}
