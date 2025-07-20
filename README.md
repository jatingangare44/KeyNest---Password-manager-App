# 🔐 KeyNest - Android Password Manager

KeyNest is a secure, elegant, and lightweight Android password manager that helps you store, manage, and protect your login credentials. Designed with modern UI principles and security features, it's the perfect companion for managing your digital life.

---

## 🚀 Features

- 🔐 Encrypted local password storage using Android Keystore (planned)
- 👆 Biometric login support (Fingerprint/Face ID)
- 🔒 Master password protection with auto logout
- ⏳ Session timeout handling with optional self-destruct (after failed attempts)
- 🌙 Dark mode toggle (coming soon)
- 🔎 Search feature to quickly find saved passwords
- 📦 Modern, card-based UI with gradient support
- 🧠 Fully offline and local-first design (no data leaves your device)
- 🚫 Screenshots are disabled for security
- 🕶 App screen goes blank in Recents

---

## 📱 Screenshots

![About Page 1](![about1](https://github.com/user-attachments/assets/2e693617-d67a-43cb-ae3d-2199c7959941))  
![About Page 2](![about2](https://github.com/user-attachments/assets/169fd36d-3352-4497-a2ed-a5134c74ee17))  
![Login Page](![login](https://github.com/user-attachments/assets/29408023-fe07-45bb-9478-3e5b6e3e0736))  
![Register Page](![register](https://github.com/user-attachments/assets/4f1665ea-fa5d-4cbe-a58c-edfbb2c3a083)
)
![about2](https://github.com/user-attachments/assets/b59faa67-be8b-45c1-970d-6ab0e9b2df79)

---

## 🛠 Tech Stack

- **Language:** Java
- **Framework:** Android SDK (API 21+)
- **UI:** Material Components, CardView, ConstraintLayout
- **Security:** AndroidX Security library (EncryptedSharedPreferences - planned)
- **Other:** SharedPreferences, RecyclerView

---

## 📂 Project Structure

KeyNest/
├── app/
│ ├── java/com/jatin/keynest/
│ │ ├── activities/
│ │ ├── adapters/
│ │ ├── models/
│ │ ├── utils/
│ ├── res/
│ │ ├── layout/
│ │ ├── drawable/
│ │ ├── values/
│ │ ├── font/
├── .gitignore
├── README.md
└── build.gradle


---

## 🛡 Security Notes

KeyNest stores your passwords **only on your device** using `SharedPreferences`.

> ⚠️ In future versions, EncryptedSharedPreferences and biometric + PIN fallback will be enforced.

---

## 🧪 Current Status

🔧 This project is under active development and is being shared on [LinkedIn](https://www.linkedin.com) as part of my Android development journey.

✅ Biometric Login, Add/View Passwords implemented  
🚧 Dark mode, full encryption pending

---

## 🌐 Portfolio

🔗 [Visit My Portfolio](https://jatin-gangare.netlify.app/)  
📱 [Lost n' Found App](https://github.com/jatin-gangare/lostnfound) – Smart village lost item tracking system

---

## 👨‍💻 Developed By

**Jatin Gangare**  
📍 Maharashtra, India  
📧 jatingangare44@gmail.com

---

## 📄 License

This project is open source and available under the [MIT License](LICENSE).
