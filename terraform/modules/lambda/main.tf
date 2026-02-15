resource "aws_iam_role" "lambda" {
  name = "${var.environment}-bank-processing-lambda-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Principal = {
          Service = "lambda.amazonaws.com"
        }
      }
    ]
  })
}

resource "aws_iam_role_policy" "lambda" {
  name = "${var.environment}-bank-processing-lambda-policy"
  role = aws_iam_role.lambda.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Action = [
          "s3:GetObject",
          "s3:GetObjectVersion"
        ]
        Resource = "${var.s3_input_bucket_arn}/*"
      },
      {
        Effect = "Allow"
        Action = [
          "kafka-cluster:Connect",
          "kafka-cluster:DescribeCluster",
          "kafka-cluster:WriteData",
          "kafka-cluster:DescribeTopic"
        ]
        Resource = "*"
      },
      {
        Effect = "Allow"
        Action = [
          "logs:CreateLogGroup",
          "logs:CreateLogStream",
          "logs:PutLogEvents"
        ]
        Resource = "arn:aws:logs:*:*:*"
      }
    ]
  })
}

resource "aws_lambda_function" "dispatcher" {
  filename         = "${path.module}/../../../lambda-dispatcher/build/libs/lambda-dispatcher.jar"
  function_name    = "${var.environment}-bank-processing-dispatcher"
  role             = aws_iam_role.lambda.arn
  handler          = "com.bank.processing.dispatcher.LambdaHandler::handleRequest"
  source_code_hash = fileexists("${path.module}/../../../lambda-dispatcher/build/libs/lambda-dispatcher.jar") ? filebase64sha256("${path.module}/../../../lambda-dispatcher/build/libs/lambda-dispatcher.jar") : ""
  runtime          = "java21"
  timeout          = 60
  memory_size      = 512

  environment {
    variables = {
      MSK_BOOTSTRAP_SERVERS   = var.msk_bootstrap_servers
      MSK_TOPIC_FILE_UPLOADED = "file.uploaded"
    }
  }

  tags = {
    Name = "${var.environment}-bank-processing-dispatcher"
  }
}

resource "aws_lambda_permission" "s3" {
  statement_id  = "AllowS3Invoke"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.dispatcher.function_name
  principal     = "s3.amazonaws.com"
  source_arn    = var.s3_input_bucket_arn
}
