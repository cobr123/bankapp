FROM grafana/grafana:12.2.0
ENV GF_SECURITY_ADMIN_USER=admin
ENV GF_SECURITY_ADMIN_PASSWORD=admin
ADD ./dashboards/spring-boot.json /var/lib/grafana/dashboards/
ADD ./provisioning/dashboards/dashboards.yml /etc/grafana/provisioning/dashboards/
ADD ./provisioning/datasources/datasources.yml /etc/grafana/provisioning/datasources/