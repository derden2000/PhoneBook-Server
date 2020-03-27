package pro.antonshu.phonebook.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pro.antonshu.phonebook.entities.Phone;

@Repository
public interface PhoneRepository extends JpaRepository<Phone, Long> {

    boolean existsByNumber(String number);

    Phone findOneByNumber(String number);

    @Modifying
    @Query(value = "delete from person_phones where phone_id = ?1", nativeQuery = true)
    void deleteFromLinkedTable(Long id);
}
