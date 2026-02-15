output "parameter_names" {
  description = "Parameter Store parameter names"
  value = {
    process_enabled          = aws_ssm_parameter.process_enabled.name
    allowed_transaction_types = aws_ssm_parameter.allowed_transaction_types.name
    allow_negative_amount    = aws_ssm_parameter.allow_negative_amount.name
  }
}
