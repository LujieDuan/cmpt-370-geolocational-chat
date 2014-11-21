 -- For cmpt370 project: Local Messenger 
 -- this file creates tables

    -- drop tables first
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS messages CASCADE;
DROP TABLE IF EXISTS chats CASCADE;
DROP TABLE IF EXISTS tags CASCADE;
DROP TABLE IF EXISTS chatTags CASCADE;


    -- users table
    -- PhoneId is device Id 
CREATE TABLE users (
    userPhoneId varchar(20) NOT NULL PRIMARY KEY,
    userShowName varchar(20) NOT NULL
);

    -- chats table
    -- phonr Id of the poster and time Id are primary key for each chat
CREATE TABLE chats (
    chPhoneId varchar(20) NOT NULL
            REFERENCES users
                ON UPDATE CASCADE
                ON DELETE CASCADE,
    chTimeId timestamp NOT NULL,
    chChatTitle varchar(100) NOT NULL,
    chLatitude decimal(10,6) NOT NULL,
    chLongitude decimal(10,6) NOT NULL,
    chEndTimeMax timestamp,        -- user set max end time
    chEndTime timestamp,           -- this end time is updated whenever there is a new reply
    PRIMARY KEY(chPhoneId, chTimeId)
);

    -- messages table
CREATE TABLE messages (
    meMessageId int NOT NULL,
    meMessageTime timestamp NOT NULL,
    meContent text NOT NULL,
    meTimeId varchar(50) NOT NULL,
    mePhoneId varchar(20) NOT NULL,      -- this is the phone Id of chat poster
    mePhoneIdPost varchar(20) NOT NULL
            REFERENCES users
                ON UPDATE CASCADE
                ON DELETE CASCADE,     -- this is the phone Id of reply poster
    PRIMARY KEY(meMessageId, meTimeId, mePhoneId, mePhoneIdPost),
    FOREIGN KEY(mePhoneId, meTimeId)
                REFERENCES chats
                    ON UPDATE CASCADE
                    ON DELETE CASCADE
);

    -- tags table
CREATE TABLE tags (
    taTagName varchar(100) NOT NULL PRIMARY KEY
);

    -- the mIddle relation table between tags and chats
CREATE TABLE chatTags (
    ctPhoneId varchar(20) NOT NULL,
    ctTimeId timestamp NOT NULL,
    ctTagName varchar(100) NOT NULL
                REFERENCES tags
                    ON UPDATE CASCADE
                    ON DELETE CASCADE,
    PRIMARY KEY(ctPhoneId, ctTimeId, ctTagName),
    FOREIGN KEY(ctPhoneId, ctTimeId)
                REFERENCES chats
                    ON UPDATE CASCADE
                    ON DELETE CASCADE
);