# Java Records - Records.toTextBlock

Let's enhance the upcoming Java programming language enhancement `record` with a `toTextBlock` method.
This is the second part of the mini series about `record`s.

#### Proposed Methods

- [Records.copy](2020-05-05-records-copy.md)
- [Records.toTextBlock](2020-05-06-records-toTextBlock.md)

## `Records.toTextBlock(Record)`

For record types, a `public String toString()` method implementation is generated for us.
It returns a _"string representation of all the record components, with their names"_.
That's nice.
All in one line.
That's not so nice.
Especially when your record contains many components.
And components are sometimes records, as well.
Thus, their components contribute to the same line.

The proposed `Records.toTextBlock(Record)` method also produces a string representation.
But with new line separators and tab characters inserted to achieve a tree-like view of all record components.

### Pseudo-Code Usage Sample

Given the record declartion `R`:
```
record R(T0 n0, T1 n1, ... Tn nn) {}
```

The generated `toString()` method produces:
```
"R[n0=v0, n1=v1, ... nn=vn]"
```

The string representation produced by `toTextBlock(Record)` looks like:
```
""" 
R
\tn0 = v0
\tn1 = v1
...
\tnn = vn
"""
```

Values of type record are printed as indented text blocks.
Let's assume that type `T2` is declared as a `record`.

While `Record.toString()` represents that within the same line:

```
"R[n0=v0, n1=v1, n2=T2[nA=w0, nB=w1, ..., nz=wz] ... nn=vn]"
```

```
R
\tn0 = v0
\tn1 = v1
\tn2 -> T2
\t\t\tnA = w0
\t\t\tnB = w1
...
\tnn = vn
"""
```

### Proof Of Concept Implementation

```java
class Records {
  /** Returns a multi-line string representation of the given object. */
  public static String toTextBlock(Record record) {
    return toTextBlock(0, record, "\t", Class::getSimpleName, true);
  }

  /** Returns a multi-line string representation of the given object. */
  private static String toTextBlock(int level, Record record, String indent, Function<Class<?>, String> caption, boolean sortByName) {
    var lines = new ArrayList<String>();
    if (level == 0) lines.add(caption.apply(record.getClass()));

    var components = record.getClass().getRecordComponents();
    if (sortByName) Arrays.sort(components, Comparator.comparing(RecordComponent::getName));

    for (var component : components) {
      var name = component.getName();
      var shift = indent.repeat(level);
      try {
        var value = component.getAccessor().invoke(record);
        var nested = value.getClass();
        if (nested.isRecord()) {
          lines.add(String.format("%s%s%s -> %s", shift, indent, name, caption.apply(nested)));
          lines.add(toTextBlock(level + 2, (Record) value, indent, caption, sortByName));
          continue;
        }
        lines.add(String.format("%s%s%s = %s", shift, indent, name, value));
      } catch (ReflectiveOperationException e) {
        lines.add("// Reflection over " + component + " failed: " + e);
      }
    }
    return String.join(System.lineSeparator(), lines);
  }
}
```

### Java 9+ variant w/o `--enable-preview`

```java
class Records {

  /**
   * An informative annotation type used to indicate that a class type declaration is intended to be
   * transmuted into a {@code record} as defined by JEP 359, soon.
   */
  @Target(ElementType.TYPE)
  @Retention(RetentionPolicy.RUNTIME)
  public @interface Record {}

  /** Returns a multi-line string representation of the given object. */
  public static String toTextBlock(Object object) {
    return toTextBlock(0, object, "\t", Class::getSimpleName, true);
  }

  private static String toTextBlock(int level, Object object, String indent, Function<Class<?>, String> caption, boolean sortByName) {

    var lines = new ArrayList<String>();
    if (level == 0) lines.add(caption.apply(object.getClass()));

    var fields = object.getClass().getDeclaredFields();
    if (sortByName) Arrays.sort(fields, Comparator.comparing(Field::getName));

    for (var field : fields) {
      // if not a "private final field" continue
      var name = field.getName();
      var method = object.getClass().getDeclaredMethod(name);
      // if not a "matching component accessor" continue
      try {
        var shift = indent.repeat(level);
        var value = method.invoke(object);
        var nested = value.getClass();
        if (nested.isAnnotationPresent(Record.class)) {
          lines.add(String.format("%s%s%s -> %s", shift, indent, name, caption.apply(nested)));
          lines.add(toTextBlock(level + 2, value, indent, caption, sortComponentsByName));
          continue;
        }
        lines.add(String.format("%s%s%s = %s", shift, indent, name, value));
      } catch (ReflectiveOperationException e) {
        lines.add("// Reflection over " + method + " failed: " + e);
      }
    }
    return String.join(System.lineSeparator(), lines);
  }
}
```
