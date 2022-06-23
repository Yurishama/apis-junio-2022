import io.qameta.allure.Allure;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import jdk.jfr.Name;
import org.junit.jupiter.api.*;

import java.util.Base64;

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
    static private String username;
    static private int phoneNumber;
    static private String token2;
    static private String addressID;




    @Name("Obtener Token")
    private String obtener_Token(){

        RestAssured.baseURI = String.format("https://%s/nga/api/v1.1/private/accounts?lang=es",url_base);

        Response response=given().log().all()
                .queryParam("lang","es")
                .auth().preemptive().basic(email,pass)
                .post();

        String body_response = response.getBody().asString();
        System.out.println("Body Response: " + body_response);

        JsonPath jsonResponse = response.jsonPath();

        System.out.println("AccessToken: "+ jsonResponse.get("access_token"));
        access_token = jsonResponse.get("access_token");
        System.out.println("Access token: " + access_token);

        System.out.println("Account id: "+ jsonResponse.get("account.account_id"));
        System.out.println("uuid: "+ jsonResponse.get("account.uuid"));

        account_id = jsonResponse.get("account.account_id");
        uuid = jsonResponse.get("account.uuid");

        String datos = uuid+":"+access_token;
        String encodedToken2UP = Base64.getEncoder().encodeToString(datos.getBytes());

        return encodedToken2UP;
    }

    @Test
    @Order(1)
    @DisplayName("Test case: Vaidar que se desplieguen todas las categoras")
    @Severity(SeverityLevel.CRITICAL)
    public void obtener_categorias(){
        RestAssured.baseURI = String.format("https://%s/nga/api/v1.1/public/categories/filter",url_base);

        Response response=given()
                .log().all()
                .queryParam("lang","es")
                .filter(new AllureRestAssured())
                .get();

        String body_response = response.getBody().asString();
        String headers_response = response.getHeaders().toString();
        long time = response.getTime();

        System.out.println("Time: " + time);
        System.out.println("Body Response: " + body_response);
        System.out.println("Heades: " + headers_response);

        assertEquals(200,response.getStatusCode());
        assertNotNull(body_response);
        assertTrue(body_response.contains("all_categories"));
        assertTrue(time < 900);

    }

    @Test
    @Order(2)
    @DisplayName("Test case: Vaidar que se desplieguen todas las categoras")
    @Severity(SeverityLevel.BLOCKER)
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
        //new
        JsonPath jsonResponse = response.jsonPath();

        System.out.println("AccessToken: "+ jsonResponse.get("access_token"));
        access_token = jsonResponse.get("access_token");

        assertEquals(email,jsonResponse.get("account.email"));
        assertEquals("tag:scmcoord.com,2013:api", jsonResponse.get("token_type"));
        //Validar que el contenido de datos del token
        assertTrue(access_token.matches("[A-Za-z0-9-_]+"));
        assertTrue(headers_response.contains("Content-Type"));

        long time = response.getTime();
        System.out.println("Time: " + time);
        assertTrue(time <= 1900);


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
        //int  time = response.getTime();

        //Allure.addAttachment("Tiempo de respuesta", response.getTime()));
        Allure.addAttachment("Boddy pretty: ", body_pretty);

        System.out.println("Body Response: " + body_pretty);
        System.out.println("Heades: " + headers_response);
        System.out.println("status response: " + response.getStatusCode());
        System.out.println("time response: " + response.getTime());

        assertEquals(200,response.getStatusCode());
        assertNotNull(body_response);
        assertTrue(body_response.contains("access_token"));


        JsonPath jsonResponse = response.jsonPath();

        System.out.println("AccessToken: "+ jsonResponse.get("access_token"));
        access_token = jsonResponse.get("access_token");
        System.out.println("Access token: " + access_token);
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
        assertTrue(body_response.contains("account"));
    }

    @Test
    @Order(5)
    public void crear_usuario_nuevo(){
        username = "agente_ventas" + (Math.floor(Math.random()*435)+4321) + "@mailinator.com";
        double passworrd = ((Math.random()*488)+ 54321);

        String datos = username + ":" + passworrd;
        String encode = Base64.getEncoder().encodeToString(datos.getBytes());
        String bodyRequest =  "{\"account\":{\"email\":\""+ username + "\"}}";

        RestAssured.baseURI = String.format("https://%s/nga/api/v1.1/private/accounts",url_base);

        Response response = given().log().all()
                .header("Authorization","Basic " + encode)
                .queryParam("lang","es")
                .contentType("application/json")
                .body(bodyRequest)
                .post();

        String body_response = response.getBody().asString();

        System.out.println("Body Response: " + body_response);

        assertEquals(401,response.getStatusCode());
        assertNotNull(body_response);
        assertTrue(body_response.contains("{\"error\":{\"code\":\"ACCOUNT_VERIFICATION_REQUIRED\"}}"));

    }

    @Test
    @Order(6)
    public void update_telefono(){
        phoneNumber = (int) (Math.random()*999999999+987654321);
        String bodyRequest="{\"account\":{\"name\":\""+username+"\",\"phone\":\""+ phoneNumber+"\",\"professional\":false}}";

        RestAssured.baseURI = String.format("https://%s/nga/api/v1%s",url_base,account_id);

        Response response=given()
                .log().all()
                .queryParam("lang","es")
                .contentType("application/json")
                .accept("application/json, text/plain, */*")
                .header("Authorization","tag:scmcoord.com,2013:api " + access_token)
                .body(bodyRequest)
                .patch();

        String body_response = response.getBody().asString();

        System.out.println("Body Response: " + body_response);

        assertEquals(200,response.getStatusCode());
        assertNotNull(body_response);
        assertTrue(body_response.contains("account"));


        //Validar un campo para asegurarse que es el mismo
        JsonPath jsonResponse = response.jsonPath();

        System.out.println("Telefono: "+ jsonResponse.get("account.phone"));
        System.out.println("Telefono request: "+  phoneNumber);
        String phoneRespone = jsonResponse.getString("account.phone");
        assertEquals(phoneRespone,""+ phoneNumber);

    }

    @Test
    @Order(7)
    public void crear_direccion(){
        // phoneNumber

        String token2UP =uuid + ":" + access_token;
        token2 = Base64.getEncoder().encodeToString(token2UP.getBytes());

        RestAssured.baseURI = String.format("https://%s/addresses/v1/create",url_base);

        Response response= given()
                .log().all()
                .header("Content-type","application/x-www-form-urlencoded")
                .header("Authorization","Basic "+ token2 )
                .formParam("contact",username+" Lopez ")
                .formParam("phone",phoneNumber)
                .formParam("rfc","CAPL800101")
                .formParam("zipCode","45999")
                .formParam("exteriorInfo","Lopez Mateos 761283")
                .formParam("region","11")
                .formParam("municipality","292")
                .formParam("area","7488")
                .formParam("alias","Casa")
                .filter(new AllureRestAssured())
                .post();


        String body_response = response.getBody().asString();

        System.out.println("Body Response: " + body_response);

        assertEquals(201,response.getStatusCode());
        assertNotNull(body_response);
        assertTrue(body_response.contains("addressID"));


        //Ambas funcionan
        JsonPath jsonResponse = response.jsonPath();
        //addressID =jsonResponse.get("addressID");
        addressID = response.jsonPath().get("addressID");
        System.out.println("Address: "+ addressID);

    }

    @Test
    @Order(8)
    public void borrar_direccion(){
        //https://{{url_base}}/addresses/v1/delete/{{addressID}}

        RestAssured.baseURI = String.format("https://%s/addresses/v1/delete/%s",url_base,addressID);

        Response response=given()
                .log().all()
                .auth().preemptive().basic(uuid,access_token)
                .delete();
        String body_response = response.getBody().asString();

        System.out.println("Body Response: " + body_response);

        assertEquals(200,response.getStatusCode());
        assertNotNull(body_response);
        assertTrue(body_response.contains("{\"message\":\""+addressID+" deleted correctly\"}"));

    }

    //Crear un anuncio usando la funcion para tener un token
    @Test
    @Order(9)
    public void crear_anuncio(){
        String tokenUp2 = obtener_Token();
        System.out.println("Token 2 "+ tokenUp2);

        RestAssured.baseURI = String.format("https://%s/v2/accounts/%s/up",url_base,uuid);

        System.out.printf("Endpoint: %s",RestAssured.baseURI);

        String body_request="{\"category\":\"8121\"," +
                "\"subject\":\"Mudanzas y fletes a todo el pais\",\"body\":\"Si estas buscando una mudanza barata, esta es tu opción. Tenemos cobertura en todo el país\",\"region\":\"5\",\"municipality\":\"51\",\"area\":\"140000\",\"price\":\"1\",\"phone_hidden\":\"true\",\"show_phone\":\"false\",\"contact_phone\":\"9977886655\"}";

        Response response = given()
                .log().all()
                .header("Authorization","Basic "+tokenUp2)
                .contentType("application/json")
                .accept("application/json, text/plain, */*")
                .body(body_request)
                .header("x-source","PHOENIX_DESKTOP")
                .post();

        String body_response = response.getBody().asString();

        System.out.println("Body Response: " + body_response);

        assertEquals(200,response.getStatusCode());
        assertNotNull(body_response);

    }

    @Test
    @Order(10)
    public void ver_anuncios_pendientes(){
        RestAssured.baseURI =  String.format("https://%s/nga/api/v1%s/klfst?status=pending&lim=20&o=0&query=&lang=es",url_base,account_id);

        Response response = given()
                .log().all()
                .queryParam("status","pending")
                .queryParam("lim","10")
                .queryParam("o","0")
                .queryParam("query","")
                .queryParam("lang","es")
                .header("Authorization","tag:scmcoord.com,2013:api "+ access_token)
                .get();

        String body_response = response.getBody().asString();

        System.out.println("Body Response: " + body_response);

        assertEquals(200,response.getStatusCode());
        assertNotNull(body_response);

    }
}
