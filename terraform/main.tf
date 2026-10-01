provider "aws" {
  region = var.aws_region
}

locals {
  project_name = "franq-management-api"
}