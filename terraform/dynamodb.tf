resource "aws_dynamodb_table" "franchise_management" {
  name         = "franchise-management"
  billing_mode = "PAY_PER_REQUEST"

  hash_key  = "PK"
  range_key = "SK"

  attribute {
    name = "PK"
    type = "S"
  }

  attribute {
    name = "SK"
    type = "S"
  }

  tags = {
    Project = "franq-management-api"
  }
}