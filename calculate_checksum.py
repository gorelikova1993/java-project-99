import hashlib

def calculate_flyway_checksum(file_path):
    try:
        with open(file_path, "rb") as f:
            content = f.read()
            # Вычисляем SHA-1 хеш
            sha1_hash = hashlib.sha1(content).digest()
            # Flyway использует первые 4 байта SHA-1 хеша как 32-битное целое число
            # Преобразуем первые 4 байта в int32
            checksum = int.from_bytes(sha1_hash[:4], byteorder='big', signed=True)
            return checksum
    except FileNotFoundError:
        print(f"File {file_path} not found")
        return None

# Список файлов миграций
files = [
    "src/main/resources/db/migration/V2__create_task_status_table.sql",
    "src/main/resources/db/migration/V3__create_labels_table.sql",
    "src/main/resources/db/migration/V4__create_tasks_and_task_labels_tables.sql"
]

for file_path in files:
    checksum = calculate_flyway_checksum(file_path)
    if checksum is not None:
        print(f"Checksum for {file_path}: {checksum}")