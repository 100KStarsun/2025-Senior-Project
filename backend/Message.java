import java.time.Instant;

public record Message (String text, Instant timestamp, boolean isFromFirst) {}