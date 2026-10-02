resource "aws_lb" "api" {
  name               = "franq-management-api"
  internal           = false
  load_balancer_type = "application"

  security_groups = [
    aws_security_group.alb.id
  ]

  subnets = [
    aws_subnet.public_a.id,
    aws_subnet.public_b.id
  ]

  tags = {
    Project = "franq-management-api"
  }
}


resource "aws_lb_target_group" "api" {
  name        = "franq-management-api-v2"
  port        = 8080
  protocol    = "HTTP"
  target_type = "ip"
  vpc_id      = aws_vpc.main.id

  health_check {
    enabled             = true
    path                = "/actuator/health/readiness"
    protocol            = "HTTP"
    port                = "traffic-port"

    healthy_threshold   = 2
    unhealthy_threshold = 3

    timeout  = 5
    interval = 30

    matcher = "200"
  }

  lifecycle {
    create_before_destroy = true
  }

  tags = {
    Project = "franq-management-api"
  }
}


resource "aws_lb_listener" "api" {
  load_balancer_arn = aws_lb.api.arn
  port              = 80
  protocol          = "HTTP"

  default_action {
    type = "forward"

    forward {
      target_group {
        arn = aws_lb_target_group.api.arn
      }
    }
  }
}

