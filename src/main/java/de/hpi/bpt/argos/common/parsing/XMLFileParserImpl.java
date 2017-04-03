package de.hpi.bpt.argos.common.parsing;

import de.hpi.bpt.argos.persistence.database.PersistenceEntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * {@inheritDoc}
 * This is a abstract base implementation.
 */
public abstract class XMLFileParserImpl extends DefaultHandler implements XMLFileParser {
	protected static Logger logger = LoggerFactory.getLogger(XMLFileParserImpl.class);

	protected static SAXParserFactory parserFactory = SAXParserFactory.newInstance();
	protected SAXParser parser;
	protected PersistenceEntityManager entityManager;
	protected List<String> openedElements;
	protected List<String> closedElements;

	/**
	 * This constructor initializes all members with default value.
	 */
	public XMLFileParserImpl() {
		openedElements = new ArrayList<>();
		closedElements = new ArrayList<>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setup(PersistenceEntityManager entityManager) {
		try {
			parser = parserFactory.newSAXParser();
		} catch (Exception e) {
			logger.error("can not setup XML file parser");
			logger.trace("Reason: ", e);
		}
		this.entityManager = entityManager;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void parse(File dataFile) {
		if (parser == null || entityManager == null) {
			logger.error(String.format("can not parse XML file '%1$s', parser is not initialized", dataFile.getName()));
			return;
		}

		try (InputStream inputStream = new FileInputStream(dataFile)) {

			Reader reader = new InputStreamReader(inputStream, "UTF-8");
			InputSource source = new InputSource(reader);
			source.setEncoding("UTF-8");

			parser.parse(source, this);
		} catch (Exception e) {
			logger.error(String.format("can not parse file '%1$s'", dataFile.getName()));
			logger.trace("Reason: ", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);

		openedElements.add(qName);

		startElement(qName);
	}

	/**
	 * This method gets called when a new element in the XML stream was started.
	 * @param elementName - the name of the new element
	 */
	protected void startElement(String elementName) {
		// empty, because this is meant to be overwritten if needed
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		super.endElement(uri, localName, qName);

		if (!latestOpenedElement(0).equals(qName)) {
			throw new SAXException();
		}

		openedElements.remove(openedElements.size() - 1);
		closedElements.add(qName);

		endElement(qName);
	}

	/**
	 * This method gets called when an element is closed in the XML stream.
	 * @param elementName - the name of the closed element
	 */
	protected void endElement(String elementName) {
		// empty, because this is meant to be overwritten if needed
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		super.characters(ch, start, length);

		element(new String(ch, start, length));
	}

	/**
	 * This method gets called when an element contains data.
	 * @param elementData - the data within the element
	 */
	protected abstract void element(String elementData);

	/**
	 * This method return the latest opened element name.
	 * @param topOffset - the element offset (0 -> latest, 1 -> one before latest, ...)
	 * @return - the element name
	 */
	protected String latestOpenedElement(int topOffset) {
		int offset = Math.max(1, Math.min(openedElements.size(), topOffset));

		return openedElements.get(openedElements.size() - offset);
	}

	/**
	 * This method returns the latest closed element name.
	 * @param topOffset - the element offset (0 -> latest, 1 -> one before latest, ...)
	 * @return - the element name
	 */
	protected String latestClosedElement(int topOffset) {
		int offset = Math.max(1, Math.min(closedElements.size(), topOffset));

		return closedElements.get(closedElements.size() - offset);
	}
}
