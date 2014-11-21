
-- For cmpt370 project: local messenger
-- this file insert testing data in database

-- new message, aka new reply
INSERT INTO messages(meMessageId, meMessageTime, meContent, meTimeId, mePhoneId, mePhoneIdPost)
    VALUES (0, '2014-10-12 04:08:06', 'HELLO WORLD!', '2014-10-25 16:46:29', '123456789012345', '123456789012345');
INSERT INTO messages(meMessageId, meMessageTime, meContent, meTimeId, mePhoneId, mePhoneIdPost)
    SELECT max(meMessageId) + 1 , '2014-10-12 04:08:06', 'HELLO WORLD111!', '2014-10-25 16:46:29', '123456789012345', '123456789012346'
    FROM messages WHERE meTimeId = '2014-10-25 16:46:29' AND mePhoneId = '123456789012345';
INSERT INTO messages(meMessageId, meMessageTime, meContent, meTimeId, mePhoneId, mePhoneIdPost)
    SELECT max(meMessageId) + 1 , '2014-10-12 04:08:06', 'HELLO WORLD111!', '2014-10-25 16:46:29', '123456789012345', '123456789012345'
FROM messages WHERE meTimeId = '2014-10-25 16:46:29' AND mePhoneId = '123456789012345';


INSERT INTO messages(meMessageId, meMessageTime, meContent, meTimeId, mePhoneId, mePhoneIdPost)
    VALUES (0, '2014-10-12 04:08:06', 'HELLO WORLD2222!', '2014-10-25 16:46:29', '123456789012346', '123456789012346');
    -- when create a new chat, first create a chat then a message


    -- new tags
INSERT INTO tags(taTagName) 
    VALUES ('General');
INSERT INTO tags(taTagName)
    VALUES ('Garage sale');



    -- insert into chatTags
INSERT INTO chatTags(ctPhoneId, ctTimeId, ctTagName)
VALUES ('2014-10-25 16:46:29', '123456789012345', 'General'), 
('2014-10-25 16:46:29', '123456789012345', 'Garage sale'), 
('2014-10-25 16:46:29', '123456789012346', 'General');



SELECT * FROM messages;
SELECT * FROM tags;