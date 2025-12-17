FROM otel/opentelemetry-collector-contrib:0.95.0

COPY otel-collector.prod.yml /etc/otelcol/config.yml

CMD ["--config=/etc/otelcol/config.yml"]
