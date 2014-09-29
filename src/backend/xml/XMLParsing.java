package backend.xml;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author CS308 Team16
 * Based on code from http://javafxportal.blogspot.com/2012/03/how-to-read-xml-file-in-java-dom-parser.html.
 */
public class XMLParsing {

	private InitialGameParameters igp;
	
	/**
	 * Uses Java's Document parsing tool to take an input XML file
	 * and extract the values from specific tags.
	 * @param file 
	 * 		the input File object that the user has selected with initial parameters
	 * @return
	 * 		returns an instance of a class that packages the data storing
	 * 		the initial state of the simulation
	 */
	public InitialGameParameters getInitialGameParametersFromXMLFile(File file) throws ParserConfigurationException, SAXException, IOException
	{	
		Document doc = createDocumentFromFile(file);
		igp = new InitialGameParameters();

		parseSimulationWorldData(doc);
		NodeList cellList = doc.getElementsByTagName("cell");
		parseInitialCells(cellList);
		
		return igp;
	}
	
	/**
	 * Sets up the Document parsing tools given the input file that the user specifies for initial configuration
	 * @param file
	 * 		Input file from user
	 * @return
	 * 		Document object constructed from the file
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	private Document createDocumentFromFile(File file) throws ParserConfigurationException, SAXException, IOException
	{
		File fXmlFile = file;
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);
		doc.getDocumentElement().normalize();
		return doc;
	}

	/**
	 * Parse the general parameters such as mode, grid size, etc. from the document
	 * @param doc
	 */
	private void parseSimulationWorldData(Document doc)
	{
		igp.simulationMode = getTagValueFromDoc(doc, "simulationMode");
		igp.unitShape = getTagValueFromDoc(doc, "unitShape");
		igp.edgeType = getTagValueFromDoc(doc, "edgeType");
		igp.gridXSize = Integer.parseInt(getTagValueFromDoc(doc, "gridXSize"));
		igp.gridYSize = Integer.parseInt(getTagValueFromDoc(doc, "gridYSize"));
		igp.thresholdValue = Double.parseDouble(getTagValueFromDoc(doc, "thresholdValue"));
	}
	
	/**
	 * Iterate through all instances of cell elements and parse the data for that cell
	 * @param cellNodeList
	 */
	private void parseInitialCells(NodeList cellNodeList)
	{
		List<InitialCell> initialCells = new ArrayList<InitialCell>();
		
		for (int nodeIndex = 0; nodeIndex < cellNodeList.getLength(); nodeIndex++)
		{
			Node cellNode = cellNodeList.item(nodeIndex);
			if (cellNode.getNodeType() == Node.ELEMENT_NODE)
			{
				Element eElement = (Element) cellNode;
				initialCells.add(createInitialCell(eElement));
			}
		}		
		
		igp.initialCells = initialCells;
	}
	
	/**
	 * 
	 * @param eElement
	 * 		One particular cell element in the document.
	 * @return
	 * 		An InitialCell object that has the parameters for that particular cell element.
	 */
	private InitialCell createInitialCell(Element eElement)
	{
		InitialCell initialCell = new InitialCell();
		initialCell.myState = getTagValueFromElement(eElement, "state");
		initialCell.myX = Integer.parseInt(getTagValueFromElement(eElement, "x"));
		initialCell.myY = Integer.parseInt(getTagValueFromElement(eElement, "y"));
		return initialCell;
	}

	/**
	 * Extract value by looking for a tag in a larger document.
	 * @param doc
	 * 		The document object with the data to look through.
	 * @param tagName
	 * 		The particular tag to match and get the value from.
	 * @return
	 * 		The value associated with the tag.
	 */
	private String getTagValueFromDoc(Document doc, String tagName)
	{
		Node node = doc.getElementsByTagName(tagName).item(0);
		if(node == null)
			return null;
		return node.getChildNodes().item(0).getNodeValue().replaceAll("\\s", "");
	}
	
	/**
	 * Extract value by looking for a tag in a specific element in the document.
	 * @param eElement
	 * 		The element object with the data to look through.
	 * @param tagName
	 * 		The particular tag to match and get the value from.
	 * @return
	 * 		The value associated with the tag.
	 */
	private String getTagValueFromElement(Element eElement, String tagName) {
		return eElement.getElementsByTagName(tagName).item(0).getChildNodes().item(0).getNodeValue().replaceAll("\\s", "");
	}
}