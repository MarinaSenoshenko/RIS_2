package worker.service.rabbirtmq;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import ru.nsu.ccfit.schema.crack_hash_request.CrackHashManagerRequest;
import worker.api.dto.CrackHashService;

@Service
@Slf4j
@EnableRabbit
@AllArgsConstructor
public class RabbitMQConsumer {
    private final CrackHashService crackHashService;
    private final RabbitMQProducer rabbitMQProducer;

    @RabbitListener(queues = "${crackHashService.worker.queue.input}")
    public void receiveMessage(CrackHashManagerRequest message) {
        log.info("Received message: {}", message);
        rabbitMQProducer.produce(crackHashService.crackCode(message));
    }

    @RabbitListener(queues = "${crackHashService.worker.queue.input_percent}")
    public void processWorkerRequest(String requestId) throws InterruptedException {
        log.info("Received request from manager for requestId: {}", requestId);
        rabbitMQProducer.produce(crackHashService.getPercentOfCompletion(requestId));
    }
}
