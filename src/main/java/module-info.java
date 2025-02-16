module com.dev.tool {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.zxing;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;
    requires java.sql;
    requires org.apache.logging.log4j;
    requires com.zaxxer.sparsebitset;
    requires commons.math3;
    requires javafx.graphics;
    requires java.desktop;
    requires org.apache.pdfbox;


    opens com.dev.tool to javafx.fxml;
    exports com.dev.tool;
}