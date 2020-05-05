# Java Records - Records.copy

Let's enhance the upcoming Java programming language enhancement `record`.
Records are described in [JEP 359(Preview)](https://openjdk.java.net/jeps/359) (follow-up to [JEP draft(Second Preview)](https://openjdk.java.net/jeps/8242303)) and by Brian Goetz in [Data Classes and Sealed Types for Java](https://cr.openjdk.java.net/~briangoetz/amber/datum.html).
Looking forward to the Java release, that doesn't mandate the `--enable-preview` switch to use records anymore.

Starting with mini series about `record`s, I propose new methods to be added to the `java.lang.Record` type.
Or, these new methods can also reside in a `java.util.Records` helper taking the record object as a first parameter.
Yes, an externally hosted `Records` helper class is probably the best target.
For the time being.

**Beware!**

> ⚠ ["Records are addictive!!"](https://twitter.com/delabassee/status/1255497443568955397)
>
> _David Delabassée_

Sources can be found and copied from the [records](../demo/test/records) package.
Feedback on the ideas and the code is appreciated: https://github.com/sormuras/sormuras.github.io/issues

## `Records.copy(Record template, Map<String, Object> overrides)`

This method creates a copy of the specified record object using the given named value overrides.
If a component is not overridden, i.e. the `overrides` map doesn't contain the name of a component as a key, the component value of the record object being copied is used as the value.
That means, an empty `overrides` map passed to the method returns a shallow clone of the `template` object.
An `overrides` map with all components being present is equal to calling the canonical constructor of `R`.

The proposed `copy` method works best on records with many declared components and a few overrides.

### Pseudo-Code Usage Sample
```java
record R(T0 n0, T1 n1, ... Tn nn) {}

var template = new R(v0, v1, v2, ...);
var expected = new R(v0, o1, v2, ...);
var actual = Records.copy(template, Map.of("n1", o1));

assert expected.equals(actual);
```

If named parameters are a thing in the future, a type-safe syntax for a generated `copy` instance method could read like:

```java
var template = new R(v0, v1, v2, ...);
var copy = template.copy(n1 -> o1, ...);
```

A generated copy-constructor with named and type-safe overrides could read like:

```java
var template = new R(v0, v1, v2, ...);
var copy = new R(template, n1 -> o1, ...);
```

### Proof Of Concept Implementation for Java 14+

This proof-of-concept implementation leverages the record-related reflection API introduced by JEP 359.
It requires the `--enable-preview` switch to be present at compile time and runtime.
The canonical constructor is used as a copy constructor.
The signature of the canonical constructor is determined on-the-fly.
That is possible due to the order specification of `Class#getRecordComponents()`.

```java
static <R extends Record> R clone(R template, Map<String, Object> overrides) {
  try {
    var types = new ArrayList<Class<?>>();
    var values = new ArrayList<>();
    for (var component : template.getClass().getRecordComponents()) {
      types.add(component.getType());
      var name = component.getName();
      var overridden = overrides.containsKey(name);
      values.add(overridden ? overrides.get(name) : component.getAccessor().invoke(template));
    }
    var canonical = template.getClass().getDeclaredConstructor(types.toArray(Class[]::new));
    @SuppressWarnings("unchecked")
    var result = (R) canonical.newInstance(values.toArray(Object[]::new));
    return result;
  } catch (ReflectiveOperationException e) {
    throw new AssertionError("Reflection failed: " + e, e);
  }
}
```

### Variant for Java 9+

This variant copies an object of a record-like class that also implements `Cloneable`.
No `--enable-preview` required here.
But might fail due to `InaccessibleObjectException` instances being thrown.

```java
public static <R extends Cloneable> R copy(R template, Map<String, Object> overrides) {
  var recordLikeClass = template.getClass();
  try {
    @SuppressWarnings("unchecked")
    R clone = (R) recordLikeClass.getDeclaredMethod("clone").invoke(template);
    for (var override : overrides.entrySet()) {
      var componentLikeField = recordLikeClass.getDeclaredField(override.getKey());
      componentLikeField.setAccessible(true);
      componentLikeField.set(clone, override.getValue());
    }
    return clone;
  } catch (ReflectiveOperationException e) {
    throw new AssertionError("Reflection over " + recordLikeClass + " failed: " + e, e);
  }
}
```
