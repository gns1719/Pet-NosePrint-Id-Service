import boto3

sm_client = boto3.client('sagemaker')

model_name = 'custom-model-triplet-net'
ecr_image = '202533504773.dkr.ecr.ap-northeast-2.amazonaws.com/model-triplet-net:latest'  # 예: '123456789012.dkr.ecr.ap-northeast-2.amazonaws.com/my-image:latest'
role_arn = 'arn:aws:iam::202533504773:role/service-role/AmazonSageMaker-ExecutionRole-20250521T133181'  # SageMaker 실행 권한이 있는 IAM 역할 ARN

response = sm_client.create_model(
    ModelName=model_name,
    PrimaryContainer={
        'Image': ecr_image,
        'Environment': {
            'SAGEMAKER_PROGRAM': 'inference.py'
        }
    },
    ExecutionRoleArn=role_arn,
)
print('모델 생성 완료:', response)