drop table IF exists persons cascade;
create table persons (
    id bigserial,
    name varchar(255),
    surname varchar(255),
    primary key(id)
);

insert into persons (name, surname) values
    ('Ivan', 'Ivanov'),
    ('Petr', 'Petrov'),
    ('John', 'Doe');

drop table IF exists phones cascade;
create table phones (
    id bigserial,
    number varchar(255),
    unique (number),
    primary key(id)
);

insert into phones (number) values
    ('11111'),
    ('22222'),
    ('33333'),
    ('44444');

DROP TABLE IF EXISTS person_phones;
CREATE TABLE person_phones (
  person_id               INT NOT NULL,
  phone_id               INT NOT NULL,
  PRIMARY KEY (person_id, phone_id),
  FOREIGN KEY (person_id)
  REFERENCES persons (id),
  FOREIGN KEY (phone_id)
  REFERENCES phones (id)
);

INSERT INTO person_phones (person_id, phone_id)
VALUES
(1, 1),
(1, 2),
(2, 3),
(3, 1);