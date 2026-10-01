output "dynamodb_table_name" {
  value = aws_dynamodb_table.franchise_management.name
}

output "ecr_repository_url" {
  value = aws_ecr_repository.api.repository_url
}