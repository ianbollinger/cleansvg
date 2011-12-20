/*
 * Copyright 2011 Ian D. Bollinger
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.cleansvg;

import java.util.*;
import com.google.common.collect.*;
import com.google.inject.Inject;
import org.cleansvg.SVGModule.Defaults;
import org.w3c.dom.*;
import org.w3c.dom.traversal.NodeFilter;

/**
 * @author ian.bollinger@gmail.com (Ian D. Bollinger)
 */
class Cleaner {
    // TODO: export all this junk to file

    // private final static String PREFIX_CC = "cc";
    // private final static String PREFIX_DC = "dc";
    // private final static String PREFIX_RDF = "rdf";
    // private final static String PREFIX_INKSCAPE = "inkscape";
    // private final static String PREFIX_SODIPODI = "sodipodi";

    private static final Set<String> JUNK_NAMESPACES = ImmutableSet.of(
            "http://web.resource.org/cc/", "http://purl.org/dc/elements/1.1/",
            "http://www.w3.org/1999/02/22-rdf-syntax-ns#",
            "http://www.inkscape.org/namespaces/inkscape",
            "http://sodipodi.sourceforge.net/DTD/sodipodi-0.dtd");
    private static final Set<String> REMOVABLE_EMPTY_TAGS = ImmutableSet.of(
            "defs", "g", "metadata");

    private final Map<String, String> defaults;
    private Document document;

    @Inject
    Cleaner(@Defaults final Map<String, String> defaults) {
        this.defaults = defaults;
    }

    public void process(final Document document) {
        // TODO: this should be injected.
        this.document = document;
        removeJunk();
        firstPass();
        pushDownAttributes();
        removeEmptyGroups();
        makeGroupsPass();
        removeEmptyGroups();
    }

    public Iterable<Node> nodeIterable(final int filter) {
        return new FilteredNodeIterable(document, filter);
    }

    private void firstPass() {
        for (final Node node : nodeIterable(NodeFilter.SHOW_COMMENT
                | NodeFilter.SHOW_DOCUMENT_TYPE | NodeFilter.SHOW_ELEMENT
                | NodeFilter.SHOW_TEXT)) {
            processNode(node);
        }
    }

    private void removeEmptyGroups() {
        for (final Node node : nodeIterable(NodeFilter.SHOW_ELEMENT)) {
            final Element element = (Element) node;
            if (isGroup(element) && !element.hasAttributes()) {
                final Node parent = element.getParentNode();
                for (final Node child : new ChildNodeIteratable(element)) {
                    parent.appendChild(child);
                }
                deleteNode(element);
            }
        }
    }

    private void pushDownAttributes() {
        for (final Node node : nodeIterable(NodeFilter.SHOW_ELEMENT)) {
            final Element element = (Element) node;
            if (isGroup(element)) {
                final NodeList children = element.getElementsByTagName("*");
                if (children.getLength() == 1) {
                    Element child = (Element) children.item(0);
                    if (child.getElementsByTagName("*").getLength() > 0) {
                        continue;
                    }
                    element.getParentNode().insertBefore(child, element);
                    for (final Attr n : new AttrIterable(element.getAttributes())) {
                        child.setAttributeNode(n);
                    }
                    deleteNode(element);
                }
            }
        }
    }

    private void removeJunk() {
        for (final Node node : nodeIterable(NodeFilter.SHOW_ELEMENT)) {
            if (isRemovable((Element) node)) {
                deleteNode(node);
            }
        }
    }

    private void makeGroupsPass() {
        for (final Node node : nodeIterable(NodeFilter.SHOW_ELEMENT)) {
            makeGroups((Element) node);
        }
    }

    private void processNode(final Node node) {
        switch (node.getNodeType()) {
        case Node.ELEMENT_NODE:
            processElement((Element) node);
            return;
        case Node.TEXT_NODE:
            processText((Text) node);
            return;
        default:
            deleteNode(node);
        }
    }

    private void processElement(final Element element) {
        if (isJunkNamespace(element)) {
            deleteNode(element);
        } else {
            convertStyleAttributes(element);
            compactPathAttribute(element);
            processAttributes(element);
        }
    }

    private void processText(final Text text) {
        if (text.getTextContent().isEmpty()) {
            deleteNode(text);
        }
    }

    private void processAttributes(final Element element) {
        final List<Attr> attrs = ImmutableList
                .copyOf(new AttrIterable(element.getAttributes()));
        for (final Attr attr : attrs) {
            if (isRemovable(attr)) {
                element.removeAttributeNode(attr);
            }
        }
    }

    private void deleteNode(final Node node) {
        node.getParentNode().removeChild(node);
    }

    private void compactPathAttribute(final Element element) {
        if (!element.hasAttribute("d")) {
            return;
        }
        element.setAttribute("d",
                Paths.compactAndRelativize(element.getAttribute("d"), 2));
    }

    private void convertStyleAttributes(final Element element) {
        if (!element.hasAttribute("style")) {
            return;
        }
        for (final String property : element.getAttribute("style").split(";")) {
            if (!property.isEmpty()) {
                final String[] nameValue = property.split(":");
                element.setAttribute(nameValue[0].trim(), nameValue[1].trim());
            }
        }
        element.removeAttribute("style");
    }

    private void makeGroups(final Element element) {
        if ("text".equals(element.getTagName())) {
            return;
        }
        final List<Element> children = Lists.newArrayList();
        for (final Node child : new ChildNodeIteratable(element)) {
            if (child instanceof Element) {
                children.add((Element) child);
            }
        }
        final int size = children.size();
        if (size <= 1) {
            return;
        }
        Element a = children.get(0);
        for (int i = 1; i < size; ++i) {
            final Element b = children.get(i);
            final Map<String, String> attrs = new NamedNodeMapAdapter(a);
            final Map<String, String> attrs1 = new NamedNodeMapAdapter(a);
            final Map<String, String> attrs2 = new NamedNodeMapAdapter(b);
            attrs.entrySet().retainAll(attrs2.entrySet());
            attrs.remove("d");
            if (attrs.isEmpty()) {
                a = b;
                continue;
            }
            if (isGroup(a) && attrs1.equals(attrs2)) {
                for (final String attr : attrs2.keySet()) {
                    b.removeAttribute(attr);
                }
                a.appendChild(b);
            } else {
                final Element group = document.createElement("g");
                for (final Map.Entry<String, String> entry : attrs.entrySet()) {
                    group.setAttribute(entry.getKey(), entry.getValue());
                }
                element.insertBefore(group, a);
                group.appendChild(a);
                group.appendChild(b);
                for (final String attr : attrs.keySet()) {
                    a.removeAttribute(attr);
                    b.removeAttribute(attr);
                }
                a = group;
            }
        }
    }

    private boolean isRemovable(final Attr attr) {
        return isJunkNamespace(attr) || "id".equals(attr.getName())
                || attr.getValue().equals(defaults.get(attr.getName()));
    }

    private boolean isRemovable(final Element element) {
        // TODO: this isn't quite right. Empty elements with attributes don't
        // count.
        return !element.hasChildNodes()
                && REMOVABLE_EMPTY_TAGS.contains(element.getTagName());
    }

    private boolean isJunkNamespace(final Node node) {
        final String uri = node.getNamespaceURI();
        // String prefix = node.getPrefix();
        return JUNK_NAMESPACES.contains(uri);
    }

    private boolean isGroup(final Element element) {
        return "g".equals(element.getTagName());
    }
}
