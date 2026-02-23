# Bank Processing Platform -- Complete Deployment Guide

Author: Rodrigo Vieira Barbosa\
Program: Dry-Run Partner Evaluation

------------------------------------------------------------------------

# 1. Overview

This project implements a distributed banking transaction processing
system using:

-   Amazon S3
-   Amazon MSK (Kafka)
-   Amazon DynamoDB
-   Amazon EKS
-   AWS Systems Manager Parameter Store
-   Amazon CloudWatch
-   Terraform (Infrastructure as Code)
-   Kotlin + Spring Boot 3

The architecture processes CSV transaction files, validates and enriches
data, publishes events to MSK, consumes events asynchronously, persists
data into DynamoDB, and generates consolidated reports.

------------------------------------------------------------------------

# 2. Architecture Flow

S3 (CSV Upload)
↓
Processor Service (EKS)
↓
MSK Topic
↓
Consumer Service (EKS)
↓
DynamoDB
↓
S3 (Reports)

------------------------------------------------------------------------

# 3. Prerequisites

Install locally:

-   AWS CLI
-   Terraform \>= 1.5
-   Docker
-   kubectl
-   Maven 3.8+
-   Java 21
-   Access to AWS account

------------------------------------------------------------------------

# 4. Configure AWS CLI

``` bash
aws configure --profile bank-poc
```

Validate:

``` bash
aws sts get-caller-identity --profile bank-poc
```

------------------------------------------------------------------------

# 5. Provision Infrastructure (Terraform)

Navigate to the terraform folder:

``` bash
cd terraform
```

Initialize:

``` bash
terraform init
```

Plan:

``` bash
terraform plan
```

Apply:

``` bash
terraform apply -auto-approve
```

Resources provisioned:

-   VPC
-   Subnets
-   MSK Cluster
-   EKS Cluster
-   DynamoDB Tables
-   S3 Buckets
-   IAM Roles
-   CloudWatch Dashboard
-   CloudWatch Alarm

------------------------------------------------------------------------

# 6. Configure kubectl

``` bash
aws eks update-kubeconfig --region us-east-1 --name bank-processing-cluster
```

Test:

``` bash
kubectl get nodes
```

------------------------------------------------------------------------

# 7. Build Applications

From project root:

``` bash
mvn clean package
```

Build Docker images:

``` bash
docker build -t processor-service ./processor-service
docker build -t consumer-service ./consumer-service
```

------------------------------------------------------------------------

# 8. Push Images to ECR

Login:

``` bash
aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin <ACCOUNT_ID>.dkr.ecr.us-east-1.amazonaws.com
```

Tag:

``` bash
docker tag processor-service:latest <ECR_URL>/processor-service:latest
docker tag consumer-service:latest <ECR_URL>/consumer-service:latest
```

Push:

``` bash
docker push <ECR_URL>/processor-service:latest
docker push <ECR_URL>/consumer-service:latest
```

------------------------------------------------------------------------

# 9. Deploy Kubernetes Resources

Apply namespace:

``` bash
kubectl apply -f k8s/namespace.yaml
```

Apply ConfigMaps:

``` bash
kubectl apply -f k8s/processor-configmap.yaml
kubectl apply -f k8s/consumer-configmap.yaml
```

Apply ServiceAccounts:

``` bash
kubectl apply -f k8s/service-account.yaml
```

Deploy applications:

``` bash
kubectl apply -f k8s/processor-deployment.yaml
kubectl apply -f k8s/consumer-deployment.yaml
```

Check pods:

``` bash
kubectl get pods -n bank-processing
```

------------------------------------------------------------------------

# 10. Upload CSV File

Upload test file:

``` bash
aws s3 cp sample.csv s3://<INPUT_BUCKET>/
```

------------------------------------------------------------------------

# 11. Validate Processing

Check processor logs:

``` bash
kubectl logs <processor-pod> -n bank-processing
```

Check consumer logs:

``` bash
kubectl logs <consumer-pod> -n bank-processing
```

Verify DynamoDB:

``` bash
aws dynamodb scan --table-name processed-transactions
```

------------------------------------------------------------------------

# 12. Metrics & Monitoring

Access actuator locally (if port-forwarding):

``` bash
kubectl port-forward deployment/consumer-service 8080:8080
```

Open:

    http://localhost:8080/actuator/prometheus

CloudWatch Dashboard automatically provisioned via Terraform.

------------------------------------------------------------------------

# 13. Circuit Breaker Test

Force errors (invalid Dynamo table name) and observe:

-   Error rate increases
-   Circuit opens when \> 15%
-   Logs show: CIRCUIT_OPEN

------------------------------------------------------------------------

# 14. Cleanup

Destroy infrastructure:

``` bash
terraform destroy -auto-approve
```

------------------------------------------------------------------------

# 15. Time Invested

Total estimated hours: \_\_\_\_\_\_\_\_

------------------------------------------------------------------------

# End of Document