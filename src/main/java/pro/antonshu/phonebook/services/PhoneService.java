package pro.antonshu.phonebook.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.antonshu.phonebook.entities.Phone;
import pro.antonshu.phonebook.repositories.PhoneRepository;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class PhoneService {

    private PhoneRepository phoneRepository;

    @Autowired
    public void setPhoneRepository(PhoneRepository phoneRepository) {
        this.phoneRepository = phoneRepository;
    }

    public Phone save(Phone phone) {
        return phoneRepository.save(phone);
    }

    public List<Phone> saveList(List<Phone> phoneList) {
        return phoneRepository.saveAll(phoneList);
    }

    @Transactional
    public void clearPersonPhoneList(List<Phone> phones) {
        phones.forEach(phone -> deletePhone(phone));
    }

    @Transactional
    public void deletePhone(Phone phone) {
        phoneRepository.deleteFromLinkedTable(phone.getId());
        phoneRepository.deleteById(phone.getId());
    }
}
