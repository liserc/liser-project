package com.liser.oauth2.web.exceptions;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.util.Map;

/**
 * @author LISER
 * @date 2019/2/26
 */
public class Oauth2AuthorizationExceptionJacksonSerializer extends StdSerializer<Oauth2AuthorizationException> {

    protected Oauth2AuthorizationExceptionJacksonSerializer() {
        super(Oauth2AuthorizationException.class);
    }

    @Override
    public void serialize(Oauth2AuthorizationException value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        jsonGenerator.writeStartObject();

//        jsonGenerator.writeObjectField("timestamp", DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
//
//        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
//        if (attributes != null) {
//            HttpServletRequest request = ((ServletRequestAttributes) attributes).getRequest();
//            jsonGenerator.writeObjectField("path", request.getServletPath());
//        }
//        jsonGenerator.writeObjectField("status", value.getHttpErrorCode());
//        jsonGenerator.writeObjectField("error", value.getOAuth2ErrorCode());
//
//        String errorMessage = value.getMessage();
//
//        if (errorMessage != null) {
//            errorMessage = HtmlUtils.htmlEscape(errorMessage, "UTF-8");
//        }

        jsonGenerator.writeNumberField("code", value.getErrorCode());
        jsonGenerator.writeStringField("message", value.getErrorMessage());

        if (value.getAdditionalInformation() != null) {
            for (Map.Entry<String, String> entry : value.getAdditionalInformation().entrySet()) {
                String key = entry.getKey();
                String add = entry.getValue();
                jsonGenerator.writeStringField(key, add);
            }
        }

        jsonGenerator.writeEndObject();
    }
}
