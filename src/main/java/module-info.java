module com.dev.tool {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.pdfbox;
    requires java.desktop;
    requires com.google.zxing;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;
    requires java.sql;
    requires org.apache.logging.log4j;
    requires org.apache.fontbox;
    requires com.zaxxer.sparsebitset;
    requires commons.math3;


    opens com.dev.tool to javafx.fxml;
    exports com.dev.tool;
}