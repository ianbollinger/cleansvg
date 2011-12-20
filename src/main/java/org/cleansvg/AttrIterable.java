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
import org.w3c.dom.*;

class AttrIterable implements Iterable<Attr> {
    private final NamedNodeMap nodes;

    @Inject
    AttrIterable(final NamedNodeMap nodes) {
        this.nodes = nodes;
    }

    @Override
    public Iterator<Attr> iterator() {
        return new AttrIterator(nodes);
    }

    static class AttrIterator extends AbstractIterator<Attr> {
        private final NamedNodeMap nodes;
        private int index;

        @Inject
        AttrIterator(final NamedNodeMap nodes) {
            this.nodes = nodes;
            this.index = 0;
        }

        @Override
        protected Attr computeNext() {
            if (index < nodes.getLength()) {
                return (Attr) nodes.item(index++);
            }
            return endOfData();
        }
    }
}
