resource "aws_s3_bucket" "input" {
  bucket = "${var.environment}-bank-input-${data.aws_caller_identity.current.account_id}"

  tags = {
    Name = "${var.environment}-bank-input"
  }
}

resource "aws_s3_bucket_versioning" "input" {
  bucket = aws_s3_bucket.input.id

  versioning_configuration {
    status = "Enabled"
  }
}

resource "aws_s3_bucket_notification" "input" {
  bucket = aws_s3_bucket.input.id

  lambda_function {
    lambda_function_arn = var.lambda_function_arn
    events              = ["s3:ObjectCreated:*"]
    filter_prefix       = "input/"
    filter_suffix       = ".csv"
  }
}

resource "aws_s3_bucket" "rejected" {
  bucket = "${var.environment}-bank-rejected-${data.aws_caller_identity.current.account_id}"

  tags = {
    Name = "${var.environment}-bank-rejected"
  }
}

resource "aws_s3_bucket" "reports" {
  bucket = "${var.environment}-bank-reports-${data.aws_caller_identity.current.account_id}"

  tags = {
    Name = "${var.environment}-bank-reports"
  }
}

data "aws_caller_identity" "current" {}
