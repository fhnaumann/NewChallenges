from pydantic import BaseModel, Field
from typing import Union, ClassVar


class ForbidConfigModel(BaseModel):
    class Config:
        extra = 'forbid'
