### Local PostgreSQL Database Setup

This submodule provides a Docker Compose manifest to spin up a local PostgreSQL database for development and testing. The setup uses environment variables for sensitive and configurable parameters and persists data using a Docker volume.

### Prerequisites

1) Docker installed

2) Docker Compose installed

3) A database client like DBeaver

### Steps to Run

1. Make sure Docker and Docker Compose are installed.
2. Create a `.env` file in the root directory with the following variables:

   POSTGRES_DB=mydatabase
   POSTGRES_USER=myuser
   POSTGRES_PASSWORD=mypassword
   DB_PORT=5432
   DB_CONTAINER_PORT=5432
   DB_VOLUME=postgres_data
3. Start the database container:

    docker-compose -f docker-compose.yaml up -d
4. Verify the container is running:
    docker ps
5. Connect to the database using your favorite client (e.g., DBeaver):

    Host: localhost

    Port: 5432

    Database: mydatabase

    User: myuser

    Password: mypassword
6. Stop and Remove Containers:

   docker-compose down