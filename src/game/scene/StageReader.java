package scene;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

public class StageReader {
    Document doc = null;
    private File stageFile = null;
    public StageReader(String path) {
        stageFile = new File(path);
        doc = assembleDoc();
    }
    private Document assembleDoc() {
        Document xml = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING,true);
            xml = dbf.newDocumentBuilder().parse(stageFile);
            xml.getDocumentElement().normalize();
        } catch (Exception ignored) {}
        return xml;
    }
    public int[] getTileData(int x, int y) {
        if (doc == null) return null;
        NodeList l = doc.getElementsByTagName("tiles");
        if (l.item(0) == null) return null;
        else {
            Node n = l.item(0);
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                String v = ((Element) n).getAttribute("T"+x+"-"+y);
                if (v.length() == 0) return null;
                int n1 = Integer.parseInt(v.substring(0,v.indexOf(","))), n2 = Integer.parseInt(v.substring(v.indexOf(",")+1));
                return new int[] {n1,n2};
            }
        }
        return null;
    }

}
