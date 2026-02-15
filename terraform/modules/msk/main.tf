# Note: This module assumes an existing MSK cluster or creates a minimal one for POC
# In production, you may want to use an existing cluster

resource "aws_security_group" "msk" {
  name        = "${var.environment}-bank-processing-msk-sg"
  description = "Security group for MSK cluster"
  vpc_id      = var.vpc_id

  ingress {
    from_port   = 9098
    to_port     = 9098
    protocol    = "tcp"
    cidr_blocks = ["10.0.0.0/16"]
    description = "SASL/IAM access"
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "${var.environment}-bank-processing-msk-sg"
  }
}

resource "aws_msk_cluster" "main" {
  cluster_name           = "${var.environment}-bank-processing-msk"
  kafka_version          = "3.6.0"
  number_of_broker_nodes = 3

  broker_node_group_info {
    instance_type   = "kafka.t3.small"
    client_subnets  = var.private_subnet_ids
    security_groups = [aws_security_group.msk.id]

    storage_info {
      ebs_storage_info {
        volume_size = 100
      }
    }
  }

  client_authentication {
    sasl {
      iam = true
    }
  }

  encryption_info {
    encryption_in_transit {
      client_broker = "TLS"
      in_cluster    = true
    }
  }

  tags = {
    Name = "${var.environment}-bank-processing-msk"
  }
}
