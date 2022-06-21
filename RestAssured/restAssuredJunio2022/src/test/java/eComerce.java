import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
//import com.jayway.jsonpath.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
//import org.junit.FixMethodOrder;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

public class eComerce {

    //Variables
    static private String url_base = "webapi.segundamano.mx";
    static private String token_basic1 = "dGVzdDFfYWdlbnRlQG1haWxpbmF0b3IuY29tOjEyMzQ1Ng==";
    static private String email="test1_agente@mailinator.com";
    static private String pass="123456";
    static private String access_token;
    static private String account_id;
    static private String uuid;


    @Test
    @Order(1)
    public void obtener_categorias(){
        RestAssured.baseURI = String.format("https://%s/nga/api/v1.1/public/categories/filter",url_base);

        Response response=given()
                .log().all()
                .queryParam("lang","es")
                .get();

        String body_response = response.getBody().asString();
        String headers_response = response.getHeaders().toString();

        System.out.println("Body Response: " + body_response);
        System.out.println("Heades: " + headers_response);

        assertEquals(200,response.getStatusCode());
        assertNotNull(body_response);
        assertTrue(body_response.contains("all_categories"));
    }

    @Test
    @Order(2)
    public void obtener_Token_usando_header_authorization(){
        RestAssured.baseURI = String.format("https://%s/nga/api/v1.1/private/accounts",url_base);

        Response response=given()
                .log().all()
                .queryParam("lang","es")
                .header("Authorization","Basic " + token_basic1)
                .post();

        String body_response = response.getBody().asString();
        String headers_response = response.getHeaders().toString();

        System.out.println("Body Response: " + body_response);
        System.out.println("Heades: " + headers_response);

        assertEquals(200,response.getStatusCode());
        assertNotNull(body_response);
        assertTrue(body_response.contains("access_token"));

    }

    @Test
    @Order(3)
    public void obtener_token_usando_auth_emil_pass(){
        RestAssured.baseURI = String.format("https://%s/nga/api/v1.1/private/accounts",url_base);

        Response response=given()
                .log().all()
                .queryParam("lang","es")
                .auth().preemptive().basic(email,pass)
                .post();

        String body_response = response.getBody().asString();
        String body_pretty = response.prettyPrint();
        String headers_response = response.getHeaders().toString();

        System.out.println("Body Response: " + body_pretty);
        System.out.println("Heades: " + headers_response);
        System.out.println("status response: " + response.getStatusCode());
        System.out.println("time response: " + response.getTime());

        assertEquals(200,response.getStatusCode());
        // Validar el tiempo de respuesta
        //assert
        assertNotNull(body_response);
        assertTrue(body_response.contains("access_token"));

        //Obtener valores de variables

        //access_token = JsonPath.read(body_response,"$.access_token");
        //access_token = JsonPath.read(body_response, "$.access_token");
        //System.out.println("Access token: " + access_token);

        JsonPath jsonResponse = response.jsonPath();

        System.out.println("AccessToken: "+ jsonResponse.get("access_token"));
        access_token = jsonResponse.get("access_token");
        System.out.println("Access token: " + access_token);

        //System.out.println("account_id: " + JsonPath.read(body_response, "$.account.account_id"));
        // System.out.println("uuid: " + JsonPath.read(body_response, "$.account.uuid"));
        System.out.println("Account id: "+ jsonResponse.get("account.account_id"));
        System.out.println("uuid: "+ jsonResponse.get("account.uuid"));

        account_id = jsonResponse.get("account.account_id");
        uuid = jsonResponse.get("account.uuid");
    }

    @Test
    @Order(4)
    public void editar_info_usuario(){

        String body_request = "{\"account\"" +
                ":{\"name\":\"Paloma Cabeza de Vaca\"," +
                "\"phone\":\"5323034487\"," +
                "\"locations\"" +
                ":[{\"code\":\"8\"," +
                "\"key\":\"region\"," +
                "\"label\":\"Colima\"," +
                "\"locations\"" +
                ":[{\"code\":\"110\"," +
                "\"key\":\"municipality\"," +
                "\"label\":\"Tecomán\"}]}]," +
                "\"professional\":false," +
                "\"phone_hidden\":false}}";

        RestAssured.baseURI=String.format("https://%s/nga/api/v1%s",url_base,account_id);

        Response response=given()
                .log().all()
                .queryParam("lang","es")
                .header("Content-type","application/json")
                .header("Authorization","tag:scmcoord.com,2013:api " + access_token)
                .body(body_request)
                .patch();

        String body_response = response.getBody().asString();

        System.out.println("Body Response: " + body_response);

        assertEquals(200,response.getStatusCode());
        assertNotNull(body_response);
        assertTrue(body_response.contains("access_token"));
    }
}
