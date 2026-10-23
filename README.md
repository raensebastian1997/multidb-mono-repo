# Development

Untuk melihat perubahan Java tanpa build penuh berulang kali, jalankan `MicroserviceNewApplication` dari IntelliJ IDEA dalam mode **Debug**. Project ini sudah menggunakan Spring Boot DevTools untuk memuat ulang aplikasi saat file hasil compile berubah.

Aktifkan pengaturan IntelliJ berikut:

1. **Settings > Build, Execution, Deployment > Compiler > Build project automatically**.
2. **Settings > Advanced Settings > Compiler > Allow auto-make to start even if developed application is currently running**.

Pada IntelliJ IDEA 2026.2, opsi pertama juga mengaktifkan **Tools > Actions on Save > Build project**. IntelliJ akan mengompilasi perubahan secara incremental saat file disimpan. Jika perlu, paksa compile dengan **Build > Build Project** (`Ctrl+F9`).

Perubahan isi method biasanya bisa diterapkan lewat Java HotSwap. Perubahan struktur class atau konfigurasi Spring memerlukan DevTools untuk me-restart konteks aplikasi, sehingga prosesnya bisa memakan beberapa detik.
