<?php
 
/*
 * Following code will set the tag of a chat.
 * This could be used as the third step of creating a new chat. 
 * A chat can have mutiple tags.
 * So for each tag, call this once. 
 * Need chat Id (user Id with time Id) and tag Name.
 */
 
// array for JSON response
$response = array();
 
 
$input = file_get_contents('php://input');

$obj = json_decode($input, true);

// check for required fields
if (isset($obj['userId']) && isset($obj['timeId']) && isset($obj['tagName'])) {
 
    $userId = $obj['userId'];
    $timeId = $obj['timeId'];
    $tagName = $obj['tagName'];

    // include db connect class
    require_once __DIR__ . '/db_connect.php';
 
    // connecting to db
    $db = new DB_CONNECT();
 
    // mysql inserting a new row
    $result = mysql_query("INSERT INTO chatTags(ctPhoneId, ctTimeId, ctTagName) VALUES('$chatId', '$timeId', '$tagName')");
 
    // check if row inserted or not
    if ($result) {
        // successfully inserted into database
        $response["success"] = 1;
        $response["message"] = "Set tag done.";
 
        // echoing JSON response
        echo json_encode($response);
    } else {
        // failed to insert row
        $response["success"] = 0;
        $response["message"] = "An error occurred during set new tag to chat";
 
        // echoing JSON response
        echo json_encode($response);
    }
} else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing when set new tag to chat";
 
    // echoing JSON response
    echo json_encode($response);
}
?>