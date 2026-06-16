#!/bin/sh
chown -R spring:spring /app/data /app/generated-qr
exec su-exec spring java -jar /app/app.jar
