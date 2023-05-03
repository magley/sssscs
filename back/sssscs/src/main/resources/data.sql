select 1;

--password: Bob12345
insert into users (email, password, role, name, surname, phone_number, verified, login_counter, blocked) values('bob@gmail.com', '$2a$10$88c6kZsVCcZ5RKp5TdTnk.FP0rhOuaM8qADUZbn9EHJYPxCi0Qnwe', 'ADMIN', 'Bob', 'Jones', '1234567', true, 0, false);

--password: A1234567
insert into users (email, password, role, name, surname, phone_number, verified, login_counter, blocked) values('regular@gmail.com', '$2a$10$dAB.s9Vgh0Xc5D3PZjz.t.GFkB.aCf/l1NtAAuPH5ov943nkK4COK', 'REGULAR', 'Reg', 'Ular', '1234567', true, 0, false);

insert into certificate (status, subject_data_common_name, type, valid_from, valid_to, owner_id, parent_id) values('GOOD', 'root_admin', 'ROOT', '2021-01-01', '2023-12-06', 1, null);
insert into certificate (status, subject_data_common_name, type, valid_from, valid_to, owner_id, parent_id) values('GOOD', 'end_regular', 'END', '2021-01-01', '2023-12-06', 2, 1);
insert into certificate (status, subject_data_common_name, type, valid_from, valid_to, owner_id, parent_id) values('GOOD', 'end_regular2', 'END', '2021-01-01', '2023-12-06', 2, 1);
insert into certificate (status, subject_data_common_name, type, valid_from, valid_to, owner_id, parent_id) values('GOOD', 'end_admin', 'END', '2021-01-01', '2023-12-06', 2, 2);