package api.Tests;


import api.Reg.Register;
import api.Reg.SuccessReg;
import api.Reg.UnSuccessReg;
import api.Specification.Specification;
import api.Data.UserData;
import org.junit.Assert;
import org.junit.Test;
import java.util.List;


import static io.restassured.RestAssured.*;

public class ReqresTest {

    private final static String  URL = "https://reqres.in/";


    /** 1. Получить список пользователей
     *  2. Проверить, содержит ли ссылка на аватор id пользователя
     *  3. Проверить, оканчиваются ли email адресса на "@reqres.in"
     */
    @Test
    public void checkAvatarAndIdTest(){
        Specification.installSpecification(Specification.requestSpec(URL), Specification.responseSpecOK200());
        List<UserData> users = given()
                .when()
                .get("api/users?page=2")
                .then().log().all()
                .extract().body().jsonPath().getList("data", UserData.class);

        //Проверка содержит ли аватар id
        users.forEach(x-> Assert.assertTrue(x.getAvatar().contains(x.getId().toString())));
        //Проверка оканчивается ли email адресс на "@reqres.in"
        users.forEach(x-> Assert.assertTrue(x.getEmail().endsWith("@reqres.in")));
    }


    /** 1. Проверить статус запроса для успешной регистрации (должен быть 200)
     *  2. Проверить что полученные id и token после успешной регистрации не ноль
     *  3. Проверить что полученные id и token совпадают с ожидаемыми
     */
    @Test
    public void successReg(){
        Specification.installSpecification(Specification.requestSpec(URL), Specification.responseSpecOK200());
        Integer id = 4;
        String token = "QpwL5tke4Pnpja7X4";

        Register user = new Register("eve.holt@reqres.in", "pistol");
        SuccessReg successReg = given()
                .body(user)
                .when()
                .post("api/register")
                .then().log().all()
                .extract().as(SuccessReg.class);

        Assert.assertNotNull(successReg.getId());
        Assert.assertNotNull(successReg.getToken());

        Assert.assertEquals(id, successReg.getId());
        Assert.assertEquals(token, successReg.getToken());
    }


    /** Проверить, равняется ли статус запроса 400 при неуспешной регистрации
     *
     */
    @Test
    public void unSuccessReg(){
        Specification.installSpecification(Specification.requestSpec(URL), Specification.responseSpecError400());
        Register user = new Register("sydney@fife", "");

        UnSuccessReg unSuccessReg = given()
                .body(user)
                .when()
                .post("api/register")
                .then().log().all()
                .extract().as(UnSuccessReg.class);
        Assert.assertEquals("Missing password", unSuccessReg.getError());
    }


    /**
     * Проверить что при отправке delete запроса на страницу api/users/2 статус ответа будет 204
     */
    @Test
    public void deleteTest(){
        Specification.installSpecification(Specification.requestSpec(URL), Specification.responseSpecUnique(204));
        given()
                .when()
                .delete("api/users/2")
                .then().log().all();
    }
}
