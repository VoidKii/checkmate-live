# Checkmate Live

An Android chess practice companion for games against bots.

## Project status

Early development. The first milestone is a transparent, user-controlled screen-capture foundation. Later milestones add on-device board recognition and Stockfish analysis, followed by optional online analysis.

## Modes

- **Offline:** board processing and Stockfish analysis run on the device.
- **Online (optional):** cloud analysis can be added as an opt-in feature.
- **Automatic fallback:** use offline analysis when no network is available.

## Fair-play scope

Designed for solo practice and games against chess bots. Do not use engine assistance in games against human opponents or where the platform rules prohibit it.

## Privacy

Screen capture starts only after Android's system permission prompt. Capture must remain visible through the required foreground-service notification, and users can stop it at any time.

## Build

Android Studio / Gradle project files will be added in the next development milestone.