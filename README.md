# TravelHub Mobile App

**Beyond the map** 🌍

TravelHub is a native Android travel discovery and social sharing application built with **Kotlin and Jetpack Compose**. It allows users to discover destinations, share travel experiences, save favorite places, write reviews, and interact with a travel community.

The application communicates with a **Django REST Framework** backend through REST APIs and uses JWT authentication for secure user sessions.

---

## Features

### 🔐 Authentication

* User registration and login
* JWT-based authentication
* Automatic access-token refresh
* Persistent login state
* Secure logout

### 🗺️ Destination Discovery

* Browse travel destinations
* Search destinations by name
* Filter destinations by category
* View detailed destination information
* View destination images
* View ratings and reviews
* View related community posts

### 📍 Destination Submission

* Submit new travel destinations
* Add destination descriptions and categories
* Add latitude and longitude
* Upload destination images
* Destinations remain pending until approved by an administrator

### 📝 Community Posts

* Create travel-related posts
* Attach destinations to posts
* Upload post images
* Edit posts
* Delete posts
* Like and unlike posts
* Browse the community feed

### ⭐ Favorites

* Save destinations
* Remove saved destinations
* View saved destinations in one place
* Favorite state synchronized across relevant screens

### ⭐ Reviews

* Submit destination ratings and reviews
* View community reviews
* Prevent duplicate reviews for the same destination

### 🧳 Services & Bookings

* Browse travel services
* View service details
* Complete a mock booking flow
* View booking confirmation
* View booking history
* Cancel mock bookings

> The booking system currently uses mock data because the corresponding backend booking API is still under development.

### 👤 Profile

* View user profile
* Edit biography
* Manage interests
* View user-related content
* Log out

---

## Technology Stack

### Android

| Technology            | Purpose                            |
| --------------------- | ---------------------------------- |
| Kotlin                | Application development            |
| Jetpack Compose       | UI development                     |
| Material 3            | UI components and design system    |
| Retrofit              | REST API communication             |
| OkHttp                | Networking and authentication      |
| kotlinx.serialization | JSON serialization/deserialization |
| Jetpack DataStore     | Local application/session storage  |
| Kotlin Coroutines     | Asynchronous operations            |
| ViewModel             | UI state management                |
| Navigation Compose    | Application navigation             |

### Backend

| Technology            | Purpose             |
| --------------------- | ------------------- |
| Django                | Backend framework   |
| Django REST Framework | REST API            |
| PostgreSQL            | Database            |
| SimpleJWT             | JWT authentication  |
| Cloudinary            | Media/image storage |
| Gunicorn              | Production server   |
| Whitenoise            | Static file serving |
| Render                | Backend deployment  |

---

## Architecture

The application follows a layered architecture based around **MVVM principles**.

```text
┌──────────────────────────────┐
│       Jetpack Compose UI     │
└──────────────┬───────────────┘
               │
               ▼
┌──────────────────────────────┐
│          ViewModel           │
│      UI State / Logic        │
└──────────────┬───────────────┘
               │
               ▼
┌──────────────────────────────┐
│         Repository           │
│    Data & Business Layer     │
└──────────────┬───────────────┘
               │
               ▼
┌──────────────────────────────┐
│       Retrofit / API         │
└──────────────┬───────────────┘
               │
               ▼
┌──────────────────────────────┐
│    Django REST Framework     │
└──────────────┬───────────────┘
               │
               ▼
┌──────────────────────────────┐
│         PostgreSQL           │
└──────────────────────────────┘
```

Repositories are defined through interfaces, allowing mock and real implementations to be exchanged without requiring changes to the UI layer.

---

## Authentication

TravelHub uses JWT authentication.

The authentication flow is:

```text
Login
  │
  ▼
Django API
  │
  ├── Access Token
  └── Refresh Token
          │
          ▼
     DataStore
```

The access token is automatically attached to authenticated requests using an OkHttp interceptor.

When an access token expires, the application attempts to obtain a new access token using the refresh token. If the refresh token is also invalid or expired, the user session is cleared and the application returns the user to the login screen.

---

## API Configuration

### Android Emulator

```text
http://10.0.2.2:8000/api/
```

### Physical Android Device

When running the backend locally and testing with a physical device on the same network:

```text
http://<PC-LAN-IP>:8000/api/
```

### Production

```text
https://<your-app>.onrender.com/api/
```

The API base URL can be configured according to the development environment.

---

## Project Structure

A simplified structure of the Android project is:

```text
app/
└── src/
    └── main/
        └── java/
            └── com/
                └── travelhub/
                    └── mobileApp/
                        ├── data/
                        │   ├── api/
                        │   ├── dto/
                        │   ├── repository/
                        │   └── local/
                        │
                        ├── domain/
                        │   └── models/
                        │
                        ├── ui/
                        │   ├── screens/
                        │   ├── components/
                        │   └── theme/
                        │
                        ├── navigation/
                        │
                        └── MainActivity.kt
```

The exact package and directory structure may evolve as development continues.

---

## Navigation

The application is organized into authentication and main application flows.

