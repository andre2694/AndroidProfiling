CREATE USER 'profiling'@'localhost' IDENTIFIED BY 'android-profiling';
CREATE DATABASE IF NOT EXISTS android_profiling;
USE android_profiling;
GRANT ALL PRIVILEGES ON *.* TO 'profiling'@'localhost' IDENTIFIED BY 'android-profiling';