# Changelog Revisi BookSpace

## File Java yang direvisi

- `src/main/java/id/ac/ub/filkom/perpustakaan/control/PinjamService.java`
  - Menambahkan daftar anggota, pencarian anggota, validasi ID anggota, dan penghitungan pinjaman aktif untuk kebutuhan pemilih akun dan ringkasan kuota.
  - Alur utama `pinjamBuku()` tetap mempertahankan pemeriksaan batas, pemeriksaan stok, pembuatan transaksi, pengurangan stok, dan bukti.
- `src/main/java/id/ac/ub/filkom/perpustakaan/boundary/HalamanPinjamController.java`
  - Menambahkan pemilih akun simulasi melalui parameter `anggota` dan informasi kuota ke model.

## File tampilan yang direvisi

- `src/main/resources/static/css/library.css`
- `src/main/resources/templates/pinjam.html`
- `src/main/resources/templates/reservasi.html`
- `src/main/resources/templates/stok.html`
- `src/main/resources/templates/bukti.html`
- `src/main/resources/templates/bukti-reservasi.html`

## File lain

File Java, entity, repository, konfigurasi, skema database, data awal, dan unit test yang tidak tercantum di atas dipertahankan dari source project yang diunggah.
