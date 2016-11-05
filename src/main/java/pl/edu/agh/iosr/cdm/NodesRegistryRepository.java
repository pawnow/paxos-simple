package pl.edu.agh.iosr.cdm;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by domin on 05.11.16.
 */
@Repository
public interface NodesRegistryRepository extends CrudRepository<Node, Long> {
}
