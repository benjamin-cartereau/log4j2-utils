package fr.bcarter.log4j;

import java.util.List;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.ContextDataInjector;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.impl.ContextDataFactory;
import org.apache.logging.log4j.core.impl.ContextDataInjectorFactory;
import org.apache.logging.log4j.core.impl.Log4jLogEvent;
import org.apache.logging.log4j.core.impl.LogEventFactory;
import org.apache.logging.log4j.core.time.MutableInstant;
import org.apache.logging.log4j.core.util.ClockFactory;
import org.apache.logging.log4j.message.MapMessage;
import org.apache.logging.log4j.message.Message;

/**
 * Log4j2 custom Event Factory that creates a LogEvent by replacing some
 *  properties values of the default event by values set within the message
 *  that implements the MapMessage
 */
public class MapMessageLogEventFactory implements LogEventFactory {

    private final ContextDataInjector injector = ContextDataInjectorFactory.createInjector();

    @Override
    public LogEvent createEvent(String loggerName, Marker marker, String fqcn,
            Level level, Message message,
            List<Property> properties, Throwable t) {
        
        // Let's instantiate a builder...
        Log4jLogEvent.Builder builder = Log4jLogEvent.newBuilder();
        
        // ...and set basic properties
        builder.setLoggerName(loggerName);
        builder.setMarker(marker);
        builder.setLoggerFqcn(fqcn);
        builder.setLevel(level == null ? Level.OFF : level);
        builder.setThrown(t);
        //builder.setSource(source); // NOT SET
        
        // Context data have to be injected through properties first
        builder.setContextData(injector.injectContextData(properties, ContextDataFactory.createContextData()));
        
        // Now let's deal with the custom processing, dealing with MapMessage
        //  having LogEvent properties in it
        if (message instanceof MapMessage) {
            @SuppressWarnings("unchecked")
            MapMessage<?, Object> mapMessage = (MapMessage<?, Object>) message;
            
            // If present, remove and replace the timestamp
            String timestampAsString = mapMessage.remove("timestamp");
            if (timestampAsString != null && timestampAsString.trim().length() > 0) {
                try {
                    builder.setTimeMillis(Long.valueOf(timestampAsString));
                    MutableInstant instant = new MutableInstant();
                    instant.initFrom(ClockFactory.getClock());
                    mapMessage.with("tracedAt", instant.getEpochMillisecond());
                } catch (NumberFormatException nfe) {
                    // Silently fail 
                }
            }

            // If present, remove and replace the thread name
            String threadName = mapMessage.remove("threadName");
            if (threadName != null && threadName.trim().length() > 0) {
                builder.setThreadName(threadName);
            }
            
            //builder.setThreadPriority(threadPriority); // NOT SET

            String threadIdAsString = mapMessage.remove("threadId");
            if (threadIdAsString != null && threadIdAsString.trim().length() > 0) {
                try {
                    builder.setThreadId(Long.valueOf(threadIdAsString));
                } catch (NumberFormatException nfe) {
                    // Silently fail 
                }
            }
            
            // Other data within MapMessage : NOT MODIFIED
        }
        
        builder.setMessage(message);

        //return new Log4jLogEvent(loggerName, marker, fqcn, level, message, properties, t);
        return builder.build();
    }
}
