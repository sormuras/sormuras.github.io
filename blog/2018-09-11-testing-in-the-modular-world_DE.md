# Testen im Modulsystem von Java

In diesem Blog geht es um die Organisation, das Auffinden und das Ausführen von Tests im Modulsystem von Java.
Es ist **keine** [Einführung in das Java Modulsystem](https://blog.codefx.org/java/java-module-system-tutorial/), auch bekannt als Projekt [Jigsaw](https://openjdk.java.net/projects/jigsaw/).

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

Für kleinere Projekte war das Vorgehen okay -- wobei schon damals viele Entwickler bei größeren Projekten die Vermischung von Produktions- und Testklassen als Nachteil dieses Ansatzes empfanden.
Denn der Aufwand für das finale Zusammenstellen des Produktes wurde mit der Zeit immer höher, weil die Testklassen dabei rausgefiltert werden mussten.

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

## Auf zu neuen Ufern: Hallo Jigsaw, Hallo Java Module!

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
Anders die mittlere Spalte `test/com.xyz` oder die Spalte _white box_; hier wurde nämlich eine Datei `module-info.[java|test]` hinzugefügt.
Bevor wir aber das Thema _white box_-Testen vertiefen, starten wir mit den beiden einfacheren Modulen.

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

Zunächst erweitern wir die [Zugriffstabelle](https://docs.oracle.com/javase/tutorial/java/javaOO/accesscontrol.html) aus dem Java Tutorial um eine Spalte.
Nämlich um eine Spalte, welche die Zugriffsmöglichkeiten aus einem fremden Modul beschreibt.

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

Damit wir das gewohnte Verhalten wieder herstellen, können wir entweder das komplette Java-Modulsystem (für's Testen) ausschalten.
Oder wir nutzen einen neuen Weg der es ermöglicht, dass sich Test- und Haupttypen logisch in ein und demselben Modul befinden.
Analog zu damals, als die Lösung `split packages` waren, die vom `class-path` aufgelöst wurden.
_Same same but different._ Nur, dass `split packages` in der modularen Welt von Jigsaw nicht mehr erlaubt sind.

## 🔥`module-info.[java|test]`🔥

Es gibt mindestens drei Möglichkeiten, wie man die strikten Grenzen des Java-Modulsystems beim Testen umgehen kann. 

### Zurück zum `classpath`

Alle `module-info.java`-Dateien löschen, oder diese zumindest vom Kompilieren ausschließen - und schon ignorieren die Tests die Grenzen des Modulsystems!
Dadurch werden, neben internen Details von Java selbst, auch Interna von anderen und eben der eignenen Bibliothek verfügbar.
Letzteres war das Ziel -- doch die Kosten, es zu erreichen, sind hoch.

Wie aber können wir die Grenzen des Modulsystems intakt lassen und trotzdem die internen Typen der eigenen Bibliothek testen?
Dazu mehr in den nächsten zwei Abschnitten.

### Modulares White-Box-Testen mit `module-info.java` in `src/test/java`

Die für den Testautor einfachste Variante besteht darin, eine Beschreibung für ein Testmodul anzulegen.
Die Beschreibung kann mit der gleichen Syntax geschehen, die bei _normalen_ Modulen eingesetzt wird.

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

_Notiz: Das Kopieren von Direktiven aus dem Hauptmodul is natürlich etwas mühselig und fehleranfällig.
Das [pro](https://github.com/forax/pro) Buildtool automatisiert diesen Schritt und erlaubt es ausschließlich die zusätzlichen Direktiven fürs Testen anzugeben._

### White box modular testing with extra `java` command line options

Neben der gerade beschriebenen Variante eines dediziertem Testmoduls, kann man auch mittels `java` Kommandozeilenparameter ans Ziel kommen.
Man konfiguriert so das Modulsystem quasi beim Starten der JVM. 
Die meisten Buildtools unterstützen die Angabe solcher `java` Kommandozeilenparameter beim Starten eines Testlaufs.

Um obiges Testmodul mittels `java` nachzubauen, braucht es folgende Parameter: 

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

## Zusammenfassung und ein Beispiel

- Wie organisiert man nun Tests in modularen Projekten?

Es ist in meine Augen notwendig, dass sowohl Black Box als auch White Box Tests geschrieben und ausgeführt werden.
Das [micromata/sawdust](https://github.com/micromata/sawdust) Projekt zeigt eine mögliche Struktur:

```text
├───modular-blackbox
│   └───src
│       ├───main
│       │   └───java    | module foo {}
│       │       └───foo
│       └───test
│           └───java    | open module bar { requires foo; ...jupiter.api; }
│               └───bar
│
├───modular-whitebox-patch-compile
│   └───src
│       ├───main
│       │   └───java    | module foo {}
│       │       └───foo
│       └───test
│           └───java    | open module foo { requires org.junit.jupiter.api; }
│               └───foo | --patch-module foo=src/main/java
│
└───modular-whitebox-patch-runtime
    └───src
        ├───main
        │   └───java    | module foo {}
        │       └───foo
        └───test
            └───java    | "module-info.test"
                └───foo | --patch-module foo=target/test/classes
```

Dazu kommt noch, dass man das Verhalten seines Moduls sowohl auf [class-path als auch module-path](https://blog.joda.org/2018/03/jpms-negative-benefits.html) testen sollte.

## Resources

- [Feedback](https://github.com/sormuras/sormuras.github.io/issues) via GitHub

- _die Resourcen befinden sich im englischen Original_ : [testing-in-the-modular-world](https://sormuras.github.io/blog/2018-09-11-testing-in-the-modular-world)

## History

2018-11-15 Erste deutsche Version

Cheers and Happy Testing,
Christian

✅
