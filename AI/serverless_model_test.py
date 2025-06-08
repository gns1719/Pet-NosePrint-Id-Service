import base64
import json
import boto3

client = boto3.client("sagemaker-runtime")

with open("test_dognose.jpg", "rb") as f:
    encoded = base64.b64encode(f.read()).decode("utf-8")

payload = json.dumps({"image_base64": encoded})

response = client.invoke_endpoint(
    EndpointName="triplet-lbp-serverless",
    ContentType="application/json",
    Accept="application/json",
    Body=payload
)

result = json.loads(response['Body'].read().decode())
print(result)   