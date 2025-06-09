import sagemaker
from sagemaker.pytorch import PyTorchModel

role = ""  # 실제 역할 ARN으로 바꿔주세요
model_data = ""

# SageMaker 세션 및 기본 설정
sess = sagemaker.Session()
vpc_config = {
    "Subnets": [],        # 엔드포인트를 배치할 서브넷 ID
    "SecurityGroupIds": []   # 해당 서브넷에서 접근 가능한 보안 그룹 ID
}

model = PyTorchModel(
    entry_point="inference.py",  # model.tar.gz 내 추론 스크립트 경로
    role=role,
    framework_version="1.13",
    py_version="py39",
    model_data=model_data,
    vpc_config=vpc_config,        # ✅ VPC 설정 추가!
    enable_network_isolation=False
)

predictor = model.deploy(
    initial_instance_count=1,
    instance_type="ml.t2.medium",  # 예시 인스턴스 타입 (ml.t2.medium도 가능)
    endpoint_name="triplet-lbp-vpc"
)
