resource "aws_ecs_service" "api" {
  name            = "franq-management-api"
  cluster         = aws_ecs_cluster.api.id
  task_definition = aws_ecs_task_definition.api.arn

  desired_count = 1
  launch_type   = "FARGATE"

  network_configuration {
    subnets = [
      aws_subnet.public_a.id,
      aws_subnet.public_b.id
    ]

    security_groups = [
      aws_security_group.ecs.id
    ]

    assign_public_ip = true
  }

  tags = {
    Project = "franq-management-api"
  }
}