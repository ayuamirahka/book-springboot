package id.ac.ub.filkom.perpustakaan.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.UUID;

/** «entity» TransaksiPinjam — tabel transaksi_pinjam. */
@Entity
@Table(name = "transaksi_pinjam")
public class TransaksiPinjam {

    public static final String DIPINJAM = "DIPINJAM";

    @Id
    @Column(name = "id_transaksi", length = 10)
    private String idTransaksi;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_anggota") // FK -> anggota
    private Anggota anggota;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_buku") // FK -> buku
    private Buku buku;

    @Column(name = "tanggal_pinjam")
    private LocalDate tanggalPinjam;

    @Column(name = "status", length = 20)
    private String status;

    protected TransaksiPinjam() { } // wajib untuk JPA

    /** Pesan buat(idAnggota, idBuku) pada sequence 8a. */
    public static TransaksiPinjam buat(Anggota anggota, Buku buku) {
        TransaksiPinjam t = new TransaksiPinjam();
        // id 9 karakter, contoh: T3F9A1C2B
        t.idTransaksi = "T" + UUID.randomUUID().toString()
                .substring(0, 8).toUpperCase();
        t.anggota = anggota;
        t.buku = buku;
        t.tanggalPinjam = LocalDate.now();
        t.status = DIPINJAM;
        anggota.catatPinjaman(t);
        return t;
    }

    public boolean isDipinjam() {
        return DIPINJAM.equals(status);
    }

    public String getIdTransaksi() {
        return idTransaksi;
    }

    public Anggota getAnggota() {
        return anggota;
    }

    public Buku getBuku() {
        return buku;
    }

    public LocalDate getTanggalPinjam() {
        return tanggalPinjam;
    }

    public String getStatus() {
        return status;
    }
}

