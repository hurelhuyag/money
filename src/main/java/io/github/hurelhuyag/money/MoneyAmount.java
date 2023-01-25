package io.github.hurelhuyag.money;

public final class MoneyAmount extends Number implements Comparable<MoneyAmount> {

    public enum DivideMode {
        HALF_DOWN,
        HALF_UP,
        HALF_EVEN,
    }

    public static final int PRECISION = 100;
    public static final MoneyAmount ZERO = new MoneyAmount(0L);
    public static final MoneyAmount ONE = new MoneyAmount(100L);

    /**
     * Instantiate with internal long value without any modification
     * @param value value to use at internally
     */
    public static MoneyAmount valueOf(long value) {
        return new MoneyAmount(value);
    }

    /**
     * Instantiate same amount of primitive long type
     * @param value value in primitive long type
     * @return same amount in MoneyAmount type
     */
    public static MoneyAmount valueFrom(long value) {
        return new MoneyAmount(value * PRECISION);
    }

    /**
     * Instantiate same amount of primitive double type
     * @param value value in primitive double type
     * @return same amount in MoneyAmount type
     */
    public static MoneyAmount valueFrom(double value) {
        return new MoneyAmount((long) (value * PRECISION));
    }

    /**
     * Instantiate from string representation.
     * @param value value in string
     * @return parsed MoneyAmount value
     * @throws NumberFormatException if passed value is not suitable to parsed into MoneyAmount
     */
    public static MoneyAmount parse(String value) throws NumberFormatException {
        int i = value.lastIndexOf('.');
        if (i == 0) {
            throw new NumberFormatException("value missing before dot sign");
        }
        int valueLen = value.length();
        if (i > 16 || (i == -1 && valueLen > 16)) {
            throw new NumberFormatException("overflow");
        }
        if (i == -1) {
            return new MoneyAmount(Long.parseLong(value) * PRECISION);
        } else {
            var bsLen = valueLen - i - 1;
            var b = Long.parseLong(value, i + 1, valueLen, 10);
            switch (bsLen) {
                case 0:
                    throw new NumberFormatException("value missing after dot sign");
                case 1:
                    b *= 10L;
                    break;
                case 2:
                    break;
                default:
                    throw new NumberFormatException("too much precision for money");
                    //b /= Math.pow(10, bsLen - 2);
            }
            return new MoneyAmount(Long.parseLong(value, 0, i, 10) * PRECISION + b);
        }
    }

    private final long value;

    private MoneyAmount(long value) {
        this.value = value;
    }

    public MoneyAmount add(MoneyAmount another) {
        return new MoneyAmount(value + another.value);
    }

    public MoneyAmount subtract(MoneyAmount another) {
        return new MoneyAmount(value - another.value);
    }

    public MoneyAmount multiply(MoneyAmount another) {
        return new MoneyAmount(value * another.value / PRECISION);
    }

    public MoneyAmount divide(MoneyAmount another, DivideMode mode) {
        long r = value * PRECISION * 10 / another.value;
        long last = Math.abs(r) % 10;
        r /= 10;
        if (last == 5) {
            r += switch (mode) {
                case HALF_UP -> 1;
                case HALF_DOWN -> 0;
                case HALF_EVEN -> {
                    var r1 = r % 10;
                    if (r1 % 2 == 1) {
                        yield 1;
                    } else {
                        yield 0;
                    }
                }
            };
        } else if (last > 5) {
            r += 1;
        }
        return new MoneyAmount(r);
    }

    public MoneyAmount abs() {
        if (value >= 0) {
            return this;
        } else {
            return new MoneyAmount(-value);
        }
    }

    public MoneyAmount negate() {
        return new MoneyAmount(-value);
    }

    public boolean isNegative() {
        return value < 0;
    }

    public boolean isPositive() {
        return value > 0;
    }

    public boolean isGreaterThan(MoneyAmount another) {
        return value > another.value;
    }

    public boolean isGreaterThanOrEqual(MoneyAmount another) {
        return value >= another.value;
    }

    public boolean isLowerThan(MoneyAmount another) {
        return value < another.value;
    }

    public boolean isLowerThanOrEqual(MoneyAmount another) {
        return value <= another.value;
    }

    @Override
    public int intValue() {
        return (int) (value / PRECISION);
    }

    @Override
    public long longValue() {
        return value / PRECISION;
    }

    @Override
    public float floatValue() {
        return (float)value / PRECISION;
    }

    @Override
    public double doubleValue() {
        return (double)value / PRECISION;
    }

    @Override
    public int compareTo(MoneyAmount another) {
        return (int)(this.value - another.value);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof MoneyAmount moneyAmount) {
            return moneyAmount.value == this.value;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(value);
    }

    @Override
    public String toString() {
        if (value >= 0) {
            return String.format("%d.%02d", value / PRECISION, value % PRECISION);
        } else {
            return String.format("-%d.%02d", -value / PRECISION, -value % PRECISION);
        }
    }
}
