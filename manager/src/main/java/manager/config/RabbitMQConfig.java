package manager.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2XmlMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    @Autowired
    private CachingConnectionFactory cachingConnectionFactory;
    @Value("${crackHashService.manager.queue.input}")
    private String inputQueue;
    @Value("${crackHashService.manager.queue.output}")
    private String outputQueue;

    @Bean
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(cachingConnectionFactory);
        rabbitTemplate.setMessageConverter(xmlMessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public Queue workersToManagerQueue() {
        return new Queue(inputQueue, true);
    }

    @Bean
    public Queue managerToWorkersQueue() {
        return new Queue(outputQueue, true);
    }

    @Bean
    public Jackson2XmlMessageConverter xmlMessageConverter() {
        return new Jackson2XmlMessageConverter();
    }
}
