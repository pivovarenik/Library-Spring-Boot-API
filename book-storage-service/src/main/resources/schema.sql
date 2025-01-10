DROP TABLE IF EXISTS book;
DROP TABLE IF EXISTS users;


CREATE TABLE IF NOT EXISTS book (
                      id int AUTO_INCREMENT PRIMARY KEY, --не уверен но оставляю так
                      isbn VARCHAR(20) NOT NULL UNIQUE,
                      title VARCHAR(255) NOT NULL,
                      genre VARCHAR(100),
                      description TEXT,
                      author VARCHAR(255) NOT NULL
);
CREATE TABLE IF NOT EXISTS users(
                      id int AUTO_INCREMENT PRIMARY KEY,
                      username VARCHAR(70) NOT NULL UNIQUE,
                      password VARCHAR(255) NOT NULL
);