import boto3

# SageMaker Runtime 클라이언트 생성
runtime = boto3.client('sagemaker-runtime', region_name='ap-northeast-2')

# 로컬 이미지 파일 읽기
with open("test_image.jpg", "rb") as f:
    image_bytes = f.read()

# 엔드포인트 호출
    response = runtime.invoke_endpoint(
    EndpointName="pytorch-inference-2025-06-04-06-52-48-549",  # 실제 엔드포인트 이름
    ContentType="image/jpeg",                    # inference.py와 일치
    Accept="application/json",                   # output_fn과 일치
    Body=image_bytes
)

# 결과 출력
embedding = response['Body'].read().decode("utf-8")
print(embedding)  # {"embedding": [0.123, 0.456, ...]}fh