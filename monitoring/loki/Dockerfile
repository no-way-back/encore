FROM grafana/loki:latest

# Loki 설정 파일
COPY --chown=loki:root loki-config.prod.yml /etc/loki/config.yml

EXPOSE 3100 9096

ENTRYPOINT ["/usr/bin/loki"]
CMD ["-config.file=/etc/loki/config.yml"]
