package org.zalando.riptide.failsafe;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class CompositeDelayParserTest {

    private final DelayParser first = mock(DelayParser.class);
    private final DelayParser second = mock(DelayParser.class);

    private final DelayParser unit = new CompositeDelayParser(Arrays.asList(first, second));

    @BeforeEach
    void defaultBehavior() {
        // starting with Mockito 3.4.4, mocks will return Duration.ZERO instead of null, by default
        when(first.parse(any())).thenReturn(null);
        when(second.parse(any())).thenReturn(null);
    }

    @Test
    void shouldUseFirstNonNullDelay() {
        when(first.parse("1")).thenReturn(Duration.ofSeconds(1));
        when(second.parse("2")).thenReturn(Duration.ofSeconds(2));

        assertEquals(Duration.ofSeconds(1), unit.parse("1"));
    }

    @Test
    void shouldIgnoreNullDelay() {
        when(second.parse("2")).thenReturn(Duration.ofSeconds(2));

        assertEquals(Duration.ofSeconds(2), unit.parse("2"));
    }

    @Test
    void shouldFallbackToNullDelay() {
        when(first.parse("1")).thenReturn(Duration.ofSeconds(1));
        when(second.parse("2")).thenReturn(Duration.ofSeconds(2));

        assertNull(unit.parse("3"));
    }

}
