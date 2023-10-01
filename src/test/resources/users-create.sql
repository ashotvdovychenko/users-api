--password 'password'
INSERT INTO users (id, username, email, password, first_name, last_name, birth_date, phone_number, address)
VALUES (1, 'first', 'User1@user.com', '$2a$12$oV6w7EchcbvZ5ZP/9HuDyOmYYS5G2tqV7BdpCRArnBAn4/LIk2qo.',
        'John', 'Doe', '1985-10-01', '(206) 342-8631', 'First user address'),
       (2, 'second', 'User2@user.com', '$2a$12$oV6w7EchcbvZ5ZP/9HuDyOmYYS5G2tqV7BdpCRArnBAn4/LIk2qo.',
        'Hugh', 'Rocha', '1999-05-12', '(717) 550-1675', 'Second user address'),
       (3, 'third', 'User3@user.com', '$2a$12$oV6w7EchcbvZ5ZP/9HuDyOmYYS5G2tqV7BdpCRArnBAn4/LIk2qo.',
        'Spencer', 'Rivers', '2004-11-30', '(248) 762-0356', 'Third user address'),
       (4, 'fourth', 'User4@user.com', '$2a$12$oV6w7EchcbvZ5ZP/9HuDyOmYYS5G2tqV7BdpCRArnBAn4/LIk2qo.',
        'Roberta', 'Hansen', '1974-01-23', '(253) 644-2182', 'Fourth user address'),
       (5, 'fifth', 'User5@user.com', '$2a$12$oV6w7EchcbvZ5ZP/9HuDyOmYYS5G2tqV7BdpCRArnBAn4/LIk2qo.',
        'Esther', 'Wilson', '2001-08-09', '(212) 658-3916', 'Fifth user address'),
       (6, 'sixth', 'User6@user.com', '$2a$12$oV6w7EchcbvZ5ZP/9HuDyOmYYS5G2tqV7BdpCRArnBAn4/LIk2qo.',
        'Randy', 'Morgan', '2000-12-30', '(209) 300-2557', 'Sixth user address'),
       (7, 'seventh', 'User7@user.com', '$2a$12$oV6w7EchcbvZ5ZP/9HuDyOmYYS5G2tqV7BdpCRArnBAn4/LIk2qo.',
        'Frances', 'Wise', '1996-11-26', '(252) 258-3799', 'Seventh user address');