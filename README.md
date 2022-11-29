# tut-spring-kafka-producer

Installing it on amazon EC2 with Amazon Linux 2:
1. Install docker: https://gist.github.com/npearce/6f3c7826c7499587f00957fee62f8ee9Hi

docker run -p 9010:9010 --env ACTIVE_PROFILES=prod --env KAFKA_BOOTSTRAP_SERVERS=172.31.35.102:9092 -d mephisto2120/tut-spring-kafka-producer:latest
