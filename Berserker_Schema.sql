CREATE TABLE `scenarioinfo` (
  `id` int(8) NOT NULL AUTO_INCREMENT,
  `scenarioName` varchar(256) DEFAULT NULL,
  `createTime` datetime DEFAULT NULL,
  `numThreads` int(4) DEFAULT NULL,
  `rampUp` int(4) DEFAULT NULL,
  `duration` int(4) DEFAULT NULL,
  `scenarioDescription` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

CREATE TABLE `scriptfileinfo` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `scriptFileName` varchar(256) DEFAULT NULL,
  `scriptFilePath` varchar(512) DEFAULT NULL,
  `uploadTime` datetime DEFAULT NULL,
  `scenarioId` int(8) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

CREATE TABLE `paramfileinfo` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `paramFileName` varchar(256) DEFAULT NULL,
  `paramFilePath` varchar(512) DEFAULT NULL,
  `uploadTime` datetime DEFAULT NULL,
  `scenarioId` int(8) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

CREATE TABLE `scenarioresultinfo` (
  `id` int(8) NOT NULL AUTO_INCREMENT,
  `scenarioName` varchar(256) DEFAULT NULL,
  `numThreads` int(4) DEFAULT NULL,
  `rampUp` int(4) DEFAULT NULL,
  `duration` int(4) DEFAULT NULL,
  `runTime` datetime DEFAULT NULL,
  `scenarioId` int(8) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8;

CREATE TABLE `sampleresultinfo` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `timeStamp` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  `samplerLabel` varchar(64) DEFAULT NULL,
  `meanTime` int(16) DEFAULT NULL,
  `minTime` int(16) DEFAULT NULL,
  `maxTime` int(16) DEFAULT NULL,
  `standardDeviation` double(8,2) DEFAULT NULL,
  `errorPercentage` int(3) DEFAULT NULL,
  `requestRate` double(8,2) DEFAULT NULL,
  `receiveKBPerSecond` double(12,2) DEFAULT NULL,
  `sentKBPerSecond` double(12,2) DEFAULT NULL,
  `avgPageBytes` double(16,2) DEFAULT NULL,
  `threadCount` int(4) DEFAULT NULL,
  `resultId` int(8) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=18582 DEFAULT CHARSET=utf8;
