FROM debian:bookworm-slim

# Optional: system tools and X11 dependencies for GUI
RUN apt-get update && apt-get install -y \
    openjdk-17-jre \
    libxext6 libxrender1 libxtst6 libxi6 libxrandr2 libfreetype6 \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY ssplot.jar .
COPY run.sh .
RUN chmod +x run.sh

ENTRYPOINT ["./run.sh"]
