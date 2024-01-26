import type { PunishmentName } from "../model/punishments";
import type { Searchable } from "../searchable";

export interface PunishmentView extends Searchable {
        /**The internal code that is used for building and accessing paths in the configuration store/file */
        id: PunishmentName,
        /** The description that is displayed to the user below the label. */
        description: string,
}

export interface PunishmentsView {
        allpunishments: {
                [punishmentName in PunishmentName]: PunishmentView
        }
}