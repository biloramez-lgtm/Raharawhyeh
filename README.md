# 🕌 Raha Rawhey - رها روحي
### تطبيق مواقيت الصلاة والقبلة الاحترافي

---

## 📱 الميزات

| الميزة | الوصف |
|--------|-------|
| 🕐 مواقيت الصلاة | جميع الصلوات الخمس + الشروق في الوقت الفعلي |
| 🧭 بوصلة القبلة | بوصلة تفاعلية دقيقة مع رسوم متحركة |
| 🌙 التاريخ الهجري | عرض التاريخ الهجري مع كل الأوقات |
| ⏰ العد التنازلي | عداد تنازلي للصلاة القادمة |
| 🌍 مناهج حساب متعددة | أم القرى، مصر، كراتشي، أمريكا، رابطة العالم... |
| 📍 تحديد الموقع | GPS تلقائي مع fallback لمكة المكرمة |
| 💾 حفظ مؤقت | Room DB لحفظ الأوقات بدون إنترنت |
| 🎨 واجهة إسلامية فاخرة | ثيم داكن ذهبي مع نقوش إسلامية |

---

## 🏗️ هيكل المشروع

```
RahaRawhey/
├── app/src/main/
│   ├── kotlin/com/raharawhey/
│   │   ├── MainActivity.kt              ← الشاشة الرئيسية + Navigation Rail
│   │   ├── RahaRawheyApp.kt             ← Application class (Hilt)
│   │   ├── di/
│   │   │   └── AppModule.kt             ← Dependency Injection
│   │   ├── data/
│   │   │   ├── api/
│   │   │   │   ├── PrayerApiService.kt  ← Retrofit API (aladhan.com)
│   │   │   │   └── PrayerDatabase.kt   ← Room Database + DAO
│   │   │   ├── models/
│   │   │   │   └── Models.kt           ← Data classes
│   │   │   ├── repository/
│   │   │   │   └── PrayerRepository.kt ← Repository pattern
│   │   │   └── receiver/
│   │   │       └── Receivers.kt        ← Alarm + Boot receivers
│   │   ├── ui/
│   │   │   ├── Navigation.kt           ← Screen sealed class
│   │   │   ├── theme/
│   │   │   │   ├── Theme.kt            ← Material3 Islamic theme
│   │   │   │   └── Typography.kt       ← Typography + Shapes
│   │   │   ├── components/
│   │   │   │   └── Components.kt       ← Shared composables
│   │   │   └── screens/
│   │   │       ├── PrayerTimesScreen.kt ← شاشة المواقيت
│   │   │       ├── PrayerTimesViewModel.kt
│   │   │       ├── QiblaScreen.kt       ← شاشة القبلة
│   │   │       ├── QiblaViewModel.kt
│   │   │       └── SettingsScreen.kt    ← الإعدادات
│   │   └── utils/
│   │       └── LocationUtils.kt        ← GPS + حساب القبلة
│   ├── res/
│   │   ├── values/
│   │   │   ├── strings.xml
│   │   │   └── themes.xml
│   │   ├── drawable/
│   │   │   └── ic_splash_mosque.xml    ← Splash screen icon
│   │   └── xml/
│   │       └── locales_config.xml
│   └── AndroidManifest.xml
├── gradle/
│   └── libs.versions.toml              ← Version catalog
├── build.gradle.kts
├── settings.gradle.kts
└── app/
    ├── build.gradle.kts
    └── proguard-rules.pro
```

---

## 🚀 خطوات الإعداد في Android Studio

### 1. إنشاء مشروع جديد
```
File → New → New Project → Empty Activity
Package name: com.raharawhey
Min SDK: 26 (Android 8.0)
Language: Kotlin
Build configuration: Kotlin DSL
```

### 2. نسخ الملفات
انسخ جميع الملفات من هذا المشروع إلى مشروعك بنفس المسارات.

### 3. إضافة الخطوط (اختياري لكن يُحسّن التصميم)
1. حمّل **Amiri** و **Cairo** من Google Fonts
2. ضعها في `app/src/main/res/font/`
3. في `Typography.kt` غيّر:
```kotlin
val AmiriFamily = FontFamily(Font(R.font.amiri_regular), Font(R.font.amiri_bold, FontWeight.Bold))
val CairoFamily = FontFamily(Font(R.font.cairo_regular),  Font(R.font.cairo_semibold, FontWeight.SemiBold))
```

### 4. أيقونات التطبيق
1. انقر يمين على `res` → New → Image Asset
2. اختر Launcher Icons
3. استخدم أيقونة المسجد المتضمنة أو صمّم أيقونتك

### 5. تشغيل المشروع
```
Build → Make Project  ثم  Run → Run 'app'
```

---

## 🌐 API المستخدمة

**aladhan.com** - مجانية وبدون مفتاح API
```
https://api.aladhan.com/v1/timings/{timestamp}
?latitude={lat}&longitude={lng}&method={method}
```

---

## 📦 النشر على Google Play

### قبل الرفع تأكد من:
- [ ] تغيير `applicationId` إلى اسم حزمة فريد
- [ ] رفع رقم `versionCode` عند كل تحديث
- [ ] إنشاء Keystore للتوقيع:
  ```
  Build → Generate Signed Bundle / APK → Android App Bundle
  ```
- [ ] إضافة صور الـ Store Listing (screenshots, feature graphic)
- [ ] كتابة وصف التطبيق بالعربية والإنجليزية
- [ ] تصنيف المحتوى: Everyone

### أمور مهمة للـ Play Console:
- استخدم **App Bundle (.aab)** وليس APK
- Privacy Policy مطلوبة (بسبب صلاحية الموقع)
- Target SDK يجب أن يكون 34 أو أعلى

---

## 🎨 الألوان المستخدمة

| اللون | Hex | الاستخدام |
|-------|-----|-----------|
| Gold Accent | `#C9A84C` | العنوان الرئيسي والنجوم |
| Deep Navy | `#0A0F1E` | الخلفية الأساسية |
| Surface Deep | `#111B2E` | بطاقات الصلاة |
| Islamic Green | `#1B5E20` | البوصلة والتمييز |
| Teal | `#26A69A` | العد التنازلي |

---

## 📞 الصلاحيات المطلوبة

| الصلاحية | السبب |
|----------|-------|
| ACCESS_FINE_LOCATION | تحديد الموقع للأوقات والقبلة |
| INTERNET | جلب أوقات الصلاة من API |
| POST_NOTIFICATIONS | إشعارات الصلاة |
| SCHEDULE_EXACT_ALARM | ضبط منبهات دقيقة |
| RECEIVE_BOOT_COMPLETED | إعادة الإشعارات بعد إيقاف الجهاز |

---

## 💡 تحسينات مستقبلية مقترحة

- [ ] أذان صوتي (AudioManager + MediaPlayer)
- [ ] ودجت الشاشة الرئيسية
- [ ] تقويم شهري للأوقات
- [ ] أسماء الله الحسنى
- [ ] سبحة إلكترونية
- [ ] دعاء الصباح والمساء
- [ ] الاشتراك Premium بدون إعلانات

---

*بناه بحب لخدمة المسلمين حول العالم 🤍*
