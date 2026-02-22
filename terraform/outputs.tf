output "vpc_id" {
  description = "VPC ID"
  value       = module.networking.vpc_id
}

output "msk_bootstrap_servers" {
  description = "MSK bootstrap servers"
  value       = var.msk_bootstrap_servers
}

output "s3_input_bucket" {
  description = "S3 input bucket name"
  value       = module.s3.input_bucket_name
}

output "s3_rejected_bucket" {
  description = "S3 rejected bucket name"
  value       = module.s3.rejected_bucket_name
}

output "s3_reports_bucket" {
  description = "S3 reports bucket name"
  value       = module.s3.reports_bucket_name
}

output "lambda_function_name" {
  description = "Lambda function name"
  value       = module.lambda.function_name
}

output "eks_cluster_name" {
  description = "EKS cluster name"
  value       = module.eks.cluster_name
}

output "dynamodb_tables" {
  description = "DynamoDB table names"
  value       = module.dynamodb.table_names
}
