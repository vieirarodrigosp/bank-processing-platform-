terraform {
  required_version = ">= 1.5.0"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

provider "aws" {
  region = var.aws_region

  default_tags {
    tags = {
      Project     = "bank-processing-platform"
      Environment = var.environment
      ManagedBy   = "terraform"
    }
  }
}

# Networking Module
module "networking" {
  source = "./modules/networking"

  environment = var.environment
  vpc_cidr    = var.vpc_cidr
}

# S3 Module
module "s3" {
  source = "./modules/s3"

  environment           = var.environment
  lambda_function_arn   = module.lambda.function_arn
}

# MSK Module
module "msk" {
  source = "./modules/msk"

  environment       = var.environment
  vpc_id            = module.networking.vpc_id
  private_subnet_ids = module.networking.private_subnet_ids
}

# DynamoDB Module
module "dynamodb" {
  source = "./modules/dynamodb"

  environment = var.environment
}

# Parameter Store Module
module "parameter_store" {
  source = "./modules/parameter-store"

  environment = var.environment
}

# Lambda Module
module "lambda" {
  source = "./modules/lambda"

  environment            = var.environment
  msk_bootstrap_servers  = module.msk.bootstrap_servers
  s3_input_bucket_arn    = module.s3.input_bucket_arn
}

# EKS Module
module "eks" {
  source = "./modules/eks"

  environment         = var.environment
  vpc_id              = module.networking.vpc_id
  private_subnet_ids  = module.networking.private_subnet_ids
  msk_cluster_arn     = module.msk.cluster_arn
  dynamodb_table_arns = module.dynamodb.table_arns
  s3_bucket_arns      = module.s3.bucket_arns
}

# Monitoring Module
module "monitoring" {
  source = "./modules/monitoring"

  environment         = var.environment
  lambda_function_name = module.lambda.function_name
}
