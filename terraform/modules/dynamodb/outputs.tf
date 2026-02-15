output "processed_files_table_name" {
  description = "Processed files table name"
  value       = aws_dynamodb_table.processed_files.name
}

output "processed_transactions_table_name" {
  description = "Processed transactions table name"
  value       = aws_dynamodb_table.processed_transactions.name
}

output "transaction_summary_table_name" {
  description = "Transaction summary table name"
  value       = aws_dynamodb_table.transaction_summary.name
}

output "table_names" {
  description = "All DynamoDB table names"
  value = {
    processed_files       = aws_dynamodb_table.processed_files.name
    processed_transactions = aws_dynamodb_table.processed_transactions.name
    transaction_summary   = aws_dynamodb_table.transaction_summary.name
  }
}

output "table_arns" {
  description = "All DynamoDB table ARNs"
  value = [
    aws_dynamodb_table.processed_files.arn,
    aws_dynamodb_table.processed_transactions.arn,
    aws_dynamodb_table.transaction_summary.arn
  ]
}
