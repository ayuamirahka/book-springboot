package id.ac.ub.filkom.perpustakaan.control;

import id.ac.ub.filkom.perpustakaan.entity.Anggota;
import id.ac.ub.filkom.perpustakaan.entity.AnggotaRepository;
import id.ac.ub.filkom.perpustakaan.entity.Buku;
import id.ac.ub.filkom.perpustakaan.entity.BukuRepository;
import id.ac.ub.filkom.perpustakaan.entity.TransaksiPinjam;
import id.ac.ub.filkom.perpustakaan.entity.TransaksiPinjamRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * «control» PinjamController pada desain 8a.
 * Di Spring Boot direalisasikan sebagai @Service (BUKAN @Controller),
 * karena tugasnya mengoordinasikan alur bisnis satu use case.
 */
@Service
public class PinjamService {

    private final AnggotaRepository anggotaRepository;
    private final BukuRepository bukuRepository;
    private final TransaksiPinjamRepository transaksiRepository;

    public PinjamService(AnggotaRepository anggotaRepository,
                         BukuRepository bukuRepository,
                         TransaksiPinjamRepository transaksiRepository) {
        this.anggotaRepository = anggotaRepository;
        this.bukuRepository = bukuRepository;
        this.transaksiRepository = transaksiRepository;
    }

    public List<Buku> daftarBuku() {
        return bukuRepository.findAll();
    }

    /** Data anggota untuk pemilihan akun simulasi pada halaman web. */
    public List<Anggota> daftarAnggota() {
        return anggotaRepository.findAll();
    }

    /** Mengambil anggota yang sedang dipakai pada sesi simulasi. */
    public Anggota cariAnggota(String idAnggota) {
        return anggotaRepository.findById(idAnggota)
                .orElseThrow(() -> PinjamGagalException.tidakDitemukan("Anggota"));
    }

    public boolean adaAnggota(String idAnggota) {
        return idAnggota != null && anggotaRepository.existsById(idAnggota);
    }

    /** Ringkasan kuota untuk ditampilkan pada halaman, bukan bagian dari alur transaksi. */
    @Transactional(readOnly = true)
    public long jumlahPinjamanAktif(String idAnggota) {
        Anggota anggota = cariAnggota(idAnggota);
        return anggota.getDaftarPinjam().stream()
                .filter(TransaksiPinjam::isDipinjam)
                .count();
    }

    /** +pinjamBuku(idAnggota : String, idBuku : String) : Bukti */
    @Transactional
    public Bukti pinjamBuku(String idAnggota, String idBuku) {
        // Persiapan: ambil objek entity dari database
        Anggota anggota = anggotaRepository.findById(idAnggota)
                .orElseThrow(() -> PinjamGagalException.tidakDitemukan("Anggota"));
        Buku buku = bukuRepository.findById(idBuku)
                .orElseThrow(() -> PinjamGagalException.tidakDitemukan("Buku"));

        // [3] cekBatasPinjam() -> [4] boleh pinjam
        if (!anggota.cekBatasPinjam()) {
            throw PinjamGagalException.melewatiBatas(); // alur 4b
        }

        // [5] cekStok() -> [6] stok tersedia / habis
        if (!buku.cekStok()) {
            throw PinjamGagalException.stokHabis(); // [13] pesanHabis
        }

        // alt [stok tersedia]
        // [7] buat(idAnggota, idBuku) -> [8] transaksi tersimpan
        TransaksiPinjam transaksi = TransaksiPinjam.buat(anggota, buku);
        transaksiRepository.save(transaksi);

        // [9] kurangiStok() -> [10] ok
        buku.kurangiStok();
        bukuRepository.save(buku);

        return Bukti.dari(transaksi); // [11] bukti peminjaman
    }
}

