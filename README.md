# Checkmate Live

An Android chess practice companion for games against bots and solo training.

## Current implementation

- Native Android app with a minimal Compose interface
- User-controlled Android screen capture with a persistent foreground notification
- Live 8×8 board-pattern detection from captured frames
- Starting-position calibration using the board's visible piece set and theme
- Piece classification and FEN construction
- Move-change confirmation before analysis
- Offline Stockfish 18 analysis on-device
- Floating panel with best-move arrows, evaluation, pause, stop, and recalibration controls
- White/Black side selection
- ARM64 and ARMv7 debug APKs built by GitHub Actions

## First-run steps

1. Install the APK matching the phone architecture. For a Samsung Galaxy A23, use the ARM64 APK.
2. Open Checkmate Live.
3. Grant the floating-panel permission.
4. Choose White or Black.
5. Tap **Start live analysis** and approve Android's screen-capture prompt.
6. Open a chess bot game at the standard starting position. Keep the board visible until the panel reports that calibration is complete.
7. Use the floating panel to pause, stop, or recalibrate.

The recognizer uses visual templates from the initial position. It can confuse visually similar pieces or fail with unusual board themes, animations, overlays, or changed piece sets. Always verify the detected position and suggested move. This is an early implementation, not a guaranteed-perfect chess vision system.

## Modes

- **Offline:** Stockfish analysis runs on-device and does not need an account or internet connection.
- **Online:** shown as a future option; no cloud service is configured yet.
- **Automatic fallback:** not implemented yet. Offline is the only connected analysis mode.

## Fair-play scope

Designed for solo practice and games against chess bots. Do not use engine assistance in human games or where a platform's rules prohibit it.

## Privacy

Screen capture starts only after Android's system permission prompt. The capture service runs visibly with a notification, and the floating panel has a stop control. Captured frames are processed on-device by the recognizer; they are not uploaded by this implementation.

## Build and licensing

GitHub Actions downloads the official Stockfish 18 Android binaries, verifies SHA-256 checksums, packages the matching ABI, and builds debug APKs. See `THIRD_PARTY_NOTICES.md`, `LICENSE-OpenCheps`, and `Stockfish-COPYING.txt` for third-party licensing information.
