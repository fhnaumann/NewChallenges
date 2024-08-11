import json

MC_VERSION = "1.20.4"

def main():
    with open("death_messages_from_client.json", "rb") as source, \
        open("death_messages_as_data_source_JSON.json", "w") as death_message_data_source:
        
        death_messages_from_client = json.load(source)

        death_messages_data_source_as_list = []
        for key, message in death_messages_from_client.items():
            death_messages_data_source_as_list.append(_transform_death_message(key, message))
        all_dict_data_source = {
            "version": MC_VERSION,
            "data": death_messages_data_source_as_list
        }
        json.dump(all_dict_data_source, death_message_data_source, indent=2)

def _transform_death_message(key, message):
    return {
        "code": key.lower(),
        "deathMessageWithDummyData": _replace_with_dummy(message)
    }
def _replace_with_regex(message):
    return message.replace("%1$s", "(?<player>.*?)").replace("%2$s", "(?<mob>.*?)").replace("%3$s", "(?<item>.*?)")
def _replace_with_dummy(message):
    player_who_died = "[player]"
    killer = "[mob]"
    item = "[item]"
    return message.replace("%1$s", player_who_died).replace("%2$s", killer).replace("%3$s", item)
if __name__ == "__main__":
    main()
