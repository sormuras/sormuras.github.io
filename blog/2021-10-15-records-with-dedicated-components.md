# Records with Dedicated Components

This is the third part of the miniseries about `record`s.

#### Parts

- [Records.copy](2020-05-05-records-copy.md)
- [Records.toTextBlock](2020-05-06-records-to-text-block.md)
- [Records with Dedicated Components](2021-10-15-records-with-dedicated-components.md)

## Either Names or Sealed Interface + Convention

Names of record components aren't supported directly by the Java language, yet.
As described in [Functional transformation of immutable objects](https://github.com/openjdk/amber-docs/blob/master/eg-drafts/reconstruction-records-and-classes.md#extrapolating-from-records), they may be supported in the future. 

In the meanwhile, here's a way to achieve something similar with minimal extra code.
It leverages following features, all available in Java 17:

- [JEP 395: Records](https://openjdk.java.net/jeps/395) 
- [JEP 409: Sealed Classes](https://openjdk.java.net/jeps/409)
- [JEP 394: Pattern Matching for `instanceof`](https://openjdk.java.net/jeps/394)

```java
public record Person(Name name, Nickname nickname) {

  public sealed interface Component {}

  public record Name(String value) implements Component {}

  public record Nickname(String value) implements Component {}

  public static Person of(String name, Component... components) {
    return new Person(name).with(components);
  }

  public Person(String name) {
    this(new Name(name), new Nickname("None"));
  }

  public Person with(Component component) {
    return new Person(
        component instanceof Name name ? name : name,
        component instanceof Nickname nickname ? nickname : nickname);
  }

  public Person with(Component... components) {
    var copy = this;
    for (var component : components) copy = copy.with(component);
    return copy;
  }

  public static void main(String[] args) {
    // Person[name=Name[value=Terence], nickname=Nickname[value=Nobody]]
    System.out.println(Person.of("Terence").with(new Nickname("Nobody")));
  }
}
```
