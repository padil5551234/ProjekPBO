USE data_kos;

CREATE TABLE IF NOT EXISTS `ulasan` (
  `id_ulasan` int(11) NOT NULL AUTO_INCREMENT,
  `id_kos` int(11) NOT NULL,
  `id_user` int(11) NOT NULL,
  `rating` int(1) NOT NULL CHECK (rating >= 1 AND rating <= 5),
  `komentar` text DEFAULT NULL,
  `tanggal` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id_ulasan`),
  KEY `fk_ulasan_kos` (`id_kos`),
  KEY `fk_ulasan_user` (`id_user`),
  CONSTRAINT `fk_ulasan_kos` FOREIGN KEY (`id_kos`) REFERENCES `kosan` (`id_kos`) ON DELETE CASCADE,
  CONSTRAINT `fk_ulasan_user` FOREIGN KEY (`id_user`) REFERENCES `users` (`id_user`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;