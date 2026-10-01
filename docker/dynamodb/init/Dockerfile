FROM amazon/aws-cli:latest

COPY create-table.sh /create-table.sh

RUN chmod +x /create-table.sh

ENTRYPOINT ["/create-table.sh"]