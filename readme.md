docker run -d \
--name postgres-datalayer \
-e POSTGRES_DB=datalayer \
-e POSTGRES_USER=postgres \
-e POSTGRES_PASSWORD=postgres \
-p 5432:5432 \
-v postgres_data:/var/lib/postgresql/data \
postgres:15