<?php
 
/*
 * Following code will list all tags in database
 */

 
// array for JSON response
$response = array();
 
// include db connect class
require_once __DIR__ . '/db_connect.php';
 
// connecting to db
$db = new DB_CONNECT();
 

// get tags from tags table
$result = mysql_query("SELECT taTagName FROM tags;");

if (!empty($result)) {
    // check for empty result
    if (mysql_num_rows($result) > 0) {
        // looping through all results
        // products node

        while ($row = mysql_fetch_array($result)) {

            $tag = $row["taTagName"];

            // push single product into final response array
            array_push($response, $tag);
        }
        

        // echoing JSON response
    echo json_encode($response);
    } else {
        // no products found
        $response["success"] = 0;
        $response["message"] = "No tag found";

        // echo no users JSON
        echo json_encode($response);
    }
} else {
    // no product found
    $response["success"] = 0;
    $response["message"] = "No tag found";

    // echo no users JSON
    echo json_encode($response);
}

?>