variable "environment" {
  description = "Environment name (poc, dev, staging, prod)"
  type        = string
}

variable "lambda_function_name" {
  description = "Lambda function name used for monitoring"
  type        = string
}

variable "alarm_email" {
  description = "Email address for alarm notifications"
  type        = string
  default     = "rodrigovbarbosa@brq.com"

  validation {
    condition     = can(regex("^.+@.+\\..+$", var.alarm_email))
    error_message = "alarm_email must be a valid email address."
  }
}

variable "log_retention_days" {
  description = "CloudWatch log retention in days"
  type        = number
  default     = 5

  validation {
    condition     = var.log_retention_days > 0
    error_message = "log_retention_days must be greater than zero."
  }
}