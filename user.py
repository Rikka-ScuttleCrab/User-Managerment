from faker import Faker
import random

fake = Faker()

password_hashes = [
    "$2a$10$M6xvmoyqQbncTzZP9aFUsOPSDMa2M1Hi6RbozRGWk6UizvG.UwARG",
    "$2a$10$e67bYaspGN2kDjS66GCtP.M71usrhrjyMx.vY/Nv7i2ffHFa4AxKK",
    "$2a$10$3WiTGGLOuicmtMwsqWLW7.qXP/9YnTNzZC7YW8wGJcEBpSeJtGv.a",
]

# ✅ chỉ dùng role 1, 2, 3 (loại bỏ 4)
valid_roles = [1, 2, 3]

sql = 'INSERT INTO users(username, password_hash, role_id, gender, email, nickname) VALUES\n'
values = []

for i in range(1, 1001):
    username = f"user{i:05d}"

    # ✅ domain unique theo username
    domain = f"domain{i:05d}.com"
    email = f"{username}@{domain}"

    password_hash = random.choice(password_hashes)
    gender = random.choice(["MALE", "FEMALE"])

    # ✅ random role nhưng KHÔNG bao giờ là 4
    role_id = random.choice(valid_roles)

    nickname = fake.first_name()

    values.append(
        f'("{username}","{password_hash}",{role_id},"{gender}","{email}","{nickname}")'
    )

sql += ",\n".join(values) + ";"

with open("users.sql", "w", encoding="utf-8") as f:
    f.write(sql)

print("Done: 1000 users (role_id != 4, unique domains)")