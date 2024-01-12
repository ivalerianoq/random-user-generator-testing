import static io.restassured.RestAssured.given;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.hamcrest.Matchers.*;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.ArrayList;
import java.util.List;

@Tag("RandomUserData")
class RandomUserTests {

    @Test
    @Timeout(60)
    @DisplayName("it should generate random user data for a US citizen over 50 years old")
    void itShouldReturnUSCitizenOver50Test() {
        given()
                .queryParam("nat", "us")
                .when()
                .get("https://randomuser.me/api/")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .log().ifValidationFails()
                .body("results", hasSize(1),
                        "results[0].dob.age", greaterThan(50),
                        "results[0].nat", equalTo("US"));
    }

    @Test
    @Timeout(60)
    @DisplayName("it should generate number of random users specified in results param")
    void itShouldReturnMultipleUsersTest() {
        var response = given()
                .queryParam("results", 10)
                .when()
                .get("https://randomuser.me/api/")
                .then()
                .statusCode(HttpStatus.SC_OK);
        JSONArray randomUsers = new JSONObject(response.extract().asString()).getJSONArray("results");
        assertThatJson(randomUsers).isArray().hasSize(10);
        JSONArray sortedUsers = sortByFirstName(randomUsers);
        System.out.print(sortedUsers.toString(1));
    }

    private JSONArray sortByFirstName(JSONArray randomUsers) {
        List<JSONObject> sortedUsers = new ArrayList<>();
        for (int i = 0; i < randomUsers.length(); i++) {
            sortedUsers.add(randomUsers.getJSONObject(i));
        }
        sortedUsers.sort((randomUserA, randomUserB) ->
                String.CASE_INSENSITIVE_ORDER.compare(
                        randomUserA.getJSONObject("name").getString("first"),
                        randomUserB.getJSONObject("name").getString("first")));
        return new JSONArray(sortedUsers);
    }

    /*private JSONArray sortByFirstName(JSONArray randomUsers) {
        TreeSet<JSONObject> sortedUsers = new TreeSet<>((randomUserA, randomUserB) ->
                String.CASE_INSENSITIVE_ORDER.compare(
                        randomUserA.getJSONObject("name").getString("first"),
                        randomUserB.getJSONObject("name").getString("first")));

        for(int i = 0; i < randomUsers.length(); i++) {
            sortedUsers.add(randomUsers.getJSONObject(i));
        }
        return new JSONArray(sortedUsers);
    }*/
}
