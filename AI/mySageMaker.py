from sagemaker.pytorch import PyTorchModel
import sagemaker

sagemaker_session = sagemaker.Session()
role = "arn:aws:iam::202533504773:role/service-role/AmazonSageMaker-ExecutionRole-20250521T133181"  # 실제 role ARN으로 교체

model = PyTorchModel(
    entry_point="inference.py",
    model_data="s3://triplet-net-lbp-model-buket/model.tar.gz",
    role=role,
    framework_version="1.13.1",  # PyTorch 버전
    py_version="py39"
)

predictor = model.deploy(
    initial_instance_count=1,
    instance_type="ml.t2.medium"
)
