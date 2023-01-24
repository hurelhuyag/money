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

    public static MoneyAmount valueOf(long value) {
        return new MoneyAmount(value);
    }

    public static MoneyAmount parse(String value) throws NumberFormatException {
        int i = value.lastIndexOf('.');
        if (i == 0) {
            throw new NumberFormatException("value missing before dot sign");
        }
        if (i > 16 || (i == -1 && value.length() > 16)) {
            throw new NumberFormatException("overflow");
        }
        if (i == -1) {
            return new MoneyAmount(Long.parseLong(value) * PRECISION);
        } else {
            var bs = value.substring(i + 1);
            var bsLen = bs.length();
            var b = Long.parseLong(bs);
            switch (bsLen) {
                case 0:
                    throw new NumberFormatException("value missing after dot sign");
                case 1:
                    b *= 10L;
                case 2:
                    break;
                default:
                    throw new NumberFormatException("too much precision for money");
                    //b /= Math.pow(10, bsLen - 2);
            }
            return new MoneyAmount(Long.parseLong(value.substring(0, i)) * PRECISION + b);
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
