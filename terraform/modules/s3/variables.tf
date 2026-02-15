variable "environment" {
  description = "Environment name"
  type        = string
}

variable "lambda_function_arn" {
  description = "Lambda function ARN for S3 event notification"
  type        = string
}
