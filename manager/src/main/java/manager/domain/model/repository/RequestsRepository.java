package manager.domain.model.repository;

import manager.domain.model.entity.Request;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestsRepository extends MongoRepository<Request, String> {

}
