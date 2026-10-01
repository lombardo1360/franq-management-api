resource "aws_ecr_repository" "api" {
  name                 = "franq-management-api"
  image_tag_mutability = "MUTABLE"

  image_scanning_configuration {
    scan_on_push = true
  }

  tags = {
    Project = "franq-management-api"
  }
}