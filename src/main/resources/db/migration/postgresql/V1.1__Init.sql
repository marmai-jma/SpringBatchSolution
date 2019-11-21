CREATE TABLE book  (
    id SERIAL NOT NULL PRIMARY KEY,
    title VARCHAR(90),
    author VARCHAR(40),
    isbn VARCHAR(13),
    publisher VARCHAR(20),
    year NUMERIC
);