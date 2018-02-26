package ch.awae.utils.xml;

import java.util.List;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import org.w3c.dom.Node;

import ch.awae.utils.functional.Result;
import ch.awae.utils.functional.T2;

/**
 * Pipeline-Style XML processing utility. <br>
 * 
 * An XPath instance holds a list of all XML elements that match the defined
 * "path"
 * 
 * @author Andreas Wälchli
 * @since awaeUtils 1.0.0
 */
public interface XPath {

    /**
     * Recursively returns an XPath containing all current nodes and all their
     * children
     */
    XPath any();

    /**
     * Returns an XPath containing all children of all current nodes
     */
    XPath children();

    /**
     * Returns an XPath containing all children of all current nodes that match
     * the given name
     * 
     * @param name
     */
    default XPath node(String name) {
        return this.children().filterName(name::equals);
    }

    /**
     * Returns an XPath containing all current elements that contain an argument
     * with the given name
     * 
     * @param name
     */
    default XPath filterAttribute(String name) {
        return this.filterAttribute(name, val -> true);
    }

    /**
     * Returns an XPath containing all current elements that have a name
     * validated by a given predicate
     * 
     * @param predicate
     */
    XPath filterName(Predicate<String> predicate);

    /**
     * Returns an XPath containing all current elements that have a given
     * attribute with a given value
     * 
     * @param name
     * @param value
     */
    default XPath filterAttribute(String name, String value) {
        return this.filterAttribute(name, val -> val.equals(value));
    }

    /**
     * Returns an XPath containing all current elements that have a given
     * attribute validated by a given predicate
     * 
     * @param name
     * @param predicate
     */
    XPath filterAttribute(String name, Predicate<String> predicate);

    /**
     * Returns an XPath containing the node with a given index
     * 
     * @param index
     */
    default XPath index(int index) {
        return this.indexRange(index, 1);
    }

    /**
     * 
     * Returns an XPath containing a given number of nodes starting at a given
     * index
     * 
     * @param from
     * @param amount
     */
    XPath indexRange(int from, int amount);

    // ###### EXTRACTORS ######

    /**
     * Returns a stream containing the text of all current elements
     */
    Stream<Result<String>> text();

    /**
     * Returns a stream containing a list of attribute tuples for each elements
     */
    Stream<List<T2<String, String>>> attributes();

    /**
     * Returns a stream containing all DOM nodes of all current elements
     */
    Stream<Node> node();

    /**
     * Returns a stream containing the attribute value for a given attribute
     * name for each element
     */
    Stream<Result<String>> attribute(String name);

    /**
     * Replaces the text content of all current elements by a given String
     * 
     * @param text
     */
    void setText(String text);

    /**
     * Inserts the given attribute into all current elements. If the attribute
     * value is {@code null}, the attribute will be removed.
     * 
     * @param name
     * @param value
     */
    void setAttribute(String name, String value);

    /**
     * Removes all current elements from the XML
     */
    void dropNode();

    /**
     * Replaces the text of each element by the value returned by the updater
     * method. The old text will be passed to the updater as the argument.
     * 
     * @param updater
     */
    void updateText(UnaryOperator<String> updater);

    /**
     * Adds a node with the given name into each of the current nodes and
     * returns an XPath containing all created nodes
     * 
     * @param name
     */
    XPath addNode(String name);

}
