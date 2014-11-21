<?php
 
/*
 * This class is used for get the user show name
 * by specifiy user Id. 
 */
 
// array for JSON response
$response = array();
 
// include db connect class
require_once __DIR__ . '/db_connect.php';
 
// connecting to db
$db = new DB_CONNECT();
 
// check for post data
if (isset($_GET["phoneId"])) {
    $id = $_GET['phoneId'];
 
    // get the time ID from chats table
    $result = mysql_query("SELECT userShowName FROM users WHERE userPhoneId = $id");
 
    if (!empty($result)) {
        // check for empty result
        if (mysql_num_rows($result) > 0) {
 
            $result = mysql_fetch_array($result);
            // success
            $response["success"] = 1;
            $response["showName"] = $result["userShowName"];
 
            // echoing JSON response
            echo json_encode($response);
        } else {
            // no product found
            $response["success"] = 0;
            $response["message"] = "No user found during fetching user show name. ";
 
            // echo no users JSON
            echo json_encode($response);
        }
    } else {
        // no product found
        $response["success"] = 0;
        $response["message"] = "No user found during fetching user show name";
 
        // echo no users JSON
        echo json_encode($response);
    }
} else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing during fetching user show name. ";
 
    // echoing JSON response
    echo json_encode($response);
}
?>