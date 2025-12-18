FROM grafana/grafana:latest

# ===== 환경 변수 (필요 시 ECS Task Definition에서 override 가능) =====
ENV GF_SECURITY_ADMIN_USER=admin \
    GF_SECURITY_ADMIN_PASSWORD=admin \
    GF_USERS_ALLOW_SIGN_UP=false

# ===== Datasource provisioning =====
COPY grafana-datasources.yml /etc/grafana/provisioning/datasources/datasources.yml

# ===== 권한 보정 (공식 이미지 권장 방식) =====
USER root
RUN chown -R grafana:grafana /etc/grafana/provisioning
USER grafana

EXPOSE 3000
