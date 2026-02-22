variable "environment" {
  description = "Environment name"
  type        = string
}

variable "msk_bootstrap_servers" {
  description = "MSK bootstrap servers"
  type        = string
}

variable "s3_input_bucket_name" {
  type = string
}

variable "s3_input_bucket_arn" {
  description = "S3 input bucket ARN"
  type        = string
}
