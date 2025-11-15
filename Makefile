.PHONY: test start-service

test:
	cd test && docker build -t hotelio-tester .
	docker run --rm --network=host\
	  -e DB_HOST=127.0.0.1 \
	  -e DB_PORT=5432 \
	  -e DB_NAME=hotelio \
	  -e DB_USER=hotelio \
	  -e DB_PASSWORD=hotelio \
	  -e API_URL=http://127.0.0.1:8084 \
	  hotelio-tester

start-service-task1:
	cd tasks/task1 && docker compose down -v && docker compose up -d --build

start-service-task2:
	cd tasks/task2 && docker compose down -v && docker compose up -d --build

stop-service-task2:
	cd tasks/task2 && docker compose down -v
