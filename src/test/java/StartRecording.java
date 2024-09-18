import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import static io.restassured.RestAssured.given;

public class StartRecording {

    public static String meetingUrl;
    private static String Base_Url = "https://api.100ms.live/v2/recordings/room/";
    private static String Room_Id = System.getenv("Room_Id");
    private static String Auth_Token = System.getenv("Auth_Token");

    private static String payloadPath = "C:/100ms/untitled/src/testData/startRecordingPayload.json"; // Path to the payload

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = Base_Url;
    }

    @Test
    public void startRecording() throws IOException {

        String startRecordingEndpoint = Room_Id + "/start";

        String payload = new String(Files.readAllBytes(Paths.get(payloadPath)));

        Response response = given()
                .header("Authorization", "Bearer " + Auth_Token)
                .contentType("application/json")
                .body(payload)
                .when()
                .post(startRecordingEndpoint)
                .then()
                .statusCode(200)  // Expecting a 200 status code for successful start
                .extract()
                .response();

        // ------------------------ Basic assertions ------------------------------------------------//

        String name = response.jsonPath().getString("id");
        Assertions.assertNotNull(name);

        String roomId = response.jsonPath().getString("room_id");
        Assertions.assertNotNull(roomId);

        meetingUrl = response.jsonPath().getString("meeting_url");
        //---------------------------------------------------------------------------------------------//

        // ------------------------ Error thrown for multiple calls ------------------------------------//
        Response secondResponse = given()
                .header("Authorization", "Bearer " + Auth_Token)
                .contentType("application/json")
                .body(payload)
                .when()
                .post(startRecordingEndpoint)
                .then()
                .statusCode(409)
                .extract()
                .response();

        String errorMsg = response.jsonPath().getString("message");
        Assertions.assertTrue(errorMsg.equals("beam already started"));

        //----------------------------------------------------------------------------------------------//
    }

    //--------------------------- edge case -------------------------------------------//
    @Test
    public void invalidRoomId() throws IOException {
        String startRecordingEndpoint = "1" + "/start";

        String payload = new String(Files.readAllBytes(Paths.get(payloadPath)));

        Response response = given()
                .header("Authorization", "Bearer " + Auth_Token)
                .contentType("application/json")
                .body(payload)
                .when()
                .post(startRecordingEndpoint)
                .then()
                .statusCode(403)  // Expecting a 200 status code for successful start
                .extract()
                .response();

        String errorMsg = response.jsonPath().getString("message");
        Assertions.assertTrue(errorMsg.equals("user does not have required permission"));
    }
    @Test
    public void invalidAuthKey() throws IOException {

        String startRecordingEndpoint = Room_Id + "/start";

        String payload = new String(Files.readAllBytes(Paths.get(payloadPath)));

        Response response = given()
                .header("Authorization", "Bearer " + "random")
                .contentType("application/json")
                .body(payload)
                .when()
                .post(startRecordingEndpoint)
                .then()
                .statusCode(401)  // Expecting a 200 status code for successful start
                .extract()
                .response();

        String errorMsg = response.jsonPath().getString("message");
        Assertions.assertTrue(errorMsg.equals("Token validation error"));
    }
}
