resource "aws_dynamodb_table" "processed_files" {
  name         = "${var.environment}-bank-processing-processed-files"
  billing_mode = "PAY_PER_REQUEST"
  hash_key     = "fileHash"

  attribute {
    name = "fileHash"
    type = "S"
  }

  server_side_encryption {
    enabled = true
  }

  point_in_time_recovery {
    enabled = true
  }

  ttl {
    attribute_name = "ttl"
    enabled        = true
  }

  tags = {
    Name = "${var.environment}-bank-processing-processed-files"
    Environment = var.environment
    Component   = "dynamodb"
  }
}

resource "aws_dynamodb_table" "processed_transactions" {
  name         = "${var.environment}-bank-processing-processed-transactions"
  billing_mode = "PAY_PER_REQUEST"
  hash_key     = "transactionId"

  attribute {
    name = "transactionId"
    type = "S"
  }

  server_side_encryption {
    enabled = true
  }

  point_in_time_recovery {
    enabled = true
  }

  ttl {
    attribute_name = "ttl"
    enabled        = true
  }

  tags = {
    Name = "${var.environment}-bank-processing-processed-transactions"
    Environment = var.environment
    Component   = "dynamodb"
  }
}

resource "aws_dynamodb_table" "transaction_summary" {
  name         = "${var.environment}-bank-processing-transaction-summary"
  billing_mode = "PAY_PER_REQUEST"
  hash_key     = "transactionType"

  attribute {
    name = "transactionType"
    type = "S"
  }

  stream_enabled   = true
  stream_view_type = "NEW_AND_OLD_IMAGES"

  server_side_encryption {
    enabled = true
  }

  point_in_time_recovery {
    enabled = true
  }

  tags = {
    Name = "${var.environment}-bank-processing-transaction-summary"
    Environment = var.environment
    Component   = "dynamodb"
  }
}
