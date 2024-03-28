package manager.service.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import manager.domain.model.repository.RequestsRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.nsu.ccfit.schema.crack_hash_request.CrackHashManagerRequest;

@Service
@Slf4j
public class RabbitMQProducer implements ConnectionListener {
    @Autowired
    private RequestsRepository requestRepository;
    @Value("${crackHashService.manager.queue.output}")
    private String outputQueue;
    @Value("${crackHashService.manager.queue.output_percent}")
    private String outputQueuePercent;
    private final AmqpTemplate amqpTemplate;

    public RabbitMQProducer(AmqpTemplate amqpTemplate, ConnectionFactory connectionFactory) {
        this.amqpTemplate = amqpTemplate;
        connectionFactory.addConnectionListener(this);
    }

    public boolean trySendMessage(CrackHashManagerRequest request) {
        try {
            amqpTemplate.convertAndSend(outputQueue, request);
            log.info("Set {} part of {} task request was sent", request.getPartNumber(), request.getRequestId());
            return true;
        } catch (AmqpException ex) {
            log.error("Failed to send request '{}', cached message", request.getRequestId());
            return false;
        }
    }

    public void requestWorkerByRequestId(String requestId) {
        try {
            amqpTemplate.convertAndSend(outputQueuePercent, requestId);
            log.info("Request sent to worker for requestId: {}", requestId);
        } catch (AmqpException ex) {
            log.error("Failed to send request to worker for requestId: {}", requestId);
        }
    }

    @Override
    public void onCreate(@NotNull Connection connection) {
        for (var request : requestRepository.findAll()) {
            if (trySendMessage(request.getRequest())) {
                requestRepository.delete(request);
            }
        }
    }
}
