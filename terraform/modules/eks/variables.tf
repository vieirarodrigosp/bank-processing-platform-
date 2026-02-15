variable "environment" {
  description = "Environment name"
  type        = string
}

variable "vpc_id" {
  description = "VPC ID"
  type        = string
}

variable "private_subnet_ids" {
  description = "Private subnet IDs"
  type        = list(string)
}

variable "msk_cluster_arn" {
  description = "MSK cluster ARN"
  type        = string
}

variable "dynamodb_table_arns" {
  description = "DynamoDB table ARNs"
  type        = list(string)
}

variable "s3_bucket_arns" {
  description = "S3 bucket ARNs"
  type        = list(string)
}
