CREATE TABLE `userdb` (
                          `id` int NOT NULL AUTO_INCREMENT,
                          `username` varchar(20) NOT NULL,
                          `status` int NOT NULL DEFAULT '0',
                          `password` varchar(20) NOT NULL,
                          `email` varchar(45) NOT NULL,
                          `auth` int NOT NULL DEFAULT '0',
                          PRIMARY KEY (`id`),
                          UNIQUE KEY `id_UNIQUE` (`id`),
                          UNIQUE KEY `username_UNIQUE` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci