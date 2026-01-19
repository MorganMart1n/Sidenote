package com.example;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;
public class ApearOnTop {
    //Clicked bool
    static boolean value = false;
    public static void apearOnTop(ActionEvent event){
            //Get the current Stage and set always on top
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                value = !value;
                if (value == true) {
                    stage.setAlwaysOnTop(true);
                }else{stage.setAlwaysOnTop(false);}
            }
}   
