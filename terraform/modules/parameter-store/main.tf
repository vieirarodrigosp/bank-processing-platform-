resource "aws_ssm_parameter" "process_enabled" {
  name  = "/bank-processing/feature/process-enabled"
  type  = "String"
  value = "true"

  tags = {
    Name        = "${var.environment}-process-enabled"
    Environment = var.environment
  }
}

resource "aws_ssm_parameter" "allowed_transaction_types" {
  name  = "/bank-processing/validation/allowed-transaction-types"
  type  = "String"
  value = "PIX,TED,DOC"

  tags = {
    Name        = "${var.environment}-allowed-transaction-types"
    Environment = var.environment
  }
}

resource "aws_ssm_parameter" "allow_negative_amount" {
  name  = "/bank-processing/validation/allow-negative-amount"
  type  = "String"
  value = "false"

  tags = {
    Name        = "${var.environment}-allow-negative-amount"
    Environment = var.environment
  }
}
