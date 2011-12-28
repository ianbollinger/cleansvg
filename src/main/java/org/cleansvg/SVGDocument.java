package org.cleansvg;

import java.util.*;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import org.w3c.dom.*;
import org.w3c.dom.traversal.NodeFilter;

/**
 * @author ian.bollinger@gmail.com (Ian D. Bollinger)
 */
public class SVGDocument {
    private final Document document;

    @Inject
    SVGDocument(final Document document) {
        this.document = document;
    }

    public void deleteNode(final Node node) {
        node.getParentNode().removeChild(node);
    }

    public Iterable<Node> getNodes() {
        return new FilteredNodeIterable(document, NodeFilter.SHOW_COMMENT
                | NodeFilter.SHOW_DOCUMENT_TYPE | NodeFilter.SHOW_ELEMENT
                | NodeFilter.SHOW_TEXT);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public Iterable<Element> getElements() {
        return (Iterable) new FilteredNodeIterable(document,
                NodeFilter.SHOW_ELEMENT);
    }

    // TODO: get rid of NodeList because it's crap.
    public NodeList getAllChildElements(final Element element) {
        return element.getElementsByTagName("*");
    }

    public Iterable<Node> getDirectChildNodes(final Element element) {
        return new ChildNodeIterable(element);
    }

    // TODO: make immutable.
    public List<Element> getDirectChildElements(final Element element) {
        final List<Element> children = Lists.newArrayList();
        for (final Node child : getDirectChildNodes(element)) {
            if (child instanceof Element) {
                children.add((Element) child);
            }
        }
        return children;
    }

    public Element createGroup() {
        return document.createElement("g");
    }

    public Iterable<Attr> getAttributes(final Element element) {
        return new AttrIterable(element.getAttributes());
    }

    public Map<String, String> getAttributeMap(final Element element) {
        return new NamedNodeMapAdapter(element);
    }

    public void removeAttributes(final Element element,
            final Map<String, String> attributes) {
        for (final String attribute : attributes.keySet()) {
            element.removeAttribute(attribute);
        }
    }

    public void setAttributes(final Element element,
            final Map<String, String> attributes) {
        for (final Map.Entry<String, String> entry : attributes.entrySet()) {
            element.setAttribute(entry.getKey(), entry.getValue());
        }
    }

    public void setAttributes(final Element element,
            final Iterable<Attr> attributes) {
        for (final Attr attribute : attributes) {
            element.setAttributeNode(attribute);
        }
    }

    public void appendChildren(final Node node, final Node... children) {
        for (final Node child : children) {
            node.appendChild(child);
        }
    }

    public void appendChildren(final Node node, final Iterable<Node> children) {
        for (final Node child : children) {
            node.appendChild(child);
        }
    }

    public boolean isGroup(final Element element) {
        return "g".equals(element.getTagName());
    }


    public void pushUpAttributes(final Element element) {
        appendChildren(element.getParentNode(), getDirectChildNodes(element));
    }

    public void pushDownAttributes(final Element element, final Element child) {
        setAttributes(child, getAttributes(element));
    }
}
