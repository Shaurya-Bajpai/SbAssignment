# Face Recognition Attendance App

An Android attendance management application using **face recognition** to verify staff identity before marking attendance. It supports separate Admin and Staff flows, local Room persistence, DataStore session persistence, and location capture only after successful face verification.

---

# 1. Features

### Admin

- Admin login and persistent session.
- View all registered staff.
- Register staff with:
  - Name
  - Employee ID
  - Face image
  - FaceNet embedding
- View an individual staff profile.
- View attendance history.
- View attendance:
  - Selfie
  - Date
  - Time
  - Latitude
  - Longitude
- Logout.

### Staff

- Staff login and persistent session.
- View registered staff.
- Select a staff profile.
- View staff dashboard.
- Mark attendance using face verification.
- Request location **only after successful face verification**.
- Save attendance with:
  - Employee ID
  - Name
  - Attendance selfie
  - Date/time
  - Latitude
  - Longitude
- Logout.

---

# 2. Technology Stack

| Technology | Purpose |
|---|---|
| Kotlin | Primary programming language |
| Jetpack Compose | UI development |
| Material 3 | UI components |
| CameraX | Camera preview and image capture |
| ML Kit Face Detection | Face detection and validation |
| FaceNet TFLite | 512-dimensional face embeddings |
| TensorFlow Lite | Local face model inference |
| Room | Local database |
| DataStore Preferences | Persistent login/session |
| Coil | Local image loading |
| Kotlin Coroutines | Asynchronous/database operations |
| Navigation Compose | Screen navigation |
| Google Play Services Location | Location services |
| Gradle Version Catalog | Dependency/version management |

---

# 3. Demo Credentials

> Replace the placeholders below with the actual credentials used in the final application.

### Admin

```text
Username: admin
Password: admin
```

### Staff

```text
Username: staff
Password: staff
```

> Staff also needs to exist in the local Room database.

---

# 4. Architecture

The project follows a layered Android architecture using Jetpack Compose, Navigation, repositories, Room, FaceNet, ML Kit and utility classes.

```text
                         Login
                           |
                +----------+----------+
                |                     |
              Admin                 Staff
                |                     |
                v                     v
         Admin Dashboard        Staff List
                |                     |
        +-------+-------+             v
        |               |        Staff Home
    Add Staff      Staff Profile      |
        |               |             |
        v               v             v
 Face Registration  Attendance    Mark Attendance
                         History        |
                                        v
                                  Face Verification
                                        |
                              +---------+---------+
                              |                   |
                           NO MATCH             MATCH
                              |                   |
                           Reject              Location
                                                  |
                                                  v
                                           Save Attendance
                                                  |
                                                  v
                                               Room
```

---

# 5. Face Recognition Flow

## Staff Registration

```text
Admin
  ↓
Register Staff
  ↓
Enter Name + Employee ID
  ↓
Open Camera
  ↓
ML Kit Face Detection
  ↓
Validate Face
  ↓
Crop Face
  ↓
FaceNet TFLite
  ↓
512-D Embedding
  ↓
Save StaffEntity in Room
```

## Attendance Verification

```text
Staff
  ↓
Select Staff
  ↓
Staff Home
  ↓
Mark Attendance
  ↓
Camera
  ↓
ML Kit Face Detection
  ↓
Crop Face
  ↓
FaceNet TFLite
  ↓
512-D Embedding
  ↓
Compare with selected employee's registered embedding
  ↓
MATCH / NO MATCH
```

If the face does not match:

```text
NO MATCH
   ↓
Attendance rejected
   ↓
No location request
   ↓
No attendance record
```

If the face matches:

```text
MATCH
   ↓
Request location permission if required
   ↓
Get current location
   ↓
Save attendance selfie
   ↓
Create AttendanceEntity
   ↓
Insert into Room
   ↓
Attendance marked
```

---

# 6. FaceNet Model

The project uses:

```text
facenet.tflite
```

Location:

```text
app/src/main/assets/facenet.tflite
```

### Model Input

```text
Shape: [1, 160, 160, 3]
Type: FLOAT32
```

### Model Output

