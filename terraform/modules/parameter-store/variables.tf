variable "environment" {
  description = "Environment name"
  type        = string
}

variable "process_enabled" {
  type        = string
  default     = "true"
}

variable "allowed_transaction_types" {
  type        = string
  default     = "PIX,TED,DOC"
}

variable "allow_negative_amount" {
  type        = string
  default     = "false"
}