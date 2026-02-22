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

resource "aws_cloudwatch_log_group" "lambda" {
  name              = "/aws/lambda/${var.lambda_function_name}"
  retention_in_days = var.log_retention_days

  tags = {
    Name        = "${var.environment}-lambda-logs"
    Environment = var.environment
  }
}

resource "aws_cloudwatch_log_group" "processor" {
  name              = "/aws/eks/${var.environment}/processor-service"
  retention_in_days = var.log_retention_days

  tags = {
    Name        = "${var.environment}-processor-logs"
    Environment = var.environment
  }
}

resource "aws_cloudwatch_log_group" "consumer" {
  name              = "/aws/eks/${var.environment}/consumer-service"
  retention_in_days = var.log_retention_days

  tags = {
    Name        = "${var.environment}-consumer-logs"
    Environment = var.environment
  }
}

resource "aws_cloudwatch_metric_alarm" "error_rate_15_percent" {
  alarm_name          = "${var.environment}-error-rate-gt-15"
  comparison_operator = "GreaterThanThreshold"
  threshold           = 15
  evaluation_periods  = 2
  datapoints_to_alarm = 2
  treat_missing_data  = "notBreaching"

  insufficient_data_actions = []

  metric_query {
    id          = "processed"
    return_data = false

    metric {
      namespace   = "BankProcessing"
      metric_name = "TransactionsProcessed"
      period      = 300
      stat        = "Sum"

      dimensions = {
        Environment = var.environment
      }
    }
  }

  metric_query {
    id          = "failed"
    return_data = false

    metric {
      namespace   = "BankProcessing"
      metric_name = "TransactionsFailed"
      period      = 300
      stat        = "Sum"

      dimensions = {
        Environment = var.environment
      }
    }
  }

  metric_query {
    id          = "error_rate"
    expression  = "IF(processed > 0, (failed / processed) * 100, 0)"
    label       = "ErrorRate"
    return_data = true
  }

  alarm_description = "Triggers when processing error rate exceeds 15%"

  alarm_actions = [aws_sns_topic.alarms.arn]
  ok_actions    = [aws_sns_topic.alarms.arn]

  tags = {
    Name        = "${var.environment}-error-rate-alarm"
    Environment = var.environment
  }
}

resource "aws_cloudwatch_metric_alarm" "lambda_errors" {
  alarm_name          = "${var.environment}-lambda-errors"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = 2
  datapoints_to_alarm = 2
  threshold           = 1

  period              = 300
  statistic           = "Sum"
  namespace           = "AWS/Lambda"
  metric_name         = "Errors"

  insufficient_data_actions = []

  dimensions = {
    FunctionName = var.lambda_function_name
  }

  alarm_description  = "Lambda errors detected"
  treat_missing_data = "notBreaching"

  alarm_actions = [aws_sns_topic.alarms.arn]
  ok_actions    = [aws_sns_topic.alarms.arn]

  tags = {
    Name        = "${var.environment}-lambda-error-alarm"
    Environment = var.environment
  }
}

resource "aws_cloudwatch_metric_alarm" "lambda_throttles" {
  alarm_name          = "${var.environment}-lambda-throttles"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = 2
  datapoints_to_alarm = 2
  threshold           = 1
  period              = 300
  statistic           = "Sum"
  namespace           = "AWS/Lambda"
  metric_name         = "Throttles"

  insufficient_data_actions = []

  dimensions = {
    FunctionName = var.lambda_function_name
  }

  alarm_description  = "Lambda throttling detected for 10 minutes"
  treat_missing_data = "notBreaching"

  alarm_actions = [aws_sns_topic.alarms.arn]
  ok_actions    = [aws_sns_topic.alarms.arn]

  tags = {
    Name        = "${var.environment}-lambda-throttle-alarm"
    Environment = var.environment
  }
}