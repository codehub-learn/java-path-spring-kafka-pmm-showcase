FROM bellsoft/liberica-openjre-debian:17

RUN apt-get update && apt-get install -y curl

ENV KAFKA_VERSION 3.4.0
ENV SCALA_VERSION 2.13

RUN mkdir /tmp/kafka && \
    curl "https://downloads.apache.org/kafka/${KAFKA_VERSION}/kafka_${SCALA_VERSION}-${KAFKA_VERSION}.tgz" \
    -o /tmp/kafka/kafka.tgz && \
    mkdir /kafka && cd /kafka && \
    tar -xvzf /tmp/kafka/kafka.tgz --strip 1

RUN mkdir -p /data/kafka

COPY start-kafka.sh /usr/bin

RUN chmod +x /usr/bin/start-kafka.sh

CMD ["/usr/bin/start-kafka.sh"]
