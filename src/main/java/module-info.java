module com.example { 
    requires javafx.controls; 
    requires javafx.fxml; 
    requires transitive javafx.graphics; 
    requires transitive javafx.base; 

    requires org.fxmisc.richtext;
    requires org.fxmisc.flowless;
    requires reactfx;
    opens com.example to javafx.fxml; exports com.example; }