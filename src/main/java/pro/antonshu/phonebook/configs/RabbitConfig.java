package pro.antonshu.phonebook.configs;


import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.antonshu.phonebook.services.rabbitmq.RabbitMQListener;

/**
 * Значения настроек в целях безопасности скрыты. Значения указаны в файле application.properties
 */

@Configuration
public class RabbitConfig {

    public static final String SERVER_EXCHANGER = "serverExchanger";

    private String hostName, serverQueueName, clientQueueName, routingKey;
    private String userName, password;

    @Value("${rabbitmq.hostName}")
    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    @Value("${rabbitmq.serverQueueName}")
    public void setServerQueueName(String serverQueueName) {
        this.serverQueueName = serverQueueName;
    }

    @Value("${rabbitmq.clientQueueName}")
    public void setClientQueueName(String clientQueueName) {
        this.clientQueueName = clientQueueName;
    }

    @Value("${rabbitmq.routingKey}")
    public void setRoutingKey(String routingKey) {
        this.routingKey = routingKey;
    }

    @Value("${rabbitmq.userName}")
    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Value("${rabbitmq.password}")
    public void setPassword(String password) {
        this.password = password;
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory =
                new CachingConnectionFactory(hostName);
        connectionFactory.setUsername(userName);
        connectionFactory.setPassword(password);
        return connectionFactory;
    }


    @Bean
    Queue queueTopic1() {
        return new Queue(serverQueueName, true, false, false);
    }

    @Bean
    Queue queueTopic2() {
        return new Queue(clientQueueName, true, false, false);
    }

    @Bean
    TopicExchange topicExchange() {
        return new TopicExchange(SERVER_EXCHANGER);
    }

    @Bean
    Binding bindingTopic1(@Qualifier("queueTopic1") Queue queue, TopicExchange topicExchange) {
        return BindingBuilder.bind(queue).to(topicExchange).with(routingKey);
    }

    @Bean
    SimpleMessageListenerContainer containerForTopic(ConnectionFactory connectionFactory, MessageListener listener) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(clientQueueName);
        container.setMessageListener(listener);
        return container;
    }

    @Bean
    MessageListener listener() {
        return new RabbitMQListener();
    }
}