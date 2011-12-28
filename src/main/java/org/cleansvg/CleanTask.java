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

import java.io.*;
import java.util.logging.*;
import com.google.common.io.*;
import com.google.inject.*;
import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.transcoder.*;
import org.apache.batik.transcoder.svg2svg.SVGTranscoder;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.Document;

class CleanTask implements Task {
    private final Logger logger;
    private final InputSupplier<? extends Reader> readerSupplier;
    private final String inputUri;
    private final OutputSupplier<? extends Writer> writerSupplier;
    private final Cleaner cleaner;
    private final ExceptionHandler<IOException> exceptionHandler;

    @Inject
    CleanTask(final Logger logger,
            final InputSupplier<? extends Reader> readerSupplier,
            final String inputUri,
            final OutputSupplier<? extends Writer> writerSupplier,
            final Cleaner cleaner,
            final ExceptionHandler<IOException> exceptionHandler) {
        this.logger = logger;
        this.cleaner = cleaner;
        this.readerSupplier = readerSupplier;
        this.inputUri = inputUri;
        this.writerSupplier = writerSupplier;
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    public void run() {
        try {
            final Document document = input();
            cleaner.process(document);
            output(document);
        } catch (final TranscoderException e) {
            logger.log(Level.SEVERE, "Transcoding failed.", e);
        } catch (final IOException e) {
            logger.log(Level.SEVERE, "Unable to read document from file.", e);
            exceptionHandler.handle(e);
        }
    }

    private Document input() throws IOException {
        final Reader reader = readerSupplier.getInput();
        boolean thrown = true;
        try {
            final Document document = getDocument(reader);
            thrown = false;
            return document;
        } finally {
            Closeables.close(reader, thrown);
        }
    }

    private Document getDocument(final Reader reader) throws IOException {
        final String parser = XMLResourceDescriptor.getXMLParserClassName();
        final SAXSVGDocumentFactory factory = new SAXSVGDocumentFactory(parser);
        return factory.createDocument(inputUri, reader);
    }

    private void output(final Document document)
            throws IOException, TranscoderException {
        final Writer writer = writerSupplier.getOutput();
        boolean thrown = true;
        try {
            transcode(document, writer);
            thrown = false;
        } finally {
            Closeables.close(writer, thrown);
        }
    }

    private void transcode(final Document document,
            final Writer writer) throws TranscoderException {
        final Transcoder transcoder = new SVGTranscoder();
        final TranscoderInput input = new TranscoderInput(document);
        final TranscoderOutput output = new TranscoderOutput(writer);
        transcoder.addTranscodingHint(SVGTranscoder.KEY_TABULATION_WIDTH, 0);
        transcoder.addTranscodingHint(SVGTranscoder.KEY_DOCUMENT_WIDTH,
                Integer.MAX_VALUE);
        // transcoder.addTranscodingHint(SVGTranscoder.KEY_FORMAT,
        // SVGTranscoder.VALUE_FORMAT_OFF);
        transcoder.transcode(input, output);
    }
}
