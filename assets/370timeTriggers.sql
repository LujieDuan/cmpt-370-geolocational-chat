-- For cmpt370 project: local messenger
-- this file create triggers that can update end time and auto delete chat



    -- This trigger set the end time to default 5 days after posting
DELIMITER $$
DROP TRIGGER IF EXISTS userSetEndTimeConstraint $$
CREATE TRIGGER userSetEndTimeConstraint
AFTER INSERT on chats
FOR EACH ROW
BEGIN
UPDATE chats SET chEndTime = ADDDTIME(now() + '5 00:00:00')
WHERE chPhoneId = NEW.chPhoneId and chTimeId = NEW.chTimeId;
END $$
DELIMITER ;


    -- The above trigger can also be replaceb by this
ALTER TABLE chats ALTER chEndTime SET DEFAULT ADDDTIME(now() + '5 00:00:00');


    -- create a trigger that increase the end time by one day whenever a chat gets a new reply
DELIMITER $$
DROP TRIGGER IF EXISTS endTimeIncrease $$
CREATE TRIGGER endTimeIncrease
AFTER INSERT on messages
FOR EACH ROW
BEGIN
IF ((SELECT chEndTime FROM chats WHERE NEW.mePhoneId = chPhoneId AND NEW.meTimeId = chTimeID) <  ADDDTIME(now() + '1 00:00:00')
    UPDATE chats SET chEndTime = ADDDTIME(now() + '1 00:00:00')
WHERE chPhoneId = NEW.mePhoneId and chTimeId = NEW.meTimeId;
END IF;
END $$
DELIMITER ;
    
    

    
    -- Create a dummy table that get 'update' or 'pin' every time a client do select on chats
    -- then this 'update' will trigger the auto chats delete trigger

CREATE TABLE pinDatabase(
    pin int
);
    
    -- create a trigger that delete the chats based on chEndTimeMax (user set end time) and chEndTime (default end time)
DELIMITER $$
DROP TRIGGER IF EXISTS autoDeleteChats $$
CREATE TRIGGER autoDeleteChats
AFTER UPDATE on pinDatabase
FOR EACH ROW
BEGIN
DELETE FROM chats WHERE chEndTime < now() OR chatEndTimeMax < now();
END $$
DELIMITER ;

    
    