FROM grafana/loki:latest

# ===== Loki 설정 파일 =====
COPY loki-config.prod.yml /etc/loki/config.yml

# ===== Loki 데이터 디렉토리 =====
RUN mkdir -p /loki && \
    chown -R loki:loki /loki

USER loki

EXPOSE 3100 9096

ENTRYPOINT ["/usr/bin/loki"]
CMD ["-config.file=/etc/loki/config.yml"]
