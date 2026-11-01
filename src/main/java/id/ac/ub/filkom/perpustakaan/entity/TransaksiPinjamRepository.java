package id.ac.ub.filkom.perpustakaan.entity;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Akses tabel transaksi_pinjam.
 * save(transaksi) merealisasikan method simpan() milik
 * TransaksiPinjam pada class diagram 8a.
 */
public interface TransaksiPinjamRepository extends JpaRepository<TransaksiPinjam, String> {
}

