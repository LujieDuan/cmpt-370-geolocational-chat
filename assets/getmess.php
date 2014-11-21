<?php
 
/*
 * Following code will list all the messages
 * of a single chat from the last update.
 * If want all the messages of a chat, just post 0
 * as last message Id.
 * Also need the chat Id (user Id and time Id).
 */
 
// array for JSON response
$response = array();
 
// include db connect class
require_once __DIR__ . '/db_connect.php';
 
// connecting to db
$db = new DB_CONNECT();
 
// check for post data
if (isset($_GET["creatorId"]) && isset($_GET["timeId"]) && isset($_GET["lastMessageId"])) {
    $userId = $_GET['creatorId'];
    $timeId = $_GET['timeId'];
    $lastMessageId = $_GET['lastMessageId'];
 
    // get messages from messages table that start after last massage
    $result = mysql_query("SELECT meMessageId, meMessageTime, meContent, mePhoneIdPost, userShowName FROM messages, users WHERE mePhoneId = '$userId' AND meTimeId = '$timeId' AND meMessageId > '$lastMessageId' AND userPhoneId = mePhoneIdPost ORDER BY meMessageId;");
 
    if (!empty($result)) {
        // check for empty result
        if (mysql_num_rows($result) > 0) {
            // looping through all results
            // products node
            $response["messages"] = array();
 
            while ($row = mysql_fetch_array($result)) {
                // temp user array
                $message = array();
                $message["messageId"] = $row["meMessageId"];
                $message["time"] = $row["meMessageTime"];
                $message["message"] = $row["meContent"];
                $message["userName"] = $row["userShowName"];
                $message["userId"] = $row["mePhoneIdPost"];
                
                // push single product into final response array
                array_push($response["messages"], $message);
            }
            // success
            $response["success"] = 1;
 
            // echoing JSON response
        echo json_encode($response);
        } else {
            // no products found
            $response["success"] = 1;
            $response["message"] = "No new message found";
 
            // echo no users JSON
            echo json_encode($response);
        }
    } else {
        // no product found
        $response["success"] = 1;
        $response["message"] = "No new message found";
 
        // echo no users JSON
        echo json_encode($response);
    }
} else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing during fetching message. ";
 
    // echoing JSON response
    echo json_encode($response);
}
?>