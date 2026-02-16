# Bank Processing Platform POC

A cloud-native bank transaction processing platform built on AWS using Kotlin, Spring Boot, and Terraform.

## Architecture

The platform consists of three main components:
- **Lambda Dispatcher**: Receives S3 events and publishes to Kafka
- **Processor Service**: Validates transactions and generates rejection reports
- **Consumer Service**: Persists transactions to DynamoDB and generates consolidated reports

## Prerequisites

- AWS CLI configured with credentials
- Terraform >= 1.5.0
- Java 21
- Gradle 8.x
- Docker
- kubectl
- AWS Account: 160885283918
- AWS Region: us-east-1

## Project Structure

```
.
├── terraform/              # Infrastructure as Code
│   ├── modules/           # Terraform modules
│   │   ├── networking/    # VPC, subnets, NAT gateways
│   │   ├── s3/           # S3 buckets
│   │   ├── msk/          # Kafka cluster
│   │   ├── lambda/       # Lambda Dispatcher
│   │   ├── eks/          # EKS cluster and IRSA
│   │   ├── dynamodb/     # DynamoDB tables
│   │   ├── parameter-store/ # Configuration parameters
│   │   └── monitoring/   # CloudWatch logs and alarms
│   └── main.tf           # Root module
├── lambda-dispatcher/     # Lambda function (Kotlin)
├── processor-service/     # Processor service (Kotlin + Spring Boot)
├── consumer-service/      # Consumer service (Kotlin + Spring Boot)
└── k8s/                  # Kubernetes manifests
```

## Getting Started

### 1. Build Applications

```bash
./gradlew build
```

### 2. Deploy Infrastructure

```bash
cd terraform
terraform init
terraform plan
terraform apply
```

### 3. Deploy Services to EKS

```bash
# Update kubeconfig
aws eks update-kubeconfig --name dev-bank-processing-eks --region us-east-1

# Apply Kubernetes manifests
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/processor-service/
kubectl apply -f k8s/consumer-service/
```

## Configuration

Configuration parameters are stored in AWS Systems Manager Parameter Store:
- `/bank-processing/feature/process-enabled` - Enable/disable processing
- `/bank-processing/validation/allowed-transaction-types` - Valid transaction types
- `/bank-processing/validation/allow-negative-amount` - Allow negative amounts

## Testing

Run tests:
```bash
./gradlew test
```

## Monitoring

- CloudWatch Logs: `/aws/lambda/`, `/aws/eks/`
- CloudWatch Metrics: `BankProcessing` namespace
- Alarms: Error rate > 15%

## License

Proprietary
