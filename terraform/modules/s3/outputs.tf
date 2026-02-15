output "input_bucket_name" {
  description = "Input bucket name"
  value       = aws_s3_bucket.input.id
}

output "input_bucket_arn" {
  description = "Input bucket ARN"
  value       = aws_s3_bucket.input.arn
}

output "rejected_bucket_name" {
  description = "Rejected bucket name"
  value       = aws_s3_bucket.rejected.id
}

output "rejected_bucket_arn" {
  description = "Rejected bucket ARN"
  value       = aws_s3_bucket.rejected.arn
}

output "reports_bucket_name" {
  description = "Reports bucket name"
  value       = aws_s3_bucket.reports.id
}

output "reports_bucket_arn" {
  description = "Reports bucket ARN"
  value       = aws_s3_bucket.reports.arn
}

output "bucket_arns" {
  description = "All bucket ARNs"
  value = [
    aws_s3_bucket.input.arn,
    aws_s3_bucket.rejected.arn,
    aws_s3_bucket.reports.arn
  ]
}
