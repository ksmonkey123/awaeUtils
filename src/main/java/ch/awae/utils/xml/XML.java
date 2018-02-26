package ch.awae.utils.xml;

import java.util.stream.Stream;

import org.w3c.dom.Document;

/**
 * XML Data Wrapper
 * 
 * This class wraps a DOM Document for nice interaction with other components. <br>
 * 
 * XML instances keep DOM Documents around internally. Therefore any mutation
 * performed by / on an XML instance affects the backing DOM document - and
 * vice-versa.
 * 
 * @author Andreas Wälchli
 * @since awaeUtils 1.0.0
 */
public class XML {

	private final Document document;

	/**
	 * Creates a new XML instance based off a given DOM Document
	 * 
	 * @param document
	 *            the DOM document to base the XML instance off
	 */
	public XML(Document document) {
		this.document = document;
	}

	/**
	 * Provides an XPath with the document root element
	 * 
	 * @return the query root
	 */
	public XPath query() {
		return new XPathImp(Stream.of(this.document.getDocumentElement()));
	}

	/**
	 * Gets the backing DOM Document
	 * 
	 * @return the document
	 */
	public Document getDocument() {
		return this.document;
	}

}
