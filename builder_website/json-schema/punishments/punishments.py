from pydantic import *
from typing import Dict, Union, Optional
import json
from common.common import ForbidConfigModel

class PunishmentConfigModel(ForbidConfigModel):
    pass

class EndPunishment(PunishmentConfigModel):
    pass

class DeathPunishment(PunishmentConfigModel):
    pass

class HealthPunishment(PunishmentConfigModel):
    amountLost: int = Field(default=1, ge=1, le=10)

class RandomEffectPunishment(PunishmentConfigModel):
    effectsAtOnce: int = Field(default=1, ge=1, le=5)

class Punishments(PunishmentConfigModel):
    End: Optional[EndPunishment] = None
    Death: Optional[DeathPunishment] = None
    Health: Optional[HealthPunishment] = None

