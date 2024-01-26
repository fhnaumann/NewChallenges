import json
from pydantic import BaseModel, Field
from typing import Optional, Dict
from rules import Rules
from common.common import ForbidConfigModel

class Model(ForbidConfigModel):
    # 'hacky' way to enforce that at least one rule object
    # exists within the 'rules' object
    # rules: Dict[str, Rules] = Field(..., min_items=1)
    rules: Rules = None


if __name__=="__main__":
    # Print the JSON schema
    with open('rules.json', 'w') as f:
        # model = NoBlockBreak()
        json.dump(Model.model_json_schema(), f, indent=2)
    #print(json.dumps(json_schema, indent=2))