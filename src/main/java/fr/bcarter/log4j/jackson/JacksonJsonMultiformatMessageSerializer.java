package fr.bcarter.log4j.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;
import java.io.IOException;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.MultiformatMessage;

/**
 * Serialize a message that is a MultiformatMessage as JSON
 */
public class JacksonJsonMultiformatMessageSerializer extends StdScalarSerializer<Message> {

    private static final long serialVersionUID = 1L;

    JacksonJsonMultiformatMessageSerializer() {
        super(Message.class);
    }

    @Override
    public void serialize(final Message value, final JsonGenerator jgen, final SerializerProvider provider) throws IOException {
        if (value instanceof MultiformatMessage) {
            String messageAsJson = ((MultiformatMessage) value).getFormattedMessage(new String[]{"JSON"});
            jgen.writeRawValue(messageAsJson);
        } else {
            jgen.writeRaw(value.getFormattedMessage());
        }
    }

}
