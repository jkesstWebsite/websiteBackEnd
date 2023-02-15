CREATE TABLE `webenddb`.`userdb` (
                                     `id` INT NOT NULL AUTO_INCREMENT,
                                     `username` VARCHAR(20) NOT NULL,
                                     `status` INT NOT NULL DEFAULT 0,
                                     PRIMARY KEY (`id`),
                                     UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE,
                                     UNIQUE INDEX `username_UNIQUE` (`username` ASC) VISIBLE);