# Simplest Money Amount ![workflow](https://github.com/hurelhuyag/money/actions/workflows/ci.yaml/badge.svg)

## Advantages

- No BigDecimal
- No Floating point math
- No Currency requirement. Just Money Amount
- Internally use `long` as money amount in cents
- Simple To use
- Fast because integer math operation. Event division is integer math
- Every method mimics every BigDecimal. If you know BigDecimal, You know how to use.
- Edge cases tested and compared with BigDecimal

## Requirement

- Money amounts are coded in cents precision
- Expecting 100 cents are 1 unit currency
- Divide operation has 3 choices for rounding. HALF_DOWN, HALF_UP, and HALF_EVEN

## How to use

### Import Dependency
```xml
<dependency>
    <groupId>io.github.hurelhuyag</groupId>
    <artifactId>money</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```
Ps: Currently, Not released yet. You can run `mvn install` locally until I release it on maven central.

### Code
You can parse from string
```java
var amount = MoneyAmount.parse("100.00");

```
or from raw value
```java
var amount = MoneyAmount.valueOf(10000);
```

Using methods
```java
var total = MoneyAmount.ZERO;
for (var item : orderItems) {
    total = total.add(item.price().multiply(MoneyAmount.valueOf(item.count()*MoneyAmount.PRECISION)));
}
var vat = total.divide(10, DivideMode.HALF_UP);

System.out.println("total: " + total);
System.out.println("VAT: " + vat);
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

## Contribution

If you have something in you mind feel free to open a pull request or create an issue.

## To Do

- More divide operation tests
- Benchmark
