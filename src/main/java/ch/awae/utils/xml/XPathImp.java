/**
 * 
 */
package ch.awae.utils.xml;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ch.awae.utils.functional.Result;
import ch.awae.utils.functional.T2;

/**
 * @author Andreas Wälchli
 * @version 1.1, May 11, 2015
 *
 */
class XPathImp implements XPath {

    public XPathImp(Stream<Element> elements) {
        this.elements = elements;
    }

    private final Stream<Element> elements;

    @Override
    public XPath any() {
        return new XPathImp(this.elements.map(XPathImp::recurseThroughTree).flatMap(List::stream));
    }

    private static List<Element> recurseThroughTree(Element e) {
        List<Element> accum = new ArrayList<>();
        accum.add(e);
        NodeList children = e.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            if (children.item(i) instanceof Element)
                accum.addAll(recurseThroughTree((Element) children.item(i)));
        }
        return accum;
    }

    @Override
    public XPath children() {
        return new XPathImp(this.elements.flatMap(e -> {
            List<Element> accum = new ArrayList<>();
            NodeList children = e.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                if (children.item(i) instanceof Element)
                    accum.add((Element) children.item(i));
            }
            return accum.stream();
        }));
    }

    @Override
    public XPath filterName(Predicate<String> predicate) {
        return new XPathImp(this.elements.filter(n -> predicate.test(n.getNodeName())));
    }

    @Override
    public XPath filterAttribute(String name, Predicate<String> predicate) {
        return new XPathImp(
                this.elements.filter(e -> Optional.ofNullable(e.getAttribute(name)).filter(predicate).isPresent()));
    }

    @Override
    public XPath indexRange(int from, int amount) {
        return new XPathImp(this.elements.sequential().skip(from).limit(amount));
    }

    @Override
    public Stream<Result<String>> text() {
        return this.elements.map(e -> Result.ofNullable(e.getTextContent()));
    }

    @Override
    public Stream<Result<String>> attribute(String name) {
        return this.elements.map(e -> Result.ofNullable(e.getAttribute(name)));
    }

    @Override
    public Stream<List<T2<String, String>>> attributes() {
        return this.elements.map(e -> e.getAttributes()).map(m -> {
            List<T2<String, String>> list = new ArrayList<>();
            for (int i = 0; i < m.getLength(); i++) {
                list.add(new T2<>(m.item(i).getNodeName(), m.item(i).getNodeValue()));
            }
            return list;
        });
    }

    @Override
    public Stream<Node> node() {
        return this.elements.map(e -> e);
    }

    @Override
    public void setText(String text) {
        this.elements.forEach(e -> e.setTextContent(text));
    }

    @Override
    public void setAttribute(String name, String value) {
        if (value == null)
            this.elements.forEach(e -> e.removeAttribute(name));
        else
            this.elements.forEach(e -> e.setAttribute(name, value));
    }

    @Override
    public void dropNode() {
        this.elements.forEach(e -> e.getParentNode().removeChild(e));
    }

    @Override
    public void updateText(UnaryOperator<String> updater) {
        this.elements.forEach(e -> e.setTextContent(updater.apply(e.getTextContent())));
    }

    @Override
    public XPath addNode(String name) {
        return new XPathImp(this.elements.map(e -> {
            Element el = e.getOwnerDocument().createElement(name);
            e.appendChild(el);
            return el;
        }));
    }
}
