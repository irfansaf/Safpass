package com.irfansaf.safpass.xml.converter;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class XmlConverter<T> {

    private final Class<T> documentClass;
    private final XmlMapper mapper;

    /**
     *
     * @param documentClass
     */
    public XmlConverter(Class<T> documentClass) {
        this.documentClass = documentClass;
        JacksonXmlModule module = new JacksonXmlModule();
        module.setDefaultUseWrapper(false);
        this.mapper = new XmlMapper(module);
        this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.mapper.enable(ToXmlGenerator.Feature.WRITE_XML_DECLARATION);
    }

    /**
     * Maps the given object to the given output stream.
     *
     * @param document the document object which represents the XML document
     * @param outputStream the output stream
     * @throws IOException if any error occured
     */
    public void write(T document, OutputStream outputStream) throws IOException {
        mapper.writeValue(outputStream, document);
    }

    public T read(InputStream inputStream) throws IOException {
        return mapper.readValue(inputStream, documentClass);
    }
}
