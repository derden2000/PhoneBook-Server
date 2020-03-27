package pro.antonshu.phonebook.entities;


import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

/**
 * Сущность контакта телефоннов книги достаточно легковесна, поэтому Dto и Мапперы я применять не стал.
 * Для каджого контакта предусмотрен список телефонных номеров, который реализован в виде переходной таблицы.
 * Используется БД PostgreSQL, а также framework FlyWay для удоства изменения и наполнения БД.
 *
 * @see db/migration/V1__initialize.sql
 */

@Entity
@Table(name = "persons")
@NoArgsConstructor
@Data
//@JsonIgnoreProperties(value = {"handler", "hibernateLazyInitializer"})
//@ToString
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "surname")
    private String surname;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "person_phones",
            joinColumns = @JoinColumn(name = "person_id"),
            inverseJoinColumns = @JoinColumn(name = "phone_id"))
    private List<Phone> phoneList;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return Objects.equals(name, person.name) &&
                Objects.equals(surname, person.surname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, surname);
    }

}
