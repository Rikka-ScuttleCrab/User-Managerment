import random
import time
import os

# Danh sách message lỗi
messages = [
    "ERROR... FIX THE CODE LN123",
    "SYSTEM FAILURE...",
    "shutdown system...",
    "CRITICAL ERROR...",
    "NULL POINTER EXCEPTION...",
    "ACCESS DENIED...",
    "MEMORY OVERFLOW...",
]

# Màu đỏ cho terminal Windows
os.system("color 0C")

while True:
    # Random dữ liệu
    line = random.randint(1, 500)
    code = random.randint(1000, 9999)
    msg = random.choice(messages)

    # In ra terminal
    print(
        f"{msg} | LN:{line} | CODE:{code}"
    )

    # Tốc độ chạy
    time.sleep(0.05)