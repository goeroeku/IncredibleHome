# IncredibleHome
### Aplikasi IoT untuk memanage lampu dan pintu rumah dengan memanfaatkan platform arkademy.com
Feature:
- Menyalakan dan mematikan lampu (support 2 lokasi lampu)
- Memantau status lampu (support 2 lokasi lampu)
- Membuka dan menutup pintu (support 2 lokasi pintu)
- Bisa dikontrol menggunakan suara

Note:
- Untuk implementasi code ini membutuhkan URL_API, TOKEN dan DEVICE_ID dari [arkademy.com](https://arkademy.com)
- Jika belum memperoleh silakan daftar dulu di [arkademy.com](https://arkademy.com)
- Jika sudah, buka file build.gradle pada direktori <project>/app/
- Kemudian edit xxxxxx pada bagian bagian :
  ```
  buildConfigField "String", "URL_API", "\"xxxxxx\""
  buildConfigField "String", "DEVICE_IC", "\"xxxxx\""
  ```