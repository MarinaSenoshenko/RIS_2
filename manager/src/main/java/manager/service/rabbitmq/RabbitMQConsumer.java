package manager.service.rabbitmq;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import manager.api.dto.CrackHashService;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import ru.nsu.ccfit.schema.crack_hash_response.CrackHashWorkerResponse;
import ru.nsu.ccfit.schema.percent_of_completion_response.PercentResponse;

import java.io.IOException;

@Service
@Slf4j
@EnableRabbit
public class RabbitMQConsumer {
    @Autowired
    private CrackHashService crackHashService;

    @RabbitListener(queues = "${crackHashService.manager.queue.input}")
    public void receiveMessage(CrackHashWorkerResponse message,
                               @Header(AmqpHeaders.CHANNEL) Channel channel,
                               @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        log.info("Received message: {}", message);
        crackHashService.workerCallbackHandler(message);
        channel.basicAck(tag, false);
    }

    @RabbitListener(queues = "${crackHashService.manager.queue.input_percent}")
    public void receiveMessage(PercentResponse percentResponse,
                               @Header(AmqpHeaders.CHANNEL) Channel channel,
                               @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        log.info("Received message: {}", percentResponse);
        crackHashService.workerCallbackHandler(percentResponse);
        channel.basicAck(tag, false);
    }
}
