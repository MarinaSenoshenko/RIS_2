package manager.service;

import lombok.extern.slf4j.Slf4j;
import manager.api.dto.CrackHashService;
import manager.api.dto.requests.RequestStatusDto;
import manager.domain.model.entity.RequestStatus;
import manager.domain.model.repository.RequestStatusRepository;
import manager.domain.model.repository.RequestsRepository;
import manager.domain.service.DomainCrackHashService;
import manager.service.rabbitmq.RabbitMQProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.nsu.ccfit.schema.crack_hash_request.CrackHashManagerRequest;
import ru.nsu.ccfit.schema.crack_hash_response.CrackHashWorkerResponse;

import java.util.Date;
import java.util.UUID;
import java.util.stream.IntStream;

import static manager.domain.model.entity.RequestStatus.Status.*;


@Service
@Slf4j
@Transactional(isolation = Isolation.SERIALIZABLE)
@EnableScheduling
public class CrackHashServiceImpl implements CrackHashService {
    @Autowired
    private RabbitMQProducer rabbitMQProducer;
    @Autowired
    private RequestStatusRepository requestStatusRepository;
    @Autowired
    private RequestsRepository requestsRepository;
    @Value("${crackHashService.manager.countWorkers}")
    private Integer countOfWorker;
    @Value("${crackHashService.expireTimeMinutes}")
    private Long expireTimeMinutes;

    private void sendTaskToWorker(CrackHashManagerRequest crackHashManagerRequest) {
        if (!rabbitMQProducer.trySendMessage(crackHashManagerRequest)) {
            log.info("no connection, save {} to database", crackHashManagerRequest);
            requestsRepository.insert(DomainCrackHashService.getRequest(crackHashManagerRequest));
        }
    }

    @Override
    public String crackHash(String hash, int maxLength) {
        String requestId = UUID.randomUUID().toString().replace("-", "");

        requestStatusRepository.insert(DomainCrackHashService.getRequestStatus(requestId));

        IntStream.range(0, countOfWorker).forEach(i ->
                sendTaskToWorker(DomainCrackHashService.buildCrackHashManagerRequest(
                        requestId, hash, maxLength, i, countOfWorker
                )
        ));
        return requestId;
    }

    @Override
    public RequestStatusDto getStatus(String requestId) {
        return DomainCrackHashService.buildRequestStatusDto(requestStatusRepository.findByRequestId(requestId));
    }

    @Override
    public void workerCallbackHandler(CrackHashWorkerResponse crackHashWorkerResponse) {
        RequestStatus requestStatus = requestStatusRepository.findByRequestId(crackHashWorkerResponse.getRequestId());

        if (requestStatus.getStatus() == IN_PROGRESS) {
            if (!crackHashWorkerResponse.getAnswers().getWords().isEmpty()) {
                requestStatus.getResult().addAll(crackHashWorkerResponse.getAnswers().getWords());
                requestStatusRepository.save(DomainCrackHashService.changeStatus(READY, requestStatus));

                log.info("Received word: {} from request id: {},from worker in partNumber: {}, time counting: {}",
                        crackHashWorkerResponse.getAnswers().getWords(),
                        crackHashWorkerResponse.getRequestId(),
                        crackHashWorkerResponse.getPartNumber(),
                        DomainCrackHashService.getFormattedTime(System.currentTimeMillis() - requestStatus.getUpdated().getTime())
                );
            }
        }
    }

    @Scheduled(fixedDelay = 10000)
    private void expireRequests() {
        requestStatusRepository.findAllByUpdatedBeforeAndStatusEquals(
                new Date(System.currentTimeMillis() - DomainCrackHashService.MILL_TO_MIN * expireTimeMinutes), IN_PROGRESS)
                .forEach(i -> requestStatusRepository.save(DomainCrackHashService.changeStatus(ERROR, i)));
    }
}
