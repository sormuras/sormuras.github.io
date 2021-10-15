# Records with Dedicated Components

This is the third part of the miniseries about `record`s.
It revisits the copy idea introduced in the first part;
this time without resorting to reflection.

#### Parts

1. [Records.copy](2020-05-05-records-copy.md)
1. [Records.toTextBlock](2020-05-06-records-to-text-block.md)
1. [Records with Dedicated Components](2021-10-15-records-with-dedicated-components.md)

## Naming Conventions

Given a `Person` record and a demo program that asserts some basic properties.

```java
public record Person(String name, String nickname) {

  public static void main(String[] args) {
    var person = new Person("Terence", "Nobody");

    assert "Terence".equals(person.name());
    assert "Nobody".equals(person.nickname());
    
    var mario = new Person("Mario", person.nickname());

    assert "Mario".equals(mario.name());
    assert "Nobody".equals(mario.nickname());
  }
}
```

As described in [Functional transformation of immutable objects](https://github.com/openjdk/amber-docs/blob/master/eg-drafts/reconstruction-records-and-classes.md#extrapolating-from-records), Java might support the following syntax in the future:

```
var mario = person with { name = "Mario"; }
```

In the meanwhile, here's a way to achieve something similar with minimal extra code.
It leverages following features, all available since Java 17:

- [JEP 395: Records](https://openjdk.java.net/jeps/395) 
- [JEP 409: Sealed Classes](https://openjdk.java.net/jeps/409)
- [JEP 394: Pattern Matching for `instanceof`](https://openjdk.java.net/jeps/394)

Here's a line we could write today:

```
var mario = person.with(new Name("Mario"));
```

It uses a type with a deliberate chosen name close to its component name.  

That's the underlying code with an update demo program:

```java
public record Person(String name, String nickname) {

  public sealed interface Component {}

  public record Name(String value) implements Component {}

  public record Nickname(String value) implements Component {}

  public Person(String name) {
    this(name, "");
  }

  public Person with(Component component) {
    return new Person(
        component instanceof Name name ? name.value : name,
        component instanceof Nickname nickname ? nickname.value : nickname);
  }

  public Person with(Component... components) {
    var copy = this;
    for (var component : components) copy = copy.with(component);
    return copy;
  }

  public static void main(String[] args) {
    var person =
        new Person("Terence")
            .with(new Nickname("Nobody"))
            .with(new Nickname("Somebody"), new Name("Mario"));

    assert "Mario".equals(person.name());
    assert "Somebody".equals(person.nickname());
    
    System.out.println(person); // Person[name=Mario, nickname=Somebody]    
  }
}
```

## Open Ends

- What about extracting some or all `with` methods into an interface?
- What about `Optional<T>` as component types?
- What about `Collection<T>` as component types?
- What about nested components?
- What about tweaking a small part of an entire object tree?
