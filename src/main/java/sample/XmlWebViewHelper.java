package sample;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import javafx.scene.web.WebView;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.net.URL;
import java.util.Scanner;
import java.util.regex.Matcher;


public class XmlWebViewHelper {

    private static final String TEMPLATE_HTML_PATH = "/index.html";
    private static final String PRETTIFY_JS_PATH = "/prettify.js";
    private static final String PRETTIFY_CSS_PATH = "/prettify.css";
    private static final String JQUERY_JS_PATH = "/jquery-2.1.4.min.js";

    private String template;

    public XmlWebViewHelper(WebView webView) {
        template = readFile(getResource(TEMPLATE_HTML_PATH).getPath());
        // for some reason relative paths in index.html
        // don't work and i had to fix them this way:
        template = fixPath(template, PRETTIFY_JS_PATH);
        template = fixPath(template, PRETTIFY_CSS_PATH);
        template = fixPath(template, JQUERY_JS_PATH);
    }


    public void highlight(WebView webView, String text) {
        if (text == null) text = "";
        webView.getEngine().executeScript("highlight('" + text + "')");
    }

    public void setXML(WebView webView, String xml) {
        if (xml == null) xml = "";
        try {
            webView.getEngine().loadContent(formattedXML(xml));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }

    private String fixPath(String src, String path) {
        return src.replace(path, getResource(path).toExternalForm());
    }

    private static URL getResource(String path) {
        return XmlWebViewHelper.class.getResource(path);
    }

    public void logDocument(WebView webView) {
        Document document = webView.getEngine().getDocument();
        System.out.println(document + " " + stringFromDocument(document));
    }

    private String formattedXML(String s) throws IOException, ParserConfigurationException, SAXException {
        s = format(s);
        s = encodeHTML(s);

        String page = template.replaceFirst("(?s)<pre[^>]*>.*?</pre>",
                Matcher.quoteReplacement("<pre class='prettyprint'>" + s + "</pre>"));

        return page;
    }

    private static String encodeHTML(String s) {
        StringBuffer out = new StringBuffer();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c > 127 || c == '"' || c == '<' || c == '>') {
                out.append("&#" + (int) c + ";");
            } else {
                out.append(c);
            }
        }
        return out.toString();
    }

    private String format(String unformattedXml) throws IOException, ParserConfigurationException, SAXException {
        String result = "";
        final Document document = documentFromString(unformattedXml);
        if (document != null) {
            OutputFormat format = new OutputFormat(document);
            format.setOmitXMLDeclaration(true);
            format.setLineWidth(65);
            format.setIndenting(true);
            format.setIndent(2);
            Writer out = new StringWriter();
            XMLSerializer serializer = new XMLSerializer(out, format);
            serializer.serialize(document);
            result = out.toString();
        } else {
            System.err.println("Wrong Document Format document != null in format(String unformattedXml)");
        }

        return result;
    }

    private String stringFromDocument(Document doc) {
        try {
            DOMSource domSource = new DOMSource(doc);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.transform(domSource, result);
            return writer.toString();
        } catch (TransformerException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private Document documentFromString(String xmlString) throws IOException, ParserConfigurationException, SAXException {
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(xmlString));
        return db.parse(is);
    }

    public static String readFile(String pathname) {
        File file = new File(pathname);
        StringBuilder fileContents = new StringBuilder((int) file.length());
        String lineSeparator = System.getProperty("line.separator");

        try {
            try (Scanner scanner = new Scanner(file)) {
                while (scanner.hasNextLine()) {
                    fileContents.append(scanner.nextLine()).append(lineSeparator);
                }
                return fileContents.toString();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }
}
