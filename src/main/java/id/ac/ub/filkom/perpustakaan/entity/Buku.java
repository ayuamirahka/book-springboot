package id.ac.ub.filkom.perpustakaan.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** «entity» Buku — tabel buku (skema 8a & latihan hal 57). */
@Entity
@Table(name = "buku")
public class Buku {

    @Id
    @Column(name = "id_buku", length = 10)
    private String idBuku;

    @Column(name = "judul", length = 200)
    private String judul;

    @Column(name = "stok")
    private int stok;

    protected Buku() { } // wajib untuk JPA

    public Buku(String idBuku, String judul, int stok) {
        if (stok < 0) {
            throw new IllegalArgumentException("Stok tidak boleh negatif");
        }
        this.idBuku = idBuku;
        this.judul = judul;
        this.stok = stok;
    }

    /** +cekStok() : boolean */
    public boolean cekStok() {
        return stok > 0;
    }

    /** +kurangiStok() : void */
    public void kurangiStok() {
        if (stok <= 0) {
            throw new IllegalStateException("Stok sudah habis");
        }
        stok--;
    }

    /**
     * +tambahStok(jumlah : int) : void
     * Menambahkan stok buku saat restock oleh pustakawan (Kelola Stok).
     */
    public void tambahStok(int jumlah) {
        if (jumlah <= 0) {
            throw new IllegalArgumentException("Jumlah penambahan stok harus lebih dari 0");
        }
        this.stok += jumlah;
    }

    /**
     * +ubahStok(stokBaru : int) : void
     * Mengatur nilai stok buku secara langsung oleh pustakawan (Kelola Stok).
     */
    public void ubahStok(int stokBaru) {
        if (stokBaru < 0) {
            throw new IllegalArgumentException("Stok tidak boleh bernilai negatif");
        }
        this.stok = stokBaru;
    }

    /**
     * +cekBisaReservasi() : boolean
     * Reservasi hanya diperbolehkan jika stok buku habis (= 0).
     */
    public boolean cekBisaReservasi() {
        return this.stok == 0;
    }

    // getter: getIdBuku(), getJudul(), getStok()
    public String getIdBuku() {
        return idBuku;
    }

    public String getJudul() {
        return judul;
    }

    public int getStok() {
        return stok;
    }
}
