<?php
 
/*
 * Following code willupdate user name. If the phoneId is new to
 * database, will insert the phoneId and user name. 
 * need put a phone Id (device Id)
 * and a new name.
 */
 
// array for JSON response
$response = array();

$input = file_get_contents('php://input');

$obj = json_decode($input, true);

// check for required fields
if (isset($obj['userId']) && isset($obj['userShowName'])) {
 
    $id = $obj['userId'];
    $name = $obj['userShowName'];
 
    // include db connect class
    require_once __DIR__ . '/db_connect.php';
 
    // connecting to db
    $db = new DB_CONNECT();
 
    // mysql inserting a new row
    mysql_query("INSERT INTO users(userPhoneId, userShowName) VALUES('$id','$name')");
    $result = mysql_query("UPDATE users SET userShowName = '$name' WHERE userPhoneId = '$id'");
 
    // check if row inserted or not
    if ($result) {
        // successfully inserted into database
        $response["success"] = 1;
        $response["message"] = "update user name successfully!";
 
        // echoing JSON response
        echo json_encode($response);
    } else {
        // failed to insert row
        $response["success"] = 0;
        $response["message"] = "An error occurred during update user name";
 
        // echoing JSON response
        echo json_encode($response);
    }
} else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing on update user name";
 
    // echoing JSON response
    echo json_encode($response);
}
?>