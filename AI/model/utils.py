from PIL import Image
import numpy as np
from scipy.ndimage import generic_filter
from torchvision import transforms

def lbp_basic(gray_image):
    def lbp_func(values):
        center = values[4]
        binary = (values >= center).astype(np.uint8)
        # exclude center pixel and compute LBP as clockwise binary pattern
        lbp_value = (
            (binary[0] << 7) | (binary[1] << 6) |
            (binary[2] << 5) | (binary[5] << 4) |
            (binary[8] << 3) | (binary[7] << 2) |
            (binary[6] << 1) | (binary[3] << 0)
        )
        return lbp_value

    padded_image = np.pad(gray_image, pad_width=1, mode='edge')
    lbp_image = generic_filter(padded_image, lbp_func, size=3)
    return lbp_image.astype(np.uint8)

def preprocess_lbp(image_path):
    img = Image.open(image_path).convert('L')  # grayscale
    img_np = np.array(img)

    lbp = lbp_basic(img_np)
    lbp = (lbp / lbp.max() * 255).astype(np.uint8)  # normalize for visualization

    lbp_img = Image.fromarray(lbp).convert('RGB')

    transform = transforms.Compose([
        transforms.Resize((224, 224)),
        transforms.ToTensor(),
        transforms.Normalize([0.5]*3, [0.5]*3)
    ])
    return transform(lbp_img).unsqueeze(0)
