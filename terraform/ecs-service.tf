resource "aws_ecs_service" "api" {
  name            = "franq-management-api"
  cluster         = aws_ecs_cluster.api.id
  task_definition = aws_ecs_task_definition.api.arn

  desired_count = 1
  launch_type   = "FARGATE"

  enable_execute_command = true

  health_check_grace_period_seconds = 120

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

  load_balancer {
    target_group_arn = aws_lb_target_group.api.arn
    container_name   = "franq-management-api"
    container_port   = 8080
  }

  tags = {
    Project = "franq-management-api"
  }
}