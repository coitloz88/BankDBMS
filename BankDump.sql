-- --------------------------------------------------------
-- 호스트:                          127.0.0.1
-- 서버 버전:                        8.0.27 - MySQL Community Server - GPL
-- 서버 OS:                        Win64
-- HeidiSQL 버전:                  11.3.0.6295
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- bankapp 데이터베이스 구조 내보내기
CREATE DATABASE IF NOT EXISTS `bankapp` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `bankapp`;

-- 테이블 bankapp.account 구조 내보내기
CREATE TABLE IF NOT EXISTS `account` (
  `AccountID` char(13) NOT NULL DEFAULT '0',
  `Balance` bigint NOT NULL DEFAULT '0',
  `Password` char(4) NOT NULL DEFAULT '0',
  `isMinus` tinyint NOT NULL DEFAULT '0',
  `AcBranchID` int unsigned NOT NULL,
  `AcUserID` int unsigned NOT NULL,
  `StartDate` date NOT NULL,
  PRIMARY KEY (`AccountID`),
  KEY `FK_AcBranchID` (`AcBranchID`),
  KEY `FK_AcUserID` (`AcUserID`),
  CONSTRAINT `FK_AcBranchID` FOREIGN KEY (`AcBranchID`) REFERENCES `bankbranch` (`BranchID`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FK_AcUserID` FOREIGN KEY (`AcUserID`) REFERENCES `user` (`UserID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 테이블 데이터 bankapp.account:~8 rows (대략적) 내보내기
/*!40000 ALTER TABLE `account` DISABLE KEYS */;
INSERT INTO `account` (`AccountID`, `Balance`, `Password`, `isMinus`, `AcBranchID`, `AcUserID`, `StartDate`) VALUES
	('000-0000-0001', 14600, '4466', 1, 1, 2, '2021-12-01'),
	('000-0000-0002', 64550, '1234', -1, 1, 8, '2021-12-01'),
	('000-0000-0004', 29500, '6543', 1, 0, 1, '2021-12-01'),
	('000-0000-0006', 4500, '3573', 1, 2, 2, '2021-12-01'),
	('000-0000-0008', 0, '7897', 1, 2, 1, '2021-12-01'),
	('000-0000-0009', 0, '3366', -1, 2, 112233, '2021-12-01'),
	('000-0000-0010', 3450, '0010', 1, 3, 112233, '2021-12-03');
/*!40000 ALTER TABLE `account` ENABLE KEYS */;

-- 테이블 bankapp.actransaction 구조 내보내기
CREATE TABLE IF NOT EXISTS `actransaction` (
  `acTimeStamp` datetime NOT NULL,
  `acType` tinyint NOT NULL DEFAULT '0',
  `amount` int unsigned NOT NULL DEFAULT '0',
  `TBranchID` int unsigned NOT NULL,
  `TAccountID` char(13) NOT NULL DEFAULT '',
  PRIMARY KEY (`acTimeStamp`,`TBranchID`,`TAccountID`) USING BTREE,
  KEY `FK_TAccountID` (`TAccountID`),
  KEY `FK_TBranchID` (`TBranchID`),
  CONSTRAINT `FK_TAccountID` FOREIGN KEY (`TAccountID`) REFERENCES `account` (`AccountID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_TBranchID` FOREIGN KEY (`TBranchID`) REFERENCES `bankbranch` (`BranchID`) ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 테이블 데이터 bankapp.actransaction:~18 rows (대략적) 내보내기
/*!40000 ALTER TABLE `actransaction` DISABLE KEYS */;
INSERT INTO `actransaction` (`acTimeStamp`, `acType`, `amount`, `TBranchID`, `TAccountID`) VALUES
	('2021-12-03 02:12:45', 1, 4500, 2, '000-0000-0006'),
	('2021-12-03 03:51:30', 1, 10000, 0, '000-0000-0004'),
	('2021-12-03 03:51:50', -1, 8000, 0, '000-0000-0004'),
	('2021-12-03 03:52:10', 1, 20000, 0, '000-0000-0004'),
	('2021-12-03 04:39:48', 1, 10000, 3, '000-0000-0002'),
	('2021-12-03 04:46:10', -1, 50, 3, '000-0000-0002'),
	('2021-12-03 04:46:21', 1, 4550, 3, '000-0000-0002'),
	('2021-12-03 04:47:00', 1, 1000, 3, '000-0000-0009'),
	('2021-12-03 04:47:01', 1, 500, 2, '000-0000-0002'),
	('2021-12-03 04:47:42', -1, 1000, 3, '000-0000-0009'),
	('2021-12-03 06:09:19', -1, 450, 0, '000-0000-0002'),
	('2021-12-03 15:50:02', 1, 50000, 1, '000-0000-0002'),
	('2021-12-03 15:50:17', 1, 7500, 1, '000-0000-0004'),
	('2021-12-03 15:52:42', 1, 20000, 1, '000-0000-0001'),
	('2021-12-03 15:53:19', -1, 5400, 1, '000-0000-0001'),
	('2021-12-03 17:39:36', -1, 50, 3, '000-0000-0010'),
	('2021-12-03 18:31:01', 1, 3500, 2, '000-0000-0010');
/*!40000 ALTER TABLE `actransaction` ENABLE KEYS */;

-- 테이블 bankapp.administrator 구조 내보내기
CREATE TABLE IF NOT EXISTS `administrator` (
  `AdminID` int unsigned NOT NULL,
  `Fname` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `Lname` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `phoneNum` char(13) NOT NULL,
  `Ad_state` varchar(16) DEFAULT NULL,
  `Ad_details` varchar(48) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `BirthDate` date NOT NULL,
  `AdBranchID` int unsigned NOT NULL,
  PRIMARY KEY (`AdminID`),
  KEY `FK_AdBranchID` (`AdBranchID`),
  CONSTRAINT `FK_AdBranchID` FOREIGN KEY (`AdBranchID`) REFERENCES `bankbranch` (`BranchID`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 테이블 데이터 bankapp.administrator:~7 rows (대략적) 내보내기
/*!40000 ALTER TABLE `administrator` DISABLE KEYS */;
INSERT INTO `administrator` (`AdminID`, `Fname`, `Lname`, `phoneNum`, `Ad_state`, `Ad_details`, `BirthDate`, `AdBranchID`) VALUES
	(1, 'Jintae', 'Lee', '010-1111-2222', 'Seoul', 'Junggu', '1989-05-11', 0),
	(2, 'Byul', 'Park', '010-1111-3333', 'Seoul', 'Junggu', '1992-01-24', 0),
	(5, 'Hee', 'Bae', '010-6547-3246', 'Ulsan', 'Namgu', '1995-01-31', 2),
	(6, 'Gildong', 'Kim', '010-7000-9000', 'Busan', 'Semyeon', '1991-09-22', 2),
	(10, 'Hanbyul', 'Choi', '010-1265-8954', 'Seoul', 'Gangnamgu', '1995-11-03', 1),
	(11221122, 'Tom', 'Smith', '010-5511-1155', 'Busan', 'Gijanggun', '1988-02-17', 3),
	(33223322, 'Jenny', 'Hwang', '010-4444-0100', 'Busan', 'Gijanggun', '1990-03-08', 3);
/*!40000 ALTER TABLE `administrator` ENABLE KEYS */;

-- 테이블 bankapp.bankbranch 구조 내보내기
CREATE TABLE IF NOT EXISTS `bankbranch` (
  `BranchID` int unsigned NOT NULL,
  `Lo_state` varchar(16) NOT NULL DEFAULT '',
  `Lo_details` varchar(48) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `ManagerID` int unsigned DEFAULT NULL,
  PRIMARY KEY (`BranchID`),
  KEY `FK_ManagerID` (`ManagerID`),
  CONSTRAINT `FK_ManagerID` FOREIGN KEY (`ManagerID`) REFERENCES `administrator` (`AdminID`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 테이블 데이터 bankapp.bankbranch:~4 rows (대략적) 내보내기
/*!40000 ALTER TABLE `bankbranch` DISABLE KEYS */;
INSERT INTO `bankbranch` (`BranchID`, `Lo_state`, `Lo_details`, `ManagerID`) VALUES
	(0, 'online', '-', 1),
	(1, 'Seoul', 'Junggu', 10),
	(2, 'Busan', 'Sasanggu', 5),
	(3, 'Busan', 'Gijanggun', 11221122);
/*!40000 ALTER TABLE `bankbranch` ENABLE KEYS */;

-- 테이블 bankapp.dbmanager 구조 내보내기
CREATE TABLE IF NOT EXISTS `dbmanager` (
  `DBManagerID` int unsigned NOT NULL,
  `Fname` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `Lname` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `phoneNum` char(13) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `Ad_state` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `Ad_details` varchar(48) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `BirthDate` date NOT NULL,
  `Password` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  PRIMARY KEY (`DBManagerID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 테이블 데이터 bankapp.dbmanager:~2 rows (대략적) 내보내기
/*!40000 ALTER TABLE `dbmanager` DISABLE KEYS */;
INSERT INTO `dbmanager` (`DBManagerID`, `Fname`, `Lname`, `phoneNum`, `Ad_state`, `Ad_details`, `BirthDate`, `Password`) VALUES
	(1, 'Garam', 'Jung', '010-4656-5645', 'Seoul', 'Mapogu', '1979-09-03', 'dksldy'),
	(2, 'Yuri', 'Lee', '010-5472-0210', 'Seoul', 'Mapogu', '1996-05-05', '1234');
/*!40000 ALTER TABLE `dbmanager` ENABLE KEYS */;

-- 테이블 bankapp.user 구조 내보내기
CREATE TABLE IF NOT EXISTS `user` (
  `UserID` int unsigned NOT NULL,
  `Fname` varchar(16) NOT NULL DEFAULT '',
  `Lname` varchar(12) NOT NULL DEFAULT '',
  `phoneNum` char(13) NOT NULL DEFAULT '',
  `Ad_state` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '',
  `Ad_details` varchar(48) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `BirthDate` date NOT NULL,
  PRIMARY KEY (`UserID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 테이블 데이터 bankapp.user:~5 rows (대략적) 내보내기
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` (`UserID`, `Fname`, `Lname`, `phoneNum`, `Ad_state`, `Ad_details`, `BirthDate`) VALUES
	(1, 'Younghee', 'Kim', '010-1234-5678', 'Seoul', 'Gangnamgu', '2001-01-01'),
	(2, 'Gildong', 'Hong', '010-0000-5678', 'Ulsan', 'Namgu', '1998-01-25'),
	(8, 'Jenny', 'Kim', '010-6535-3481', 'Busan', 'Gijanggun', '1988-03-06'),
	(112233, 'Thomas', 'Campbell', '010-5821-4697', 'Seoul', 'Gangnamgu', '2001-08-23'),
	(12341234, 'Luke', 'Watson', '010-3584-1274', 'Seoul', 'Mapogu', '1987-10-02');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
