package io.github.hurelhuyag.money;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.*;

public class MoneyAmountTest {

    @Test
    public void essential() {
        assertEquals("0.50", MoneyAmount.valueOf(50).toString());
        assertEquals("-0.50", MoneyAmount.valueOf(-50).toString());
        assertEquals("5.00", MoneyAmount.valueOf(500).toString());
        assertEquals("-5.00", MoneyAmount.valueOf(-500).toString());

        assertEquals(1, MoneyAmount.valueOf(101).intValue());
        assertEquals(1L, MoneyAmount.valueOf(101).longValue());
        assertEquals(1.01F, MoneyAmount.valueOf(101).floatValue());
        assertEquals(1.01D, MoneyAmount.valueOf(101).doubleValue());
    }

    @Test
    public void parse() {
        assertEquals(MoneyAmount.valueOf(12300), MoneyAmount.parse("123"));
        assertEquals(MoneyAmount.valueOf(12340), MoneyAmount.parse("123.4"));
        assertEquals(MoneyAmount.valueOf(12345), MoneyAmount.parse("123.45"));
        assertEquals(MoneyAmount.valueOf(45), MoneyAmount.parse("0.45"));
        assertEquals(MoneyAmount.valueOf(1_234_567_890_123_456_78L), MoneyAmount.parse("1234567890123456.78"));
        assertThrows(NumberFormatException.class, () -> MoneyAmount.parse(".456"));
        assertThrows(NumberFormatException.class, () -> MoneyAmount.parse("123."));
        assertThrows(NumberFormatException.class, () -> MoneyAmount.parse("123.456"));
        assertThrows(NumberFormatException.class, () -> MoneyAmount.parse("123.4567"));
        assertThrows(NumberFormatException.class, () -> MoneyAmount.parse("92233720368547758"));
        assertThrows(NumberFormatException.class, () -> MoneyAmount.parse("92233720368547758.07"));
    }

    @Test
    public void comparison() {
        //assertTrue(new BigDecimal("5").compareTo(new BigDecimal("6")) < 0);
        assertTrue(MoneyAmount.valueOf(5).compareTo(MoneyAmount.valueOf(6)) < 0);
        //assertTrue(new BigDecimal("5").compareTo(new BigDecimal("4")) > 0);
        assertTrue(MoneyAmount.valueOf(5).compareTo(MoneyAmount.valueOf(4)) > 0);
        assertEquals(0, MoneyAmount.valueOf(5).compareTo(MoneyAmount.valueOf(5)));

        assertTrue(MoneyAmount.valueOf(5).isGreaterThan(MoneyAmount.valueOf(4)));
        assertFalse(MoneyAmount.valueOf(5).isGreaterThan(MoneyAmount.valueOf(5)));
        assertFalse(MoneyAmount.valueOf(5).isGreaterThan(MoneyAmount.valueOf(6)));

        assertTrue(MoneyAmount.valueOf(5).isGreaterThanOrEqual(MoneyAmount.valueOf(4)));
        assertTrue(MoneyAmount.valueOf(5).isGreaterThanOrEqual(MoneyAmount.valueOf(5)));
        assertFalse(MoneyAmount.valueOf(5).isGreaterThanOrEqual(MoneyAmount.valueOf(6)));

        assertTrue(MoneyAmount.valueOf(5).isLowerThan(MoneyAmount.valueOf(6)));
        assertFalse(MoneyAmount.valueOf(5).isLowerThan(MoneyAmount.valueOf(5)));
        assertFalse(MoneyAmount.valueOf(5).isLowerThan(MoneyAmount.valueOf(4)));

        assertTrue(MoneyAmount.valueOf(5).isLowerThanOrEqual(MoneyAmount.valueOf(6)));
        assertTrue(MoneyAmount.valueOf(5).isLowerThanOrEqual(MoneyAmount.valueOf(5)));
        assertFalse(MoneyAmount.valueOf(5).isLowerThanOrEqual(MoneyAmount.valueOf(4)));
    }

