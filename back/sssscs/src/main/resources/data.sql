select 1;

--password: Bob12345
insert into users (email, password, role, name, surname, phone_number, verified, login_counter) values('bob@gmail.com', '$2a$10$88c6kZsVCcZ5RKp5TdTnk.FP0rhOuaM8qADUZbn9EHJYPxCi0Qnwe', 'ADMIN', 'Bob', 'Jones', '1234567', true, 0);