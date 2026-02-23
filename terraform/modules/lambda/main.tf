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

data "aws_iam_policy_document" "lambda_policy" {

  # S3 access
  statement {
    sid    = "AllowReadInputBucket"
    effect = "Allow"

    actions = [
      "s3:GetObject",
      "s3:GetObjectVersion"
    ]

    resources = [
      "${var.s3_input_bucket_arn}/*"
    ]
  }

  # MSK IAM Auth
  statement {
    sid    = "AllowMSKAccess"
    effect = "Allow"

    actions = [
      "kafka-cluster:Connect",
      "kafka-cluster:DescribeCluster",
      "kafka-cluster:DescribeClusterDynamicConfiguration",
      "kafka-cluster:WriteData",
      "kafka-cluster:DescribeTopic"
    ]

    resources = [
      var.msk_cluster_arn,
      "${var.msk_cluster_arn}/*"
    ]
  }

  # CloudWatch Logs
  statement {
    sid    = "AllowLogs"
    effect = "Allow"

    actions = [
      "logs:CreateLogGroup",
      "logs:CreateLogStream",
      "logs:PutLogEvents"
    ]

    resources = [
      "arn:aws:logs:${var.aws_region}:${var.aws_account_id}:*"
    ]
  }

  # REQUIRED for Lambda in VPC
  statement {
    sid    = "AllowVPCAccess"
    effect = "Allow"

    actions = [
      "ec2:CreateNetworkInterface",
      "ec2:DescribeNetworkInterfaces",
      "ec2:DeleteNetworkInterface"
    ]

    resources = ["*"]
  }
}

resource "aws_iam_role_policy" "lambda" {
  name   = "${var.environment}-bank-processing-lambda-policy"
  role   = aws_iam_role.lambda.id
  policy = data.aws_iam_policy_document.lambda_policy.json
}

resource "aws_lambda_function" "dispatcher" {
  function_name = "${var.environment}-bank-processing-dispatcher"
  filename = "${path.root}/../lambda-dispatcher/target/lambda-dispatcher-*.jar"
  source_code_hash = filebase64sha256("${path.root}/../lambda-dispatcher/target/lambda-dispatcher-0.0.1-SNAPSHOT.jar")
  role             = aws_iam_role.lambda.arn
  handler          = "com.bank.processing.dispatcher.LambdaHandler::handleRequest"
  runtime          = "java21"
  timeout          = 60
  memory_size      = 512
  publish          = true

  vpc_config {
    subnet_ids         = var.private_subnet_ids
    security_group_ids = [var.lambda_security_group_id]
  }

  environment {
    variables = {
      MSK_BOOTSTRAP_SERVERS   = var.msk_bootstrap_servers
      MSK_CLUSTER_ARN         = var.msk_cluster_arn
      MSK_TOPIC_FILE_UPLOADED = "file.uploaded"
      ENVIRONMENT             = var.environment
    }
  }

  tags = {
    Name = "${var.environment}-bank-processing-dispatcher"
    Environment = var.environment
  }
}

resource "aws_lambda_permission" "allow_s3" {
  statement_id  = "AllowS3Invoke"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.dispatcher.function_name
  principal     = "s3.amazonaws.com"
  source_arn    = var.s3_input_bucket_arn
}