    @Test
    public void simpleMath() {
        assertEquals(MoneyAmount.valueOf(250), MoneyAmount.valueOf(150).add(MoneyAmount.valueOf(100)));
        assertEquals(MoneyAmount.valueOf(50), MoneyAmount.valueOf(100).subtract(MoneyAmount.valueOf(50)));

        assertEquals(MoneyAmount.valueOf(100), MoneyAmount.valueOf(100).multiply(MoneyAmount.valueOf(100))); // 1 * 1 = 1
        assertEquals(MoneyAmount.valueOf(400), MoneyAmount.valueOf(200).multiply(MoneyAmount.valueOf(200))); // 2 * 2 = 4
        assertEquals(MoneyAmount.valueOf(1000), MoneyAmount.valueOf(1000).multiply(MoneyAmount.valueOf(100))); // 10 * 1 = 10
        assertEquals(MoneyAmount.valueOf(10201), MoneyAmount.valueOf(1010).multiply(MoneyAmount.valueOf(1010))); // 10.1 * 10.1 = 102.01

        assertTrue(MoneyAmount.valueOf(-5).isNegative());
        assertFalse(MoneyAmount.valueOf(5).isNegative());
        assertTrue(MoneyAmount.valueOf(5).isPositive());
        assertFalse(MoneyAmount.valueOf(-5).isPositive());

        assertEquals(MoneyAmount.valueOf(5), MoneyAmount.valueOf(-5).abs());
        assertEquals(MoneyAmount.valueOf(5), MoneyAmount.valueOf(5).abs());

        assertEquals(MoneyAmount.valueOf(5), MoneyAmount.valueOf(-5).negate());
        assertEquals(MoneyAmount.valueOf(-5), MoneyAmount.valueOf(5).negate());
    }

    @Test
    public void divide() {
        assertEquals(MoneyAmount.valueOf(1000), MoneyAmount.valueOf(1000).divide(MoneyAmount.valueOf(100), MoneyAmount.DivideMode.HALF_UP)); // 10 / 1 = 10
        assertEquals(MoneyAmount.valueOf(500), MoneyAmount.valueOf(1000).divide(MoneyAmount.valueOf(200), MoneyAmount.DivideMode.HALF_UP)); // 10 / 2 = 5
        assertEquals(MoneyAmount.valueOf(333), MoneyAmount.valueOf(1000).divide(MoneyAmount.valueOf(300), MoneyAmount.DivideMode.HALF_UP)); // 10 / 3 = 3.333333333333333
        assertEquals(MoneyAmount.valueOf(467), MoneyAmount.valueOf(1400).divide(MoneyAmount.valueOf(300), MoneyAmount.DivideMode.HALF_UP)); // 14 / 3 = 4.666666666666667
        assertEquals(MoneyAmount.valueOf(33), MoneyAmount.valueOf(100).divide(MoneyAmount.valueOf(300), MoneyAmount.DivideMode.HALF_UP)); // 1 / 3 = 0.33
        assertEquals(MoneyAmount.valueOf(-33), MoneyAmount.valueOf(-100).divide(MoneyAmount.valueOf(300), MoneyAmount.DivideMode.HALF_UP)); // -1 / 3 = 0.33
        assertEquals(MoneyAmount.valueOf(33), MoneyAmount.valueOf(-100).divide(MoneyAmount.valueOf(-300), MoneyAmount.DivideMode.HALF_UP)); // -1 / -3 = 0.33
    }

