Replace Micronaut Nullability annotations with JSpecify Nullability annotations.

JSpecify is part of the Micronaut BOM. If this is a Micronaut 4 application, you need to add the Jspecify dependency.

For an application using the Micronaut Gradle version catalogue.

You can add the dependency by adding something like this in the `dependencies` block of the build file.

```kotlin
    implementation(mn.jspecify)
```

For applications that do not use the Micronaut Gradle version catalogue, you can define the JSpecify dependency using group id and artifact id:

```kotlin
   implementation("org.jspecify:jspecify")
```

For Maven applications, you can define the JSpecify by adding the following to the `dependencies` block in the `pom.xml`:

```xml
<dependency>
    <groupId>org.jspecify</groupId>
    <artifactId>jspecify</artifactId>
</dependency>
```

Replace usages of `io.micronaut.core.annotation.Nullable` with `org.jspecify.annotations.Nullable`

Replace usages of `io.micronaut.core.annotation.NonNull` with `org.jspecify.annotations.NonNull`

When you find a Micronaut nullability annotation in a fully qualified type, the syntax is a bit different from JSpecify.

For example, this code is valid with Micronaut nullability annotations:

```java
public ReadBuffer adapt(@NonNull io.micronaut.core.io.buffer.ByteBuffer<?> buffer) {
```

With the JSpecify nullability annotation, you need to change this code to:

```java
public ReadBuffer adapt(io.micronaut.core.io.buffer.@NonNull ByteBuffer<?> buffer) {
```

For inner classes, Micronaut nullability annotations, you could do:

```java
public FileChangedEvent(@NonNull Path path, @NonNull WatchEvent.Kind eventType) {
```

With JSpecify, you will need to change the code to:

```java
public FileChangedEvent(@NonNull Path path, WatchEvent.@NonNull Kind eventType) {
```