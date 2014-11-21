    
-- For cmpt370 project: local messenger
-- this file insert testing data to database


    -- new user
INSERT INTO users(userPhoneId, userShowName) 
    VALUES ('123456789012345', 'Mike');
INSERT INTO users(userPhoneId, userShowName) 
    VALUES ('123456789012346', 'Jack');


    -- new chat
INSERT INTO chats(chPhoneId, chTimeId, chChatTitle, chLatitude, chLongitude)
    VALUES ('123456789012345', now(), 'first chat!!', 54.0, 23.0);
INSERT INTO chats(chPhoneId, chTimeId, chChatTitle, chLatitude, chLongitude)
    VALUES ('123456789012346', now(), 'second chat!!', 14.0, 13.0);

SELECT * FROM users;
SELECT * FROM chats;
