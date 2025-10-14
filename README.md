# ⏰ SHAREYOURTIME

<div align="center">
  <img src="https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" alt="Android" />
  <img src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white" alt="Java" />
  <img src="https://img.shields.io/badge/Firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=black" alt="Firebase" />
  <img src="https://img.shields.io/badge/Google_Maps-4285F4?style=for-the-badge&logo=google-maps&logoColor=white" alt="Google Maps" />
</div>

<div align="center">
  <h3>Comprehensive Android Productivity App for Time Management</h3>
  <p><strong>Features:</strong> Alarms, Timers, World Clock, User Profiles & More</p>
</div>

---

## 📋 Table of Contents
- [Overview](#overview)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Installation](#installation)
- [Branches](#branches)
- [Screenshots](#screenshots)
- [Key Components](#key-components)
- [Permissions](#permissions)
- [Localization](#localization)
- [Contributing](#contributing)

## 🎯 Overview

SHAREYOURTIME is a comprehensive Android productivity application designed for efficient time management. The app combines multiple time-related features into a single, user-friendly interface with modern Android development practices.

### Key Highlights
- 🚀 **Full-Featured Time Management** with alarms, timers, and world clock
- 🔐 **Secure Authentication** using Firebase Auth
- 🗺️ **Google Maps Integration** for location-based features
- 📱 **Modern Android Architecture** with Jetpack components
- 🌍 **Multilingual Support** (English & Spanish)
- 🎨 **Material Design** with light/dark theme support

## ✨ Features

### 🔐 Authentication & User Management
- **Firebase Authentication** for secure login/registration
- **Profile Management** with camera-based photo uploads
- **Firebase Storage** integration for user data
- **User Preferences** and settings synchronization

### ⏰ Time Management
- **Alarm System** with notifications and scheduling
- **Timer & Stopwatch** with lap functionality
- **World Clock** with multi-timezone support
- **Background Services** for reliable operation

### 🗺️ Location & Maps
- **Google Maps Integration** for location-based features
- **Time Zone API** for accurate time calculations
- **Location Services** for enhanced functionality

### 🎨 User Interface
- **Material Design** components and guidelines
- **Light/Dark Theme** switching
- **Homescreen Widget** with real-time updates
- **Responsive Design** for various screen sizes

### 🌍 Internationalization
- **English & Spanish** localization
- **Dynamic Language** switching
- **Cultural Adaptation** for different regions

## 🛠️ Tech Stack

### Core Technologies
- **Java** - Primary programming language
- **Android SDK** - Native Android development
- **Gradle** - Build automation and dependency management

### Android Jetpack
- **Navigation Component** - Fragment navigation
- **ViewModel** - UI-related data management
- **LiveData** - Observable data holders
- **Room** - Local database (if implemented)

### Backend Services
- **Firebase Authentication** - User management
- **Cloud Firestore** - Real-time database
- **Firebase Storage** - File and image storage
- **Firebase Analytics** - Usage tracking

### External APIs
- **Google Maps SDK** - Maps and location services
- **Time Zone API** - Accurate time zone calculations
- **Google Play Services** - Enhanced functionality

### UI/UX
- **Material Design** - Google's design system
- **Custom Widgets** - Homescreen integration
- **Responsive Layouts** - Multi-screen support

## 🏗️ Architecture

### Design Patterns
- **MVVM (Model-View-ViewModel)** - Clean separation of concerns
- **Repository Pattern** - Centralized data management
- **Observer Pattern** - Reactive programming with LiveData

### Project Structure
```
app/
├── src/main/java/
│   ├── activities/          # Activity classes
│   ├── fragments/           # Fragment classes
│   ├── services/            # Background services
│   ├── utils/               # Utility classes
│   ├── models/              # Data models
│   └── adapters/            # RecyclerView adapters
├── src/main/res/
│   ├── layout/              # XML layouts
│   ├── values/              # Strings, colors, styles
│   └── drawable/            # Icons and images
└── src/main/AndroidManifest.xml
```

## 🚀 Installation

### Prerequisites
- **Android Studio** Arctic Fox or later
- **Android SDK** 21+ (Android 5.0)
- **Java Development Kit** (JDK 8 or later)
- **Firebase project** setup
- **Google Maps API key**

### Setup Instructions

1. **Clone the repository**
   ```bash
   git clone https://github.com/AsierCardoso/SHAREYOURTIME.git
   cd SHAREYOURTIME
   ```

2. **Open in Android Studio**
   - Launch Android Studio
   - Select "Open an existing project"
   - Navigate to the cloned directory

3. **Firebase Configuration**
   - Create a Firebase project at [Firebase Console](https://console.firebase.google.com)
   - Download `google-services.json` and place it in the `app/` directory
   - Enable Authentication, Firestore, and Storage in Firebase Console

4. **Google Maps Setup**
   - Get a Google Maps API key from [Google Cloud Console](https://console.cloud.google.com)
   - Add the API key to `AndroidManifest.xml`:
   ```xml
   <meta-data
       android:name="com.google.android.geo.API_KEY"
       android:value="YOUR_API_KEY" />
   ```

5. **Build and Run**
   - Sync project with Gradle files
   - Build the project (Build → Make Project)
   - Run on device or emulator

## 🌿 Branches

### Main Branch (Current)
- **Full-featured version** with all capabilities
- **Firebase integration** for authentication and data storage
- **Google Maps integration** for location-based features
- **Complete user management** system

### Alternative Branch (Legacy)
- **Simplified version** without Firebase
- **No authentication** system
- **No Google Maps** integration
- **Local-only functionality** for basic time management

### Switching Between Versions
```bash
# Switch to full-featured version
git checkout main

# Switch to simplified version
git checkout [branch-name]
```

## 📱 Screenshots

<div align="center">
  <img src="screenshots/login.png" alt="Login Screen" width="200" />
  <img src="screenshots/main.png" alt="Main Screen" width="200" />
  <img src="screenshots/alarms.png" alt="Alarms" width="200" />
  <img src="screenshots/timer.png" alt="Timer" width="200" />
  <img src="screenshots/world-clock.png" alt="World Clock" width="200" />
  <img src="screenshots/maps.png" alt="Maps Integration" width="200" />
</div>

## 🎯 Key Components

### Activities
- **`LoginActivity`** - User authentication and login
- **`RegisterActivity`** - New user registration
- **`MainActivity`** - Main navigation hub and dashboard
- **`MapActivity`** - Google Maps integration and location features

### Fragments
- **`AlarmFragment`** - Alarm creation and management
- **`TimerFragment`** - Timer functionality and controls
- **`StopwatchFragment`** - Stopwatch with lap functionality
- **`WorldClockFragment`** - Multi-timezone clock display
- **`ProfileFragment`** - User profile management

### Services
- **`AlarmService`** - Background alarm notifications
- **`TimerService`** - Timer management and updates
- **`WorldClockWidget`** - Homescreen widget service

### Utils & Managers
- **`FirestoreManager`** - Database operations and synchronization
- **`TimeZoneManager`** - Time zone calculations and conversions
- **`PreferencesManager`** - App settings and user preferences
- **`NotificationManager`** - Notification handling and display

## 🔐 Permissions

### Required Permissions
```xml
<!-- Camera access for profile photos -->
<uses-permission android:name="android.permission.CAMERA" />

<!-- Internet access for Firebase and Maps -->
<uses-permission android:name="android.permission.INTERNET" />

<!-- Location access for Maps features -->
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />

<!-- Notification permissions for alarms -->
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />

<!-- Exact alarm scheduling -->
<uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" />
```

### Runtime Permissions
- **Camera** - Requested when taking profile photos
- **Location** - Requested when using Maps features
- **Notifications** - Requested for alarm functionality

## 🌍 Localization

### Supported Languages
- **English (en)** - Default language
- **Spanish (es)** - Full translation support

### Localization Features
- **Dynamic Language Switching** - Change language without restart
- **Cultural Adaptation** - Date/time formats for different regions
- **RTL Support** - Right-to-left language support (if needed)

### Adding New Languages
1. Create new resource directories (e.g., `values-fr/`)
2. Translate `strings.xml` files
3. Test with different locales
4. Update language selection in settings

## 🚀 Future Enhancements

- [ ] **Widget Customization** - More widget sizes and styles
- [ ] **Cloud Sync** - Cross-device synchronization
- [ ] **Social Features** - Share timers and achievements
- [ ] **Advanced Analytics** - Usage statistics and insights
- [ ] **Voice Commands** - Hands-free operation
- [ ] **Wear OS Support** - Smartwatch integration
- [ ] **Backup & Restore** - Data export/import functionality

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### Development Guidelines
- Follow Android coding standards
- Use meaningful commit messages
- Test on multiple devices and Android versions
- Update documentation for new features

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👨‍💻 Author

**Asier Cardoso**
- GitHub: [@AsierCardoso](https://github.com/AsierCardoso)
- LinkedIn: [asiercardoso](https://linkedin.com/in/asiercardoso)
- Email: asiercmonasterio@gmail.com

## 🙏 Acknowledgments

- **Firebase** for backend services and authentication
- **Google Maps** for location and mapping features
- **Material Design** for UI components and guidelines
- **Android community** for resources, tutorials, and support
- **Open source contributors** for libraries and tools

---

<div align="center">
  <p>Built with ❤️ for Android users worldwide</p>
  <p>
    <a href="#top">⬆️ Back to Top</a>
  </p>
</div>
