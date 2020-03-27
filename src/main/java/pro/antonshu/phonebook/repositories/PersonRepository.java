package pro.antonshu.phonebook.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pro.antonshu.phonebook.entities.Person;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {

    boolean existsByName(String name);

    boolean existsBySurname(String surname);

    Person findOneByNameAndSurname(String name, String surname);

    @Modifying
    @Query(value = "delete from persons where name=?1 and surname=?2", nativeQuery = true)
    void deleteRawPerson(String name, String surname);
}
