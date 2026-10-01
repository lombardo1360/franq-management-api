resource "aws_ecs_cluster" "api" {
  name = "franq-management-api"

  tags = {
    Project = "franq-management-api"
  }
}

resource "aws_ecs_task_definition" "api" {
  family                   = "franq-management-api"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]

  cpu    = "256"
  memory = "512"

  execution_role_arn = aws_iam_role.ecs_task_execution.arn
  task_role_arn      = aws_iam_role.ecs_task.arn

  container_definitions = jsonencode([
    {
      name      = "franq-management-api"
      image     = "${aws_ecr_repository.api.repository_url}:latest"
      essential = true

      portMappings = [
        {
          containerPort = 8080
          hostPort      = 8080
          protocol      = "tcp"
        }
      ]

      logConfiguration = {
        logDriver = "awslogs"

        options = {
          "awslogs-group"         = aws_cloudwatch_log_group.api.name
          "awslogs-region"        = "us-east-1"
          "awslogs-stream-prefix" = "api"
        }
      }
    }
  ])

  tags = {
    Project = "franq-management-api"
  }
}