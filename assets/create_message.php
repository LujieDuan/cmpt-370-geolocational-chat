<?php
 
/*
 * Following code will create a new message.
 * This is used when reply a chat. 
 * For a new message, it needs content,
 * chat Id (user Id and time Id) that it belongs to, 
 * and user Id of who send it. 
 */
 
// array for JSON response
$response = array();
 
$input = file_get_contents('php://input');

$obj = json_decode($input, true);

// check for required fields
if (isset($obj['creatorId']) && isset($obj['timeId']) &&  isset($obj['message'])&& isset($obj['userId'])) {
 
    $phoneId = $obj['creatorId'];
    $timeId = $obj['timeId'];
    $content = $obj['message'];
    $phoneIdPost = $obj['userId'];
 
    // include db connect class
    require_once __DIR__ . '/db_connect.php';
 
    // connecting to db
    $db = new DB_CONNECT();
 
    // mysql inserting a new row
    $result = mysql_query("INSERT INTO messages(meMessageId, meMessageTime, meContent, meTimeId, mePhoneId, mePhoneIdPost)
    SELECT max(meMessageId) + 1 , now(), '$content', '$timeId', '$phoneId', '$phoneIdPost'
    FROM messages WHERE meTimeId = '$timeId' AND mePhoneId = '$phoneId';");
 
    // check if row inserted or not
    if ($result) {
        // successfully inserted into database
        $response["success"] = 1;
        $response["message"] = "new message successfully created.";
 
        // echoing JSON response
        echo json_encode($response);
    } else {
        // failed to insert row
        $response["success"] = 0;
        $response["message"] = "An error occurred during creating new message.";
 
        // echoing JSON response
        echo json_encode($response);
    }
} else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing for creating a new message";
 
    // echoing JSON response
    echo json_encode($response);
}
?>