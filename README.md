# methodhandlescloner

## abstract

Attempt to quickly clone objects using `MethodHandle`s.

## why

Java 9+ gives us a `MethodHandles` class that allows us to tie together
`MethodHandle`s such that the runtime can apply many of the same optimizations
in compiled code that we get from normal Java code. Generic routines that
manipulate object fields have required in the past to either use reflection and
be slow or use more esoteric methods like runtime code generation or
`sun.misc.Unsafe`.

I'd like to do something like protostuff-runtime from protostuff/protostuff
with MethodHandles, so that we could infer Protocol Buffers schemas from normal
Java code (with annotations) and enjoy performance roughly in line with the
typical generated code encoders and decoders. I started this as a way to start
playing around with `MethodHandle`s in a simpler space.

## todo

* maybe do recursive cloning, currently references are copied and not
  traversed.
* Benchmark this vs a classic reflection-based approach and a generated code
  approach.
