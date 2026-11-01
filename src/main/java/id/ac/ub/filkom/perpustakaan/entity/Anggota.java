package id.ac.ub.filkom.perpustakaan.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

/** «entity» Anggota — tabel anggota (skema 8a & latihan hal 57). */
@Entity
@Table(name = "anggota")
public class Anggota {

    @Id
    @Column(name = "id_anggota", length = 10)
    private String idAnggota;

    @Column(name = "nama", length = 100)
    private String nama;

    @Column(name = "batas_pinjam")
    private int batasPinjam;

    /** Relasi 1 —— 0..* ke TransaksiPinjam. */
    @OneToMany(mappedBy = "anggota")
    private List<TransaksiPinjam> daftarPinjam = new ArrayList<>();

    /** Relasi 1 —— 0..* ke Reservasi. */
    @OneToMany(mappedBy = "anggota")
    private List<Reservasi> daftarReservasi = new ArrayList<>();

    protected Anggota() { } // wajib untuk JPA

    public Anggota(String idAnggota, String nama, int batasPinjam) {
        this.idAnggota = idAnggota;
        this.nama = nama;
        this.batasPinjam = batasPinjam;
    }

    /** +cekBatasPinjam() : boolean */
    public boolean cekBatasPinjam() {
        long sedangDipinjam = daftarPinjam.stream()
                .filter(TransaksiPinjam::isDipinjam)
                .count();
        return sedangDipinjam < batasPinjam;
    }

    /** Menjaga konsistensi relasi dua arah dengan TransaksiPinjam. */
    public void catatPinjaman(TransaksiPinjam transaksi) {
        daftarPinjam.add(transaksi);
    }

    /** Menjaga konsistensi relasi dua arah dengan Reservasi. */
    public void catatReservasi(Reservasi reservasi) {
        daftarReservasi.add(reservasi);
    }

    public String getIdAnggota() {
        return idAnggota;
    }

    public String getNama() {
        return nama;
    }

    public int getBatasPinjam() {
        return batasPinjam;
    }

    public List<TransaksiPinjam> getDaftarPinjam() {
        return daftarPinjam;
    }

    public List<Reservasi> getDaftarReservasi() {
        return daftarReservasi;
    }
}
