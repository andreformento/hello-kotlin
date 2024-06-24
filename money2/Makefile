start-database:
	@docker rm -f database || true
	@docker run -d \
	            --name=database \
	            -p 5432:5432 \
	            -e POSTGRES_DB=mydb \
	            -e POSTGRES_USER=user \
	            -e POSTGRES_PASSWORD=password \
	            postgres:13.3

wait-for-database: start-database
	@until docker run --rm -it \
                      -e PGPASSWORD=password \
                      --link \
                      database:pg \
                      postgres:13.3 psql -U user -h pg -c "select 1" -d mydb; do sleep 0.5; done

migrate-database: wait-for-database
	@docker run --rm \
                --link database:pg \
                -v $$(pwd)/migrations:/flyway/sql:ro \
                -e FLYWAY_EDITION=community \
                flyway/flyway:7.15.0 \
                -url=jdbc:postgresql://database:5432/mydb -schemas=public -user=user -password=password migrate
	@until docker run --rm -it \
                      -e PGPASSWORD=password \
                      --link \
                      database:pg \
                      postgres:13.3 psql -U user -h pg -c "select 1 from account limit 1" -d mydb; do sleep 0.5; done

run: migrate-database
	@./gradlew bootRun

test: migrate-database
	@./gradlew clean test

signup:
	curl 'http://localhost:8080/user/auth/signup' \
         -H 'Content-Type: application/json' \
         --data-raw '{"name":"Batman", "email":"a@a.com", "password":"pass123"}'

login:
	curl -c cookies.txt \
         'http://localhost:8080/user/auth/login' \
         -H 'Content-Type: application/json' \
         --data-raw '{"email":"a@a.com", "password":"pass123"}'

get-user:
	curl -b cookies.txt 'http://localhost:8080/user'

create-organization:
	curl -b cookies.txt 'http://localhost:8080/organizations' \
         -H 'Content-Type: application/json' \
         --data-raw '{"name":"my org", "description": "blah"}'

get-organizations:
	curl -b cookies.txt 'http://localhost:8080/organizations'
