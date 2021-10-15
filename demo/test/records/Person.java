package records;

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