```text
Splash
 │
 ├── Onboarding
 │      │
 │      ├── Login
 │      │      │
 │      │      └── Main App
 │      │
 │      └── Register
 │             │
 │             └── Main App
 │
 └── Main App
```

The main application uses bottom navigation for:

```text
Home
Explore
Services
Favorites
Profile
```

---

## Example API Interface

```kotlin
interface SpotApi {

    @GET("spots/")
    suspend fun getSpots(
        @Query("search") search: String? = null
    ): Response<List<SpotDto>>

    @GET("spots/{id}/")
    suspend fun getSpotById(
        @Path("id") id: Int
    ): Response<SpotDto>

    @POST("spots/")
    suspend fun createSpot(
        @Body body: CreateSpotRequestDto
    ): Response<SpotDto>

    @Multipart
    @POST("spots/{id}/upload_image/")
    suspend fun uploadSpotImage(
        @Path("id") id: Int,
        @Part image: MultipartBody.Part
    ): Response<UploadImageResponseDto>
}
```

---

## Example Repository

```kotlin
interface SpotRepository {

    suspend fun getAllSpots(): Result<List<Spot>>

    suspend fun searchSpots(
        query: String,
        category: String?
    ): Result<List<Spot>>

    suspend fun createSpot(
        name: String,
        description: String,
        category: String,
        latitude: Double,
        longitude: Double
    ): Result<Spot>
}
```

The repository abstraction keeps API implementation details separate from the UI and ViewModel layers.

---

## Example Authentication Interceptor

```kotlin
class AuthInterceptor(
    private val preferences: AppPreferences
) : Interceptor {

    override fun intercept(
        chain: Interceptor.Chain
    ): Response {

        val token = runBlocking {
            preferences.getAccessToken()
        }

        val request = if (token != null) {
            chain.request()
                .newBuilder()
                .addHeader(
                    "Authorization",
                    "Bearer $token"
                )
                .build()
        } else {
            chain.request()
        }

        return chain.proceed(request)
    }
}
```

---

## Example UI State

```kotlin
sealed class HomeUiState {

    object Loading : HomeUiState()

    data class Success(
        val nearbySpots: List<Spot>,
        val trendingSpots: List<Spot>,
        val feed: List<Post>
    ) : HomeUiState()

    data class Error(
        val message: String
    ) : HomeUiState()
}
```

This approach allows the UI to clearly represent loading, successful, and failed API states.

---

## Design

TravelHub uses a modern, travel-oriented visual style based on a nature-inspired color palette.

| Element    | Color     |
| ---------- | --------- |
| Primary    | `#2E7D32` |
| Secondary  | `#4FC3F7` |
| Accent     | `#F4E1C1` |
| Background | `#FAFAFA` |
| Text       | `#333333` |
| Error      | `#B00020` |

The application uses the **Material 3 typography system** to maintain consistent text hierarchy and readability.

---

## Testing

The application was tested against the Django REST backend using both:

* Android Emulator
* Physical Android device connected through the local network

The main workflows tested include:

* Registration
* Login
* Invalid login handling
* Destination search
* Destination filtering
* Destination details
* Destination submission
* Image uploads
* Post creation
* Post editing
* Post deletion
* Post likes
* Favorites
* Reviews
* Duplicate review handling
* Mock booking flow
* Profile editing
* Logout
* Network failure handling

During integration testing, several issues were identified and resolved, including:

* Null handling for destinations without reviews
* Stale ViewModel data when revisiting screens
* Navigation back-stack inconsistencies
* API/network error handling

---

## Current Limitations

Some features are currently implemented using mock or simplified logic:

* Booking functionality uses mock data while the backend booking API is being developed.
* Nearby destination sorting is currently approximated on the client side.
* Trending destination logic is not yet fully backend-driven.
* Location selection currently relies on latitude and longitude values rather than an integrated map picker.
* Advanced offline functionality is not yet implemented.
* Push notifications are not currently supported.

---

## Future Improvements

Planned improvements include:

* Real booking and reservation API
* Google Maps integration
* Backend-powered nearby and trending destinations
* Push notifications
* Advanced destination filtering
* Multiple image management
* Offline caching and synchronization
* Social following system
* Expanded moderation tools
* Improved recommendation system

---

## Development

Clone the repository:

```bash
git clone https://github.com/DevendraSenevirathna/TravelHubMobile.git
```

Open the project in **Android Studio**, allow Gradle to synchronize, configure the API base URL, and run the application on an Android emulator or physical device.

---

## Related Project

TravelHub uses a separate Django REST Framework backend for API services, authentication, database operations, and media management.

```text
TravelHub Mobile
        │
        │ REST API
        ▼
TravelHub Backend
        │
        ├── Django REST Framework
        ├── PostgreSQL
        ├── JWT Authentication
        └── Cloudinary
```

---

## Team

TravelHub Mobile was developed by:

* **Devendra Senevirathna**
* **Shashinka Srimal**

The project was developed collaboratively using Git and GitHub, with development work divided across Android UI, API integration, backend integration, and application features.

---
