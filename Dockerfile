FROM ubuntu:latest
LABEL authors="HYPERPC"

ENTRYPOINT ["top", "-b"]