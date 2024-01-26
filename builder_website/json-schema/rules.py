from pydantic import *
from typing import Dict, Union, Optional, List, Annotated
import json
from common.common import ForbidConfigModel
from punishments.punishments import Punishments

class RuleConfigModel(ForbidConfigModel):
    pass

class PunishmentRuleConfigModel(RuleConfigModel):
    punishments: Optional[Punishments] = None

class NoBlockBreakRule(PunishmentRuleConfigModel):
    # bug in the current version
    # exemptions: Annotated[List[str], StringConstraints(strict=True, pattern=r"(^abc$)|(^def$)")]
    exemptions: List[str]

class NoBlockPlaceRule(PunishmentRuleConfigModel):
    exemptions: List[str]

class NoCraftingRule(PunishmentRuleConfigModel):
    exemptions: List[str]

class NoDamageRule(PunishmentRuleConfigModel):
    pass

class NoDeathRule(PunishmentRuleConfigModel):
    pass

class NoRegenerationRule(ForbidConfigModel):
    disableAllRegeneration: bool

class Rules(ForbidConfigModel):
    NoBlockBreak: Optional[NoBlockBreakRule] = None
    NoBlockPlace: Optional[NoBlockPlaceRule] = None
    NoCrafting: Optional[NoCraftingRule] = None
    NoRegeneration: Optional[NoRegenerationRule] = None
    NoDamage: Optional[NoDamageRule] = None
    NoDeath: Optional[NoDeathRule] = None

