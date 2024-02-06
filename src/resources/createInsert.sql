CREATE DATABASE  IF NOT EXISTS `quizmaster` /*!40100 DEFAULT CHARACTER SET utf8mb3 */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `quizmaster`;
-- MySQL dump 10.13  Distrib 8.0.34, for Win64 (x86_64)
--
-- Host: localhost    Database: quizmaster
-- ------------------------------------------------------
-- Server version	8.0.34

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `course`
--

DROP TABLE IF EXISTS `course`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `course` (
  `idCourse` int NOT NULL AUTO_INCREMENT,
  `idUser` int NOT NULL,
  `nameCourse` varchar(45) NOT NULL,
  `difficultyCourse` set('Beginner','Medium','Gevorderd') NOT NULL,
  PRIMARY KEY (`idCourse`),
  UNIQUE KEY `idCourse_UNIQUE` (`idCourse`),
  UNIQUE KEY `nameCourse_UNIQUE` (`nameCourse`),
  KEY `verzinzelf3_idx` (`idUser`),
  CONSTRAINT `is coordinator of` FOREIGN KEY (`idUser`) REFERENCES `user` (`idUser`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `group`
--

DROP TABLE IF EXISTS `group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `group` (
  `idGroup` int NOT NULL AUTO_INCREMENT,
  `idUser` int NOT NULL,
  `nameGroup` varchar(45) NOT NULL,
  `amountStudent` int NOT NULL,
  `idCourse` int NOT NULL,
  PRIMARY KEY (`idGroup`),
  UNIQUE KEY `idGroup_UNIQUE` (`idGroup`),
  KEY `verzinzelf2_idx` (`idUser`),
  KEY `teaches_idx` (`idCourse`),
  CONSTRAINT `is administrator of` FOREIGN KEY (`idUser`) REFERENCES `user` (`idUser`),
  CONSTRAINT `teaches` FOREIGN KEY (`idCourse`) REFERENCES `course` (`idCourse`)
) ENGINE=InnoDB AUTO_INCREMENT=50 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `participation`
--

DROP TABLE IF EXISTS `participation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `participation` (
  `idUser` int NOT NULL,
  `idCourse` int NOT NULL,
  `idGroup` int DEFAULT NULL,
  PRIMARY KEY (`idUser`,`idCourse`),
  KEY `verzinzelf6_idx` (`idGroup`),
  KEY `verzinzelf5_idx` (`idUser`),
  KEY `verzinzelf7_idx` (`idCourse`),
  CONSTRAINT `is divided into` FOREIGN KEY (`idGroup`) REFERENCES `group` (`idGroup`),
  CONSTRAINT `is teacher of` FOREIGN KEY (`idUser`) REFERENCES `user` (`idUser`),
  CONSTRAINT `parts of` FOREIGN KEY (`idCourse`) REFERENCES `course` (`idCourse`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `question`
--

DROP TABLE IF EXISTS `question`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `question` (
  `idQuestion` int NOT NULL AUTO_INCREMENT,
  `idQuiz` int NOT NULL,
  `question` varchar(1000) NOT NULL,
  `answerRight` varchar(1000) NOT NULL,
  `answerWrong1` varchar(1000) NOT NULL,
  `answerWrong2` varchar(1000) NOT NULL,
  `answerWrong3` varchar(1000) NOT NULL,
  PRIMARY KEY (`idQuestion`),
  UNIQUE KEY `idQuestion_UNIQUE` (`idQuestion`),
  KEY `verzinzelf4_idx` (`idQuiz`),
  CONSTRAINT `is a part of` FOREIGN KEY (`idQuiz`) REFERENCES `quiz` (`idQuiz`)
) ENGINE=InnoDB AUTO_INCREMENT=90 DEFAULT CHARSET=utf8mb3 COMMENT='	';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `quiz`
--

DROP TABLE IF EXISTS `quiz`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `quiz` (
  `idQuiz` int NOT NULL AUTO_INCREMENT,
  `idCourse` int NOT NULL,
  `nameQuiz` varchar(45) NOT NULL,
  `levelQuiz` set('Beginner','Medium','Gevorderd') NOT NULL,
  `amountQuestion` int NOT NULL,
  PRIMARY KEY (`idQuiz`),
  UNIQUE KEY `nameQuiz_UNIQUE` (`nameQuiz`),
  UNIQUE KEY `idQuiz_UNIQUE` (`idQuiz`),
  KEY `verzinzelf1_idx` (`idCourse`),
  CONSTRAINT `is part of` FOREIGN KEY (`idCourse`) REFERENCES `course` (`idCourse`)
) ENGINE=InnoDB AUTO_INCREMENT=129 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `idUser` int NOT NULL AUTO_INCREMENT,
  `username` varchar(10) NOT NULL,
  `password` varchar(25) NOT NULL,
  `firstName` varchar(45) NOT NULL,
  `prefix` varchar(10) DEFAULT NULL,
  `surname` varchar(45) NOT NULL,
  `role` set('Student','Docent','Coördinator','Administrator','Functioneel Beheerder') NOT NULL,
  PRIMARY KEY (`idUser`),
  UNIQUE KEY `idUser_UNIQUE` (`idUser`),
  UNIQUE KEY `username_UNIQUE` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=213 DEFAULT CHARSET=utf8mb3 COMMENT='										';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-02-06 13:21:45
