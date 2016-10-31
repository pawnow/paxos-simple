package pl.edu.agh.iosr.cdm;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by domin4815 on 31.10.16.
 */
@Repository
public interface ProposalRepository extends CrudRepository<Proposal, Long> {
}
