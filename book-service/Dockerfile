FROM eclipse-temurin:21-jdk

ADD target/*.jar /opt
RUN mv /opt/*.jar /opt/book-service.jar

ENTRYPOINT exec java \
              -Xms128m \
              -Xmx512m \
              -server \
              -Dspring.config.location=/opt/application.properties \
              -Duser.timezone=America/Sao_Paulo \
              -Dfile.encoding=UTF-8 \
              -Dnetworkaddress.cache.ttl=60 \
              -Dnetworkaddress.cache.negative.ttl=5 \
              -XX:+UseG1GC \
              -XX:MaxGCPauseMillis=100 \
              -xx:InitiatingHeapOccupancyPercent=35 \
              -XX:+DisableExplicitGC \
              -XX:ParallelGCThreads=4 \
              -XX:+UseAdaptiveSizePolicy \
              -XX:+UseCompressedOops \
              -XX:MaxMetaspaceSize=256m \
              -XX:+HeapDumpOnOutOfMemoryError \
              -jar /opt/book-service.jar