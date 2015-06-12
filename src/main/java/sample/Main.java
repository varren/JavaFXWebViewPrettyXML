package sample;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class Main extends Application {
    private static final String DEMO_XML = "<home>" +
            "<person name=\"Bob\"></person>" +
            "<person name=\"Bob\">Some long person data is here:gogog ogogogogog ogogogo gogogogog ogogogogogg gggggg ggggggg</person>" +
            "</home>";
    private static final String DEMO_XML3 = "<home></home>";
    private static final String DEMO_XML2 = XmlWebViewHelper.readFile(XmlWebViewHelper.class.getResource("/index.html").getPath());
    @Override
    public void start(Stage primaryStage) throws Exception{
        final WebView webView = new WebView();

        XmlWebViewHelper xmlLoader = new XmlWebViewHelper();
        xmlLoader.setXML(webView, DEMO_XML);

        TextField textField = new TextField();

        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            xmlLoader.highlight(webView, newValue);
        });

        VBox root = new VBox();

        Button buttonLoadXml1 = new Button("Try demo xml");
        buttonLoadXml1.setOnMouseClicked(event -> xmlLoader.setXML(webView, DEMO_XML));


        Button buttonLoadXml2 = new Button("Try other xml");
        buttonLoadXml2.setOnMouseClicked(event -> xmlLoader.setXML(webView, DEMO_XML2));

        Button buttonLoadXml3 = new Button("Try other xml");
        buttonLoadXml3.setOnMouseClicked(event -> xmlLoader.setXML(webView, DEMO_XML3));
        root.getChildren().addAll(webView, textField,buttonLoadXml1, buttonLoadXml2,buttonLoadXml3);

        Scene scene = new Scene(root, 600, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