    @Test
    public void divideWithRounding() {
        var dividend = MoneyAmount.parse("4.02");
        var divisor = MoneyAmount.parse("4");
        // quotient: 1.005
        assertEquals(MoneyAmount.parse("1.00"), dividend.divide(divisor, MoneyAmount.DivideMode.HALF_DOWN));
        assertEquals(MoneyAmount.parse("1.01"), dividend.divide(divisor, MoneyAmount.DivideMode.HALF_UP));
        assertEquals(MoneyAmount.parse("1.00"), dividend.divide(divisor, MoneyAmount.DivideMode.HALF_EVEN));

        assertEquals(new BigDecimal("1.00"), new BigDecimal(dividend.toString()).divide(new BigDecimal(divisor.toString()), RoundingMode.HALF_DOWN));
        assertEquals(new BigDecimal("1.01"), new BigDecimal(dividend.toString()).divide(new BigDecimal(divisor.toString()), RoundingMode.HALF_UP));
        assertEquals(new BigDecimal("1.00"), new BigDecimal(dividend.toString()).divide(new BigDecimal(divisor.toString()), RoundingMode.HALF_EVEN));

        dividend = MoneyAmount.parse("4.06");
        divisor = MoneyAmount.parse("4");
        // quotient: 1.015
        assertEquals(MoneyAmount.parse("1.01"), dividend.divide(divisor, MoneyAmount.DivideMode.HALF_DOWN));
        assertEquals(MoneyAmount.parse("1.02"), dividend.divide(divisor, MoneyAmount.DivideMode.HALF_UP));
        assertEquals(MoneyAmount.parse("1.02"), dividend.divide(divisor, MoneyAmount.DivideMode.HALF_EVEN));

        assertEquals(new BigDecimal("1.01"), new BigDecimal(dividend.toString()).divide(new BigDecimal(divisor.toString()), RoundingMode.HALF_DOWN));
        assertEquals(new BigDecimal("1.02"), new BigDecimal(dividend.toString()).divide(new BigDecimal(divisor.toString()), RoundingMode.HALF_UP));
        assertEquals(new BigDecimal("1.02"), new BigDecimal(dividend.toString()).divide(new BigDecimal(divisor.toString()), RoundingMode.HALF_EVEN));

        dividend = MoneyAmount.parse("16.1");
        divisor = MoneyAmount.parse("4");
        // quotient: 4.025
        assertEquals(MoneyAmount.parse("4.02"), dividend.divide(divisor, MoneyAmount.DivideMode.HALF_DOWN));
        assertEquals(MoneyAmount.parse("4.03"), dividend.divide(divisor, MoneyAmount.DivideMode.HALF_UP));
        assertEquals(MoneyAmount.parse("4.02"), dividend.divide(divisor, MoneyAmount.DivideMode.HALF_EVEN));

        assertEquals(new BigDecimal("4.02"), new BigDecimal(dividend.toString()).divide(new BigDecimal(divisor.toString()), RoundingMode.HALF_DOWN));
        assertEquals(new BigDecimal("4.03"), new BigDecimal(dividend.toString()).divide(new BigDecimal(divisor.toString()), RoundingMode.HALF_UP));
        assertEquals(new BigDecimal("4.02"), new BigDecimal(dividend.toString()).divide(new BigDecimal(divisor.toString()), RoundingMode.HALF_EVEN));
    }

    /*@Test
    public void rounding() {
        System.out.println("HALF_DOWN");
        System.out.println(new BigDecimal("4.00499999999").setScale(2, RoundingMode.HALF_DOWN));
        System.out.println(new BigDecimal("4.005").setScale(2, RoundingMode.HALF_DOWN));
        System.out.println(new BigDecimal("4.0051").setScale(2, RoundingMode.HALF_DOWN));
        System.out.println(new BigDecimal("4.00549").setScale(2, RoundingMode.HALF_DOWN));
        System.out.println(new BigDecimal("4.006").setScale(2, RoundingMode.HALF_DOWN));

        System.out.println("HALF_UP");
        System.out.println(new BigDecimal("4.0049").setScale(2, RoundingMode.HALF_UP));
        System.out.println(new BigDecimal("4.00499999999").setScale(2, RoundingMode.HALF_UP));
        System.out.println(new BigDecimal("4.005").setScale(2, RoundingMode.HALF_UP));

        System.out.println("HALF_EVEN");
        System.out.println(new BigDecimal("4.045").setScale(2, RoundingMode.HALF_EVEN));
        System.out.println(new BigDecimal("5.055").setScale(2, RoundingMode.HALF_EVEN));
        System.out.println(new BigDecimal("6.065").setScale(2, RoundingMode.HALF_EVEN));
    }*/
}
