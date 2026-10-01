resource "aws_iam_role_policy" "github_actions" {
  name = "franq-management-api-github-actions-policy"
  role = aws_iam_role.github_actions.id

  policy = jsonencode({
    Version = "2012-10-17"

    Statement = [
      {
        Effect = "Allow"

        Action = [
          "ecr:GetAuthorizationToken"
        ]

        Resource = "*"
      },
      {
        Effect = "Allow"

        Action = [
          "ecr:BatchCheckLayerAvailability",
          "ecr:CompleteLayerUpload",
          "ecr:InitiateLayerUpload",
          "ecr:PutImage",
          "ecr:UploadLayerPart"
        ]

        Resource = aws_ecr_repository.api.arn
      },
      {
        Effect = "Allow"

        Action = [
          "ecs:DescribeServices",
          "ecs:DescribeTaskDefinition",
          "ecs:UpdateService"
        ]

        Resource = [
          aws_ecs_cluster.api.arn,
          aws_ecs_service.api.id,
          aws_ecs_task_definition.api.arn
        ]
      },
      {
        Effect = "Allow"

        Action = [
          "iam:PassRole"
        ]

        Resource = [
          aws_iam_role.ecs_task_execution.arn,
          aws_iam_role.ecs_task.arn
        ]
      }
    ]
  })
}