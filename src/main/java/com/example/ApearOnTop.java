package com.example;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;

public class ApearOnTop {

    static boolean value = false;
    public static void apearOnTop(ActionEvent event){
            
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                value = !value;
                if (value == true) {
                    stage.setAlwaysOnTop(true);
                }else{stage.setAlwaysOnTop(false);}

            }
}   
