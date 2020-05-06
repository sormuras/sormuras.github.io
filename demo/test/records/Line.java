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

import java.util.Objects;
import java.util.StringJoiner;

@Records.Record
public final class Line implements Cloneable {

  private final String name;
  private final Point p;
  private final Point q;

  public Line(String name, Point p, Point q) {
    this.name = name;
    this.p = p;
    this.q = q;
  }

  public String name() {
    return name;
  }

  public Point p() {
    return p;
  }

  public Point q() {
    return q;
  }

  @Override
  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Line line = (Line) o;
    return name.equals(line.name) && p.equals(line.p) && q.equals(line.q);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, p, q);
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", Line.class.getSimpleName() + "[", "]")
        .add("name='" + name + "'")
        .add("p=" + p)
        .add("q=" + q)
        .toString();
  }
}
