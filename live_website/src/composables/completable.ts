import type { CollectableDataConfig, CollectableEntryConfig } from '@criteria-interfaces/goals'
import { useJSONSchemaConfig } from '@/stores/default_model'
import type { DataConfig, MCEvent } from '@criteria-interfaces/live'

export function useCompletable() {

  const defaultConfig = useJSONSchemaConfig().CollectableDataConfig

  function isCompleted(collectableData: CollectableDataConfig): boolean {
    return (collectableData.currentAmount ?? 0) >= (collectableData.amountNeeded ?? Infinity)
  }

  function isCompletedBasedOffEvents(mcEvents: MCEvent<any>[], amountNeeded?: number): boolean {
    return sumOfEvents(mcEvents) >= (amountNeeded ?? defaultConfig.properties.amountNeeded.default)
  }

  function sumOfEvents(mcEvents: MCEvent<any>[]): number {
    return mcEvents.map(value => value.data?.amount).reduce((previousValue, currentValue) => previousValue + currentValue, defaultConfig.properties.currentAmount.default)
  }

  function isSingleCompletionType(collectableData: CollectableDataConfig): boolean {
    return collectableData.amountNeeded === 1 || collectableData.amountNeeded === defaultConfig.properties.amountNeeded.default;
  }

  function keepNotYetComplete(source: CollectableEntryConfig[], codeAccess: (mcEvent: MCEvent<DataConfig>) => string, mcEvents: MCEvent<any>[]): CollectableEntryConfig[] {
    return source.filter(value => {
      if(isCompleted(value.collectableData)) {
        return false
      }
      return !isCompletedBasedOffEvents(filterEventsFor(value.collectableName, codeAccess, mcEvents), value.collectableData?.amountNeeded);

    })
  }

  function filterEventsFor(code: string, codeAccess: (mcEvent: MCEvent<DataConfig>) => string, events: MCEvent<any>[]): MCEvent<any>[] {
    return events!.filter((mcEvent) => codeAccess!(mcEvent) === code)
  }

  return {
    isCompleted,
    sumOfEvents,
    isCompletedBasedOffEvents,
    isSingleCompletionType,
    keepNotYetComplete,
    filterEventsFor
  }
}