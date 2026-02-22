locals {
  base_path = "/bank-processing/${var.environment}"
}

resource "aws_ssm_parameter" "process_enabled" {
  name        = "${local.base_path}/feature/process-enabled"
  description = "Enable or disable file processing"
  type        = "String"
  value       = var.process_enabled

  tags = {
    Name        = "${var.environment}-process-enabled"
    Environment = var.environment
    Component   = "parameter-store"
  }
}

resource "aws_ssm_parameter" "allowed_transaction_types" {
  name        = "${local.base_path}/validation/allowed-transaction-types"
  description = "Allowed transaction types for processing"
  type        = "String"
  value       = var.allowed_transaction_types

  tags = {
    Name        = "${var.environment}-allowed-transaction-types"
    Environment = var.environment
    Component   = "parameter-store"
  }
}

resource "aws_ssm_parameter" "allow_negative_amount" {
  name        = "${local.base_path}/validation/allow-negative-amount"
  description = "Allow negative transaction amounts"
  type        = "String"
  value       = var.allow_negative_amount

  tags = {
    Name        = "${var.environment}-allow-negative-amount"
    Environment = var.environment
    Component   = "parameter-store"
  }
}
