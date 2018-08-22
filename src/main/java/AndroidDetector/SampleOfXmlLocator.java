package AndroidDetector;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SampleOfXmlLocator extends DefaultHandler {
    private Locator locator;

    public void setDocumentLocator(Locator locator) {
        this.locator = locator;
    }

    public void startElement(String uri, String localName, String qName, Attributes attrs)
            throws SAXException {
        if (qName.equals("order")) {
            System.out.println("here process element start");
        } else {
            String location = "";
            if (locator != null) {

                location = locator.getSystemId(); // XML-document name;
                location += " line " + locator.getLineNumber();
                location += ", column " + locator.getColumnNumber();
                location += ": ";
            }
            throw new SAXException(location + "Illegal element");
        }
    }
}