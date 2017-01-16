/*
 * Copyright (c) 2013-2017 Atlanmod INRIA LINA Mines Nantes.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Atlanmod INRIA LINA Mines Nantes - initial API and implementation
 */

package fr.inria.atlanmod.neoemf.io.reader;

import fr.inria.atlanmod.neoemf.io.persistence.PersistenceNotifier;
import fr.inria.atlanmod.neoemf.io.processor.EcoreProcessor;
import fr.inria.atlanmod.neoemf.io.processor.Processor;
import fr.inria.atlanmod.neoemf.io.processor.XPathProcessor;
import fr.inria.atlanmod.neoemf.util.logging.NeoLogger;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Objects.nonNull;

/**
 * A {@link Reader} that uses streams for reading and parsing XMI files.
 */
public class XmiStreamReader extends AbstractXmiReader {

    /**
     * Logs the progress of the current reading.
     *
     * @param percent the percentage of data read on the total size of the data
     */
    private static void logProgress(double percent) {
        NeoLogger.debug("Progress : {0}", String.format("%5s", String.format("%,.0f %%", percent)));
    }

    @Override
    public Processor defaultProcessor() {
        Processor defaultProcessor;

        defaultProcessor = new PersistenceNotifier();
        defaultProcessor = new XPathProcessor(defaultProcessor);
        defaultProcessor = new EcoreProcessor(defaultProcessor);

        return defaultProcessor;
    }

    @Override
    public void read(InputStream stream) throws IOException {
        if (!hasHandler()) {
            throw new IllegalStateException("This notifier hasn't any handler");
        }

        checkNotNull(stream);

        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setValidating(false);

        Timer logProgressTimer = new Timer(true);
        logProgressTimer.schedule(new LogProgressTimer(stream), 10000, 30000);

        try {
            factory.newSAXParser().parse(stream, new XmiSaxHandler());
        }
        catch (SAXException e) {
            if (nonNull(e.getCause())) {
                throw new IOException(e.getCause());
            }
            else {
                throw new IOException(e);
            }
        }
        catch (ParserConfigurationException e) {
            throw new IOException(e);
        }
        finally {
            logProgressTimer.cancel();
        }
    }

    /**
     * A {@link TimerTask} that logs progress.
     */
    private static class LogProgressTimer extends TimerTask {

        /**
         * The stream to watch.
         */
        private final InputStream stream;

        /**
         * The total size of the stream.
         */
        private final long total;

        /**
         * Constructs a new {@code LogProgressTimer}.
         *
         * @param stream the stream to watch
         *
         * @throws IOException if an I/O error occurs
         */
        private LogProgressTimer(InputStream stream) throws IOException {
            this.stream = stream;
            this.total = stream.available();
        }

        @Override
        public void run() {
            try {
                logProgress((double) (total - stream.available()) / (double) total * 100d);
            }
            catch (Exception ignore) {
            }
        }
    }

    /**
     * The real implementation of the XMI parser.
     */
    private class XmiSaxHandler extends DefaultHandler {

        /**
         * The URI used to declare an XMI element.
         *
         * @see #XMI_NS
         */
        private String xmiUri;

        @Override
        public void startDocument() throws SAXException {
            try {
                processStartDocument();
            }
            catch (Exception e) {
                throw new SAXException(e);
            }

            logProgress(0);
        }

        @Override
        public void endDocument() throws SAXException {
            logProgress(100);

            try {
                processEndDocument();
            }
            catch (Exception e) {
                throw new SAXException(e);
            }
        }

        @Override
        public void startPrefixMapping(String prefix, String uri) throws SAXException {
            if (Objects.equals(prefix, XMI_NS)) {
                xmiUri = uri;
            }

            processNamespace(prefix, uri);
        }

        @Override
        public void startElement(String uri, String name, String qName, Attributes attributes) throws SAXException
        {
            // Ignore XMI elements
            if (Objects.equals(uri, xmiUri)) {
                return;
            }

            try {
                processStartElement(uri, name, attributes);
            }
            catch (Exception e) {
                throw new SAXException(e);
            }
        }

        @Override
        public void endElement(String uri, String name, String qName) throws SAXException {
            // Ignore XMI elements
            if (Objects.equals(uri, xmiUri)) {
                return;
            }

            try {
                processEndElement(uri, name);
            }
            catch (Exception e) {
                throw new SAXException(e);
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            String characters = String.valueOf(ch, start, length).trim();
            try {
                if (!characters.isEmpty()) {
                    processCharacters(characters);
                }
            }
            catch (Exception e) {
                throw new SAXException(e);
            }
        }
    }
}
