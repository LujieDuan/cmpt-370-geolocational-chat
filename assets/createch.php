<?php
 
/*
 * Following code will create a chat.
 * A new chat need a unique chat ID, 
 * which is actually in two parts: the user ID of who 
 * start the chat and the time as time ID. 
 * Also, a chat title, location in latitude and longitude
 * and optional maximum end time.
 * And when create a new chat (with the first message) 
 * in app, need to create a new chat,
 * and get the timeId of this new chat,
 * then create a new
 * message to store the first message, and
 * set the tag of this chat
 */
 
// array for JSON response
$response = array();

$input = file_get_contents('php://input');

$obj = json_decode($input, true);

// check for required fields
if (isset($obj['userId']) && isset($obj['title']) && isset($obj['latitude']) && isset($obj['longitude']) && isset($obj['firstMessage']) && isset($obj['maxEndTime']) && isset($obj['tags'])) {
 
    $id = $obj['userId'];
    $title = $obj['title'];
    $latitude = $obj['latitude'];
    $longitude = $obj['longitude'];
    $maxEndTime = $obj['maxEndTime'];
    $content = $obj['firstMessage'];
    $tags = $obj['tags'];
    
    
    // include db connect class
    require_once __DIR__ . '/db_connect.php';
 
    // connecting to db
    $db = new DB_CONNECT();
    
    // mysql inserting a new row
    $result = mysql_query("INSERT INTO chats(chPhoneId, chTimeId, chChatTitle, chLatitude, chLongitude, chEndTimeMax) VALUES('$id', now(), '$title', '$latitude', '$longitude', '$maxEndTime')");
 
    // check if row inserted or not
    if ($result) {
        // successfully inserted into database
        $response["success"] = 1;
        $response["message"] = "new chat successfully created.";
    } else {
        // failed to insert row
        $response["success"] = 0;
        $response["message"] = "An error occurred during creating new chat.";
        echo json_encode($response);
    }
        
               
           // get the time ID from chats table
    $result = mysql_query("SELECT max(chTimeId) FROM chats WHERE chPhoneId = '$id'");
 
    if (!empty($result)) {
        // check for empty result
        if (mysql_num_rows($result) > 0) {
 
            $result = mysql_fetch_array($result);
            // success
            $response["success"] = 1;
            $timeId = $result["max(chTimeId)"];
        } else {
            // no product found
            $response["success"] = 0;
            $response["message"] = "No chat found during fetching last chat of a user";

            echo json_encode($response);
        }
    } else {
        // no product found
        $response["success"] = 0;
        $response["message"] = "No chat found during fetching last chat of a user";
        echo json_encode($response);
    }
        
        
        
        
         // mysql inserting a new row
    $result = mysql_query("INSERT INTO messages(meMessageId, meMessageTime, meContent, meTimeId, mePhoneId, mePhoneIdPost)
    VALUES (0, now(), '$content', '$timeId', '$id', '$id');");
 
    // check if row inserted or not
    if ($result) {
        // successfully inserted into database
        $response["success"] = 1;
        $response["message"] = "First message successfully created.";
 
        // echoing JSON response
        echo json_encode($response);
    } else {
        // failed to insert row
        $response["success"] = 0;
        $response["message"] = "An error occurred during creating first message.";
 
        // echoing JSON response
        echo json_encode($response);
    }
    
    
    $arrayLength = count($tags);
    for ($x = 0; $x < $arrayLength; $x++) {
        $result = mysql_query("INSERT INTO chatTags(ctPhoneId, ctTimeId, ctTagName) VALUES ('$id', '$timeId', '$tags[$x]')");
        if (!$result){
            $response["success"] = 0;
            $response["message"] = "An error occurred during set tag for chat.";
        }
    }
    
    
    
    
} else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing when creating a new chat.";
 
    // echoing JSON response
    echo json_encode($response);
}
?>