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

import java.util.Iterator;
import com.google.common.collect.AbstractIterator;
import com.google.inject.Inject;
import org.w3c.dom.Document;
import org.w3c.dom.traversal.*;
import org.w3c.dom.Node;

class FilteredNodeIterable implements Iterable<Node> {
    private final NodeIterator iterator;

    @Inject
    FilteredNodeIterable(final Document document, final int filter) {
        final DocumentTraversal traversable = (DocumentTraversal) document;
        // TODO: inject.
        iterator = traversable.createNodeIterator(document.getDocumentElement(),
                filter, null, true);
    }

    @Override
    public Iterator<Node> iterator() {
        return new FilteredNodeIterator(iterator, iterator.nextNode());
    }

    static class FilteredNodeIterator extends AbstractIterator<Node> {
        private final NodeIterator iterator;
        private Node node;

        @Inject
        FilteredNodeIterator(final NodeIterator iterator, final Node node) {
            this.iterator = iterator;
            this.node = node;
        }

        @Override
        public Node computeNext() {
            if (node != null) {
                final Node result = node;
                node = iterator.nextNode();
                return result;
            }
            iterator.detach();
            return endOfData();
        }
    }
}
