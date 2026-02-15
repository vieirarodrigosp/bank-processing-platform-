output "cluster_name" {
  description = "EKS cluster name"
  value       = aws_eks_cluster.main.name
}

output "cluster_endpoint" {
  description = "EKS cluster endpoint"
  value       = aws_eks_cluster.main.endpoint
}

output "cluster_security_group_id" {
  description = "EKS cluster security group ID"
  value       = aws_eks_cluster.main.vpc_config[0].cluster_security_group_id
}

output "processor_service_role_arn" {
  description = "Processor service IRSA role ARN"
  value       = aws_iam_role.processor_service.arn
}

output "consumer_service_role_arn" {
  description = "Consumer service IRSA role ARN"
  value       = aws_iam_role.consumer_service.arn
}
