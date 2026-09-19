# Hot Peppers

A [NeoForge](https://neoforged.net/) mod for Minecraft 1.21.1 that adds a Hot Pepper crop -
harvest it carelessly and it bites back.

## Features

| Thing | Behaviour |
|---|---|
| Hot Pepper Crop | 4 growth stages, farmed like wheat but noticeably slower; bone meal advances it one stage |
| Wild Hot Pepper | A non-farmable pepper plant found growing naturally in jungle biomes - the intended way to forage your first seeds |
| Bare-handed harvest | Breaking a **mature** crop without a hoe or shears burns you for half a heart |
| Eating a pepper | Restores hunger and stacks a rank of the **Spicy** effect for 15 seconds |
| Spicy stacking | Each dose raises the rank, up to **Spicy III**. A dose that would exceed III ignites you instead of raising it further |
| Spicy regeneration | Speeds up natural healing while active, burning through your hunger bar the same way saturation-fueled regen does - it doesn't stop at the usual "well fed" threshold |
| Spicy food | Craft 1-3 peppers with any food to make it spicy: the food is renamed ("Spicy Bread" / "Very Spicy Bread" / "Extremely Spicy Bread") and gains a tooltip showing its rank. Eating it applies that many doses of Spicy at once |
| Seeds | Craft 1 pepper -> 1 seed, or forage a Wild Hot Pepper. Jungle grass also has a small chance to drop seeds, but that's off by default (configurable) |

## Building

Requires a JDK 21.

```sh
./gradlew build
```

To run a development client or dedicated server:

```sh
./gradlew runClient
./gradlew runServer
```

## Installation

Grab the built jar from `build/libs/` (or a release, once published) and drop it into your
`mods` folder alongside [NeoForge](https://neoforged.net/) 21.1.x for Minecraft 1.21.1.

## License

[GPL-3.0](LICENSE).
