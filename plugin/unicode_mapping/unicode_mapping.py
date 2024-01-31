import os;
import csv;
from pathlib import Path

def filename_2_mapname(filename):
    return Path(filename.replace("minecraft", "")[1:]).stem

if __name__ == "__main__":
    folder_path = '../src/main/resources/rendered_items'

    unicode_start = 0xE000
    files = os.listdir(folder_path)
    mapped = []
    for idx, file in enumerate(files):
        mapped.append((filename_2_mapname(file), repr(chr(unicode_start+idx))))
    print(mapped)
    csv_path = '../src/main/resources/unicode_mapping.csv'
    with open(csv_path, 'w', newline='', encoding='utf-8') as csv_file:
        csv_writer = csv.writer(csv_file)
        csv_writer.writerow(['File Name', 'Unicode Symbol'])
        csv_writer.writerows(mapped)