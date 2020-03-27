package pro.antonshu.phonebook.services.rabbitmq;

import com.google.gson.Gson;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.antonshu.phonebook.entities.Person;
import pro.antonshu.phonebook.services.PersonService;
import pro.antonshu.phonebook.services.PhoneService;
import pro.antonshu.phonebook.utils.Packet;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Для обмена сообщениями используется класс Message пакета org.springframework.amqp.core
 * Этот класс позволяет идентифицировать тип данных внутри сообщений. Сервер принимает только
 * JSON сообщения с данными в виде экземпляра класса Packet.
 * При удаление записи телефонной книги происходит удаление контакта, а также его телефонов.
 * При вставке/изменении контакта старые данные полностью удаляются и контакт переписывается заново.
 *
 * @see Packet
 * @see org.springframework.amqp.core.Message
 * @see org.springframework.amqp.core.MessageProperties
 */

@Service
public class RabbitMQListener implements MessageListener {

    private PersonService personService;

    private PhoneService phoneService;

    private RabbitMQSender mqSender;

    @Autowired
    public void setMqSender(RabbitMQSender mqSender) {
        this.mqSender = mqSender;
    }

    @Autowired
    public void setPersonService(PersonService personService) {
        this.personService = personService;
    }

    @Autowired
    public void setPhoneService(PhoneService phoneService) {
        this.phoneService = phoneService;
    }

    @Override
    public void onMessage(Message message) {
        if (message.getMessageProperties().getContentType().equals(MessageProperties.CONTENT_TYPE_JSON)) {
            Gson gson = new Gson();
            String json = new String(message.getBody());
            Packet received = gson.fromJson(json, Packet.class);
            switch (received.getCommandType()) {
                case "INSERT":
                    Person person = received.getPersons().get(0);
                    System.out.println("Person received: " + person);
                    if (personService.exists(person)) {
                        phoneService.clearPersonPhoneList(personService.findOneByNameAndSurname(person).getPhoneList());
                        personService.deleteRawPerson(person);
                        person.setPhoneList(phoneService.saveList(person.getPhoneList()));
                        personService.save(person);
                    } else {
                        person.setPhoneList(phoneService.saveList(person.getPhoneList()));
                        personService.save(person);
                    }
                    mqSender.sendText("Person was saved");
                    break;
                case "DELETE":
                    long personId = Long.parseLong(received.getCommandParams().get("ID"));
                    System.out.println("Delete personId: " + personId);
                    if (personService.existsById(personId)) {
                        phoneService.clearPersonPhoneList(personService.findOneById(personId).getPhoneList());
                        personService.deleteById(personId);
                        mqSender.sendText(String.format("Person with id = %s was deleted", personId));
                    } else {
                        mqSender.sendText(String.format("Person with id = %s does not exist", personId));
                    }
                    break;
                case "GET-ONE":
                    long id = Long.parseLong(received.getCommandParams().get("ID"));
                    if (personService.existsById(id)) {
                        Person toSend = personService.findOneById(id);
                        mqSender.sendPacket(new Packet("ANSWER", null, new ArrayList<>(Collections.singleton(toSend))));
                    } else {
                        mqSender.sendText(String.format("Person with id = %s does not exist", id));
                    }
                    break;
                case "GET-ALL":
                    mqSender.sendPacket(new Packet("ANSWER", null, personService.getAllPersons()));
                    break;
                case "GET-XML":
                    mqSender.sendXml(personService.getAllPersons());
                    break;
                default:
                    mqSender.sendText("Unknown type of command");
            }
        }
    }
}
