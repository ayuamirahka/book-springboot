# BookSpace — Project Revisi Perpustakaan

Project ini mempertahankan struktur Spring Boot, pembagian BCE, dan urutan proses bisnis pada sequence diagram UC-01 Pinjam Buku, UC-02 Kelola Stok Buku, serta UC-03 Ajukan Reservasi.

## Folder proyek

Buka folder `BookSpace-Perpustakaan` di VS Code. Folder ini adalah root proyek karena berisi `pom.xml`, `src`, dan `target`.

## Menjalankan aplikasi tanpa menginstal Maven

Java 17 atau lebih baru diperlukan. Dari PowerShell pada folder proyek jalankan:

```powershell
java -jar .\target\pinjam-buku-bce-1.0.0.jar
```

Buka `http://localhost:8080`.

Jika port 8080 sedang digunakan:

```powershell
java -jar .\target\pinjam-buku-bce-1.0.0.jar --server.port=8081
```

Lalu buka `http://localhost:8081`.

## Membangun ulang source code

Untuk mengompilasi source dan menjalankan unit test, Apache Maven perlu terpasang. Dari folder yang sama jalankan:

```powershell
mvn clean test
```

Untuk membuat JAR terbaru:

```powershell
mvn clean package
```

## Akun dan data demo

Data awal pada `data.sql`:

| ID Anggota | Nama | Batas pinjaman aktif |
|---|---|---:|
| A001 | Budi Santoso | 2 buku |
| A002 | Sari Dewi | 1 buku |

| ID Buku | Judul | Stok awal |
|---|---|---:|
| B001 | Pemrograman Java | 3 |
| B002 | Rekayasa Perangkat Lunak | 0 |
| B003 | Basis Data | 2 |

Pada halaman Pinjam Buku tersedia pemilih akun simulasi. Pemilih ini bukan fitur autentikasi/login; fungsinya membantu pengujian transaksi dengan anggota berbeda.

## Urutan screenshot yang disarankan

1. **Pinjam Buku:** pilih A001, pinjam B001. A001 dapat melakukan maksimal dua peminjaman aktif. Percobaan ketiga akan menampilkan pesan `Melewati batas pinjam`.
2. **Reservasi:** B002 dimulai dengan stok 0. Buka reservasi B002 dan konfirmasi. Satu anggota tidak dapat memiliki reservasi aktif `MENUNGGU` ganda untuk buku yang sama.
3. **Kelola Stok:** tambah stok B002 melalui halaman `/stok`, lalu pastikan jumlah stok berubah.

## Catatan database

Project masih menggunakan H2 in-memory (`jdbc:h2:mem:perpustakaan`). Untuk mengembalikan data demo ke kondisi awal, hentikan seluruh proses aplikasi dengan `Ctrl + C`, lalu jalankan ulang JAR. Jangan hanya me-refresh browser; data selama proses server yang sama tetap tersimpan.

## Catatan sequence diagram

Urutan utama service tidak diubah:

- Peminjaman: memeriksa batas pinjam, memeriksa stok, membuat dan menyimpan transaksi, mengurangi stok, lalu menghasilkan bukti.
- Reservasi: memeriksa stok habis, memeriksa reservasi aktif, membuat dan menyimpan reservasi, lalu menghasilkan bukti.
- Kelola stok: tambah stok, ubah stok, dan tambah buku baru.

Perubahan pemilih akun, ringkasan kuota, serta HTML/CSS hanya membantu tampilan dan simulasi. Tidak ada langkah baru pada sequence utama.
