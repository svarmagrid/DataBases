# Running the Database

Start the PostgreSQL container:

docker-compose up -d

The database schema is automatically initialized using:

db/init.sql

The script uses CREATE TABLE IF NOT EXISTS, so restarting
the container will not cause failures if the tables already exist.

Database credentials:

Host: localhost
Port: 5432
Database: university
User: admin
Password: password

