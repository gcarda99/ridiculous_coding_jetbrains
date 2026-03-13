# Ridiculous Coding — JetBrains Plugin

> Make your coding experience **ridiculous**. Inspired by [jotson/ridiculous_coding](https://github.com/jotson/ridiculous_coding) for VS Code.

A JetBrains IDE plugin that adds fun visual effects to your keystrokes while you type.

## Features

- 💥 Explosions on every keystroke
- ⚡ Screen shake on typing
- 🌈 Color particles flying from the caret
- 🔥 Combo multipliers (the faster you type, the more intense the effects)

## Supported IDEs

All JetBrains IDEs based on IntelliJ Platform:
- IntelliJ IDEA
- WebStorm
- PyCharm
- GoLand
- Rider
- CLion
- and more...

## Installation

> Coming soon on JetBrains Marketplace.

For now, build from source:

```bash
git clone https://github.com/gcarda99/ridiculous_coding_jetbrains.git
cd ridiculous_coding_jetbrains
./gradlew buildPlugin
```

Then install the generated `.zip` from `build/distributions/` via **Settings → Plugins → Install Plugin from Disk**.

## Development

Requirements:
- JDK 17+
- IntelliJ IDEA (for plugin development)

```bash
./gradlew runIde
```

## Contributing

PRs and ideas welcome! Open an issue to discuss new effects.

## License

MIT
