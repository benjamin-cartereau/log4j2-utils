# log4j2-utils
Some stuff around log4j2 (advanced) usage


## LogEventFactory
I have tried to create a custom LogEventFactory called "MapMessageLogEventFactory".  
It aims at creating a LogEvent by replacing some properties values of the default event by values set within the message that implements the MapMessage.  
To activate this custom LogEventFactory, you should add a file "log4j2.component.properties" within the classpath with this content:  
```java
Log4jLogEventFactory = fr.bcarter.log4j.MapMessageLogEventFactory
```  
  
Link : https://logging.apache.org/log4j/2.x/manual/extending.html#LogEventFactory


## PatternConverter
I have tried to create a custom (log event) pattern converter called "FormatDatePatternConverter".  
It aims at formatting a date (as epoch) from a pattern in a specific format.  
By default, this uses {@link java.text.DateFormat#DEFAULT} format if only one option is specified.  
The second option is used to specify a custom format as specified by {@link SimpleDateFormat}.  
E.g.:  
```xml
%formatDate{%map{tracedAt}}{yyyy-MM-dd HH:mm:ss.SSS}
```

## JacksonJsonMixIn
While using log4j2-elasticsearch appender, I have tried to create a custom mixin called "MultiformatMessageJacksonJsonMixIn".  
Eventually, it serializes a message that is a MultiformatMessage (eg.:MapMessage) as JSON (plain text by default otherwise).  

Usage example:  
```xml
<Elasticsearch [...]>  
    [...]  
    <JacksonJsonLayout [...]>  
	<JacksonMixIn mixInClass="fr.cnav.architect.mersi.log4j.jackson.MultiformatMessageJacksonJsonMixIn"
							  targetClass="org.apache.logging.log4j.core.LogEvent" />  
    [...]  
    </JacksonJsonLayout>  
</Elasticsearch>
```  
  
Link : https://github.com/rfoltyns/log4j2-elasticsearch/tree/master/log4j2-elasticsearch-core#jacksonjsonlayout  