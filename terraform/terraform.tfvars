aws_region  = "us-east-1"
aws_account_id = "160885283918"

environment = "poc"

vpc_cidr = "10.0.0.0/16"

msk_bootstrap_servers = "b-3.demomskbrq.21i2ab.c3.kafka.us-east-1.amazonaws.com:9098,b-2.demomskbrq.21i2ab.c3.kafka.us-east-1.amazonaws.com:9098,b-1.demomskbrq.21i2ab.c3.kafka.us-east-1.amazonaws.com:9098"
msk_cluster_arn = "arn:aws:kafka:us-east-1:160885283918:cluster/demo-msk-brq/5f769eef-b193-4df2-900a-54740bddf169-3"