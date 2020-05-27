package fr.bcarter.log4j.converter;

import java.text.SimpleDateFormat;
import java.util.List;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;
import org.apache.logging.log4j.core.pattern.PatternConverter;
import org.apache.logging.log4j.core.pattern.PatternFormatter;
import org.apache.logging.log4j.core.pattern.PatternParser;
import org.apache.logging.log4j.core.util.datetime.FastDateFormat;

/**
 * Format a date (as epoch) from a pattern in a specific format. 
 *  By default, this uses {@link java.text.DateFormat#DEFAULT} format if only one option is specified. 
 *  The second option is used to specify a custom format as specified by {@link SimpleDateFormat}.
 * E.g.: %formatDate{%map{timestamp}{MM-dd-yy HH:mm:ss,SSS}}
 */
@Plugin(name = "CustomDatePatternConverter", category = PatternConverter.CATEGORY)
@ConverterKeys({"fd", "formatDate"})
public final class FormatDatePatternConverter extends LogEventPatternConverter {

    private final FastDateFormat dateFormat;
    private final List<PatternFormatter> patternFormatters;

    protected FormatDatePatternConverter(final List<PatternFormatter> patternFormatters) {
        // If not format specified, use the default one (MEDIUM)
        this(FastDateFormat.getInstance(), patternFormatters);
    }
    
    protected FormatDatePatternConverter(FastDateFormat dateFormat, final List<PatternFormatter> patternFormatters) {
        super("FormatDate", "formatDate");
        this.dateFormat = dateFormat;
        this.patternFormatters = patternFormatters;
    }

    /**
     * Gets an instance of the class.
     *
     * @param config The current Configuration.
     * @param options pattern options (date required, format non mandatory).
     * @return instance of class.
     */
    public static FormatDatePatternConverter newInstance(final Configuration config, String[] options) {
        if (options.length > 2 || options.length == 0) {
            LOGGER.error("Incorrect number of options on formatDate. Expected 1 or 2, but received {}",
                options.length);
            return null;
        }
        if (options[0] == null) {
            LOGGER.error("No pattern supplied on formatDate");
            return null;
        }

        final PatternParser parser = PatternLayout.createPatternParser(config);
        final List<PatternFormatter> formatters = parser.parse(options[0]);
        
        if (options.length == 1 || options[1] == null) {
            return new FormatDatePatternConverter(formatters);
        }
        return new FormatDatePatternConverter(FastDateFormat.getInstance(options[1]), formatters);
    }

    @Override
    public void format(LogEvent event, StringBuilder toAppendTo) {
        // Retrieve date as epoch from formatters
        StringBuilder epoch = new StringBuilder();
        for (int i = 0, size = patternFormatters.size(); i < size; i++) {
            patternFormatters.get(i).format(event, epoch);
        }
        
        // Add the formatted date from epoch
        try {
            toAppendTo.append(dateFormat.format(Long.valueOf(epoch.toString())));
        } catch (NumberFormatException nfe) {
            LOGGER.error("Date as epoch is not in number format ("+epoch+")", nfe);
        }
    }
}
