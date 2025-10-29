# Absolute Solver Mod for Minecraft 1.21.5

A Minecraft mod that adds the Absolute Solver abilities from the Murder Drones series. This mod gives players five powerful abilities: Translate, Scale, Rotate, Edit, and Null.

## Features

### Translate (Telekinesis)
- **Right-click** on an entity or block to grab it
- Move your crosshair to position the grabbed object
- **Right-click again** to throw the object
- **Sneak + Right-click** to release the grabbed object
- Range: 64 blocks

### Scale (Breaking/Damaging)
- **Right-click** on a block to instantly break it
- **Right-click** on an entity to deal massive damage (20 hearts)
- Useful for breaking structures and defeating enemies
- Range: 48 blocks

### Rotate
- **Right-click** on a block to rotate it (if rotatable)
- **Right-click** on an entity to spin it rapidly
- Rotates blocks that support rotation (like stairs, logs, etc.)
- Can break blocks by rotating them too much
- Range: 48 blocks

### Edit
- **Right-click** on an item to duplicate it
- **Right-click** on a damaged entity/player to repair them
- **Right-click** on a block to copy it nearby
- **Sneak + Right-click** on a block to dismantle it
- Range: 48 blocks

### Null (Black Hole)
- **Right-click** to shoot a black hole projectile
- **Sneak + Right-click** to shoot a smaller black hole
- Black holes attract and delete nearby entities and items
- Gradually expands after spawning
- Lasts 10 seconds
- Destroys blocks in its vicinity

## Installation

1. Make sure you have Minecraft 1.21.5 installed
2. Install Fabric Loader 0.16.10 or later
3. Install Fabric API
4. Place the mod jar in your mods folder
5. Launch Minecraft

## Building

This mod uses Gradle (required for Fabric mods):

```bash
# Windows
gradlew build

# Linux/Mac
./gradlew build
```

The built jar will be in `build/libs/`

## How to Get Items

Currently, items can be obtained via creative mode or commands:
- `/give @p absolute-solver:translate`
- `/give @p absolute-solver:scale`
- `/give @p absolute-solver:rotate`
- `/give @p absolute-solver:edit`
- `/give @p absolute-solver:null`

## Requirements

- Minecraft: 1.21.5
- Fabric Loader: 0.16.10+
- Fabric API
- Java 21+

## License

MIT License - feel free to modify and use as you please!

## Credits

Based on the Absolute Solver abilities from the Murder Drones series by GLITCH Productions.

