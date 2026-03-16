# KidsTube

A safe, toddler-friendly YouTube viewing app for Android. Designed specifically for young children (ages 1-4), KidsTube provides a distraction-free, curated video experience with large touch targets and parental controls.

## Features

### Toddler-Friendly Interface
- **Large colorful thumbnails** - No text, just big pictures perfect for little fingers
- **2-column grid layout** - Easy to browse and tap
- **Full-screen video player** - Immersive viewing experience
- **Auto-hiding controls** - Clean playback, controls appear on tap
- **Bright, cheerful theme** - Engaging colors kids love

### Smart Content Curation
- **Dutch-focused content** - Prioritizes Dutch-language kids videos
- **Priority channels** - Features popular kids creators:
  - Steve and Maggie
  - Vlad & Niki
  - Diana & Roma
  - Like Nastya
  - Ryan's World
  - A for Adley
- **Classic Dutch shows** - Juf Roos, Bumba, Kabouter Plop, Peppa Pig NL, Paw Patrol NL, and more
- **Diverse categories** - Cartoons, educational, music, dance, animals, vehicles
- **Shuffled feed** - Content reshuffles on scroll to keep things fresh
- **6-hour cache** - Minimizes YouTube API usage

### Video Player
- **Full-screen playback** - No distractions
- **Playback controls** - Play/Pause, Previous, Next (appear on tap)
- **Related videos** - Horizontal thumbnail strip (hidden during playback)
- **Auto-play** - Automatically plays next video with countdown
- **Sound enabled by default** - Videos start with audio
- **Smart navigation** - Next button works even without related videos (plays random curated content)

### Parental Controls
- **PIN-protected settings** - Keep kids out of settings
- **Onboarding wizard** - Easy initial setup
- **Language preferences** - Configure allowed content languages
- **Channel blocking** - Block unwanted channels
- **Auto-play toggle** - Enable/disable automatic video advancement
- **Hidden settings button** - Tiny, low-opacity gear icon invisible to toddlers

## Architecture

[\![Architecture Diagram](https://img.shields.io/badge/View_Architecture-Excalidraw-6965db?style=for-the-badge&logo=excalidraw)](https://excalidraw.com/#json=hwO8exQ70hPljnfoe1dSF,XjCcm7PYowBJ4vvkbxyFzw)

KidsTube follows Clean Architecture with a multi-module structure:

```
app/                          # Main application module
├── MainActivity.kt           # Entry point, immersive mode
└── navigation/               # Navigation graph

core/
├── core-common/              # Shared utilities
├── core-data/                # Repository implementations
├── core-database/            # Room database, caching
├── core-datastore/           # DataStore preferences
├── core-domain/              # Business logic, use cases
├── core-network/             # YouTube API client
└── core-ui/                  # Shared Compose components

feature/
├── feature-home/             # Home screen with video grid
├── feature-player/           # Video player screen
├── feature-search/           # Search functionality
├── feature-parental/         # Settings & parental controls
└── feature-onboarding/       # First-run setup wizard
```

## Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose + Material 3
- **Architecture**: MVVM + Clean Architecture
- **DI**: Hilt
- **Navigation**: Jetpack Navigation Compose
- **Networking**: Retrofit + Moshi
- **Database**: Room
- **Preferences**: DataStore
- **Image Loading**: Coil
- **Video**: WebView with YouTube mobile site

## Requirements

- Android 8.0 (API 26) or higher
- YouTube Data API v3 key

## Setup

1. **Clone the repository**
   ```bash
   git clone git@github.com:juanmarques/KidsTube.git
   cd KidsTube
   ```

2. **Get a YouTube API key**
   - Go to [Google Cloud Console](https://console.cloud.google.com/)
   - Create a new project or select existing
   - Enable "YouTube Data API v3"
   - Create an API key (restrict to Android app recommended)

3. **Configure the API key**

   Create or edit `local.properties` in the project root:
   ```properties
   YOUTUBE_API_KEY=your_api_key_here
   ```

4. **Build and run**
   ```bash
   ./gradlew assembleDebug
   ```

   Or open in Android Studio and run on device/emulator.

## Usage

### First Run
1. App opens to onboarding wizard
2. Set a parental PIN (4-6 digits)
3. Choose content languages (Dutch recommended)
4. Done! Kids can start watching

### For Kids
- Tap any thumbnail to watch
- Tap video to show/hide controls
- Use Previous/Next buttons to navigate
- Tap back arrow to return home
- Pull down to refresh content

### For Parents
- Tap tiny gear icon (top-right, barely visible)
- Enter PIN to access settings
- Configure languages, block channels, toggle auto-play
- Access search to find specific content

## Project Structure Details

### Core Modules

| Module | Purpose |
|--------|---------|
| `core-domain` | Video/Category models, use cases, repository interfaces |
| `core-data` | Repository implementations, channel allowlist |
| `core-network` | YouTube API service, DTOs |
| `core-database` | Room DB, search cache, video cache |
| `core-datastore` | User preferences, PIN storage |
| `core-ui` | VideoCard, LoadingState, theme |

### Feature Modules

| Module | Purpose |
|--------|---------|
| `feature-home` | Main grid of video thumbnails |
| `feature-player` | Full-screen video player |
| `feature-search` | Search videos (parent access) |
| `feature-parental` | Settings, PIN entry |
| `feature-onboarding` | First-run setup |

## Content Categories

### Priority (Real Kids Channels)
- Steve and Maggie Nederlands
- Vlad en Niki Nederlands
- Diana en Roma Nederlands
- Like Nastya Nederlands
- Ryan's World Nederlands
- Kids Diana Show Nederlands
- A for Adley

### Dutch Shows
- Juf Roos, Bumba, Kabouter Plop
- Woezel & Pip, Nijntje
- Masha en de Beer, Peppa Pig NL
- Paw Patrol NL, Bing NL
- Sesamstraat, Het Zandkasteel
- Maya de Bij, K3, Mini Disco
- CoComelon NL, Blippi NL
- Disney Junior NL, Efteling

### General Categories
- Tekenfilms (Cartoons)
- Kinderliedjes (Kids songs)
- Leren (Educational)
- Dieren (Animals)
- Speelgoed (Toys)
- Dansen (Dance)
- Voertuigen (Vehicles)

## API Quota Management

The app includes built-in quota tracking:
- Tracks daily YouTube API usage
- Caches search results for 6 hours
- Rotates through content pools to distribute queries
- Designed to stay well under YouTube's 10,000 unit daily limit

## Security

- PIN is stored as salted SHA-256 hash
- No plaintext password storage
- Settings protected behind PIN verification
- Minimal permissions required

## License

MIT License - see [LICENSE](LICENSE) for details.

## Contributing

Contributions welcome! Please read our contributing guidelines before submitting PRs.

## Acknowledgments

- Built for my toddler who loves watching kid-friendly content
- Inspired by YouTube Kids but with better parental control
- Thanks to all the amazing Dutch kids content creators

---

**Note**: This app is not affiliated with YouTube or Google. It uses the official YouTube Data API v3.
