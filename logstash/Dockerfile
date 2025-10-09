FROM logstash:9.1.5
RUN /usr/share/logstash/bin/logstash-plugin install logstash-output-elasticsearch
ADD pipelines.yml /usr/share/logstash/config/
ADD bankapp-pipeline.conf /usr/share/logstash/pipeline/