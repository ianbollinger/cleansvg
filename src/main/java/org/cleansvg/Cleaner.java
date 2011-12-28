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

/**
 * @author ian.bollinger@gmail.com (Ian D. Bollinger)
 */
class Cleaner {
    // TODO: export all this junk to file
    private static final Set<String> JUNK_NAMESPACES = ImmutableSet.of(
            "http://web.resource.org/cc/", "http://purl.org/dc/elements/1.1/",
            "http://www.w3.org/1999/02/22-rdf-syntax-ns#",
            "http://www.inkscape.org/namespaces/inkscape",
            "http://sodipodi.sourceforge.net/DTD/sodipodi-0.dtd");
    private static final Set<String> REMOVABLE_EMPTY_TAGS = ImmutableSet.of(
            "defs", "g", "metadata");
    private final Map<String, String> defaults;
    private SVGDocument document;

    @Inject
    Cleaner(@Defaults final Map<String, String> defaults) {
        this.defaults = defaults;
    }

    public void process(final Document document) {
        // TODO: this should be injected.
        this.document = new SVGDocument(document);
        removeJunk();
        firstPass();
        pushDownAttributes();
        removeEmptyGroups();
        makeGroupsPass();
        removeEmptyGroups();
    }

    // TODO: rename!
    private void removeJunk() {
        for (final Element element : document.getElements()) {
            if (isRemovable(element)) {
                document.deleteNode(element);
            }
        }
    }

    private boolean isRemovable(final Attr attr) {
        return isJunkNamespace(attr) || "id".equals(attr.getName())
                || attr.getValue().equals(defaults.get(attr.getName()));
    }

    private void firstPass() {
        for (final Node node : document.getNodes()) {
            processNode(node);
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
            document.deleteNode(node);
        }
    }

    private void processElement(final Element element) {
        if (isJunkNamespace(element)) {
            document.deleteNode(element);
        } else {
            convertStyleAttributes(element);
            convertColorAttributes(element);
            compactPathAttribute(element);
            processAttributes(element);
        }
    }

    private void pushDownAttributes() {
        for (final Element element : document.getElements()) {
            if (document.isGroup(element)) {
                final NodeList children = document.getAllChildElements(element);
                if (children.getLength() == 1) {
                    final Element child = (Element) children.item(0);
                    if (document.getAllChildElements(child).getLength() > 0) {
                        continue;
                    }
                    element.getParentNode().insertBefore(child, element);
                    document.pushDownAttributes(element, child);
                    document.deleteNode(element);
                }
            }
        }
    }

    private void removeEmptyGroups() {
        for (final Element element : document.getElements()) {
            if (document.isGroup(element) && !element.hasAttributes()) {
                document.pushUpAttributes(element);
                document.deleteNode(element);
            }
        }
    }

    private void makeGroupsPass() {
        for (final Element element : document.getElements()) {
            makeGroups(element);
        }
    }

    private void processText(final Text text) {
        if (text.getTextContent().isEmpty()) {
            document.deleteNode(text);
        }
    }

    private void processAttributes(final Element element) {
        for (final Attr attr : ImmutableList.copyOf(
                document.getAttributes(element))) {
            if (isRemovable(attr)) {
                element.removeAttributeNode(attr);
            }
        }
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

    private void convertColorAttributes(final Element element) {
        // if (!element.hasAttribute("fill")) {
        //    return;
        // }
    }

    // TODO: This method is too complicated.
    private void makeGroups(final Element element) {
        if ("text".equals(element.getTagName())) {
            return;
        }
        final List<Element> children = document.getDirectChildElements(element);
        final int size = children.size();
        if (size <= 1) {
            return;
        }
        Element a = children.get(0);
        for (final Element b : children.subList(1, size)) {
            final Map<String, String> attrs = document.getAttributeMap(a);
            final Map<String, String> attrs1 = document.getAttributeMap(a);
            final Map<String, String> attrs2 = document.getAttributeMap(b);
            attrs.entrySet().retainAll(attrs2.entrySet());
            attrs.remove("d");
            if (attrs.isEmpty()) {
                a = b;
                continue;
            }
            if (document.isGroup(a) && attrs1.equals(attrs2)) {
                document.removeAttributes(b, attrs2);
                a.appendChild(b);
            } else {
                final Element group = document.createGroup();
                document.setAttributes(group, attrs);
                element.insertBefore(group, a);
                document.appendChildren(group, a, b);
                document.removeAttributes(a, attrs);
                document.removeAttributes(b, attrs);
                a = group;
            }
        }
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
}
