package de.hpi.bpt.argos.parsing;

import de.hpi.bpt.argos.parsing.util.FileUtilImpl;
import de.hpi.bpt.argos.util.LoggerUtilImpl;
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

	private static SAXParserFactory parserFactory = SAXParserFactory.newInstance();
	private SAXParser parser;
	private List<String> openedElements;

	/**
	 * This constructor initializes all members with default value.
	 */
	public XMLFileParserImpl() {
		openedElements = new ArrayList<>();

		try {
			parser = parserFactory.newSAXParser();
		} catch (Exception e) {
			LoggerUtilImpl.getInstance().error(logger, "cannot setup XML file parser", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void parse(File dataFile) {
		if (parser == null) {
			logger.error(String.format("cannot parse XML file '%1$s', parser is not initialized", dataFile.getName()));
			return;
		}

//		if (!FileUtilImpl.getInstance().wasModified(dataFile)) {
//			logger.info(String.format("static data file '%1$s' was skipped, since it was loaded earlier", dataFile.getName()));
//			return;
//		} else {
//			FileUtilImpl.getInstance().modify(dataFile);
//		}

		logger.info(String.format("start parsing '%1$s' ...", dataFile.getName()));

		try (InputStream inputStream = new FileInputStream(dataFile)) {

			Reader reader = new InputStreamReader(inputStream, "UTF-8");
			InputSource source = new InputSource(reader);
			source.setEncoding("UTF-8");

			parser.parse(source, this);
		} catch (Exception e) {
			logger.error(String.format("cannot parse file '%1$s'", dataFile.getName()));
			logger.trace("Reason: ", e);
		}

		logger.info(String.format("... finished parsing '%1$s'", dataFile.getName()));
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
	 * {@inheritDoc}
	 */
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		super.endElement(uri, localName, qName);

		if (!latestOpenedElement(0).equals(qName)) {
			throw new SAXException();
		}

		openedElements.remove(openedElements.size() - 1);

		endElement(qName);
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
	 * {@inheritDoc}
	 */
	@Override
	public String latestOpenedElement(int topOffset) {
		int offset = Math.max(1, Math.min(openedElements.size(), topOffset + 1));

		return openedElements.get(openedElements.size() - offset);
	}

	/**
	 * This method gets called when a new element in the XML stream was started.
	 * @param elementName - the name of the new element
	 */
	protected abstract void startElement(String elementName);

	/**
	 * This method gets called when an element contains data.
	 * @param elementData - the data within the element
	 */
	protected abstract void element(String elementData);

	/**
	 * This method gets called when an element is closed in the XML stream.
	 * @param elementName - the name of the closed element
	 */
	protected abstract void endElement(String elementName);
}
