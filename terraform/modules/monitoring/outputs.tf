output "log_group_names" {
  description = "CloudWatch log group names"
  value = {
    lambda    = aws_cloudwatch_log_group.lambda.name
    processor = aws_cloudwatch_log_group.processor.name
    consumer  = aws_cloudwatch_log_group.consumer.name
  }
}

output "sns_topic_arn" {
  description = "SNS topic ARN for alarms"
  value       = aws_sns_topic.alarms.arn
}
