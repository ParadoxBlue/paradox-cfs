# Paradox CFS
Paradox CFS is a simple configuration system that allows for quick syntax when working with configurations.
It has basic built in serializaton for types, as long as recursively down the line all constructor parameters
end in primitive types or Strings.

## Usage
### Instantiation
Using the configuration system was designed to be as simplistic and intuitive as possible, while maintaining usability,
and also maintaining that sweet, sweet shorthand syntax.

```kotlin
val config = Config()
```

This will create a blank config for you to be able to write your stuff to.

```kotlin
val config = Config(File("Some/Path"))
```

This will create a config based on the file you passed in. It will read the file, and parse it to a config.

### Syntax

One of the more intuitive things about Paradox CFS is that it has somewhat goofy but quick syntax.

```kotlin
val config = Config()
val int = config.int("some.key")
val bool = config.bool("another.key")
//... all the way through number, double, float, long, byte, short, string, list, map, and section.
```

These are the various ways of accessing data in a config.

```kotlin
val config = Config {
    "key" to "value"
    "another key" to 5
    "a section" to Config {
        "nesting stuff" to "works!"
        "spaces are cool" to Config {
            "you're teling me =P" to "Wooh!"
        }
    }
}
```

This is one of the ways to actually write to a config. Yeah, this is perfectly valid syntax.

## Output

```
key: "value"
another key: 5
a section: [
	spaces are cool: [
		you're teling me =P: "Wooh!"
	]
	nesting stuff: "works!"
]
```

## Note
ParadoxCFS doesn't care much about spacing, new lines, et cetera. This is also perfectly valid in parsing..

```
key: "value" another key: 5 a section: [spaces are cool: [you're teling me =P: "Wooh!" ] nesting stuff: "works!" ]
```

Please note: Internally, this is using a HashMap, so things might not be ordered in the config correctly. Other than that, should be good to go.
Please report all errors or bugs you find!
