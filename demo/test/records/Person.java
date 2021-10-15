package records;

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
