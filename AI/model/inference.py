print("=== STARTED ===")

import os
import io
import torch
import json
import base64
from PIL import Image

from model import TripletNetwork
from utils import preprocess_lbp  # LBP 전처리 함수 포함되어 있어야 함

device = torch.device('cuda' if torch.cuda.is_available() else 'cpu')

# 1. 모델 로딩
def model_fn(model_dir):
    model = TripletNetwork().to(device)
    model_path = os.path.join(model_dir, 'triplet_lbp_model.pth')
    model.load_state_dict(torch.load(model_path, map_location=device))
    model.eval()
    return model

# 2. 입력 디코딩 (Content-Type: image/jpeg or application/json)
def input_fn(request_body, content_type):
    if content_type == 'application/json':
        body = json.loads(request_body)
        image_data = base64.b64decode(body['image_base64'])
        image = Image.open(io.BytesIO(image_data)).convert('RGB')
    elif content_type == 'image/jpeg':
        image = Image.open(io.BytesIO(request_body)).convert('RGB')
    else:
        raise ValueError(f"Unsupported content type: {content_type}")

    return image
3
# 3. 추론 실행
def predict_fn(input_data, model):
    # LBP 전처리 적용
    input_tensor = preprocess_lbp(input_data).to(device)
    with torch.no_grad():
        embedding = model(input_tensor).cpu().numpy().flatten()
    return embedding.tolist()  # JSON으로 변환 가능하게 list로 리턴

# 4. 출력 포맷 지정
def output_fn(prediction, accept):
    if accept == 'application/json':
        return json.dumps({'embedding': prediction}), 'application/json'
    else:
        raise ValueError(f"Unsupported accept type: {accept}")
