package pro.antonshu.phonebook.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.antonshu.phonebook.entities.Person;
import pro.antonshu.phonebook.repositories.PersonRepository;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class PersonService {

    private PersonRepository personRepository;

    @Autowired
    public void setPersonRepository(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public Person findOneById(Long id) {
        return personRepository.findById(id).get();
    }

    public void deleteById(Long id) {
        personRepository.deleteById(id);
    }

    public Person save(Person person) {
        if (personRepository.existsByName(person.getName()) && personRepository.existsBySurname(person.getSurname())) {
            return personRepository.findOneByNameAndSurname(person.getName(), person.getSurname());
        } else {
            return personRepository.save(person);
        }
    }

    public Person findOneByNameAndSurname(Person person) {
        return personRepository.findOneByNameAndSurname(person.getName(), person.getSurname());
    }

    public boolean existsById(Long id) {
        return personRepository.existsById(id);
    }

    public boolean exists(Person person) {
        return personRepository.existsByName(person.getName()) && personRepository.existsBySurname(person.getSurname());
    }

    public List<Person> getAllPersons() {
        return personRepository.findAll();
    }

    public void delete(Person person) {
        personRepository.delete(person);
    }

    @Transactional
    public void deleteRawPerson(Person person) {
        personRepository.deleteRawPerson(person.getName(), person.getSurname());
    }
}
