package sample;

import javafx.concurrent.Worker;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class AlternativeXmlWevViewHelper {
    public AlternativeXmlWevViewHelper(WebView webView) {
        webView.getEngine().getLoadWorker().stateProperty().addListener(
                (ov, oldState, newState) -> {
                    if (newState == Worker.State.SUCCEEDED) {
                        try {
                            WebEngine engine = webView.getEngine();
                            String highlightScript = readFile(Main.class.getResource("/hl.js").getPath());
                            Document page = engine.getDocument();
                            Node head = page.getElementsByTagName("head").item(0);


                            Element jquery = page.createElement("script");
                            jquery.setAttribute("src", Main.class.getResource("/jquery-2.1.4.min.js").toExternalForm());
                            head.appendChild(jquery);

                            Element jqueryHighlight = page.createElement("script");
                            jqueryHighlight.setTextContent(highlightScript);
                            head.appendChild(jqueryHighlight);



                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public void setXML(WebView webView, String xml) {
        webView.getEngine().loadContent(
                "<textarea readonly style='width:100%; height:100%'>"+ xml +"</textarea>");
    }

    public void highlight(WebView webView, String text) {
        if (text == null) text = "";
        webView.getEngine().executeScript("highlight('" + text + "')");
    }

    private String readFile(String pathname) throws IOException {

        File file = new File(pathname);
        StringBuilder fileContents = new StringBuilder((int) file.length());
        String lineSeparator = System.getProperty("line.separator");

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                fileContents.append(scanner.nextLine()).append(lineSeparator);
            }
            return fileContents.toString();
        }
    }

}
