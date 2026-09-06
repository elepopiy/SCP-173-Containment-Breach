# SCP-173 Containment Breach

**SCP-173 Containment Breach** is an independent SCP-inspired first-person horror game developed with **Blitz3D x95**.

The player is trapped inside a containment facility during an SCP-173 containment breach. The facility contains multiple rooms, automatic gates, containment equipment, environmental areas, and a fully interactive SCP-173 system.

> **CONTAINMENT BREACH DETECTED**

---

## Features

### SCP-173 AI

SCP-173 is the main threat of the game.

The creature uses a custom movement and visibility system:

* SCP-173 stops moving when directly observed.
* SCP-173 moves when it is outside the player's view.
* SCP-173 can rapidly approach the player.
* SCP-173 uses world collision checks.
* SCP-173 can interact with automatic gates.
* SCP-173 has a dedicated attack state.
* SCP-173 can be carried before activation.
* SCP-173 has separate inactive and active states.

---

## Blink System

The player can manually blink using:

**SPACE**

During a blink:

* SCP-173 can move.
* A limited movement distance is applied.
* The blink lasts for a short period of time.
* The player's view temporarily becomes unavailable to the SCP.

The blink system is designed to make encounters with SCP-173 unpredictable.

---

## Containment System

SCP-173 initially exists inside a containment crate.

The player can interact with SCP-173 and transport it to a containment machine.

### Containment Sequence

1. Locate SCP-173.
2. Pick up SCP-173.
3. Carry SCP-173 to the containment machine.
4. Place SCP-173 into the machine.
5. Activate the machine.
6. Complete the activation sequence.
7. SCP-173 becomes active.

---

## Containment Machine

The facility contains a dedicated containment machine.

The machine includes:

* Main body
* Front panel
* Side panels
* Back panel
* Platform
* Control panel
* Screen
* Activation button
* Machine light
* Alarm light

The machine has its own activation state and animation timer.

---

## Facility

The main facility contains multiple interconnected areas.

Current environments include:

* Main corridor
* Side room
* Containment areas
* Automatic gates
* Storage crate
* Containment machine
* Indoor lighting
* Environmental decorations
* Structural details

---

## Automatic Gates

The facility contains automatic security gates.

Gates react to:

* Player proximity
* SCP-173 proximity

The gate system also integrates with the game's world collision system.

---

## Outdoor Environment

The facility is surrounded by an outdoor environment.

Current outdoor elements include:

* Grass
* Trees
* Rocks
* Bushes
* Houses
* Bridge
* Fence
* Clouds
* Outdoor lamps
* Environmental decorations

---

## Player

The game uses a first-person player controller.

### Movement

* WASD movement
* Sprinting
* Mouse look
* Collision detection

### Interaction

**E** is used for interaction with important objects.

### Flashlight

The player has access to a flashlight for navigating darker areas of the facility.

---

## Audio

The game currently includes multiple audio systems.

### Footsteps

Separate sounds are used for:

* Walking
* Running

### Music

The game supports multiple music states:

* Main OST
* Theme music
* Containment machine activation music

---

## Lighting

The facility uses a custom lighting setup.

Current lighting features include:

* Ceiling lights
* Ambient lighting
* Creepy lighting
* Outdoor lighting
* Flashlight

The creepy lighting system can dynamically change its intensity to increase the atmosphere.

---

## Texture System

The game uses a custom UV-based texture system.

### Wall Textures

Wall textures use manually generated UV coordinates.

### Ground Textures

Ground textures use UV tiling instead of texture scaling.

### Grass Textures

The outdoor grass system also uses UV tiling.

This prevents large surfaces from excessively stretching the original textures.

---

## Geometry Improvements

The wall geometry has been optimized to reduce unnecessary surfaces.

The custom wall mesh system removes unnecessary:

* Top faces
* Bottom faces
* Internal surfaces

This helps reduce unnecessary geometry and minimizes overlapping surfaces.

---

## Z-Fighting Reduction

Several geometry changes were made to reduce visual problems caused by overlapping surfaces.

The custom wall and ground mesh systems are designed to minimize unnecessary coplanar geometry.

---

## Navigation

The project contains an A* navigation system and navigation grid infrastructure for future SCP pathfinding improvements.

The navigation system is designed around:

* Grid-based navigation
* Open nodes
* Closed nodes
* Parent nodes
* G/H/F scores
* Generated paths

Further pathfinding improvements are planned for future versions.

---

## Controls

| Key     | Action            |
| ------- | ----------------- |
| `W`     | Move Forward      |
| `A`     | Move Left         |
| `S`     | Move Backward     |
| `D`     | Move Right        |
| `SHIFT` | Sprint            |
| `SPACE` | Blink             |
| `E`     | Interact          |
| `F11`   | Toggle Fullscreen |
| `Mouse` | Look              |
| `ESC`   | Exit              |

---

## Technical Information

| Property    | Value                |
| ----------- | -------------------- |
| Engine      | Blitz3D x95          |
| Language    | BlitzBasic / Blitz3D |
| Platform    | Windows              |
| Resolution  | 1920 × 1080          |
| Color Depth | 32-bit               |
| Perspective | First Person         |

---

## Project Structure

A simplified project structure is:

```text
SCP-173 Containment Breach/
│
├── GFX/
│   ├── Wall.jpg
│   ├── Ground.jpg
│   └── Grass.jpg
│
├── SFX/
│   ├── Walk.mp3
│   ├── Run.mp3
│   ├── Ost.mp3
│   └── Theme.mp3
│
├── SCP-173/
│   └── source/
│       ├── 173.3ds
│       ├── 173_Spec.jpg
│       └── 173texture.jpg
│
└── Main Game Source
```

---

## Current Version

**Version:** `1.0.0`

**Status:** Initial Release

The project is actively being developed.

---

## Known Limitations

The current version is an early release and several systems are still being expanded.

Planned improvements include:

* More advanced SCP-173 pathfinding
* Additional facility areas
* More animations
* Improved environmental detail
* Expanded audio systems
* Additional containment mechanics
* More SCP-related gameplay systems
* Improved atmosphere
* Additional gameplay events

---

## Roadmap

### v1.x

* Expand the facility
* Improve SCP-173 navigation
* Add more environmental details
* Improve animations
* Expand audio
* Improve containment gameplay

### Future

* Larger facility
* More SCP entities
* More gameplay mechanics
* More advanced AI
* Additional containment scenarios
* Expanded story

---

## Credits

**Game:** SCP-173 Containment Breach

**Developer:** Görkem Doruk Tan

**Engine:** Blitz3D x95

**SCP Universe:** SCP Foundation / SCP Wiki

---

## Disclaimer

SCP-173 Containment Breach is an independent fan project inspired by the SCP Foundation universe.

This project is not an official SCP Foundation game.

SCP Foundation and related SCP concepts belong to their respective creators and contributors.

---

## License

This project's source code and original assets are subject to their respective licenses and permissions.

Third-party assets remain the property of their original creators.

---

> **DO NOT BLINK.**
