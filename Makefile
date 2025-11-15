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

build-service-task2:
	cd tasks/task2/booking-service && docker build -f Dockerfile.build -t booking-service-build . \
		&& docker run --rm -v $(PWD)/tasks/task2/booking-service:/app booking-service-build gradle build --no-daemon

stop-service-task2:
	cd tasks/task2 && docker compose down -v

build-monolith-task2:
	cd hotelio-monolith && docker build -f ../tasks/task2/booking-service/Dockerfile.build -t hotelio-monolith-build . \
		&& docker run --rm -v $(PWD)/hotelio-monolith:/app hotelio-monolith-build gradle build --no-daemon
	cp hotelio-monolith/build/libs/hotelio-monolith-1.0.0.jar tasks/monolith/
