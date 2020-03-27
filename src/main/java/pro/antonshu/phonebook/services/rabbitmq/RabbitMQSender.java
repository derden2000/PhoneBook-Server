package pro.antonshu.phonebook.services.rabbitmq;

import com.google.gson.Gson;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.antonshu.phonebook.configs.RabbitConfig;
import pro.antonshu.phonebook.utils.Packet;

import java.beans.XMLEncoder;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Класс реализации отправки сообщений в зависимости от типа сообщений.
 *
 * @see org.springframework.amqp.core.Message
 * @see org.springframework.amqp.core.MessageProperties
 */

@Service
public class RabbitMQSender {

    private RabbitTemplate rabbitTemplate;

    @Autowired
    public void setRabbitTemplate(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendPacket(Packet packet) {
        Gson gson = new Gson();
        String json = gson.toJson(packet);
        System.out.println("json: " + json);
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentType(MessageProperties.CONTENT_TYPE_JSON);
        Message message = new Message(json.getBytes(), messageProperties);
        rabbitTemplate.send(RabbitConfig.SERVER_EXCHANGER, "client", message);
    }

    public void sendText(String text) {
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentType(MessageProperties.CONTENT_TYPE_TEXT_PLAIN);
        Message message = new Message(text.getBytes(), messageProperties);
        rabbitTemplate.send(RabbitConfig.SERVER_EXCHANGER, "client", message);
        System.out.println("Text sended: " + text);
    }

    public void sendXml(Object object) {
        XMLEncoder xmlEncoder = null;
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd_MM_YYYY_HH_mm_ss");
        String fileName = String.format("files/%s.xml", dateFormat.format(date));
        System.out.println(dateFormat.format(date));
        try {
            xmlEncoder = new XMLEncoder(new BufferedOutputStream(
                    new FileOutputStream(fileName)));
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        xmlEncoder.writeObject(object);
        xmlEncoder.close();
        try {
            byte[] array = Files.readAllBytes(Paths.get(fileName));
            MessageProperties messageProperties = new MessageProperties();
            messageProperties.setContentType(MessageProperties.CONTENT_TYPE_XML);
            Message message = new Message(array, messageProperties);
            rabbitTemplate.send(RabbitConfig.SERVER_EXCHANGER, "client", message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
