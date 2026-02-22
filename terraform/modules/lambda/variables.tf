variable "environment" {
  description = "Environment name"
  type        = string
}

variable "msk_bootstrap_servers" {
  description = "MSK bootstrap servers"
  type        = string
}

variable "msk_cluster_arn" {
  description = "MSK cluster arn"
  type        = string
}

variable "aws_region" {
  description = "aws region"
  type        = string
}

variable "s3_input_bucket_name" {
  type = string
}

variable "s3_input_bucket_arn" {
  description = "S3 input bucket ARN"
  type        = string
}

variable "aws_account_id" {
  description = "AWS account id"
  type = string
}

variable "private_subnet_ids" {
  description = "Private subnet IDs for Lambda VPC config"
  type        = list(string)
}

variable "lambda_security_group_id" {
  description = "Security group ID for Lambda"
  type        = string
}