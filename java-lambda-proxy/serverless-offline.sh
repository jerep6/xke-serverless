#!/usr/bin/env bash
PORT=$1
mvn clean verify -Pintegration-test -Dit.test=ServerlessOffline -Dport="$PORT"