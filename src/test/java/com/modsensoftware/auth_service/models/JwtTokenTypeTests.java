package com.modsensoftware.auth_service.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

public class JwtTokenTypeTests {

    @Test
    public void testEnumValues() {
        // Проверяем, что перечисление содержит ожидаемые значения
        JwtTokenType[] values = JwtTokenType.values();
        assertEquals(2, values.length);
        assertSame(values[0], JwtTokenType.ACCESS);
        assertSame(values[1], JwtTokenType.REFRESH);
    }

    @Test
    public void testEnumNames() {
        // Проверяем, что имена перечислений соответствуют ожидаемым
        assertEquals("ACCESS", JwtTokenType.ACCESS.name());
        assertEquals("REFRESH", JwtTokenType.REFRESH.name());
    }

    @Test
    public void testEnumOrdinal() {
        // Проверяем, что порядковые номера перечислений соответствуют ожидаемым
        assertEquals(0, JwtTokenType.ACCESS.ordinal());
        assertEquals(1, JwtTokenType.REFRESH.ordinal());
    }
}
