resource "aws_s3_bucket" "input" {
  bucket = "${var.environment}-bank-input-${data.aws_caller_identity.current.account_id}"

  tags = {
    Name = "${var.environment}-bank-input"
  }

  force_destroy = true
}

resource "aws_lambda_permission" "allow_s3" {
  statement_id  = "AllowExecutionFromS3"
  action        = "lambda:InvokeFunction"
  function_name = var.lambda_function_arn
  principal     = "s3.amazonaws.com"
  source_arn    = aws_s3_bucket.input.arn
}

resource "aws_s3_bucket_versioning" "input" {
  bucket = aws_s3_bucket.input.id

  versioning_configuration {
    status = "Enabled"
  }
}

resource "aws_s3_bucket_notification" "input" {
  bucket = aws_s3_bucket.input.id

  depends_on = [
    aws_lambda_permission.allow_s3,
    aws_s3_bucket_versioning.input
  ]

  lambda_function {
    lambda_function_arn = var.lambda_function_arn
    events              = ["s3:ObjectCreated:*"]
    filter_prefix       = "input/"
    filter_suffix       = ".csv"
  }
}

resource "aws_s3_bucket_public_access_block" "input" {
  bucket = aws_s3_bucket.input.id

  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true
}

resource "aws_s3_bucket_server_side_encryption_configuration" "input" {
  bucket = aws_s3_bucket.input.id

  rule {
    apply_server_side_encryption_by_default {
      sse_algorithm = "AES256"
    }
  }
}

resource "aws_s3_bucket" "rejected" {
  bucket = "${var.environment}-bank-rejected-${data.aws_caller_identity.current.account_id}"

  tags = {
    Name = "${var.environment}-bank-rejected"
  }

  force_destroy = true
}

resource "aws_s3_bucket_public_access_block" "rejected" {
  bucket = aws_s3_bucket.rejected.id

  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true
}

resource "aws_s3_bucket_server_side_encryption_configuration" "rejected" {
  bucket = aws_s3_bucket.rejected.id

  rule {
    apply_server_side_encryption_by_default {
      sse_algorithm = "AES256"
    }
  }
}

resource "aws_s3_bucket" "reports" {
  bucket = "${var.environment}-bank-reports-${data.aws_caller_identity.current.account_id}"

  tags = {
    Name = "${var.environment}-bank-reports"
  }

  force_destroy = true
}

resource "aws_s3_bucket_public_access_block" "reports" {
  bucket = aws_s3_bucket.reports.id

  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true
}

resource "aws_s3_bucket_server_side_encryption_configuration" "reports" {
  bucket = aws_s3_bucket.reports.id

  rule {
    apply_server_side_encryption_by_default {
      sse_algorithm = "AES256"
    }
  }
}

data "aws_caller_identity" "current" {}
