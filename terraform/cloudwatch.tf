resource "aws_cloudwatch_log_group" "api" {
  name              = "/ecs/franq-management-api"
  retention_in_days = 7

  tags = {
    Project = "franq-management-api"
  }
}