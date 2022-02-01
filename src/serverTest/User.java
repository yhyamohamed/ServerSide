package serverTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class User {
    private int id;
    private String username;
    private String email;
    private int mark ;
    static int count=1;
static HashMap<Integer, User> userData = new HashMap<>();
static {
    userData.put(count++,new User(1,"yaya","w enta malk",9));
    userData.put( count++,new User(2,"yaya2","w enta malk2",9));
}

    public User(int id ,String uname , String mail ,int mark){
        this.id = id;
        username = uname;
        email=mail;
        this.mark= mark;


    }
    public static  void add(User us){
        userData.put(count++,us);
    }

    public boolean checkCre(String user,String mail){
        boolean found = false;
        for(HashMap.Entry m:userData.entrySet()){
            if(m.getValue() instanceof  User){
                System.out.println( "checking " + ((User) m.getValue()).username);
                System.out.println("against " +user);
              if (((((User) m.getValue()).username).equals(user))||((((User) m.getValue()).email).equals(mail))){

                  found=true;
                }
            }
    }
        return found;
    }
    public static void main(String[] args) {

//        System.out.println(userData.get(1).toArray());


    }
}
