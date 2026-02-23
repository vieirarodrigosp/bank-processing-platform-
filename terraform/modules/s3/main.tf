data "aws_caller_identity" "current" {}

locals {
  input_bucket_name    = "${var.environment}-bank-input-${data.aws_caller_identity.current.account_id}"
  rejected_bucket_name = "${var.environment}-bank-rejected-${data.aws_caller_identity.current.account_id}"
  reports_bucket_name  = "${var.environment}-bank-reports-${data.aws_caller_identity.current.account_id}"
}

resource "aws_s3_bucket" "input" {
  bucket        = local.input_bucket_name
  force_destroy = true

  tags = {
    Name        = "${var.environment}-bank-input"
    Environment = var.environment
  }
}

resource "aws_s3_bucket_versioning" "input" {
  bucket = aws_s3_bucket.input.id

  versioning_configuration {
    status = "Enabled"
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

resource "aws_s3_bucket_policy" "input_ssl" {
  bucket = aws_s3_bucket.input.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Sid       = "ForceSSLOnly"
        Effect    = "Deny"
        Principal = "*"
        Action    = "s3:*"
        Resource = [
          aws_s3_bucket.input.arn,
          "${aws_s3_bucket.input.arn}/*"
        ]
        Condition = {
          Bool = {
            "aws:SecureTransport" = "false"
          }
        }
      }
    ]
  })
}

resource "aws_s3_bucket" "rejected" {
  bucket        = local.rejected_bucket_name
  force_destroy = true

  tags = {
    Name        = "${var.environment}-bank-rejected"
    Environment = var.environment
  }
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

resource "aws_s3_bucket_policy" "rejected_ssl" {
  bucket = aws_s3_bucket.rejected.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Sid       = "ForceSSLOnly"
      Effect    = "Deny"
      Principal = "*"
      Action    = "s3:*"

      Resource = [
        aws_s3_bucket.rejected.arn,
        "${aws_s3_bucket.rejected.arn}/*"
      ]

      Condition = {
        Bool = {
          "aws:SecureTransport" = "false"
        }
      }
    }]
  })
}

resource "aws_s3_bucket_lifecycle_configuration" "rejected" {
  bucket = aws_s3_bucket.rejected.id

  rule {
    id     = "expire-after-5-days"
    status = "Enabled"

    filter {
      prefix = "rejected/"
    }

    expiration {
      days = 5
    }
  }
}

resource "aws_s3_bucket" "reports" {
  bucket        = local.reports_bucket_name
  force_destroy = true

  tags = {
    Name        = "${var.environment}-bank-reports"
    Environment = var.environment
  }
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

resource "aws_s3_bucket_policy" "reports_ssl" {
  bucket = aws_s3_bucket.reports.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Sid       = "ForceSSLOnly"
      Effect    = "Deny"
      Principal = "*"
      Action    = "s3:*"

      Resource = [
        aws_s3_bucket.reports.arn,
        "${aws_s3_bucket.reports.arn}/*"
      ]

      Condition = {
        Bool = {
          "aws:SecureTransport" = "false"
        }
      }
    }]
  })
}

resource "aws_s3_bucket_lifecycle_configuration" "reports" {
  bucket = aws_s3_bucket.reports.id

  rule {
    id     = "expire-after-5-days"
    status = "Enabled"

    filter {
      prefix = "rejected/"
    }

    expiration {
      days = 5
    }
  }
}
