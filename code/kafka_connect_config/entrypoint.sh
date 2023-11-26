#!/bin/bash
cp /custom-jars/*.jar /opt/bitnami/kafka/libs
cp -r /data-generator-datasets /tmp/
#exec ./opt/bitnami/kafka/bin/connect-standalone.sh /opt/bitnami/kafka/config/connect-standalone.properties /opt/bitnami/kafka/config/CSVSourceConnector.properties
exec ./opt/bitnami/kafka/bin/connect-distributed.sh /opt/bitnami/kafka/config/connect-distributed.properties /opt/bitnami/kafka/config/CSVSourceConnector.properties
