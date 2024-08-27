import type { CollectableDataConfig } from '@criteria-interfaces/goals'

export function useCompletable() {

  function isCompleted(collectableData: CollectableDataConfig): boolean {
    return (collectableData.currentAmount ?? 0) >= (collectableData.amountNeeded ?? Infinity)
  }

  return {
    isCompleted
  }
}