DROP TABLE IF EXISTS person;
CREATE TABLE person
(
    id bigint NOT NULL AUTO_INCREMENT,
    name varchar(50),
    age int,
    primary key (id)
);