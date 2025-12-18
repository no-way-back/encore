FROM otel/opentelemetry-collector-contrib:0.95.0

# 프로덕션 설정 복사
COPY otel-collector.prod.yml /etc/otelcol/config.yml

# Health check
HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 \
CMD wget --no-verbose --tries=1 --spider http://localhost:13133/ || exit 1

# 실행
CMD ["--config=/etc/otelcol/config.yml"]