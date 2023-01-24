# Simplest Money Amount

## How to use

### First You need to instantiate MoneyAmount object
You can parse from string
```java
var amount = MoneyAmount.parse("100.00");
```
or from raw value
```java
var amount = MoneyAmount.valueOf(10000);
```

### Available operations

You can use those methods like you use with BigDecimal

- MoneyAmount.add(MoneyAmount)
- MoneyAmount.subtract(MoneyAmount)
- MoneyAmount.multiply(MoneyAmount)
- MoneyAmount.divide(MoneyAmount, DivideMode)
- MoneyAmount.negate()
- MoneyAmount.abs()
- MoneyAmount.compareTo(MoneyAmount)
- MoneyAmount.toString()
