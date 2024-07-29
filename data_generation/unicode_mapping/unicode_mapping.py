import json
import os;
import csv;
from pathlib import Path

def filename_2_mapname(filename):
    return Path(filename.replace("minecraft", ""))

def create_provider_entry(key_name, unicode: int, namespace="minecraft"):
    return {
        "file": f"{namespace}:{key_name}.png",
        "chars": [chr(unicode)],
        "height": 10,
        "ascent": 8,
        "type": "bitmap"
    }

if __name__ == "__main__":
    available_img_names = [img_name.replace(".png", "") for img_name in os.listdir('textures')]

    # TODO: rewrite everything to be more modular and automatically generate resource pack zip

    mapped_items = []
    mapped_entities = []
    with open("materials.json", 'r') as file:
        csv_reader = csv.reader(file)
        key_names = [row[1] for row in csv_reader]
        unicode_start = 0xE000
        for idx, file in enumerate(key_names):
            if file in available_img_names:
                mapped_items.append((file, unicode_start+idx, ))

    with open("entities_alive_list.csv", 'r') as file:
        csv_reader = csv.reader(file)
        key_names = [row[1] for row in csv_reader]
        unicode_start = 0xE000
        for idx, file in enumerate(key_names):
            mapped_entities.append((file, unicode_start+idx))

    csv_path_materials = '../src/main/resources/material_unicode_mapping.csv'
    with open(csv_path_materials, 'w', newline='', encoding='utf-8') as csv_file:
        csv_writer = csv.writer(csv_file)
        csv_writer.writerow(['File Name', 'Unicode Symbol'])
        csv_writer.writerows([(key, unicode) for key, unicode in mapped_items])
        # csv_writer.writerows([(key, repr(chr(unicode))) for key, unicode in mapped_entities])
    
    data = {
        "providers": []
    }
    for key_name, unicode in mapped_items:
        entry = create_provider_entry(key_name, unicode)
        data["providers"].append(entry)
    for key_name, unicode in mapped_entities:
        entry = create_provider_entry(key_name, unicode, "minecraft")
        # data["providers"].append(entry)
    with open("default.json", "w") as json_file:
        json.dump(data, json_file, indent=4)