```text
Shape: [1, 512]
Type: FLOAT32
```

### Image Preprocessing

The face image is resized to:

```text
160 × 160
```

Pixel normalization:

```text
(pixel - 127.5) / 127.5
```

The resulting 512-dimensional embedding is L2-normalized.

### Face Similarity

Cosine similarity is used to compare:

```text
Captured Face Embedding
            ↓
Registered Face Embedding
```

Current application threshold:

```text
0.65
```

The threshold may require further calibration using additional real-world face samples.

---

# 7. ML Kit Face Detection

ML Kit Face Detection is used before FaceNet to:

- Detect the face.
- Validate that a face exists.
- Check face orientation.
- Obtain the face bounding box.
- Crop the face before sending it to FaceNet.

The application uses accurate face detection and expects a reasonably frontal face during capture.

---

# 8. Room Database

Room is used for local data persistence.

## StaffEntity

The Staff table stores:

```text
employeeId
name
faceEmbedding
faceImagePath
```

The `FloatArray` face embedding is converted to and from `ByteArray` using a Room `TypeConverter`.

## AttendanceEntity

The Attendance table stores:

```text
id
employeeId
name
selfiePath
dateTime
latitude
longitude
```

Attendance is inserted only after successful face verification and successful location retrieval.

---

# 9. Image Storage

Images are stored in the application's internal storage.

Staff images are stored under:

```text
files/staff_faces/
```

Registered staff images use the employee ID.

Attendance selfies use a unique filename containing:

```text
employeeId + timestamp
```

This prevents multiple attendance images from overwriting each other.

---

# 10. Location Handling

Location is deliberately captured **only after the face matches**.

```text
Mark Attendance
      ↓
Capture Face
      ↓
Face Verification
      ↓
   +--+--+
   |     |
 FAIL   MATCH
   |     |
   |     v
   |   Location Permission
   |     ↓
   |   Get GPS
   |     ↓
   |   Save Attendance
   |
 Stop
```

A failed face verification therefore does not request or save the user's location.

The attendance record stores:

```text
Latitude
Longitude
```

---

# 11. Login and Session Management

The application uses **DataStore Preferences** to persist the logged-in role.

Possible stored values:

```text
ADMIN
STAFF
```

## Login

```text
Login
  ↓
Save role in DataStore
  ↓
Navigate to corresponding screen
```

## App Restart

```text
App Opens
   ↓
Read DataStore
   ↓
ADMIN → Admin Flow
STAFF → Staff Flow
No Value → Login
```

## Logout

```text
Logout
  ↓
Remove stored role
  ↓
Clear navigation stack
  ↓
Login Screen
```

After logout, the user cannot return to the authenticated screen using the Android Back button.

---
---

# 12. How to Run the App

## Requirements

- Android Studio
- Android SDK
- Compatible JDK
- Android emulator or physical Android device

A physical Android device is recommended for testing:

- Camera
- Face detection
- GPS
- Runtime permissions

## Steps

### 1. Open the project

Open the project in Android Studio.

### 2. Sync Gradle

Allow Android Studio to download and configure all dependencies.

### 3. Verify the FaceNet model

Make sure this file exists:

```text
app/src/main/assets/facenet.tflite
```

### 4. Connect a device

Use either:

- Physical Android device
- Android emulator

A physical device is recommended for more realistic camera and location testing.

### 5. Run the application

Select:

```text
app
```

as the run configuration and click **Run**.

---

# 13. Required Permissions

The application requires:

```xml
<uses-permission android:name="android.permission.CAMERA" />

<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />

<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
```

Camera permission is required for:

- Staff registration
- Attendance verification

Location permission is requested only after a successful face match during attendance.

---

# 14. Admin Flow

```text
Admin Login
     ↓
Admin Dashboard
     ↓
Add Staff
     ↓
Enter Name
     ↓
Enter Employee ID
     ↓
Capture Face
     ↓
ML Kit Face Detection
     ↓
Face Crop
     ↓
FaceNet Embedding
     ↓
Add Details
     ↓
Save Staff in Room
```

The captured face is returned to the Add Staff screen before final submission.

