package id.ac.ub.filkom.perpustakaan.control;

import id.ac.ub.filkom.perpustakaan.entity.Buku;
import id.ac.ub.filkom.perpustakaan.entity.BukuRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * «control» StokService — Mengoordinasikan alur use case Kelola Stok Buku (aktor: Pustakawan).
 * Mengikuti pola BCE (Boundary-Control-Entity).
 */
@Service
public class StokService {

    private final BukuRepository bukuRepository;

    public StokService(BukuRepository bukuRepository) {
        this.bukuRepository = bukuRepository;
    }

    public List<Buku> daftarSemuaBuku() {
        return bukuRepository.findAll();
    }

    public Buku cariBuku(String idBuku) {
        return bukuRepository.findById(idBuku)
                .orElseThrow(() -> new IllegalArgumentException("Buku dengan ID " + idBuku + " tidak ditemukan"));
    }

    /**
     * Use Case UC-02 Kelola Stok Buku: Tambah Stok (Restock)
     * Sequence: [2] -> [3] -> [4] -> [5] -> [6] -> [7] -> [8] -> [9]
     */
    @Transactional
    public Buku tambahStok(String idBuku, int jumlah) {
        if (jumlah <= 0) {
            // [11] alur alternatif: validasi jumlah
            throw new IllegalArgumentException("Jumlah stok tambahan harus lebih dari 0");
        }

        // [3] findById(idBuku) -> [4] buku
        Buku buku = bukuRepository.findById(idBuku)
                .orElseThrow(() -> new IllegalArgumentException("Buku dengan ID " + idBuku + " tidak ditemukan"));

        // [5] tambahStok(jumlah) -> [6] stok bertambah
        buku.tambahStok(jumlah);

        // [7] save(buku) -> [8] buku tersimpan
        bukuRepository.save(buku);

        // [9] return buku yang telah diperbarui
        return buku;
    }

    /**
     * Use Case UC-02 Kelola Stok Buku: Ubah Stok Langsung
     */
    @Transactional
    public Buku ubahStok(String idBuku, int stokBaru) {
        if (stokBaru < 0) {
            // [11] alur alternatif: validasi stok negatif
            throw new IllegalArgumentException("Stok tidak boleh bernilai negatif");
        }

        // [3] findById(idBuku) -> [4] buku
        Buku buku = bukuRepository.findById(idBuku)
                .orElseThrow(() -> new IllegalArgumentException("Buku dengan ID " + idBuku + " tidak ditemukan"));

        // [5b] ubahStok(stokBaru)
        buku.ubahStok(stokBaru);

        // [7] save(buku)
        bukuRepository.save(buku);

        // [9] return buku
        return buku;
    }

    /**
     * Use Case UC-02 Kelola Stok Buku: Tambah Buku Baru
     */
    @Transactional
    public Buku tambahBukuBaru(String idBuku, String judul, int stok) {
        if (idBuku == null || idBuku.trim().isEmpty()) {
            throw new IllegalArgumentException("ID Buku tidak boleh kosong");
        }
        if (judul == null || judul.trim().isEmpty()) {
            throw new IllegalArgumentException("Judul buku tidak boleh kosong");
        }
        if (stok < 0) {
            throw new IllegalArgumentException("Stok tidak boleh negatif");
        }
        if (bukuRepository.existsById(idBuku.trim())) {
            throw new IllegalArgumentException("Buku dengan ID " + idBuku + " sudah terdaftar");
        }

        Buku bukuBaru = new Buku(idBuku.trim(), judul.trim(), stok);
        return bukuRepository.save(bukuBaru);
    }
}

