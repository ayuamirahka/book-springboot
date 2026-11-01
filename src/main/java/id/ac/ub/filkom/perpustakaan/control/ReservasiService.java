package id.ac.ub.filkom.perpustakaan.control;

import id.ac.ub.filkom.perpustakaan.entity.Anggota;
import id.ac.ub.filkom.perpustakaan.entity.AnggotaRepository;
import id.ac.ub.filkom.perpustakaan.entity.Buku;
import id.ac.ub.filkom.perpustakaan.entity.BukuRepository;
import id.ac.ub.filkom.perpustakaan.entity.Reservasi;
import id.ac.ub.filkom.perpustakaan.entity.ReservasiRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * «control» ReservasiService — Mengoordinasikan alur use case «extend» Ajukan Reservasi.
 * Sesuai pola BCE, memegang kendali alur bisnis dan aturan use case reservasi buku.
 */
@Service
public class ReservasiService {

    private final AnggotaRepository anggotaRepository;
    private final BukuRepository bukuRepository;
    private final ReservasiRepository reservasiRepository;

    public ReservasiService(AnggotaRepository anggotaRepository,
                            BukuRepository bukuRepository,
                            ReservasiRepository reservasiRepository) {
        this.anggotaRepository = anggotaRepository;
        this.bukuRepository = bukuRepository;
        this.reservasiRepository = reservasiRepository;
    }

    public List<Reservasi> daftarSemuaReservasi() {
        return reservasiRepository.findAll();
    }

    public Buku getInfoBuku(String idBuku) {
        return bukuRepository.findById(idBuku)
                .orElseThrow(() -> ReservasiGagalException.tidakDitemukan("Buku"));
    }

    /**
     * Use Case UC-03 «extend» Ajukan Reservasi:
     * Alur:
     * [2] ajukanReservasi(idAnggota, idBuku)
     * [3] cekBisaReservasi() -> [4] hasil boolean
     * [5] cekReservasiAktif()
     * [6] Reservasi.buat(anggota, buku)
     * [7] save(reservasi)
     * [8] return BuktiReservasi
     */
    @Transactional
    public BuktiReservasi ajukanReservasi(String idAnggota, String idBuku) {
        // Persiapan: Ambil entity anggota dan buku
        Anggota anggota = anggotaRepository.findById(idAnggota)
                .orElseThrow(() -> ReservasiGagalException.tidakDitemukan("Anggota"));
        Buku buku = bukuRepository.findById(idBuku)
                .orElseThrow(() -> ReservasiGagalException.tidakDitemukan("Buku"));

        // [3] cekBisaReservasi() -> [4] boleh reservasi hanya jika stok habis
        if (!buku.cekBisaReservasi()) {
            throw ReservasiGagalException.stokMasihAda();
        }

        // [5] Cek apakah sudah ada reservasi aktif yang masih berstatus MENUNGGU
        if (reservasiRepository.existsByAnggotaIdAnggotaAndBukuIdBukuAndStatus(idAnggota, idBuku, Reservasi.MENUNGGU)) {
            throw ReservasiGagalException.sudahReservasi();
        }

        // [6] buat(anggota, buku) -> relasi dua arah dicatat
        Reservasi reservasi = Reservasi.buat(anggota, buku);

        // [7] simpan ke repository
        reservasiRepository.save(reservasi);

        // [8] hasil kembalian berupa DTO BuktiReservasi
        return BuktiReservasi.dari(reservasi);
    }
}

