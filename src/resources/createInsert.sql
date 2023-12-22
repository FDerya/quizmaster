SELECT * FROM quizmaster.quiz;

ALTER TABLE `quizmaster`.`question` 
CHANGE COLUMN `answerGood` `answerRight` VARCHAR(200) NOT NULL ;


ALTER TABLE `quizmaster`.`question` 

CHANGE COLUMN `question` `question` VARCHAR(1000) NOT NULL ;


ALTER TABLE `quizmaster`.`question` 

CHANGE COLUMN `answerRight` `answerRight` VARCHAR(1000) NOT NULL ,

CHANGE COLUMN `answerWrong1` `answerWrong1` VARCHAR(1000) NOT NULL ,

CHANGE COLUMN `answerWrong2` `answerWrong2` VARCHAR(1000) NOT NULL ,

CHANGE COLUMN `answerWrong3` `answerWrong3` VARCHAR(1000) NOT NULL ;
