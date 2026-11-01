package id.ac.ub.filkom.perpustakaan.entity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Akses tabel reservasi (implementasi otomatis Spring Data JPA).
 */
public interface ReservasiRepository extends JpaRepository<Reservasi, String> {
    List<Reservasi> findByAnggota(Anggota anggota);
    List<Reservasi> findByBuku(Buku buku);
    List<Reservasi> findByStatus(String status);
    boolean existsByAnggotaIdAnggotaAndBukuIdBukuAndStatus(String idAnggota, String idBuku, String status);
}

