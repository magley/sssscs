select 1;

--password: Bob12345
insert into users (email, password, role, name, surname, phone_number, verified, blocked, lastnpasswords) values('bob@gmail.com', '$2a$10$88c6kZsVCcZ5RKp5TdTnk.FP0rhOuaM8qADUZbn9EHJYPxCi0Qnwe', 'ADMIN', 'Bob', 'Jones', '1234567', true, false, '');

--password: A1234567
--insert into users (email, password, role, name, surname, phone_number, verified, blocked) values('regular@gmail.com', '$2a$10$dAB.s9Vgh0Xc5D3PZjz.t.GFkB.aCf/l1NtAAuPH5ov943nkK4COK', 'REGULAR', 'Reg', 'Ular', '1234567', true, false);

insert into certificate (owner_id, valid_from, valid_to, subject_data_common_name, subject_data_email, subject_data_name, subject_data_surname, subject_data_organization, type, status) values
    (1, '2023-06-16 18:59:58.964571', '2024-06-16 18:59:58.95704', 'sssscsLocalhost', 'bob@gmail.com', 'Bob', 'Bobic', 'sssscs' , 'ROOT', 'GOOD');
