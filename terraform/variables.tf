variable "aws_region" {
  description = "AWS region for resources"
  type        = string
}

variable "aws_account_id" {
  description = "AWS account ID"
  type        = string
}

variable "environment" {
  description = "Environment name (poc, dev, staging, prod)"
  type        = string
}

variable "vpc_cidr" {
  description = "CIDR block for VPC"
  type        = string
}

variable "msk_bootstrap_servers" {
  description = "Existing MSK bootstrap servers"
  type        = string
}

variable "msk_cluster_arn" {
  description = "Existing MSK cluster ARN"
  type        = string
}