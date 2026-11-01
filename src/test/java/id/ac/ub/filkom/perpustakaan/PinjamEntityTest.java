package id.ac.ub.filkom.perpustakaan;

import id.ac.ub.filkom.perpustakaan.entity.Anggota;
import id.ac.ub.filkom.perpustakaan.entity.Buku;
import id.ac.ub.filkom.perpustakaan.entity.Reservasi;
import id.ac.ub.filkom.perpustakaan.entity.TransaksiPinjam;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test method entity yang dipanggil control pada sequence diagram.
 * Mencakup UC-01 Pinjam Buku, UC-02 Kelola Stok Buku, dan UC-03 Ajukan Reservasi.
 */
class PinjamEntityTest {

    @Test
    void cekStok_dan_kurangiStok() {
        Buku buku = new Buku("B001", "Pemrograman Java", 1);
        assertTrue(buku.cekStok());
        buku.kurangiStok();
        assertEquals(0, buku.getStok());
        assertFalse(buku.cekStok());
        assertThrows(IllegalStateException.class, buku::kurangiStok);
    }

    @Test
    void cekBatasPinjam_menolak_jika_sudah_mencapai_batas() {
        Anggota anggota = new Anggota("A002", "Sari Dewi", 1);
        Buku buku = new Buku("B003", "Basis Data", 2);
        assertTrue(anggota.cekBatasPinjam());
        TransaksiPinjam t = TransaksiPinjam.buat(anggota, buku);
        assertEquals(TransaksiPinjam.DIPINJAM, t.getStatus());
        assertFalse(anggota.cekBatasPinjam());
    }

    @Test
    void tambahStok_berhasil_dan_menolak_jumlah_tidak_valid() {
        Buku buku = new Buku("B001", "Pemrograman Java", 3);
        buku.tambahStok(5);
        assertEquals(8, buku.getStok());

        assertThrows(IllegalArgumentException.class, () -> buku.tambahStok(0));
        assertThrows(IllegalArgumentException.class, () -> buku.tambahStok(-2));
    }

    @Test
    void ubahStok_berhasil_dan_menolak_stok_negatif() {
        Buku buku = new Buku("B001", "Pemrograman Java", 3);
        buku.ubahStok(10);
        assertEquals(10, buku.getStok());

        buku.ubahStok(0);
        assertEquals(0, buku.getStok());

        assertThrows(IllegalArgumentException.class, () -> buku.ubahStok(-1));
    }

    @Test
    void cekBisaReservasi_hanya_jika_stok_habis() {
        Buku bukuAdaStok = new Buku("B001", "Pemrograman Java", 2);
        assertFalse(bukuAdaStok.cekBisaReservasi());

        Buku bukuHabis = new Buku("B002", "Rekayasa Perangkat Lunak", 0);
        assertTrue(bukuHabis.cekBisaReservasi());
    }

    @Test
    void reservasi_buat_dan_perubahan_status() {
        Anggota anggota = new Anggota("A001", "Budi Santoso", 2);
        Buku bukuHabis = new Buku("B002", "Rekayasa Perangkat Lunak", 0);

        Reservasi r = Reservasi.buat(anggota, bukuHabis);

        assertNotNull(r.getIdReservasi());
        assertTrue(r.getIdReservasi().startsWith("R"));
        assertEquals(Reservasi.MENUNGGU, r.getStatus());
        assertTrue(r.isMenunggu());
        assertEquals(LocalDate.now(), r.getTanggalReservasi());
        assertEquals(anggota, r.getAnggota());
        assertEquals(bukuHabis, r.getBuku());
        assertTrue(anggota.getDaftarReservasi().contains(r));

        // Uji perubahan status reservasi
        r.batalkan();
        assertEquals(Reservasi.DIBATALKAN, r.getStatus());
        assertFalse(r.isMenunggu());

        r.penuhi();
        assertEquals(Reservasi.SELESAI, r.getStatus());
        assertFalse(r.isMenunggu());
    }
}
