ARG ARCH="amd64"
ARG OS="linux"
FROM quay.io/prometheus/busybox-${OS}-${ARCH}:latest
LABEL maintainer="The Prometheus Authors <prometheus-developers@googlegroups.com>"

ARG ARCH="amd64"
ARG OS="linux"
COPY ./prometheus/prometheus       			 /bin/prometheus
COPY ./prometheus/promtool         			 /bin/promtool
RUN mkdir /etc/prometheus
VOLUME [ "/etc/prometheus" ]
COPY ./prometheus/prometheus.yml 			 /etc/prometheus/prometheus.yml
COPY ./prometheus/console_libraries/                     /usr/share/prometheus/console_libraries/
COPY ./prometheus/consoles/                              /usr/share/prometheus/consoles/
COPY ./prometheus/LICENSE                                /LICENSE
COPY ./prometheus/NOTICE                                 /NOTICE

RUN ln -s /usr/share/prometheus/console_libraries /usr/share/prometheus/consoles/ /etc/prometheus/
RUN mkdir -p /prometheus && \
    chown -R nobody:nogroup etc/prometheus /prometheus

USER       nobody
EXPOSE     9090
VOLUME     [ "/prometheus" ]
WORKDIR    /prometheus
ENTRYPOINT [ "/bin/prometheus" ]
CMD        [ "--config.file=/etc/prometheus/prometheus.yml", \
             "--storage.tsdb.path=/prometheus", \
             "--web.console.libraries=/usr/share/prometheus/console_libraries", \
             "--web.console.templates=/usr/share/prometheus/consoles" ]
