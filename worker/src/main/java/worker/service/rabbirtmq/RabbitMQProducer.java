package worker.service.rabbirtmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.nsu.ccfit.schema.crack_hash_response.CrackHashWorkerResponse;
import ru.nsu.ccfit.schema.percent_of_completion_response.RequestPercentDto;

@Service
@Slf4j
public class RabbitMQProducer {
    @Value("${crackHashService.worker.queue.output}")
    private String outputQueue;
    @Value("${crackHashService.worker.queue.output_percent}")
    private String outputQueuePercent;
    private final AmqpTemplate amqpTemplate;

    public RabbitMQProducer(AmqpTemplate amqpTemplate) {
        this.amqpTemplate = amqpTemplate;
    }

    public void produce(CrackHashWorkerResponse response) {
        try {
            amqpTemplate.convertAndSend(outputQueue, response);
            log.info("Set {} part of {} task request was sent", response.getPartNumber(), response.getRequestId());
        } catch (AmqpException ex) {
            log.error("Failed to send request '{}' ", response.getRequestId());
        }
    }

    public void produce(RequestPercentDto requestPercentDto) {
        try {
            amqpTemplate.convertAndSend(outputQueuePercent, requestPercentDto);
        } catch (AmqpException ignored) {}
    }
}
