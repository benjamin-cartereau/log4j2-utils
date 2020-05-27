package fr.bcarter.log4j.jackson;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.jackson.JsonConstants;
import org.apache.logging.log4j.core.jackson.LogEventJacksonJsonMixIn;
import org.apache.logging.log4j.message.Message;

/**
 *
 */
@JsonSerialize(as = LogEvent.class)
public abstract class MultiformatMessageJacksonJsonMixIn extends LogEventJacksonJsonMixIn {
    
    @JsonProperty(JsonConstants.ELT_MESSAGE)
    @JsonSerialize(using = JacksonJsonMultiformatMessageSerializer.class)
    @Override
    public abstract Message getMessage();
}