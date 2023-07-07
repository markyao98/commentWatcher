package MicroRpc.framework.tools.IO;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

public class XmlReader {
    private final static String READ_PATH=Thread.currentThread().getContextClassLoader().getResource("dubbo-application.xml").getPath();
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        test();
    }

    public static Document initRead() throws ParserConfigurationException, IOException, SAXException {
        File file=new File(READ_PATH);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document document = db.parse(file);
        document.getDocumentElement().normalize();

        return document;
    }

    private static void test() throws ParserConfigurationException, SAXException, IOException {
        File file=new File(READ_PATH);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document document = db.parse(file);
        document.getDocumentElement().normalize();
        System.out.println("Root Element :" + document.getDocumentElement().getNodeName());
        NodeList nList = document.getElementsByTagName(DubboRegistryXmlBuilder.TAG_APPLICATION);
        System.out.println("----------------------------");
        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node nNode = nList.item(temp);
            System.out.println("\nCurrent Element :" + nNode.getNodeName());
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                System.out.println("Name : " + eElement.getElementsByTagName("name").item(0).getTextContent());
                System.out.println("host  : " + eElement.getElementsByTagName("host").item(0).getTextContent());
                System.out.println("port : " + eElement.getElementsByTagName("port").item(0).getTextContent());
                NodeList weight = eElement.getElementsByTagName("weight");
                if (weight.getLength()==0)
                    continue;
                System.out.println("weight : " + weight.item(0).getTextContent());
            }
        }

        NodeList serviceProvider = document.getElementsByTagName("serviceProvider");
        System.out.println("----------------------------");
        for (int temp = 0; temp < serviceProvider.getLength(); temp++) {
            Node nNode = serviceProvider.item(temp);
            System.out.println("\nCurrent Element :" + nNode.getNodeName());
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
//                System.out.println("serviceProvider id : " + eElement.getAttribute("id"));
                System.out.println("interfaceName : " + eElement.getElementsByTagName("interfaceName").item(0).getTextContent());
                System.out.println("implClass  : " + eElement.getElementsByTagName("implClass").item(0).getTextContent());
            }
        }
    }
}
