resource "aws_cloudwatch_log_group" "lambda" {
  name              = "/aws/lambda/${var.lambda_function_name}"
  retention_in_days = 7

  tags = {
    Name        = "${var.environment}-lambda-logs"
    Environment = var.environment
  }
}

resource "aws_cloudwatch_log_group" "processor" {
  name              = "/aws/eks/${var.environment}/processor-service"
  retention_in_days = 7

  tags = {
    Name        = "${var.environment}-processor-logs"
    Environment = var.environment
  }
}

resource "aws_cloudwatch_log_group" "consumer" {
  name              = "/aws/eks/${var.environment}/consumer-service"
  retention_in_days = 7

  tags = {
    Name        = "${var.environment}-consumer-logs"
    Environment = var.environment
  }
}

resource "aws_cloudwatch_metric_alarm" "error_rate" {
  alarm_name          = "${var.environment}-bank-processing-error-rate"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = 1
  metric_name         = "error_rate"
  namespace           = "BankProcessing"
  period              = 300
  statistic           = "Average"
  threshold           = 15
  alarm_description   = "Alert when error rate exceeds 15%"
  treat_missing_data  = "notBreaching"

  dimensions = {
    Environment = var.environment
  }

  tags = {
    Name        = "${var.environment}-error-rate-alarm"
    Environment = var.environment
  }
}

resource "aws_sns_topic" "alarms" {
  name = "${var.environment}-bank-processing-alarms"

  tags = {
    Name        = "${var.environment}-alarms"
    Environment = var.environment
  }
}

resource "aws_sns_topic_subscription" "alarms_email" {
  topic_arn = aws_sns_topic.alarms.arn
  protocol  = "email"
  endpoint  = var.alarm_email
}
