resource "aws_dynamodb_table" "processed_files" {
  name         = "${var.environment}-bank-processing-processed-files"
  billing_mode = "PAY_PER_REQUEST"
  hash_key     = "fileHash"

  attribute {
    name = "fileHash"
    type = "S"
  }

  tags = {
    Name = "${var.environment}-bank-processing-processed-files"
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

  tags = {
    Name = "${var.environment}-bank-processing-processed-transactions"
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

  tags = {
    Name = "${var.environment}-bank-processing-transaction-summary"
  }
}
