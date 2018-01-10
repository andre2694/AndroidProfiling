CREATE DATABASE IF NOT EXISTS auth_profiling;
USE auth_profiling;
GRANT ALL PRIVILEGES ON *.* TO 'profiling'@'localhost' IDENTIFIED BY 'android-profiling';