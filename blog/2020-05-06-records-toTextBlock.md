# Java Records - Records.toTextBlock

Let's enhance the upcoming Java programming language enhancement `record`.
Records are described in [JEP 359(Preview)](https://openjdk.java.net/jeps/359) (follow-up to [JEP draft(Second Preview)](https://openjdk.java.net/jeps/8242303)) and by Brian Goetz in [Data Classes and Sealed Types for Java](https://cr.openjdk.java.net/~briangoetz/amber/datum.html).
Looking forward to the Java release, that doesn't mandate the `--enable-preview` switch to use records anymore.

Starting with mini series about `record`s, I propose new methods to be added to the `java.lang.Record` type.
Or, these new methods can also reside in a `java.util.Records` helper taking the record object as a first parameter.
Yes, an externally hosted `Records` helper class is probably the best target.
For the time being.

**Beware!**

> ⚠ ["Records are addictive!!"](https://twitter.com/delabassee/status/1255497443568955397)
>
> _David Delabassée_

Sources can be found and copied from the [records](../demo/test/records) package.
[Feedback](https://github.com/sormuras/sormuras.github.io/issues) on the ideas and the code is much appreciated. 

#### Proposed Methods

- [Records.copy](2020-05-05-records-copy.md)
- [Records.toTextBlock](2020-05-06-records-toTextBlock.md)

## `Records.toTextBlock(Record)`

For record types, a `public String toString()` method implementation is generated for us.
It returns a "string representation of all the record components, with their names".
That's nice.
All in one line.
That's not so nice.
Especially when your record contains many components.
And components are sometimes records, as well.
Thus, their components contribute to the same line.

The proposed `Records.toTextBlock()` method also produces a string representation.
But with new line separators and tab characters inserted to achieve a tree-like view of all record components.

### Example for Record#toTextBlock()

Given the record declartion `R`:
```
record R(T0 n0, T1 n1, ... Tn nn) {}
```

Then `toString()` produces:
```
"R[n0=v0, n1=v1, ... nn=vn]" // produced by toString()
```

The string representation produced by `toTextBlock()` looks like:
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
Values that are of type `java.util.Collection<? extends Record>` could also be unrolled.
