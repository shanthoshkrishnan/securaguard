# SecuraGuard: Biometric Authentication System

SecuraGuard is a cross-platform mobile prototype designed for secure, offline face authentication. It uses Kotlin Multiplatform Mobile (KMM) and leverages hardware-backed key storage — Android Keystore and iOS Secure Enclave — to enable decentralized identity verification in low-connectivity environments.

---

## 🔍 Purpose

This project was built as part of a proposal for the Smart India Hackathon 2024 (Hardware Edition). It explores how secure biometric verification can be performed locally on-device, eliminating dependency on external servers — especially in rural or infrastructure-limited regions.

---

## ✅ Key Features

- On-device face authentication (offline)
- Hardware-bound RSA encryption (Android/iOS secure modules)
- UIDAI-aligned public key infrastructure
- Cross-platform architecture using KMM
- Modular activities for registration, comparison, and verification

---

## 🛠️ Tech Stack

| Area                | Technology                           |
|---------------------|---------------------------------------|
| Language            | Kotlin (KMM), XML                     |
| Android Security    | Android Keystore (RSA, OAEP)          |
| iOS Security        | Secure Enclave (planned)              |
| App Layers          | FaceActivity, KeyManager, Auth logic  |
| UI Toolkit          | Jetpack Compose / XML Layouts         |
| Backend-less Flow   | No external server or network needed  |
| Tools               | Android Studio, Gradle, Git           |

---

## 📂 Project Structure Snapshot

```
app/
├── manifests/
│   └── AndroidManifest.xml
├── kotlin+java/com.example.securaguardapp/
│   ├── activities/
│   │   ├── FaceActivity.kt
│   │   ├── FaceCaptureActivity.kt
│   │   ├── HomeActivity.kt
│   │   ├── LoginActivity.kt
│   │   ├── OtpActivity.kt
│   │   ├── UserActivity.kt
│   ├── helpers/
│   │   ├── FaceComparisonHelper.kt
│   │   ├── FirebaseHelper.kt
│   │   ├── KeyManager.kt
│   ├── adapter/
│   │   └── RecyclerViewAdapter.kt
│   └── model/
│       └── User.kt
├── res/
│   ├── drawable/ (images + backgrounds)
│   ├── layout/
│   │   ├── activity_face_capture.xml
│   │   ├── activity_main.xml
│   │   ├── activity_user_main.xml
│   │   └── item_user.xml
│   ├── values/
│   │   ├── strings.xml
│   │   ├── styles.xml
│   │   ├── themes.xml
│   └── xml/
│       └── data_extraction_rules.xml
```

---

## 🔐 Core Workflow

1.Registration
Captures user's face

User image wil be saved in TEE or Secure Enclave

RSA key pair will be generated  using Android Keystore

Face image and metadata will be encypted using the public key

Encrypted image and metadata will be sent to the UIDAI server over a secure PKI channel

UIDAI stores the encrypted biometric record for future verification

2. Authentication
Captures live face

Compares locally with stored registered image

The result of the comparison and associated metadata will be sent to the UIDAI server

Server verifies the data using its private key counterpart

If both device and server independently confirm the match, authentication is successful

Note: The private key remains bound to the device hardware and is non-extractable, ensuring security at the device level.

---

## 🚧 Current Status

- Android modules are under development and partially functional
- Secure Enclave integration (iOS) is planned
- KeyManager, FaceActivity, and FirebaseHelper are scaffolded and being refined
- Not yet ready for production, but demonstrates working core flows

---

## 📌 Context

Originally built for Smart India Hackathon 2024 to simulate UIDAI-aligned secure verification in offline conditions. Also supports potential use cases in defense, rural identity systems, or field-level authentication.

---

## 👤 Author

**Shanthosh K**  
GitHub: [@shanthoshkrishnan](https://github.com/shanthoshkrishnan)  
Email: shanthosh.krishnan@outlook.com

---

## ⚠️ Disclaimer

This codebase is a prototype, not a production-ready system. For real-world use, proper security audits, UIDAI approval, and legal compliance would be required.
