package manager.domain.model.repository;

import manager.domain.model.entity.RequestStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Date;

@Repository
public interface RequestStatusRepository extends MongoRepository<RequestStatus, String> {
    RequestStatus findByRequestId(String requestId);
    Collection<RequestStatus> findAllByUpdatedBeforeAndStatusEquals(Date timestamp, RequestStatus.Status status);
}
