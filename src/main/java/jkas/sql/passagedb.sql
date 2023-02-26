CREATE TABLE `webenddb`.`passagedb` (
                                        `id` INT NOT NULL AUTO_INCREMENT,
                                        `title` VARCHAR(60) NOT NULL,
                                        `authorid` INT NOT NULL COMMENT 'Using the userid',
                                        `date` DATE NOT NULL,
                                        `visible` INT NOT NULL DEFAULT 1,
                                        `content` MEDIUMTEXT NOT NULL,
                                        PRIMARY KEY (`id`),
                                        UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE,
                                        UNIQUE INDEX `authorid_UNIQUE` (`authorid` ASC) VISIBLE);
