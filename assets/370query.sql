
-- For cmpt370 project: local messenger
-- this file do test queries

-- get all chats in range, along with the first message and user name
SELECT 
        chChatId,
        taTagName,
        userShowName, 
        meContent, 
        meMessageTime
    FROM
        users,
        messages,
        chats,
        tags,
        chatTags
    WHERE
        chChatId = ctChatId AND
        ctTagName = taTagName AND
        chChatId = meChatId AND
        mePhoneId = userPhoneId AND
        memessagetime in (SELECT min(memessagetime) from messages group by mechatid)
;
        


    -- get all messages of a single chat
SELECT 
        meMessageTime,
        meLatitude,
        meLongtitude,
        meContent,
        meChatId
    FROM
        messages
    WHERE 
        meChatId = '201410101234567'
    ORDER BY
        meMessageTime
;