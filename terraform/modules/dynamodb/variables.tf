variable "environment" {
  description = "Environment name"
  type        = string
}

variable "enable_deletion_protection" {
  description = "Enable deletion protection for DynamoDB tables"
  type        = bool
  default     = false
}