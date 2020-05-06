# Java Records - Records.toTextBlock

Let's enhance the upcoming Java programming language enhancement `record` with a `toTextBlock` method.
This is the second part of the mini series about `record`s.

#### Proposed Methods

- [Records.copy](2020-05-05-records-copy.md)
- [Records.toTextBlock](2020-05-06-records-toTextBlock.md)

## `Records.toTextBlock(Record)`

For record types, a `public String toString()` method implementation is generated for us.
It returns a _"string representation of all the record components, with their names"_.
That's nice.
All in one line.
That's not so nice.
Especially when your record contains many components.
And components are sometimes records, as well.
Thus, their components contribute to the same line.

The proposed `Records.toTextBlock(Record)` method also produces a string representation.
But with new line separators and tab characters inserted to achieve a tree-like view of all record components.

### Pseudo-Code Usage Sample

Given the record declartion `R`:
```
record R(T0 n0, T1 n1, ... Tn nn) {}
```

The generated `toString()` method produces:
```
"R[n0=v0, n1=v1, ... nn=vn]"
```

The string representation produced by `toTextBlock(Record)` looks like:
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
Let's assume that type `T2` is declared as a `record`.

While `Record.toString()` represents that within the same line:

```
"R[n0=v0, n1=v1, n2=T2[nA=w0, nB=w1, ..., nz=wz] ... nn=vn]"
```

```
R
\tn0 = v0
\tn1 = v1
\tn2 = T2
\t\tnA = w0
\t\tnB = w1
...
\tnn = vn
"""
```

### Proof Of Concept Implementation

_TODO_
