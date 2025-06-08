import sagemaker
from sagemaker.pytorch import PyTorchModel
from sagemaker.serverless import ServerlessInferenceConfig

role = "my-sageMaker-role"
model_data = "my-model-path"

model = PyTorchModel(
    entry_point="inference.py",             # model.tar.gz 내부 경로 기준
    role=role,
    framework_version="1.13",               
    py_version="py39",
    model_data=model_data
)

serverless_config = ServerlessInferenceConfig(
    memory_size_in_mb=1024,
    max_concurrency=2
)

predictor = model.deploy(
    serverless_inference_config=serverless_config,
    endpoint_name="triplet-lbp-serverless"
)
