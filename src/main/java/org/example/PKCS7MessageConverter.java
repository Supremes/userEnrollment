package org.example;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class PKCS7MessageConverter extends AbstractHttpMessageConverter<byte[]> {
    public PKCS7MessageConverter() {
        super(new MediaType("application", "pkcs7-signature", StandardCharsets.UTF_8));
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return byte[].class.isAssignableFrom(clazz);
    }

    @Override
    protected byte[] readInternal(Class<? extends byte[]> clazz, HttpInputMessage inputMessage) throws IOException {
        return StreamUtils.copyToByteArray(inputMessage.getBody());
    }

    @Override
    protected void writeInternal(byte[] bytes, HttpOutputMessage outputMessage) throws IOException {
        outputMessage.getBody().write(bytes);
    }
}
