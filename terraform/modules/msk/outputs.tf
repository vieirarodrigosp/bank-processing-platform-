output "cluster_arn" {
  description = "MSK cluster ARN"
  value       = aws_msk_cluster.main.arn
}

output "bootstrap_servers" {
  description = "MSK bootstrap servers"
  value       = aws_msk_cluster.main.bootstrap_brokers_sasl_iam
}

output "security_group_id" {
  description = "MSK security group ID"
  value       = aws_security_group.msk.id
}
