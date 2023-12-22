create table IF NOT EXISTS users (
                               id BIGINT PRIMARY KEY AUTO_INCREMENT not null,
                               login VARCHAR(255) not null ,
                               password VARCHAR(255) not null,
                               auth_token VARCHAR(255) not null
);
create table IF NOT EXISTS file (
                               id BIGINT PRIMARY KEY AUTO_INCREMENT not null,
                               filename VARCHAR(255) not null ,
                               size BIGINT(255) not null
);
INSERT INTO users(auth_token, login, password) VALUES ('5bf82eab207b54ffdf3a980ddf668aeb', 'user', 'qwerty1');
