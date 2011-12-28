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
import org.w3c.dom.Node;

class ChildNodeIterable implements Iterable<Node> {
    private final Node node;

    @Inject
    ChildNodeIterable(final Node node) {
        this.node = node;
    }

    @Override
    public Iterator<Node> iterator() {
        return new ChildNodeIterator(node.getFirstChild());
    }

    static class ChildNodeIterator extends AbstractIterator<Node> {
        private Node nextNode;

        ChildNodeIterator(final Node node) {
            nextNode = node;
        }

        @Override
        public Node computeNext() {
            if (nextNode != null) {
                final Node result = nextNode;
                nextNode = nextNode.getNextSibling();
                return result;
            }
            return endOfData();
        }
    }
}
