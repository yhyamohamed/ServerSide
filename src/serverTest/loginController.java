package serverTest;

import com.google.gson.JsonObject;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class loginController {
    private LogInPage loginPage;
    private Stage primaryStage;

    public loginController(LogInPage logInPage, Stage primaryStage) {
        loginPage = logInPage;
        this.primaryStage=primaryStage;
        loginPage.logInBtnAction(logIn());


    }




    private EventHandler<ActionEvent> logIn() {
    return new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {

            System.out.println(loginPage.getUserNameTxt());
            System.out.println(loginPage.getPasswordTxt());
            User us= new User(User.count,loginPage.getUserNameTxt(),loginPage.getPasswordTxt(),9);
            if(us.checkCre(loginPage.getUserNameTxt(),loginPage.getPasswordTxt())){
                primaryStage.close();
                Client client =new Client();

                client.setSize(600, 400);
                client.setResizable(false);
                client.setVisible(true);
                JsonObject userEntered = new JsonObject();
                userEntered.addProperty("user_name",loginPage.getUserNameTxt());
                userEntered.addProperty("pass_word",loginPage.getPasswordTxt());
                userEntered.addProperty("type","login");
                client.sendAnnounce(userEntered );
            }else{
                loginPage.getLogInMsg().setText("not existed");
                loginPage.getLogInMsg().setVisible(true);
            }

        }
    };
    }


}
