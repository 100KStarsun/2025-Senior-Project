import java.io.Serializable;
import java.time.Instant;

public record Message (String text, Instant timestamp, boolean isFromFirst) implements Serializable {}