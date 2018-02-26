package ch.awae.utils.source;

import java.io.InputStream;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilderFactory;

import org.xml.sax.InputSource;

import ch.awae.utils.functional.Result;
import ch.awae.utils.xml.XML;

/**
 * Source Data interaction Helper
 * 
 * Designed to enable interaction with different data sources as simple
 * one-liners
 * 
 * @author Andreas Wälchli
 * @version 1.1, 2015-05-11
 */
public abstract class Source {

    /**
     * Returns a Source based of the given file name. <br>
     * This method does not validate the given file name. It is implicitly
     * validated as soon as it is interacted with. This allows for clearer
     * Source handling.
     * 
     * @param file
     *            the file name
     * @return a source based off the given file
     */
    public static Source fromFile(String file) {
        return new FileSource(file);
    }

    /**
     * Returns a Source based of the given input stream <br>
     * This method does not validate the given stream. It is implicitly
     * validated as soon as it is interacted with. This allows for clearer
     * Source handling.
     * 
     * @param stream
     *            the input stream
     * @return a source based off the given stream
     */
    public static Source fromStream(InputStream stream) {
        return new StreamSource(stream);
    }

    /**
     * Returns a Source based of the given URL. <br>
     * This method does not validate the given URL. It is implicitly validated
     * as soon as it is interacted with. This allows for clearer Source
     * handling.
     * 
     * @param url
     *            the source url
     * @return a source based off the given url
     */
    public static Source fromURL(String url) {
        return new URLSource(url);
    }

    /**
     * Returns the Source as an input stream
     * 
     * @return a Result holding the stream or any potential exception
     */
    public abstract Result<InputStream> mkStream();

    /**
     * reads the source into a string
     * 
     * @return a Result holding the string or any potential exception
     */
    public Result<String> mkString() {
        return this.mkStream().map((stream) -> {
            StringBuilder sb = new StringBuilder();
            int character;
            while ((character = stream.read()) >= 0)
                sb.append((char) character);
            return sb.toString();

        });
    }

    /**
     * reads the source into an XML document
     * 
     * @return a result holding the XML
     */
    public Result<XML> mkXML() {
        return this.mkString().map(StringReader::new).map(InputSource::new)
                .map(reader -> DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(reader)).map(XML::new);

    }
}
