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

  deletion_protection_enabled = var.enable_deletion_protection

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

  attribute {
    name = "customerId"
    type = "S"
  }

  attribute {
    name = "transactionType"
    type = "S"
  }

  # GSI PARA CONSULTA POR CLIENTE
  global_secondary_index {
    name            = "customerId-index"
    hash_key        = "customerId"
    projection_type = "ALL"
  }

  # GSI PARA CONSULTA POR TIPO
  global_secondary_index {
    name            = "transactionType-index"
    hash_key        = "transactionType"
    projection_type = "ALL"
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

  deletion_protection_enabled = var.enable_deletion_protection

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

  ttl {
    attribute_name = "ttl"
    enabled        = true
  }

  deletion_protection_enabled = var.enable_deletion_protection

  tags = {
    Name = "${var.environment}-bank-processing-transaction-summary"
    Environment = var.environment
    Component   = "dynamodb"
  }
}
