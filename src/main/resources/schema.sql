DROP TABLE IF EXISTS reservasi;
DROP TABLE IF EXISTS transaksi_pinjam;
DROP TABLE IF EXISTS buku;
DROP TABLE IF EXISTS anggota;

CREATE TABLE anggota (
    id_anggota VARCHAR(10) PRIMARY KEY,
    nama VARCHAR(100),
    batas_pinjam INT
);

CREATE TABLE buku (
    id_buku VARCHAR(10) PRIMARY KEY,
    judul VARCHAR(200),
    stok INT
);

CREATE TABLE transaksi_pinjam (
    id_transaksi VARCHAR(10) PRIMARY KEY,
    id_anggota VARCHAR(10) NOT NULL,
    id_buku VARCHAR(10) NOT NULL,
    tanggal_pinjam DATE,
    status VARCHAR(20),
    CONSTRAINT fk_transaksi_anggota FOREIGN KEY (id_anggota) REFERENCES anggota(id_anggota),
    CONSTRAINT fk_transaksi_buku FOREIGN KEY (id_buku) REFERENCES buku(id_buku)
);

CREATE TABLE reservasi (
    id_reservasi VARCHAR(10) PRIMARY KEY,
    id_anggota VARCHAR(10) NOT NULL,
    id_buku VARCHAR(10) NOT NULL,
    tanggal_reservasi DATE,
    status VARCHAR(20),
    CONSTRAINT fk_reservasi_anggota FOREIGN KEY (id_anggota) REFERENCES anggota(id_anggota),
    CONSTRAINT fk_reservasi_buku FOREIGN KEY (id_buku) REFERENCES buku(id_buku)
);
