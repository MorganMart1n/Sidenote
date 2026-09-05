module com.example { 
    requires transitive javafx.controls; 
    requires javafx.fxml; 
    requires transitive javafx.graphics; 
    requires transitive javafx.base; 

    requires transitive org.fxmisc.richtext;
    requires org.fxmisc.flowless;
    requires reactfx;

    opens com.example to javafx.fxml; 

    opens com.example.css to javafx.graphics; 

    exports com.example; 
}