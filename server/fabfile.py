from fabric.api import local
import os

def run_server():
    local('mvn compile exec:java -Dexec.mainClass=\"server.Server\" > server.txt')


def run_auth():
    local('mvn compile exec:java -Dexec.mainClass=\"server.AuthServer\" > auth.txt')


def init_db():
    local("mysql -u root -p < src/main/java/dbutils/setup.sql") # this one comes first
    local("mysql -u root -p < src/main/java/dbutils/authsetup.sql")

def create_db():
    local("mysql -u profiling -pandroid-profiling < src/main/java/dbutils/init.sql")
    local("mysql -u profiling -pandroid-profiling < src/main/java/dbutils/auth.sql")


def clean_db():
    local("mysql -u profiling -pandroid-profiling -e 'DROP DATABASE IF EXISTS android_profiling'")
    local("mysql -u profiling -pandroid-profiling -e 'CREATE DATABASE IF NOT EXISTS android_profiling'")
    local("mysql -u profiling -pandroid-profiling -e 'DROP DATABASE IF EXISTS auth_profiling'")
    local("mysql -u profiling -pandroid-profiling -e 'CREATE DATABASE IF NOT EXISTS auth_profiling'")


def test():
    clean_db()
    create_db()
    local('mvn clean compile test')

def test_this(msg):
    local('mvn clean compile -Dtest=' + msg + " test")
