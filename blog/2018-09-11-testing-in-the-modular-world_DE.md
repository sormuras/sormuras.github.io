# Testen im Modulsystem von Java

In diesem Blog geht es um die Organisation, das Auffinden und das Ausführen von Tests im Modulsystem von Java.
Es ist **keine** [Einführung in das Java Modulsystem](https://blog.codefx.org/java/java-module-system-tutorial/).

_Zu viel Text, her mit dem Code: Beispielprojekt [sormuras/testing-in-the-modular-world](https://github.com/sormuras/testing-in-the-modular-world) klonen und mit `mvn verify` bauen._

## Anno dazumal...

Zunächst ein kleiner Exkurs in die Vergangenheit, denn schon damals stand diese Frage im Raum: [_"Wo soll ich Testklassen ablegen?"_](https://junit.org/junit4/faq.html#organize_1)

- Lege Testklassen im selben Verzeichnis ab, in dem sich auch die Produktionsklassen befinden.

Zum Beispiel:

```text
src/
   com/
      xyz/
         📜 SomeClass.java
         🔨 SomeClassTests.java
```

Für kleinere Projekte war das Vorgehen okay -- wobei schon damals viele Entwickler bei größeren Projekten die Vermischung von Produktions- und Testklassen als Nachteil dieses Ansatzes empfanden. Denn der Aufwand für das finale Zusammenstellen des Produktes wurde mit der Zeit immer höher, weil die Testklassen dabei rausgefiltert werden mussten.

- Deswegen war es einfacher, die Testklassen in einem separaten aber analog aufgebauten Verzeichnisbaum abzulegen.

```text
main/                          test/
   com/                           com/
      xyz/                           xyz/
         📜 SomeClass.java              🔨 SomeClassTests.java
```

Dieser Ansatz ermöglicht es dennoch, dass alle Tests weiterhin auf die gleichen Elemente der Produktionsklassen zugreifen können.

Wodurch war das damals und ist es heute weiterhin möglich? Durch den **Klassenpfad**!

Jedes Element des Klassenpfads wird der Laufzeitumgebung als Ursprung eines Resourcenbaums zur Verfügung gestellt.
Ein spezieller Ressourcentypus sind Javaklassen (`.class`-Dateien), die wiederherum zu einem Paket (`package`) gehören.
Der Klassenpfad schreibt dabei nicht vor, wie häufig ein Paket deklariert werden darf.
Dadurch werden alle Ressourcen zu einem logischen Baum zusammengefügt, was zu einer änhlichen Situation führt, als ob man wie früher alle Ressourcen unter einem physikalischen Verzeichnis ablegte.
Testklassen können somit so auf die Produktionsklassen zugreifen, als ob sie im selben Verzeichnis lägen: Java `packages` werden wie "white boxes" behandelt. Das schließt den Zugriff auf Klassenelemente, die mit *package private* oder mit `protected` versehen sind, ein.

// TODO Fließtexte ab hier weiter übersetzen.
// TODO Code-Beispiele, markiert durch ```code``` Blöcke, so belassen.

Schonmal eine Testklasse in einem anderen Paket als die zu testende Produktionsklassen abgelegt?

```text
main/                          test/                               test/
   com/                           com/                                black/
      xyz/                           xyz/                                box/
         📜 SomeClass.java              🔨 SomeClassTests.java              🔲 BlackBoxTests.java
```

Das ist dann das "Black-Box"-Testen! Hier gelten alle Zugriffsregeln, die es für Sichtbarkeiten von Typen in andere Paketen gibt.
Wie lauten diese Regeln?
_Hinweis: weiter unten folgt eine Übersicht zum diesem Thema._

## Auf zu neuen Ufern: Hallo Java Module!

Mit dem Java-Modulsystem kann man eine Gruppe von Paketen unter einem Modulnamen zusammenfassen.
Dabei kann man als Autor eines Moduls frei entscheiden, welche der Pakete für andere Module zur Verfügung stehen.
Wenn man nun die oben beschriebene Idee der separierten Verzeichnisse einfach auf das Modulsystem überträgt, ensteht das folgende Bild:

```text
main/                          test/                               test/
   com.xyz/                       com.xyz/                            black.box/
      com/                           com/                                black/
         abc/                           abc/                                box/
            📜 OtherClass.java             🔨 OtherClassTests.java              🔲 BlackBoxTests.java
         xyz/                           xyz/                             ☕ module-info.java
            📜 SomeClass.java              🔨 SomeClassTests.java
      ☕ module-info.java             🔥 module-info.[java|test] 🔥
```

Die linke Spalte `main` und die rechte Spalte `test/black.box` enthalten keine großen Überraschungen.
Anders die mittlere Spalte `test/com.xyz` oder die Spalte _white box_; hier wurde nämlich eine Datei `module-info.[java|test]` hinzugefügt. Bevor wir aber das Thema _white box_-Testen vertiefen, starten wir mit den beiden einfacheren Modulen.

### ☕ `module com.xyz`

- Das Module namens `com.xyz` enthält ein paar ausgedachte Einträge.
- Es enthält die Pakete `com.abc` und `com.xyz`.
- Es exportiert einzig und allein das Paket `com.xyz`.

```java
module com.xyz {
    requires java.logging;
    requires java.sql;

    exports com.xyz;
}
```
_Hinweis: Das Paket `com.abc` sollte **nicht** in einem Modul namens `com.xyz` auftauchen. Warum nicht? Stephen erläutert in seinem Blog [JPMS module naming](https://blog.joda.org/2017/04/java-se-9-jpms-module-naming.html) die Details._

### ☕ `open module black.box`

- Das Testmodul `black.box` benötigt das Modul `com.xyz` sowie eine Reihe anderer Module rund ums Testen.
- Es kann dabei nur auf zugreifbare Typen in diesen anderen Modulen zugreifen (nämlich solche, die mit `public` versehen sind und sich gleichzeitig in einem exportierten Paket befinden).
- Das gilt natürlich ebenso für _unser_ `com.xyz`-Modul: Tests können auf öffentliche Klassen im Paket `com.xyz` zugreifen - nicht aber auf Klassen im geschützen Paket `com.abc`, selbst wenn diese `public` sind.
- Zusätzlich erlaubt das `black.box`-Modul mittels `open` tiefe Reflektion, damit Testframeworks auch _package private_-Tests auffinden und ausführen können.

```java
open module black.box {
    requires com.xyz;

    requires org.junit.jupiter.api;
    requires org.assertj.core;
    requires org.mockito;
}
```

Black-Box-Testen ist allerdings der einfache Teil der Geschichte.
Das `black.box`-Testmodul ist quasi der erste Kunde des Hauptmoduls `com.xyz`.
Das Testmodul hält sich an die vom Modulsystem vorgegebenen Grenzen -- so wie jedes andere Modul auch. 

Es folgt der spannende Teil.

## Modular White Box Testing

Zunächst erweitern wir die [Zugriffstabelle](https://docs.oracle.com/javase/tutorial/java/javaOO/accesscontrol.html) um eine Spalte. Nämlich um eine Spalte, welche die Zugriffsmöglichkeiten aus einem fremden Modul beschreibt.

### Zugriffstabelle

Die Klasse `A` in `package foo` enthält jeweils ein Feld für jeden Zugriffsmodifikator.
Jede Spalte von `B` bis `F` steht für eine andere Klasse und zeigt die Sichtbarkeit an: Ein ✅ bedeutet, dass das entsprechende Feld von `A` sichtbar ist; ein ❌ steht für "nicht sichtbar".

- **B** - gleiches `module`, gleiches `package`, **andere** Datei: `package foo; class B {}`
- **C** - gleiches `module`, **anderes** package, Ableitung: `package bar; class C extends foo.A {}`
- **D** - gleiches `module`, **anderes** package, beziehungslos: `package bar; class D {}`
- **E** - **anderes** `module`, package `foo` wird exportiert: `package bar; class E {}`
- **F** - **anderes** `module`, package `foo` wird _nicht_ exportiert `package bar; class F {}`

```text
                       B     C     D    E     F
package foo;
public class A {       ✅   ✅   ✅   ✅   ❌  // public
  public int i;        ✅   ✅   ✅   ✅   ❌  // public
  protected int j;     ✅   ✅   ❌   ❌   ❌  // protected
  int k;               ✅   ❌   ❌   ❌   ❌  // _keine Modifikator_ oder _package private_
  private int l;       ❌   ❌   ❌   ❌   ❌  // private
}
```

Die Spalten **E** und **F** wurden bereits im obigen Abschnitt "Modulares Blackbox-Testen" behandelt.
Wobei **F** nur zeigt, dass selbst mit `public` modifizierte Typen aus nicht-exportierten `package`s eben nicht sichtbar sind.
Aber wir möchten ja Unittests so schreiben wie immer, und dabei auch auf interne Typen zugreifen können.

Wir wollen also **B**, **C** und **D** zurück!

Damit wir das gewohnte Verhalten wieder herstellen, können wir entweder das komplette Java-Modulsystem (für's Testen) ausschalten. Oder wir nutzen einen neuen Weg der es ermöglicht, dass sich Test- und Haupttypen logisch in ein und demselben Modul befinden. Analog zu damals, als die Lösung `split packages` waren, die vom `class-path` aufgelöst wurden.
_Same same but different._ Nur, dass `split packages` in der modularen Welt nicht mehr erlaubt sind.

## 🔥`module-info.[java|test]`🔥

Es gibt mindestens drei Möglichkeiten, wie man die strikten Grenzen des Java-Modulsystems beim Testen umgehen kann. 

### Zurück zum `classpath`

Alle `module-info.java`-Dateien löschen, oder diese zumindest vom Kompilieren ausschließen - und schon ignorieren die Tests die Grenzen des Modulsystems! Dadurch werden, neben internen Details von Java selbst, auch Interna von anderen und eben der eignenen Bibliothek verfügbar. Letzteres war das Ziel -- doch die Kosten, es zu erreichen, sind hoch.

Wie aber können wir die Grenzen des Modulsystems intakt lassen und trotzdem die internen Typen der eigenen Bibliothek testen?
Dazu mehr in den nächsten zwei Abschnitten.

### Modulares White-Box-Testen mit `module-info.java` in `src/test/java`

Die für den Testautor einfachste Variante besteht darin, eine Beschreibung für ein Testmodul anzulegen.
Die Beschreibung kann mit der gleichen Syntax geschehen, die bei _normalen_ Modulen eingesetzt wird:

So kann ein Testmodul aussehen

Dabei wird es in zwei logische Abschnitte geteilt:

1. Kopie aller Direktiven aus dem Hauptmodul
2. Zusätzliche Direktiven für das Testen

- `module-info.java`

```java
// Selber Modulname wie das Hauptmodule, zusätzlich "open"
open module com.xyz {
    requires java.logging;          // Aus dem Hauptmodul kopiert
    requires java.sql;              // - " -
    exports com.xyz;                // - " -

    requires org.junit.jupiter.api; // Test-spezifische Abhängigkeiten 
    requires org.assertj.core;      // - " -
    requires org.mockito;           // - " -
}
```

_Note: Copying parts from the main module descriptor manually is brittle. The "Java 9 compatible build tool" [pro](https://github.com/forax/pro) solves this by auto-merging a main and test module descriptor on-the-fly._

Notiz Jule: Ab hier plötzlich ENGLISCH

### White box modular testing with extra `java` command line options

The foundation tool `java` version 9+ provides command line options configure the Java module system "on-the-fly" at start up time.
Various test launcher tools allow additional command line options to be passed to the test runtime.

Here are the additional command line options needed to achieve the same modular configuration as above:

- `module-info.test`

```text
--add-opens                                   | "open module com.xyz"
  com.xyz/com.abc=org.junit.platform.commons  |
--add-opens                                   |
  com.xyz/com.xyz=org.junit.platform.commons  |

--add-reads                                   | "requires org.junit.jupiter.api"
  com.xyz=org.junit.jupiter.api               |
--add-reads                                   | "requires org.assertj.core"
  com.xyz=org.assertj.core                    |
--add-reads                                   | "requires org.mockito"
  com.xyz=org.mockito                         |
```

This option is already "supported" by some IDEs, at least they don't stumble compiling tests when a `module-info.test` file is present.

Notiz Jule: Ende ENGLISCH

## Zusammenfassung und ein Beispiel

- Wie organisieren wir also Tests in modularen Projekten?

Das hängt davon ab. Und zwar davon, was wir testen wollen.

Notiz Jule: Ab hier plötzlich ENGLISCH

Are you writing a standalone program that consumes modules without being designed to be re-usable itself?
Is it a library you want to distribute as a Java module?
Is your library distributed as a multi-release JAR?
Do you test how your library behaves on the [class-path and module-path](https://blog.joda.org/2018/03/jpms-negative-benefits.html)?

For a library, I'd suggest the following blueprint.

### Maven Blueprint

Suppose you want to write and test a module named `foo` in a typical single project setup:
*main* sources are in `src/main/java` directory, *white box test* sources in `src/test/java`.
The *black box* **i**ntegration **t**esting projects are located under `src/it` and they are executed by the [maven-invoker-plugin](https://github.com/apache/maven-invoker-plugin).
The simplified layout of [sormuras/testing-in-the-modular-world](https://github.com/sormuras/testing-in-the-modular-world) looks like:

```text
src
├── main
│   └── java
│       ├── foo
│       │   ├── PackageFoo.java
│       │   └── PublicFoo.java
│       └── module-info.java <------------------ module foo { exports foo; }
├── test
│   └── java                                .--- open module foo {
│       ├── foo                            /       exports foo;
│       │   └── PackageFooTests.java      /        requires org.junit.jupiter.api;
│       └── module-info.[java|test] <----<       }
└── it                                    \
    └── bar                                °---- --add-reads
        └── src                                    foo=org.junit.jupiter.api
            └── test                             --add-opens
                └── java                           foo/foo=org.junit.platform.commons
                    ├── bar
                    │   └── PublicFooTests.java
                    └── module-info.java <------ open module bar {
                                                   requires foo;
                                                   requires org.junit.jupiter.api;
                                                 }
```

```text
$ mvn verify
...
[INFO] Scanning for projects...
[INFO]
[INFO]------------------------------------------------------------------------
[INFO]Building testing-in-the-modular-world 1.0-SNAPSHOT
[INFO]------------------------------------------------------------------------
[INFO]---maven-compiler-plugin:3.8.0:compile(default-compile) @testing-in-the-modular-world ---
[INFO]---maven-compiler-plugin:3.8.0:testCompile(default-testCompile) @testing-in-the-modular-world ---
[INFO]---junit-platform-maven-plugin:0.0.10:launch-junit-platform(launch) @testing-in-the-modular-world ---
[INFO] Launching JUnit Platform...
[INFO] ╷
[INFO] └─ JUnit Jupiter ✔
[INFO]    └─ PackageFooTests ✔
[INFO]       ├─ accessPackageFooInModuleFoo() ✔
...
```

White box tests are done.

Now module `foo` is installed locally and the `maven-invoker-plugin` executes all integration tests:

```
...
[INFO]---maven-jar-plugin:2.4:jar(default-jar) @testing-in-the-modular-world ---
[INFO]---maven-invoker-plugin:3.1.0:install(integration-test) @testing-in-the-modular-world ---
[INFO]---maven-invoker-plugin:3.1.0:integration-test(integration-test) @testing-in-the-modular-world ---
[INFO] Building:bar/pom.xml
[INFO]           bar/pom.xml ......................................SUCCESS (5.8 s)
[INFO]
[INFO]---maven-invoker-plugin:3.1.0:verify(integration-test) @testing-in-the-modular-world ---
[INFO]-------------------------------------------------
[INFO] Build Summary:
[INFO]   Passed: 1, Failed: 0, Errors: 0, Skipped: 0
[INFO]-------------------------------------------------
[INFO]------------------------------------------------------------------------
[INFO]BUILD SUCCESS
[INFO]------------------------------------------------------------------------
```

_Note: although I favor the `MODULAR_PATCHED_TEST_COMPILE` test mode with a `module-info.java` describing the test module for white box testing, I recommend to stick with `MODULAR_PATCHED_TEST_RUNTIME` for now._
_Most build tools don't support two module descriptors on the path, nor do they understand module descriptors sharing a single name._

### Maven + JUnit Platform Maven Plugin

The [micromata/sawdust](https://github.com/micromata/sawdust) project shows all test modes in action.
Browse the sources of the sub-projects to see how to configure test mode.
See also the linked [Job log](https://travis-ci.org/micromata/sawdust) produced by Travis CI to verify you.

### Foundation tools `javac` and `java` (and `jshell`)

The [junit5-modular-world](https://github.com/junit-team/junit5-samples/tree/master/junit5-modular-world) sample project uses Java foundation tools to demonstrate testing the modular world.
This project's layout is based on proposals introduced by the [Module System Quick-Start Guide](http://openjdk.java.net/projects/jigsaw/quick-start).

## Resources

- [Feedback](https://github.com/sormuras/sormuras.github.io/issues) via GitHub

- _die Resourcen befinden sich im englischen Original_ : [testing-in-the-modular-world](https://sormuras.github.io/blog/2018-09-11-testing-in-the-modular-world)

## History

2018-10-11 Erste deutsche Version

Cheers and Happy Testing,
Christian

✅
