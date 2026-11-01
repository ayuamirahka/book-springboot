package id.ac.ub.filkom.perpustakaan.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.UUID;

/**
 * «entity» Reservasi — tabel reservasi (latihan hal 57).
 */
@Entity
@Table(name = "reservasi")
public class Reservasi {

    public static final String MENUNGGU = "MENUNGGU";
    public static final String SELESAI = "SELESAI";
    public static final String DIBATALKAN = "DIBATALKAN";

    @Id
    @Column(name = "id_reservasi", length = 10)
    private String idReservasi;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_anggota") // FK -> anggota
    private Anggota anggota;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_buku") // FK -> buku
    private Buku buku;

    @Column(name = "tanggal_reservasi")
    private LocalDate tanggalReservasi;

    @Column(name = "status", length = 20)
    private String status;

    protected Reservasi() { } // wajib untuk JPA

    /**
     * Factory method untuk membuat reservasi baru.
     */
    public static Reservasi buat(Anggota anggota, Buku buku) {
        Reservasi r = new Reservasi();
        // ID 9 karakter berawalan R, contoh: RF1A2B3C4
        r.idReservasi = "R" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        r.anggota = anggota;
        r.buku = buku;
        r.tanggalReservasi = LocalDate.now();
        r.status = MENUNGGU;
        anggota.catatReservasi(r);
        return r;
    }

    public boolean isMenunggu() {
        return MENUNGGU.equals(status);
    }

    public void batalkan() {
        this.status = DIBATALKAN;
    }

    public void penuhi() {
        this.status = SELESAI;
    }

    public String getIdReservasi() {
        return idReservasi;
    }

    public Anggota getAnggota() {
        return anggota;
    }

    public Buku getBuku() {
        return buku;
    }

    public LocalDate getTanggalReservasi() {
        return tanggalReservasi;
    }

    public String getStatus() {
        return status;
    }
}