The staff record is committed only when the Admin clicks **Add Details**.

### Admin Staff Profile

From the Admin dashboard:

```text
Staff List
   ↓
Click Staff Card
   ↓
Staff Profile
   ↓
Attendance History
```

The profile displays the staff member's information and attendance records.

---

# 15. Staff Flow

```text
Staff Login
     ↓
Check Staff Records
     ↓
Staff List
     ↓
Select Staff
     ↓
Staff Home
     ↓
Mark Attendance
     ↓
Attendance Camera
     ↓
Face Verification
```

### If the face does not match

```text
Face Does Not Match
        ↓
Attendance Rejected
        ↓
No Location Request
        ↓
No Attendance Saved
```

### If the face matches

```text
Face Matched
     ↓
Location Permission
     ↓
Get Current Location
     ↓
Save Attendance Selfie
     ↓
Create AttendanceEntity
     ↓
Insert into Room
     ↓
Attendance Marked
     ↓
Staff Home
```

---

# 16. Staff Home

The Staff Home screen displays:

- Staff profile image
- Staff name
- Employee ID
- Attendance status
- Mark Attendance action

After successful attendance:

```text
Attendance Marked
```

is displayed on the Staff Home screen.

The actual attendance record still contains the date/time and GPS coordinates in Room.

---

# 17. Admin Staff Profile

When the Admin clicks a staff card, the selected staff member's profile is opened.

The profile contains:

### Staff Information

```text
Profile Image
Name
Employee ID
```

### Attendance

Each attendance record displays:

```text
Attendance Selfie
Date
Time
Latitude
Longitude
```

Records are loaded from the Room database and displayed with the latest attendance first.

---

# 18. Testing Checklist

## Admin

- [ ] Admin login
- [ ] Admin session is stored
- [ ] Admin dashboard opens
- [ ] Register staff
- [ ] Capture staff face
- [ ] Save staff
- [ ] Staff appears in Admin list
- [ ] Open staff profile
- [ ] View attendance history
- [ ] View attendance location
- [ ] Logout
- [ ] Reopen app and verify Admin session

## Staff

- [ ] Staff login
- [ ] Staff session is stored
- [ ] Staff list opens
- [ ] Select staff
- [ ] Staff Home opens
- [ ] Mark attendance
- [ ] Capture face
- [ ] Correct face is recognized
- [ ] Location permission is requested after face match
- [ ] Attendance is saved
- [ ] Staff Home shows `Attendance Marked`
- [ ] Logout
- [ ] Reopen app and verify Staff session

## Face Mismatch

- [ ] Select a staff member
- [ ] Capture another person's face
- [ ] Verify `Face does not match`
- [ ] Verify location is not requested
- [ ] Verify attendance is not saved

---

# 19. Production Considerations

For production usage, the following improvements would be recommended:

- Secure backend authentication and authorization
- Encrypted storage for biometric information
- Secure biometric data handling
- User consent and privacy controls
- Server-side attendance synchronization
- Liveness/anti-spoofing detection
- More extensive face threshold calibration
- Audit logging
- Secure location handling
- Backup and recovery
- Multi-device synchronization
- Appropriate privacy controls for biometric and location data

---

# 20. Core Attendance Rule

The most important rule of the application is:

```text
Face Match
    ↓
Location
    ↓
Attendance Record
```

A failed face match:

```text
Does NOT request location
Does NOT save selfie
Does NOT create attendance
```

A successful face match:

```text
Requests location
      ↓
Gets latitude + longitude
      ↓
Saves attendance selfie
      ↓
Creates attendance record
      ↓
Stores attendance in Room
```

---

# 21. Summary

This project demonstrates an Android face-recognition-based attendance system using:

```text
Kotlin
+
Jetpack Compose
+
Material 3
+
CameraX
+
ML Kit Face Detection
+
FaceNet TFLite
+
TensorFlow Lite
+
Room
+
DataStore
+
Google Play Services Location
+
Coil
```

The application provides separate Admin and Staff workflows, persistent login sessions, local staff/attendance storage, face verification, attendance selfies, and GPS-based attendance records.
