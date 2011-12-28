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
import com.google.common.base.Objects;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import org.w3c.dom.*;

class NamedNodeMapAdapter extends AbstractMap<String, String> {
    private final Element parent;
    private final Set<Map.Entry<String, String>> entries;

    @Inject
    NamedNodeMapAdapter(final Element parent) {
        this.parent = parent;
        // TODO: inject.
        entries = createEntrySet();
    }

    private Set<Map.Entry<String, String>> createEntrySet() {
        final Set<Map.Entry<String, String>> results = Sets.newHashSet();
        for (final Attr attr : new AttrIterable(parent.getAttributes())) {
            results.add(new Entry(attr.getName(), attr.getValue()));
        }
        return results;
    }

    @Override
    public Set<Map.Entry<String, String>> entrySet() {
        return entries;
    }

    static class Entry implements Map.Entry<String, String> {
        private final String key;
        private String value;

        @Inject
        Entry(final String key, final String value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public String getValue() {
            return value;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Entry)) {
                return false;
            }
            final Entry other = (Entry) o;
            return key.equals(other.key) && value.equals(other.value);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(key, value);
        }

        @Override
        public String setValue(final String value) {
            final String result = this.value;
            this.value = value;
            return result;
        }
    }
}
