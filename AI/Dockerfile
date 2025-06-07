FROM pytorch/pytorch:1.13.1-cuda11.6-cudnn8-runtime

WORKDIR /opt/program

# 필수 툴킷 설치
COPY serve/requirements.txt .
RUN pip install --upgrade pip && \
    pip install -r requirements.txt && \
    pip install sagemaker-inference

# 소스 복사
COPY model /opt/program/model
COPY model/inference.py .

# 엔트리포인트 지정
ENV SAGEMAKER_PROGRAM=inference.py

# SageMaker inference toolkit이 serve 엔트리포인트를 자동으로 만듦
ENV SAGEMAKER_SUBMIT_DIRECTORY=/opt/program
ENV PYTHONPATH=/opt/program

CMD ["serve"]