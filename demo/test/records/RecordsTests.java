/*
 * Copyright (C) 2020 Christian Stein
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package records;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertLinesMatch;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

class RecordsTests {

  @Test
  void copyPoint() {
    var a = new Point(1, 2);
    var b = new Point(1, 3);

    assertEquals(b, Records.copy(a, Map.of("y", 3)));
  }

  @Test
  void printPointAsTextBlock() {
    var a = new Point(1, 2);

    assertEquals("Point[x=1, y=2]", a.toString());
    assertLinesMatch(
        List.of("Point", "\tx = 1", "\ty = 2"),
        Records.toTextBlock(a).lines().collect(Collectors.toList()));
  }

  @Test
  void printLineAsTextBlock() {
    var line = new Line("Silver", new Point(1, 2), new Point(3, 4));

    assertEquals("Line[name='Silver', p=Point[x=1, y=2], q=Point[x=3, y=4]]", line.toString());
    assertLinesMatch(
        List.of(
            "Line",
            "\tname = Silver",
            "\tp -> Point",
            "\t\t\tx = 1",
            "\t\t\ty = 2",
            "\tq -> Point",
            "\t\t\tx = 3",
            "\t\t\ty = 4"),
        Records.toTextBlock(line).lines().collect(Collectors.toList()));
  }
}
