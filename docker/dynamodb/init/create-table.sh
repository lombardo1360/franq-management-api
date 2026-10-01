#!/bin/sh

set -e

echo "Waiting for DynamoDB..."

until curl -s http://dynamodb:8000 > /dev/null; do
    sleep 2
done

echo "Creating DynamoDB table..."

aws dynamodb create-table \
    --table-name franchise-management \
    --attribute-definitions \
        AttributeName=PK,AttributeType=S \
        AttributeName=SK,AttributeType=S \
    --key-schema \
        AttributeName=PK,KeyType=HASH \
        AttributeName=SK,KeyType=RANGE \
    --billing-mode PAY_PER_REQUEST \
    --endpoint-url http://dynamodb:8000 \
    --region us-east-1

echo "DynamoDB table created successfully."