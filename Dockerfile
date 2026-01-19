FROM ubuntu:latest
LABEL authors="taras"

ENTRYPOINT ["top", "-b"]