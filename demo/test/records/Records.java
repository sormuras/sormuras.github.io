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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.function.Function;

/** Record-related helper. */
public class Records {

  /**
   * An informative annotation type used to indicate that a class type declaration is intended to be
   * transmuted into a {@code record} as defined by JEP 359, soon.
   */
  @Target(ElementType.TYPE)
  @Retention(RetentionPolicy.RUNTIME)
  public @interface Record {}

  /** Returns a new instance based on a given template record object and a value override map. */
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

  /** Returns a multi-line string representation of the given object. */
  public static String toTextBlock(Object object) {
    return toTextBlock(0, object, "\t", Class::getSimpleName, true);
  }

  /** Returns a multi-line string representation of the given object. */
  public static String toTextBlock(
      int level,
      Object object,
      String indent,
      Function<Class<?>, String> caption,
      boolean sortComponentsByName) {

    var lines = new ArrayList<String>();
    if (level == 0) lines.add(caption.apply(object.getClass()));

    var fields = object.getClass().getDeclaredFields();
    if (sortComponentsByName) Arrays.sort(fields, Comparator.comparing(Field::getName));

    for (var field : fields) {
      if (field.isEnumConstant()) continue;
      if (field.isSynthetic()) continue;
      if (Modifier.isStatic(field.getModifiers())) continue;
      if (!Modifier.isPrivate(field.getModifiers())) continue;
      if (!Modifier.isFinal(field.getModifiers())) continue;
      var name = field.getName();
      Method method;
      try {
        method = object.getClass().getDeclaredMethod(name);
      } catch (NoSuchMethodException e) {
        continue; // record component accessor is missing
      }
      if (method.isBridge()) continue;
      if (method.isDefault()) continue;
      if (method.isSynthetic()) continue;
      if (method.isVarArgs()) continue;
      if (!method.getReturnType().equals(field.getType())) continue;
      if (Modifier.isStatic(method.getModifiers())) continue;
      if (!Modifier.isPublic(method.getModifiers())) continue;
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
